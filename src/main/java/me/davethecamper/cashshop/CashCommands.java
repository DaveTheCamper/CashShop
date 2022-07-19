package me.davethecamper.cashshop;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.player.CashPlayer;

public class CashCommands implements CommandExecutor {
	
	public CashCommands(CashShop main) {
		this.main = main;
	}
	
	private CashShop main;
	

	private final String ADMIN_PERMISSION = "cash.admin";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String name = cmd.getName();
		switch (name) {
			case "cash":
				if (sender instanceof Player) {
					playerCashCommands(sender, cmd, args);
				} else {
					
				}
				
				anyCashCommands(sender, cmd, args);
				break;
				
			case "shop":
				if (sender instanceof Player) {
					playerShopCommands(sender, cmd, args);
				}
				break;
		}
		return false;
	}
	

	
	@SuppressWarnings("deprecation")
	private void playerCashCommands(CommandSender sender, Command cmd, String[] args) {
		Player p = (Player) sender;
		CashPlayer cp = CashShop.getInstance().getCashPlayer(p.getUniqueId());
		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "enviar":
				case "pagar":
				case "pay":
					if (args.length >= 3) {
						OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
						int amount = Integer.valueOf(args[2]);
						if (amount >= 0 && cp.getCash() >= amount) {
							if (of != null && of.hasPlayedBefore()) {
								CashPlayer other = CashShop.getInstance().getCashPlayer(of.getUniqueId());
								other.addCash(amount);
								cp.removeCash(amount);
								
								p.sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §aVocê enviou §b" + amount + " §acash o jogador §b" + of.getName());
								if (of.isOnline()) {
									of.getPlayer().sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §aVocê recebeu §b" + amount + " §acash do jogador §b" + p.getName());
								}
							} else {
								p.sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §cPlayer não encontrado");
							}
						} else {
							p.sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §c/cash enviar [nick] [quantia]");
						}
					}
					break;
					
				case "ver":
					p.sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §eVocê tem §6" + cp.getCash() + " §ecash's");
					break;
					
				case "editor":
					if (p.hasPermission(ADMIN_PERMISSION)) {
						if (main.getPlayerEditorCurrentInventory(p.getUniqueId()) == null) {
							main.createPlayerInventory(p.getUniqueId());
						} else {
							p.openInventory(main.getPlayerEditorCurrentInventory(p.getUniqueId()).getInventory());
						}
					}
					break;
					
			}
		} else {
			p.sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §eVocê tem §6" + cp.getCash() + " §ecash's");
		}
	}
	
	@SuppressWarnings("deprecation")
	private void anyCashCommands(CommandSender sender, Command cmd, String[] args) {
		if (args.length == 0) return;
		
		switch (args[0].toLowerCase()) {
			case "reload":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					main.reload();
					sender.sendMessage(main.messages.getString("messages.reload"));
				}
				break;
				
			case "total":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					int year = Integer.parseInt(args[1]);
					int month = Integer.parseInt(args[2]);
					
					sender.sendMessage("§aTotal: §2" + CashShop.getInstance().getTotalMoneySpent(year, month) + "R$");
				}
				break;
				
			case "give_product":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					Player receiver = Bukkit.getPlayer(args[1]);
					CashPlayer cp = CashShop.getInstance().getCashPlayer(receiver.getUniqueId());
					SellProductMenu product = CashShop.getInstance().getProduct(args[2]);
					
					cp.buyCurrentProduct(1, product, false);
					sender.sendMessage(main.messages.getString("commands.product_gived"));
				}
				break;
				
			case "add":
			case "give":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
					CashPlayer cp = main.getNormalPlayerInventory(of.getUniqueId());
					int amount = Integer.parseInt(args[2]);

					cp.addCash(amount);
					sender.sendMessage(main.messages.getString("commands.cash.give").replaceAll("@amount", amount + "").replaceAll("@player", of.getName()));
				}
				break;
				
			case "liberar":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
					CashPlayer cp = main.getNormalPlayerInventory(of.getUniqueId());
					TransactionInfo ti = cp.getPendingTransactions().get(args[2]);
					
					if (ti != null) {
						CashShop.getInstance().getTransactionsManager().addToApprove(cp, ti);
						sender.sendMessage(main.messages.getString("commands.cash.allowed"));
					} else {
						sender.sendMessage(main.messages.getString("commands.cash.not_allowed"));
					}
				}
				break;
				
			case "set":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
					CashPlayer cp = main.getNormalPlayerInventory(of.getUniqueId());
					int amount = Integer.parseInt(args[2]);

					cp.setCash(amount);
					sender.sendMessage(main.messages.getString("commands.cash.set").replaceAll("@amount", amount + "").replaceAll("@player", of.getName()));
				}
				break;
				
			case "remove":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
					CashPlayer cp = main.getNormalPlayerInventory(of.getUniqueId());
					int amount = Integer.parseInt(args[2]);
					
					cp.removeCash(amount);
					sender.sendMessage(main.messages.getString("commands.cash.remove").replaceAll("@amount", amount + "").replaceAll("@player", of.getName()));
				}
				break;
				
			case "see":
			case "ver":
			case "show":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
					CashPlayer cp = main.getNormalPlayerInventory(of.getUniqueId());
					
					sender.sendMessage(main.messages.getString("commands.cash.show").replaceAll("@amount", cp.getCash() + "").replaceAll("@player", of.getName()));
				}
				break;
				
			case "cupom":
				switch (args[1].toLowerCase()) {
					case "add":
						if (sender.hasPermission(ADMIN_PERMISSION)) {
							try {
								String name = args[2].toLowerCase();
								double percentage = Double.parseDouble(args[3]);
								int hours = Integer.parseInt(args[4]);
								
								main.getCupomManager().addCupom(name, percentage, hours);
								sender.sendMessage(main.messages.getString("commands.cash.cupom.add"));
							} catch (Exception e) {
								sender.sendMessage("§cUsage /cupom add [name] [percentage] [duration_in_hours]");
							}
						}
						break;
						
					case "remove":
						if (sender.hasPermission(ADMIN_PERMISSION)) {
							try {
								String name = args[2].toLowerCase();
								
								if (main.getCupomManager().removeCupom(name)) {
									sender.sendMessage(main.messages.getString("commands.cash.cupom.remove.sucess"));
								} else {
									sender.sendMessage(main.messages.getString("commands.cash.cupom.remove.fail"));
								}
							} catch (Exception e) {
								sender.sendMessage("§cUsage /cupom remove [name]");
							}
						}
						break;
				}
				break;
		}
	}
	
	private void playerShopCommands(CommandSender sender, Command cmd, String[] args) {
		Player p = (Player) sender;
		if (main.getNormalPlayerInventory(p.getUniqueId()).haveCurrentInventoryFromMain()) {
			main.getNormalPlayerInventory(p.getUniqueId()).openCurrentInventory();
		} else {
			main.getNormalPlayerInventory(p.getUniqueId()).updateCurrentInventory((ConfigInteractiveMenu) CashShop.getInstance().getStaticItem("main"));
		}
	}

}
