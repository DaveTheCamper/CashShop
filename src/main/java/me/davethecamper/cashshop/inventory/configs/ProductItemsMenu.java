package me.davethecamper.cashshop.inventory.configs;

import lombok.Getter;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.configs.temporary.TemporarySellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class ProductItemsMenu extends SavableMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5464335178422473193L;


	public ProductItemsMenu(String identificador, ConfigManager item_config, SellProductMenu dad) {
		this(identificador, item_config, dad, null);
	}

	public ProductItemsMenu(String identificador, ConfigManager item_config, SellProductMenu dad, Consumer<ProductItemsMenu> consumer) {
		super(identificador, item_config, dad);
		
		this.dad = dad;
		this.items = new ArrayList<>(dad.getProduct().getItems());
		this.consumer = consumer;
		
		load();
	}
	
	private ArrayList<ItemStack> items;
	private ArrayList<ItemStack> temp_items = new ArrayList<>();
	
	private SellProductMenu dad;

	@Getter
	private boolean intentionToSave;

	private Consumer<ProductItemsMenu> consumer;

	
	private void load() {
		this.changeInventorySize(54);
		
		this.changeSaveButtonSlot(47);
		this.changeCancelButtonSlot(51);
		this.changeIdentifierSlot(-1);
		
		updateItems();
	}
	
	private void updateItems() {
		for (int i = 0; i < 45; i++) {
			this.removeItem("item" + i);
		}
		
		for (int i = 0; i < items.size(); i++) {
			this.registerItem("item" + i, items.get(i), i);
		}
	}
	
	
	public ArrayList<ItemStack> getItems() {
		return items;
	}
	
	public void updateItems(ArrayList<ItemStack> new_items) {
		items.clear();
		new_items.addAll(new_items);
	}
	

	public void startEditing(UUID player, boolean viewOnly) {
		if (viewOnly) {
			this.generateInventory(getInventorySize()-9, getInventorySize());
			ConfigInteractiveMenu menu = new ConfigInteractiveMenu("temporary_display_product_items", this.getMessages(), null, new ItemMenuProperties(ItemGenerator.getItemStack("STONE")), getDisplayComponents(), this.getInventorySize(), "§8Itens");
			
			CashShop.getInstance().getCashPlayer(player).updateCurrentInventory(menu);
		} else {
			this.startEditing(player);
		}
	}
	
	private HashMap<Integer, EditionComponent> getDisplayComponents() {
		HashMap<Integer, EditionComponent> map = new HashMap<>();
		
		for (int i = 0; i < this.getInventorySize(); i++) {
			if (this.getInventory().getItem(i) != null && !this.getInventory().getItem(i).getType().equals(Material.AIR)) {
				map.put(i, new EditionComponent(EditionComponentType.DO_NOTHING, "display_product_item", this.getInventory().getItem(i)));
			}
		}
		
		map.put(47, new EditionComponent(EditionComponentType.DO_NOTHING, "display_product_item", ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r")));
		map.put(51, new EditionComponent(EditionComponentType.DO_NOTHING, "display_product_item", ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r")));
		
		map.put(49, new EditionComponent(EditionComponentType.STATIC, "back_button", CashShop.getInstance().getStaticItem("back_button").getItemProperties().getItem().clone()));
		
		return map;
	}

	@Override
	public void saveHandler() {
		dad.updateItems(this);
	}
	
	
	@Override
	public ProductItemsMenu clone() {
		return new ProductItemsMenu(this.getId(), item_config, dad);
	}


	@Override
	protected boolean updateBeforeBack() {return true;}
	
	
	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		if (clicked_slot < items.size()) {
			if (Bukkit.getPlayer(uuid).getInventory().firstEmpty() != -1) Bukkit.getPlayer(uuid).getInventory().addItem(items.get(clicked_slot));
			items.remove(clicked_slot);
			temp_items.remove(this.getInventory().getItem(clicked_slot));
			updateItems();
			return true;
		} else {
			switch (slots.get(clicked_slot)) {
				case SAVE_BUTTON:
					finishEditing(uuid, true);
					return true;
					
				case CANCEL_BUTTON:
					finishEditing(uuid, false);
					return true;
					
				default:
					return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
			}
		}
	}

	protected void finishEditing(UUID uuid, boolean save) {
		this.intentionToSave = save;

		if (Objects.nonNull(consumer)) {
			consumer.accept(this);
			return;
		}

		if (save) {
			saveHandler();
			super.backOneInventory(uuid, dad);
			return;
		}

		for (ItemStack item : temp_items) {
			Bukkit.getPlayer(uuid).getInventory().addItem(item);
		}
		super.backOneInventory(uuid, dad);
	}
	
	@Override
	public boolean inventoryPlayerClickHandler(int clicked_slot, ItemStack item) {
		items.add(item.clone());
		temp_items.add(item.clone());
		
		Bukkit.getPlayer(this.getPlayer()).getInventory().setItem(clicked_slot, null);
		
		updateItems();
		return true;
	}

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {return fc;}


	public static ProductItemsMenu createTemporaryProductItems(TemporarySellProductMenu temporaryMenu, Consumer<ProductItemsMenu> consumer) {
		return new ProductItemsMenu(UUID.randomUUID().toString(), CashShop.getInstance().getMessagesConfig(), temporaryMenu, consumer);
	}

}
