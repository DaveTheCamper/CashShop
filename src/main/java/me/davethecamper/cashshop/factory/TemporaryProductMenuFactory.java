package me.davethecamper.cashshop.factory;

import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.request.TemporaryProductMenuRequest;

public class TemporaryProductMenuFactory {

    public static SellProductMenu createTemporarySellProductMenu(TemporaryProductMenuRequest request) {
        SellProductMenu productMenu = SellProductMenu.createTemporaryProduct(request.getIdentifier(), request.getValor(), request.getDelay(), request.getProduct(), request.getItem());

        productMenu.setMoney(request.isMoney());
        productMenu.setAllowBonus(request.isAllowBonus());

        return productMenu;
    }

}
