package me.davethecamper.cashshop.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.davethecamper.cashshop.inventory.WaitingForChat;

public class WaitingChatEvent extends Event {

	public WaitingChatEvent(WaitingForChat e) {
		this.waiting = e;
	}
	
	private WaitingForChat waiting;
	
	public WaitingForChat getWaitingForChat() {return this.waiting;}
	
	
	
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
