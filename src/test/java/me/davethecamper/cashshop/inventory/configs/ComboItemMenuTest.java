package me.davethecamper.cashshop.inventory.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComboItemMenuTest {

    private ComboItemMenu comboItemMenu;
    private ConfigManager itemConfig;
    private ReciclableMenu previousMenu;
    private ItemMenuProperties itemProperties;
    private double updatedValue;
    private double updatedCurrency;

    @BeforeEach
    public void setUp() {
        itemConfig = mock(ConfigManager.class);
        previousMenu = mock(ReciclableMenu.class);
        itemProperties = mock(ItemMenuProperties.class);
        updatedValue = 100.0;
        updatedCurrency = 50.0;
        comboItemMenu = new ComboItemMenu("test", itemConfig, previousMenu, itemProperties, updatedValue,
                updatedCurrency);
    }

    @Test
    public void testConstructor() {
        assertNotNull(comboItemMenu);
        assertEquals(updatedCurrency, comboItemMenu.getValueCurrency());
        assertEquals("combo-item", comboItemMenu.getDescriber());
    }

    @Test
    public void testLoad() {
        comboItemMenu.load();
        // Add assertions or verifications as needed
    }

    @Test
    public void testSaveHandler() {
        FileConfiguration fileConfiguration = mock(FileConfiguration.class);

        comboItemMenu.saveHandler(fileConfiguration);

        verify(fileConfiguration, times(1)).set("currency_tag", updatedCurrency);
    }

    @Test
    public void testGetValueCurrency() {
        assertEquals(updatedCurrency, comboItemMenu.getValueCurrency());
    }

    @Test
    public void testSetValueCurrency() {
        double newCurrency = 75.0;
        comboItemMenu.setValueCurrency(newCurrency);
        assertEquals(newCurrency, comboItemMenu.getValueCurrency());
    }
}