package me.davethecamper.cashshop.inventory.configs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryAction;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;

public abstract class SavableMenu extends IdentificableMenu implements Cloneable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2374929748798889027L;

	public SavableMenu(String identificador, ConfigManager item_config, ReciclableMenu previous) {
		super(identificador, item_config);
		
		this.previous = previous;
		
		load();
	}
	
	
	protected final String SAVE_BUTTON = "save_button";
	protected final String CANCEL_BUTTON = "cancel_button";
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	private void load() {
		this.registerItem(SAVE_BUTTON, item_config.getItemFromConfig("items.save"), 29);
		
		this.registerItem(CANCEL_BUTTON, item_config.getItemFromConfig("items.cancel"), 33);
	}
	
	
	public final void save() {
		String path = CashShop.getInstance().getStaticObjects().isStaticObject(this.getId()) ? "static" : this.getDescriber();
		File f = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/objects/" + path + "/" + this.getId() + ".yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		fc = saveHandler(fc);
		
		if (!fc.isSet("nonSaveObject")) {
			try {
				fc.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			CashShop.getInstance().registerObject(path, this.getId(), this);
		}
	}
	
	protected abstract FileConfiguration saveHandler(FileConfiguration fc);
	
	protected abstract void saveHandler();
	

	
	protected void changeSaveButtonSlot(int slot) {
		this.changeItemSlot(SAVE_BUTTON, slot);
	}
	
	protected void changeCancelButtonSlot(int slot) {
		this.changeItemSlot(CANCEL_BUTTON, slot);
	}
	
	
	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
			case SAVE_BUTTON:
				save();
				super.backOneInventory(uuid);
				return true;
				
			case CANCEL_BUTTON:
				super.backOneInventory(uuid);
				return true;
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}
	

}
