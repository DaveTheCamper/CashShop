package me.davethecamper.cashshop.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.CupomManager;
import me.davethecamper.cashshop.TransactionsManager;
import me.davethecamper.cashshop.inventory.configs.ComboItemMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.inventory.configs.SavableMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.player.CashPlayer;
import net.milkbowl.vault.economy.Economy;

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
	
	
	public TransactionsManager getTransactionsManager() {return this.main.transactions;}
	
	public ConfigManager getMainConfig() {return this.main.configuration;}
	
	public ConfigManager getMessagesConfig() {return this.main.messages;}
	
	public CashShopLists getLists() {return this.lists;}
	
	public CategoriesManager getCategoriesManager() {return this.categories;}
	
	public CashShopStaticMenus getStaticObjects() {return this.static_menus;}
	
	public CupomManager getCupomManager() {return main.getCupomManager();}
	
	public CashPlayer getCashPlayer(UUID uuid) {return main.getNormalPlayerInventory(uuid);}
	
	public Economy getEconomy() {return main.getEconomy();}
	
	
	public CashShopGateway getGateway(String name) {return main.getGateway(name);}
	
	public ArrayList<String> getGatewaysNames() {return main.getGatewaysNames();}
	
	
	
	public <Z extends ConfigItemMenu> ConfigItemMenu getConfigItemBasedOnClass(String name, Class<Z> reference) {
		switch (reference.getSimpleName()) {
			case "ConfigInteractiveMenu":
				return categories.getCategorie(name) != null ? categories.getCategorie(name).clone() : null;
				
			case "SellProductMenu":
				return getProduct(name) != null ? getProduct(name).clone() : null;
				
			case "ComboItemMenu":
				return getCombo(name) != null ? getCombo(name).clone() : null;
				
			case "ConfigItemMenu":
				return getCosmeticItem(name) != null ? getCosmeticItem(name).clone() : null;
		}
		return null;
	}
	

	public ConfigItemMenu getCosmeticItem(String name) {
		return getSomethingFromTree(name, main.do_nothing) != null ? getSomethingFromTree(name, main.do_nothing).clone() : null;
	}
	
	public SellProductMenu getProduct(String name) {
		return getSomethingFromTree(name, main.products) != null ? getSomethingFromTree(name, main.products).clone() : null;
	}
	
	public ComboItemMenu getCombo(String name) {
		return getSomethingFromTree(name, main.combos) != null ? getSomethingFromTree(name, main.combos).clone() : null;
	}
	
	public ConfigItemMenu getStaticItem(String name) {
		return getSomethingFromTree(name, main.static_items) != null ? getSomethingFromTree(name, main.static_items).clone() : null;
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
	
	public <Z extends ConfigItemMenu> void save(String name, InputStream stream) {
		try (Reader reader = new InputStreamReader(stream, "UTF-8")) {
			Z z = main.load(YamlConfiguration.loadConfiguration(reader), name, false);
		
			z.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<Integer, Double> money_spent = new HashMap<>();
	
	public double getTotalMoneySpent(int year, int month) {
		int valor = year*12 + month;
		
		if (money_spent.containsKey(valor)) {
			return money_spent.get(valor);
		}
		
		double total = 0;
		File files = new File(main.getDataFolder() + "/players/");
		
		for (File f : files.listFiles()) {
			if (!f.getName().endsWith(".yml")) continue;
			UUID uuid = UUID.fromString(f.getName().replaceAll(".yml", ""));
			total += getCashPlayer(uuid).getAmountSpent(year, month);
		}
		
		money_spent.put(valor, total);
		
		return total;
	}
	

	public CashPlayer getTopCash() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		return getTopCash(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
	}
	
	public CashPlayer getTopCash(int year, int month) {
		double highest = Double.MIN_VALUE;
		CashPlayer top = null;
		File files = new File(main.getDataFolder() + "/players/");
		
		for (File f : files.listFiles()) {
			if (!f.getName().endsWith(".yml")) continue;
			UUID uuid = UUID.fromString(f.getName().replaceAll(".yml", ""));
			CashPlayer cp = getCashPlayer(uuid);
			double spent = cp.getAmountSpent(year, month);
			if (spent > highest) {
				top = cp;
				highest = spent;
			}
		}
		
		return top;
	}
	
	public ArrayList<CashPlayer> getCashRanking(int year, int month) {
		TreeMap<Double, CashPlayer> tree = new TreeMap<>();
		ArrayList<CashPlayer> players = new ArrayList<>();
		File files = new File(main.getDataFolder() + "/players/");
		
		for (File f : files.listFiles()) {
			if (!f.getName().endsWith(".yml")) continue;
			UUID uuid = UUID.fromString(f.getName().replaceAll(".yml", ""));
			CashPlayer cp = getCashPlayer(uuid);
			double spent = cp.getAmountSpent(year, month);
			
			if (spent < 1) continue;
			
			while (tree.get(spent) != null) {
				spent += 0.001;
			}
			
			tree.put(spent, cp);
		}
		
		for (Double value : tree.keySet()) {
			players.add(tree.get(value));
		}
		
		return players;
	}
	
	public void disableCommandUsage(String command) {
		//this.main.getCommand(command).setExecutor(null);
	}
	

}
