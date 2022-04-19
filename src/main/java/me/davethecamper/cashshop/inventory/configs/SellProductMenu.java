package me.davethecamper.cashshop.inventory.configs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import me.davethecamper.cashshop.player.CashPlayer;

public class SellProductMenu extends ValuebleItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6784249458971882595L;

	public SellProductMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, ProductConfig product, int updated_value, long delay) {
		super(identificador, item_config, previous, item_properties, updated_value);
		
		this.product = product;
		this.delay_buy_again = delay;
		this.setDescriber("product");
		
		load();
	}
	
	
	private ProductConfig product;
	
	private long delay_buy_again;
	
	
	protected final String COMMANDS = "comandos";
	protected final String ITEMS = "items_give";
	protected final String DELAY = "delay";
	
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
		
		registerItem(DELAY, ItemGenerator.getItemStack(
				item_config.getString("items.delay.material"), 
				item_config.getString("items.delay.name"), 
				item_config.getStringAsItemLore("items.delay.lore").replaceAll("@delay", this.delay_buy_again + "")), 4);
		
		registerItem(COMMANDS, 
				ItemGenerator.getItemStack(
						item_config.getString("items.commands.material"), 
						item_config.getString("items.commands.name"), 
						item_config.getStringAsItemLore("items.commands.hint"), new ArrayList<>(product.getCommands())), 25);
	}
	
	private final DecimalFormat f = new DecimalFormat("#,###");
	private final DecimalFormat f2 = new DecimalFormat("#,##0.00");
	
	public ItemStack getSellingItem(CashPlayer player, int amount) {
		double discount = player != null ? CashShop.getInstance().getCupomManager().getDiscount(player.getCupom()) : 0;
		double value_cash = player != null && player.isCashTransaction() ? (((double) this.getValueInCash()*amount)) - (((double) this.getValueInCash()*amount)*(discount/100)) : this.getValueInCash()*amount;
		double value_cash_money = CashShop.getInstance().getMainConfig().getInt("coin.value")*amount;
		ItemStack item = this.getItemProperties().getItem().clone();
		item = player != null ? ItemGenerator.replaces(item, player) : item;
		item = player == null || !player.isCashTransaction() ? 
				ItemGenerator.addLoreAfter(item, ";=;" + item_config.getString("product.sell").replaceAll("@value", f.format(value_cash)) + (amount > 1 ? " §7(x" + amount + ")" : "") + ";=;§r", "") : 
					ItemGenerator.addLoreAfter(ItemGenerator.tryReplace(ItemGenerator.tryReplace(item, "@curvalue", f.format(value_cash_money)), "@value", f2.format(value_cash)),  (discount > 0 ? "§d" + f.format(discount) + "% OFF " : "") + (amount > 1 ? "§7(x" + amount + ")" : ""), "");
		
		return item;
	}
	
	public ProductConfig getProduct() {
		return this.product;
	}
	
	public long getDelayToBuy() {
		return this.delay_buy_again;
	}
	
	public void updateItems(ProductItemsMenu new_items) {
		product.updateItems(new_items.getItems());
		this.startEditing(this.getPlayer());
	}
	

	@Override
	public FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("delay", this.delay_buy_again);
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
		return new SellProductMenu(id, item_config, this.previous, item_properties.clone(), product.clone(), this.getValueInCash(), delay_buy_again);
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
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case DELAY:
				this.delay_buy_again = (Long) o;
				break;
				
			default:
				super.changerVarHandler(var_name, o);
		}
	}
	
	@Override
	public boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		this.setPlayer(uuid);
		
		switch (slots.get(clicked_slot)) {
			case DELAY:
				createVarChanger(DELAY, WaitingForChat.Primitives.LONG, false);
				return true;
		
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

	public static SellProductMenu createTemporaryProduct(String identifier, int valor, int delay, ProductConfig product, ItemStack item) {
		return new SellProductMenu(identifier, CashShop.getInstance().getMessagesConfig(), null, new ItemMenuProperties(item), product, valor, delay);
	}
}
