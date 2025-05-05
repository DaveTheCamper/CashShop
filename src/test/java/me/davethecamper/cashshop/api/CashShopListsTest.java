package me.davethecamper.cashshop.api;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.configs.ComboItemMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashShopListsTest {

    private CashShop main;
    private CashShopLists cashShopLists;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        cashShopLists = new CashShopLists(main);
    }

    @Test
    public void testGetSortedCategories() {
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        categories.put("category1", menu);
        when(main.categories).thenReturn(categories);

        ArrayList<? extends ConfigItemMenu> sortedCategories = cashShopLists.getSortedCategories();

        assertEquals(1, sortedCategories.size());
        assertEquals(menu, sortedCategories.get(0));
    }

    @Test
    public void testGetSortedProducts() {
        TreeMap<String, SellProductMenu> products = new TreeMap<>();
        SellProductMenu menu = mock(SellProductMenu.class);
        products.put("product1", menu);
        when(main.products).thenReturn(products);

        ArrayList<? extends ConfigItemMenu> sortedProducts = cashShopLists.getSortedProducts();

        assertEquals(1, sortedProducts.size());
        assertEquals(menu, sortedProducts.get(0));
    }

    @Test
    public void testGetSortedCombos() {
        TreeMap<String, ComboItemMenu> combos = new TreeMap<>();
        ComboItemMenu menu = mock(ComboItemMenu.class);
        combos.put("combo1", menu);
        when(main.combos).thenReturn(combos);

        ArrayList<? extends ConfigItemMenu> sortedCombos = cashShopLists.getSortedCombos();

        assertEquals(1, sortedCombos.size());
        assertEquals(menu, sortedCombos.get(0));
    }

    @Test
    public void testGetSortedDecorativeItems() {
        TreeMap<String, ConfigItemMenu> decorativeItems = new TreeMap<>();
        ConfigItemMenu menu = mock(ConfigItemMenu.class);
        decorativeItems.put("decorative1", menu);
        when(main.do_nothing).thenReturn(decorativeItems);

        ArrayList<? extends ConfigItemMenu> sortedDecorativeItems = cashShopLists.getSortedDecorativeItems();

        assertEquals(1, sortedDecorativeItems.size());
        assertEquals(menu, sortedDecorativeItems.get(0));
    }

    @Test
    public void testGetStaticItems() {
        TreeMap<String, ConfigItemMenu> staticItems = new TreeMap<>();
        ConfigItemMenu menu = mock(ConfigItemMenu.class);
        staticItems.put("static1", menu);
        when(main.static_items).thenReturn(staticItems);

        ArrayList<? extends ConfigItemMenu> staticItemsList = cashShopLists.getStaticItems();

        assertEquals(1, staticItemsList.size());
        assertEquals(menu, staticItemsList.get(0));
    }

    @Test
    public void testUpdateCache() {
        TreeMap<String, ConfigItemMenu> map = new TreeMap<>();
        ConfigItemMenu menu = mock(ConfigItemMenu.class);
        map.put("item1", menu);

        cashShopLists.updateCache(map);

        ArrayList<? extends ConfigItemMenu> cachedList = cashShopLists.getSortedCategories();
        assertEquals(1, cachedList.size());
        assertEquals(menu, cachedList.get(0));
    }

    @Test
    public void testGetListBasedOnClass() {
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        ConfigInteractiveMenu categoryMenu = mock(ConfigInteractiveMenu.class);
        categories.put("category1", categoryMenu);
        when(main.categories).thenReturn(categories);

        ArrayList<? extends ConfigItemMenu> list = cashShopLists.getListBasedOnClass(ConfigInteractiveMenu.class);
        assertEquals(1, list.size());
        assertEquals(categoryMenu, list.get(0));
    }
}