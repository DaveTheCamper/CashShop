package me.davethecamper.cashshop.inventory.configs;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IdentificableMenuTest {

    private IdentificableMenu identificableMenu;
    private ConfigManager itemConfig;
    private UUID uuid;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        uuid = UUID.randomUUID();
        identificableMenu = spy(new TestIdentificableMenu("test", itemConfig));
        identificableMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(identificableMenu);
        assertEquals("test", identificableMenu.getId());
        assertEquals(itemConfig, identificableMenu.getMessages());
    }

    @Test
    public void testReload() {
        doNothing().when(identificableMenu).load();
        identificableMenu.reload();
        verify(identificableMenu, times(1)).load();
    }

    @Test
    public void testStartEditing() {
        doNothing().when(Bukkit.getPluginManager()).callEvent(any(ChangeEditorInventoryEvent.class));
        Player player = mock(Player.class);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);
        identificableMenu.startEditing(uuid);
        verify(player, times(1)).openInventory(identificableMenu.getInventory());
    }

    @Test
    public void testRemoveItemByName() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        identificableMenu.removeItem("test_item");
        assertFalse(identificableMenu.getUpdatedItems().containsValue(itemStack));
    }

    @Test
    public void testRemoveItemBySlot() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        identificableMenu.removeItem(1);
        assertFalse(identificableMenu.getUpdatedItems().containsValue(itemStack));
    }

    @Test
    public void testUpdateItemSlot() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        identificableMenu.updateItemSlot("test_item", 2);
        assertEquals(2, identificableMenu.getUpdatedItems().entrySet().stream()
                .filter(entry -> entry.getValue().equals(itemStack)).findFirst().get().getKey());
    }

    @Test
    public void testRegisterItem() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        assertTrue(identificableMenu.getUpdatedItems().containsValue(itemStack));
    }

    @Test
    public void testChangeIdentifierSlot() {
        identificableMenu.changeIdentifierSlot(5);
        verify(identificableMenu, times(1)).changeItemSlot("identifier", 5);
    }

    @Test
    public void testChangeItemStack() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        ItemStack newItemStack = mock(ItemStack.class);
        identificableMenu.changeItemStack("test_item", newItemStack);
        assertEquals(newItemStack, identificableMenu.getUpdatedItems().get(1));
    }

    @Test
    public void testChangeItemSlot() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        identificableMenu.changeItemSlot("test_item", 2);
        assertEquals(2, identificableMenu.getUpdatedItems().entrySet().stream()
                .filter(entry -> entry.getValue().equals(itemStack)).findFirst().get().getKey());
    }

    @Test
    public void testCreateVarChanger() {
        doNothing().when(identificableMenu).createVarChanger(anyString(), any(WaitingForChat.Primitives.class),
                anyBoolean());
        identificableMenu.createVarChanger("test_var", WaitingForChat.Primitives.STRING);
        verify(identificableMenu, times(1)).createVarChanger("test_var", WaitingForChat.Primitives.STRING, true);
    }

    @Test
    public void testChangerVar() {
        doNothing().when(identificableMenu).changerVarHandler(anyString(), any());
        Player player = mock(Player.class);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);
        identificableMenu.changerVar("test_var", "new_value");
        verify(identificableMenu, times(1)).changerVarHandler("test_var", "new_value");
        verify(player, times(1)).openInventory(identificableMenu.getInventory());
    }

    @Test
    public void testChangerVarHandler() {
        File oldFile = mock(File.class);
        File newFile = mock(File.class);
        when(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath())
                .thenReturn("pluginDataFolder");
        doReturn(oldFile).when(identificableMenu).getFile(anyString());
        doReturn(newFile).when(identificableMenu).getFile(anyString());
        CashShop cashShop = mock(CashShop.class);
        when(CashShop.getInstance()).thenReturn(cashShop);
        Player player = mock(Player.class);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);
        identificableMenu.changerVarHandler("identifier", "new_id");
        verify(oldFile, times(1)).renameTo(newFile);
        verify(cashShop, times(1)).update(anyString(), anyString(), anyString());
        assertEquals("new_id", identificableMenu.getId());
    }

    @Test
    public void testGetUpdatedItems() {
        ItemStack itemStack = mock(ItemStack.class);
        identificableMenu.registerItem("test_item", itemStack, 1);
        assertTrue(identificableMenu.getUpdatedItems().containsValue(itemStack));
    }

    @Test
    public void testInventoryClick() {
        when(identificableMenu.isValidSlot(anyInt())).thenReturn(true);
        doReturn(true).when(identificableMenu).inventoryClickHandler(any(UUID.class), anyInt(), anyInt(),
                any(InventoryAction.class));
        assertTrue(identificableMenu.inventoryClick(uuid, 1, 0, InventoryAction.PICKUP_ALL));
        verify(identificableMenu, times(1)).inventoryClickHandler(uuid, 1, 0, InventoryAction.PICKUP_ALL);
    }

    @Test
    public void testInventoryClickHandler() {
        when(identificableMenu.getSlots().get(anyInt())).thenReturn("identifier");
        doNothing().when(identificableMenu).createVarChanger(anyString(), any(WaitingForChat.Primitives.class));
        assertTrue(identificableMenu.inventoryClickHandler(uuid, 1, 0, InventoryAction.PICKUP_ALL));
        verify(identificableMenu, times(1)).createVarChanger("identifier", WaitingForChat.Primitives.STRING);
    }

    @Test
    public void testInventoryPlayerClickHandler() {
        assertTrue(identificableMenu.inventoryPlayerClickHandler(1, mock(ItemStack.class)));
    }

    @Test
    public void testIsValidSlot() {
        identificableMenu.registerItem("test_item", mock(ItemStack.class), 1);
        assertTrue(identificableMenu.isValidSlot(1));
        assertFalse(identificableMenu.isValidSlot(2));
    }

    private static class TestIdentificableMenu extends IdentificableMenu {

        public TestIdentificableMenu(String identifier, ConfigManager item_config) {
            super(identifier, item_config);
        }

        @Override
        protected HashMap<Integer, ItemStack> getUpdatedItems() {
            return super.getUpdatedItems();
        }

        @Override
        public boolean inventoryClick(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
            return super.inventoryClick(uuid, clicked_slot, slot_button, type);
        }

        @Override
        public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
            return super.inventoryPlayerClickHandler(clicked_slot, item);
        }

        @Override
        public void reload() {
            super.reload();
        }
    }
}