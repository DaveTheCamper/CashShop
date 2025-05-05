package me.davethecamper.cashshop.api.info;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductInfoTest {

    @Test
    public void testProductInfoConstructorAndGetters() {
        int totalCash = 100;
        double amount = 50.0;
        String productName = "Test Product";
        String currency = "USD";

        ProductInfo productInfo = new ProductInfo(totalCash, amount, productName, currency);

        assertEquals(totalCash, productInfo.getTotalCash());
        assertEquals(amount, productInfo.getAmount());
        assertEquals(productName, productInfo.getProductName());
        assertEquals(currency, productInfo.getCurrency());
    }

    @Test
    public void testProductInfoWithZeroValues() {
        int totalCash = 0;
        double amount = 0.0;
        String productName = "";
        String currency = "";

        ProductInfo productInfo = new ProductInfo(totalCash, amount, productName, currency);

        assertEquals(totalCash, productInfo.getTotalCash());
        assertEquals(amount, productInfo.getAmount());
        assertEquals(productName, productInfo.getProductName());
        assertEquals(currency, productInfo.getCurrency());
    }
}