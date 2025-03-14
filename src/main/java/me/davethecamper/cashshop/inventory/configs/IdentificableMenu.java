package me.davethecamper.cashshop.inventory.configs;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public abstract class IdentificableMenu extends ReciclableMenu {
	
	public IdentificableMenu(String identifier, ConfigManager itemConfig) {
		this.identifier = identifier;
		this.itemConfig = itemConfig;
		
		this.inv_name = "Editor";
		this.range_min = this.getInventorySize()-9;
		this.range_max = this.getInventorySize();
		
		
		load();
	}
	
	protected ConfigManager itemConfig;
	
	protected String identifier;
	private String type = "do-nothing";
	
	private boolean unsafe = false;
	
	protected HashMap<Integer, String> slots = new HashMap<>();
	private HashMap<String, Integer> identificable_to_slot = new HashMap<>();
	private HashMap<String, ItemStack> identificable_to_item = new HashMap<>();
	
	
	protected final String IDENTIFIER = "identifier";
	
	
	private void load() {
		registerItem(IDENTIFIER, 
				ItemGenerator.getItemStack(
						itemConfig.getString("items.identifier.material"),
						itemConfig.getString("items.identifier.name"),
						itemConfig.getStringAsItemLore("items.identifier.lore").replaceAll("@id", this.getId())), 13);
	}
	
	@Override
	public void reload() {
		identificable_to_item.clear();
		identificable_to_slot.clear();
		slots.clear();
		
		load();
	}
	
	public void startEditing(UUID player) {
		this.generateInventory(getInventorySize()-9, getInventorySize());
		this.setPlayer(player);
		
		Bukkit.getPluginManager().callEvent(new ChangeEditorInventoryEvent(player, this));
		
		Bukkit.getPlayer(player).openInventory(this.getInventory());
	}
	
	
	public final String getId() {
		return this.identifier;
	}

	
	protected String getDescriber() {return this.type;}
	
	public ConfigManager getMessages() {return this.itemConfig;}
	
	public void setUnsafe(boolean arg) {this.unsafe = arg;}
	
	protected void setDescriber(String describer) {this.type = describer;}
	
	
	
	protected void removeItem(String name) {
		if (identificable_to_item.containsKey(name)) {
			this.update(identificable_to_slot.get(name), null);
			identificable_to_item.remove(name);
			slots.remove(identificable_to_slot.get(name));
			identificable_to_slot.remove(name);
		}
	}
	
	protected void removeItem(int slot) {
		if (slots.containsKey(slot)) {
			String identifier = slots.get(slot);
			removeItem(identifier);
		}
	}
	
	protected void updateItemSlot(String name, int slot) {
		if (identificable_to_item.containsKey(name)) {
			ItemStack item = identificable_to_item.get(name);
			
			removeItem(name);
			
			registerItem(name, item, slot);
		}
	}
	
	protected void registerItem(String name, ItemStack item, int slot) {
		identificable_to_item.put(name, item);
		identificable_to_slot.put(name, slot);
		slots.put(slot, name);

		this.update(slot, item);
	}
	
	protected void changeIdentifierSlot(int slot) {
		changeItemSlot(IDENTIFIER, slot);
	}
	

	protected void changeItemStack(String name, ItemStack item) {
		registerItem(name, item, identificable_to_slot.get(name));
	}
	
	protected void changeItemSlot(String name, int slot) {
		slots.remove(identificable_to_slot.get(name));
		identificable_to_slot.put(name, slot);
		if (slot >= 0) slots.put(slot, name);
		
		
	}
	

	protected void createVarChanger(String var_name, WaitingForChat.Primitives type) {
		this.createVarChanger(var_name, type, true);
	}
	
	protected void createVarChanger(String var_name, WaitingForChat.Primitives type, boolean ignore_negatives) {
		new WaitingForChat(this.getPlayer(), type, var_name, this, ignore_negatives);
	}
	
	public void changerVar(String var_name, Object o) {
		changerVarHandler(var_name, o);
		Bukkit.getPlayer(this.getPlayer()).openInventory(this.getInventory());
	}
	
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case IDENTIFIER:
				File old_file = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/objects/" + getDescriber() + "/" + this.getId() + ".yml");
				String new_name = (String) o;
				File new_file = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/objects/" + getDescriber() + "/" + new_name + ".yml");
				
				if (new_file.exists()) {
					Bukkit.getPlayer(this.getPlayer()).sendMessage(itemConfig.getString("chat.error.existent"));
					return;
				}
				
				old_file.renameTo(new_file);
				
				CashShop.getInstance().update(this.getDescriber(), this.getId(), new_name);
				this.identifier = new_name;
				break;
		}
		
		reload();
		this.generateInventory();
	}
	
	

	@Override
	protected HashMap<Integer, ItemStack> getUpdatedItems() {
		HashMap<Integer, ItemStack> items = new HashMap<>();
		
		for (Integer slot : slots.keySet()) {
			items.put(slot, identificable_to_item.get(slots.get(slot)));
		}
		
		return items;
	}
	
	@Override
	public final boolean inventoryClick(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		if (this.unsafe ? true : isValidSlot(clicked_slot)) {
			this.setPlayer(uuid);
			return inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
		return false;
	}
	
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
			case IDENTIFIER:
				createVarChanger(IDENTIFIER, WaitingForChat.Primitives.STRING);
				return true;
		}
		return false;
	}
	
	@Override
	public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
		return true;
	}
	
	protected boolean isValidSlot(int clicked_slot) {
		return slots.containsKey(clicked_slot);
	}

}
