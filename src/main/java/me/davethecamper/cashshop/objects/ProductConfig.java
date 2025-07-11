package me.davethecamper.cashshop.objects;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Data
@Builder
@ToString
public class ProductConfig implements Cloneable {

	public ProductConfig() {
		this(new ArrayList<>(), new ArrayList<>());
	}
	
	public ProductConfig(ArrayList<ItemStack> items,  ArrayList<String> commands) {
		this.items = new ArrayList<>(items);
		this.commands = new ArrayList<>(commands);
	}

	@Builder.Default
	private ArrayList<ItemStack> items = new ArrayList<>();

	@Builder.Default
	private ArrayList<String> commands = new ArrayList<>();


    public void setItems(ArrayList<ItemStack> items) {
		this.items = new ArrayList<>(items);
	}

	public void setCommands(ArrayList<String> commands) {
		this.commands = new ArrayList<>(commands);
	}

	
	public void updateItems(ArrayList<ItemStack> new_items) {
		items.clear();
		items.addAll(new_items);
	}
	
	@Override
	public ProductConfig clone() {
		return new ProductConfig(new ArrayList<>(items), new ArrayList<>(commands));
	}

}
