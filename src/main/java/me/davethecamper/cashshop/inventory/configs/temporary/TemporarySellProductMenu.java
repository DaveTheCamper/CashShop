package me.davethecamper.cashshop.inventory.configs.temporary;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;
import java.util.function.Consumer;

public class TemporarySellProductMenu extends SellProductMenu {
	
	private static final long serialVersionUID = -3241739116571529794L;


	public TemporarySellProductMenu(ItemMenuProperties item_properties, ProductConfig product, double updated_value) {
		this(item_properties, product, updated_value, null);
	}

	public TemporarySellProductMenu(ItemMenuProperties item_properties, ProductConfig product, double updated_value, Consumer<TemporarySellProductMenu> callback) {
		this(item_properties, product, updated_value, 0, callback);
	}

	public TemporarySellProductMenu(ItemMenuProperties item_properties, ProductConfig product, double updated_value, long delay, Consumer<TemporarySellProductMenu> callback) {
		super(UUID.randomUUID().toString(), CashShop.getInstance().getMessagesConfig(), null, item_properties, product, updated_value, delay);
		
		this.callback = callback;
	}
	
	
	private final Consumer<TemporarySellProductMenu> callback;
	
	public boolean hasIntentionToSave = false;

	public boolean hasIntentionToDelete = false;
	
	
	@Override
	public void startEditing(UUID player) {
		this.setPrevious(((CashShop) Bukkit.getPluginManager().getPlugin("CashShop")).getPlayerEditorCurrentInventory(player));
		
		super.startEditing(player);
	}
	
	@Override
	public FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("nonSaveObject", true);
		hasIntentionToSave = true;
		
		return fc;
	}

	@Override
	public void delete() {
		hasIntentionToDelete = true;

		super.delete();
	}

	@Override
	protected void backOneInventory(UUID player, ReciclableMenu menu) {
		super.backOneInventory(player, getPreviousMenu());
		
		callback.accept(this);
	}
	

}
