package me.davethecamper.cashshop.api;

import java.util.TreeMap;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.IdentificableMenu;

public class CategoriesManager {
	
	public CategoriesManager(CashShop main) {
		this.main = main;
	}
	
	private CashShop main;
	
	
	public boolean isMainCategorie(IdentificableMenu menu) {
		return menu.getId().equals(main.LABEL_MAIN);
	}
	
	public boolean isTransactionsCategorie(IdentificableMenu menu) {
		return menu.getId().equals(main.LABEL_TRANSACTIONS);
	}
	
	public boolean isCheckoutCategorie(IdentificableMenu menu) {
		return menu.getId().equals(main.LABEL_CHECKOUT);
	}
	
	public boolean isCombosCategorie(IdentificableMenu menu) {
		return menu.getId().equals(main.LABEL_COMBOS);
	}
	
	
	public ConfigInteractiveMenu getCategorie(String name) {
		return getSomethingFromTree(name, main.categories) != null ? getSomethingFromTree(name, main.categories).clone() : null;
	}
	
	public ConfigInteractiveMenu getMainCategorie() {
		return getCategorie(main.LABEL_MAIN) != null ? getCategorie(main.LABEL_MAIN).clone() : null;
	}
	
	public ConfigInteractiveMenu getTransactionsCategorie() {
		return getCategorie(main.LABEL_TRANSACTIONS) != null ? getCategorie(main.LABEL_TRANSACTIONS).clone() : null;
	}
	
	public ConfigInteractiveMenu getCheckoutCategorie() {
		return getCategorie(main.LABEL_CHECKOUT) != null ? getCategorie(main.LABEL_CHECKOUT).clone() : null;
	}
	
	public ConfigInteractiveMenu getCombosCategorie() {
		return getCategorie(main.LABEL_COMBOS) != null ? getCategorie(main.LABEL_COMBOS).clone() : null;
	}

	
	private <Z> Z getSomethingFromTree(String name, TreeMap<String, Z> tree) {
		return tree.get(name);
	}
}
