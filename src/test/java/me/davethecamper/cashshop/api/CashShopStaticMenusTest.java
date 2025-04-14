package me.davethecamper.cashshop.api;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.TreeMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashShopStaticMenusTest {

    private CashShop main;
    private CashShopStaticMenus staticMenus;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        staticMenus = new CashShopStaticMenus(main);
    }

    @Test
    public void testGetStaticItems() {
        TreeMap<String, ConfigItemMenu> staticItems = new TreeMap<>();
        ConfigItemMenu menu = mock(ConfigItemMenu.class);
        staticItems.put("static1", menu);
        when(main.static_items).thenReturn(staticItems);

        TreeMap<String, ConfigItemMenu> result = staticMenus.getStaticItems();

        assertEquals(staticItems, result);
        assertEquals(1, result.size());
        assertEquals(menu, result.get("static1"));
    }

    @Test
    public void testIsStaticObject() {
        HashSet<String> staticLabels = new HashSet<>();
        staticLabels.add("static1");
        when(main.static_labels).thenReturn(staticLabels);

        assertTrue(staticMenus.isStaticObject("static1"));
        assertFalse(staticMenus.isStaticObject("nonexistent"));
    }
}