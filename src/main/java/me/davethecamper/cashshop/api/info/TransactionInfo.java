package me.davethecamper.cashshop.api.info;

import me.davethecamper.cashshop.api.CashShopGateway;

public class TransactionInfo {
	
	public TransactionInfo(String link, String transaction_token) {
		this("null", "null", "null", 0, 0, System.currentTimeMillis(), link, transaction_token);
	}

	public TransactionInfo(String player, CashShopGateway gateway, String cupom, int cash, double real_money, long creation_date, String link, String transaction_token) {
		this(player, gateway != null ? gateway.getIdentifier() : "null", cupom, cash, real_money, creation_date, link, transaction_token);
	}
	
	public TransactionInfo(String player, String gateway, String cupom, int cash, double real_money, long creation_date, String link, String transaction_token) {
		this.player = player;
		this.link = link;
		this.transaction_token = transaction_token;
		this.api_caller = gateway;
		this.status = TransactionResponse.WAITING_FOR_PAYMENT;
		this.cash = cash;
		this.creation_date = creation_date;
		this.cupom = cupom;
		this.real_money = real_money;
	}
	
	private String link, transaction_token, api_caller, player, cupom;
	
	private int cash;
	
	private double real_money;
	
	private long creation_date;
	
	private TransactionResponse status;
	
	
	
	public int getCash() {
		return this.cash;
	}
	
	
	public double getRealMoneySpent() {
		return this.real_money;
	}
	
	
	public long getCreationDate() {
		return this.creation_date;
	}
	
	
	public String getLink() {
		return link;
	}
	
	public String getCupom() {
		return cupom;
	}
	
	public String getPlayer() {
		return this.player;
	}

	public String getTransactionToken() {
		return transaction_token;
	}
	
	public String getGatewayCaller() {
		return api_caller;
	}
	
	
	public TransactionResponse getStatus() {
		return this.status;
	}

	
	public void updateTransactionStatus(TransactionResponse status) {
		this.status = status;
	}

}
