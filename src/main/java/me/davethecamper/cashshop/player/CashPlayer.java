package me.davethecamper.cashshop.player;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;

public class CashPlayer {
	
	public CashPlayer(UUID uuid) {
		this.uuid = uuid;
		load();
	}
	
	
	private TreeMap<String, TransactionInfo> transactions_pending = new TreeMap<>();
	private TreeMap<String, TransactionInfo> transactions_approved = new TreeMap<>();
	
	private UUID uuid;
	
	private int cash = 0;
	private int product_amount = 1;
	
	private boolean is_cash_transaction = false;
	
	private String cupom = "", gift_for = "";
	

	private ArrayList<ConfigInteractiveMenu> previus_menus = new ArrayList<>();
	
	private ConfigInteractiveMenu current_menu;
	private ConfigInteractiveMenu current_checkout;
	
	private SellProductMenu current_product;
	
	private Inventory current_inventory;

	
	
	public int getCash() {return cash;}

	public String getCupom() {return cupom;}

	public String getGiftFor() {return gift_for;}
	
	public Inventory getCurrentInventory() {return this.current_inventory;}
	
	public EditionComponent getCurrentComponent(int slot) {return this.current_menu.getVisualizableItems().get(slot);}
	
	public boolean isCashTransaction() {return this.is_cash_transaction;}
	
	public boolean haveAnyCurrentInventory() {return this.current_menu != null;}
	
	public boolean haveCurrentInventory() {return this.current_menu != null && previus_menus.size() > 0;}
	

	public void setCash(int cash) {this.cash = cash;}

	public void setCupom(String cupom) {this.cupom = cupom;}

	public void setGiftFor(String gift_for) {this.gift_for = gift_for;}
	

			

	public void updateCurrentInventory(ConfigInteractiveMenu new_menu) {
		updateCurrentInventory(new_menu, true);
	}
	
	public void updateCurrentInventory(ConfigInteractiveMenu new_menu, boolean add_list) {
		if (current_menu != null && add_list) previus_menus.add(current_menu);
		this.current_menu = new_menu.clone();
		openCurrentInventory();
	}
	
	
	public void openGatewayMenu() {
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.GATEWAYS_MENU).clone();
		ArrayList<ItemStack> items = new ArrayList<>();
		
		for (String name : CashShop.getInstance().getGatewaysNames()) {
			ItemStack item = CashShop.getInstance().getStaticItem(CashShop.REPLACE_GATEWAY_BUTTON).getItemProperties().getItem().clone();
			ItemMeta im = item.getItemMeta();
			im.setLore(new ArrayList<>());
			im.setDisplayName(CashShop.getInstance().getGateway(name).getIdentifier());
			item.setItemMeta(im);
			
			items.add(item);
		}
		
		cim.replaceIndicators(CashShop.REPLACE_GATEWAY_BUTTON, items);
		
		updateCurrentInventory(cim);
	}

	public void openBuyCashMenu() {
		is_cash_transaction = true;
		
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_MENU).clone();
		cim.updateSomething(CashShop.CONFIRM_BUY_BUTTON, new EditionComponent(EditionComponentType.STATIC, CashShop.GATEWAYS_MENU));
		this.current_checkout = cim;
		
		updateCurrentInventory(cim);
		
		SellProductMenu spm = (SellProductMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_CASH_BUTTON).clone();
		
		updateCurrentProduct(spm, 1, false);
	}
	
	public void openCurrentInventory() {
		if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
			this.current_inventory = generateInventory();
			Bukkit.getPlayer(uuid).openInventory(this.current_inventory);
		}
	}
	
	
	public void updateCurrentProduct(SellProductMenu new_item) {
		is_cash_transaction = false;
		this.current_checkout = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_MENU).clone();
		updateCurrentProduct(new_item, 1, true);
	}

	private void updateCurrentProduct(SellProductMenu new_item, int amount, boolean add_list) {
		this.product_amount = amount;
		this.current_product = new_item;
		
		ConfigInteractiveMenu buy_menu = this.current_checkout.clone();
		buy_menu.updateProduct(this.current_product.getSellingItem(this, this.product_amount));
		
		updateCurrentInventory(buy_menu, add_list);
	}
	
	
	public void backInventory() {
		if (previus_menus.size() > 0) {
			this.current_menu = previus_menus.get(previus_menus.size()-1);
			previus_menus.remove(previus_menus.size()-1);
			openCurrentInventory();
		}
	}
	
	private Inventory generateInventory() {
		Inventory inv = Bukkit.createInventory(null, current_menu.getSize(), current_menu.getName());
		
		for (Integer slot : current_menu.getVisualizableItems().keySet()) {
			EditionComponent component = current_menu.getVisualizableItems().get(slot);
			ItemStack item = current_menu.generateItem(component, this);
			
			inv.setItem(slot, item);
		}
		
		return inv;
	}
	
	
	public void addProductAmount(int amount) {
		this.product_amount = this.product_amount + amount > 0 ? this.product_amount + amount : 1;
		updateCurrentProduct(this.current_product, this.product_amount, false);
	}

	public void removeProductAmount(int amount) {
		addProductAmount(-amount);
	}
	
	
	
	
	public void save() {
		
	}
	
	public void load() {
		
	}
}
