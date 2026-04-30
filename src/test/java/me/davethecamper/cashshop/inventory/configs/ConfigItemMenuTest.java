package me.davethecamper.cashshop.inventory.configs;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigItemMenuTest {

    private ConfigItemMenu configItemMenu;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private ItemMenuProperties itemProperties;
    private UUID uuid;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        itemProperties = mock(ItemMenuProperties.class);
        uuid = UUID.randomUUID();
        itemStack = mock(ItemStack.class);
        configItemMenu = spy(new ConfigItemMenu("test", itemConfig, previousMenu, itemProperties));
        configItemMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(configItemMenu);
        assertEquals("test", configItemMenu.getId());
        assertEquals(itemConfig, configItemMenu.getItemConfig());
        assertEquals(previousMenu, configItemMenu.getPrevious());
        assertEquals(itemProperties, configItemMenu.getItemProperties());
    }

    @Test
    public void testReload() {
        doNothing().when(configItemMenu).load();
        configItemMenu.reload();
        verify(configItemMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        doNothing().when(configItemMenu).changeIdentifierSlot(anyInt());
        doNothing().when(configItemMenu).registerItem(anyString(), any(ItemStack.class), anyInt());
        configItemMenu.load();
        verify(configItemMenu, times(1)).changeIdentifierSlot(15);
        verify(configItemMenu, times(1)).registerItem(eq("change_name"), any(ItemStack.class), eq(19));
        verify(configItemMenu, times(1)).registerItem(eq("glow"), any(ItemStack.class), eq(20));
        verify(configItemMenu, times(1)).registerItem(eq("add_lore"), any(ItemStack.class), eq(21));
        verify(configItemMenu, times(1)).registerItem(eq("flags"), any(ItemStack.class), eq(18));
    }

    @Test
    public void testChangeLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("New Lore");
        configItemMenu.changeLore(lore, "add_lore");
        verify(itemProperties, times(1)).setLore(lore);
        verify(configItemMenu, times(1)).updateLore();
        verify(configItemMenu, times(1)).updateItem();
    }

    @Test
    public void testUpdateItem() {
        configItemMenu.updateItem();
        verify(configItemMenu, times(1)).registerItem(eq("item"), eq(itemProperties.getItem()), eq(2));
    }

    @Test
    public void testGetItemProperties() {
        assertEquals(itemProperties, configItemMenu.getItemProperties());
    }

    @Test
    public void testSaveHandler() {
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        configItemMenu.saveHandler(fileConfiguration);
        verify(fileConfiguration, times(1)).set("type", configItemMenu.getDescriber());
        verify(fileConfiguration, times(1)).set("item.name", itemProperties.getName());
        verify(fileConfiguration, times(1)).set("item.glow", itemProperties.isGlow());
        verify(fileConfiguration, times(1)).set("item.lore", itemProperties.getLore());
        verify(fileConfiguration, times(1)).set("item.item", itemProperties.getItem());
    }

    @Test
    public void testDelete() {
        CashShop cashShop = mock(CashShop.class);
        when(CashShop.getInstance()).thenReturn(cashShop);
        String mainPath = "pluginDataFolder";
        when(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath())
                .thenReturn(mainPath);
        File file = mock(File.class);
        doReturn(file).when(configItemMenu).getFile(anyString());
        configItemMenu.delete();
        verify(file, times(1)).delete();
        verify(cashShop, times(1)).unregister(configItemMenu.getDescriber(), configItemMenu.getId());
        verify(configItemMenu, times(1)).backOneInventory(eq(uuid), any(ReciclableMenu.class));
    }

    @Test
    public void testClone() {
        ConfigItemMenu clonedMenu = configItemMenu.clone();
        assertNotNull(clonedMenu);
        assertEquals(configItemMenu.getId(), clonedMenu.getId());
        assertEquals(configItemMenu.getItemConfig(), clonedMenu.getItemConfig());
        assertEquals(configItemMenu.getPrevious(), clonedMenu.getPrevious());
        assertEquals(configItemMenu.getItemProperties(), clonedMenu.getItemProperties());
    }

    @Test
    public void testChangerVarHandler() {
        configItemMenu.changerVarHandler("change_name", "New Name");
        verify(itemProperties, times(1)).setName("New Name");
        verify(configItemMenu, times(1)).updateName();
        verify(configItemMenu, times(1)).updateItem();
    }

    @Test
    public void testInventoryClickHandler() {
        when(configItemMenu.getSlots().get(19)).thenReturn("change_name");
        assertTrue(configItemMenu.inventoryClickHandler(uuid, 19, 0, InventoryAction.PICKUP_ALL));

        when(configItemMenu.getSlots().get(20)).thenReturn("glow");
        assertTrue(configItemMenu.inventoryClickHandler(uuid, 20, 0, InventoryAction.PICKUP_ALL));

        when(configItemMenu.getSlots().get(18)).thenReturn("flags");
        assertTrue(configItemMenu.inventoryClickHandler(uuid, 18, 0, InventoryAction.PICKUP_ALL));

        when(configItemMenu.getSlots().get(21)).thenReturn("add_lore");
        assertTrue(configItemMenu.inventoryClickHandler(uuid, 21, 0, InventoryAction.PICKUP_ALL));
    }

    @Test
    public void testInventoryPlayerClickHandler() {
        when(itemStack.getType()).thenReturn(Material.DIAMOND_SWORD);
        assertTrue(configItemMenu.inventoryPlayerClickHandler(0, itemStack));
        verify(itemProperties, times(1)).readItem(itemStack);
        verify(configItemMenu, times(1)).updateItem();
        verify(configItemMenu, times(1)).updateName();
        verify(configItemMenu, times(1)).updateGlow();
        verify(configItemMenu, times(1)).updateLore();
        verify(configItemMenu, times(1)).updateFlags();
    }
}