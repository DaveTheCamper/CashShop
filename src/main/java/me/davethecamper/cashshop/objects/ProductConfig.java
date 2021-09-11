package me.davethecamper.cashshop.objects;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class ProductConfig implements Cloneable {
	
	
	public ProductConfig() {
		this(new ArrayList<>(), new ArrayList<>());
	}
	
	public ProductConfig(ArrayList<ItemStack> items,  ArrayList<String> commands) {
		this.items = new ArrayList<>(items);
		this.commands = new ArrayList<>(commands);
	}
	
	private ArrayList<ItemStack> items;
	
	private ArrayList<String> commands;

	
	
	public ArrayList<ItemStack> getItems() {return items;}

	public ArrayList<String> getCommands() {return commands;}
	

	public void setItems(ArrayList<ItemStack> items) {this.items = new ArrayList<>(items);}

	public void setCommands(ArrayList<String> commands) {this.commands = new ArrayList<>(commands);}

	
	public void updateItems(ArrayList<ItemStack> new_items) {
		items.clear();
		items.addAll(new_items);
	}
	
	@Override
	public ProductConfig clone() {
		return new ProductConfig(new ArrayList<ItemStack>(items), new ArrayList<String>(commands));
	}

}
