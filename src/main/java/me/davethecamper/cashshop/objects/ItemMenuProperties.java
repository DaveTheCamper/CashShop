package me.davethecamper.cashshop.objects;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMenuProperties implements Cloneable {
	
	public ItemMenuProperties(ItemStack item, String name, ArrayList<String> lore, boolean glow) {
		this.glow = glow;
		this.item = item;
		this.name = name;
		this.lore = lore;
	}
	
	
	private ArrayList<String> lore;
	
	private ItemStack item;
	
	private String name;
	
	private boolean glow;

	
	
	public ArrayList<String> getLore() {return lore;}
	
	public String getLoreAsString() {
		String s = "";
		for (String line : lore) {
			s = s + line + ";=;";
		}
		return s.substring(0, s.length()-3);
	}

	public ItemStack getItem() {return item.clone();}

	public String getName() {return name;}

	public boolean isGlow() {return glow;}
	
	
	public void setName(String arg) {
		this.name = arg;
		updateItem();
	}
	
	public void setGlow(boolean arg) {
		this.glow = arg;
		updateItem();
	}
	
	public void setLore(ArrayList<String> lore) {
		this.lore = new ArrayList<String>(lore);
		updateItem();
	}
	
	public void readItem(ItemStack item) {
		this.item = item.clone();
		
		if (item.hasItemMeta()) {
			ItemMeta im = item.getItemMeta();
			if (im.hasEnchants()) this.glow = true;
			if (im.hasLore()) this.lore = new ArrayList<>(im.getLore());
			if (im.hasDisplayName()) this.name = im.getDisplayName();
		}
		
		if (item.getEnchantments().size() > 0) this.glow = true;
		
		updateItem();
	}
	
	public void updateItem() {
		ItemStack item = this.item.clone();
		ItemMeta im = item.getItemMeta();
		
		im.setLore(lore);
		im.setDisplayName(name);
		
		if (im.hasEnchants()) {
			for (Enchantment ec : im.getEnchants().keySet()) {im.removeEnchant(ec);}
		}
		
		for (Enchantment ec : item.getEnchantments().keySet()) {item.removeEnchantment(ec);}
		
		
		if (isGlow()) {
			im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		item.setItemMeta(im);
		this.item = item;
	}
	
	
	@Override
	public ItemMenuProperties clone() {
		return new ItemMenuProperties(item.clone(), name, new ArrayList<String>(lore), glow);
	}
}
