package me.davethecamper.cashshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.davethecamper.cashshop.api.CashShopApi;
import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.InitializationResult;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.choosers.MainChooseMenu;
import me.davethecamper.cashshop.inventory.configs.ComboItemMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.configs.ValuebleItemMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.inventory.edition.EditionComponentType;
import me.davethecamper.cashshop.objects.CashShopClassLoader;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import me.davethecamper.cashshop.player.CashPlayer;

public class CashShop extends JavaPlugin {

	public static String PLUGIN_NAME = "CashShop";
	
	public final String LABEL_MAIN = "main";
	public final String LABEL_TRANSACTIONS = "transactions";
	public final String LABEL_COMBOS = "combos";
	public final String LABEL_CHECKOUT = "checkout";
	
	public static final String MAIN_MENU = "main";
	public static final String TRANSACTION_MENU = "transactions";
	public static final String GATEWAYS_MENU = "gateways";
	public static final String CHECKOUT_MENU = "checkout";
	public static final String COMBOS_MENU = "combo_buy";
	
	public static final String ADD_AMOUNT_1_BUTTON = "add_amount_1";
	public static final String ADD_AMOUNT_5_BUTTON = "add_amount_5";
	public static final String ADD_AMOUNT_10_BUTTON = "add_amount_10";
	public static final String REMOVE_AMOUNT_1_BUTTON = "remove_amount_1";
	public static final String REMOVE_AMOUNT_5_BUTTON = "remove_amount_5";
	public static final String REMOVE_AMOUNT_10_BUTTON = "remove_amount_10";
	public static final String GIFT_NAME_BUTTON = "gift_name";
	public static final String DISCOUNT_BUTTON = "discount_coupon";
	public static final String BACK_BUTTON = "back_button";
	public static final String SHOW_CASH_BUTTON = "show_cash";
	public static final String CONFIRM_BUY_BUTTON = "confirm_buy";
	public static final String CHECKOUT_CASH_BUTTON = "checkout_cash";

	public final static String REPLACE_GATEWAY_BUTTON = "gateway_info";
	public final static String REPLACE_TRANSACTION_BUTTON = "transaction_info";
	public final static String REPLACE_ITEM_SELLING_BUTTON = "preview_selling";
	
	public ArrayList<String> static_labels = new ArrayList<>(Arrays.asList(REPLACE_GATEWAY_BUTTON, CONFIRM_BUY_BUTTON, SHOW_CASH_BUTTON, REPLACE_ITEM_SELLING_BUTTON, REPLACE_TRANSACTION_BUTTON, BACK_BUTTON, DISCOUNT_BUTTON, GIFT_NAME_BUTTON, REMOVE_AMOUNT_10_BUTTON, REMOVE_AMOUNT_5_BUTTON, REMOVE_AMOUNT_1_BUTTON, ADD_AMOUNT_10_BUTTON, ADD_AMOUNT_5_BUTTON, ADD_AMOUNT_1_BUTTON, COMBOS_MENU, CHECKOUT_MENU, GATEWAYS_MENU, TRANSACTION_MENU, MAIN_MENU));

	
	public ConfigManager configuration;
	public ConfigManager messages;
	
	private String coin_name = "Cash";

	private static CashShopApi api;
	private CashShop cs;
	private CupomManager cm;
	

	private HashMap<String, CashShopGateway> apis = new HashMap<>();

	public TreeMap<String, ConfigItemMenu> do_nothing = new TreeMap<>();
	public TreeMap<String, ConfigInteractiveMenu> categories = new TreeMap<>();
	public TreeMap<String, SellProductMenu> products = new TreeMap<>();
	public TreeMap<String, ComboItemMenu> combos = new TreeMap<>();
	public TreeMap<String, ConfigItemMenu> static_items = new TreeMap<>();
	
	
	private HashMap<UUID, ReciclableMenu> players_editors = new HashMap<>();
	public HashMap<UUID, CashPlayer> players = new HashMap<>();
	
	
	public CashPlayer getNormalPlayerInventory(UUID uuid) {
		if (players.get(uuid) == null) {
			players.put(uuid, new CashPlayer(uuid));
		}
		return players.get(uuid);
	}
	
	

	public void createPlayerInventory(UUID uuid) {
		MainChooseMenu mcm = new MainChooseMenu(uuid, messages);
		
		players_editors.put(uuid, mcm);
		
		Bukkit.getPlayer(uuid).openInventory(mcm.getInventory());
	}
	
	public void changePlayerInventory(UUID uuid, ReciclableMenu menu) {players_editors.put(uuid, menu);}
	
	public ReciclableMenu getPlayerCurrentInventory(UUID uuid) {return players_editors.get(uuid);}
	
	public boolean haveInventoryOpen(UUID uuid) {return players_editors.containsKey(uuid);}
	
	
	
	
	@Override
	public void onEnable() {
		cs = this;
		api = new CashShopApi(cs);
		cm = new CupomManager();
		
		load();
		autoSave();
		new TransactionsManager(this);
	}
	
	@Override
	public void onDisable() {
		this.saveAll();
	}
	
	public static CashShopApi getInstance() {
		return api;
	}
	
	
	public void reload() {
		apis.clear();
		do_nothing.clear();
		categories.clear();
		products.clear();
		combos.clear();
		static_items.clear();
		players_editors.clear();
		
		
		api = new CashShopApi(cs);
		configuration = null;
		messages = null;
		
		HandlerList.unregisterAll(this);
		
		load();
	}
	
	private void load() {
		loadConfigs();
		loadAllApis();
		
		Bukkit.getPluginManager().registerEvents(new EventsCatcher(this), cs);
		
		loadCommands();
		
		verifyExitentStaticItems();
		loadObjects();
	}
	
	@SuppressWarnings("unchecked")
	private <Z extends ConfigItemMenu> void loadObjects() {
		File f = new File(this.getDataFolder().getAbsolutePath() + "/objects/");
		
		if (f.exists()) {
			for (File folder : f.listFiles()) {
				if (folder.isDirectory()) {
					for (File object : folder.listFiles()) {
						FileConfiguration fc = YamlConfiguration.loadConfiguration(object);
						
						ItemMenuProperties imp = fc.get("item.name") != null ? 
								new ItemMenuProperties(fc.getItemStack("item.item"), fc.getString("item.name"), new ArrayList<>(fc.getStringList("item.lore")), fc.getBoolean("item.glow")) : null;
						
						HashMap<Integer, EditionComponent> component = null;
						String name = fc.get("inventory.name") != null ? fc.getString("inventory.name") : "";
						
						int size = fc.get("inventory.name") != null ? fc.getInt("inventory.size") : 0;
						int value = fc.get("value") != null ? fc.getInt("value") : 1;
						double combo_value = fc.get("combo.value") != null ? fc.getDouble("combo.value") : 0;
						
						
						if (fc.get("inventory.items") != null) {
							component = new HashMap<>();
							for (String s : fc.getConfigurationSection("inventory.items.slot").getKeys(false)) {
								EditionComponent c = new EditionComponent(EditionComponentType.valueOf(fc.getString("inventory.items.slot." + s + ".type")), fc.getString("inventory.items.slot." + s + ".name"));
								component.put(Integer.parseInt(s), c);
							}
						}
						
						ArrayList<ItemStack> items = fc.get("selling.items") != null ? new ArrayList<ItemStack>((Collection<? extends ItemStack>) fc.getList("selling.items")) : null;
						
						ArrayList<String> commands = fc.get("selling.commands") != null ? new ArrayList<>(fc.getStringList("selling.commands")) : null;
 						
						
						String identificador = object.getName().substring(0, object.getName().length()-4);
						String type = fc.getString("type");
						
						TreeMap<String, Z> map = (TreeMap<String, Z>) getMapBasedOnType(type, object);
						
						switch (type) {
							case "do-nothing":
								addToMap(map, identificador, (Z) new ConfigItemMenu(identificador, this.messages, null, imp));
								break;
								
							case "category":
								addToMap(map, identificador, (Z) new ConfigInteractiveMenu(identificador, this.messages, null, imp, component, size, name));
								break;
								
							case "product":
								addToMap(map, identificador, (Z) new SellProductMenu(identificador, this.messages, null, imp, new ProductConfig(items, commands), value));
								break;
								
							case "combo-item":
								addToMap(map, identificador, (Z) new ComboItemMenu(identificador, this.messages, null, imp, value, combo_value));
								break;
								
							case "valueable":
								addToMap(map, identificador, (Z) new ValuebleItemMenu(identificador, this.messages, null, imp, value));
								break;
						}
					}
				}
			}
		}
	}
	
	private void verifyExitentStaticItems() {
		for (String s : static_labels) {
			File f = new File(this.getDataFolder().getAbsolutePath() + "/objects/static/" + s + ".yml");
			if (!f.exists()) {
				this.saveResource("objects/static/" +  s + ".yml", true);
			}
		}
	}
	
	
	private TreeMap<String, ? extends ConfigItemMenu> getMapBasedOnType(String type, File f) {
		if (f.getParentFile().getName().contains("static")) {
			return this.static_items;
		}
		
		switch (type) {
			case "do-nothing":
				return this.do_nothing;
			case "category":
				return this.categories;
			case "product":
				return this.products;
			case "combo-item":
				return this.combos;
		}
		
		return null;
	}
	
	private <Z extends ConfigItemMenu> void addToMap(TreeMap<String, Z> map, String identifier, Z item) {
		map.put(identifier, item);
	}
	
	private void loadConfigs() {
		File f = new File(cs.getDataFolder().getAbsolutePath() + "/config.yml");
		if (!f.exists()) cs.saveResource("config.yml", true);
		
		configuration = new ConfigManager(f);
		
		f = new File(cs.getDataFolder().getAbsolutePath() + "/messages/msg_" + configuration.getString("message_file") + ".yml");
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			cs.saveResource("messages/msg_en.yml", isEnabled());
		}
		
		if (!f.exists()) f = new File(cs.getDataFolder().getAbsolutePath() + "/messages/msg_en.yml");
		
		messages = new ConfigManager(f);
	}
	
	public CashShopGateway getGateway(String name) {
		return apis.get(name);
	}
	
	public ArrayList<String> getGatewaysNames() {
		return new ArrayList<String>(apis.keySet());
	}
	
	public CupomManager getCupomManager() {
		return this.cm;
	}
	
	private void loadAllApis() {
		File f = new File(cs.getDataFolder().getAbsolutePath() + "/Gateways/");
		if (!f.exists()) {
			f.mkdirs();
		} else {
			for (File gateway : f.listFiles()) {
				CashShopGateway api = null;
				if (gateway.getName().endsWith(".jar")) {
					BufferedReader reader = null;
					try {
						JarFile jf = new JarFile(gateway);
						
						ZipEntry ze = jf.getEntry("gateway.yml");
						if (ze != null) {
							reader = new BufferedReader(new InputStreamReader(jf.getInputStream(ze), "UTF-8"));
							FileConfiguration fc = YamlConfiguration.loadConfiguration(reader);
							String class_name = fc.getString("class");

							Class<? extends CashShopGateway> c = CashShopGateway.class;
							ClassLoader classloader = c.getClassLoader();
							
							CashShopClassLoader cscl = new CashShopClassLoader(new URL[] {gateway.toURI().toURL()}, classloader, class_name, jf);
							
							api = cscl.getGateway();
							
							cscl.close();
						}
						
						reader.close();
						jf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (api != null) {
					File api_config = new File(f.getAbsoluteFile() + "/" + api.getIdentifier() + "/config.yml");
					FileConfiguration fc = YamlConfiguration.loadConfiguration(api_config);
					if (api_config.exists()) {
						InitializationResult ir = api.init(fc, coin_name);
						
						if (!api.isValidCurrency(configuration.getString("currency.code"))) {
							Bukkit.getConsoleSender().sendMessage("§f<*> §b" + api.getIdentifier() + " §f-> §c" + messages.getString("api.error.currency"));
							continue;
						}
						
						switch (ir) {
							case INITIALIZATED:
								apis.put(api.getIdentifier(), api);
								Bukkit.getConsoleSender().sendMessage("§f<*> §b" + api.getIdentifier() + " §f-> §a" + messages.getString("api.loaded"));
								break;
								
							case INVALID_CREDENTIALS:
								Bukkit.getConsoleSender().sendMessage("§f<*> §b" + api.getIdentifier() + " §f-> §c" + messages.getString("api.error.credentials"));
								break;

							case INVALID_CURRENCY:
								Bukkit.getConsoleSender().sendMessage("§f<*> §b" + api.getIdentifier() + " §f-> §c" + messages.getString("api.error.currency"));
								break;
								
							case OFFLINE_API:
								Bukkit.getConsoleSender().sendMessage("§f<*> §b" + api.getIdentifier() + " §f-> §c" + messages.getString("api.error.offline"));
								break;
								
							default:
								break;
						}
						
					} else {
						api.generateConfigurationFile(fc);
						try {
							fc.save(api_config);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	

	private void autoSave() {
		new BukkitRunnable() {
			@Override
			public void run() {
				saveAll();
			}
		}.runTaskTimer(this, 0, 300*20);
	}
	
	private void saveAll() {
		cm.save();
		
		for (UUID uuid : players.keySet()) {
			CashPlayer cp = players.get(uuid);
			if (cp.hasChanges()) {
				cp.save();
			}
		}
	}
	
	
	private void loadCommands() {
		CashCommands cc = new CashCommands(this);

		getCommand("cash").setExecutor(cc);
		getCommand("shop").setExecutor(cc);
	}
}
