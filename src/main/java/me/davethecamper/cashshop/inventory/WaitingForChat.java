package me.davethecamper.cashshop.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.events.WaitingChatEvent;
import me.davethecamper.cashshop.inventory.configs.IdentificableMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingForChat implements Listener {

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final String message) {
		this(player, type, var_name, null, message);
	}

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller) {
		this(player, type, var_name, caller, caller.getMessages().getString("chat.to_do." + type.toString()));
	}

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller, boolean block_negatives) {
		this(player, type, var_name, caller, block_negatives, caller.getMessages().getString("chat.to_do." + type.toString()));
	}

	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller, boolean block_negatives, String message) {
		this(player, type, var_name, caller, message);
		this.block_negative = block_negatives;
	}
	
	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller, String message) {
		this.player = player;
		this.type = type;
		this.caller = caller;
		this.var_name = var_name;
		this.message = message;

		executeWaitingChat();
	}
	
	private boolean block_negative = true;
	
	private String var_name;

	private String message;

	private String errorMessage;
	
	private Object result;
	
	private UUID player;
	
	private WaitingForChat.Primitives type;
	
	private IdentificableMenu caller;

	private Predicate<String> validator;
	
	
	public String getVarName() {return var_name;}
	
	public Object getResult() {return result;}

	public UUID getPlayer() {return player;}


	public void executeWaitingChat() {
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME));

		Bukkit.getPlayer(player).sendMessage(message);
		Bukkit.getPlayer(player).closeInventory();
	}
	

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
				e.getPlayer().sendMessage(getErrorMessage());
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

				case STRING: {
					if (Bukkit.getPlayer(getPlayer()).isOp()) return true;

					return Objects.isNull(validator) ?
							isValidWord(message) :
							validator.test(message);
				}
					
				default: break;
			}
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	private String getErrorMessage() {
		if (Objects.nonNull(this.errorMessage))
			return this.errorMessage;

		return CashShop.getInstance().getMessagesConfig().getString("chat.generic_error." + type.toString());
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

	private boolean isValidWord(String message) {
		if (message.length() > 256) return false;

		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			if (Character.isDigit(c)) continue;

			if (c == ' ' || c == '&' || c == ',' || c == '.' || c == '_' || c == '-' || c == '#') continue;

			if (c >= 'A' && c <= 'Z') continue;

			if (c >= 'a' && c <= 'z') continue;

			if (c >= 'À' && c <= 'ü') continue;

			return false;
		}

		return true;
	}
	
	public enum Primitives {
		STRING,
		INTEGER,
		LONG,
		DOUBLE,
		FLOAT;
	}

}
