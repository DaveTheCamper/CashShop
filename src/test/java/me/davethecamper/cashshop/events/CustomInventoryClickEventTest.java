package me.davethecamper.cashshop.events;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomInventoryClickEventTest {

    private InventoryClickEvent clickEvent;
    private CustomInventoryClickEvent event;

    @BeforeEach
    public void setUp() {
        clickEvent = mock(InventoryClickEvent.class);
        event = new CustomInventoryClickEvent(clickEvent) {
        };
    }

    @Test
    public void testIsRightClick() {
        when(clickEvent.getAction()).thenReturn(InventoryAction.PICKUP_HALF);
        assertTrue(event.isRightClick());

        when(clickEvent.getAction()).thenReturn(InventoryAction.PICKUP_ALL);
        assertFalse(event.isRightClick());
    }

    @Test
    public void testIsLeftClick() {
        when(clickEvent.getAction()).thenReturn(InventoryAction.PICKUP_ALL);
        assertTrue(event.isLeftClick());

        when(clickEvent.getAction()).thenReturn(InventoryAction.COLLECT_TO_CURSOR);
        assertTrue(event.isLeftClick());

        when(clickEvent.getAction()).thenReturn(InventoryAction.SWAP_WITH_CURSOR);
        assertTrue(event.isLeftClick());

        when(clickEvent.getAction()).thenReturn(InventoryAction.PICKUP_HALF);
        assertFalse(event.isLeftClick());
    }

    @Test
    public void testHandlers() {
        assertNotNull(event.getHandlers());
        assertNotNull(CustomInventoryClickEvent.getHandlerList());
    }
}