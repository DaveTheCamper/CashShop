package me.davethecamper.cashshop.listener;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.events.PreOpenCashInventoryEvent;
import me.davethecamper.cashshop.inventory.configs.ConfigItemMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConfirmBuyCustomizerListener implements Listener {

    private static final String CONFIRM_BUY = "confirm_buy";

    private static final DecimalFormat formatter = new DecimalFormat("#,##0");

    @EventHandler
    public void onPreOpen(PreOpenCashInventoryEvent e) {
        if (e.getMenu().getId().equals("checkout")) {
            updateButtons(e);
        }
    }

    private void updateButtons(PreOpenCashInventoryEvent e) {
        CashPlayer cashPlayer = CashShop.getInstance().getCashPlayer(e.getUniqueId());

        if (cashPlayer.isCashTransaction()) return;

        SellProductMenu currentProduct = cashPlayer.getCurrentProduct();

        if (currentProduct.isMoney()) return;

        e.getMenu().updateComponentDisplayItem(CONFIRM_BUY, createDisplayItem(cashPlayer));
    }

    private ItemStack createDisplayItem(CashPlayer cashPlayer) {
        ConfigItemMenu staticItem = CashShop.getInstance().getStaticItem(CONFIRM_BUY);
        SellProductMenu currentProduct = cashPlayer.getCurrentProduct();

        ItemStack itemStack = staticItem.getItemProperties().getItem().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();

        int current = cashPlayer.getCash(currentProduct.isAllowBonus());
        int price = (int) (currentProduct.getValueInCash() * cashPlayer.getProductAmount());
        int difference = current - price;

        ArrayList<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("§7Seu cash         §f| §6" + formatter.format(current));
        lore.add("§7Preço             §f| §c" + formatter.format(price));
        lore.add("§f                     | §7______");
        lore.add("§7Cash restante   §f| §a" + formatter.format(difference));
        lore.add("");

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
