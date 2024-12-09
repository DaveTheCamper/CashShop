package me.davethecamper.cashshop.player;

import lombok.Data;
import lombok.Setter;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.events.BuyCashItemEvent;
import me.davethecamper.cashshop.events.PreOpenCashInventoryEvent;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.ProductConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Data
public class CashPlayer {
	
	public CashPlayer(UUID uuid) {
		this.uniqueId = uuid;
		load();
	}
	
	
	private TreeMap<String, TransactionInfo> transactionsPending = new TreeMap<>();
	private TreeMap<String, TransactionInfo> transactionsApproved = new TreeMap<>();
	
	private HashMap<String, Long> lastBuyTime = new HashMap<>();
	
	private UUID uniqueId;

	private int cash = 0;

	private int cashBonus = 0;

	@Setter
    private int productAmount = 1;
	
	private boolean isCashTransaction = false;
	private boolean changes = false;
	private boolean updating = false;
	private boolean runningUpdater = false;
	private boolean usingMenus;
	private boolean cancelUpdater;

	private boolean keepBackHistory;
	
	@Setter
    private String cupom = "...";

	@Setter
	private String giftFor = "...";
	
	
	private BukkitRunnable updaterRunnable;

	private ArrayList<ConfigInteractiveMenu> previusMenus = new ArrayList<>();
	
	private ConfigInteractiveMenu currentMenu;
	private ConfigInteractiveMenu currentCheckout;
	
	private SellProductMenu currentProduct;
	
	private Inventory currentInventory;

	
	public EditionComponent getCurrentComponent(int slot) {return this.currentMenu.getVisualizableItems().get(slot);}
	
	public ConfigInteractiveMenu getCurrentInteractiveMenu() {return this.currentMenu;}
	
	public TreeMap<String, TransactionInfo> getPendingTransactions() {return this.transactionsPending;}
	
	public boolean isCashTransaction() {return this.isCashTransaction;}
	
	public boolean isOnline() {return Bukkit.getOfflinePlayer(uniqueId).isOnline();}
	
	public boolean hasChanges() {return this.changes;}
	
	public boolean haveAnyCurrentInventory() {return this.currentMenu != null;}
	
	public boolean haveCurrentInventoryFromMain() {return this.currentMenu != null && !previusMenus.isEmpty() && previusMenus.get(0).getId().equals("main");}


	public void setCash(int cash) {changes = true; this.cash = cash;}
	public void setCashBonus(int cash) {changes = true; this.cashBonus = cash;}

    public void setTransactionAsAproved(TransactionInfo ti) {
		ti.setApproved();
		
		transactionsPending.remove(ti.getTransactionToken());
		transactionsApproved.put(ti.getTransactionToken(), ti);
		this.changes = true;
		CashShop.getInstance().getCupomManager().addTransaction(ti.getCupom(), ti.getTransactionToken(), ti.getCash());
	}
	
	public void cancelTransaction(TransactionInfo ti) {
		transactionsPending.remove(ti.getTransactionToken());
		this.changes = true;
	}

	public int getCash() {
		return getCash(false);
	}

	public int getCash(boolean bonus) {
		return cash + (bonus ? cashBonus : 0);
	}

	public void addCash(long amount) {
		addCash(amount, false);
	}

	public void addCash(long amount, boolean bonus) {
		if (bonus) {
			this.cashBonus += (int) amount;
		} else {
			this.cash += (int) amount;
		}

		this.changes = true;
	}

	public void removeCash(long amount) {
		removeCash(amount, false);
	}

	public void removeCash(long amount, boolean bonus) {
		long total = amount;
		if (bonus) {
			long removeBonus = Math.min(total, cashBonus);

			addCash(-removeBonus, true);
			total -= removeBonus;
		}

		addCash(-total);
	}
			
	
	public void updateProductAmount() {
		new WaitingForChat(uniqueId, WaitingForChat.Primitives.INTEGER, "set_amount", CashShop.getInstance().getMessagesConfig().getString("chat.amount"));
	}
	
	public void updateDiscount() {
		new WaitingForChat(uniqueId, WaitingForChat.Primitives.STRING, "set_discount", CashShop.getInstance().getMessagesConfig().getString("chat.discount"));
	}
	
	public void updateGift() {
		new WaitingForChat(uniqueId, WaitingForChat.Primitives.STRING, "set_gift", CashShop.getInstance().getMessagesConfig().getString("chat.gift"));
	}
	
	public void reloadCurrentMenu() {
		if (this.isUsingMenus()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!isUsingMenus()) return;

					ConfigInteractiveMenu cat = CashShop.getInstance().getCategoriesManager().getCategorie(currentMenu.getId());
					Player p = Bukkit.getPlayer(uniqueId);
					PreOpenCashInventoryEvent pre = new PreOpenCashInventoryEvent(uniqueId, cat);
					
					Bukkit.getPluginManager().callEvent(pre);
					
					currentMenu.updateEntirelyInventory(pre.getMenu());
					Inventory generatedInv = generateInventory(pre.getMenu());
					Inventory inv = p.getOpenInventory().getTopInventory();
					
					for (int i = 0; i < inv.getSize(); i++) {
						inv.setItem(i, generatedInv.getItem(i));
					}
				}
			}.runTask(Bukkit.getPluginManager().getPlugin("CashShop"));
		}
	}

	public void updateCurrentInventory(ConfigInteractiveMenu new_menu) {
		updateCurrentInventory(new_menu, true);
	}

	public void updateCurrentInventory(ConfigInteractiveMenu new_menu, boolean add_list) {
		updateCurrentInventory(new_menu, add_list, true);
	}
	
	public void updateCurrentInventory(ConfigInteractiveMenu new_menu, boolean add_list, boolean open_inventory) {
		if (currentMenu != null && new_menu != null && !currentMenu.getId().equals(new_menu.getId())) {
			if (isRunningUpdater()) updaterRunnable.cancel();
		}

		if (currentMenu != null && add_list) {
			previusMenus.add(currentMenu);
		} else if (!keepBackHistory) {
			previusMenus.clear();
			setKeepBackHistory(false);
		}
		
		this.currentMenu = new_menu;
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
		
		generateTransactionItems(transactionsPending, items, identifiers, false);
		generateTransactionItems(transactionsApproved, items, identifiers, true);
		
		cim.replaceIndicators(CashShop.REPLACE_TRANSACTION_BUTTON, items, identifiers);
		
		updateCurrentInventory(cim);
	}

	public void openBuyCashMenu() {
		isCashTransaction = true;
		
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_MENU);
		cim.updateSomething(CashShop.CONFIRM_BUY_BUTTON, new EditionComponent(EditionComponentType.STATIC, CashShop.GATEWAYS_MENU));
		this.currentCheckout = cim;

		SellProductMenu spm = (SellProductMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_CASH_BUTTON);
		
		updateCurrentProduct(spm, 1, true);
	}
	
	public void openCurrentInventory() {
		if (Bukkit.getOfflinePlayer(uniqueId).isOnline()) {
			if (Objects.isNull(currentMenu)) return;

			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PreOpenCashInventoryEvent(uniqueId, currentMenu));
					currentInventory = generateInventory();
					Bukkit.getPlayer(uniqueId).openInventory(currentInventory);
				}
			}.runTask(Bukkit.getPluginManager().getPlugin("CashShop"));
		}
	}

	public void openLogFromCurrentInventory() {
		if (Bukkit.getOfflinePlayer(uniqueId).isOnline()) {
			if (this.currentMenu.getLogInventory() == null) {
				this.currentMenu.setLogInventory(generateInventory());
			}
			this.currentInventory = this.currentMenu.getLogInventory();
			Bukkit.getPlayer(uniqueId).openInventory(this.currentInventory);
		}
	}
	
	public void selectGateway(int slot) {
		ConfigInteractiveMenu cim = (ConfigInteractiveMenu) currentMenu;
		
		if (cim.isReplacedItem(slot)) {
			String identifier = cim.getReplacedItem(slot);

			CashShop.getInstance().getTransactionsManager().createPlayerTransaction(identifier, this);

			Bukkit.getPlayer(uniqueId).closeInventory();
			this.currentMenu = null;
		}
	}

	public void updateCurrentProduct() {
		if (this.currentProduct != null) {
			updateCurrentProduct(this.currentProduct, this.productAmount, false);
		} else {
			ConfigInteractiveMenu menu = this.getCurrentInteractiveMenu();
			if (!this.previusMenus.isEmpty()) this.previusMenus.remove(this.previusMenus.size()-1);
			
			this.updateCurrentInventory(CashShop.getInstance().getCategoriesManager().getCategorie(menu.getId()));
		}
		
	}
	
	public void updateCurrentProduct(SellProductMenu new_item) {
		isCashTransaction = false;
		this.currentCheckout = (ConfigInteractiveMenu) CashShop.getInstance().getStaticItem(CashShop.CHECKOUT_MENU);
		updateCurrentProduct(new_item, 1, true);
	}

	private void updateCurrentProduct(SellProductMenu new_item, int amount, boolean add_list) {
		this.productAmount = amount;
		this.currentProduct = new_item;
		
		ConfigInteractiveMenu buy_menu = this.currentCheckout;
		
		buy_menu.updateProduct(this.currentProduct.getSellingItem(this, this.productAmount));
		
		updateCurrentInventory(buy_menu, add_list);
	}

	public void buyCurrentProduct() {
		buyCurrentProduct(this.productAmount, this.currentProduct, true);
	}
	
	public void buyCurrentProduct(int amount, SellProductMenu menu, boolean remove_cash) {
		long cash_needed = (long) (amount*menu.getValueInCash());
		
		if (!remove_cash || this.canBuyThisItem(menu)) {
			Player player = Bukkit.getPlayer(uniqueId);

			if (!remove_cash || verifyCurrency(menu, cash_needed)) {
				this.currentMenu = null;
				
				if (remove_cash) this.removeCurrency(menu, cash_needed);
				
				ProductConfig pc = menu.getProduct();
				
				for (int i = 0; i < amount; i++) {
					for (ItemStack item : pc.getItems()) {
						this.giveItem(item.clone());
					}
					
					for (String s : pc.getCommands()) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("@player", Bukkit.getOfflinePlayer(uniqueId).getName()));
					}
				}
				
				this.lastBuyTime.put(menu.getId(), System.currentTimeMillis());


				Bukkit.getLogger().info("[CASH] O jogador " + player.getName() + " comprou product=" + pc);
				Bukkit.getLogger().info("[CASH] " + player.getName() + " id=" + menu.getId() + ", cash=" + menu.isMoney() +
						", value=" + cash_needed + ", remove=" + remove_cash);

				player.closeInventory();
				player.sendMessage(CashShop.getInstance().getMessagesConfig().getString("product.buy.sucess"));
				
				Bukkit.getPluginManager().callEvent(new BuyCashItemEvent(uniqueId, menu, amount));
			} else {
				player.sendMessage(CashShop.getInstance().getMessagesConfig().getString("product.buy.fail"));
			}
		}
	}
	
	private void removeCurrency(SellProductMenu menu, long value) {
		if (menu.isMoney()) {
			CashShop.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(this.getUniqueId()), value);
			return;
		}
		
		this.removeCash(value, true);
	}
	
	private boolean verifyCurrency(SellProductMenu menu, long value) {
		if (menu.isMoney()) {
			return CashShop.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(this.getUniqueId())) >= value;
		}
		
		return this.getCash(true) >= value;
	}
	
	public boolean canBuyThisItem(SellProductMenu product) {
		if (Objects.isNull(product) || product.getDelayToBuy() == 0)
			return true;

		if (!lastBuyTime.containsKey(product.getId()))
			return true;

		if (product.getDelayToBuy() < 0)
			return false;

		if (Objects.isNull(CashShop.getInstance().getProduct(product.getId())))
			return true;

		long storedTime = lastBuyTime.get(product.getId());

		return storedTime + (product.getDelayToBuy()*1000*3600) < System.currentTimeMillis();
	}
	
	private void giveItem(ItemStack item) {
		Player p = Bukkit.getPlayer(uniqueId);
        if (espacoInv(p.getInventory(), item) >= item.getAmount()) {
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
			if (lastBuyTime.get(product.getId()) != null) {
				long tempo_armazenado = lastBuyTime.get(product.getId());
				
				return ((tempo_armazenado + (product.getDelayToBuy()*1000*3600)) - System.currentTimeMillis())/1000;
			}
		}
		
		return 0;
	}
	
	public void addProductAmount(int amount) {
		if (this.currentProduct.getDelayToBuy() == 0) {
			this.productAmount = this.productAmount + amount > 0 ? this.productAmount + amount : 1;
			ArrayList<ConfigInteractiveMenu> oldList = new ArrayList<>(previusMenus);
			
			updateCurrentProduct(this.currentProduct, this.productAmount, false);
			
			this.previusMenus.clear();
			this.previusMenus.addAll(oldList);
		}
	}

	public void removeProductAmount(int amount) {
		addProductAmount(-amount);
	}
	
	public void backInventory() {
		if (isRunningUpdater()) this.setCancelUpdater(true);

		if (!previusMenus.isEmpty()) {
			this.currentMenu = previusMenus.get(previusMenus.size()-1);
			previusMenus.remove(previusMenus.size()-1);
			openCurrentInventory();
		}
	}

	private Inventory generateInventory() {
		return generateInventory(this.currentMenu);
	}
	
	private Inventory generateInventory(ConfigInteractiveMenu currentMenu) {
		Inventory inv = Bukkit.createInventory(null, currentMenu.getSize(), currentMenu.getName());
		
		for (Integer slot : currentMenu.getVisualizableItems().keySet()) {
			EditionComponent component = currentMenu.getVisualizableItems().get(slot).clone();
			
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
			
			ItemStack item = currentMenu.generateItem(component, this);
			
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
		
		for (String s : transactionsApproved.keySet()) {
			TransactionInfo ti = transactionsApproved.get(s);
			c.setTimeInMillis(ti.getApproveDate());
			
			if (c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH)+1 == month) {
				total += ti.getRealMoneySpent();
			}
		}
		
		return total;
	}
	
	public void startUpdater(int millis) {
		if (updaterRunnable != null && runningUpdater) return;
			
		runningUpdater = true;
		Plugin plugin = Bukkit.getPluginManager().getPlugin("CashShop");
		CashPlayer cp = this;
		
		updaterRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (!cp.isUsingMenus() || cancelUpdater) {
					this.cancel();
					return;
				}
				
				updating = true;
				callReload();
				updating = false;
			}
			
			private void callReload() {
				new BukkitRunnable() {
					@Override
					public void run() {
						cp.reloadCurrentMenu();
					}
				}.runTask(plugin);
			}
			
			@Override
			public void cancel() throws IllegalStateException {
				updating = false;
				runningUpdater = false;
				cancelUpdater = false;
				super.cancel();
			}
		};
		
		updaterRunnable.runTaskTimer(plugin, millis, millis);
	}
	
	
	public void save() {
		File f = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/players/" + uniqueId + ".yml");
		
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		fc.set("cash", this.cash);
		fc.set("cashBonus", this.cashBonus);
		
		fc.set("transactions.pending", null);
		fc.set("transactions.approved", null);
		fc.set("buy_times", null);
		
		for (String token : transactionsPending.keySet()) {
			setTransactionToFile("transactions.pending", transactionsPending.get(token), fc);
		}
		
		for (String token : transactionsApproved.keySet()) {
			setTransactionToFile("transactions.approved", transactionsApproved.get(token), fc);
		}
		
		for (String id : this.lastBuyTime.keySet()) {
			fc.set("buy_times." + id, this.lastBuyTime.get(id));
		}
		
		
		try {
			fc.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void load() {
		File f = new File(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/players/" + uniqueId + ".yml");
		
		if (f.exists()) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			this.cash = fc.getInt("cash");
			this.cashBonus = fc.getInt("cashBonus", 0);
			
			if (fc.get("transactions.pending") != null) {
				for (String token : fc.getConfigurationSection("transactions.pending").getKeys(false)) {
					TransactionInfo transaction = loadTransaction("transactions.pending", token, fc);
					transactionsPending.put(token, transaction);
				}
			}

			if (fc.get("transactions.approved") != null) {
				for (String token : fc.getConfigurationSection("transactions.approved").getKeys(false)) {
					TransactionInfo transaction = loadTransaction("transactions.approved", token, fc);
					transactionsApproved.put(token, transaction);
				}
			}
			
			if (fc.get("buy_times") != null) {
				for (String id : fc.getConfigurationSection("buy_times").getKeys(false)) {
					lastBuyTime.put(id, fc.getLong("buy_times." + id));
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
