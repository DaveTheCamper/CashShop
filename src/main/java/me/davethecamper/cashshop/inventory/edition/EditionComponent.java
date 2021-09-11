package me.davethecamper.cashshop.inventory.edition;

import org.bukkit.inventory.ItemStack;

public class EditionComponent {

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
	
	
	public EditionComponentType getType() {return type;}
	
	public String getName() {return name;}
	
	public ItemStack getItemStack() {return item;}

	
	public void setType(EditionComponentType type) {this.type = type;}

	public void setName(String name) {this.name = name;}
	
	

}
