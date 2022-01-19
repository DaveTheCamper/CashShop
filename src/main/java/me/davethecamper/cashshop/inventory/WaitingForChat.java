package me.davethecamper.cashshop.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.events.WaitingChatEvent;
import me.davethecamper.cashshop.inventory.configs.IdentificableMenu;

public class WaitingForChat implements Listener {

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final String message) {
		this(player, type, var_name, null, message);
	}

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller) {
		this(player, type, var_name, caller, caller.getMessages().getString("chat.to_do." + type.toString()));
	}

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller, boolean block_negatives) {
		this(player, type, var_name, caller, caller.getMessages().getString("chat.to_do." + type.toString()));
		this.block_negative = block_negatives;
	}
	
	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller, String message) {
		this.player = player;
		this.type = type;
		this.caller = caller;
		this.var_name = var_name;
		
		
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME));
		
		Bukkit.getPlayer(player).sendMessage(message);
		Bukkit.getPlayer(player).closeInventory();
	}
	
	private boolean block_negative = true;
	
	private String var_name;
	
	private Object result;
	
	private UUID player;
	
	private WaitingForChat.Primitives type;
	
	private IdentificableMenu caller;
	
	
	public String getVarName() {return var_name;}
	
	public Object getResult() {return result;}

	public UUID getPlayer() {return player;}
	

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().getUniqueId().equals(player)) {
			e.setCancelled(true);
			String message = e.getMessage();
			
			if (isValid(message)) {
				Object o = message;
				
				switch (type) {
					case INTEGER:
						o = Integer.parseInt(message);
						break;
						
					case LONG:
						o = Long.parseLong(message);
						break;
						
					case DOUBLE:
						o = Double.parseDouble(message);
						break;
						
					case FLOAT:
						o = Float.parseFloat(message);
						break;
						
					case STRING:
						o = message.replaceAll("&", "§");
						break;
				}
				
				finish(o);
			} else {
				e.getPlayer().sendMessage(CashShop.getInstance().getMessagesConfig().getString("chat.generic_error." + type.toString()));
			}
		}
	}
	
	private boolean isValid(String message) {
		try {
			switch (type) {
				case INTEGER:
					{
						int val = Integer.parseInt(message);
						return val > 0 || !block_negative;
					}
					
				case LONG:
					{
						long val = Long.parseLong(message);
						return val > 0 || !block_negative;
					}
					
				case DOUBLE:
					{
						double val = Double.parseDouble(message);
						return val > 0 || !block_negative;
					}
					
				case FLOAT:
					{
						float val = Float.parseFloat(message);
						return val > 0 || !block_negative;
					}
					
				default: break;
			}
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private void finish(Object obj) {
		HandlerList.unregisterAll(this);
		this.result = obj;
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME), () -> {
			if (caller == null) {
				Bukkit.getPluginManager().callEvent(new WaitingChatEvent(this));
			} else {
				caller.changerVar(var_name, obj);
			}
		});
	}
	
	public enum Primitives {
		STRING,
		INTEGER,
		LONG,
		DOUBLE,
		FLOAT;
	}

}
