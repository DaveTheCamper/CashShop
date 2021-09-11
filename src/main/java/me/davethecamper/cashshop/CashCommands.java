package me.davethecamper.cashshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

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
					if (main.getPlayerCurrentInventory(p.getUniqueId()) == null) {
						main.createPlayerInventory(p.getUniqueId());
					} else {
						p.openInventory(main.getPlayerCurrentInventory(p.getUniqueId()).getInventory());
					}
				}
				break;
				
		}
	}
	
	private void anyCashCommands(CommandSender sender, Command cmd, String[] args) {
		switch (args[0].toLowerCase()) {
			case "reload":
				if (sender.hasPermission(ADMIN_PERMISSION)) {
					main.reload();
					sender.sendMessage(main.messages.getString("reload"));
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
