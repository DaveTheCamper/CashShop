package me.davethecamper.cashshop.events;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PreOpenCashInventoryEventTest {

    private UUID uuid;
    private ConfigInteractiveMenu menu;
    private PreOpenCashInventoryEvent event;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        menu = mock(ConfigInteractiveMenu.class);
        event = new PreOpenCashInventoryEvent(uuid, menu);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(uuid, event.getUniqueId());
        assertEquals(menu, event.getMenu());
    }

    @Test
    public void testHandlers() {
        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers);

        HandlerList handlerList = PreOpenCashInventoryEvent.getHandlerList();
        assertNotNull(handlerList);
    }
}