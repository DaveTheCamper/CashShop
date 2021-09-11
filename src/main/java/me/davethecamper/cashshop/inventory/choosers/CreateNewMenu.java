package me.davethecamper.cashshop.inventory.choosers;

import java.util.UUID;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;

public class CreateNewMenu extends ChoosableMenu {

	public CreateNewMenu(UUID uuid, ConfigManager item_config, ReciclableMenu previus) {
		super(uuid, item_config, previus);
		// TODO Auto-generated constructor stub
		load();
	}

	private final int BUTTON_CREATE = 11;
	private final int BUTTON_CLONE = 15;
	private final int BUTTON_BACK = 26;

	@Override
	public void reload() {
		this.unregisterAll();
		load();
	}
	
	private void load() {
		this.registerButton(BUTTON_CREATE, item_config.getItemFromConfig("items.choosable.create.creation"));
		
		this.registerButton(BUTTON_CLONE, item_config.getItemFromConfig("items.choosable.create.clone"));
		
		this.registerButton(BUTTON_BACK, item_config.getItemFromConfig("items.back"));
	}

	@Override
	public ChoosableMenu getNextChoosable(int choose) {
		switch (choose) {
			case BUTTON_CREATE:
				return new CCWhatMenu(this.getPlayer(), item_config, this, ChooseEditionMenu.CREATE_MODE);
				
			case BUTTON_CLONE:
				return new CCWhatMenu(this.getPlayer(), item_config, this, ChooseEditionMenu.CLONE_MODE);
				
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
