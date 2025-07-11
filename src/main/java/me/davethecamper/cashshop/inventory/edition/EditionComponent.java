package me.davethecamper.cashshop.inventory.edition;

import lombok.Data;
import me.davethecamper.cashshop.events.CashMenuInventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Data
public class EditionComponent implements Cloneable {

	public EditionComponent(EditionComponentType type, String name, ItemStack item) {
		this.type = type;
		this.item = item;
		this.name = name;
	}
	
	public EditionComponent(EditionComponentType type, String name) {
		this.type = type;
		this.name = name;
	}

	private EditionComponentType type;
	
	private String name;
	
	private ItemStack item;

    private Consumer<CashMenuInventoryClickEvent> consumer;

	
	public ItemStack getItemStack() {return item;}
	
	public void setItemStack(ItemStack item) {this.item = item;}
	
	@Override
	public EditionComponent clone() {
		return new EditionComponent(type, name, item != null ? item.clone() : null);
	}
	
	

}
