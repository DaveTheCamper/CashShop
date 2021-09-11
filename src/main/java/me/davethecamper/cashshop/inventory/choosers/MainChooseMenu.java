package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

public class MainChooseMenu extends ChoosableMenu {

	public MainChooseMenu(UUID uuid, ConfigManager item_config) {
		super(uuid, item_config, null);
		
		load();
	}

	private final int SLOT_CREATE = 11;
	private final int SLOT_EDIT = 15;
	
	@Override
	public void reload() {
		this.unregisterAll();
		load();
	}
	
	private void load() {
		this.registerButton(SLOT_CREATE, item_config.getItemFromConfig("items.choosable.main.create"));
		
		this.registerButton(SLOT_EDIT, item_config.getItemFromConfig("items.choosable.main.edit"));
	}

	@Override
	public ChoosableMenu getNextChoosable(int choose) {
		switch (choose) {
			case SLOT_CREATE:
				return new CreateNewMenu(this.getPlayer(), item_config, this);
				
			case SLOT_EDIT:
				return new EditSomethingMenu(this.getPlayer(), item_config, this);
		}
		return null;
	}

	@Override
	public ConfigInteractiveMenu getFinalStep(int choose) {
		return null;
	}

	@Override
	public boolean isLastChoose(int choose) {
		return false;
	}

	@Override
	protected boolean updateBeforeBack() {return false;}

	
	
}
