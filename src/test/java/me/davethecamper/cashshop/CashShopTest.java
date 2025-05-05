package me.davethecamper.cashshop;

import me.davethecamper.cashshop.api.CashShopApi;
import me.davethecamper.cashshop.api.CashShopGateway;
import me.davethecamper.cashshop.api.info.InitializationResult;
import me.davethecamper.cashshop.exceptions.EconomyDisabledException;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.configs.*;
import me.davethecamper.cashshop.player.CashPlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashShopTest {

    private CashShop cashShop;
    private FileConfiguration config;
    private Economy economy;

    @BeforeEach
    public void setUp() {
        cashShop = spy(new CashShop());
        config = mock(FileConfiguration.class);
        economy = mock(Economy.class);
        doReturn(config).when(cashShop).getConfig();
    }

    @Test
    public void testOnEnable() {
        doNothing().when(cashShop).load();
        doNothing().when(cashShop).autoSave();
        doNothing().when(cashShop).setupEconomy();
        doNothing().when(cashShop).loadCommands();

        cashShop.onEnable();

        verify(cashShop, times(1)).load();
        verify(cashShop, times(1)).autoSave();
        verify(cashShop, times(1)).setupEconomy();
        verify(cashShop, times(1)).loadCommands();
    }

    @Test
    public void testOnDisable() {
        TransactionsManager transactionsManager = mock(TransactionsManager.class);
        cashShop.transactions = transactionsManager;

        doNothing().when(cashShop).saveAll();
        doNothing().when(transactionsManager).stop();

        cashShop.onDisable();

        verify(cashShop, times(1)).saveAll();
        verify(transactionsManager, times(1)).stop();
    }

    @Test
    public void testReload() {
        doNothing().when(cashShop).load();

        cashShop.reload();

        verify(cashShop, times(1)).load();
        assertNull(cashShop.configuration);
        assertNull(cashShop.messages);
    }

    @Test
    public void testGetTotalMoneySpent() {
        when(cashShop.getTotalMoneySpent(anyInt(), anyInt())).thenReturn(100.0);
        double totalMoneySpent = cashShop.getTotalMoneySpent(2021, 5);
        assertEquals(100.0, totalMoneySpent);
    }

    @Test
    public void testGetCashPlayer() {
        UUID uuid = UUID.randomUUID();
        CashPlayer cashPlayer = mock(CashPlayer.class);
        when(cashShop.getCashPlayer(uuid)).thenReturn(cashPlayer);
        assertEquals(cashPlayer, cashShop.getCashPlayer(uuid));
    }

    @Test
    public void testGetProduct() {
        String productName = "product1";
        SellProductMenu product = mock(SellProductMenu.class);
        when(cashShop.getProduct(productName)).thenReturn(product);
        assertEquals(product, cashShop.getProduct(productName));
    }

    @Test
    public void testGetEconomy() {
        when(cashShop.getEconomy()).thenReturn(economy);
        assertEquals(economy, cashShop.getEconomy());
    }

    @Test
    public void testGetMainConfig() {
        when(cashShop.getMainConfig()).thenReturn(config);
        assertEquals(config, cashShop.getMainConfig());
    }

    @Test
    public void testGetMessagesConfig() {
        when(cashShop.getMessagesConfig()).thenReturn(config);
        assertEquals(config, cashShop.getMessagesConfig());
    }

    @Test
    public void testGetCategoriesManager() {
        CategoriesManager categoriesManager = mock(CategoriesManager.class);
        when(cashShop.getCategoriesManager()).thenReturn(categoriesManager);
        assertEquals(categoriesManager, cashShop.getCategoriesManager());
    }

    @Test
    public void testGetStaticObjects() {
        CashShopStaticMenus staticMenus = mock(CashShopStaticMenus.class);
        when(cashShop.getStaticObjects()).thenReturn(staticMenus);
        assertEquals(staticMenus, cashShop.getStaticObjects());
    }

    @Test
    public void testGetCupomManager() {
        CupomManager cupomManager = mock(CupomManager.class);
        when(cashShop.getCupomManager()).thenReturn(cupomManager);
        assertEquals(cupomManager, cashShop.getCupomManager());
    }

    @Test
    public void testGetLists() {
        CashShopLists lists = mock(CashShopLists.class);
        when(cashShop.getLists()).thenReturn(lists);
        assertEquals(lists, cashShop.getLists());
    }

    @Test
    public void testGetTransactionsManager() {
        TransactionsManager transactionsManager = mock(TransactionsManager.class);
        when(cashShop.getTransactionsManager()).thenReturn(transactionsManager);
        assertEquals(transactionsManager, cashShop.getTransactionsManager());
    }

    @Test
    public void testGetGateway() {
        String gatewayName = "gateway1";
        CashShopGateway gateway = mock(CashShopGateway.class);
        when(cashShop.getGateway(gatewayName)).thenReturn(gateway);
        assertEquals(gateway, cashShop.getGateway(gatewayName));
    }

    @Test
    public void testGetGatewaysNames() {
        ArrayList<String> gatewaysNames = new ArrayList<>();
        when(cashShop.getGatewaysNames()).thenReturn(gatewaysNames);
        assertEquals(gatewaysNames, cashShop.getGatewaysNames());
    }

    @Test
    public void testSetupEconomy() {
        RegisteredServiceProvider<Economy> rsp = mock(RegisteredServiceProvider.class);
        when(rsp.getProvider()).thenReturn(economy);
        when(Bukkit.getServer().getServicesManager().getRegistration(Economy.class)).thenReturn(rsp);

        boolean result = cashShop.setupEconomy();

        assertTrue(result);
        assertEquals(economy, cashShop.getEconomy());
    }

    @Test
    public void testSetupEconomy_NoVault() {
        when(Bukkit.getServer().getPluginManager().getPlugin("Vault")).thenReturn(null);

        boolean result = cashShop.setupEconomy();

        assertFalse(result);
    }

    @Test
    public void testSetupEconomy_NoEconomyProvider() {
        when(Bukkit.getServer().getServicesManager().getRegistration(Economy.class)).thenReturn(null);

        boolean result = cashShop.setupEconomy();

        assertFalse(result);
    }

    @Test
    public void testSaveAll() {
        CashPlayer cashPlayer = mock(CashPlayer.class);
        UUID uuid = UUID.randomUUID();
        cashShop.players.put(uuid, cashPlayer);

        doNothing().when(cashPlayer).save();
        when(cashPlayer.hasChanges()).thenReturn(true);

        cashShop.saveAll();

        verify(cashPlayer, times(1)).save();
    }

    @Test
    public void testSaveAll_NoChanges() {
        CashPlayer cashPlayer = mock(CashPlayer.class);
        UUID uuid = UUID.randomUUID();
        cashShop.players.put(uuid, cashPlayer);

        doNothing().when(cashPlayer).save();
        when(cashPlayer.hasChanges()).thenReturn(false);

        cashShop.saveAll();

        verify(cashPlayer, never()).save();
    }
}