package me.davethecamper.cashshop.api.services;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONObject;

import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.http.serializer.Json;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnitRequest;

import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.InitializationResult;
import me.davethecamper.cashshop.api.info.PlayerInfo;
import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;


public class CashShopPaypal implements CashShopGateway {
	
	private final String PATH_SECRET = "config.secret";
	private final String PATH_CLIENT = "config.client_id";
	
	private Credentials credentials;
	
	
	private HashMap<String, String> owner_info = new HashMap<>();
	


	public InitializationResult init(FileConfiguration yaml, String currency) {
		
		if (!this.isValidCurrency(currency)) {
			return InitializationResult.INVALID_CURRENCY;
		}
		
		owner_info.put(PATH_CLIENT, yaml.getString(PATH_CLIENT));
		owner_info.put(PATH_SECRET, yaml.getString(PATH_SECRET));
		
		try {
			credentials = new Credentials(getInfo(PATH_CLIENT), getInfo(PATH_SECRET));
			testCredentials();
		} catch (Exception e) {
			e.printStackTrace();
			return InitializationResult.INVALID_CREDENTIALS;
		}
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		df = new DecimalFormat("0.00", otherSymbols);
		return InitializationResult.INITIALIZATED;
	}
	
	
	private String getInfo(String s) {
		return owner_info.get(s);
	}
	
	public String getIdentifier() {return "PayPal";}
	
	
	public boolean isValidCurrency(String currency) {
		return true;
	}
	
	
	private DecimalFormat df;
	
	public TransactionInfo generateTransaction(ProductInfo product, PlayerInfo player) {
		Order order = null;
		// Construct a request object and set desired parameters
		// Here, OrdersCreateRequest() creates a POST request to /v2/checkout/orders
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.checkoutPaymentIntent("CAPTURE");
		List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
		purchaseUnits.add(new PurchaseUnitRequest().amountWithBreakdown(new AmountWithBreakdown().currencyCode(product.getCurrency()).value(df.format(product.getAmount()))));
		orderRequest.purchaseUnits(purchaseUnits);
		OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

		try {
			// Call API with your client and get a response for your call
			HttpResponse<Order> response = credentials.getPayPalHttpClient().execute(request);

			// If call returns body in response, you can get the de-serialized version by
			// calling result() on the response
			order = response.result();
			System.out.println("Order ID: " + order.id());
			order.links().forEach(link -> System.out.println(link.rel() + " => " + link.method() + ":" + link.href()));
		} catch (IOException ioe) {
			if (ioe instanceof HttpException) {
				// Something went wrong server-side
				HttpException he = (HttpException) ioe;
				System.out.println(he.getMessage());
				he.headers().forEach(x -> System.out.println(x + " :" + he.headers().header(x)));
			} else {
				// Something went wrong client-side
			}
			return null;
		}
		
		try {
			List<LinkDescription> list = order.links().stream().filter(link -> link.rel().equals("approve")).collect(toList());
			TransactionInfo ti = new TransactionInfo(list.get(0).href(), order.id());
			return ti;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TransactionResponse verifyTransaction(String token) {
		OrdersGetRequest request = new OrdersGetRequest(token);
	    //3. Call PayPal to get the transaction
	    HttpResponse<Order> response;
		try {
			response = credentials.getPayPalHttpClient().execute(request);
		    //4. Save the transaction in your database. Implement logic to save transaction to your database for future reference.
		    JSONObject json = new JSONObject(new Json().serialize(response.result()));
		    switch (json.getString("status")) {
			    case "COMPLETED":
			    	return TransactionResponse.APPROVED;
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return TransactionResponse.WAITING_FOR_PAYMENT;
	}

	public void generateConfigurationFile(FileConfiguration yaml) {
		yaml.set(PATH_SECRET, "default");
		yaml.set(PATH_CLIENT, "default");
	}

	
	
	
	
	// Paypal methods
	
	private void testCredentials() {
		generateTransaction(new ProductInfo(1, "USD", "Testing Credentials"), null);
	}

}
