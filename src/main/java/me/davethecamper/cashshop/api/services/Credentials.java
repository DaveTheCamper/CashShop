package me.davethecamper.cashshop.api.services;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;

public class Credentials {
	
	public Credentials(String client_id, String secret) {
		this.environment = new PayPalEnvironment.Live(client_id, secret);
		this.client = new PayPalHttpClient(environment);
	}
	
    // Creating a sandbox environment
    private PayPalEnvironment environment;
    
    // Creating a client for the environment
    private PayPalHttpClient client;
    
    public PayPalHttpClient getPayPalHttpClient() {
    	return client;
    }
}
