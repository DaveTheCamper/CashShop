package me.davethecamper.cashshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.XSound;

import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;
import me.davethecamper.cashshop.events.TransactionCompleteEvent;
import me.davethecamper.cashshop.player.CashPlayer;

public class TransactionsManager {
	
	public TransactionsManager(CashShop main) {
		this.main = main;
		threadVerifier();
		verifyTransactions();
	}
	
	private CashShop main;
	
	private Thread thread;
	
	private volatile HashMap<CashPlayer, ArrayList<TransactionInfo>> to_approve = new HashMap<>();
	private volatile HashMap<CashPlayer, ArrayList<TransactionInfo>> to_cancel = new HashMap<>();
	

	private void threadVerifier() {
		this.thread = new Thread() {
			@Override
			public void run() {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						for (UUID uuid : new ArrayList<>(main.players.keySet())) {
							CashPlayer cp = main.players.get(uuid);
							if (cp.isOnline()) {
								for (String token : new ArrayList<>(cp.getPendingTransactions().keySet())) {
									try {
										TransactionInfo ti = cp.getPendingTransactions().get(token);
										
										CashShopGateway csg = main.getGateway(ti.getGatewayCaller());
										TransactionResponse tr = csg.verifyTransaction(ti.getTransactionToken());
										
										System.out.println(uuid + " " + token + " §a" + tr);
									
										if (tr != null) {
											switch (tr) {
												case APPROVED:
													addToApprove(cp, ti);
													System.out.println("§aEnviado comando para liberar cash ao jogador " + Bukkit.getOfflinePlayer(uuid).getName());
													break;
												
												case CANCELLED:
													addToCancel(cp, ti);
													break;
													
												default:
													break;
												
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						
					}
				}, 0, main.configuration.getInt("delay_verify")*1000);;
			}
		};
		thread.start();
	}
	
	public void stop() {
		thread.interrupt();
	}
	
	public void addToApprove(CashPlayer player, TransactionInfo ti) {
		ArrayList<TransactionInfo> list = new ArrayList<>();
		if (to_approve.containsKey(player)) {
			list.addAll(to_approve.get(player));
		}
		list.add(ti);
		to_approve.put(player, list);
	}

	private void addToCancel(CashPlayer player, TransactionInfo ti) {
		ArrayList<TransactionInfo> list = new ArrayList<>();
		if (to_cancel.containsKey(player)) {
			list.addAll(to_cancel.get(player));
		}
		list.add(ti);
		to_cancel.put(player, list);
	}
	

	@SuppressWarnings("deprecation")
	private void verifyTransactions() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (CashPlayer cp : new ArrayList<>(to_approve.keySet())) {
					for (TransactionInfo ti : new ArrayList<>(to_approve.get(cp))) {
						OfflinePlayer of = Bukkit.getOfflinePlayer(ti.getPlayer());
						CashPlayer other = CashShop.getInstance().getCashPlayer(of.getUniqueId());
						
						other.addCash(ti.getCash());
						cp.setTransactionAsAproved(ti);
						
						to_approve.get(cp).remove(ti);
						Bukkit.getPluginManager().callEvent(new TransactionCompleteEvent(of.getUniqueId(), ti));
						if (Bukkit.getPlayer(cp.getUniqueId()) != null && Bukkit.getPlayer(cp.getUniqueId()).isOnline()) {
							Bukkit.getPlayer(cp.getUniqueId()).sendMessage("§aTransação liberada, adicionado " + ti.getCash() + " cash's");
							Bukkit.getPlayer(cp.getUniqueId()).playSound(Bukkit.getPlayer(cp.getUniqueId()).getLocation(), XSound.ENTITY_CAT_PURREOW.parseSound(), 1f, 1.3f);
						}
					}
				}

				for (CashPlayer cp : to_cancel.keySet()) {
					for (TransactionInfo ti : new ArrayList<>(to_cancel.get(cp))) {
						cp.cancelTransaction(ti);
					}
				}
			}
		}.runTaskTimer(main, 0, main.configuration.getInt("delay_verify")*3);
	}

	public void createPlayerTransaction(String identifier, CashPlayer player) {
		double amount = player.getProductAmount();;
		double total_in_money = amount - (amount * (CashShop.getInstance().getCupomManager().getDiscount(player.getCupom()) / 100));

		CashShopGateway csg = CashShop.getInstance().getGateway(identifier);
		ProductInfo pi = new ProductInfo(total_in_money, "Cash", CashShop.getInstance().getMainConfig().getString("currency.code"));
		TransactionInfo ti = csg.generateTransaction(pi, null);

		System.out.println(isValidNick(this.giftFor));
		ti = new TransactionInfo(isValidNick(this.giftFor) ? giftFor : Bukkit.getOfflinePlayer(uniqueId).getName(), csg, this.cupom, (int) Math.round(productAmount * CashShop.getInstance().getMainConfig().getInt("coin.value")), total_in_money, System.currentTimeMillis(), ti.getLink(), ti.getTransactionToken());

		player.getTransactionsPending().put(ti.getTransactionToken(), ti);
		player.setChanges(true);

		csg.sendLink(player, ti);
	}
	

}
