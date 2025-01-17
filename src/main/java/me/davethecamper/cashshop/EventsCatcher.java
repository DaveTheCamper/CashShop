package me.davethecamper.cashshop;

import me.davethecamper.cashshop.events.CashMenuInventoryClickEvent;
import me.davethecamper.cashshop.events.CashPlayerInventoryClickEvent;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import me.davethecamper.cashshop.events.WaitingChatEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.choosers.ChoosableMenu;
import me.davethecamper.cashshop.inventory.choosers.MainChooseMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.configs.ValuebleItemMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventsCatcher implements Listener {

	public EventsCatcher(CashShop main) {
		this.main = main;
	}
	
	private final CashShop main;
	
	private final HashMap<UUID, Boolean> isUsingEditor = new HashMap<>();
	
	private final HashMap<UUID, Boolean> isUsingMenus = new HashMap<>();

	private final Map<UUID, Long> lastAccessTime = new HashMap<>();
	
	
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		UUID uuid = e.getWhoClicked().getUniqueId();
		
		if (e.getClickedInventory() == null) return;

		if (main.haveEditorInventoryOpen(uuid) && isEditingEditor(uuid)) {
			executeEditorMenuClick(e, uuid);
		} else if (main.getNormalPlayerInventory(uuid).haveAnyCurrentInventory() && isUsingMenus(uuid)) {
			e.setCancelled(true);
			
			CashPlayer cp = main.getNormalPlayerInventory(uuid);
			if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
				if (lastAccessTime.getOrDefault(uuid, 0L) > System.currentTimeMillis()) return;

				lastAccessTime.put(uuid, System.currentTimeMillis() + 100);

				CashMenuInventoryClickEvent event = new CashMenuInventoryClickEvent(uuid, cp.getCurrentInteractiveMenu(), e);

				if (cp.getCurrentInteractiveMenu().getComponentBySlot(e.getSlot()) != null) {
					if (cp.getCurrentInteractiveMenu().getComponentBySlot(e.getSlot()).getConsumer() != null) {
						ConfigInteractiveMenu curr = cp.getCurrentInteractiveMenu();
						
						cp.getCurrentInteractiveMenu().getComponentBySlot(e.getSlot()).getConsumer().accept(event);	
						
						if (curr != cp.getCurrentInteractiveMenu()) return;
					}
				}
				
				Bukkit.getPluginManager().callEvent(event);
				
				if (event.isCancelled()) {
					return;
				}
				
				e.setCancelled(event.isCancelClick());				
			} else {
				CashPlayerInventoryClickEvent event = new CashPlayerInventoryClickEvent(uuid, cp.getCurrentInteractiveMenu(), e);
				ConfigInteractiveMenu curr = cp.getCurrentInteractiveMenu();
				
				Bukkit.getPluginManager().callEvent(event);
				
				e.setCancelled(event.isCancelClick());
				
				if (curr != cp.getCurrentInteractiveMenu()) return;
			}
			

			if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
				EditionComponent cc = cp.getCurrentComponent(e.getSlot());
				
				if (cc != null) {
					switch (cc.getType()) {
						case BUY_PRODUCT:
							SellProductMenu current_product = CashShop.getInstance().getProduct(cc.getName());
							if (current_product != null && cp.canBuyThisItem(current_product)) {
								cp.updateCurrentProduct(current_product);
							}
							break;
							
						case CATEGORY:
							ConfigInteractiveMenu cim = CashShop.getInstance().getCategoriesManager().getCategorie(cc.getName());
							cp.updateCurrentInventory(cim);
							break;

                        case STATIC:
							switch (cc.getName()) {
								case CashShop.BACK_BUTTON:
									cp.backInventory();
									break;
									
								case CashShop.ADD_AMOUNT_1_BUTTON:
								case CashShop.ADD_AMOUNT_5_BUTTON:
								case CashShop.ADD_AMOUNT_10_BUTTON:
									cp.addProductAmount((int) ((ValuebleItemMenu) CashShop.getInstance().getStaticItem(cc.getName())).getValueInCash());
									break;

								case CashShop.REMOVE_AMOUNT_1_BUTTON:
								case CashShop.REMOVE_AMOUNT_5_BUTTON:
								case CashShop.REMOVE_AMOUNT_10_BUTTON:
									cp.removeProductAmount((int) ((ValuebleItemMenu) CashShop.getInstance().getStaticItem(cc.getName())).getValueInCash());
									break;
									
								case CashShop.CHECKOUT_MENU:
									cp.openBuyCashMenu();
									break;
									
								case CashShop.GATEWAYS_MENU:
									double value = ((double) cp.getProductAmount()) - (((double) cp.getProductAmount())*(CashShop.getInstance().getCupomManager().getDiscount(cp.getCupom())/100));
									if (value >= main.configuration.getInt("currency.minimum_spent")) {
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
									
								case CashShop.CONFIRM_BUY_BUTTON:
									cp.buyCurrentProduct();
									break;
									
									
							}
							break;
							
						case DISPLAY_ITEM:
							switch (cc.getName()) {
								case CashShop.REPLACE_ITEM_SELLING_BUTTON:
									if (cp.getCurrentProduct().getDelayToBuy() > 0) return;
									
									cp.updateProductAmount();
									break;
							}
							break;
							
						default:
							break;
					}
				}
				
				if (cp.getCurrentInteractiveMenu() != null) {
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
	}

	private void executeEditorMenuClick(InventoryClickEvent e, UUID uuid) {
		e.setCancelled(true);

		ReciclableMenu rm = main.getPlayerEditorCurrentInventory(uuid);

		if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
			rm.inventoryClick(uuid, e.getSlot(), e.getHotbarButton(), e.getAction());
		} else {
			rm.inventoryPlayerClickHandler(e.getSlot(), e.getCurrentItem());
		}

		if (rm instanceof ChoosableMenu) {
			ChoosableMenu cm = (ChoosableMenu) rm;
			ReciclableMenu next;

			if (cm.isLastChoose(e.getSlot())) {
				next = cm.getFinalStep(e.getSlot());

			} else {
				next = cm.getNextChoosable(e.getSlot());
			}

			if (next == null) {
				return;
			}


			main.changePlayerEditorInventory(uuid, next);
			next.setPlayer(uuid);

			e.getWhoClicked().openInventory(next.getInventory());
		}
	}

	@EventHandler
	public void onChangeInventoryEvent(ChangeEditorInventoryEvent e) {
		if (e.getReciclableMenu() == null) {
			main.changePlayerEditorInventory(e.getUuid(), new MainChooseMenu(e.getUuid(), CashShop.getInstance().getMessagesConfig()));
			return;
		}
		
		main.changePlayerEditorInventory(e.getUuid(), e.getReciclableMenu());
	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		UUID uuid = e.getWhoClicked().getUniqueId();
		if (main.haveEditorInventoryOpen(uuid) && isEditingEditor(uuid)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onWaitingChat(WaitingChatEvent e) {
		CashPlayer cp = main.getNormalPlayerInventory(e.getWaitingForChat().getPlayer());
		switch (e.getWaitingForChat().getVarName()) {
			case "set_gift":
				cp.setKeepBackHistory(true);
				if (Bukkit.getPlayer((String) e.getWaitingForChat().getResult()) != null) {
					cp.setGiftFor(Bukkit.getPlayer((String) e.getWaitingForChat().getResult()).getName());
				} else {
					cp.setGiftFor("...");
				}
				break;
				
			case "set_discount":
				cp.setKeepBackHistory(true);
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
				cp.setKeepBackHistory(true);
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
		
		if (main.haveEditorInventoryOpen(uuid)) {
			if (main.getPlayerEditorCurrentInventory(uuid).getInventory() == null || e.getInventory().equals(main.getPlayerEditorCurrentInventory(uuid).getInventory())) {
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
		
		if (main.haveEditorInventoryOpen(uuid)) {
			if (main.getPlayerEditorCurrentInventory(uuid).getInventory() != null && e.getInventory().equals(main.getPlayerEditorCurrentInventory(uuid).getInventory())) {
				setEditingEditor(uuid, true);
			}
		}

		if (main.getNormalPlayerInventory(uuid).haveAnyCurrentInventory()) {
			if (e.getInventory().equals(main.getNormalPlayerInventory(uuid).getCurrentInventory())) {
				setUsingMenus(uuid, true);
			}
		}
	}
	
	
	private boolean isEditingEditor(UUID uuid) {return isUsingEditor.getOrDefault(uuid, false);}
	
	private void setEditingEditor(UUID uuid, Boolean arg) { isUsingEditor.put(uuid, arg);}
	

	private boolean isUsingMenus(UUID uuid) {return isUsingMenus.getOrDefault(uuid, false);}
	
	private void setUsingMenus(UUID uuid, Boolean arg) { 
		CashPlayer cp = CashShop.getInstance().getCashPlayer(uuid);
		
		isUsingMenus.put(uuid, arg);
		cp.setUsingMenus(arg);
		
		if (!arg) {
			
			if (!cp.isUpdating() && cp.isRunningUpdater()) {
				cp.getUpdaterRunnable().cancel();
			}
		}
	}
	
	

}
