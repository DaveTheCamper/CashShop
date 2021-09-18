package me.davethecamper.cashshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;
import me.davethecamper.cashshop.player.CashPlayer;

public class TransactionsManager {
	
	public TransactionsManager(CashShop main) {
		this.main = main;
		threadVerifier();
		verifyTransactions();
	}
	
	private CashShop main;
	
	private volatile HashMap<CashPlayer, ArrayList<TransactionInfo>> to_approve = new HashMap<>();
	

	private void threadVerifier() {
		new Thread() {
			@Override
			public void run() {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						for (UUID uuid : main.players.keySet()) {
							CashPlayer cp = main.players.get(uuid);

							try {
								if (cp.isOnline()) {
									for (String token : new ArrayList<>(cp.getPendingTransactions().keySet())) {
										TransactionInfo ti = cp.getPendingTransactions().get(token);
										
										CashShopGateway csg = main.getGateway(ti.getGatewayCaller());
										
										if (csg.verifyTransaction(ti.getTransactionToken()).equals(TransactionResponse.APPROVED)) {
											addToApprove(cp, ti);
											System.out.println("§aEnviado comando para liberar cash ao jogador " + Bukkit.getOfflinePlayer(uuid).getName());
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					}
				}, 0, main.configuration.getInt("delay_verify")*1000);;
			}
		}.start();
	}
	
	private void addToApprove(CashPlayer player, TransactionInfo ti) {
		ArrayList<TransactionInfo> list = new ArrayList<>();
		if (to_approve.containsKey(player)) {
			list.addAll(to_approve.get(player));
		}
		list.add(ti);
		to_approve.put(player, list);
	}
	
	
	private void verifyTransactions() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (CashPlayer cp : to_approve.keySet()) {
					for (TransactionInfo ti : new ArrayList<>(to_approve.get(cp))) {
						cp.addCash(ti.getCash());
						cp.setTransactionAsAproved(ti);
						to_approve.get(cp).remove(ti);
					}
				}
			}
		}.runTaskTimer(main, 0, main.configuration.getInt("delay_verify")*3);
	}
	

}
