package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

public class EditSomethingMenu extends ChoosableMenu {

	public EditSomethingMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus) {
		super(uuid, item_config, previus);
		
		load();
	}

	private final int BUTTON_DECORATIVE = 11;
	private final int BUTTON_PRODUCTS = 12;
	private final int BUTTON_COMBOS = 13;
	private final int BUTTON_STATIC_INVS = 14;
	private final int BUTTON_CATEGORIES = 15;
	private final int BUTTON_BACK = 26;

	@Override
	public void reload() {
		this.unregisterAll();
		load();
	}
	
	private void load() {
		this.registerButton(BUTTON_DECORATIVE, item_config.getItemFromConfig("items.choosable.edit_something.decorative"));
		
		this.registerButton(BUTTON_PRODUCTS, item_config.getItemFromConfig("items.choosable.edit_something.products"));
		
		this.registerButton(BUTTON_COMBOS, item_config.getItemFromConfig("items.choosable.edit_something.combos"));
		
		this.registerButton(BUTTON_STATIC_INVS, item_config.getItemFromConfig("items.choosable.edit_something.static"));
		
		this.registerButton(BUTTON_CATEGORIES, item_config.getItemFromConfig("items.choosable.edit_something.categories"));
	
		
		this.registerButton(BUTTON_BACK, item_config.getItemFromConfig("items.back"));
	}

	@Override
	public ChoosableMenu getNextChoosable(int choose) {
		switch (choose) {
			case BUTTON_DECORATIVE:
				return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedDecorativeItems(), ChooseEditionMenu.EDIT_MODE);
		
			case BUTTON_PRODUCTS:
				return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedProducts(), ChooseEditionMenu.EDIT_MODE);
			
			case BUTTON_COMBOS:
				return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedCombos(), ChooseEditionMenu.EDIT_MODE);
				
			case BUTTON_STATIC_INVS:
				return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getStaticItems(), ChooseEditionMenu.EDIT_MODE);
			
			case BUTTON_CATEGORIES:
				return new ChooseEditionMenu(this.getPlayer(), item_config, this, CashShop.getInstance().getLists().getSortedCategories(), ChooseEditionMenu.EDIT_MODE);
				
			case BUTTON_BACK:
				return (ChoosableMenu) this.getPreviousMenu();
		}
		return null;
	}

	@Override
	public ConfigInteractiveMenu getFinalStep(int choose) {return null;}

	@Override
	public boolean isLastChoose(int choose) {return false;}

	@Override
	protected boolean updateBeforeBack() {return false;}

}
