package me.davethecamper.cashshop.objects;

import java.util.HashMap;

public class Cupom {
	
	public Cupom(String name, double percentage, long expiration) {
		this(name, percentage, expiration, new HashMap<>());
	}
	

	public Cupom(String name, double percentage, long expiration, HashMap<String, Double> usages) {
		this.usages = usages;
		this.name = name;
		this.percetage = percentage;
		this.expiration = expiration;
	}
	
	private HashMap<String, Double> usages = new HashMap<>();
	
	
	private String name;
	
	private double percetage;
	
	private long expiration;
	
	

	public String getName() {return name;}

	public double getPercetage() {return percetage;}

	public long getExpiration() {return expiration;}
	
	public HashMap<String, Double> getUsages() {return this.usages;}
	
	public boolean isExpirated() {return System.currentTimeMillis() > this.getExpiration();}
	
	
	public void addUsage(String token, double amount) {usages.put(token, amount);}
	
	
	
	

}
