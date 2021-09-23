package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

public class PreOpenCashInventoryEvent extends Event {

	public PreOpenCashInventoryEvent(UUID uuid, ConfigInteractiveMenu rm) {
		this.uuid = uuid;
		this.reciclableMenu = rm;
	}
	
	private UUID uuid;
	
	private ConfigInteractiveMenu reciclableMenu;

	
	public UUID getUniqueId() {return uuid;}

	public ConfigInteractiveMenu getMenu() {return reciclableMenu;}
	
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
