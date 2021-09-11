package me.davethecamper.cashshop.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;

public class CashShopLists {
	
	public CashShopLists(CashShop main) {
		this.main = main;
	}
	
	private CashShop main;
	
	private HashMap<Integer, ArrayList<? extends ConfigItemMenu>> sorts_cache = new HashMap<>();
	

	public ArrayList<? extends ConfigItemMenu> getSortedCategories() {
		return getSortedList(main.categories);
	}
	
	public ArrayList<? extends ConfigItemMenu> getSortedProducts() {
		return getSortedList(main.products);
	}
	
	public ArrayList<? extends ConfigItemMenu> getSortedCombos() {
		return getSortedList(main.combos);
	}
	
	public ArrayList<? extends ConfigItemMenu> getSortedDecorativeItems() {
		return getSortedList(main.do_nothing);
	}
	
	public ArrayList<? extends ConfigItemMenu> getStaticItems() {
		return getSortedList(main.static_items);
	}
	
	
	private <Z extends ConfigItemMenu> ArrayList<? extends ConfigItemMenu> getSortedList(TreeMap<String, Z> map) {
		if (sorts_cache.get(map.hashCode()) == null) {
			updateCache(map);
		}
		return sorts_cache.get(map.hashCode());
	}
	
	public <Z extends ConfigItemMenu> void updateCache(TreeMap<String, Z> map) {
		ArrayList<Z> list = new ArrayList<>();
		for (String s : map.keySet()) {
			list.add(map.get(s));
		}
		sorts_cache.put(map.hashCode(), list);
	}
	
	public <Z extends ConfigItemMenu> ArrayList<? extends ConfigItemMenu> getListBasedOnClass(Class<Z> reference) {
		switch (reference.getSimpleName()) {
			case "ConfigInteractiveMenu":
				return getSortedCategories();
				
			case "SellProductMenu":
				return getSortedProducts();
				
			case "ComboItemMenu":
				return getSortedProducts();
				
			case "ConfigItemMenu":
				return getSortedDecorativeItems();
		}
		return null;
	}

}
