package me.davethecamper.cashshop.inventory;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReciclableMenuTest {

    private ReciclableMenu reciclableMenu;
    private UUID uuid;
    private Inventory inventory;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        inventory = mock(Inventory.class);
        itemStack = mock(ItemStack.class);
        reciclableMenu = spy(new TestReciclableMenu());
        reciclableMenu.setPlayer(uuid);
    }

    @Test
    public void testGetInventory() {
        doNothing().when(reciclableMenu).generateInventory();
        reciclableMenu.getInventory();
        verify(reciclableMenu, times(1)).generateInventory();
    }

    @Test
    public void testGenerateInventory() {
        doReturn(inventory).when(Bukkit.class);
        doReturn(itemStack).when(ItemGenerator.class);
        reciclableMenu.generateInventory();
        assertNotNull(reciclableMenu.getInventory());
    }

    @Test
    public void testBackOneInventory() {
        ReciclableMenu previousMenu = mock(ReciclableMenu.class);
        reciclableMenu.setPrevious(previousMenu);
        doNothing().when(Bukkit.getPluginManager()).callEvent(any(ChangeEditorInventoryEvent.class));
        doReturn(true).when(Bukkit.getOfflinePlayer(uuid)).isOnline();
        doReturn(mock(Player.class)).when(Bukkit.getPlayer(uuid));

        reciclableMenu.backOneInventory(uuid);

        verify(Bukkit.getPluginManager(), times(1)).callEvent(any(ChangeEditorInventoryEvent.class));
    }

    @Test
    public void testUpdate() {
        reciclableMenu.update(0, itemStack);
        assertEquals(itemStack, reciclableMenu.getInventory().getItem(0));
    }

    @Test
    public void testDisposeInventory() {
        reciclableMenu.disposeInventory();
        assertNull(reciclableMenu.getInventory());
    }

    private static class TestReciclableMenu extends ReciclableMenu {

        @Override
        protected HashMap<Integer, ItemStack> getUpdatedItems() {
            return new HashMap<>();
        }

        @Override
        protected boolean updateBeforeBack() {
            return false;
        }

        @Override
        public boolean inventoryClick(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
            return false;
        }

        @Override
        public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
            return false;
        }

        @Override
        public void reload() {
        }
    }
}