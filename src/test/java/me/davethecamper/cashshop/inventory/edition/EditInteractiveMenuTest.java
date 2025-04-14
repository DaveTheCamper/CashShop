package me.davethecamper.cashshop.inventory.edition;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EditInteractiveMenuTest {

    private EditInteractiveMenu editInteractiveMenu;
    private ConfigManager itemConfig;
    private ConfigInteractiveMenu dad;
    private UUID uuid;
    private Inventory inventory;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        dad = mock(ConfigInteractiveMenu.class);
        uuid = UUID.randomUUID();
        inventory = mock(Inventory.class);
        itemStack = mock(ItemStack.class);
        editInteractiveMenu = spy(new EditInteractiveMenu("test", itemConfig, dad));
        editInteractiveMenu.setPlayer(uuid);
    }

    @Test
    public void testConstructor() {
        assertNotNull(editInteractiveMenu);
        assertEquals("test", editInteractiveMenu.getId());
        assertEquals(itemConfig, editInteractiveMenu.getItemConfig());
        assertEquals(dad, editInteractiveMenu.getDad());
    }

    @Test
    public void testLoad() {
        editInteractiveMenu.load();
        assertEquals(dad.getSize(), editInteractiveMenu.getInventorySize());
    }

    @Test
    public void testLoadItems() {
        HashMap<Integer, EditionComponent> slots = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        slots.put(0, component);
        editInteractiveMenu = spy(new EditInteractiveMenu("test", itemConfig, dad, slots));
        editInteractiveMenu.loadItems();
        verify(editInteractiveMenu, times(1)).loadComponent(component, 0);
    }

    @Test
    public void testLoadComponent() {
        EditionComponent component = mock(EditionComponent.class);
        when(dad.generateItem(component)).thenReturn(itemStack);
        doReturn(itemStack).when(ItemGenerator.class);
        editInteractiveMenu.loadComponent(component, 0);
        verify(editInteractiveMenu, times(1)).registerItem(anyString(), eq(itemStack), eq(0));
    }

    @Test
    public void testGetItems() {
        HashMap<Integer, EditionComponent> slots = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        slots.put(0, component);
        editInteractiveMenu = spy(new EditInteractiveMenu("test", itemConfig, dad, slots));
        assertEquals(slots, editInteractiveMenu.getItems());
    }

    @Test
    public void testStartEditing() {
        Player player = mock(Player.class);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);
        doNothing().when(Bukkit.getPluginManager()).callEvent(any(ChangeEditorInventoryEvent.class));
        editInteractiveMenu.startEditing(uuid);
        verify(player, times(1)).openInventory(editInteractiveMenu.getInventory());
    }

    @Test
    public void testFinishEdition() {
        editInteractiveMenu.finishEdition(true);
        verify(editInteractiveMenu, times(1)).saveHandler();
        assertNull(editInteractiveMenu.getCurrentEditor());
    }

    @Test
    public void testUpdateInventory() {
        editInteractiveMenu.updateInventory();
        assertEquals(dad.getSize(), editInteractiveMenu.getInventorySize());
    }

    @Test
    public void testClone() {
        HashMap<Integer, EditionComponent> slots = new HashMap<>();
        EditionComponent component = mock(EditionComponent.class);
        slots.put(0, component);
        editInteractiveMenu = spy(new EditInteractiveMenu("test", itemConfig, dad, slots));
        EditInteractiveMenu clonedMenu = editInteractiveMenu.clone(dad);
        assertNotNull(clonedMenu);
        assertEquals(editInteractiveMenu.getId(), clonedMenu.getId());
        assertEquals(editInteractiveMenu.getItemConfig(), clonedMenu.getItemConfig());
        assertEquals(editInteractiveMenu.getDad(), clonedMenu.getDad());
        assertEquals(editInteractiveMenu.getItems().size(), clonedMenu.getItems().size());
    }

    @Test
    public void testInventoryClickHandler() {
        EditionComponent component = mock(EditionComponent.class);
        editInteractiveMenu.getItems().put(0, component);
        editInteractiveMenu.getCurrentEditor().setCurrentComponent(component);
        editInteractiveMenu.inventoryClickHandler(uuid, 0, 0, InventoryAction.PICKUP_ALL);
        verify(editInteractiveMenu, times(1)).loadComponent(component, 0);
    }

    @Test
    public void testInventoryPlayerClickHandler() {
        editInteractiveMenu.inventoryPlayerClickHandler(EditingPlayer.BUTTON_SAVE, itemStack);
        verify(editInteractiveMenu, times(1)).finishEdition(true);
    }

    @Test
    public void testSaveHandler() {
        editInteractiveMenu.saveHandler();
        verify(dad, times(1)).updateEditor(editInteractiveMenu);
        verify(editInteractiveMenu, times(1)).finish();
    }

    @Test
    public void testFinish() {
        editInteractiveMenu.finish();
        verify(editInteractiveMenu.getCurrentEditor(), times(1)).finish(true);
        assertNull(editInteractiveMenu.getCurrentEditor());
        verify(dad, times(1)).startEditing(uuid);
    }
}