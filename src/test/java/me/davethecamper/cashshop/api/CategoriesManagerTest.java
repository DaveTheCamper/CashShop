package me.davethecamper.cashshop.api;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.IdentificableMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoriesManagerTest {

    private CashShop main;
    private CategoriesManager categoriesManager;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        categoriesManager = new CategoriesManager(main);
    }

    @Test
    public void testIsMainCategorie() {
        IdentificableMenu menu = mock(IdentificableMenu.class);
        when(menu.getId()).thenReturn("main");
        when(main.LABEL_MAIN).thenReturn("main");

        assertTrue(categoriesManager.isMainCategorie(menu));
    }

    @Test
    public void testIsTransactionsCategorie() {
        IdentificableMenu menu = mock(IdentificableMenu.class);
        when(menu.getId()).thenReturn("transactions");
        when(main.LABEL_TRANSACTIONS).thenReturn("transactions");

        assertTrue(categoriesManager.isTransactionsCategorie(menu));
    }

    @Test
    public void testIsCheckoutCategorie() {
        IdentificableMenu menu = mock(IdentificableMenu.class);
        when(menu.getId()).thenReturn("checkout");
        when(main.LABEL_CHECKOUT).thenReturn("checkout");

        assertTrue(categoriesManager.isCheckoutCategorie(menu));
    }

    @Test
    public void testIsCombosCategorie() {
        IdentificableMenu menu = mock(IdentificableMenu.class);
        when(menu.getId()).thenReturn("combos");
        when(main.LABEL_COMBOS).thenReturn("combos");

        assertTrue(categoriesManager.isCombosCategorie(menu));
    }

    @Test
    public void testGetCategorie() {
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        categories.put("category1", menu);
        when(main.categories).thenReturn(categories);

        assertEquals(menu, categoriesManager.getCategorie("category1"));
    }

    @Test
    public void testGetMainCategorie() {
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        categories.put("main", menu);
        when(main.categories).thenReturn(categories);
        when(main.LABEL_MAIN).thenReturn("main");

        assertEquals(menu, categoriesManager.getMainCategorie());
    }

    @Test
    public void testGetTransactionsCategorie() {
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        categories.put("transactions", menu);
        when(main.categories).thenReturn(categories);
        when(main.LABEL_TRANSACTIONS).thenReturn("transactions");

        assertEquals(menu, categoriesManager.getTransactionsCategorie());
    }

    @Test
    public void testGetCheckoutCategorie() {
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        categories.put("checkout", menu);
        when(main.categories).thenReturn(categories);
        when(main.LABEL_CHECKOUT).thenReturn("checkout");

        assertEquals(menu, categoriesManager.getCheckoutCategorie());
    }

    @Test
    public void testGetCombosCategorie() {
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
        categories.put("combos", menu);
        when(main.categories).thenReturn(categories);
        when(main.LABEL_COMBOS).thenReturn("combos");

        assertEquals(menu, categoriesManager.getCombosCategorie());
    }
}