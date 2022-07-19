package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;

public class CashMenuInventoryClickEvent extends CustomInventoryClickEvent implements Cancellable {

	public CashMenuInventoryClickEvent(UUID uuid, ConfigInteractiveMenu rm, InventoryClickEvent click_event) {
		super(click_event);
		this.player = uuid;
		this.menu = rm;
		this.event = click_event;
	}
	
	private UUID player;
	
	private ConfigInteractiveMenu menu;
	
	private InventoryClickEvent event;
	
	private boolean cancel_click = true, cancelled;
	
	


	public void setCancelClick(boolean cancel_click) {this.cancel_click = cancel_click;}
	
	
	public boolean isCancelClick() {return cancel_click;}
	
	public boolean isVisualizableItem() {return this.menu.getVisualizableItems().containsKey(event.getSlot());}
	
	public EditionComponent getEditionComponent() {return this.menu.getVisualizableItems().get(event.getSlot());}

	public UUID getPlayer() {return player;}

	public ConfigInteractiveMenu getMenu() {return menu;}
	
	public InventoryClickEvent getClickEvent() {return this.event;}
	


	@Override
	public boolean isCancelled() {
		return cancelled;
	}


	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
