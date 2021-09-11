package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

public class EditStaticMenu extends ChoosableMenu {

	public EditStaticMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus) {
		super(uuid, item_config, previus);
		load();
	}

	private final int BUTTON_TRANSACTIONS = 11;
	private final int BUTTON_COMBOS = 13;
	private final int BUTTON_SELLING = 15;
	private final int BUTTON_BACK = 26;

	@Override
	public void reload() {
		this.unregisterAll();
		load();
	}
	
	private void load() {
		this.registerButton(BUTTON_BACK, item_config.getItemFromConfig("items.back"));
		
		this.range_max = this.getInventorySize();
		this.range_min = this.getInventorySize()-9;
	}


	@Override
	public ChoosableMenu getNextChoosable(int choose) {
		switch (choose) {
			case BUTTON_BACK:
				return (ChoosableMenu) this.getPreviousMenu();
		}
		return null;
	}

	@Override
	public ConfigInteractiveMenu getFinalStep(int choose) {
		switch (choose) {
			case BUTTON_TRANSACTIONS:
				return CashShop.getInstance().getCategoriesManager().getTransactionsCategorie().clone();
			case BUTTON_COMBOS:
				return CashShop.getInstance().getCategoriesManager().getCombosCategorie().clone();
			case BUTTON_SELLING:
				return CashShop.getInstance().getCategoriesManager().getCheckoutCategorie().clone();
		}
		return null;
	}

	@Override
	public boolean isLastChoose(int choose) {
		switch (choose) {
			case BUTTON_TRANSACTIONS:
			case BUTTON_COMBOS:
			case BUTTON_SELLING:
				return true;
		}
		return false;
	}

	@Override
	protected boolean updateBeforeBack() {
		// TODO Auto-generated method stub
		return false;
	}

}
