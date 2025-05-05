package me.davethecamper.cashshop.objects;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemMenuPropertiesTest {

    private ItemStack item;
    private ItemMeta itemMeta;
    private ItemMenuProperties itemMenuProperties;

    @BeforeEach
    public void setUp() {
        item = mock(ItemStack.class);
        itemMeta = mock(ItemMeta.class);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(item.clone()).thenReturn(item);
        itemMenuProperties = new ItemMenuProperties(item);
    }

    @Test
    public void testConstructorWithItem() {
        assertNotNull(itemMenuProperties);
        assertEquals(item, itemMenuProperties.getItem());
    }

    @Test
    public void testConstructorWithMaterialNameLoreGlow() {
        String material = "DIAMOND_SWORD";
        String name = "Sword";
        ArrayList<String> lore = new ArrayList<>(Arrays.asList("A powerful sword"));
        boolean glow = true;

        itemMenuProperties = new ItemMenuProperties(material, name, lore, glow);

        assertNotNull(itemMenuProperties);
        assertEquals(name, itemMenuProperties.getName());
        assertEquals(lore, itemMenuProperties.getLore());
        assertTrue(itemMenuProperties.isGlow());
    }

    @Test
    public void testConstructorWithItemNameLoreGlow() {
        String name = "Sword";
        ArrayList<String> lore = new ArrayList<>(Arrays.asList("A powerful sword"));
        boolean glow = true;

        itemMenuProperties = new ItemMenuProperties(item, name, lore, glow);

        assertNotNull(itemMenuProperties);
        assertEquals(name, itemMenuProperties.getName());
        assertEquals(lore, itemMenuProperties.getLore());
        assertTrue(itemMenuProperties.isGlow());
    }

    @Test
    public void testSetName() {
        String newName = "New Sword";
        itemMenuProperties.setName(newName);

        assertEquals(newName, itemMenuProperties.getName());
        verify(itemMeta, times(1)).setDisplayName(newName);
        verify(item, times(1)).setItemMeta(itemMeta);
    }

    @Test
    public void testSetGlow() {
        itemMenuProperties.setGlow(true);

        assertTrue(itemMenuProperties.isGlow());
        verify(itemMeta, times(1)).addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        verify(itemMeta, times(1)).addItemFlags(ItemFlag.HIDE_ENCHANTS);
        verify(item, times(1)).setItemMeta(itemMeta);
    }

    @Test
    public void testSetLore() {
        ArrayList<String> newLore = new ArrayList<>(Arrays.asList("New Lore"));
        itemMenuProperties.setLore(newLore);

        assertEquals(newLore, itemMenuProperties.getLore());
        verify(itemMeta, times(1)).setLore(newLore);
        verify(item, times(1)).setItemMeta(itemMeta);
    }

    @Test
    public void testHideFlags() {
        itemMenuProperties.hideFlags();

        verify(itemMeta, times(1)).addItemFlags(ItemFlag.values());
        verify(item, times(1)).setItemMeta(itemMeta);
    }

    @Test
    public void testReadItem() {
        when(item.hasItemMeta()).thenReturn(true);
        when(itemMeta.hasEnchants()).thenReturn(true);
        when(itemMeta.hasLore()).thenReturn(true);
        when(itemMeta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("Lore")));
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn("Sword");

        itemMenuProperties.readItem(item);

        assertTrue(itemMenuProperties.isGlow());
        assertEquals("Sword", itemMenuProperties.getName());
        assertEquals(new ArrayList<>(Arrays.asList("Lore")), itemMenuProperties.getLore());
    }

    @Test
    public void testUpdateItem() {
        itemMenuProperties.setName("Sword");
        itemMenuProperties.setLore(new ArrayList<>(Arrays.asList("Lore")));
        itemMenuProperties.setGlow(true);

        itemMenuProperties.updateItem();

        verify(itemMeta, times(1)).setDisplayName("Sword");
        verify(itemMeta, times(1)).setLore(new ArrayList<>(Arrays.asList("Lore")));
        verify(itemMeta, times(1)).addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        verify(itemMeta, times(1)).addItemFlags(ItemFlag.HIDE_ENCHANTS);
        verify(item, times(1)).setItemMeta(itemMeta);
    }

    @Test
    public void testClone() {
        ItemMenuProperties clonedProperties = itemMenuProperties.clone();

        assertNotNull(clonedProperties);
        assertEquals(itemMenuProperties.getName(), clonedProperties.getName());
        assertEquals(itemMenuProperties.getLore(), clonedProperties.getLore());
        assertEquals(itemMenuProperties.isGlow(), clonedProperties.isGlow());
    }
}