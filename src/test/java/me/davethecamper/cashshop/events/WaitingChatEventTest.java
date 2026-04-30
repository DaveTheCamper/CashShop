package me.davethecamper.cashshop.events;

import me.davethecamper.cashshop.inventory.WaitingForChat;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WaitingChatEventTest {

    private WaitingForChat waitingForChat;
    private WaitingChatEvent event;

    @BeforeEach
    public void setUp() {
        waitingForChat = mock(WaitingForChat.class);
        event = new WaitingChatEvent(waitingForChat);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(waitingForChat, event.getWaitingForChat());
    }

    @Test
    public void testHandlers() {
        HandlerList handlers = event.getHandlers();
        assertNotNull(handlers);

        HandlerList handlerList = WaitingChatEvent.getHandlerList();
        assertNotNull(handlerList);
    }
}