package me.davethecamper.cashshop.inventory.configs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import me.davethecamper.cashshop.player.CashPlayer;

public class SellProductMenu extends ValuebleItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6784249458971882595L;

	public SellProductMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, ProductConfig product, int updated_value) {
		super(identificador, item_config, previous, item_properties, updated_value);
		
		this.product = product;
		this.setDescriber("product");
		
		load();
	}
	
	
	private ProductConfig product;
	
	
	protected final String COMMANDS = "comandos";
	protected final String ITEMS = "items_give";
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	private void load() {
		registerItem(ITEMS, ItemGenerator.getItemStack(
				item_config.getString("items.items_give.material"), 
				item_config.getString("items.items_give.name"), 
				item_config.getStringAsItemLore("items.items_give.lore")), 23);
		
		registerItem(COMMANDS, 
				ItemGenerator.getItemStack(
						item_config.getString("items.commands.material"), 
						item_config.getString("items.commands.name"), 
						item_config.getStringAsItemLore("items.commands.hint"), new ArrayList<>(product.getCommands())), 25);
	}
	
	private final DecimalFormat f = new DecimalFormat("#,###");
	
	public ItemStack getSellingItem(CashPlayer player, int amount) {
		Bukkit.getConsoleSender().sendMessage("AAAAA");
		ItemStack item = this.getItemProperties().getItem().clone();
		item = player != null ? ItemGenerator.replaces(item, player) : item;
		item = !player.isCashTransaction() ? 
				ItemGenerator.addLoreAfter(item, ";=;" + item_config.getString("product.sell").replaceAll("@value", f.format(this.getValueInCash()*amount)) + (amount > 1 ? " §7(x" + amount + ")" : "") + ";=;§r", "") : 
					ItemGenerator.addLoreAfter(ItemGenerator.tryReplace(ItemGenerator.tryReplace(item, "@curvalue", f.format(CashShop.getInstance().getMainConfig().getInt("coin.value")*amount)), "@value", f.format(this.getValueInCash()*amount)),  (amount > 1 ? "§7(x" + amount + ")" : ""), "");
		
		return item;
	}
	
	public ProductConfig getProduct() {
		return this.product;
	}
	
	public void updateItems(ProductItemsMenu new_items) {
		product.updateItems(new_items.getItems());
		this.startEditing(this.getPlayer());
	}
	

	@Override
	public FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("selling.items", product.getItems());
		fc.set("selling.commands", product.getCommands());
		
		return super.saveHandler(fc);
	}
	
	@Override
	public SellProductMenu clone() {
		return this.clone(getId());
	}
	
	@Override
	public SellProductMenu clone(String id) {
		return new SellProductMenu(id, item_config, this.previous, item_properties.clone(), product.clone(), this.getValueInCash());
	}
	
	@Override
	public void changeLore(ArrayList<String> lore, String what) {
		switch (what) {
			case COMMANDS:
				this.getProduct().setCommands(new ArrayList<>(lore));
				break;
				
			default:
				super.changeLore(lore, what);
				break;
		}
	}
	
	
	
	@Override
	public boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		this.setPlayer(uuid);
		
		switch (slots.get(clicked_slot)) {
			case ITEMS:
				new ProductItemsMenu(this.getId(), item_config, this).startEditing(uuid);
				return true;
				
			case COMMANDS:
				new LoreEditorMenu(this.getId(), COMMANDS, item_config, this, product.getCommands()).startEditing(uuid);
				return true;
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}

}
