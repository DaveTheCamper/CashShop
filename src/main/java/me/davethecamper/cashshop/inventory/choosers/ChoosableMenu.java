package me.davethecamper.cashshop.inventory.choosers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;

public abstract class ChoosableMenu extends ReciclableMenu {
	
	public ChoosableMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus) {
		super(previus);
		this.item_config = item_config;
		
		this.inv_name = "Editor";
		
		this.changeInventorySize(27);
	}
	
	
	private HashMap<Integer, ItemStack> items = new HashMap<>();
	
	protected ConfigManager item_config;

	
	
	
	public abstract ChoosableMenu getNextChoosable(int choose);
	
	public abstract ConfigItemMenu getFinalStep(int choose);
	
	@Override
	protected HashMap<Integer, ItemStack> getUpdatedItems() {return items;}
	
	public abstract boolean isLastChoose(int choose);
	
	
	
	protected void unregisterAll() {items.clear();}
	
	protected void registerButton(int slot, ItemStack item) {
		items.put(slot, item);
	}
	
	@Override
	public boolean inventoryClick(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {return true;}

	@Override
	public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {return true;}
	
}
