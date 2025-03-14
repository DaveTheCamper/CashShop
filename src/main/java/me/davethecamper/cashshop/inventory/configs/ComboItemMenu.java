package me.davethecamper.cashshop.inventory.configs;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;

public class ComboItemMenu extends ValuebleItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5008341982011601850L;


	public ComboItemMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, double updated_value, double updated_currency) {
		super(identificador, item_config, previous, item_properties, updated_value);
		
		this.value_currency = updated_currency;
		this.setDescriber("combo-item");
		
		load();
	}
	
	
	private final String CURRENCY_TAG = "currency_tag";
	
	private double value_currency;
	
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	private void load() {
		changeValueItemSlot(23);
		
		updateCurrencyItem();
	}
	
	private void updateCurrencyItem() {
		this.registerItem(CURRENCY_TAG, 
				ItemGenerator.getItemStack(
						itemConfig.getString("items.currency.material"),
						itemConfig.getString("items.currency.name"),
						itemConfig.getStringAsItemLore("items.currency.lore").replaceAll("@currency_value", value_currency + "")), 25);
	}
	
	public double getCurrencyValue() {
		return this.value_currency;
	}
	
	public void setCurrencyValue(double arg) {
		this.value_currency = arg;
	}
	

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("combo.value", getCurrencyValue());
		
		return super.saveHandler(fc);
	}
	
	@Override
	public ComboItemMenu clone() {
		return this.clone(getId());
	}

	@Override
	public ComboItemMenu clone(String id) {
		return new ComboItemMenu(id, itemConfig, previous, item_properties.clone(), this.getValueInCash(), this.getCurrencyValue());
	}
	
	@Override
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case CURRENCY_TAG:
				this.value_currency = (Double) o;
				updateCurrencyItem();
				break;
				
			default:
				super.changerVarHandler(var_name, o);
				break;
		}
	}
	
	
	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
			case CURRENCY_TAG:
				this.createVarChanger(CURRENCY_TAG, WaitingForChat.Primitives.DOUBLE);
				return true;
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}

}
