package me.davethecamper.cashshop.api.info;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionResponseTest {

    @Test
    public void testTransactionResponseValues() {
        assertNotNull(TransactionResponse.valueOf("APPROVED"));
        assertNotNull(TransactionResponse.valueOf("WAITING_FOR_PAYMENT"));
        assertNotNull(TransactionResponse.valueOf("CANCELLED"));
        assertNotNull(TransactionResponse.valueOf("REFUNDED"));
    }

    @Test
    public void testTransactionResponseEnumValues() {
        TransactionResponse[] values = TransactionResponse.values();
        assertEquals(4, values.length);
        assertEquals(TransactionResponse.APPROVED, values[0]);
        assertEquals(TransactionResponse.WAITING_FOR_PAYMENT, values[1]);
        assertEquals(TransactionResponse.CANCELLED, values[2]);
        assertEquals(TransactionResponse.REFUNDED, values[3]);
    }
}