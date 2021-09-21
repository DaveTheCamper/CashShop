package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.davethecamper.cashshop.api.info.TransactionInfo;

public class TransactionCompleteEvent extends Event {
	
	public TransactionCompleteEvent(UUID player, TransactionInfo ti) {
		this.uuid = player;
		this.transaction = ti;
	}
	
	private UUID uuid;
	
	private TransactionInfo transaction;
	
	
	public UUID getPlayer() {return uuid;}

	public TransactionInfo getTransaction() {return transaction;}
	
	

	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
