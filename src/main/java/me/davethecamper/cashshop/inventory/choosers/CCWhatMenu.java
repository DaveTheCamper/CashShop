package me.davethecamper.cashshop.inventory.choosers;

import java.util.ArrayList;
import java.util.UUID;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ComboItemMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;

public class CCWhatMenu extends ChoosableMenu {

	public CCWhatMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus, int mode) {
		super(uuid, item_config, previus);
		
		this.mode = mode;
		
		load();
	}
	
	private final int BUTTON_BACK = 26;
	private final int BUTTON_DECORATIVE = 10;
	private final int BUTTON_PRODUCT = 12;
	private final int BUTTON_COMBO = 14;
	private final int BUTTON_CATEGORIE = 16;
	
	private int mode;

	@Override
	public void reload() {
		this.unregisterAll();
		load();
	}
	
	private void load() {
		this.changeInventorySize(27);
		
		this.registerButton(BUTTON_BACK, item_config.getItemFromConfig("items.back"));
		
		this.registerButton(BUTTON_DECORATIVE, item_config.getItemFromConfig("items.choosable.create.decorative"));
		this.registerButton(BUTTON_PRODUCT, item_config.getItemFromConfig("items.choosable.create.product"));
		this.registerButton(BUTTON_COMBO, item_config.getItemFromConfig("items.choosable.create.combo"));
		this.registerButton(BUTTON_CATEGORIE, item_config.getItemFromConfig("items.choosable.create.categorie"));
	}

	@Override
	public ChoosableMenu getNextChoosable(int choose) {

		switch (choose) {
			case BUTTON_BACK:
				return (ChoosableMenu) this.getPreviousMenu();
			default:
				switch (mode) {
					case ChooseEditionMenu.CLONE_MODE:
						switch (choose) {
							case BUTTON_DECORATIVE:
								return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedDecorativeItems(), mode);
								
							case BUTTON_PRODUCT:
								return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedProducts(), mode);
								
							case BUTTON_COMBO:
								return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedCombos(), mode);
								
							case BUTTON_CATEGORIE:
								return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedCategories(), mode);
						}
						break;
				}
				break;
			
		}
 		return null;
	}
	
	private String getNewName(ArrayList<? extends ConfigItemMenu> list, String prefix, Class<? extends ConfigItemMenu> reference) {
		int size = list.size();
		String name = prefix + size;
		
		while (CashShop.getInstance().getConfigItemBasedOnClass(name, reference) != null) {
			name = prefix + (++size);
		}
		
		return name;
	}

	@Override
	public ConfigItemMenu getFinalStep(int choose) {
		String name = "";
		ConfigItemMenu cim = null;
		ItemMenuProperties default_properties = new ItemMenuProperties(ItemGenerator.getItemStack("STONE"), "§fDefault name", new ArrayList<>(), false);
		
		switch (choose) {
			case BUTTON_DECORATIVE:
				name = this.getNewName(CashShop.getInstance().getLists().getSortedDecorativeItems(), "product", ConfigItemMenu.class);
				cim = new ConfigItemMenu(name, item_config, this, default_properties);
				break;
			
			case BUTTON_PRODUCT:
				name = this.getNewName(CashShop.getInstance().getLists().getSortedProducts(), "product", SellProductMenu.class);
				cim = new SellProductMenu(name, item_config, this, default_properties, new ProductConfig(), 1, 0);
				break;
				
			case BUTTON_COMBO:
				name = this.getNewName(CashShop.getInstance().getLists().getSortedCombos(), "combo", ComboItemMenu.class);
				cim = new ComboItemMenu(name, item_config, this, default_properties, 1, 1);
				break;
				
			case BUTTON_CATEGORIE:
				name = this.getNewName(CashShop.getInstance().getLists().getSortedCategories(), "category", ConfigInteractiveMenu.class);
				cim = new ConfigInteractiveMenu(name, item_config, this, default_properties, 54, "§7Default category name");
				break;
		}
		
		return cim;
	}

	@Override
	public boolean isLastChoose(int choose) {
		switch (mode) {
			case ChooseEditionMenu.CREATE_MODE:
				switch (choose) {
					case BUTTON_DECORATIVE:
					case BUTTON_PRODUCT:
					case BUTTON_COMBO:
					case BUTTON_CATEGORIE:
						return true;
				}
		}
		return false;
	}

	@Override
	protected boolean updateBeforeBack() {return false;}

	

}
