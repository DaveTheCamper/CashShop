package me.davethecamper.cashshop;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
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
	
	
	private void playerCashCommands(CommandSender sender, Command cmd, String[] args) {
		Player p = (Player) sender;
		switch (args[0].toLowerCase()) {
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
	}
	
	@SuppressWarnings("deprecation")
	private void anyCashCommands(CommandSender sender, Command cmd, String[] args) {
		switch (args[0].toLowerCase()) {
			case "reload":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					main.reload();
					sender.sendMessage(main.messages.getString("messages.reload"));
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
		if (main.getNormalPlayerInventory(p.getUniqueId()).haveCurrentInventory()) {
			main.getNormalPlayerInventory(p.getUniqueId()).openCurrentInventory();
		} else {
			main.getNormalPlayerInventory(p.getUniqueId()).updateCurrentInventory((ConfigInteractiveMenu) CashShop.getInstance().getStaticItem("main"));
		}
	}

}
