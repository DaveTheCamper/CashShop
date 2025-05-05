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

public class EditSomethingMenuTest {

    private UUID uuid;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private CashShop cashShop;
    private EditSomethingMenu editSomethingMenu;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
        editSomethingMenu = new EditSomethingMenu(uuid, itemConfig, previousMenu);
    }

    @Test
    public void testConstructor() {
        assertNotNull(editSomethingMenu);
        assertEquals("Editor", editSomethingMenu.inv_name);
        assertEquals(27, editSomethingMenu.getInventorySize());
    }

    @Test
    public void testReload() {
        doNothing().when(editSomethingMenu).unregisterAll();
        doNothing().when(editSomethingMenu).load();

        editSomethingMenu.reload();

        verify(editSomethingMenu, times(1)).unregisterAll();
        verify(editSomethingMenu, times(1)).load();
    }

    @Test
    public void testLoad() {
        ItemStack decorativeItem = mock(ItemStack.class);
        ItemStack productsItem = mock(ItemStack.class);
        ItemStack combosItem = mock(ItemStack.class);
        ItemStack staticItem = mock(ItemStack.class);
        ItemStack categoriesItem = mock(ItemStack.class);
        ItemStack backItem = mock(ItemStack.class);

        when(itemConfig.getItemFromConfig("items.choosable.edit_something.decorative")).thenReturn(decorativeItem);
        when(itemConfig.getItemFromConfig("items.choosable.edit_something.products")).thenReturn(productsItem);
        when(itemConfig.getItemFromConfig("items.choosable.edit_something.combos")).thenReturn(combosItem);
        when(itemConfig.getItemFromConfig("items.choosable.edit_something.static")).thenReturn(staticItem);
        when(itemConfig.getItemFromConfig("items.choosable.edit_something.categories")).thenReturn(categoriesItem);
        when(itemConfig.getItemFromConfig("items.back")).thenReturn(backItem);

        editSomethingMenu.load();

        assertEquals(decorativeItem, editSomethingMenu.getUpdatedItems().get(11));
        assertEquals(productsItem, editSomethingMenu.getUpdatedItems().get(12));
        assertEquals(combosItem, editSomethingMenu.getUpdatedItems().get(13));
        assertEquals(staticItem, editSomethingMenu.getUpdatedItems().get(14));
        assertEquals(categoriesItem, editSomethingMenu.getUpdatedItems().get(15));
        assertEquals(backItem, editSomethingMenu.getUpdatedItems().get(26));
    }

    @Test
    public void testGetNextChoosable_DecorativeButton() {
        when(cashShop.getLists().getSortedDecorativeItems()).thenReturn(new ArrayList<>());

        ChoosableMenu nextMenu = editSomethingMenu.getNextChoosable(11);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
        assertEquals(ChooseEditionMenu.EDIT_MODE, ((ChooseEditionMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_ProductsButton() {
        when(cashShop.getLists().getSortedProducts()).thenReturn(new ArrayList<>());

        ChoosableMenu nextMenu = editSomethingMenu.getNextChoosable(12);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
        assertEquals(ChooseEditionMenu.EDIT_MODE, ((ChooseEditionMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_CombosButton() {
        when(cashShop.getLists().getSortedCombos()).thenReturn(new ArrayList<>());

        ChoosableMenu nextMenu = editSomethingMenu.getNextChoosable(13);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
        assertEquals(ChooseEditionMenu.EDIT_MODE, ((ChooseEditionMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_StaticButton() {
        when(cashShop.getLists().getStaticItems()).thenReturn(new ArrayList<>());

        ChoosableMenu nextMenu = editSomethingMenu.getNextChoosable(14);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
        assertEquals(ChooseEditionMenu.EDIT_MODE, ((ChooseEditionMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_CategoriesButton() {
        when(cashShop.getLists().getSortedCategories()).thenReturn(new ArrayList<>());

        ChoosableMenu nextMenu = editSomethingMenu.getNextChoosable(15);

        assertNotNull(nextMenu);
        assertTrue(nextMenu instanceof ChooseEditionMenu);
        assertEquals(ChooseEditionMenu.EDIT_MODE, ((ChooseEditionMenu) nextMenu).getMode());
    }

    @Test
    public void testGetNextChoosable_BackButton() {
        when(previousMenu.getPlayer()).thenReturn(uuid);
        when(previousMenu.getPreviousMenu()).thenReturn(previousMenu);

        ChoosableMenu nextMenu = editSomethingMenu.getNextChoosable(26);

        assertEquals(previousMenu, nextMenu);
    }
}