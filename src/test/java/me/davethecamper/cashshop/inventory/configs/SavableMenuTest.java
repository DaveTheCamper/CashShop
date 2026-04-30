package me.davethecamper.cashshop.inventory.configs;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryAction;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SavableMenuTest {

    private SavableMenu savableMenu;
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
        savableMenu = spy(new TestSavableMenu("test", itemConfig, previousMenu));
        savableMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(savableMenu);
        assertEquals("test", savableMenu.getId());
        assertEquals(itemConfig, savableMenu.getItemConfig());
        assertEquals(previousMenu, savableMenu.getPrevious());
    }

    @Test
    public void testReload() {
        doNothing().when(savableMenu).load();
        savableMenu.reload();
        verify(savableMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        ItemStack saveItem = mock(ItemStack.class);
        ItemStack cancelItem = mock(ItemStack.class);
        when(itemConfig.getItemFromConfig("items.save")).thenReturn(saveItem);
        when(itemConfig.getItemFromConfig("items.cancel")).thenReturn(cancelItem);
        savableMenu.load();
        verify(savableMenu, times(1)).registerItem(eq("save_button"), eq(saveItem), eq(29));
        verify(savableMenu, times(1)).registerItem(eq("cancel_button"), eq(cancelItem), eq(33));
    }

    @Test
    public void testSave() {
        File file = mock(File.class);
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        when(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath())
                .thenReturn("pluginDataFolder");
        when(YamlConfiguration.loadConfiguration(file)).thenReturn(fileConfiguration);
        doReturn(file).when(savableMenu).getFile(anyString());
        doReturn(fileConfiguration).when(savableMenu).saveHandler(fileConfiguration);

        savableMenu.save();

        verify(fileConfiguration, times(1)).save(file);
        verify(cashShop, times(1)).registerObject(anyString(), eq("test"), eq(savableMenu));
    }

    @Test
    public void testChangeSaveButtonSlot() {
        savableMenu.changeSaveButtonSlot(5);
        verify(savableMenu, times(1)).changeItemSlot("save_button", 5);
    }

    @Test
    public void testChangeCancelButtonSlot() {
        savableMenu.changeCancelButtonSlot(5);
        verify(savableMenu, times(1)).changeItemSlot("cancel_button", 5);
    }

    @Test
    public void testInventoryClickHandler() {
        when(savableMenu.getSlots().get(anyInt())).thenReturn("save_button");
        assertTrue(savableMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(savableMenu, times(1)).save();
        verify(savableMenu, times(1)).backOneInventory(uuid);

        when(savableMenu.getSlots().get(anyInt())).thenReturn("cancel_button");
        assertTrue(savableMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(savableMenu, times(2)).backOneInventory(uuid);

        when(savableMenu.getSlots().get(anyInt())).thenReturn("other");
        assertFalse(savableMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
    }

    private static class TestSavableMenu extends SavableMenu {

        public TestSavableMenu(String identificador, ConfigManager item_config, ReciclableMenu previous) {
            super(identificador, item_config, previous);
        }

        @Override
        protected FileConfiguration saveHandler(FileConfiguration fc) {
            return fc;
        }

        @Override
        protected void saveHandler() {
            // Implement save logic for testing
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