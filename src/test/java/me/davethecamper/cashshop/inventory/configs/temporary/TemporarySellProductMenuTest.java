package me.davethecamper.cashshop.inventory.configs.temporary;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TemporarySellProductMenuTest {

    private ItemMenuProperties itemProperties;
    private ProductConfig product;
    private double updatedValue;
    private Consumer<TemporarySellProductMenu> callback;
    private TemporarySellProductMenu temporarySellProductMenu;
    private CashShop cashShop;

    @BeforeEach
    public void setUp() {
        itemProperties = mock(ItemMenuProperties.class);
        product = mock(ProductConfig.class);
        updatedValue = 100.0;
        callback = mock(Consumer.class);
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
        temporarySellProductMenu = new TemporarySellProductMenu(itemProperties, product, updatedValue, callback);
    }

    @Test
    public void testConstructor() {
        assertNotNull(temporarySellProductMenu);
        assertEquals(callback, temporarySellProductMenu.callback);
        assertFalse(temporarySellProductMenu.hasIntentionToSave);
    }

    @Test
    public void testStartEditing() {
        UUID player = UUID.randomUUID();
        ReciclableMenu previousMenu = mock(ReciclableMenu.class);
        when(cashShop.getPlayerEditorCurrentInventory(player)).thenReturn(previousMenu);

        temporarySellProductMenu.startEditing(player);

        assertEquals(previousMenu, temporarySellProductMenu.getPreviousMenu());
    }

    @Test
    public void testSaveHandler() {
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);

        FileConfiguration result = temporarySellProductMenu.saveHandler(fileConfiguration);

        verify(fileConfiguration, times(1)).set("nonSaveObject", true);
        assertTrue(temporarySellProductMenu.hasIntentionToSave);
        assertEquals(fileConfiguration, result);
    }

    @Test
    public void testBackOneInventory() {
        UUID player = UUID.randomUUID();
        ReciclableMenu previousMenu = mock(ReciclableMenu.class);
        temporarySellProductMenu.setPrevious(previousMenu);

        temporarySellProductMenu.backOneInventory(player, previousMenu);

        verify(callback, times(1)).accept(temporarySellProductMenu);
    }
}