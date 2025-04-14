package me.davethecamper.cashshop.inventory.configs;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.CashMenuInventoryClickEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.edition.EditInteractiveMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigInteractiveMenuTest {

    private ConfigInteractiveMenu configInteractiveMenu;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private ItemMenuProperties itemProperties;
    private EditInteractiveMenu editInteractiveMenu;
    private UUID uuid;
    private Inventory inventory;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        itemProperties = mock(ItemMenuProperties.class);
        editInteractiveMenu = mock(EditInteractiveMenu.class);
        uuid = UUID.randomUUID();
        inventory = mock(Inventory.class);
        itemStack = mock(ItemStack.class);
        configInteractiveMenu = spy(
                new ConfigInteractiveMenu("test", itemConfig, previousMenu, itemProperties, 27, "Test Menu"));
        configInteractiveMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(configInteractiveMenu);
        assertEquals("test", configInteractiveMenu.getId());
        assertEquals(itemConfig, configInteractiveMenu.getItemConfig());
        assertEquals(previousMenu, configInteractiveMenu.getPrevious());
        assertEquals(itemProperties, configInteractiveMenu.getItemProperties());
        assertEquals(27, configInteractiveMenu.getSize());
        assertEquals("Test Menu", configInteractiveMenu.getName());
    }

    @Test
    public void testReload() {
        doNothing().when(configInteractiveMenu).load();
        configInteractiveMenu.reload();
        verify(configInteractiveMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        doNothing().when(configInteractiveMenu).changeIdentifierSlot(anyInt());
        doNothing().when(configInteractiveMenu).registerItem(anyString(), any(ItemStack.class), anyInt());
        doNothing().when(configInteractiveMenu).updateSizeButton();
        doNothing().when(configInteractiveMenu).updateTitleButton();
        configInteractiveMenu.load();
        verify(configInteractiveMenu, times(1)).changeIdentifierSlot(6);
        verify(configInteractiveMenu, times(1)).registerItem(eq("edit_button"), any(ItemStack.class), eq(23));
        verify(configInteractiveMenu, times(1)).updateSizeButton();
        verify(configInteractiveMenu, times(1)).updateTitleButton();
    }

    @Test
    public void testGetTotalReplacersByRegex() {
        CashShop cashShop = mock(CashShop.class);
        when(CashShop.getInstance()).thenReturn(cashShop);
        CategoriesManager categoriesManager = mock(CategoriesManager.class);
        when(cashShop.getCategoriesManager()).thenReturn(categoriesManager);
        ConfigInteractiveMenu category = mock(ConfigInteractiveMenu.class);
        when(categoriesManager.getCategorie(anyString())).thenReturn(category);
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(category.getVisualizableItems()).thenReturn(visualizableItems);

        int total = configInteractiveMenu.getTotalReplacersByRegex("test");
        assertEquals(1, total);
    }

    @Test
    public void testGetTotalReplacers() {
        CashShop cashShop = mock(CashShop.class);
        when(CashShop.getInstance()).thenReturn(cashShop);
        CategoriesManager categoriesManager = mock(CategoriesManager.class);
        when(cashShop.getCategoriesManager()).thenReturn(categoriesManager);
        ConfigInteractiveMenu category = mock(ConfigInteractiveMenu.class);
        when(categoriesManager.getCategorie(anyString())).thenReturn(category);
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(category.getVisualizableItems()).thenReturn(visualizableItems);

        int total = configInteractiveMenu.getTotalReplacers("test");
        assertEquals(1, total);
    }

    @Test
    public void testGetSlotsWithName() {
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(configInteractiveMenu.getVisualizableItems()).thenReturn(visualizableItems);

        List<Integer> slots = configInteractiveMenu.getSlotsWithName("test");
        assertEquals(1, slots.size());
        assertEquals(1, slots.get(0).intValue());
    }

    @Test
    public void testReplaceIndicators() {
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(configInteractiveMenu.getVisualizableItems()).thenReturn(visualizableItems);

        List<Integer> slots = configInteractiveMenu.replaceIndicators("test", itemStack, "identifier");
        assertEquals(1, slots.size());
        assertEquals(1, slots.get(0).intValue());
    }

    @Test
    public void testIsReplacedItem() {
        configInteractiveMenu.getReplaces().put(1, "identifier");
        assertTrue(configInteractiveMenu.isReplacedItem(1));
        assertFalse(configInteractiveMenu.isReplacedItem(2));
    }

    @Test
    public void testGetReplacedItem() {
        configInteractiveMenu.getReplaces().put(1, "identifier");
        assertEquals("identifier", configInteractiveMenu.getReplacedItem(1));
        assertNull(configInteractiveMenu.getReplacedItem(2));
    }

    @Test
    public void testGetComponentsByName() {
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(configInteractiveMenu.getVisualizableItems()).thenReturn(visualizableItems);

        List<EditionComponent> components = configInteractiveMenu.getComponentsByName("test");
        assertEquals(1, components.size());
        assertEquals(component, components.get(0));
    }

    @Test
    public void testGetComponentBySlot() {
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        visualizableItems.put(1, component);
        when(configInteractiveMenu.getVisualizableItems()).thenReturn(visualizableItems);

        EditionComponent result = configInteractiveMenu.getComponentBySlot(1);
        assertEquals(component, result);
    }

    @Test
    public void testUpdateEntirelyInventory() {
        ConfigInteractiveMenu newMenu = mock(ConfigInteractiveMenu.class);
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        visualizableItems.put(1, component);
        when(newMenu.getVisualizableItems()).thenReturn(visualizableItems);
        HashMap<Integer, String> replaces = new HashMap<>();
        replaces.put(1, "identifier");
        when(newMenu.getReplaces()).thenReturn(replaces);

        configInteractiveMenu.updateEntirelyInventory(newMenu);

        assertEquals(visualizableItems, configInteractiveMenu.getVisualizableItems());
        assertEquals(replaces, configInteractiveMenu.getReplaces());
    }

    @Test
    public void testUpdateConsumerByName() {
        Consumer<CashMenuInventoryClickEvent> consumer = mock(Consumer.class);
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(configInteractiveMenu.getVisualizableItems()).thenReturn(visualizableItems);

        configInteractiveMenu.updateConsumerByName(consumer, "test");

        verify(component, times(1)).setConsumer(consumer);
    }

    @Test
    public void testUpdateProduct() {
        configInteractiveMenu.updateProduct(itemStack);
        verify(configInteractiveMenu, times(1)).updateSomething(eq(CashShop.REPLACE_ITEM_SELLING_BUTTON),
                any(EditionComponent.class));
    }

    @Test
    public void testUpdateSomething() {
        EditionComponent newComponent = mock(EditionComponent.class);
        HashMap<Integer, EditionComponent> visualizableItems = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        when(component.getName()).thenReturn("test");
        visualizableItems.put(1, component);
        when(configInteractiveMenu.getVisualizableItems()).thenReturn(visualizableItems);

        configInteractiveMenu.updateSomething("test", newComponent);

        assertEquals(newComponent, configInteractiveMenu.getVisualizableItems().get(1));
    }

    @Test
    public void testUpdateSizeButton() {
        doNothing().when(configInteractiveMenu).registerItem(anyString(), any(ItemStack.class), anyInt());
        configInteractiveMenu.updateSizeButton();
        verify(configInteractiveMenu, times(1)).registerItem(eq("inv_size_button"), any(ItemStack.class), eq(24));
    }

    @Test
    public void testUpdateTitleButton() {
        doNothing().when(configInteractiveMenu).registerItem(anyString(), any(ItemStack.class), anyInt());
        configInteractiveMenu.updateTitleButton();
        verify(configInteractiveMenu, times(1)).registerItem(eq("title_button"), any(ItemStack.class), eq(25));
    }

    @Test
    public void testUpdateEditor() {
        configInteractiveMenu.updateEditor(editInteractiveMenu);
        assertEquals(editInteractiveMenu, configInteractiveMenu.getEdition());
    }

    @Test
    public void testGetLogInventory() {
        configInteractiveMenu.setLogInventory(inventory);
        assertEquals(inventory, configInteractiveMenu.getLogInventory());
    }

    @Test
    public void testOpenLogInventory() {
        CashPlayer cashPlayer = mock(CashPlayer.class);
        CashShop cashShop = mock(CashShop.class);
        when(CashShop.getInstance()).thenReturn(cashShop);
        when(cashShop.getCashPlayer(uuid)).thenReturn(cashPlayer);

        Player player = mock(Player.class);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);

        configInteractiveMenu.openLogInventory(player);

        verify(cashPlayer, times(1)).updateCurrentInventory(configInteractiveMenu, false, false);
        verify(cashPlayer, times(1)).openLogFromCurrentInventory();
    }

    @Test
    public void testGenerateLogInventory() {
        CashPlayer cashPlayer = mock(CashPlayer.class);
        configInteractiveMenu.generateLogInventory(cashPlayer);
        assertNotNull(configInteractiveMenu.getLogInventory());
    }

    @Test
    public void testGenerateItem() {
        EditionComponent component = mock(EditionComponent.class);
        when(component.getType()).thenReturn(EditionComponentType.DISPLAY_ITEM);
        when(component.getItemStack()).thenReturn(itemStack);

        ItemStack result = configInteractiveMenu.generateItem(component);
        assertEquals(itemStack, result);
    }

    @Test
    public void testGenerateItemWithPlayer() {
        EditionComponent component = mock(EditionComponent.class);
        when(component.getType()).thenReturn(EditionComponentType.DISPLAY_ITEM);
        when(component.getItemStack()).thenReturn(itemStack);

        CashPlayer cashPlayer = mock(CashPlayer.class);
        ItemStack result = configInteractiveMenu.generateItem(component, cashPlayer);
        assertEquals(itemStack, result);
    }

    @Test
    public void testSaveHandler() {
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        configInteractiveMenu.saveHandler(fileConfiguration);
        verify(fileConfiguration, times(1)).set("inventory.name", "Test Menu");
        verify(fileConfiguration, times(1)).set("inventory.size", 27);
        verify(fileConfiguration, times(1)).set("inventory.items", null);
    }

    @Test
    public void testClone() {
        ConfigInteractiveMenu clonedMenu = configInteractiveMenu.clone();
        assertNotNull(clonedMenu);
        assertEquals(configInteractiveMenu.getId(), clonedMenu.getId());
        assertEquals(configInteractiveMenu.getItemConfig(), clonedMenu.getItemConfig());
        assertEquals(configInteractiveMenu.getPrevious(), clonedMenu.getPrevious());
        assertEquals(configInteractiveMenu.getItemProperties(), clonedMenu.getItemProperties());
        assertEquals(configInteractiveMenu.getSize(), clonedMenu.getSize());
        assertEquals(configInteractiveMenu.getName(), clonedMenu.getName());
    }

    @Test
    public void testChangerVarHandler() {
        configInteractiveMenu.changerVarHandler("inv_size_button", 54);
        assertEquals(54, configInteractiveMenu.getSize());

        configInteractiveMenu.changerVarHandler("title_button", "New Title");
        assertEquals("New Title", configInteractiveMenu.getName());
    }

    @Test
    public void testInventoryClickHandler() {
        when(configInteractiveMenu.getSlots().get(23)).thenReturn("edit_button");
        assertTrue(configInteractiveMenu.inventoryClickHandler(uuid, 23, 0, InventoryAction.PICKUP_ALL));

        when(configInteractiveMenu.getSlots().get(24)).thenReturn("inv_size_button");
        assertTrue(configInteractiveMenu.inventoryClickHandler(uuid, 24, 0, InventoryAction.PICKUP_ALL));

        when(configInteractiveMenu.getSlots().get(25)).thenReturn("title_button");
        assertTrue(configInteractiveMenu.inventoryClickHandler(uuid, 25, 0, InventoryAction.PICKUP_ALL));
    }
}