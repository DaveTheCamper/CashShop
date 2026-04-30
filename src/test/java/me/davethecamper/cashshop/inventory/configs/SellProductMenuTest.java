package me.davethecamper.cashshop.inventory.configs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import me.davethecamper.cashshop.player.CashPlayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SellProductMenuTest {

    private SellProductMenu sellProductMenu;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private ItemMenuProperties itemProperties;
    private ProductConfig product;
    private UUID uuid;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        itemProperties = mock(ItemMenuProperties.class);
        product = mock(ProductConfig.class);
        uuid = UUID.randomUUID();
        itemStack = mock(ItemStack.class);
        sellProductMenu = spy(
                new SellProductMenu("test", itemConfig, previousMenu, itemProperties, product, 100.0, 60L));
        sellProductMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(sellProductMenu);
        assertEquals("test", sellProductMenu.getId());
        assertEquals(itemConfig, sellProductMenu.getItemConfig());
        assertEquals(previousMenu, sellProductMenu.getPrevious());
        assertEquals(itemProperties, sellProductMenu.getItemProperties());
        assertEquals(product, sellProductMenu.getProduct());
        assertEquals(100.0, sellProductMenu.getValueInCash());
        assertEquals(60L, sellProductMenu.getDelayToBuy());
    }

    @Test
    public void testReload() {
        doNothing().when(sellProductMenu).load();
        sellProductMenu.reload();
        verify(sellProductMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        doNothing().when(sellProductMenu).registerItem(anyString(), any(ItemStack.class), anyInt());
        sellProductMenu.load();
        verify(sellProductMenu, times(1)).registerItem(eq("items_give"), any(ItemStack.class), eq(23));
        verify(sellProductMenu, times(1)).registerItem(eq("delay"), any(ItemStack.class), eq(4));
        verify(sellProductMenu, times(1)).registerItem(eq("commands"), any(ItemStack.class), eq(25));
    }

    @Test
    public void testGetSellingItem() {
        CashPlayer player = mock(CashPlayer.class);
        when(player.getCupom()).thenReturn("DISCOUNT");
        when(player.isCashTransaction()).thenReturn(true);
        when(CashShop.getInstance().getCupomManager().getDiscount(anyString())).thenReturn(10.0);
        when(CashShop.getInstance().getMainConfig().getInt("coin.value")).thenReturn(100);

        ItemStack result = sellProductMenu.getSellingItem(player, 1);
        assertNotNull(result);
    }

    @Test
    public void testUpdateItems() {
        ProductItemsMenu newItems = mock(ProductItemsMenu.class);
        ArrayList<ItemStack> items = new ArrayList<>();
        when(newItems.getItems()).thenReturn(items);

        sellProductMenu.updateItems(newItems);

        verify(product, times(1)).updateItems(items);
        verify(sellProductMenu, times(1)).startEditing(uuid);
    }

    @Test
    public void testSaveHandler() {
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        sellProductMenu.saveHandler(fileConfiguration);
        verify(fileConfiguration, times(1)).set("delay", sellProductMenu.getDelayToBuy());
        verify(fileConfiguration, times(1)).set("selling.items", product.getItems());
        verify(fileConfiguration, times(1)).set("selling.commands", product.getCommands());
    }

    @Test
    public void testClone() {
        SellProductMenu clonedMenu = sellProductMenu.clone();
        assertNotNull(clonedMenu);
        assertEquals(sellProductMenu.getId(), clonedMenu.getId());
        assertEquals(sellProductMenu.getItemConfig(), clonedMenu.getItemConfig());
        assertEquals(sellProductMenu.getPrevious(), clonedMenu.getPrevious());
        assertEquals(sellProductMenu.getItemProperties(), clonedMenu.getItemProperties());
        assertEquals(sellProductMenu.getProduct(), clonedMenu.getProduct());
        assertEquals(sellProductMenu.getValueInCash(), clonedMenu.getValueInCash());
        assertEquals(sellProductMenu.getDelayToBuy(), clonedMenu.getDelayToBuy());
    }

    @Test
    public void testChangerVarHandler() {
        sellProductMenu.changerVarHandler("delay", 120L);
        assertEquals(120L, sellProductMenu.getDelayToBuy());

        sellProductMenu.changerVarHandler("value_tag", 200.0);
        assertEquals(200.0, sellProductMenu.getValueInCash());
    }

    @Test
    public void testInventoryClickHandler() {
        when(sellProductMenu.getSlots().get(anyInt())).thenReturn("delay");
        assertTrue(sellProductMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(sellProductMenu, times(1)).createVarChanger("delay", WaitingForChat.Primitives.LONG, false);

        when(sellProductMenu.getSlots().get(anyInt())).thenReturn("items_give");
        assertTrue(sellProductMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(sellProductMenu, times(1)).startEditing(uuid);

        when(sellProductMenu.getSlots().get(anyInt())).thenReturn("commands");
        assertTrue(sellProductMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(sellProductMenu, times(1)).startEditing(uuid);

        when(sellProductMenu.getSlots().get(anyInt())).thenReturn("other");
        assertFalse(sellProductMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
    }

    @Test
    public void testCreateTemporaryProduct() {
        ProductConfig productConfig = mock(ProductConfig.class);
        ItemStack item = mock(ItemStack.class);
        SellProductMenu tempProduct = SellProductMenu.createTemporaryProduct("temp", 100, 60, productConfig, item);
        assertNotNull(tempProduct);
        assertEquals("temp", tempProduct.getId());
        assertEquals(100, tempProduct.getValueInCash());
        assertEquals(60, tempProduct.getDelayToBuy());
        assertEquals(productConfig, tempProduct.getProduct());
        assertEquals(item, tempProduct.getItemProperties().getItem());
    }
}