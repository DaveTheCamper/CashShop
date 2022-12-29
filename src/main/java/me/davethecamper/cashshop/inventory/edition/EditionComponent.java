package me.davethecamper.cashshop.inventory.edition;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import me.davethecamper.cashshop.events.CashMenuInventoryClickEvent;

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
	
	
	public EditionComponentType getType() {return type;}
	
	public String getName() {return name;}
	
	public ItemStack getItemStack() {return item;}
	
	public Consumer<CashMenuInventoryClickEvent> getConsumer() {return consumer;}
	
	

	public void setConsumer(Consumer<CashMenuInventoryClickEvent> consumer) {this.consumer = consumer;}

	public void setType(EditionComponentType type) {this.type = type;}

	public void setName(String name) {this.name = name;}
	
	public void setItemStack(ItemStack item) {this.item = item;}
	
	@Override
	public EditionComponent clone() {
		return new EditionComponent(type, name, item != null ? item.clone() : item);
	}
	
	

}
