package me.davethecamper.cashshop.events;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class CashPlayerProductUpdateEvent extends Event implements Cancellable {

    private final CashPlayer cashPlayer;

    private final SellProductMenu sellingProduct;

    private final boolean addList;

    private boolean cancelled;


    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
