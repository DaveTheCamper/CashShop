package me.davethecamper.cashshop.inventory.choosers;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.*;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CCWhatMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private CashShop cashShop;
    private CCWhatMenu ccWhatMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
        ccWhatMenu = new CCWhatMenu(uuid, itemConfig, previousMenu, ChooseEditionMenu.CREATE_MODE);
    }

    @Test
    public void testReload() {
        doNothing().when(ccWhatMenu).unregisterAll();
        doNothing().when(ccWhatMenu).load();

        ccWhatMenu.reload();

        verify(ccWhatMenu, times(1)).unregisterAll();
        verify(ccWhatMenu, times(1)).load();
    }

    @Test
    public void testGetNextChoosable_BackButton() {
        when(previousMenu.getPlayer()).thenReturn(uuid);
        when(previousMenu.getPreviousMenu()).thenReturn(previousMenu);

        ChoosableMenu nextMenu = ccWhatMenu.getNextChoosable(26);

        assertEquals(previousMenu, nextMenu);
    }

    @Test
    public void testGetFinalStep_Decorative() {
        ArrayList<ConfigItemMenu> list = new ArrayList<>();
        when(cashShop.getLists().getSortedDecorativeItems()).thenReturn(list);
        when(itemConfig.getItemFromConfig("items.choosable.create.decorative")).thenReturn(mock(ItemStack.class));

        ConfigItemMenu finalStep = ccWhatMenu.getFinalStep(10);

        assertNotNull(finalStep);
        assertTrue(finalStep instanceof ConfigItemMenu);
    }

    @Test
    public void testGetFinalStep_Product() {
        ArrayList<SellProductMenu> list = new ArrayList<>();
        when(cashShop.getLists().getSortedProducts()).thenReturn(list);
        when(itemConfig.getItemFromConfig("items.choosable.create.product")).thenReturn(mock(ItemStack.class));

        ConfigItemMenu finalStep = ccWhatMenu.getFinalStep(12);

        assertNotNull(finalStep);
        assertTrue(finalStep instanceof SellProductMenu);
    }

    @Test
    public void testGetFinalStep_Combo() {
        ArrayList<ComboItemMenu> list = new ArrayList<>();
        when(cashShop.getLists().getSortedCombos()).thenReturn(list);
        when(itemConfig.getItemFromConfig("items.choosable.create.combo")).thenReturn(mock(ItemStack.class));

        ConfigItemMenu finalStep = ccWhatMenu.getFinalStep(14);

        assertNotNull(finalStep);
        assertTrue(finalStep instanceof ComboItemMenu);
    }

    @Test
    public void testGetFinalStep_Categorie() {
        ArrayList<ConfigInteractiveMenu> list = new ArrayList<>();
        when(cashShop.getLists().getSortedCategories()).thenReturn(list);
        when(itemConfig.getItemFromConfig("items.choosable.create.categorie")).thenReturn(mock(ItemStack.class));

        ConfigItemMenu finalStep = ccWhatMenu.getFinalStep(16);

        assertNotNull(finalStep);
        assertTrue(finalStep instanceof ConfigInteractiveMenu);
    }

    @Test
    public void testIsLastChoose_Decorative() {
        assertTrue(ccWhatMenu.isLastChoose(10));
    }

    @Test
    public void testIsLastChoose_Product() {
        assertTrue(ccWhatMenu.isLastChoose(12));
    }

    @Test
    public void testIsLastChoose_Combo() {
        assertTrue(ccWhatMenu.isLastChoose(14));
    }

    @Test
    public void testIsLastChoose_Categorie() {
        assertTrue(ccWhatMenu.isLastChoose(16));
    }

    @Test
    public void testIsLastChoose_Invalid() {
        assertFalse(ccWhatMenu.isLastChoose(0));
    }
}