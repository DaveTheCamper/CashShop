package me.davethecamper.cashshop.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class CustomInventoryClickEvent extends Event {
	
	public CustomInventoryClickEvent(InventoryClickEvent e) {
		this.event = e;
	}
	
	private InventoryClickEvent event;
	
	
	public boolean isRightClick() {
		switch (event.getAction()) {
			case PICKUP_HALF:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isLeftClick() {
		switch (event.getAction()) {
			case PICKUP_ALL:
			case COLLECT_TO_CURSOR:
			case SWAP_WITH_CURSOR:
				return true;
			default:
				return false;
		
		}
	}


	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
