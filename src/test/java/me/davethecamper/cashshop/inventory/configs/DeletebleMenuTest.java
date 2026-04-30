package me.davethecamper.cashshop.inventory.configs;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeletebleMenuTest {

    private DeletebleMenu deletebleMenu;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private UUID uuid;
    private CashShop cashShop;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        uuid = UUID.randomUUID();
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
        deletebleMenu = spy(new TestDeletebleMenu("test", itemConfig, previousMenu));
        deletebleMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(deletebleMenu);
        assertEquals("test", deletebleMenu.getId());
        assertEquals(itemConfig, deletebleMenu.getItemConfig());
        assertEquals(previousMenu, deletebleMenu.getPrevious());
    }

    @Test
    public void testReload() {
        doNothing().when(deletebleMenu).load();
        deletebleMenu.reload();
        verify(deletebleMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemConfig.getString("items.delete.material")).thenReturn("material");
        when(itemConfig.getString("items.delete.name")).thenReturn("name");
        when(itemConfig.getStringAsItemLore("items.delete.lore")).thenReturn(new ArrayList<>());
        doReturn(itemStack).when(ItemGenerator.class);
        deletebleMenu.load();
        verify(deletebleMenu, times(1)).registerItem(eq("delete"), eq(itemStack),
                eq(deletebleMenu.getInventorySize() - 1));
    }

    @Test
    public void testChangeItemSlot() {
        deletebleMenu.changeItemSlot("delete", 5);
        verify(deletebleMenu, times(1)).changeItemSlot("delete", -1);

        deletebleMenu.changeItemSlot("other", 5);
        verify(deletebleMenu, times(1)).changeItemSlot("other", 5);
    }

    @Test
    public void testInventoryClickHandler() {
        when(deletebleMenu.getSlots().get(anyInt())).thenReturn("delete");
        assertTrue(deletebleMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(deletebleMenu, times(1)).delete();

        when(deletebleMenu.getSlots().get(anyInt())).thenReturn("other");
        assertFalse(deletebleMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
    }

    private static class TestDeletebleMenu extends DeletebleMenu {

        public TestDeletebleMenu(String identificador, ConfigManager item_config, ReciclableMenu previous) {
            super(identificador, item_config, previous);
        }

        @Override
        public void delete() {
            // Implement delete logic for testing
        }

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