package me.davethecamper.cashshop.events;

import java.util.UUID;

import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashPlayerInventoryClickEventTest {

    private UUID uuid;
    private ConfigInteractiveMenu menu;
    private InventoryClickEvent clickEvent;
    private CashPlayerInventoryClickEvent event;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        menu = mock(ConfigInteractiveMenu.class);
        clickEvent = mock(InventoryClickEvent.class);
        event = new CashPlayerInventoryClickEvent(uuid, menu, clickEvent);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(uuid, event.getPlayer());
        assertEquals(menu, event.getMenu());
        assertEquals(clickEvent, event.getClickEvent());
    }

    @Test
    public void testSetAndIsCancelClick() {
        event.setCancelClick(false);
        assertFalse(event.isCancelClick());

        event.setCancelClick(true);
        assertTrue(event.isCancelClick());
    }

    @Test
    public void testHandlers() {
        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers);

        HandlerList handlerList = CashPlayerInventoryClickEvent.getHandlerList();
        assertNotNull(handlerList);
    }
}