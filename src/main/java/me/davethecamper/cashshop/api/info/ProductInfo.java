package me.davethecamper.cashshop.api.info;

public class ProductInfo {
	
	public ProductInfo(double amount, String product_name, String currency) {
		this.amount = amount;
		this.product_name = product_name;
		this.currency = currency;
	}

	
	private String product_name, currency;
	
	private double amount;

	
	
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
