package me.davethecamper.cashshop.api.info;

import lombok.Getter;

@Getter
public class ProductInfo {
	
	public ProductInfo(int totalCash, double amount, String product_name, String currency) {
		this.amount = amount;
		this.productName = product_name;
		this.currency = currency;
		this.totalCash = totalCash;
	}
	
	private String productName, currency;
	
	private double amount;

	private int totalCash;
}
