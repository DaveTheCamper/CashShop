package me.davethecamper.cashshop.inventory.configs;

import lombok.Getter;
import lombok.Setter;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.CashMenuInventoryClickEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.edition.EditInteractiveMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class ConfigInteractiveMenu extends ConfigItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8970182968484470269L;

	public ConfigInteractiveMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, int size, String name) {
		this(identificador, item_config, previous, item_properties, size, name, null, new HashMap<>());
	}

	public ConfigInteractiveMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, HashMap<Integer, EditionComponent> slots, int size, String name) {
		super(identificador, item_config, previous, item_properties);
		
		this.size = size;
		this.name = name;
		this.edition = new EditInteractiveMenu(name, item_config, this, slots);
		this.setDescriber("category");
		
		load();
	}

	public ConfigInteractiveMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, int size, String name, EditInteractiveMenu edition, HashMap<Integer, String> replaces) {
		super(identificador, item_config, previous, item_properties);
		
		this.size = size;
		this.name = name;
		this.edition = edition == null ? new EditInteractiveMenu(name, item_config, this) : edition;
		this.replaces = replaces;
		this.setDescriber("category");
		
		load();
	}
	
	protected final String EDIT_BUTTON = "edit_button";
	protected final String INV_SIZE_BUTTON = "inv_size_button";
	protected final String TITLE_BUTTON = "title_button";
	
	private EditInteractiveMenu edition;
	
	@Setter
    @Getter
    private int size;
	
	@Setter
    @Getter
    private String name;
	
	private Inventory inventory_log;
	
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	private void load() {
		this.changeIdentifierSlot(6);

		this.registerItem(EDIT_BUTTON, itemConfig.getItemFromConfig("items.category.edit"), 23);
		
		updateSizeButton();
		updateTitleButton();
	}
	
	@Getter
	private HashMap<Integer, String> replaces = new HashMap<>();

	public int getTotalReplacersByRegex(String name) {
		int total = 0;
		
		HashMap<Integer, EditionComponent> map = CashShop.getInstance().getCategoriesManager().getCategorie(this.getId()).getVisualizableItems();
		for (Integer slot : map.keySet()) {
			if (map.get(slot).getName().contains(name)) {
				total++;
			}
		}
		
		return total;
	}

	public int getTotalReplacers(String name) {
		int total = 0;
		
		HashMap<Integer, EditionComponent> map = CashShop.getInstance().getCategoriesManager().getCategorie(this.getId()).getVisualizableItems();
		for (Integer slot : map.keySet()) {
			if (map.get(slot).getName().equals(name)) {
				total++;
			}
		}
		
		return total;
	}

	public List<Integer> getSlotsWithName(String name) {
		ArrayList<Integer> slots = new ArrayList<>();

		for (Integer slot : new ArrayList<>(this.getVisualizableItems().keySet())) {
			if (this.getVisualizableItems().get(slot).getName().equals(name)) {
				slots.add(slot);
			}
		}

		return slots;
	}
	
	public List<Integer> replaceIndicators(String name, ItemStack item, String identifiers) {
		ArrayList<Integer> slots = new ArrayList<>();
		for (Integer slot : new ArrayList<>(this.getVisualizableItems().keySet())) {
			if (this.getVisualizableItems().get(slot).getName().equals(name)) {
				slots.add(slot);
			}
		}
		
		ArrayList<ItemStack> items_t = new ArrayList<>();
		ArrayList<String> identifiers_t = new ArrayList<>();
		
		for (int i = 0; i < slots.size(); i++) {
			items_t.add(item);
			identifiers_t.add(identifiers);
		}
		
		return replaceIndicators(name, items_t, identifiers_t);
	}
	
	public List<Integer> replaceIndicators(String name, ArrayList<ItemStack> list, ArrayList<String> identifiers) {
		ArrayList<Integer> slots = new ArrayList<>();
		for (Integer slot : new ArrayList<>(this.getVisualizableItems().keySet())) {
			if (this.getVisualizableItems().get(slot).getName().equals(name)) {
				slots.add(slot);
			}
		}
		
		Collections.sort(slots);
		
		int count = 0;
		for (Integer slot : slots) {
			if (count < list.size()) {
				this.getVisualizableItems().put(slot, new EditionComponent(EditionComponentType.DISPLAY_ITEM, name, list.get(count)));
				replaces.put(slot, identifiers.get(count));
				count++;
			} else {
				this.getVisualizableItems().put(slot, new EditionComponent(EditionComponentType.DISPLAY_ITEM, "null", ItemGenerator.getItemStack("AIR")));
			}
		}
		
		return slots;
	}

	public boolean isReplacedItem(int slot) {
		return replaces.containsKey(slot);
	}
	
	
	public String getReplacedItem(int slot) {
		return replaces.get(slot);
	}
	
	public ArrayList<EditionComponent> getComponentsByName(String name) {
		ArrayList<EditionComponent> components = new ArrayList<>();
		
		for (Integer slot : new ArrayList<>(this.getVisualizableItems().keySet())) {
			if (this.getVisualizableItems().get(slot).getName().equals(name)) {
				components.add(this.getVisualizableItems().get(slot));
			}
		}
		return components;
	}
	
	public EditionComponent getComponentBySlot(int slot) {
		return this.getVisualizableItems().get(slot);
	}
	
	
	public void updateEntirelyInventory(ConfigInteractiveMenu i) {
		this.replaces = new HashMap<>(i.getReplaces());
		
		this.getVisualizableItems().clear();
		this.getVisualizableItems().putAll(i.getVisualizableItems());
	}
	
	public void updateConsumerByName(Consumer<CashMenuInventoryClickEvent> consumer, String name) {
		for (EditionComponent ec : this.getComponentsByName(name)) {
			ec.setConsumer(consumer);
		}
		
		for (Integer i : replaces.keySet()) {
			if (Objects.nonNull(replaces.get(i)) && replaces.get(i).equals(name)) {
				this.getComponentBySlot(i).setConsumer(consumer);
			}
		}
	}

	public void updateProduct(ItemStack item) {
		updateSomething(CashShop.REPLACE_ITEM_SELLING_BUTTON, new EditionComponent(EditionComponentType.DISPLAY_ITEM, CashShop.REPLACE_ITEM_SELLING_BUTTON, item));
	}
	
	public void updateSomething(String name, EditionComponent new_component) {
		for (Integer slot : new ArrayList<>(this.getVisualizableItems().keySet())) {
			if (this.getVisualizableItems().get(slot).getName().equals(name)) {
				this.getVisualizableItems().put(slot, new_component);
			}
		}
	}
	
	private void updateSizeButton() {
		this.registerItem(INV_SIZE_BUTTON, ItemGenerator.getItemStack(
				itemConfig.getString("items.category.size.material"),
				itemConfig.getString("items.category.size.name"),
				itemConfig.getStringAsItemLore("items.category.size.lore").replaceAll("@size", size + "")), 24);
	}
	
	private void updateTitleButton() {
		this.registerItem(TITLE_BUTTON, ItemGenerator.getItemStack(
				itemConfig.getString("items.category.title.material"),
				itemConfig.getString("items.category.title.name"),
				itemConfig.getStringAsItemLore("items.category.title.lore").replaceAll("@title", name)), 25);
	}

	
	public void updateEditor(EditInteractiveMenu edition) { this.edition = edition;}


    public HashMap<Integer, EditionComponent> getVisualizableItems() {return edition.getItems();}
	
	public Inventory getLogInventory() {
		if (this.inventory_log == null) {
			generateLogInventory(null);
		}
		return this.inventory_log;
	}


    public void setLogInventory(Inventory inv) {this.inventory_log = inv;}
	
	
	public void openLogInventory(Player p) {
		CashPlayer cp = CashShop.getInstance().getCashPlayer(p.getUniqueId());
		cp.updateCurrentInventory(this, false, false);
		cp.openLogFromCurrentInventory();
	}
	
	private void generateLogInventory(CashPlayer cp) {
		Inventory inv = Bukkit.createInventory(null, this.getSize(), this.getName());
		
		for (Integer slot : this.getVisualizableItems().keySet()) {
			EditionComponent component = this.getVisualizableItems().get(slot);
			ItemStack item = this.generateItem(component, cp);
			
			inv.setItem(slot, item);
		}
		
		this.inventory_log = inv;
	}
	

	
	public ItemStack generateItem(EditionComponent component) {
		return generateItem(component, null);
	}
	
	public ItemStack generateItem(EditionComponent component, CashPlayer player) {
		ItemStack item = component.getItemStack();
		if (item == null) {
			switch (component.getType()) {
				case BUY_PRODUCT:
					if (CashShop.getInstance().getProduct(component.getName()) != null) 
						item = CashShop.getInstance().getProduct(component.getName()).getSellingItem(player, 1);
					break;
					
				case CATEGORY:
					item = CashShop.getInstance().getCategoriesManager().getCategorie(component.getName()).getItemProperties().getItem();
					break;
					
				case COMBO:
					item = CashShop.getInstance().getCombo(component.getName()).getItemProperties().getItem();
					break;
					
				case DO_NOTHING:
					item = CashShop.getInstance().getCosmeticItem(component.getName()).getItemProperties().getItem();
					break;
					
				case STATIC:
					item = CashShop.getInstance().getStaticItem(component.getName()).getItemProperties().getItem();
					break;
					
				case DISPLAY_ITEM:
					item = component.getItemStack();
					break;
					
				default:
					return null;
			}
		}
		
		if (player != null && item != null) {
			item = ItemGenerator.replaces(item.clone(), player);
		}
		
		return item;
	}

	
	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("inventory.name", getName());
		fc.set("inventory.size", getSize());
		
		fc.set("inventory.items", null);
		
		for (Integer slot : edition.getItems().keySet()) {
			EditionComponent ec = edition.getItems().get(slot);
			
			fc.set("inventory.items.slot." + slot + ".name", ec.getName());
			fc.set("inventory.items.slot." + slot + ".type", ec.getType().toString());
		}
		
		
		return super.saveHandler(fc);
	}
	
	@Override
	public ConfigInteractiveMenu clone() {
		return this.clone(this.getId());
	}

	@Override
	public ConfigInteractiveMenu clone(String id) {
		ConfigInteractiveMenu cim = new ConfigInteractiveMenu(id, itemConfig, this.previous, item_properties.clone(), size, name, null, new HashMap<>(replaces));
		cim.updateEditor(edition.clone(cim));
		return cim;
	}
	
	@Override
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case INV_SIZE_BUTTON:
				this.size = (Integer) o;
				updateSizeButton();
				break;
				
			case TITLE_BUTTON:
				this.name = (String) o;
				updateTitleButton();
				break;
				
			default:
				super.changerVarHandler(var_name, o);
		}
	}
	
	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		switch (slots.get(clicked_slot)) {
				case EDIT_BUTTON:
					new EditInteractiveMenu(name, itemConfig, this, edition != null ? new HashMap<>(edition.getItems()) : null).startEditing(uuid);
					return true;
					
				case INV_SIZE_BUTTON:
					this.createVarChanger(INV_SIZE_BUTTON, WaitingForChat.Primitives.INTEGER);
					return true;
					
				case TITLE_BUTTON:
					this.createVarChanger(TITLE_BUTTON, WaitingForChat.Primitives.STRING);
					return true;
					
				default:
					return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}
	
	
}
