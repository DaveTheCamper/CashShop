package me.davethecamper.cashshop.api.info;

public class ProductInfo {
	
	public ProductInfo(int totalCash, double amount, String product_name, String currency) {
		this.amount = amount;
		this.product_name = product_name;
		this.currency = currency;
		this.totalCash = totalCash;
	}

	
	private String product_name, currency;
	
	private double amount;

	private int totalCash;

	
	
	public String getProductName() {
		return product_name;
	}
	
	public String getCurrency() {
		return currency;
	}

	public double getAmount() {
		return amount;
	}

}
