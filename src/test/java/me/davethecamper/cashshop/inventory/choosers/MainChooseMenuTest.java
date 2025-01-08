package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainChooseMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private MainChooseMenu mainChooseMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        mainChooseMenu = new MainChooseMenu(uuid, itemConfig);
    }

    @Test
    public void testConstructor() {
        assertNotNull(mainChooseMenu);
        assertEquals("Editor", mainChooseMenu.inv_name);
        assertEquals(27, mainChooseMenu.getInventorySize());
    }

    @Test
    public void testReload() {
        doNothing().when(mainChooseMenu).unregisterAll();
        doNothing().when(mainChooseMenu).load();

        mainChooseMenu.reload();

        verify(mainChooseMenu, times(1)).unregisterAll();
        verify(mainChooseMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        ItemStack createItem = mock(ItemStack.class);
        ItemStack editItem = mock(ItemStack.class);

        when(itemConfig.getItemFromConfig("items.choosable.main.create")).thenReturn(createItem);
        when(itemConfig.getItemFromConfig("items.choosable.main.edit")).thenReturn(editItem);

        mainChooseMenu.load();

        assertEquals(createItem, mainChooseMenu.getUpdatedItems().get(11));
        assertEquals(editItem, mainChooseMenu.getUpdatedItems().get(15));
    }

    @Test
    public void testGetNextChoosable_CreateButton() {
        ChoosableMenu nextMenu = mainChooseMenu.getNextChoosable(11);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof CreateNewMenu);
    }

    @Test
    public void testGetNextChoosable_EditButton() {
        ChoosableMenu nextMenu = mainChooseMenu.getNextChoosable(15);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof EditSomethingMenu);
    }

    @Test
    public void testGetFinalStep() {
        assertNull(mainChooseMenu.getFinalStep(0));
    }

    @Test
    public void testIsLastChoose() {
        assertFalse(mainChooseMenu.isLastChoose(0));
    }
}