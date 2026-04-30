package me.davethecamper.cashshop.api.info;

import me.davethecamper.cashshop.api.CashShopGateway;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionInfoTest {

    @Test
    public void testTransactionInfoConstructorAndGetters() {
        String player = "player1";
        String gateway = "gateway1";
        String cupom = "cupom1";
        int cash = 100;
        double realMoney = 50.0;
        long creationDate = System.currentTimeMillis();
        String link = "http://example.com";
        String transactionToken = "token123";

        TransactionInfo transactionInfo = new TransactionInfo(player, gateway, cupom, cash, realMoney, creationDate,
                link, transactionToken);

        assertEquals(player, transactionInfo.getPlayer());
        assertEquals(gateway, transactionInfo.getGatewayCaller());
        assertEquals(cupom, transactionInfo.getCupom());
        assertEquals(cash, transactionInfo.getCash());
        assertEquals(realMoney, transactionInfo.getRealMoneySpent());
        assertEquals(creationDate, transactionInfo.getCreationDate());
        assertEquals(link, transactionInfo.getLink());
        assertEquals(transactionToken, transactionInfo.getTransactionToken());
        assertEquals(TransactionResponse.WAITING_FOR_PAYMENT, transactionInfo.getStatus());
    }

    @Test
    public void testSetApproved() {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setApproved();

        assertEquals(TransactionResponse.APPROVED, transactionInfo.getStatus());
        assertTrue(transactionInfo.getApproveDate() > 0);
    }

    @Test
    public void testUpdateTransactionStatus() {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.updateTransactionStatus(TransactionResponse.COMPLETED);

        assertEquals(TransactionResponse.COMPLETED, transactionInfo.getStatus());
    }
}