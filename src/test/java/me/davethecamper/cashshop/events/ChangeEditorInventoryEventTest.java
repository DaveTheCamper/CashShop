package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.davethecamper.cashshop.inventory.ReciclableMenu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChangeEditorInventoryEventTest {

    private UUID uuid;
    private ReciclableMenu reciclableMenu;
    private ChangeEditorInventoryEvent event;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        reciclableMenu = mock(ReciclableMenu.class);
        event = new ChangeEditorInventoryEvent(uuid, reciclableMenu);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(uuid, event.getUuid());
        assertEquals(reciclableMenu, event.getReciclableMenu());
    }

    @Test
    public void testHandlers() {
        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers);

        HandlerList handlerList = ChangeEditorInventoryEvent.getHandlerList();
        assertNotNull(handlerList);
    }
}