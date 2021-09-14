package me.davethecamper.cashshop;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import me.davethecamper.cashshop.events.ChangeInventoryEvent;
import me.davethecamper.cashshop.events.WaitingChatEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.choosers.ChoosableMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.configs.ValuebleItemMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.player.CashPlayer;


public class EventsCatcher implements Listener {

	public EventsCatcher(CashShop main) {
		this.main = main;
	}
	
	private CashShop main;
	
	private HashMap<UUID, Boolean> is_editing_editor = new HashMap<>();
	
	private HashMap<UUID, Boolean> is_using_menus = new HashMap<>();
	
	
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		UUID uuid = e.getWhoClicked().getUniqueId();
		
		if (main.haveInventoryOpen(uuid) && isEditingEditor(uuid)) {
			if (e.getClickedInventory() == null) return;
			e.setCancelled(true);
			
			ReciclableMenu rm = main.getPlayerCurrentInventory(uuid);
			
			if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
				rm.inventoryClick(uuid, e.getSlot(), e.getHotbarButton(), e.getAction());
			} else {
				rm.inventoryPlayerClickHandler(e.getSlot(), e.getCurrentItem());
			}

			if (rm instanceof ChoosableMenu) {
				ChoosableMenu cm = (ChoosableMenu) rm;
				ReciclableMenu next = null;
				
				if (cm.isLastChoose(e.getSlot())) {
					next = cm.getFinalStep(e.getSlot());
					
				} else {
					next = cm.getNextChoosable(e.getSlot());
				}
				
				if (next == null) {return;}
				

				main.changePlayerInventory(uuid, next);
				next.setPlayer(uuid);
				
				e.getWhoClicked().openInventory(next.getInventory());
				
			}
		} else if (main.getNormalPlayerInventory(uuid).haveAnyCurrentInventory() && isUsingMenus(uuid)) {
			if (e.getClickedInventory() == null) return;
			e.setCancelled(true);

			if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
				CashPlayer cp = main.getNormalPlayerInventory(uuid);
				EditionComponent cc = cp.getCurrentComponent(e.getSlot());
				
				if (cc != null) {
					switch (cc.getType()) {
						case BUY_PRODUCT:
							SellProductMenu current_product = CashShop.getInstance().getProduct(cc.getName());
							cp.updateCurrentProduct(current_product);
							break;
							
						case CATEGORY:
							ConfigInteractiveMenu cim = CashShop.getInstance().getCategoriesManager().getCategorie(cc.getName());
							cp.updateCurrentInventory(cim);
							break;
							
						case COMBO:
							break;
							
						case STATIC:
							switch (cc.getName()) {
								case CashShop.BACK_BUTTON:
									cp.backInventory();
									break;
									
								case CashShop.ADD_AMOUNT_1_BUTTON:
								case CashShop.ADD_AMOUNT_5_BUTTON:
								case CashShop.ADD_AMOUNT_10_BUTTON:
									cp.addProductAmount(((ValuebleItemMenu) CashShop.getInstance().getStaticItem(cc.getName())).getValueInCash());
									break;

								case CashShop.REMOVE_AMOUNT_1_BUTTON:
								case CashShop.REMOVE_AMOUNT_5_BUTTON:
								case CashShop.REMOVE_AMOUNT_10_BUTTON:
									cp.removeProductAmount(((ValuebleItemMenu) CashShop.getInstance().getStaticItem(cc.getName())).getValueInCash());
									break;
									
								case CashShop.CHECKOUT_MENU:
									cp.openBuyCashMenu();
									break;
									
								case CashShop.GATEWAYS_MENU:
									if (cp.getProductAmount() >= main.configuration.getInt("currency.minimum_spent")) {
										cp.openGatewayMenu();
									} else {
										e.getWhoClicked().sendMessage(main.messages.getString("payment.error.min_value").replaceAll("@value", main.configuration.getInt("currency.minimum_spent") + " " + main.configuration.getString("currency.code")));
									}
									break;
									
								case CashShop.TRANSACTION_MENU:
									cp.openTransactions();
									break;
									
								case CashShop.DISCOUNT_BUTTON:
									cp.updateDiscount();
									break;
									
								case CashShop.GIFT_NAME_BUTTON:
									cp.updateGift();
									break;
									
									
							}
							break;
							
						case DISPLAY_ITEM:
							switch (cc.getName()) {
								case CashShop.REPLACE_ITEM_SELLING_BUTTON:
									cp.updateProductAmount();
									break;
							}
							break;
							
						default:
							break;
					}
				}
				
				switch (cp.getCurrentInteractiveMenu().getId()) {
					case CashShop.GATEWAYS_MENU:
						if (e.getCurrentItem() != null) {
							cp.selectGateway(e.getSlot());
						}
						break;
				}
			}
			
		}
	}
	
	@EventHandler
	public void onChangeInventoryEvent(ChangeInventoryEvent e) {
		main.changePlayerInventory(e.getUuid(), e.getReciclableMenu());
	}
	
	@EventHandler
	public void onWaitingChat(WaitingChatEvent e) {
		CashPlayer cp = main.getNormalPlayerInventory(e.getWaitingForChat().getPlayer());
		switch (e.getWaitingForChat().getVarName()) {
			case "set_gift":
				cp.setGiftFor((String) e.getWaitingForChat().getResult());
				break;
				
			case "set_discount":
				String cupom = ((String) e.getWaitingForChat().getResult()).toLowerCase();
				if (main.getCupomManager().isValid(cupom)) {
					cp.setCupom(cupom);
					cp.updateCurrentProduct();
					return;
				} else {
					Bukkit.getPlayer(e.getWaitingForChat().getPlayer()).sendMessage(main.messages.getString("chat.invalid_cupom"));
				}
				break;
				
			case "set_amount":
				Integer amount = (Integer) e.getWaitingForChat().getResult();
				if (amount > 0) {
					cp.setProductAmount(amount);
					cp.updateCurrentProduct();
				} else {
					Bukkit.getPlayer(e.getWaitingForChat().getPlayer()).sendMessage(main.messages.getString("chat.invalid_amount"));
				}
				break;
				
			default:
				return;
		}
		cp.openCurrentInventory();
	}
	

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		
		if (main.haveInventoryOpen(uuid)) {
			if (e.getInventory().equals(main.getPlayerCurrentInventory(uuid).getInventory())) {
				setEditingEditor(uuid, false);
			}
		}
		
		if (main.getNormalPlayerInventory(uuid).haveAnyCurrentInventory()) {
			if (e.getInventory().equals(main.getNormalPlayerInventory(uuid).getCurrentInventory())) {
				setUsingMenus(uuid, false);
			}
		}
	}
	

	@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		
		if (main.haveInventoryOpen(uuid)) {
			if (e.getInventory().equals(main.getPlayerCurrentInventory(uuid).getInventory())) {
				setEditingEditor(uuid, true);
			}
		}

		if (main.getNormalPlayerInventory(uuid).haveAnyCurrentInventory()) {
			if (e.getInventory().equals(main.getNormalPlayerInventory(uuid).getCurrentInventory())) {
				setUsingMenus(uuid, true);
			}
		}
	}
	
	
	private boolean isEditingEditor(UUID uuid) {return is_editing_editor.containsKey(uuid) ? is_editing_editor.get(uuid) : false;}
	
	private void setEditingEditor(UUID uuid, Boolean arg) { is_editing_editor.put(uuid, arg);}
	

	private boolean isUsingMenus(UUID uuid) {return is_using_menus.containsKey(uuid) ? is_using_menus.get(uuid) : false;}
	
	private void setUsingMenus(UUID uuid, Boolean arg) { is_using_menus.put(uuid, arg);}
	
	

}
