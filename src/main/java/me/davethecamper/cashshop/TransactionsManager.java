package me.davethecamper.cashshop;

import com.cryptomorin.xseries.XSound;
import lombok.extern.slf4j.Slf4j;
import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.PlayerInfo;
import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;
import me.davethecamper.cashshop.events.TransactionCompleteEvent;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class TransactionsManager {
	
	public TransactionsManager(CashShop main) {
		this.main = main;
		threadVerifier();
	}
	
	private CashShop main;
	
	private Thread thread;
	
	private volatile HashMap<CashPlayer, ArrayList<TransactionInfo>> to_cancel = new HashMap<>();
	

	private void threadVerifier() {
		this.thread = new Thread(() -> {
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

                                    if (Objects.isNull(csg)) continue;

                                    TransactionResponse tr = csg.verifyTransaction(ti.getTransactionToken());

                                    //Bukkit.getLogger().info("§eValidating transaction uuid=" + uuid + " token=" + token + " tr=" + tr);

                                    if (tr != null) {
                                        switch (tr) {
                                            case APPROVED:
                                                approveTransaction(cp, ti);
                                                Bukkit.getConsoleSender().sendMessage("§aEnviado comando para liberar cash ao jogador " + Bukkit.getOfflinePlayer(uuid).getName());
                                                break;

                                            case CANCELLED:
                                                addToCancel(cp, ti);
                                                Bukkit.getConsoleSender().sendMessage("§cTransação cancelada " + ti.getPlayer() + " " + ti.getTransactionToken());
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
            }, 0, main.configuration.getInt("delay_verify")*1000L);;
        });
		thread.start();
	}
	
	public void stop() {
		thread.interrupt();
	}

	private void addToCancel(CashPlayer player, TransactionInfo ti) {
		ArrayList<TransactionInfo> list = new ArrayList<>();
		if (to_cancel.containsKey(player)) {
			list.addAll(to_cancel.get(player));
		}
		list.add(ti);
		to_cancel.put(player, list);

		Bukkit.getScheduler().runTask(main, () -> player.cancelTransaction(ti));
	}

	public void approveTransaction(CashPlayer player, TransactionInfo ti) {
		ti.setGracePeriodDays(CashShop.getInstance().getGateway(ti.getGatewayCaller()).getGracePeriodDays());

		Bukkit.getScheduler().runTask(main, () -> finishApproveTransaction(player, ti));
	}

	private void finishApproveTransaction(CashPlayer cp, TransactionInfo ti) {
		Bukkit.getConsoleSender().sendMessage("§aTransação liberada " + ti.toString());

		OfflinePlayer of = Bukkit.getOfflinePlayer(ti.getPlayer());
		CashPlayer other = CashShop.getInstance().getCashPlayer(of.getUniqueId());

		other.addCash(ti.getCash());
		cp.setTransactionAsAproved(ti);

		Bukkit.getPluginManager().callEvent(new TransactionCompleteEvent(of.getUniqueId(), ti));

		if (of.isOnline()) {
			of.getPlayer().sendMessage("§7Transação liberada, adicionado §a" + ti.getCash() + " cash's");
			of.getPlayer().playSound(Bukkit.getPlayer(cp.getUniqueId()).getLocation(), XSound.ENTITY_CAT_PURREOW.parseSound(), 1f, 1.3f);
		}
	}

	public void createPlayerTransaction(String identifier, CashPlayer player) {
		double amount = player.getProductAmount();;
		double total_in_money = amount - (amount * (CashShop.getInstance().getCupomManager().getDiscount(player.getCupom()) / 100));
		int cashMultiply = CashShop.getInstance().getMainConfig().getInt("coin.value") * (int) amount;
		String playerName = Bukkit.getPlayer(player.getUniqueId()).getName();

		CashShopGateway csg = CashShop.getInstance().getGateway(identifier);
		ProductInfo pi = new ProductInfo(cashMultiply, total_in_money, "Cash", CashShop.getInstance().getMainConfig().getString("currency.code"));
		PlayerInfo playerInfo = new PlayerInfo(playerName, "", "");

		Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("CashShop"), () -> {
			TransactionInfo ti = csg.generateTransaction(pi, playerInfo);

			ti = new TransactionInfo(isValidNick(player.getGiftFor()) ? player.getGiftFor() : Bukkit.getOfflinePlayer(player.getUniqueId()).getName(), csg, player.getCupom(), (int) Math.round(amount * CashShop.getInstance().getMainConfig().getInt("coin.value")), total_in_money, System.currentTimeMillis(), ti.getLink(), ti.getTransactionToken());

			synchronized (player.getTransactionsPending()) {
				player.getTransactionsPending().put(ti.getTransactionToken(), ti);
			}

			player.setChanges(true);

			final TransactionInfo finalTransaction = ti;

			Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CashShop"), () ->
					csg.sendLink(player, finalTransaction));
		});
	}

	private boolean isValidNick(String nick) {
		char[] chars = nick.toCharArray();
		if (chars.length >= 16 || chars.length == 0) return false;

		for (int i = 0; i < chars.length; i++) {
			if (!Character.isDigit(chars[i]) && !Character.isLetter(chars[i]) && chars[i] != '_') {
				return false;
			}
		}
		return true;
	}
	

}
