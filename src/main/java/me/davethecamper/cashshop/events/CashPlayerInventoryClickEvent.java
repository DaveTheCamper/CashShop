package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

public class CashPlayerInventoryClickEvent extends Event {

	public CashPlayerInventoryClickEvent(UUID uuid, ConfigInteractiveMenu rm, InventoryClickEvent click_event) {
		this.player = uuid;
		this.menu = rm;
		this.event = click_event;
	}
	
	private UUID player;
	
	private ConfigInteractiveMenu menu;
	
	private InventoryClickEvent event;
	
	private boolean cancel_click = true;
	
	


	public void setCancelClick(boolean cancel_click) {this.cancel_click = cancel_click;}
	
	
	public boolean isCancelClick() {return cancel_click;}

	public UUID getPlayer() {return player;}

	public ConfigInteractiveMenu getMenu() {return menu;}
	
	public InventoryClickEvent getClickEvent() {return this.event;}
	

	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
