package me.davethecamper.cashshop.inventory.configs.temporary;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.configuration.file.FileConfiguration;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.objects.ItemMenuProperties;
import me.davethecamper.cashshop.objects.ProductConfig;

public class TemporarySellProductMenu extends SellProductMenu {
	
	private static final long serialVersionUID = -3241739116571529794L;
	

	public TemporarySellProductMenu(ItemMenuProperties item_properties, ProductConfig product, int updated_value, Consumer<TemporarySellProductMenu> callback) {
		this(item_properties, product, updated_value, 0, callback);
	}

	public TemporarySellProductMenu(ItemMenuProperties item_properties, ProductConfig product, int updated_value, long delay, Consumer<TemporarySellProductMenu> callback) {
		super("temporary_product", CashShop.getInstance().getMessagesConfig(), null, item_properties, product, updated_value, delay);
		
		this.callback = callback;
	}
	
	
	private Consumer<TemporarySellProductMenu> callback;
	
	@Override
	public FileConfiguration saveHandler(FileConfiguration fc) {
		fc.set("nonSaveObject", true);
		
		return fc;
	}
	
	@Override
	protected void backOneInventory(UUID player, ReciclableMenu menu) {
		callback.accept(this);
	}
	

}
