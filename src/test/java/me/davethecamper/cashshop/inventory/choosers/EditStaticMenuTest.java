package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EditStaticMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private CashShop cashShop;
    private EditStaticMenu editStaticMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
        editStaticMenu = new EditStaticMenu(uuid, itemConfig, previousMenu);
    }

    @Test
    public void testConstructor() {
        assertNotNull(editStaticMenu);
        assertEquals("Editor", editStaticMenu.inv_name);
        assertEquals(27, editStaticMenu.getInventorySize());
    }

    @Test
    public void testReload() {
        doNothing().when(editStaticMenu).unregisterAll();
        doNothing().when(editStaticMenu).load();

        editStaticMenu.reload();

        verify(editStaticMenu, times(1)).unregisterAll();
        verify(editStaticMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        ItemStack backItem = mock(ItemStack.class);

        when(itemConfig.getItemFromConfig("items.back")).thenReturn(backItem);

        editStaticMenu.load();

        assertEquals(backItem, editStaticMenu.getUpdatedItems().get(26));
    }

    @Test
    public void testGetNextChoosable_BackButton() {
        when(previousMenu.getPlayer()).thenReturn(uuid);
        when(previousMenu.getPreviousMenu()).thenReturn(previousMenu);

        ChoosableMenu nextMenu = editStaticMenu.getNextChoosable(26);

        assertEquals(previousMenu, nextMenu);
    }

    @Test
    public void testGetFinalStep_TransactionsButton() {
        ConfigInteractiveMenu transactionsMenu = mock(ConfigInteractiveMenu.class);
        when(cashShop.getCategoriesManager().getTransactionsCategorie()).thenReturn(transactionsMenu);
        when(transactionsMenu.clone()).thenReturn(transactionsMenu);

        ConfigInteractiveMenu finalStep = editStaticMenu.getFinalStep(11);

        assertNotNull(finalStep);
        assertEquals(transactionsMenu, finalStep);
    }

    @Test
    public void testGetFinalStep_CombosButton() {
        ConfigInteractiveMenu combosMenu = mock(ConfigInteractiveMenu.class);
        when(cashShop.getCategoriesManager().getCombosCategorie()).thenReturn(combosMenu);
        when(combosMenu.clone()).thenReturn(combosMenu);

        ConfigInteractiveMenu finalStep = editStaticMenu.getFinalStep(13);

        assertNotNull(finalStep);
        assertEquals(combosMenu, finalStep);
    }

    @Test
    public void testGetFinalStep_SellingButton() {
        ConfigInteractiveMenu sellingMenu = mock(ConfigInteractiveMenu.class);
        when(cashShop.getCategoriesManager().getCheckoutCategorie()).thenReturn(sellingMenu);
        when(sellingMenu.clone()).thenReturn(sellingMenu);

        ConfigInteractiveMenu finalStep = editStaticMenu.getFinalStep(15);

        assertNotNull(finalStep);
        assertEquals(sellingMenu, finalStep);
    }

    @Test
    public void testIsLastChoose_TransactionsButton() {
        assertTrue(editStaticMenu.isLastChoose(11));
    }

    @Test
    public void testIsLastChoose_CombosButton() {
        assertTrue(editStaticMenu.isLastChoose(13));
    }

    @Test
    public void testIsLastChoose_SellingButton() {
        assertTrue(editStaticMenu.isLastChoose(15));
    }

    @Test
    public void testIsLastChoose_InvalidButton() {
        assertFalse(editStaticMenu.isLastChoose(0));
    }
}