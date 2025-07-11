package me.davethecamper.cashshop.inventory.configs;

import lombok.Getter;
import lombok.Setter;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class SellProductMenu extends ValuebleItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6784249458971882595L;

	public SellProductMenu(String identificador, ConfigManager item_config, ReciclableMenu previous, ItemMenuProperties item_properties, ProductConfig product, double updated_value, long delay) {
		super(identificador, item_config, previous, item_properties, updated_value);
		
		this.product = product;
		this.delay_buy_again = delay;
		this.setDescriber("product");
		
		load();
	}
	
	
	@Getter
    private ProductConfig product;
	
	private long delay_buy_again;
	
	@Getter
	@Setter
	private boolean money;

	@Setter
	@Getter
	private boolean allowBonus;
	
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
				itemConfig.getString("items.items_give.material"),
				itemConfig.getString("items.items_give.name"),
				itemConfig.getStringAsItemLore("items.items_give.lore")), 23);
		
		registerItem(DELAY, ItemGenerator.getItemStack(
				itemConfig.getString("items.delay.material"),
				itemConfig.getString("items.delay.name"),
				itemConfig.getStringAsItemLore("items.delay.lore").replaceAll("@delay", this.delay_buy_again + "")), 4);
		
		registerItem(COMMANDS, 
				ItemGenerator.getItemStack(
						itemConfig.getString("items.commands.material"),
						itemConfig.getString("items.commands.name"),
						itemConfig.getStringAsItemLore("items.commands.hint"), new ArrayList<>(product.getCommands())), 25);
	}
	
	private final DecimalFormat f = new DecimalFormat("#,###");
	private final DecimalFormat f2 = new DecimalFormat("#,##0.00");
	
	public ItemStack getSellingItem(CashPlayer player, int amount) {
		double discount = player != null ? CashShop.getInstance().getCupomManager().getDiscount(player.getCupom()) : 0;
		double valueCash = player != null && player.isCashTransaction() ? (this.getValueInCash() * amount) - ((this.getValueInCash() * amount) * (discount/100)) : this.getValueInCash()*amount;
		double valueCashMoney = CashShop.getInstance().getMainConfig().getInt("coin.value") * amount;

		String extraLabel = amount > 1 ? " §7(x" + amount + ")" : "";
		String discountLabel = discount > 0 ? "§d" + f.format(discount) + "% OFF " : "";
		String coinLabel = money ? "" : " ¢";

		ItemStack item = this.getItemProperties().getItem().clone();
		item = player != null ? ItemGenerator.replaces(item, player) : item;
		item = player == null || !player.isCashTransaction() ?
				ItemGenerator.addLoreAfter(item, ";=;" + itemConfig.getString("product.sell").replaceAll("@value", f.format(valueCash) + coinLabel) + extraLabel + ";=;§r", "") :
					ItemGenerator.addLoreAfter(ItemGenerator.tryReplace(ItemGenerator.tryReplace(item, "@curvalue", f.format(valueCashMoney)), "@value", f2.format(valueCash)), discountLabel + extraLabel, "");
		
		return item;
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
		return new SellProductMenu(id, itemConfig, this.previous, item_properties.clone(), product.clone(), this.getValueInCash(), delay_buy_again);
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
				new ProductItemsMenu(this.getId(), itemConfig, this).startEditing(uuid);
				return true;
				
			case COMMANDS:
				new LoreEditorMenu(this.getId(), COMMANDS, itemConfig, this, product.getCommands()).startEditing(uuid);
				return true;
				
			default:
				return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
		}
	}

	public static SellProductMenu createTemporaryProduct(String identifier, int valor, int delay, ProductConfig product, ItemStack item) {
		return new SellProductMenu(identifier, CashShop.getInstance().getMessagesConfig(), null, new ItemMenuProperties(item), product, valor, delay);
	}
}
