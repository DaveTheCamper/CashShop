package me.davethecamper.cashshop.inventory.edition;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;

public class EditingPlayer {
	
	public EditingPlayer(UUID uuid, EditInteractiveMenu dad) {
		this.player = uuid;
		this.dad = dad;
		
		savePlayerItems();
		loadEditionTools();
		loadMainItems();
	}
	

	private static final ItemStack BLACK_WINDOW = ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r");
	
	public static final int BUTTON_SAVE = 3;
	public static final int BUTTON_CANCEL = 5;

	public static final int BUTTON_PAGE_BACK = 29;
	public static final int BUTTON_SEARCH = 31;
	public static final int BUTTON_PAGE_NEXT = 33;
	public static final int BUTTON_BACK = 35;

	public static final int BUTTON_ITEMS_NOTHING = 20;
	public static final int BUTTON_ITEMS_PRODUCTS = 21;
	public static final int BUTTON_ITEMS_CATEGORIES = 22;
	public static final int BUTTON_ITEMS_EXCLUSIVE = 23;
	public static final int BUTTON_ITEMS_COMBO = 24;
	
	
	
	
	private ItemStack[] old_items = new ItemStack[36];
	
	
	private UUID player;
	
	private EditInteractiveMenu dad;
	
	private EditionComponent current_component = null;
	
	
	private ArrayList<ConfigItemMenu> current_options;
	
	private EditionComponentType current_type;
	
	private boolean main_menu = true;
	
	private int page = 1;
	
	
	private Inventory getInventory() {return Bukkit.getPlayer(player).getInventory();}
	
	public int getPage() {return this.page;}
	
	public boolean isInMainMenu() {return main_menu;}
	
	public EditionComponent getCurrentComponent() {return this.current_component;}
	
	public void setCurrentComponent(int slot) {
		if (current_options.size() > slot) {
			ConfigItemMenu config = current_options.get(slot);
			current_component = new EditionComponent(current_type, config.getId());
			Bukkit.getPlayer(player).setItemOnCursor(config.getItemProperties().getItem());
			
			loadDoneItems();
		}
	}
	
	private void savePlayerItems() {
		if (Bukkit.getOfflinePlayer(player).isOnline()) {
			Inventory pi = getInventory();
			for (int i = 0; i < 36; i++) {
				old_items[i] = pi.getItem(i);
			}
		}
	}
	
	private void loadEditionTools() {
		Inventory inv = getInventory();
		
		for (int i = 0; i < 9; i++) {inv.setItem(i, BLACK_WINDOW);}
		
		inv.setItem(BUTTON_SAVE, dad.getMessages().getItemFromConfig("items.save"));
		inv.setItem(BUTTON_CANCEL, dad.getMessages().getItemFromConfig("items.cancel"));
	}
	
	public void loadMainItems() {
		this.main_menu = true;
		this.current_component = null;
		
		clearInventory(false);
		Bukkit.getPlayer(player).setItemOnCursor(null);
		
		Inventory inv = this.getInventory();
		
		for (int i = 9; i < 36; i++) {inv.setItem(i, BLACK_WINDOW);}
		

		inv.setItem(BUTTON_ITEMS_NOTHING, dad.getMessages().getItemFromConfig("items.edition.choose.nothing"));
		inv.setItem(BUTTON_ITEMS_PRODUCTS, dad.getMessages().getItemFromConfig("items.edition.choose.products"));
		inv.setItem(BUTTON_ITEMS_CATEGORIES, dad.getMessages().getItemFromConfig("items.edition.choose.categories"));
		inv.setItem(BUTTON_ITEMS_EXCLUSIVE, dad.getMessages().getItemFromConfig("items.edition.choose.exclusive"));
		inv.setItem(BUTTON_ITEMS_COMBO, dad.getMessages().getItemFromConfig("items.edition.choose.combo"));
	}
	
	private void loadDoneItems() {
		Inventory inv = this.getInventory();
		for (int i = 9; i < 36; i++) {inv.setItem(i, dad.getMessages().getItemFromConfig("items.edition.choose.done"));}
	}
	
	private void loadPlayerItems() {
		if (Bukkit.getOfflinePlayer(player).isOnline()) {
			Inventory pi = getInventory();
			for (int i = 0; i < 36; i++) {
				pi.setItem(i, old_items[i]);
			}
		}
	}
	
	public void updateSelectionInventory(ArrayList<? extends ConfigItemMenu> items, EditionComponentType type) {
		this.current_options = new ArrayList<>(items);
		this.current_type = type;
		updateSelectionInventory(1);
	}
	
	public void updateSelectionInventory(int page) {
		if ((page <= 0 || page > (current_options.size()/18) + (current_options.size()%18 > 0 ? 1 : 0)) && page != 1) return;
		
		this.main_menu = false;
		this.page = page;
		
		clearInventory(false);
		
		int slot = 0;
		for (int i = (this.page-1)*18; i < (this.page)*18; i++) {
			if (current_options.size() <= i) break;
			this.getInventory().setItem(slot+9, ItemGenerator.addLoreAfter(current_options.get(i).getItemProperties().getItem().clone(), ";=;" + current_options.get(i).getId(), "§7§o"));
			slot++;
		}
		
		updateSelectionButtons();
	}
	
	private void updateSelectionButtons() {
		for (int i = 27; i < 36; i++) {this.getInventory().setItem(i, BLACK_WINDOW);}

		this.getInventory().setItem(BUTTON_PAGE_BACK, dad.getMessages().getItemFromConfig("items.navigable.backward"));
		this.getInventory().setItem(BUTTON_PAGE_NEXT, dad.getMessages().getItemFromConfig("items.navigable.forward"));
		this.getInventory().setItem(BUTTON_SEARCH, dad.getMessages().getItemFromConfig("items.edition.search"));
		this.getInventory().setItem(BUTTON_BACK, dad.getMessages().getItemFromConfig("items.back"));
	}
	
	private void clearInventory(boolean all) {
		for (int i = all ? 0 : 9; i < 36; i++) {this.getInventory().setItem(i, null);}
	}
	
	
	public void finish(boolean save) {
		clearInventory(true);
		loadPlayerItems();
	}

}
