package me.davethecamper.cashshop.inventory.choosers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChoosableMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private ChoosableMenu choosableMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        choosableMenu = new ChoosableMenu(uuid, itemConfig, previousMenu) {
            @Override
            public ChoosableMenu getNextChoosable(int choose) {
                return null;
            }

            @Override
            public ConfigItemMenu getFinalStep(int choose) {
                return null;
            }

            @Override
            public boolean isLastChoose(int choose) {
                return false;
            }
        };
    }

    @Test
    public void testConstructor() {
        assertNotNull(choosableMenu);
        assertEquals("Editor", choosableMenu.inv_name);
        assertEquals(27, choosableMenu.getInventorySize());
    }

    @Test
    public void testRegisterButton() {
        ItemStack item = mock(ItemStack.class);
        choosableMenu.registerButton(1, item);

        HashMap<Integer, ItemStack> items = choosableMenu.getUpdatedItems();
        assertEquals(1, items.size());
        assertEquals(item, items.get(1));
    }

    @Test
    public void testUnregisterAll() {
        ItemStack item = mock(ItemStack.class);
        choosableMenu.registerButton(1, item);
        choosableMenu.unregisterAll();

        HashMap<Integer, ItemStack> items = choosableMenu.getUpdatedItems();
        assertTrue(items.isEmpty());
    }

    @Test
    public void testInventoryClick() {
        boolean result = choosableMenu.inventoryClick(uuid, 1, 0, InventoryAction.PICKUP_ALL);
        assertTrue(result);
    }

    @Test
    public void testInventoryPlayerClickHandler() {
        ItemStack item = mock(ItemStack.class);
        boolean result = choosableMenu.inventoryPlayerClickHandler(1, item);
        assertTrue(result);
    }
}