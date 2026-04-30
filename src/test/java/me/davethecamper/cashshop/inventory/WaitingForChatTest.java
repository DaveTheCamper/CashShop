package me.davethecamper.cashshop.inventory;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.events.WaitingChatEvent;
import me.davethecamper.cashshop.inventory.configs.IdentificableMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WaitingForChatTest {

    private UUID playerUUID;
    private Player player;
    private IdentificableMenu caller;
    private WaitingForChat waitingForChat;

    @BeforeEach
    public void setUp() {
        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        caller = mock(IdentificableMenu.class);
        when(player.getUniqueId()).thenReturn(playerUUID);
        when(Bukkit.getPlayer(playerUUID)).thenReturn(player);
        CashShop.setInstance(mock(CashShop.class));
    }

    @Test
    public void testConstructor() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.STRING, "var_name", "message");
        assertNotNull(waitingForChat);
        assertEquals(playerUUID, waitingForChat.getPlayer());
        assertEquals("var_name", waitingForChat.getVarName());
        assertEquals("message", waitingForChat.message);
    }

    @Test
    public void testExecuteWaitingChat() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.STRING, "var_name", "message");
        waitingForChat.executeWaitingChat();

        verify(player, times(1)).sendMessage("message");
        verify(player, times(1)).closeInventory();
    }

    @Test
    public void testOnChat_ValidMessage() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.STRING, "var_name", "message");
        AsyncPlayerChatEvent chatEvent = mock(AsyncPlayerChatEvent.class);
        when(chatEvent.getPlayer()).thenReturn(player);
        when(chatEvent.getMessage()).thenReturn("valid_message");

        waitingForChat.onChat(chatEvent);

        verify(chatEvent, times(1)).setCancelled(true);
        assertEquals("valid_message", waitingForChat.getResult());
    }

    @Test
    public void testOnChat_InvalidMessage() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.INTEGER, "var_name", "message");
        AsyncPlayerChatEvent chatEvent = mock(AsyncPlayerChatEvent.class);
        when(chatEvent.getPlayer()).thenReturn(player);
        when(chatEvent.getMessage()).thenReturn("invalid_message");

        waitingForChat.onChat(chatEvent);

        verify(chatEvent, times(1)).setCancelled(true);
        verify(player, times(1)).sendMessage(anyString());
        assertNull(waitingForChat.getResult());
    }

    @Test
    public void testIsValid_Integer() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.INTEGER, "var_name", "message");
        assertTrue(waitingForChat.isValid("123"));
        assertFalse(waitingForChat.isValid("-123"));
        assertFalse(waitingForChat.isValid("abc"));
    }

    @Test
    public void testIsValid_String() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.STRING, "var_name", "message");
        assertTrue(waitingForChat.isValid("valid_message"));
        assertFalse(waitingForChat.isValid("invalid_message_with_special_characters!@#"));
    }

    @Test
    public void testFinish() {
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.STRING, "var_name", "message");
        waitingForChat.finish("result");

        assertEquals("result", waitingForChat.getResult());
        verify(Bukkit.getScheduler(), times(1)).runTask(any(), any(Runnable.class));
    }

    @Test
    public void testGetErrorMessage() {
        when(CashShop.getInstance().getMessagesConfig().getString(anyString())).thenReturn("error_message");
        waitingForChat = new WaitingForChat(playerUUID, WaitingForChat.Primitives.STRING, "var_name", "message");

        assertEquals("error_message", waitingForChat.getErrorMessage());
    }
}