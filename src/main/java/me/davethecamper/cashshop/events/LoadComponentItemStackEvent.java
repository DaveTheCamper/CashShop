package me.davethecamper.cashshop.events;

import lombok.Builder;
import lombok.Data;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@Builder
public class LoadComponentItemStackEvent extends Event {

    private final String fileName;

    private final boolean staticComponent;

    private final ConfigItemMenu configItemMenu;

    private final ItemMenuProperties itemMenuProperties;


    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
