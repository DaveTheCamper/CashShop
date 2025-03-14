package me.davethecamper.cashshop.inventory.configs;

import java.util.UUID;

import org.bukkit.event.inventory.InventoryAction;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;

public abstract class DeletebleMenu extends SavableMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6310789262758558566L;

	public DeletebleMenu(String identificador, ConfigManager item_config, ReciclableMenu previous) {
		super(identificador, item_config, previous);
		
		load();
	}
	
	protected final String DELETE_BUTTON = "delete";
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	private void load() {
		this.registerItem(DELETE_BUTTON, 
				ItemGenerator.getItemStack(
						itemConfig.getString("items.delete.material"),
						itemConfig.getString("items.delete.name"),
						itemConfig.getStringAsItemLore("items.delete.lore")), this.getInventorySize()-1);
		
		if (CashShop.getInstance().getStaticObjects().isStaticObject(this.getId())) {
			this.changeItemSlot(DELETE_BUTTON, -1);
		}
	}
	
	@Override
	protected void changeItemSlot(String name, int slot) {
		if (name.equals(DELETE_BUTTON)) {
			if (!CashShop.getInstance().getStaticObjects().isStaticObject(this.getId())) {
				super.changeItemSlot(name, slot);
			} else {
				super.changeItemSlot(name, -1);
			}
		} else {
			super.changeItemSlot(name, slot);
		}
	}
	
	public abstract void delete();
	

	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
			case DELETE_BUTTON:
				switch (slot_button) {
					case 8:
						if (!CashShop.getInstance().getStaticObjects().isStaticObject(this.getId())) delete();
						return true;
				}
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}

}
