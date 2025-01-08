package me.davethecamper.cashshop;

import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.api.info.TransactionResponse;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionsManagerTest {

    private CashShop main;
    private TransactionsManager transactionsManager;
    private CashPlayer cashPlayer;
    private TransactionInfo transactionInfo;
    private CashShopGateway cashShopGateway;
    private BukkitScheduler scheduler;
    private PluginManager pluginManager;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        transactionsManager = new TransactionsManager(main);
        cashPlayer = mock(CashPlayer.class);
        transactionInfo = mock(TransactionInfo.class);
        cashShopGateway = mock(CashShopGateway.class);
        scheduler = mock(BukkitScheduler.class);
        pluginManager = mock(PluginManager.class);

        when(main.getGateway(anyString())).thenReturn(cashShopGateway);
        when(main.getScheduler()).thenReturn(scheduler);
        when(main.getPluginManager()).thenReturn(pluginManager);
        when(main.getCashPlayer(any(UUID.class))).thenReturn(cashPlayer);
        when(main.getPlayers()).thenReturn(new HashMap<>());
        when(main.getConfiguration()).thenReturn(mock(FileConfiguration.class));
    }

    @Test
    public void testStop() {
        transactionsManager.stop();
        assertTrue(transactionsManager.getThread().isInterrupted());
    }

    @Test
    public void testAddToCancel() {
        transactionsManager.addToCancel(cashPlayer, transactionInfo);
        verify(scheduler, times(1)).runTask(eq(main), any(Runnable.class));
    }

    @Test
    public void testApproveTransaction() {
        transactionsManager.approveTransaction(cashPlayer, transactionInfo);
        verify(scheduler, times(1)).runTask(eq(main), any(Runnable.class));
    }

    @Test
    public void testFinishApproveTransaction() {
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(Bukkit.getOfflinePlayer(any(UUID.class))).thenReturn(offlinePlayer);
        when(offlinePlayer.isOnline()).thenReturn(true);
        when(offlinePlayer.getPlayer()).thenReturn(mock(Player.class));

        transactionsManager.finishApproveTransaction(cashPlayer, transactionInfo);

        verify(pluginManager, times(1)).callEvent(any());
        verify(offlinePlayer.getPlayer(), times(1)).sendMessage(anyString());
        verify(offlinePlayer.getPlayer(), times(1)).playSound(any(), any(), anyFloat(), anyFloat());
    }

    @Test
    public void testCreatePlayerTransaction() {
        when(main.getMainConfig()).thenReturn(mock(FileConfiguration.class));
        when(main.getCupomManager()).thenReturn(mock(CupomManager.class));
        when(main.getGateway(anyString())).thenReturn(cashShopGateway);
        when(cashShopGateway.generateTransaction(any(), any())).thenReturn(transactionInfo);

        transactionsManager.createPlayerTransaction("identifier", cashPlayer);

        verify(scheduler, times(1)).runTaskAsynchronously(eq(main), any(Runnable.class));
    }

    @Test
    public void testIsValidNick() {
        assertTrue(transactionsManager.isValidNick("valid_nick"));
        assertFalse(transactionsManager.isValidNick("invalid-nick"));
        assertFalse(transactionsManager.isValidNick(""));
        assertFalse(transactionsManager.isValidNick("thisisaverylongnickname"));
    }
}