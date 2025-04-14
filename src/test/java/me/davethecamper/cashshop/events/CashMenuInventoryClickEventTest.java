package me.davethecamper.cashshop.events;

import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashMenuInventoryClickEventTest {

    private UUID uuid;
    private ConfigInteractiveMenu menu;
    private InventoryClickEvent clickEvent;
    private CashMenuInventoryClickEvent event;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        menu = mock(ConfigInteractiveMenu.class);
        clickEvent = mock(InventoryClickEvent.class);
        event = new CashMenuInventoryClickEvent(uuid, menu, clickEvent);
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
    public void testIsVisualizableItem() {
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        visualizableItems.put(1, component);
        when(menu.getVisualizableItems()).thenReturn(visualizableItems);
        when(clickEvent.getSlot()).thenReturn(1);

        assertTrue(event.isVisualizableItem());

        when(clickEvent.getSlot()).thenReturn(2);
        assertFalse(event.isVisualizableItem());
    }

    @Test
    public void testGetEditionComponent() {
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        visualizableItems.put(1, component);
        when(menu.getVisualizableItems()).thenReturn(visualizableItems);
        when(clickEvent.getSlot()).thenReturn(1);

        assertEquals(component, event.getEditionComponent());

        when(clickEvent.getSlot()).thenReturn(2);
        assertNull(event.getEditionComponent());
    }

    @Test
    public void testIsCancelledAndSetCancelled() {
        event.setCancelled(true);
        assertTrue(event.isCancelled());

        event.setCancelled(false);
        assertFalse(event.isCancelled());
    }
}