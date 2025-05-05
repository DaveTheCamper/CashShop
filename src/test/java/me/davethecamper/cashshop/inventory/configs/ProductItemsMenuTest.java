package me.davethecamper.cashshop.inventory.configs;

import lombok.Getter;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.configs.temporary.TemporarySellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductItemsMenuTest {

    private ProductItemsMenu productItemsMenu;
    private ConfigManager itemConfig;
    private SellProductMenu dad;
    private UUID uuid;
    private ItemStack itemStack;
    private Consumer<ProductItemsMenu> consumer;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        dad = mock(SellProductMenu.class);
        uuid = UUID.randomUUID();
        itemStack = mock(ItemStack.class);
        consumer = mock(Consumer.class);
        productItemsMenu = spy(new ProductItemsMenu("test", itemConfig, dad, consumer));
        productItemsMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(productItemsMenu);
        assertEquals("test", productItemsMenu.getId());
        assertEquals(itemConfig, productItemsMenu.getItemConfig());
        assertEquals(dad, productItemsMenu.getDad());
        assertEquals(consumer, productItemsMenu.getConsumer());
    }

    @Test
    public void testLoad() {
        doNothing().when(productItemsMenu).changeInventorySize(anyInt());
        doNothing().when(productItemsMenu).changeSaveButtonSlot(anyInt());
        doNothing().when(productItemsMenu).changeCancelButtonSlot(anyInt());
        doNothing().when(productItemsMenu).changeIdentifierSlot(anyInt());
        doNothing().when(productItemsMenu).updateItems();
        productItemsMenu.load();
        verify(productItemsMenu, times(1)).changeInventorySize(54);
        verify(productItemsMenu, times(1)).changeSaveButtonSlot(47);
        verify(productItemsMenu, times(1)).changeCancelButtonSlot(51);
        verify(productItemsMenu, times(1)).changeIdentifierSlot(-1);
        verify(productItemsMenu, times(1)).updateItems();
    }

    @Test
    public void testUpdateItems() {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(itemStack);
        productItemsMenu.updateItems(items);
        assertEquals(items, productItemsMenu.getItems());
    }

    @Test
    public void testStartEditing() {
        Player player = mock(Player.class);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);
        doNothing().when(Bukkit.getPluginManager()).callEvent(any());
        productItemsMenu.startEditing(uuid, false);
        verify(player, times(1)).openInventory(productItemsMenu.getInventory());
    }

    @Test
    public void testFinishEditing() {
        productItemsMenu.finishEditing(uuid, true);
        assertTrue(productItemsMenu.isIntentionToSave());
        verify(consumer, times(1)).accept(productItemsMenu);
    }

    @Test
    public void testInventoryClickHandler() {
        when(productItemsMenu.getSlots().get(anyInt())).thenReturn("save_button");
        assertTrue(productItemsMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(productItemsMenu, times(1)).finishEditing(uuid, true);

        when(productItemsMenu.getSlots().get(anyInt())).thenReturn("cancel_button");
        assertTrue(productItemsMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
        verify(productItemsMenu, times(1)).finishEditing(uuid, false);

        when(productItemsMenu.getSlots().get(anyInt())).thenReturn("other");
        assertFalse(productItemsMenu.inventoryClickHandler(uuid, 0, 8, InventoryAction.PICKUP_ALL));
    }

    @Test
    public void testInventoryPlayerClickHandler() {
        productItemsMenu.inventoryPlayerClickHandler(0, itemStack);
        assertTrue(productItemsMenu.getItems().contains(itemStack));
        verify(productItemsMenu, times(1)).updateItems();
    }

    @Test
    public void testSaveHandler() {
        productItemsMenu.saveHandler();
        verify(dad, times(1)).updateItems(productItemsMenu);
    }

    @Test
    public void testClone() {
        ProductItemsMenu clonedMenu = productItemsMenu.clone();
        assertNotNull(clonedMenu);
        assertEquals(productItemsMenu.getId(), clonedMenu.getId());
        assertEquals(productItemsMenu.getItemConfig(), clonedMenu.getItemConfig());
        assertEquals(productItemsMenu.getDad(), clonedMenu.getDad());
    }

    @Test
    public void testCreateTemporaryProductItems() {
        TemporarySellProductMenu temporaryMenu = mock(TemporarySellProductMenu.class);
        ProductItemsMenu tempProductItems = ProductItemsMenu.createTemporaryProductItems(temporaryMenu, consumer);
        assertNotNull(tempProductItems);
        assertEquals(temporaryMenu, tempProductItems.getDad());
        assertEquals(consumer, tempProductItems.getConsumer());
    }
}