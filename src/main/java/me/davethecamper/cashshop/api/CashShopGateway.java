package me.davethecamper.cashshop.api;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.player.CashPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.davethecamper.cashshop.api.info.InitializationResult;
import me.davethecamper.cashshop.api.info.PlayerInfo;
import me.davethecamper.cashshop.api.info.ProductInfo;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;
import org.bukkit.entity.Player;

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
	
	InitializationResult init(FileConfiguration yaml, String currency);
	
	
	/** 
	 * <p>The name of your API</p>
	 */
	String getIdentifier();
	
	/** 
	 * <p>The name of your API with colors</p>
	 */
	String getColoredDisplayName();
	
	
	/**
	 *  <p>Here you will evaluate if your API support some currency</p>
	 *  <p><br>Example: Paypal support all currencies but Picpay is only BRL</br></p>
	 */
	boolean isValidCurrency(String currency);
	
	
	/**
	 *  <p>Here you will set in your yaml what your API need to connect
	 *  you don't need to save, just set the default camps</p>
	 *  
	 *  <p><br>Example: private_key, security_key, secret_id</br></p>
	 */ 
	void generateConfigurationFile(FileConfiguration yaml);

	
	/**
	 *  <p>Generation of transaction with productinfo and payerinfo, make sure return
	 *  correct transaction link and transaction id</p>
	 */
	TransactionInfo generateTransaction(ProductInfo product, PlayerInfo player);
	
	
	/**
	 *  <p>After a transation is created, core plugin will verify the token
	 *  and give player cash after approved</p>
	 */
	TransactionResponse verifyTransaction(String token);

	default int getGracePeriodDays() {
		return 0;
	}

	default long getMaxTransactionWaiting() {return 3600L*24L;}

	default void sendLink(CashPlayer player, TransactionInfo transactionInfo) {
		Player p = Bukkit.getPlayer(player.getUniqueId());
		ConfigManager messages = CashShop.getInstance().getMessagesConfig();
		p.sendMessage(messages.getString("payment.info"));

		TextComponent link = new TextComponent(messages.getString("payment.click"));
		link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messages.getString("payment.hover")).create()));
		link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, transactionInfo.getLink()));

		TextComponent all = new TextComponent("");
		String[] split = messages.getString("payment.link").split("@link");
		for (int i = 0; i < split.length; i++) {
			TextComponent info = new TextComponent(split[i]);

			if (i == split.length-1) {
				all.addExtra(info);
				if (split.length == 1) {
					all.addExtra(link);
				}
			} else {
				all.addExtra(info);
				all.addExtra(link);
			}
		}

		p.spigot().sendMessage(all);
	}

}
