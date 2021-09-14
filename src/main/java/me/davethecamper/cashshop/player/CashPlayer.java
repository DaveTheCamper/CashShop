package me.davethecamper.cashshop.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
	private boolean changes = false;
	
	private String cupom = "...", gift_for = "...";
	

	private ArrayList<ConfigInteractiveMenu> previus_menus = new ArrayList<>();
	
	private ConfigInteractiveMenu current_menu;
	private ConfigInteractiveMenu current_checkout;
	
	private SellProductMenu current_product;
	
	private Inventory current_inventory;

	
	
	public int getCash() {return cash;}
	
	public int getProductAmount() {return this.product_amount;}

	public String getCupom() {return cupom;}

	public String getGiftFor() {return gift_for;}
	
	public Inventory getCurrentInventory() {return this.current_inventory;}
	
	public EditionComponent getCurrentComponent(int slot) {return this.current_menu.getVisualizableItems().get(slot);}
	
	public ConfigInteractiveMenu getCurrentInteractiveMenu() {return this.current_menu;}
	
	public TreeMap<String, TransactionInfo> getPendingTransactions() {return this.transactions_pending;}
	
	public boolean isCashTransaction() {return this.is_cash_transaction;}
	
	public boolean isOnline() {return Bukkit.getOfflinePlayer(uuid).isOnline();}
	
	public boolean hasChanges() {return this.changes;}
	
	public boolean haveAnyCurrentInventory() {return this.current_menu != null;}
	
	public boolean haveCurrentInventory() {return this.current_menu != null && previus_menus.size() > 0;}
	

	public void setCash(int cash) {changes = true; this.cash = cash;}
	
	public void setProductAmount(int amount) {this.product_amount = amount;}

	public void setCupom(String cupom) {this.cupom = cupom;}

	public void setGiftFor(String gift_for) {this.gift_for = gift_for;}
	
	public void setTransactionAsAproved(TransactionInfo ti) {
		ti.updateTransactionStatus(TransactionResponse.APPROVED);
		transactions_pending.remove(ti.getTransactionToken());
		transactions_approved.put(ti.getTransactionToken(), ti);
		this.changes = true;
	}
	

	public void addCash(int amount) {
		this.cash += amount;
		this.changes = true;
	}
	
	public void removeCash(int amount) {
		addCash(-amount);
	}
			
	
	public void updateProductAmount() {
		new WaitingForChat(uuid, WaitingForChat.Primitives.INTEGER, "set_amount", CashShop.getInstance().getMessagesConfig().getString("chat.amount"));
	}
	
	public void updateDiscount() {
		new WaitingForChat(uuid, WaitingForChat.Primitives.STRING, "set_discount", CashShop.getInstance().getMessagesConfig().getString("chat.discount"));
	}
	
	public void updateGift() {
		new WaitingForChat(uuid, WaitingForChat.Primitives.STRING, "set_gift", CashShop.getInstance().getMessagesConfig().getString("chat.gift"));
	}

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
		ArrayList<String> identifiers = new ArrayList<>();
		
		for (String name : CashShop.getInstance().getGatewaysNames()) {
			ItemStack item = CashShop.getInstance().getStaticItem(CashShop.REPLACE_GATEWAY_BUTTON).getItemProperties().getItem().clone();
			ItemMeta im = item.getItemMeta();
			im.setLore(new ArrayList<>());
			im.setDisplayName(CashShop.getInstance().getGateway(name).getColoredDisplayName());
			item.setItemMeta(im);
			
			items.add(item);
			identifiers.add(name);
		}
		
		cim.replaceIndicators(CashShop.REPLACE_GATEWAY_BUTTON, items, identifiers);
		
		updateCurrentInventory(cim);
	}
	
	private void generateTransactionItems(TreeMap<String, TransactionInfo> transactions, ArrayList<ItemStack> items, ArrayList<String> identifiers, boolean approved) {
		for (String token : transactions.keySet()) {
			TransactionInfo ti = transactions.get(token);
			ItemStack item = CashShop.getInstance().getStaticItem(CashShop.REPLACE_TRANSACTION_BUTTON).getItemProperties().getItem().clone();
			ItemMeta im = item.getItemMeta();
			
			ArrayList<String> lore = new ArrayList<>();
			List<String> list = CashShop.getInstance().getMessagesConfig().getStringList("items.transactions.info");
			String date = new java.text.SimpleDateFormat(CashShop.getInstance().getMainConfig().getString("date_format")).format(new Date(ti.getCreationDate()));
			
			for (String s : list) {
				lore.add(s.
						replaceAll("@token", ti.getTransactionToken()).
						replaceAll("@player", ti.getPlayer()).
						replaceAll("@cash", ti.getCash() + "").
						replaceAll("@date", date));
			}
			
			im.setLore(lore);
			im.setDisplayName(approved ? CashShop.getInstance().getMessagesConfig().getString("items.transactions.approved") : CashShop.getInstance().getMessagesConfig().getString("items.transactions.pending"));
			
			item.setItemMeta(im);
			
			items.add(item);
			identifiers.add(token);
		}
	}
	
	public void openTransactions() {
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.TRANSACTION_MENU).clone();

		ArrayList<ItemStack> items = new ArrayList<>();
		ArrayList<String> identifiers = new ArrayList<>();
		
		generateTransactionItems(transactions_pending, items, identifiers, false);
		generateTransactionItems(transactions_approved, items, identifiers, true);
		
		cim.replaceIndicators(CashShop.REPLACE_TRANSACTION_BUTTON, items, identifiers);
		
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
	
	public void selectGateway(int slot) {
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) current_menu;
		
		if (cim.isReplacedItem(slot)) {
			String identifier = cim.getReplacedItem(slot);
			CashShopGateway csg = CashShop.getInstance().getGateway(identifier);
			ProductInfo pi = new ProductInfo(product_amount, "Cash", CashShop.getInstance().getMainConfig().getString("currency.code"));
			TransactionInfo ti = csg.generateTransaction(pi, null);
			System.out.println(isValidNick(this.gift_for));
			ti = new TransactionInfo(isValidNick(this.gift_for) ? gift_for : Bukkit.getOfflinePlayer(uuid).getName(), csg, (int) Math.round(pi.getAmount()*CashShop.getInstance().getMainConfig().getInt("coin.value")), System.currentTimeMillis(), ti.getLink(), ti.getTransactionToken());
			
			transactions_pending.put(ti.getTransactionToken(), ti);
			this.changes = true;
			
			Bukkit.getPlayer(uuid).closeInventory();
			this.current_menu = null;
			
			sendLink(ti);
		} else {
			System.out.println("Not replaceble");
		}
	}
	
	private boolean isValidNick(String nick) {
		char chars[] = nick.toCharArray();
		if (chars.length >= 16 || chars.length == 0) return false;
		
		for (int i = 0; i < chars.length; i++) {
			if (!Character.isDigit(chars[i]) && !Character.isLetter(chars[i]) && chars[i] != '_') {
				return false;
			}
		}
		return true;
	}


	public void updateCurrentProduct() {
		updateCurrentProduct(this.current_product, this.product_amount, false);
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
	
	private void sendLink(TransactionInfo ti) {
		Player p = Bukkit.getPlayer(uuid);
		ConfigManager messages = CashShop.getInstance().getMessagesConfig();
		p.sendMessage(messages.getString("payment.info"));
		
		TextComponent link = new TextComponent(messages.getString("payment.click"));
		link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messages.getString("payment.hover")).create()));
		link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ti.getLink()));

		TextComponent all = new TextComponent("");
		String split[] = messages.getString("payment.link").split("@link");
		for (int i = 0; i < split.length; i++) {
			TextComponent info = new TextComponent(split[i]);
			
			if (i == split.length-1) {
				all.addExtra(info);
				if (split.length == 1) {
					all.addExtra(link);
				}
			} else {
				all.addExtra(info);
				all.addExtra(link);
			}
		}
		
		p.spigot().sendMessage(all);
	}
	
	
	
	
	public void save() {
		File f = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/players/" + uuid + ".yml");
		
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		fc.set("cash", this.cash);
		
		fc.set("transactions.pending", null);
		fc.set("transactions.approved", null);
		
		for (String token : transactions_pending.keySet()) {
			setTransactionToFile("transactions.pending", transactions_pending.get(token), fc);
		}
		
		for (String token : transactions_approved.keySet()) {
			setTransactionToFile("transactions.approved", transactions_approved.get(token), fc);
		}
		
		
		try {
			fc.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void load() {
		File f = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/players/" + uuid + ".yml");
		
		if (f.exists()) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			this.cash = fc.getInt("cash");
			
			if (fc.get("transactions.pending") != null) {
				for (String token : fc.getConfigurationSection("transactions.pending").getKeys(false)) {
					TransactionInfo transaction = loadTransaction("transactions.pending", token, fc);
					transactions_pending.put(token, transaction);
				}
			}

			if (fc.get("transactions.approved") != null) {
				for (String token : fc.getConfigurationSection("transactions.approved").getKeys(false)) {
					TransactionInfo transaction = loadTransaction("transactions.approved", token, fc);
					transactions_approved.put(token, transaction);
				}
			}
		}
	}
	
	private void setTransactionToFile(String path, TransactionInfo ti, FileConfiguration fc) {
		fc.set(path + "." + ti.getTransactionToken() + ".gateway", ti.getGatewayCaller());
		fc.set(path + "." + ti.getTransactionToken() + ".link", ti.getLink());
		fc.set(path + "." + ti.getTransactionToken() + ".cash", ti.getCash());
		fc.set(path + "." + ti.getTransactionToken() + ".player", ti.getPlayer());
		fc.set(path + "." + ti.getTransactionToken() + ".creation", ti.getCreationDate());
	}
	
	private TransactionInfo loadTransaction(String path, String token, FileConfiguration fc) {
		String gateway = fc.getString(path + "." + token + ".gateway");
		String link = fc.getString(path + "." + token + ".link");
		String player = fc.getString(path + "." + token + ".player");
		int cash = fc.getInt(path + "." + token + ".cash");
		long creation = fc.getLong(path + "." + token + ".creation");
		
		return new TransactionInfo(player, gateway, cash, creation, link, token);
	}
}
