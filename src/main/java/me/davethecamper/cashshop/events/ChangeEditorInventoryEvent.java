package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.davethecamper.cashshop.inventory.ReciclableMenu;

public class ChangeEditorInventoryEvent extends Event {
	
	public ChangeEditorInventoryEvent(UUID uuid, ReciclableMenu rm) {
		this.uuid = uuid;
		this.reciclableMenu = rm;
	}
	
	
	
	private UUID uuid;
	
	private ReciclableMenu reciclableMenu;

	
	public UUID getUuid() {return uuid;}

	public ReciclableMenu getReciclableMenu() {return reciclableMenu;}
	

	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
