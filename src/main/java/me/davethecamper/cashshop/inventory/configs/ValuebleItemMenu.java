package me.davethecamper.cashshop.inventory.configs;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;

public class ValuebleItemMenu extends ConfigItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7086558541634491119L;


	public ValuebleItemMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, double updated_value) {
		super(identificador, item_config, previous, item_properties);

		this.value = updated_value;
		this.setDescriber("valueable");
		
		load();
	}
	
	private double value;
	
	protected final String VALUE_TAG = "value_tag";
	

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("value", this.getValueInCash());
		
		return super.saveHandler(fc);
	}
	
	@Override
	public void reload() {
		changeIdentifierSlot(6);
		
		super.reload();
		load();
	}
	
	private void load() {
		changeIdentifierSlot(6);
		updateValueItem();
	}
	
	private void updateValueItem() {
		registerItem(VALUE_TAG, 
				ItemGenerator.getItemStack(
						item_config.getString("items.valuable.material"), 
						item_config.getString("items.valuable.name"), 
						item_config.getStringAsItemLore("items.valuable.lore").replaceAll("@value_cash", value + "")), 24);
	}
	
	protected void changeValueItemSlot(int slot) {
		changeItemSlot(VALUE_TAG, slot);
	}

	
	public double getValueInCash() {
		return value;
	}
	
	public void setValueInCash(double arg) {
		if (arg > 0) {
			this.value = arg;
		}
	}
	
	@Override
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case VALUE_TAG:
				this.value = (Double) o;
				this.reload();
				break;
				
			default:
				super.changerVarHandler(var_name, o);
		}
	}

	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
			case VALUE_TAG:
				this.createVarChanger(VALUE_TAG, WaitingForChat.Primitives.DOUBLE);
				return true;
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}

}
