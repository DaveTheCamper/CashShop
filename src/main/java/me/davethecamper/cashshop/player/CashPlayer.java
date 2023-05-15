package me.davethecamper.cashshop.player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.events.BuyCashItemEvent;
import me.davethecamper.cashshop.events.PreOpenCashInventoryEvent;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.ProductConfig;
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
	
	private HashMap<String, Long> last_buy_time = new HashMap<>();
	
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
	
	public UUID getUniqueId() {return this.uuid;}

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
	
	public boolean haveCurrentInventoryFromMain() {return this.current_menu != null && previus_menus.size() > 0 && previus_menus.get(0).getId().equals("main");}
	

	public void setCash(int cash) {changes = true; this.cash = cash;}
	
	public void setProductAmount(int amount) {this.product_amount = amount;}

	public void setCupom(String cupom) {this.cupom = cupom;}

	public void setGiftFor(String gift_for) {this.gift_for = gift_for;}
	
	public void setTransactionAsAproved(TransactionInfo ti) {
		ti.setApproved();
		
		transactions_pending.remove(ti.getTransactionToken());
		transactions_approved.put(ti.getTransactionToken(), ti);
		this.changes = true;
		CashShop.getInstance().getCupomManager().addTransaction(ti.getCupom(), ti.getTransactionToken(), ti.getCash());
	}
	
	public void cancelTransaction(TransactionInfo ti) {
		transactions_pending.remove(ti.getTransactionToken());
		this.changes = true;
	}
	

	public void addCash(long amount) {
		this.cash += amount;
		this.changes = true;
	}
	
	public void removeCash(long amount) {
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
	
	public void reloadCurrentMenu() {
		this.updateCurrentInventory(CashShop.getInstance().getCategoriesManager().getCategorie(this.current_menu.getId()));
		
		if (previus_menus.size() > 0) {previus_menus.remove(previus_menus.size()-1);}
	}

	public void updateCurrentInventory(ConfigInteractiveMenu new_menu) {
		updateCurrentInventory(new_menu, true);
	}

	public void updateCurrentInventory(ConfigInteractiveMenu new_menu, boolean add_list) {
		updateCurrentInventory(new_menu, add_list, true);
	}
	
	public void updateCurrentInventory(ConfigInteractiveMenu new_menu, boolean add_list, boolean open_inventory) {
		if (current_menu != null && add_list && add_list) {
			previus_menus.add(current_menu);
		} else {
			previus_menus.clear();
		}
		
		this.current_menu = new_menu;
		if (open_inventory) openCurrentInventory();
	}
	
	
	public void openGatewayMenu() {
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.GATEWAYS_MENU);
		
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
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.TRANSACTION_MENU);

		ArrayList<ItemStack> items = new ArrayList<>();
		ArrayList<String> identifiers = new ArrayList<>();
		
		generateTransactionItems(transactions_pending, items, identifiers, false);
		generateTransactionItems(transactions_approved, items, identifiers, true);
		
		cim.replaceIndicators(CashShop.REPLACE_TRANSACTION_BUTTON, items, identifiers);
		
		updateCurrentInventory(cim);
	}

	public void openBuyCashMenu() {
		is_cash_transaction = true;
		
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_MENU);
		cim.updateSomething(CashShop.CONFIRM_BUY_BUTTON, new EditionComponent(EditionComponentType.STATIC, CashShop.GATEWAYS_MENU));
		this.current_checkout = cim;
		
		updateCurrentInventory(cim);
		
		SellProductMenu spm = (SellProductMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_CASH_BUTTON);
		
		updateCurrentProduct(spm, 1, false);
	}
	
	public void openCurrentInventory() {
		if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
			Bukkit.getPluginManager().callEvent(new PreOpenCashInventoryEvent(uuid, this.current_menu));
			this.current_inventory = generateInventory();
			Bukkit.getPlayer(uuid).openInventory(this.current_inventory);
		}
	}

	public void openLogFromCurrentInventory() {
		if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
			if (this.current_menu.getLogInventory() == null) {
				this.current_menu.setLogInventory(generateInventory());
			}
			this.current_inventory = this.current_menu.getLogInventory();
			Bukkit.getPlayer(uuid).openInventory(this.current_inventory);
		}
	}
	
	public void selectGateway(int slot) {
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) current_menu;
		
		if (cim.isReplacedItem(slot)) {
			String identifier = cim.getReplacedItem(slot);
			CashShopGateway csg = CashShop.getInstance().getGateway(identifier);
			double total_in_money = ((double) product_amount) - (((double) product_amount)*(CashShop.getInstance().getCupomManager().getDiscount(getCupom())/100));
			ProductInfo pi = new ProductInfo(total_in_money, "Cash", CashShop.getInstance().getMainConfig().getString("currency.code"));
			TransactionInfo ti = csg.generateTransaction(pi, null);
			System.out.println(isValidNick(this.gift_for));
			ti = new TransactionInfo(isValidNick(this.gift_for) ? gift_for : Bukkit.getOfflinePlayer(uuid).getName(), csg, this.cupom, (int) Math.round(product_amount*CashShop.getInstance().getMainConfig().getInt("coin.value")), total_in_money, System.currentTimeMillis(), ti.getLink(), ti.getTransactionToken());
			
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
		if (this.current_product != null) {
			updateCurrentProduct(this.current_product, this.product_amount, false);
		} else {
			ConfigInteractiveMenu menu = this.getCurrentInteractiveMenu();
			if (this.previus_menus.size() > 0) this.previus_menus.remove(this.previus_menus.size()-1);
			
			this.updateCurrentInventory(CashShop.getInstance().getCategoriesManager().getCategorie(menu.getId()));
		}
		
	}
	
	public void updateCurrentProduct(SellProductMenu new_item) {
		is_cash_transaction = false;
		this.current_checkout = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_MENU);
		updateCurrentProduct(new_item, 1, true);
	}

	private void updateCurrentProduct(SellProductMenu new_item, int amount, boolean add_list) {
		this.product_amount = amount;
		this.current_product = new_item;
		
		ConfigInteractiveMenu buy_menu = this.current_checkout;
		
		buy_menu.updateProduct(this.current_product.getSellingItem(this, this.product_amount));
		
		updateCurrentInventory(buy_menu, add_list);
	}

	public void buyCurrentProduct() {
		buyCurrentProduct(this.product_amount, this.current_product, true);
	}
	
	public void buyCurrentProduct(int amount, SellProductMenu menu, boolean remove_cash) {
		long cash_needed = amount*menu.getValueInCash();
		
		if (!remove_cash || this.canBuyThisItem(menu)) {
			if (!remove_cash || verifyCurrency(menu, cash_needed)) {
				this.current_menu = null;
				
				if (remove_cash) this.removeCurrency(menu, cash_needed);
				
				ProductConfig pc = menu.getProduct();
				
				for (int i = 0; i < amount; i++) {
					for (ItemStack item : pc.getItems()) {
						this.giveItem(item.clone());
					}
					
					for (String s : pc.getCommands()) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("@player", Bukkit.getOfflinePlayer(uuid).getName()));
					}
				}
				
				this.last_buy_time.put(menu.getId(), System.currentTimeMillis());
				
				Bukkit.getPlayer(uuid).closeInventory();
				Bukkit.getPlayer(uuid).sendMessage(CashShop.getInstance().getMessagesConfig().getString("product.buy.sucess"));
				
				Bukkit.getPluginManager().callEvent(new BuyCashItemEvent(uuid, menu, amount));
			} else {
				Bukkit.getPlayer(uuid).sendMessage(CashShop.getInstance().getMessagesConfig().getString("product.buy.fail"));
			}
		}
	}
	
	private void removeCurrency(SellProductMenu menu, long value) {
		if (menu.isMoney()) {
			CashShop.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(this.getUniqueId()), value);
			return;
		}
		
		this.removeCash(value);
	}
	
	private boolean verifyCurrency(SellProductMenu menu, long value) {
		if (menu.isMoney()) {
			return CashShop.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(this.getUniqueId())) >= value;
		}
		
		return this.getCash() >= value;
	}
	
	public boolean canBuyThisItem(SellProductMenu product) {
		if (product != null && product.getDelayToBuy() != 0) {
			if (last_buy_time.get(product.getId()) != null) {
				if (product.getDelayToBuy() < 0) {
					return false;
				} else {
					long tempo_armazenado = last_buy_time.get(product.getId());
					
					return tempo_armazenado + (product.getDelayToBuy()*1000*3600) < System.currentTimeMillis();
				}
			}
		}
		return true;
	}
	
	private void giveItem(ItemStack item) {
		Player p = Bukkit.getPlayer(uuid);
        if (espacoInv((Inventory)p.getInventory(), item) >= item.getAmount()) {
            p.getInventory().addItem(item.clone());
        } else {
            p.getWorld().dropItem(p.getLocation(), item.clone());
        }
    }

	private int espacoInv(Inventory inv, ItemStack item) {
        int quantia = 0;
        int slots = 0;
        slots = inv.getType().equals(InventoryType.PLAYER) ? 35 : inv.getSize() - 1;
        for (int i = 0; i <= slots; ++i) {
            int stack;
            int max;
            if (inv.getItem(i) == null) {
                quantia += item.getMaxStackSize();
                continue;
            }
            if (item == null || inv.getItem(i).getType() != item.getType() || (max = item.getMaxStackSize()) == (stack = inv.getItem(i).getAmount())) continue;
            int adicionar = max - stack;
            quantia += adicionar;
        }
        return quantia;
    }

	public long getDelayToBuyAgain(SellProductMenu product) {
		if (product.getDelayToBuy() != 0) {
			if (last_buy_time.get(product.getId()) != null) {
				long tempo_armazenado = last_buy_time.get(product.getId());
				
				return ((tempo_armazenado + (product.getDelayToBuy()*1000*3600)) - System.currentTimeMillis())/1000;
			}
		}
		
		return 0;
	}
	
	public void addProductAmount(int amount) {
		if (this.current_product.getDelayToBuy() == 0) {
			this.product_amount = this.product_amount + amount > 0 ? this.product_amount + amount : 1;
			ArrayList<ConfigInteractiveMenu> oldList = new ArrayList<>(previus_menus);
			
			updateCurrentProduct(this.current_product, this.product_amount, false);
			
			this.previus_menus.clear();
			this.previus_menus.addAll(oldList);
		}
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
			EditionComponent component = current_menu.getVisualizableItems().get(slot).clone();
			
			switch (component.getType()) {
				case BUY_PRODUCT:
					SellProductMenu product = CashShop.getInstance().getProduct(component.getName());
					if (!this.canBuyThisItem(product)) {
						ItemStack item = ItemGenerator.getItemStack(
								CashShop.getInstance().getMessagesConfig().getString("items.delay_expired.material"), 
								CashShop.getInstance().getMessagesConfig().getString("items.delay_expired.name"), 
								CashShop.getInstance().getMessagesConfig().getStringAsItemLore(product.getDelayToBuy() < 0 ? "items.delay_expired.lore.negative" : "items.delay_expired.lore.positive").replaceAll("@time", getTranslatedTime(getDelayToBuyAgain(product))));
						
						component.setItemStack(item);
						component.setType(EditionComponentType.DO_NOTHING);
					}
					break;
					
				default:
					break;
			}
			
			ItemStack item = current_menu.generateItem(component, this);
			
			inv.setItem(slot, item);
		}
		
		return inv;
	}
	
	
	private String getTranslatedTime(long restante) {
		DecimalFormat f = new DecimalFormat("00");
		
		long segundos = restante % 60;
		long minutos = (restante / 60) % 60;
		long horas = (restante / 60 / 60) % 24;
		long dias = restante / 60 / 60 / 24;
		
		return f.format(dias) + "D " + f.format(horas) + "H " + f.format(minutos) + "m " + f.format(segundos) + "s";
	}
	
	public double getAmountSpentThisMonth() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		return getAmountSpent(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
	}

	public double getAmountSpent(int year, int month) {
		double total = 0;
		Calendar c = Calendar.getInstance();
		
		for (String s : transactions_approved.keySet()) {
			TransactionInfo ti = transactions_approved.get(s);
			c.setTimeInMillis(ti.getApproveDate());
			
			if (c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH)+1 == month) {
				total += ti.getRealMoneySpent();
			}
		}
		
		return total;
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
		fc.set("buy_times", null);
		
		for (String token : transactions_pending.keySet()) {
			setTransactionToFile("transactions.pending", transactions_pending.get(token), fc);
		}
		
		for (String token : transactions_approved.keySet()) {
			setTransactionToFile("transactions.approved", transactions_approved.get(token), fc);
		}
		
		for (String id : this.last_buy_time.keySet()) {
			fc.set("buy_times." + id, this.last_buy_time.get(id));
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
			
			if (fc.get("buy_times") != null) {
				for (String id : fc.getConfigurationSection("buy_times").getKeys(false)) {
					last_buy_time.put(id, fc.getLong("buy_times." + id));
				}
			}
			
		}
	}
	
	private void setTransactionToFile(String path, TransactionInfo ti, FileConfiguration fc) {
		fc.set(path + "." + ti.getTransactionToken() + ".gateway", ti.getGatewayCaller());
		fc.set(path + "." + ti.getTransactionToken() + ".link", ti.getLink());
		fc.set(path + "." + ti.getTransactionToken() + ".cash", ti.getCash());
		fc.set(path + "." + ti.getTransactionToken() + ".real_money", ti.getRealMoneySpent());
		fc.set(path + "." + ti.getTransactionToken() + ".player", ti.getPlayer());
		fc.set(path + "." + ti.getTransactionToken() + ".creation", ti.getCreationDate());
		fc.set(path + "." + ti.getTransactionToken() + ".approvation", ti.getApproveDate());
		fc.set(path + "." + ti.getTransactionToken() + ".cupom", ti.getCupom());
	}
	
	private TransactionInfo loadTransaction(String path, String token, FileConfiguration fc) {
		String gateway = fc.getString(path + "." + token + ".gateway");
		String link = fc.getString(path + "." + token + ".link");
		String player = fc.getString(path + "." + token + ".player");
		String cupom = fc.getString(path + "." + token + ".cupom");
		int cash = fc.getInt(path + "." + token + ".cash");
		double real_money = fc.getDouble(path + "." + token + ".real_money");
		long creation = fc.getLong(path + "." + token + ".creation");
		long approve = fc.get(path + "." + token + ".approvation") != null ? fc.getLong(path + "." + token + ".approvation") : creation;
		
		return new TransactionInfo(player, gateway, cupom, cash, real_money, creation, approve, link, token);
	}
}
