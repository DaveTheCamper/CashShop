package me.davethecamper.cashshop.api;

import java.util.TreeMap;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;

public class CashShopStaticMenus {
	
	
	
	public CashShopStaticMenus(CashShop cs) {
		this.main = cs;
	}
	
	private CashShop main;
	
	
	public TreeMap<String, ConfigItemMenu> getStaticItems() {
		return main.static_items;
	}
	
	public boolean isStaticObject(String name) {
		return main.static_labels.contains(name);
	}
	
	

	
	
}
