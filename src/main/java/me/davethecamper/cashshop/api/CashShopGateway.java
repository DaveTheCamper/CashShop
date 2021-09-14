package me.davethecamper.cashshop.api;

import org.bukkit.configuration.file.FileConfiguration;

import me.davethecamper.cashshop.api.info.InitializationResult;
import me.davethecamper.cashshop.api.info.PlayerInfo;
import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;

public interface CashShopGateway {
	
	/** 
	 * <p>Initialization of your API, credentials can be wrong, make sure verify them
	* This method can return 4 results:</p>
	* 
	* <br>INVALID_CREDENTIALS: Some credential is invalidated by API;
	* <br>INVALID_CURRENCY: Current currency is not supported by API;
	* <br>OFFLINE_API: For some reason, API is offline or in maintenance;
	* <br>INITIALIZATED: API is valid and can be used.
	* 
	* */
	
	public InitializationResult init(FileConfiguration yaml, String currency);
	
	
	/** 
	 * <p>The name of your API</p>
	 */
	public String getIdentifier();
	
	/** 
	 * <p>The name of your API with colors</p>
	 */
	public String getColoredDisplayName();
	
	
	/**
	 *  <p>Here you will evaluate if your API support some currency</p>
	 *  <p><br>Example: Paypal support all currencies but Picpay is only BRL</br></p>
	 */
	public boolean isValidCurrency(String currency);
	
	
	/**
	 *  <p>Here you will set in your yaml what your API need to connect
	 *  you don't need to save, just set the default camps</p>
	 *  
	 *  <p><br>Example: private_key, security_key, secret_id</br></p>
	 */ 
	public void generateConfigurationFile(FileConfiguration yaml);

	
	/**
	 *  <p>Generation of transaction with productinfo and payerinfo, make sure return
	 *  correct transaction link and transaction id</p>
	 */
	public TransactionInfo generateTransaction(ProductInfo product, PlayerInfo player);	
	
	
	/**
	 *  <p>After a transation is created, core plugin will verify the token
	 *  and give player cash after approved</p>
	 */
	public TransactionResponse verifyTransaction(String token);

}
