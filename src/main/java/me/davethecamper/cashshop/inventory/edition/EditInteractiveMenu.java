package me.davethecamper.cashshop.inventory.edition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SavableMenu;

public class EditInteractiveMenu extends SavableMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3293771040281946643L;
	
	public EditInteractiveMenu(String identificador, ConfigManager item_config, ConfigInteractiveMenu dad) {
		this(identificador, item_config, dad, new HashMap<>());
	}
	
	public EditInteractiveMenu(String identificador, ConfigManager item_config, ConfigInteractiveMenu dad, HashMap<Integer, EditionComponent> slots) {
		super(identificador, item_config, dad);
		
		this.slots = slots != null ? slots : new HashMap<>();
		this.dad = dad;
		this.setUnsafe(true);
		
		load();
	}
	
	private HashMap<Integer, EditionComponent> slots = new HashMap<>();
	
	private ConfigInteractiveMenu dad;
	
	private EditingPlayer current_editor;
	
	
	private void load() {
		this.changeCancelButtonSlot(-1);
		this.changeIdentifierSlot(-1);
		this.changeSaveButtonSlot(-1);
		
		this.changeInventorySize(dad.getSize());
		this.generateInventory(0, -1);
	}
	
	private void loadItems() {
		for (Integer slot : new ArrayList<>(slots.keySet())) {
			loadComponent(slots.get(slot), slot);
		}
	}
	
	private void loadComponent(EditionComponent component, int slot) {
		
		if (this.getInventorySize() <= slot) {
			slots.remove(slot);
			return;
		}
		
		try {
			ItemStack item = dad.generateItem(component);
			item = ItemGenerator.addLoreAfter(item.clone(), component.getName(), "§7§o");
			
			this.registerItem(component.getName() + slot, item, slot);
		} catch (Exception e) {
			e.printStackTrace();
			slots.remove(slot);
		}
	}
	
	public HashMap<Integer, EditionComponent> getItems() {
		return slots;
	}
	
	
	@Override
	public void startEditing(UUID player) {
		loadItems();
		this.setPlayer(player);
		this.generateInventory();
		
		this.current_editor = new EditingPlayer(player, this);
		
		Bukkit.getPluginManager().callEvent(new ChangeEditorInventoryEvent(player, this));
		
		Bukkit.getPlayer(player).openInventory(this.getInventory());
	}
	
	public void finishEdition(boolean save) {
		if (save) this.saveHandler();
		
		
		this.current_editor = null;
	}
	
	public void updateInventory() {
		this.changeInventorySize(dad.getSize());
	}
	
	public EditInteractiveMenu clone(ConfigInteractiveMenu dad) {
		HashMap<Integer, EditionComponent> components = new HashMap<>();
		
		slots.forEach((slot, value) -> components.put(slot, value.clone()));
		
		return new EditInteractiveMenu(this.getId(), itemConfig, dad, new HashMap<>(components));
	}
	
	@Override
	public boolean updateBeforeBack() {return true;}
	
	@Override
	public void saveHandler() {
		dad.updateEditor(this);
		finish();
	}
	
	private void finish() {
		current_editor.finish(true);
		current_editor = null;
		dad.startEditing(this.getPlayer());
	}
	
	
	@Override
	public boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		if (this.current_editor.getCurrentComponent() != null) {
			if (slots.containsKey(clicked_slot)) {
				slots.remove(clicked_slot);
				this.removeItem(clicked_slot);
			} else {
				slots.put(clicked_slot, this.current_editor.getCurrentComponent());
				loadComponent(this.current_editor.getCurrentComponent(), clicked_slot);
			}
		} else {
			slots.remove(clicked_slot);
			this.removeItem(clicked_slot);
		}
		return true;
	}

	@Override
	public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
		switch (clicked_slot) {
			case EditingPlayer.BUTTON_SAVE:
				finishEdition(true);
				break;
				
			case EditingPlayer.BUTTON_CANCEL:
				finish();
				break;
				
			default:
				if (clicked_slot < 9) return true;
				
				if (this.current_editor.isInMainMenu()) {
					switch (clicked_slot) {
						case EditingPlayer.BUTTON_ITEMS_NOTHING:
							current_editor.updateSelectionInventory(CashShop.getInstance().getLists().getSortedDecorativeItems(), EditionComponentType.DO_NOTHING);
							break;
							
						case EditingPlayer.BUTTON_ITEMS_PRODUCTS:
							current_editor.updateSelectionInventory(CashShop.getInstance().getLists().getSortedProducts(), EditionComponentType.BUY_PRODUCT);
							break;
							
						case EditingPlayer.BUTTON_ITEMS_CATEGORIES:
							current_editor.updateSelectionInventory(CashShop.getInstance().getLists().getSortedCategories(), EditionComponentType.CATEGORY);
							break;
							
						case EditingPlayer.BUTTON_ITEMS_EXCLUSIVE:
							current_editor.updateSelectionInventory(CashShop.getInstance().getLists().getStaticItems(), EditionComponentType.STATIC);
							break;
							
						case EditingPlayer.BUTTON_ITEMS_COMBO:
							current_editor.updateSelectionInventory(CashShop.getInstance().getLists().getSortedCombos(), EditionComponentType.COMBO);
							break;
					}
					
				} else {
					if (current_editor.getCurrentComponent() == null) {
						switch (clicked_slot) {
							case EditingPlayer.BUTTON_PAGE_BACK:
								current_editor.updateSelectionInventory(current_editor.getPage()-1);
								break;
								
							case EditingPlayer.BUTTON_PAGE_NEXT:
								current_editor.updateSelectionInventory(current_editor.getPage()+1);
								break;
								
							case EditingPlayer.BUTTON_SEARCH:
								this.createVarChanger(EditingPlayer.BUTTON_SEARCH + "", WaitingForChat.Primitives.STRING);
								break;
								
							case EditingPlayer.BUTTON_BACK:
								this.current_editor.loadMainItems();
								break;
								
							default:
								this.current_editor.setCurrentComponent(clicked_slot-9 + ((current_editor.getPage()-1)*18));
								break;
						}
					} else {
						this.current_editor.loadMainItems();
					}
					
				}
				break;
		}
		
		
		return true;
	}

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {return fc;}


}
