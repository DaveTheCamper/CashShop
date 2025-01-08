package me.davethecamper.cashshop;

import com.cryptomorin.xseries.XMaterial;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemGeneratorTest {

    private ItemStack item;
    private ItemMeta itemMeta;

    @BeforeEach
    public void setUp() {
        item = mock(ItemStack.class);
        itemMeta = mock(ItemMeta.class);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(item.clone()).thenReturn(item);
    }

    @Test
    public void testAddLoreAfter() {
        ArrayList<String> additionalLore = new ArrayList<>(Arrays.asList("Lore1", "Lore2"));
        when(itemMeta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("ExistingLore")));

        ItemStack result = ItemGenerator.addLoreAfter(item, additionalLore);

        verify(itemMeta, times(1)).setLore(Arrays.asList("ExistingLore", "Lore1", "Lore2"));
        verify(item, times(1)).setItemMeta(itemMeta);
        assertEquals(item, result);
    }

    @Test
    public void testChangeDisplayName() {
        String newName = "NewName";

        ItemStack result = ItemGenerator.changeDisplayName(item, newName);

        verify(itemMeta, times(1)).setDisplayName(newName);
        verify(item, times(1)).setItemMeta(itemMeta);
        assertEquals(item, result);
    }

    @Test
    public void testChangeLore() {
        ArrayList<String> newLore = new ArrayList<>(Arrays.asList("Lore1", "Lore2"));

        ItemStack result = ItemGenerator.changeLore(item, newLore);

        verify(itemMeta, times(1)).setLore(newLore);
        verify(item, times(1)).setItemMeta(itemMeta);
        assertEquals(item, result);
    }

    @Test
    public void testReplaces() {
        String[] args = { "@cash", "100", "@cupom", "DISCOUNT" };
        when(itemMeta.getDisplayName()).thenReturn("Item @cash");
        when(itemMeta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("Lore @cupom")));

        ItemStack result = ItemGenerator.replaces(item, args);

        verify(itemMeta, times(1)).setDisplayName("Item 100");
        verify(itemMeta, times(1)).setLore(Arrays.asList("Lore DISCOUNT"));
        verify(item, times(1)).setItemMeta(itemMeta);
        assertEquals(item, result);
    }

    @Test
    public void testReplacesWithPlayer() {
        CashPlayer player = mock(CashPlayer.class);
        when(player.getCash()).thenReturn(100);
        when(player.getCashBonus()).thenReturn(50);
        when(player.getCupom()).thenReturn("DISCOUNT");
        when(player.getGiftFor()).thenReturn("Friend");
        when(itemMeta.getDisplayName()).thenReturn("Item @cash");
        when(itemMeta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("Lore @cupom")));

        ItemStack result = ItemGenerator.replaces(item, player);

        verify(itemMeta, times(1)).setDisplayName("Item 100 (50 bonus)");
        verify(itemMeta, times(1)).setLore(Arrays.asList("Lore DISCOUNT"));
        verify(item, times(1)).setItemMeta(itemMeta);
        assertEquals(item, result);
    }

    @Test
    public void testTryReplace() {
        String oldStr = "@cash";
        String newStr = "100";
        when(itemMeta.getDisplayName()).thenReturn("Item @cash");
        when(itemMeta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("Lore @cash")));

        ItemStack result = ItemGenerator.tryReplace(item, oldStr, newStr);

        verify(itemMeta, times(1)).setDisplayName("Item 100");
        verify(itemMeta, times(1)).setLore(Arrays.asList("Lore 100"));
        verify(item, times(1)).setItemMeta(itemMeta);
        assertEquals(item, result);
    }

    @Test
    public void testGetItemStack() {
        String material = "DIAMOND_SWORD";
        String name = "Sword";
        String lore = "A powerful sword";
        ArrayList<String> loreList = new ArrayList<>(Arrays.asList("A powerful sword"));
        boolean glow = true;

        ItemStack itemStack = ItemGenerator.getItemStack(material, name, lore, "§f", loreList, glow);

        assertNotNull(itemStack);
        assertEquals(name, itemStack.getItemMeta().getDisplayName());
        assertEquals(loreList, itemStack.getItemMeta().getLore());
        assertTrue(itemStack.getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE));
        assertTrue(itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
    }
}