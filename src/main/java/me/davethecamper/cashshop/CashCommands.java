package me.davethecamper.cashshop;

import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

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
					boolean result = playerCashCommands(sender, cmd, args);

					if (result) return true;
				}
				
				anyCashCommands(sender, cmd, args);
				break;
				
			case "shop":
				if (sender instanceof Player) {
					playerShopCommands(sender);
				}
				break;
		}
		return false;
	}
	

	
	@SuppressWarnings("deprecation")
	private boolean playerCashCommands(CommandSender sender, Command cmd, String[] args) {
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
						if (amount >= 0 && cp.getCash(false) >= amount) {
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
							p.sendMessage(CashShop.getInstance().getMessagesConfig().getString("tag") + " §7Cash insuficiente, use §a/cash ver");
						}
					}
					return true;
					
				case "ver":
					seeSubCommand(p, cp);
					return true;
					
				case "editor":
					if (p.hasPermission(ADMIN_PERMISSION)) {
						if (main.getPlayerEditorCurrentInventory(p.getUniqueId()) == null) {
							main.createPlayerInventory(p.getUniqueId());
						} else {
							p.openInventory(main.getPlayerEditorCurrentInventory(p.getUniqueId()).getInventory());
						}
					}
					return true;

				case "reset_bonus":
					if (p.hasPermission(ADMIN_PERMISSION)) {
						executeCashBonusReset(sender);
					}
					return true;
					
			}
		} else if (!p.hasPermission(ADMIN_PERMISSION)) {
			seeSubCommand(p, cp);
		}

		return !p.hasPermission(ADMIN_PERMISSION);
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
					boolean bonus = args.length >= 4 && Boolean.parseBoolean(args[3]);

					cp.addCash(amount, bonus);

					if (bonus) sender.sendMessage("§4§lO cash abaixo é bonus.");
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
					boolean bonus = args.length >= 4 && Boolean.parseBoolean(args[3]);

					if (bonus) {
						cp.setCashBonus(amount);
					} else {
						cp.setCash(amount);
					}
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
				seeSubCommand(sender, args);
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

	private void seeSubCommand(CommandSender sender, String args[]) {
		if (sender.hasPermission(ADMIN_PERMISSION)) {
			OfflinePlayer of = Bukkit.getOfflinePlayer(args[1]);
			CashPlayer cp = main.getNormalPlayerInventory(of.getUniqueId());

			seeSubCommand(sender, cp);
		}
	}

	private void seeSubCommand(CommandSender p, CashPlayer cp) {
		DecimalFormat f = new DecimalFormat("#,##0");

		p.sendMessage(" " + CashShop.getInstance().getMessagesConfig().getString("tag") + " §7Seu cash");
		p.sendMessage("");
		p.sendMessage(" §eNormal: §6" + f.format(cp.getCash()));
		p.sendMessage(" §eBonus: §6" + f.format(cp.getCashBonus()));
		p.sendMessage("");
		p.sendMessage(" §7§oCash bonus não pode ser enviado.");
	}
	
	private void playerShopCommands(CommandSender sender) {
		Player p = (Player) sender;
		CashPlayer cp = CashShop.getInstance().getCashPlayer(p.getUniqueId());

		cp.getPreviusMenus().clear();
		cp.setCashTransaction(false);

		main.getNormalPlayerInventory(p.getUniqueId()).updateCurrentInventory((ConfigInteractiveMenu) CashShop.getInstance().getStaticItem("main"));
	}

	private void executeCashBonusReset(CommandSender sender) {
		File folder = new File(Bukkit.getPluginManager().getPlugin("CashShop").getDataFolder().getAbsolutePath() + "/players");

		for (File file : folder.listFiles()) {
			String fileName = file.getName().replace(".yml", "");
			UUID uuid = UUID.fromString(fileName);

			CashPlayer cashPlayer = CashShop.getInstance().getCashPlayer(uuid);

			int before = cashPlayer.getCashBonus();

			if (before == 0) continue;

			cashPlayer.setCashBonus(0);
			Bukkit.getConsoleSender().sendMessage("§7O cash bonus do jogador " + uuid + " foi removido §c-" + before);
		}
	}
}
