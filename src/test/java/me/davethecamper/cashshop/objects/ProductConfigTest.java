package me.davethecamper.cashshop.objects;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ProductConfigTest {

    private ProductConfig productConfig;
    private ArrayList<ItemStack> items;
    private ArrayList<String> commands;

    @BeforeEach
    public void setUp() {
        items = new ArrayList<>();
        commands = new ArrayList<>();
        productConfig = new ProductConfig(items, commands);
    }

    @Test
    public void testConstructor() {
        assertNotNull(productConfig);
        assertEquals(items, productConfig.getItems());
        assertEquals(commands, productConfig.getCommands());
    }

    @Test
    public void testSetItems() {
        ArrayList<ItemStack> newItems = new ArrayList<>();
        productConfig.setItems(newItems);
        assertEquals(newItems, productConfig.getItems());
    }

    @Test
    public void testSetCommands() {
        ArrayList<String> newCommands = new ArrayList<>();
        productConfig.setCommands(newCommands);
        assertEquals(newCommands, productConfig.getCommands());
    }

    @Test
    public void testUpdateItems() {
        ArrayList<ItemStack> newItems = new ArrayList<>();
        productConfig.updateItems(newItems);
        assertEquals(newItems, productConfig.getItems());
    }

    @Test
    public void testClone() {
        ProductConfig clonedConfig = productConfig.clone();
        assertNotNull(clonedConfig);
        assertEquals(productConfig.getItems(), clonedConfig.getItems());
        assertEquals(productConfig.getCommands(), clonedConfig.getCommands());
    }
}