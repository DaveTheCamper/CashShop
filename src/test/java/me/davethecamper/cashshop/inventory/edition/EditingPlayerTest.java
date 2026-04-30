package me.davethecamper.cashshop.inventory.edition;

import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EditingPlayerTest {

    private EditingPlayer editingPlayer;
    private UUID uuid;
    private EditInteractiveMenu dad;
    private Inventory inventory;
    private Player player;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        dad = mock(EditInteractiveMenu.class);
        inventory = mock(Inventory.class);
        player = mock(Player.class);
        itemStack = mock(ItemStack.class);

        when(player.getUniqueId()).thenReturn(uuid);
        when(Bukkit.getPlayer(uuid)).thenReturn(player);
        when(player.getInventory()).thenReturn(inventory);

        editingPlayer = new EditingPlayer(uuid, dad);
    }

    @Test
    public void testConstructor() {
        assertNotNull(editingPlayer);
        assertEquals(uuid, editingPlayer.getPlayer());
        assertEquals(dad, editingPlayer.getDad());
    }

    @Test
    public void testSavePlayerItems() {
        editingPlayer.savePlayerItems();
        verify(inventory, times(36)).getItem(anyInt());
    }

    @Test
    public void testLoadEditionTools() {
        when(dad.getMessages().getItemFromConfig(anyString())).thenReturn(itemStack);

        editingPlayer.loadEditionTools();

        verify(inventory, times(9)).setItem(anyInt(), eq(ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r")));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_SAVE), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_CANCEL), eq(itemStack));
    }

    @Test
    public void testLoadMainItems() {
        when(dad.getMessages().getItemFromConfig(anyString())).thenReturn(itemStack);

        editingPlayer.loadMainItems();

        verify(inventory, times(27)).setItem(anyInt(),
                eq(ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r")));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_ITEMS_NOTHING), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_ITEMS_PRODUCTS), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_ITEMS_CATEGORIES), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_ITEMS_EXCLUSIVE), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_ITEMS_COMBO), eq(itemStack));
    }

    @Test
    public void testLoadDoneItems() {
        when(dad.getMessages().getItemFromConfig(anyString())).thenReturn(itemStack);

        editingPlayer.loadDoneItems();

        verify(inventory, times(27)).setItem(anyInt(), eq(itemStack));
    }

    @Test
    public void testLoadPlayerItems() {
        editingPlayer.savePlayerItems();
        editingPlayer.loadPlayerItems();
        verify(inventory, times(36)).setItem(anyInt(), any(ItemStack.class));
    }

    @Test
    public void testUpdateSelectionInventory() {
        ArrayList<ConfigItemMenu> items = new ArrayList<>();
        ConfigItemMenu itemMenu = mock(ConfigItemMenu.class);
        items.add(itemMenu);
        EditionComponentType type = EditionComponentType.DISPLAY_ITEM;

        editingPlayer.updateSelectionInventory(items, type);

        assertEquals(items, editingPlayer.getCurrentOptions());
        assertEquals(type, editingPlayer.getCurrentType());
    }

    @Test
    public void testUpdateSelectionInventoryWithPage() {
        ArrayList<ConfigItemMenu> items = new ArrayList<>();
        ConfigItemMenu itemMenu = mock(ConfigItemMenu.class);
        items.add(itemMenu);
        EditionComponentType type = EditionComponentType.DISPLAY_ITEM;

        editingPlayer.updateSelectionInventory(items, type);
        editingPlayer.updateSelectionInventory(1);

        assertFalse(editingPlayer.isInMainMenu());
        assertEquals(1, editingPlayer.getPage());
    }

    @Test
    public void testUpdateSelectionButtons() {
        when(dad.getMessages().getItemFromConfig(anyString())).thenReturn(itemStack);

        editingPlayer.updateSelectionButtons();

        verify(inventory, times(9)).setItem(anyInt(), eq(ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r")));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_PAGE_BACK), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_PAGE_NEXT), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_SEARCH), eq(itemStack));
        verify(inventory, times(1)).setItem(eq(EditingPlayer.BUTTON_BACK), eq(itemStack));
    }

    @Test
    public void testClearInventory() {
        editingPlayer.clearInventory(true);
        verify(inventory, times(36)).setItem(anyInt(), isNull());

        editingPlayer.clearInventory(false);
        verify(inventory, times(27)).setItem(anyInt(), isNull());
    }

    @Test
    public void testFinish() {
        editingPlayer.finish(true);
        verify(inventory, times(36)).setItem(anyInt(), isNull());
        verify(inventory, times(36)).setItem(anyInt(), any(ItemStack.class));
    }
}