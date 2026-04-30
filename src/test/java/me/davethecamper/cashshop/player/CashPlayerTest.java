package me.davethecamper.cashshop.player;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.api.info.TransactionInfo;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashPlayerTest {

    private CashPlayer cashPlayer;
    private UUID uuid;
    private CashShop cashShop;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        cashPlayer = new CashPlayer(uuid);
        cashShop = mock(CashShop.class);
        CashShop.setInstance(cashShop);
    }

    @Test
    public void testSetCash() {
        cashPlayer.setCash(100);
        assertEquals(100, cashPlayer.getCash());
    }

    @Test
    public void testAddCash() {
        cashPlayer.addCash(50);
        assertEquals(50, cashPlayer.getCash());
    }

    @Test
    public void testRemoveCash() {
        cashPlayer.addCash(100);
        cashPlayer.removeCash(50);
        assertEquals(50, cashPlayer.getCash());
    }

    @Test
    public void testSetTransactionAsApproved() {
        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(transactionInfo.getTransactionToken()).thenReturn("token123");
        when(transactionInfo.getCupom()).thenReturn("cupom123");
        when(transactionInfo.getCash()).thenReturn(100);

        cashPlayer.setTransactionAsAproved(transactionInfo);

        assertTrue(cashPlayer.getPendingTransactions().isEmpty());
        assertTrue(cashPlayer.getTransactionsApproved().containsKey("token123"));
        verify(transactionInfo, times(1)).setApproved();
    }

    @Test
    public void testCancelTransaction() {
        TransactionInfo transactionInfo = mock(TransactionInfo.class);
        when(transactionInfo.getTransactionToken()).thenReturn("token123");

        cashPlayer.getPendingTransactions().put("token123", transactionInfo);
        cashPlayer.cancelTransaction(transactionInfo);

        assertTrue(cashPlayer.getPendingTransactions().isEmpty());
    }

    @Test
    public void testIsOnline() {
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(offlinePlayer.isOnline()).thenReturn(true);
        when(Bukkit.getOfflinePlayer(uuid)).thenReturn(offlinePlayer);

        assertTrue(cashPlayer.isOnline());
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.getParentFile()).thenReturn(mock(File.class));
        when(file.createNewFile()).thenReturn(true);

        FileConfiguration fileConfiguration = mock(FileConfiguration.class);
        when(fileConfiguration.getInt("cash")).thenReturn(100);
        when(fileConfiguration.getInt("cashBonus")).thenReturn(50);

        cashPlayer.setCash(100);
        cashPlayer.setCashBonus(50);

        cashPlayer.save();
        cashPlayer.load();

        assertEquals(100, cashPlayer.getCash());
        assertEquals(50, cashPlayer.getCash(true));
    }

    @Test
    public void testBuyCurrentProduct() {
        SellProductMenu sellProductMenu = mock(SellProductMenu.class);
        when(sellProductMenu.getValueInCash()).thenReturn(50.0);
        when(sellProductMenu.getProduct()).thenReturn(mock(ProductConfig.class));

        cashPlayer.addCash(100);
        cashPlayer.buyCurrentProduct(1, sellProductMenu, true);

        assertEquals(50, cashPlayer.getCash());
    }

    @Test
    public void testUpdateCurrentInventory() {
        ConfigInteractiveMenu menu = mock(ConfigInteractiveMenu.class);
        cashPlayer.updateCurrentInventory(menu);

        assertEquals(menu, cashPlayer.getCurrentInteractiveMenu());
    }
}