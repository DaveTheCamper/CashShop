package me.davethecamper.cashshop.events;

import lombok.Getter;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class BuyCashItemEvent extends Event {

	public BuyCashItemEvent(UUID uuid, SellProductMenu product, int amount) {
		this.uuid = uuid;
		this.product = product;
		this.amount = amount;
	}
	
	private UUID uuid;
	
	@Getter
    private int amount;
	
	@Getter
    private SellProductMenu product;
	

	public UUID getUniqueId() {
		return uuid;
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
