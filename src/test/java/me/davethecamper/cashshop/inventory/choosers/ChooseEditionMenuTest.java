package me.davethecamper.cashshop.inventory.choosers;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChooseEditionMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private CashShop cashShop;
    private ArrayList<ConfigItemMenu> categories;
    private ChooseEditionMenu chooseEditionMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
        categories = new ArrayList<>();
        ConfigItemMenu itemMenu = mock(ConfigItemMenu.class);
        categories.add(itemMenu);
        chooseEditionMenu = new ChooseEditionMenu(uuid, itemConfig, previousMenu, categories,
                ChooseEditionMenu.CREATE_MODE);
    }

    @Test
    public void testReload() {
        doNothing().when(chooseEditionMenu).unregisterAll();
        doNothing().when(chooseEditionMenu).load();

        chooseEditionMenu.reload();

        verify(chooseEditionMenu, times(1)).unregisterAll();
        verify(chooseEditionMenu, times(1)).load();
    }

    @Test
    public void testGetNextChoosable_BackButton() {
        when(previousMenu.getPlayer()).thenReturn(uuid);
        when(previousMenu.getPreviousMenu()).thenReturn(previousMenu);

        ChoosableMenu nextMenu = chooseEditionMenu.getNextChoosable(chooseEditionMenu.getInventorySize() - 1);

        assertEquals(previousMenu, nextMenu);
    }

    @Test
    public void testGetNextChoosable_PageNext() {
        when(cashShop.getLists().getListBasedOnClass(any())).thenReturn(categories);

        ChoosableMenu nextMenu = chooseEditionMenu.getNextChoosable(chooseEditionMenu.getInventorySize() - 4);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
    }

    @Test
    public void testGetNextChoosable_PageBack() {
        when(cashShop.getLists().getListBasedOnClass(any())).thenReturn(categories);

        ChoosableMenu nextMenu = chooseEditionMenu.getNextChoosable(chooseEditionMenu.getInventorySize() - 6);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
    }

    @Test
    public void testGetFinalStep() {
        ConfigItemMenu itemMenu = mock(ConfigItemMenu.class);
        when(categories.get(anyInt()).clone()).thenReturn(itemMenu);

        ConfigItemMenu finalStep = chooseEditionMenu.getFinalStep(0);

        assertNotNull(finalStep);
        assertEquals(itemMenu, finalStep);
    }

    @Test
    public void testIsLastChoose() {
        assertTrue(chooseEditionMenu.isLastChoose(0));
    }

    @Test
    public void testIsLastChoose_Invalid() {
        assertFalse(chooseEditionMenu.isLastChoose(chooseEditionMenu.getInventorySize()));
    }

    @Test
    public void testGetMode() {
        assertEquals(ChooseEditionMenu.CREATE_MODE, chooseEditionMenu.getMode());
    }

    @Test
    public void testGetCategories() {
        assertEquals(categories, chooseEditionMenu.getCategories());
    }
}