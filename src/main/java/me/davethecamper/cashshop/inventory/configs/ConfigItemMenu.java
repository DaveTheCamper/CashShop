package me.davethecamper.cashshop.inventory.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;

public class ConfigItemMenu extends DeletebleMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2288926684864115390L;

	public ConfigItemMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties) {
		super(identificador, item_config, previous);
		this.item_properties = item_properties;
		
		load();
	}
	
	protected ItemMenuProperties item_properties;
	
	protected final String ITEM = "item";
	protected final String CHANGE_NAME = "change_name";
	protected final String GLOW = "glow";
	protected final String HIDE_FLAGS = "flags";
	protected final String ADD_LORE = "add_lore";
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	private void load() {
		this.changeIdentifierSlot(15);
		
		
		updateItem();
		updateName();
		updateGlow();
		updateLore();
		updateFlags();
	}
	
	private void updateName() {
		this.registerItem(CHANGE_NAME, 
				ItemGenerator.getItemStack(
						item_config.getString("items.item_properties.name.material"), 
						item_config.getString("items.item_properties.name.name"), 
						item_config.getStringAsItemLore("items.item_properties.name.lore").replaceAll("@name", item_properties.getName() + "")), 19);
	}
	
	private void updateGlow() {
		this.registerItem(GLOW, 
				ItemGenerator.getItemStack(
						item_config.getString("items.item_properties.glow.material"), 
						item_config.getString("items.item_properties.glow.name"), 
						item_config.getStringAsItemLore("items.item_properties.glow.lore").replaceAll("@glow_status", item_properties.isGlow() + "")), 20);
	}
	
	private void updateLore() {
		this.registerItem(ADD_LORE, 
				ItemGenerator.getItemStack(
						item_config.getString("items.item_properties.lore.material"), 
						item_config.getString("items.item_properties.lore.name"), 
						(item_properties.getLore() != null && item_properties.getLore().size() > 0 ? item_properties.getLoreAsString() : "") + (item_properties.getLore() != null && item_properties.getLore().size() > 0 ? ";=;" : "") + item_config.getStringAsItemLore("items.item_properties.lore.lore")), 21);
	}
	
	private void updateFlags() {
		this.registerItem(HIDE_FLAGS, 
				ItemGenerator.getItemStack(
						item_config.getString("items.item_properties.flags.material"), 
						item_config.getString("items.item_properties.flags.name"), 
						item_config.getStringAsItemLore("items.item_properties.flags.lore")), 18);
	}
	
	public void changeLore(ArrayList<String> lore, String what) {
		switch (what) {
			case ADD_LORE:
				item_properties.setLore(lore);
				updateLore();
				updateItem();
				break;
		}
	}
	
	private void updateItem() {
		this.registerItem(ITEM, item_properties.getItem(), 2);
	}
	
	public ItemMenuProperties getItemProperties() {
		return this.item_properties;
	}

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("type", this.getDescriber());
		fc.set("item.name", item_properties.getName());
		fc.set("item.glow", item_properties.isGlow());
		fc.set("item.lore", item_properties.getLore());
		fc.set("item.item", item_properties.getItem());
		
		return fc;
	}

	@Override
	public void delete() {
	    String main_path = Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath();
	    File f = new File(String.valueOf(main_path) + "/objects/" + this.getDescriber() + "/" + getId() + ".yml");
	    
	    f.delete();
	    
	    CashShop.getInstance().unregister(this.getDescriber(), getId());
	    this.backOneInventory(getPlayer(), this.getPreviousMenu().getPreviousMenu());
	}
	

	@Override
	public ConfigItemMenu clone() {
		return this.clone(getId());
	}
	
	public ConfigItemMenu clone(String id) {
		return new ConfigItemMenu(id, item_config, this.previous, item_properties.clone());
	}

	@Override
	protected boolean updateBeforeBack() {return true;}
	
	
	@Override
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case CHANGE_NAME:
				item_properties.setName((String) o);
				updateName();
				updateItem();
				break;
				
			default:
				super.changerVarHandler(var_name, o);
		}
	}
	
	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
			case CHANGE_NAME:
				this.createVarChanger(CHANGE_NAME, WaitingForChat.Primitives.STRING);
				return true;
				
			case GLOW:
				item_properties.setGlow(!item_properties.isGlow());
				updateGlow();
				updateItem();
				return true;
				
			case HIDE_FLAGS:
				item_properties.hideFlags();
				updateItem();
				return true;

			case ADD_LORE:
				new LoreEditorMenu(this.getId(), ADD_LORE, item_config, this).startEditing(uuid);
				return true;
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}
	
	@Override
	public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) return true;
		
		getItemProperties().readItem(item);
		
		updateItem();
		updateName();
		updateGlow();
		updateLore();
		updateFlags();
		
		return true;
	}

	@Override
	protected void saveHandler() {}

	
	
}
