package me.davethecamper.cashshop.listener;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.ItemGenerator;
import me.davethecamper.cashshop.events.PreOpenCashInventoryEvent;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static me.davethecamper.cashshop.CashShop.*;

public class RemoveUnecessaryCheckoutButtonsListener implements Listener {

    private static final ItemStack AIR_BUTTON = new ItemStack(Material.AIR);

    private static final ItemStack BACKGROUND_BUTTON = ItemGenerator.getItemStack("BLACK_STAINED_GLASS_PANE", "§r");


    @EventHandler
    public void onCheckoutMenu(PreOpenCashInventoryEvent e) {
        if (!e.getMenu().getId().equals(CHECKOUT_MENU)) return;

        CashPlayer cashPlayer = CashShop.getInstance().getCashPlayer(e.getUniqueId());

        if (!cashPlayer.isCashTransaction()) {

            e.getMenu().replaceIndicators(DISCOUNT_BUTTON, BACKGROUND_BUTTON, "ignored");
        }

        SellProductMenu currentProduct = cashPlayer.getCurrentProduct();

        if (currentProduct.getDelayToBuy() != 0) {
            e.getMenu().replaceIndicators(ADD_AMOUNT_1_BUTTON, AIR_BUTTON, "ignored");
            e.getMenu().replaceIndicators(ADD_AMOUNT_5_BUTTON, BACKGROUND_BUTTON, "ignored");
            e.getMenu().replaceIndicators(ADD_AMOUNT_10_BUTTON, AIR_BUTTON, "ignored");

            e.getMenu().replaceIndicators(REMOVE_AMOUNT_1_BUTTON, AIR_BUTTON, "ignored");
            e.getMenu().replaceIndicators(REMOVE_AMOUNT_5_BUTTON, BACKGROUND_BUTTON, "ignored");
            e.getMenu().replaceIndicators(REMOVE_AMOUNT_10_BUTTON, AIR_BUTTON, "ignored");

        }
    }

}
