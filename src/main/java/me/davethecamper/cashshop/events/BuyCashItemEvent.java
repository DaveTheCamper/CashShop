package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.davethecamper.cashshop.inventory.configs.SellProductMenu;

public class BuyCashItemEvent extends Event {

	public BuyCashItemEvent(UUID uuid, SellProductMenu product, int amount) {
		this.uuid = uuid;
		this.product = product;
		this.amount = amount;
	}
	
	private UUID uuid;
	
	private int amount;
	
	private SellProductMenu product;
	

	public UUID getUniqueId() {
		return uuid;
	}
	
	public int getAmount() {
		return this.amount;
	}

	public SellProductMenu getProduct() {
		return product;
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
