package me.davethecamper.cashshop.inventory.configs;

import lombok.Getter;
import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.inventory.WaitingForChat;
import me.davethecamper.cashshop.inventory.configs.temporary.TemporarySellProductMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class LoreEditorMenu extends SavableMenu {

	private static final long serialVersionUID = -8981541408500860615L;


	public LoreEditorMenu(String id, ConfigManager messagesConfig, TemporarySellProductMenu temporaryMenu, Consumer<LoreEditorMenu> consumer) {
		this(id, null, messagesConfig, temporaryMenu, temporaryMenu.getProduct().getCommands());

		this.consumer = consumer;
	}

	public LoreEditorMenu(String identificador, String what, ConfigManager item_config, ConfigItemMenu dad) {
		this(identificador, what, item_config, dad, dad.getItemProperties().getLore());
	}
	
	public LoreEditorMenu(String identificador, String what, ConfigManager item_config, ConfigItemMenu dad, ArrayList<String> list) {
		super(identificador, item_config, dad);
		
		this.dad = dad;
		this.lore = new ArrayList<>(list);
		this.what = what;
		
		load();
	}
	
	private String what;

	@Getter
	private ArrayList<String> lore;
	
	private ConfigItemMenu dad;

	@Getter
	private boolean intentionToSave;

	private Consumer<LoreEditorMenu> consumer;

	private void load() {
		this.changeIdentifierSlot(-1);
		
		int i = 0;
		for (; i < lore.size(); i++) {
			String s = item_config.getStringAsItemLore("items.lore.hint");
			this.registerItem("lore" + i, ItemGenerator.getItemStack("PAPER", "Line " + i, " §7» §f" + lore.get(i) + ";=;;=;" + s), i);
		}
		
		if (++i < 27) this.registerItem("ADD_NEW", item_config.getItemFromConfig("items.lore.new"), i-1);
	}
	
	@Override
	public void reload() {
		super.reload();
		load();
	}
	
	

	@Override
	public void saveHandler() {
		dad.changeLore(lore, what);
		this.backOneInventory(this.getPlayer(), dad);
	}
	

	@Override
	protected boolean updateBeforeBack() {return true;}
	
	
	private void updateInventory() {
		if (lore.size()+1 < 27) {
			this.removeItem("ADD_NEW");
		}
		load();
	}
	

	@Override
	public void changerVarHandler(String var_name, Object o) {
		switch (var_name) {
			case "ADD_NEW":
				lore.add(((String) o).replaceAll("&", "§"));
				break;
				
			default:
				int slot = Integer.parseInt(var_name.replaceAll("lore", ""));
				lore.set(slot, (String) o);
				break;
		}
		
		updateInventory();
	}

	@Override
	protected boolean inventoryClickHandler(UUID uuid, int clicked_slot, int slot_button, InventoryAction type) {
		if (clicked_slot < 27) {
			if (clicked_slot == lore.size()) {
				this.createVarChanger("ADD_NEW", WaitingForChat.Primitives.STRING);
				return true;
			} else {
				switch (type) {
					case HOTBAR_MOVE_AND_READD:
					case HOTBAR_SWAP:
						boolean changes = true;
						
						System.out.println(slot_button);
						switch (slot_button+1) {
							case 1:
								if (clicked_slot != 0) {
									String temp = lore.get(clicked_slot);
									lore.set(clicked_slot, lore.get(clicked_slot-1));
									lore.set(clicked_slot-1, temp);
								}
								break;
							case 2:
								if (clicked_slot != lore.size()-1) {
									String temp = lore.get(clicked_slot+1);
									lore.set(clicked_slot+1, lore.get(clicked_slot));
									lore.set(clicked_slot, temp);
								}
								break;
							case 9:
								lore.remove(clicked_slot);
								break;
								
							default:
								changes = false;
								break;
						}
						
						if (changes) updateInventory();
						return true;
						
						
					default:
						this.createVarChanger("lore" + clicked_slot, WaitingForChat.Primitives.STRING);
						return true;
				}
			}
		} else {
			switch (slots.get(clicked_slot)) {
				case SAVE_BUTTON:
					finishEditing(uuid, true);
					return true;
					
				case CANCEL_BUTTON:
					finishEditing(uuid, false);
					return true;
					
				default:
					return super.inventoryClickHandler(uuid, clicked_slot, slot_button, type);
			}
		}
	}

	protected void finishEditing(UUID uuid, boolean save) {
		this.intentionToSave = save;

		if (Objects.nonNull(consumer)) {
			consumer.accept(this);
			return;
		}

		if (save) {
			saveHandler();
			super.backOneInventory(uuid, dad);
			return;
		}

		super.backOneInventory(uuid, dad);
	}

	@Override
	protected FileConfiguration saveHandler(FileConfiguration fc) {return null;}

	public static LoreEditorMenu createTemporaryProductItems(TemporarySellProductMenu temporaryMenu, Consumer<LoreEditorMenu> consumer) {
		return new LoreEditorMenu(UUID.randomUUID().toString(), CashShop.getInstance().getMessagesConfig(), temporaryMenu, consumer);
	}
}
