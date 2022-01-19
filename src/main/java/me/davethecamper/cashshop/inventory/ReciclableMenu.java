package me.davethecamper.cashshop.inventory;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;

public abstract class ReciclableMenu {
	
	public ReciclableMenu() {
		this(null);
	}
	
	public ReciclableMenu(ReciclableMenu previous) {
		this.previous = previous;
	}
	

	protected ReciclableMenu previous = null;
	
	private Inventory inventory = null;
	
	protected String inv_name;
	
	private UUID uuid;
	
	protected int inventory_size = 36, range_min = -2, range_max = -2;
	

	public final Inventory getInventory() {
		if (this.inventory == null) generateInventory();
		return this.inventory;
	}
	
	public ReciclableMenu getPreviousMenu() {return previous;}
	
	protected abstract HashMap<Integer, ItemStack> getUpdatedItems();
	
	protected int getInventorySize() {return this.inventory_size;}
	
	public final UUID getPlayer() {return this.uuid;}
	
	
	public void setPlayer(UUID uuid) {this.uuid = uuid;}
	
	public void setPrevious(ReciclableMenu rm) {this.previous = rm;}
	
	
	public void generateInventory() {
		generateInventory(range_min, range_max);
	}
	
	public void generateInventory(int range_min, int range_max) {
		if (this.range_max == -2 && this.range_min == -2) {
			range_max = this.getInventorySize();
			range_min = 0;
		}
		
		Inventory inv = Bukkit.createInventory(null, inventory_size, inv_name);
		ItemStack black = ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r");
		this.inventory = inv;
		this.range_max = range_max;
		this.range_min = range_min;
		
		for (int i = range_min; i < range_max; i++) {
			inv.setItem(i, black);
		}
		
		
		HashMap<Integer, ItemStack> items = getUpdatedItems();
		
		for (Integer slot : items.keySet()) {
			update(slot, items.get(slot));
		}
		
	}
	
	protected void disposeInventory() {
		this.inventory = null;
	}
	

	protected void backOneInventory(UUID player) {
		backOneInventory(player, getPreviousMenu());
	}
	
	protected void backOneInventory(UUID player, ReciclableMenu menu) {
		if (Bukkit.getOfflinePlayer(player).isOnline()) {
			if (menu.updateBeforeBack()) {
				menu.reload();
				menu.generateInventory();
			}
			
			Bukkit.getPluginManager().callEvent(new ChangeEditorInventoryEvent(player, menu));
			
			
			Bukkit.getPlayer(player).openInventory(menu.getInventory());
		}
	}
	
	protected void changeInventorySize(int size) {
		this.inventory_size = size;
	}
	
	public void update(int slot, ItemStack item) {
		if (inventory != null) {
			inventory.setItem(slot, item);
		}
	}
	
	protected abstract boolean updateBeforeBack();
	
	public abstract boolean inventoryClick(UUID uuid, int clicked_slot, int slot_button, InventoryAction type);
	
	public abstract boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item);
	
	public abstract void reload();
	
	
	
}
