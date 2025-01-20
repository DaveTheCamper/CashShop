package me.davethecamper.cashshop.events;

import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BuyCashItemEventTest {

    @Test
    public void testConstructorAndGetters() {
        UUID uuid = UUID.randomUUID();
        SellProductMenu product = Mockito.mock(SellProductMenu.class);
        int amount = 5;

        BuyCashItemEvent event = new BuyCashItemEvent(uuid, product, amount);

        assertEquals(uuid, event.getUniqueId());
        assertEquals(product, event.getProduct());
        assertEquals(amount, event.getAmount());
    }

    @Test
    public void testHandlers() {
        BuyCashItemEvent event = new BuyCashItemEvent(UUID.randomUUID(), Mockito.mock(SellProductMenu.class), 5);

        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers);

        HandlerList handlerList = BuyCashItemEvent.getHandlerList();
        assertNotNull(handlerList);
    }
}