package me.davethecamper.cashshop.inventory.configs;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.ConfigManager;

public class ProductItemsMenu extends SavableMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5464335178422473193L;


	public ProductItemsMenu(String identificador, ConfigManager item_config, SellProductMenu dad) {
		super(identificador, item_config, dad);
		
		this.dad = dad;
		this.items = new ArrayList<>(dad.getProduct().getItems());
		
		load();
	}
	
	private ArrayList<ItemStack> items;
	private ArrayList<ItemStack> temp_items = new ArrayList<>();
	
	private SellProductMenu dad;

	
	private void load() {
		this.changeInventorySize(54);
		
		this.changeSaveButtonSlot(47);
		this.changeCancelButtonSlot(51);
		this.changeIdentifierSlot(-1);
		
		updateItems();
	}
	
	private void updateItems() {
		for (int i = 0; i < 45; i++) {
			this.removeItem("item" + i);
		}
		
		for (int i = 0; i < items.size(); i++) {
			this.registerItem("item" + i, items.get(i), i);
		}
	}
	
	
	public ArrayList<ItemStack> getItems() {
		return items;
	}
	
	public void updateItems(ArrayList<ItemStack> new_items) {
		items.clear();
		new_items.addAll(new_items);
	}
	
	
	
	@Override
	public void saveHandler() {
		dad.updateItems(this);
	}
	
	
	@Override
	public ProductItemsMenu clone() {
		return new ProductItemsMenu(this.getId(), item_config, dad);
	}


	@Override
	protected boolean updateBeforeBack() {return true;}
	
	
	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		if (clicked_slot < items.size()) {
			if (Bukkit.getPlayer(uuid).getInventory().firstEmpty() != -1) Bukkit.getPlayer(uuid).getInventory().addItem(items.get(clicked_slot));
			items.remove(clicked_slot);
			temp_items.remove(this.getInventory().getItem(clicked_slot));
			updateItems();
			return true;
		} else {
			switch (slots.get(clicked_slot)) {
				case SAVE_BUTTON:
					saveHandler();
					super.backOneInventory(uuid, dad);
					return true;
					
				case CANCEL_BUTTON:
					for (ItemStack item : temp_items) {
						Bukkit.getPlayer(uuid).getInventory().addItem(item);
					}
					super.backOneInventory(uuid, dad);
					return true;
					
				default:
					return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
			}
		}
	}
	
	@Override
	public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
		items.add(item.clone());
		temp_items.add(item.clone());
		
		Bukkit.getPlayer(this.getPlayer()).getInventory().setItem(clicked_slot, null);
		
		updateItems();
		return true;
	}

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {return fc;}

}
