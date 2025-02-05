package me.davethecamper.cashshop.listener;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.events.CashPlayerProductUpdateEvent;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ProductUpdateListener implements Listener {

    @EventHandler
    public void onProductUpdate(CashPlayerProductUpdateEvent e) {
        SellProductMenu sellingProduct = e.getSellingProduct();

        if (sellingProduct.isMoney()) return;

        if (sellingProduct.getId().equals(CashShop.CHECKOUT_CASH_BUTTON)) return;

        CashPlayer cashPlayer = e.getCashPlayer();

        if (sellingProduct.getValueInCash() > cashPlayer.getCash()) {
            e.setCancelled(true);

            double amount = (sellingProduct.getValueInCash() - cashPlayer.getCash()) / CashShop.getInstance().getCurrencyCashValue();

            cashPlayer.openBuyCashMenu();
            cashPlayer.setProductAmount(0);
            cashPlayer.addProductAmount((int) Math.ceil(Math.max(1, amount)));
        }
    }

}
