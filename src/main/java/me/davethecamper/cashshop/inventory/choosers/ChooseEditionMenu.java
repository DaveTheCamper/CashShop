package me.davethecamper.cashshop.inventory.choosers;

import java.util.ArrayList;
import java.util.UUID;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;

public class ChooseEditionMenu extends ChoosableMenu {
	

	public ChooseEditionMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus, ArrayList<? extends ConfigItemMenu> categories, int mode) {
		this(uuid, item_config, previus, categories, mode, 1);
	}

	public ChooseEditionMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus, ArrayList<? extends ConfigItemMenu> categories, int mode, int page) {
		super(uuid, item_config, previus);

		this.categories = categories;
		this.page = page;
		this.mode = mode;
		
		load();
	}

	private ArrayList<? extends ConfigItemMenu> categories;
	
	public static final int CLONE_MODE = 1;
	public static final int CREATE_MODE = 2;
	public static final int EDIT_MODE = 3;
	
	private int BUTTON_BACK;
	private int BUTTON_PAGE_BACK;
	private int BUTTON_PAGE_NEXT;
	private int page;
	private int mode;
	
	@Override
	public void reload() {
		this.unregisterAll();
		this.categories = CashShop.getInstance().getLists().getListBasedOnClass(categories.get(0).getClass());
		load();
	}
	
	private void load() {
		int real_size = categories.size() - ((page-1) * 45);
		int slots_by_nine = (real_size/9 + (real_size % 9 > 0 ? 1 : 0)) + 1;
		this.changeInventorySize((slots_by_nine > 6 ? 6 : (slots_by_nine < 3 ? 3 : slots_by_nine))*9);
		
		this.BUTTON_BACK = this.getInventorySize()-1;
		this.BUTTON_PAGE_NEXT = this.getInventorySize()-4;
		this.BUTTON_PAGE_BACK = this.getInventorySize()-6;
		
		this.range_max = this.getInventorySize();
		this.range_min = this.getInventorySize()-9;
		
		int slot = 0;
		for (int i = (page-1)*45; i < categories.size(); i++) {
			if (slot < 9*5) {
				this.registerButton(slot, ItemGenerator.addLoreAfter(categories.get(i).getItemProperties().getItem().clone(), ";=;" + categories.get(i).getId(), "§7§o"));
				slot++;
			} else {
				break;
			}
		}

		if (page > 1) this.registerButton(BUTTON_PAGE_BACK, item_config.getItemFromConfig("items.navigable.backward"));
		if (categories.size() - (page*45) > 0) this.registerButton(BUTTON_PAGE_NEXT, item_config.getItemFromConfig("items.navigable.forward"));

		this.registerButton(BUTTON_BACK, item_config.getItemFromConfig("items.back"));
		
		//this.registerButton(slot, item_config.getItemFromConfig("items.creation.categorie"))
	}
	
	public int getMode() {return this.mode;}
	
	public ArrayList<? extends ConfigItemMenu> getCategories() {return new ArrayList<>(this.categories);}

	@Override
	public ChoosableMenu getNextChoosable(int choose) {
		if (choose == BUTTON_BACK) {
			return (ChoosableMenu) this.getPreviousMenu();
		} else if (choose == BUTTON_PAGE_NEXT) {
			if (categories.size() - (page*45) > 0) {
				return new ChooseEditionMenu(this.getPlayer(), item_config, this.getPreviousMenu(), categories, mode, page+1);
			}
		} else if (choose == BUTTON_PAGE_BACK) {
			if (page > 1) {
				return new ChooseEditionMenu(this.getPlayer(), item_config, this.getPreviousMenu(), categories, mode, page-1);
			}
		}
		return null;
	}

	@Override
	public ConfigItemMenu getFinalStep(int choose) {
		String name = "";
		if (mode == CLONE_MODE) {
			name = getNewName(categories, "cloned_object", categories.get(0).getClass());
		}
		ConfigItemMenu cim = name.length() > 0 ? categories.get(((page-1)*45) + choose).clone(name) : categories.get(((page-1)*45) + choose).clone();
		cim.setPrevious(this.getPreviousMenu());
		return cim;
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
	public boolean isLastChoose(int choose) {
		return choose < (categories.size() - ((page-1)*45));
	}

	@Override
	protected boolean updateBeforeBack() {
		return true;
	}

}
