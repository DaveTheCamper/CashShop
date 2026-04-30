package me.davethecamper.cashshop.events;

import me.davethecamper.cashshop.api.info.TransactionInfo;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionCompleteEventTest {

    private UUID uuid;
    private TransactionInfo transactionInfo;
    private TransactionCompleteEvent event;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        transactionInfo = mock(TransactionInfo.class);
        event = new TransactionCompleteEvent(uuid, transactionInfo);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(uuid, event.getPlayer());
        assertEquals(transactionInfo, event.getTransaction());
    }

    @Test
    public void testHandlers() {
        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers);

        HandlerList handlerList = TransactionCompleteEvent.getHandlerList();
        assertNotNull(handlerList);
    }
}