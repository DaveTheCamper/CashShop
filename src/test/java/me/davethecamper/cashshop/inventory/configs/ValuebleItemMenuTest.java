package me.davethecamper.cashshop.inventory.configs;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ValuebleItemMenuTest {

    private ValuebleItemMenu valuebleItemMenu;
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
        valuebleItemMenu = spy(new ValuebleItemMenu("test", itemConfig, previousMenu, itemProperties, 100.0));
        valuebleItemMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(valuebleItemMenu);
        assertEquals("test", valuebleItemMenu.getId());
        assertEquals(itemConfig, valuebleItemMenu.getItemConfig());
        assertEquals(previousMenu, valuebleItemMenu.getPrevious());
        assertEquals(itemProperties, valuebleItemMenu.getItemProperties());
        assertEquals(100.0, valuebleItemMenu.getValueInCash());
    }

    @Test
    public void testReload() {
        doNothing().when(valuebleItemMenu).load();
        valuebleItemMenu.reload();
        verify(valuebleItemMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        doNothing().when(valuebleItemMenu).changeIdentifierSlot(anyInt());
        doNothing().when(valuebleItemMenu).registerItem(anyString(), any(ItemStack.class), anyInt());
        valuebleItemMenu.load();
        verify(valuebleItemMenu, times(1)).changeIdentifierSlot(6);
        verify(valuebleItemMenu, times(1)).updateValueItem();
    }

    @Test
    public void testUpdateValueItem() {
        when(itemConfig.getString("items.valuable.material")).thenReturn("material");
        when(itemConfig.getString("items.valuable.name")).thenReturn("name");
        when(itemConfig.getStringAsItemLore("items.valuable.lore")).thenReturn("lore @value_cash");
        doReturn(itemStack).when(ItemGenerator.class);
        valuebleItemMenu.updateValueItem();
        verify(valuebleItemMenu, times(1)).registerItem(eq("value_tag"), eq(itemStack), eq(24));
    }

    @Test
    public void testChangeValueItemSlot() {
        valuebleItemMenu.changeValueItemSlot(5);
        verify(valuebleItemMenu, times(1)).changeItemSlot("value_tag", 5);
    }

    @Test
    public void testGetValueInCash() {
        assertEquals(100.0, valuebleItemMenu.getValueInCash());
    }

    @Test
    public void testSetValueInCash() {
        valuebleItemMenu.setValueInCash(200.0);
        assertEquals(200.0, valuebleItemMenu.getValueInCash());
    }

    @Test
    public void testChangerVarHandler() {
        valuebleItemMenu.changerVarHandler("value_tag", 200.0);
        assertEquals(200.0, valuebleItemMenu.getValueInCash());
        verify(valuebleItemMenu, times(1)).reload();
    }

    @Test
    public void testInventoryClickHandler() {
        when(valuebleItemMenu.getSlots().get(anyInt())).thenReturn("value_tag");
        assertTrue(valuebleItemMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(valuebleItemMenu, times(1)).createVarChanger("value_tag", WaitingForChat.Primitives.DOUBLE);

        when(valuebleItemMenu.getSlots().get(anyInt())).thenReturn("other");
        assertFalse(valuebleItemMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
    }

    @Test
    public void testSaveHandler() {
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        valuebleItemMenu.saveHandler(fileConfiguration);
        verify(fileConfiguration, times(1)).set("value", valuebleItemMenu.getValueInCash());
    }
}