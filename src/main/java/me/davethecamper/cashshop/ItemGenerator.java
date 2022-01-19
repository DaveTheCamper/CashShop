package me.davethecamper.cashshop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cryptomorin.xseries.XMaterial;

import me.davethecamper.cashshop.player.CashPlayer;

public class ItemGenerator {
	
	
	private static HashMap<ItemStack, Boolean> has_to_replace = new HashMap<>();
	
	private static Set<String> replaces = new HashSet<>(Arrays.asList("@cash", "@cupom", "@other", "@currency"));
	
	
	
	public static ItemStack addLoreAfter(ItemStack item, String additional, String color) {
		String split[] = additional.split(";=;");
		ArrayList<String> lore = new ArrayList<>();
		if (split.length > 0) {
			for (int i = 0; i < split.length; i++) {
				lore.add(color + split[i]);
			}
		}
		return addLoreAfter(item, lore);
	}

	public static ItemStack addLoreAfter(ItemStack item, ArrayList<String> additional) {
		ItemStack new_item = item.clone();
		ItemMeta im = new_item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		
		if (new_item.hasItemMeta() && im.hasLore()) {
			lore.addAll(im.getLore());
		}
		lore.addAll(additional);
		
		im.setLore(lore);
		new_item.setItemMeta(im);
		
		return new_item;
	}
	

	public static ItemStack changeLore(ItemStack item, String lore) {
		return changeLore(item, lore, "");
	}

	public static ItemStack changeLore(ItemStack item, String lore, String color) {
		ArrayList<String> new_lore = new ArrayList<>();
		
		String partes[] = lore.split(";=;");
		
		for (int i = 0; i < partes.length; i++) {
			new_lore.add(color + partes[i]);
		}
		
		return changeLore(item, new_lore);
	}
	
	public static ItemStack changeLore(ItemStack item, ArrayList<String> lore) {
		ItemStack new_item = item.clone();
		ItemMeta im = new_item.getItemMeta();
		
		im.setLore(lore);
		
		new_item.setItemMeta(im);
		
		return new_item;
	}
	
	
	public static ItemStack replaces(ItemStack item, CashPlayer player) {
		if (has_to_replace.containsKey(item) && !has_to_replace.get(item)) {
			return item;
		}
		
		ItemStack clone = item.clone();
		
		boolean has_to_change = false;
		boolean has_to_change_lore = false;
		boolean has_meta = item.hasItemMeta();
		ItemMeta im = item.getItemMeta();
		
		String display = has_meta && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
		ArrayList<String> lore = has_meta && item.getItemMeta().hasLore() ? new ArrayList<>(item.getItemMeta().getLore()) : new ArrayList<>();
		
		String modified = tryReplace(display, player);
		if (modified.length() > 0) {
			has_to_change = true;
			im.setDisplayName(modified);
		}
		
		for (int i = 0; i < lore.size(); i++) {
			modified = tryReplace(lore.get(i), player);
			if (modified.length() > 0) {
				has_to_change = true;
				has_to_change_lore = true;
				lore.set(i, modified);
			}
		}
		
		if (has_to_change_lore) {
			im.setLore(lore);
		}

		has_to_replace.put(clone, has_to_change);
		
		if (has_to_change) {
			item.setItemMeta(im);
		}
		
		return item;
	}
	
	

	public static ItemStack tryReplace(ItemStack item, String old_str, String new_str) {
		ItemMeta im = item.getItemMeta();
		boolean has_meta = item.hasItemMeta();
		
		String display = has_meta && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
		ArrayList<String> lore = has_meta && item.getItemMeta().hasLore() ? new ArrayList<>(item.getItemMeta().getLore()) : new ArrayList<>();
		
		if (display.length() > 0) {
			display = display.replaceAll(old_str, new_str);
			im.setDisplayName(display);
		}
		
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, lore.get(i).replaceAll(old_str, new_str));
		}
		
		im.setLore(lore);
		
		item.setItemMeta(im);
		
		return item;
	}
	
	private static String tryReplace(String current, CashPlayer player) {
		boolean replace_something = false;
		for (String s : replaces) {
			if (current.contains(s)) {
				replace_something = true;
				switch (s) {
					case "@cash":
						current = current.replaceAll(s, player.getCash() + "");
						break;
						
					case "@cupom":
						current = current.replaceAll(s, player.getCupom().length() > 0 ? player.getCupom() : "...");
						break;
						
					case "@other":
						current = current.replaceAll(s, player.getGiftFor().length() > 0 ? player.getGiftFor() : "...");
						break;
						
					case "@currency":
						current = current.replaceAll(s, CashShop.getInstance().getMainConfig().getString("currency.code"));
						break;
				}
			}
		}
		
		return replace_something ? current : "";
	}
	

	public static ItemStack getItemStack(String material) {
		return getItemStack(material, "");
	}

	public static ItemStack getItemStack(String material, String name) {
		return getItemStack(material, name, "");
	}

	public static ItemStack getItemStack(String material, String name, String lore) {
		return getItemStack(material, name, lore, "§f");
	}

	public static ItemStack getItemStack(String material, String name, ArrayList<String> lore) {
		return getItemStack(material, name, "§f", lore);
	}

	public static ItemStack getItemStack(String material, String name, String lore, String color) {
		return getItemStack(material, name, lore, color, new ArrayList<>());
	}

	public static ItemStack getItemStack(String material, String name, String lore_aux, ArrayList<String> lore) {
		return getItemStack(material, name, lore_aux, "§f", lore);
	}
	
	public static ItemStack getItemStack(String material, String name, String lore_aux, String color, ArrayList<String> lore) {
		try {
			XMaterial.matchXMaterial(material).get().parseItem();
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("§4[ERROR] §6CashShop -> §cunknown material §4" + material + " §cdid you download the currect version?");
		}
		
		ItemStack item = XMaterial.matchXMaterial(material).get().parseItem();
		ItemMeta im = item.getItemMeta();
		
		if (name.length() > 0) im.setDisplayName(name);

		if (lore_aux.length() > 0) {
			String split[] = lore_aux.split(";=;");
			if (split.length > 0) {
				for (int i = 0; i < split.length; i++) {
					lore.add(color + split[i]);
				}
			}
		}

		if (lore.size() > 0) im.setLore(lore);
		
		item.setItemMeta(im);
		
		return item;
	}

}
