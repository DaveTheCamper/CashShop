package me.davethecamper.cashshop.api;

import java.util.ArrayList;
import java.util.TreeMap;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.configs.ComboItemMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.inventory.configs.SavableMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;

public class CashShopApi {
	
	public CashShopApi(CashShop main) {
		this.main = main;
		
		this.lists = new CashShopLists(main);
		this.categories = new CategoriesManager(main);
		this.static_menus = new CashShopStaticMenus(main);
	}
	
	private CashShop main;
	
	private CashShopLists lists;
	
	private CategoriesManager categories;
	
	private CashShopStaticMenus static_menus;
	
	
	public ConfigManager getMainConfig() {return this.main.configuration;}
	
	public CashShopLists getLists() {return this.lists;}
	
	public CategoriesManager getCategoriesManager() {return this.categories;}
	
	public CashShopStaticMenus getStaticObjects() {return this.static_menus;}
	
	
	public CashShopGateway getGateway(String name) {return main.getGateway(name);}
	
	public ArrayList<String> getGatewaysNames() {return main.getGatewaysNames();}
	
	
	
	public <Z extends ConfigItemMenu> ConfigItemMenu getConfigItemBasedOnClass(String name, Class<Z> reference) {
		switch (reference.getSimpleName()) {
			case "ConfigInteractiveMenu":
				return categories.getCategorie(name);
				
			case "SellProductMenu":
				return getProduct(name);
				
			case "ComboItemMenu":
				return getCombo(name);
				
			case "ConfigItemMenu":
				return getCosmeticItem(name);
		}
		return null;
	}
	

	public ConfigItemMenu getCosmeticItem(String name) {
		return getSomethingFromTree(name, main.do_nothing);
	}
	
	public SellProductMenu getProduct(String name) {
		return getSomethingFromTree(name, main.products);
	}
	
	public ComboItemMenu getCombo(String name) {
		return getSomethingFromTree(name, main.combos);
	}
	
	public ConfigItemMenu getStaticItem(String name) {
		return getSomethingFromTree(name, main.static_items);
	}
	
	private <Z> Z getSomethingFromTree(String name, TreeMap<String, Z> tree) {
		return tree.get(name);
	}
	
	
	@SuppressWarnings("unchecked")
	public <Z extends ConfigItemMenu> void registerObject(String type, String name, SavableMenu menu) {
		if (!(menu instanceof ConfigItemMenu)) return;
		
		TreeMap<String, Z> map = null;
		switch (type) {
			case "do-nothing":
				map = (TreeMap<String, Z>) main.do_nothing;
				break;
				
			case "category":
				map = (TreeMap<String, Z>) main.categories;
				break;
				
			case "product":
				map = (TreeMap<String, Z>) main.products;
				break;
				
			case "combo-item":
				map = (TreeMap<String, Z>) main.combos;
				break;
				
			case "static":
				map = (TreeMap<String, Z>) main.static_items;
				break;
		}
		
		
		map.put(name, (Z) menu);
		
		this.getLists().updateCache(map);
	}
	
	public <Z extends ConfigItemMenu> void unregister(String type, String name) {
		TreeMap<String, ? extends ConfigItemMenu> map = null;
		switch (type) {
			case "do-nothing":
				map = main.do_nothing;
				break;
				
			case "category":
				map = main.categories;
				break;
				
			case "product":
				map = main.products;
				break;
				
			case "combo-item":
				map = main.combos;
				break;
		}
		
		map.remove(name);
		
		this.getLists().updateCache(map);
	}
	
	@SuppressWarnings("unchecked")
	public <Z extends ConfigItemMenu> void update(String type, String old_name, String new_name) {
		TreeMap<String, Z> map = null;
		switch (type) {
			case "do-nothing":
				map = (TreeMap<String, Z>) main.do_nothing;
				break;
				
			case "category":
				map = (TreeMap<String, Z>) main.categories;
				break;
				
			case "product":
				map = (TreeMap<String, Z>) main.products;
				break;
				
			case "combo-item":
				map = (TreeMap<String, Z>) main.combos;
				break;
		}
		
		Z menu = map.get(old_name);
		map.put(new_name, menu);
		map.remove(old_name);
		
		for (ConfigItemMenu interactive_temp : getLists().getSortedCategories()) {
			boolean changes = false;
			ConfigInteractiveMenu interactive_menu = (ConfigInteractiveMenu) interactive_temp;
			
			if (interactive_menu != null && interactive_menu.getVisualizableItems() != null) {
				for (Integer slot : new ArrayList<>(interactive_menu.getVisualizableItems().keySet())) {
					EditionComponent ec = interactive_menu.getVisualizableItems().get(slot);
					if (ec.getName().equals(old_name)) {
						EditionComponent new_component = new EditionComponent(ec.getType(), new_name);
						interactive_menu.getVisualizableItems().put(slot, new_component);
						changes = true;
					}
				}
			}
			
			if (changes) {
				interactive_menu.save();
			}
		}
		
		this.getLists().updateCache(map);
	}
	
	
	

}
