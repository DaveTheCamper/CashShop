package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateNewMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private CreateNewMenu createNewMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        createNewMenu = new CreateNewMenu(uuid, itemConfig, previousMenu);
    }

    @Test
    public void testConstructor() {
        assertNotNull(createNewMenu);
        assertEquals("Editor", createNewMenu.inv_name);
        assertEquals(27, createNewMenu.getInventorySize());
    }

    @Test
    public void testReload() {
        doNothing().when(createNewMenu).unregisterAll();
        doNothing().when(createNewMenu).load();

        createNewMenu.reload();

        verify(createNewMenu, times(1)).unregisterAll();
        verify(createNewMenu, times(1)).load();
    }

    @Test
    public void testGetNextChoosable_CreateButton() {
        ChoosableMenu nextMenu = createNewMenu.getNextChoosable(11);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof CCWhatMenu);
        assertEquals(ChooseEditionMenu.CREATE_MODE, ((CCWhatMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_CloneButton() {
        ChoosableMenu nextMenu = createNewMenu.getNextChoosable(15);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof CCWhatMenu);
        assertEquals(ChooseEditionMenu.CLONE_MODE, ((CCWhatMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_BackButton() {
        when(previousMenu.getPlayer()).thenReturn(uuid);
        when(previousMenu.getPreviousMenu()).thenReturn(previousMenu);

        ChoosableMenu nextMenu = createNewMenu.getNextChoosable(26);

        assertEquals(previousMenu, nextMenu);
    }

    @Test
    public void testLoad() {
        ItemStack creationItem = mock(ItemStack.class);
        ItemStack cloneItem = mock(ItemStack.class);
        ItemStack backItem = mock(ItemStack.class);

        when(itemConfig.getItemFromConfig("items.choosable.create.creation")).thenReturn(creationItem);
        when(itemConfig.getItemFromConfig("items.choosable.create.clone")).thenReturn(cloneItem);
        when(itemConfig.getItemFromConfig("items.back")).thenReturn(backItem);

        createNewMenu.load();

        assertEquals(creationItem, createNewMenu.getUpdatedItems().get(11));
        assertEquals(cloneItem, createNewMenu.getUpdatedItems().get(15));
        assertEquals(backItem, createNewMenu.getUpdatedItems().get(26));
    }
}