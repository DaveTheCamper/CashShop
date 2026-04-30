package me.davethecamper.cashshop.api;

import me.davethecamper.cashshop.*;
import me.davethecamper.cashshop.inventory.configs.*;
import me.davethecamper.cashshop.player.CashPlayer;
import net.milkbowl.vault.economy.Economy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashShopApiTest {

    private CashShop main;
    private CashShopApi api;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        api = new CashShopApi(main);
    }

    @Test
    public void testGetTransactionsManager() {
        TransactionsManager transactionsManager = mock(TransactionsManager.class);
        when(main.transactions).thenReturn(transactionsManager);

        assertEquals(transactionsManager, api.getTransactionsManager());
    }

    @Test
    public void testGetMainConfig() {
        ConfigManager configManager = mock(ConfigManager.class);
        when(main.configuration).thenReturn(configManager);

        assertEquals(configManager, api.getMainConfig());
    }

    @Test
    public void testGetMessagesConfig() {
        ConfigManager messagesConfig = mock(ConfigManager.class);
        when(main.messages).thenReturn(messagesConfig);

        assertEquals(messagesConfig, api.getMessagesConfig());
    }

    @Test
    public void testGetLists() {
        CashShopLists lists = mock(CashShopLists.class);
        when(main.lists).thenReturn(lists);

        assertEquals(lists, api.getLists());
    }

    @Test
    public void testGetCategoriesManager() {
        CategoriesManager categoriesManager = mock(CategoriesManager.class);
        when(main.categories).thenReturn(categoriesManager);

        assertEquals(categoriesManager, api.getCategoriesManager());
    }

    @Test
    public void testGetStaticObjects() {
        CashShopStaticMenus staticMenus = mock(CashShopStaticMenus.class);
        when(main.static_menus).thenReturn(staticMenus);

        assertEquals(staticMenus, api.getStaticObjects());
    }

    @Test
    public void testGetCupomManager() {
        CupomManager cupomManager = mock(CupomManager.class);
        when(main.getCupomManager()).thenReturn(cupomManager);

        assertEquals(cupomManager, api.getCupomManager());
    }

    @Test
    public void testGetCashPlayer() {
        UUID uuid = UUID.randomUUID();
        CashPlayer cashPlayer = mock(CashPlayer.class);
        when(main.getNormalPlayerInventory(uuid)).thenReturn(cashPlayer);

        assertEquals(cashPlayer, api.getCashPlayer(uuid));
    }

    @Test
    public void testGetEconomy() {
        Economy economy = mock(Economy.class);
        when(main.getEconomy()).thenReturn(economy);

        assertEquals(economy, api.getEconomy());
    }

    @Test
    public void testGetGateway() {
        String gatewayName = "gateway1";
        CashShopGateway gateway = mock(CashShopGateway.class);
        when(main.getGateway(gatewayName)).thenReturn(gateway);

        assertEquals(gateway, api.getGateway(gatewayName));
    }

    @Test
    public void testGetGatewaysNames() {
        ArrayList<String> gatewaysNames = new ArrayList<>();
        when(main.getGatewaysNames()).thenReturn(gatewaysNames);

        assertEquals(gatewaysNames, api.getGatewaysNames());
    }

    @Test
    public void testGetConfigItemBasedOnClass() {
        String name = "test";
        ConfigInteractiveMenu configInteractiveMenu = mock(ConfigInteractiveMenu.class);
        when(main.categories.getCategorie(name)).thenReturn(configInteractiveMenu);

        assertEquals(configInteractiveMenu, api.getConfigItemBasedOnClass(name, ConfigInteractiveMenu.class));
    }

    @Test
    public void testGetCosmeticItem() {
        String name = "cosmetic";
        ConfigItemMenu configItemMenu = mock(ConfigItemMenu.class);
        TreeMap<String, ConfigItemMenu> tree = new TreeMap<>();
        tree.put(name, configItemMenu);
        when(main.do_nothing).thenReturn(tree);

        assertEquals(configItemMenu, api.getCosmeticItem(name));
    }

    @Test
    public void testGetProduct() {
        String name = "product";
        SellProductMenu sellProductMenu = mock(SellProductMenu.class);
        TreeMap<String, SellProductMenu> tree = new TreeMap<>();
        tree.put(name, sellProductMenu);
        when(main.products).thenReturn(tree);

        assertEquals(sellProductMenu, api.getProduct(name));
    }

    @Test
    public void testGetCombo() {
        String name = "combo";
        ComboItemMenu comboItemMenu = mock(ComboItemMenu.class);
        TreeMap<String, ComboItemMenu> tree = new TreeMap<>();
        tree.put(name, comboItemMenu);
        when(main.combos).thenReturn(tree);

        assertEquals(comboItemMenu, api.getCombo(name));
    }

    @Test
    public void testGetStaticItem() {
        String name = "static";
        ConfigItemMenu configItemMenu = mock(ConfigItemMenu.class);
        TreeMap<String, ConfigItemMenu> tree = new TreeMap<>();
        tree.put(name, configItemMenu);
        when(main.static_items).thenReturn(tree);

        assertEquals(configItemMenu, api.getStaticItem(name));
    }

    @Test
    public void testRegisterObject() {
        String type = "product";
        String name = "testProduct";
        SavableMenu menu = mock(SavableMenu.class);
        TreeMap<String, ConfigItemMenu> tree = new TreeMap<>();
        when(main.products).thenReturn(tree);

        api.registerObject(type, name, menu);

        assertTrue(tree.containsKey(name));
    }

    @Test
    public void testUnregister() {
        String type = "product";
        String name = "testProduct";
        TreeMap<String, ConfigItemMenu> tree = new TreeMap<>();
        tree.put(name, mock(ConfigItemMenu.class));
        when(main.products).thenReturn(tree);

        api.unregister(type, name);

        assertFalse(tree.containsKey(name));
    }

    @Test
    public void testUpdate() {
        String type = "product";
        String oldName = "oldProduct";
        String newName = "newProduct";
        ConfigItemMenu menu = mock(ConfigItemMenu.class);
        TreeMap<String, ConfigItemMenu> tree = new TreeMap<>();
        tree.put(oldName, menu);
        when(main.products).thenReturn(tree);

        api.update(type, oldName, newName);

        assertTrue(tree.containsKey(newName));
        assertFalse(tree.containsKey(oldName));
    }

    @Test
    public void testSave() throws IOException {
        String name = "testSave";
        InputStream stream = mock(InputStream.class);
        ConfigItemMenu menu = mock(ConfigItemMenu.class);
        when(main.load(any(YamlConfiguration.class), eq(name), eq(false))).thenReturn(menu);

        api.save(name, stream);

        verify(menu).save();
    }

    @Test
    public void testGetTotalMoneySpent() {
        int year = 2021;
        int month = 5;
        double amountSpent = 100.0;
        CashPlayer cashPlayer = mock(CashPlayer.class);
        when(cashPlayer.getAmountSpent(year, month)).thenReturn(amountSpent);
        when(main.getNormalPlayerInventory(any(UUID.class))).thenReturn(cashPlayer);
        File file = mock(File.class);
        when(file.getName()).thenReturn("player.yml");
        File[] files = { file };
        when(main.getDataFolder()).thenReturn(new File("data"));
        when(new File("data/players/").listFiles()).thenReturn(files);

        double totalMoneySpent = api.getTotalMoneySpent(year, month);

        assertEquals(amountSpent, totalMoneySpent);
    }

    @Test
    public void testGetTopCash() {
        CashPlayer cashPlayer = mock(CashPlayer.class);
        when(main.getNormalPlayerInventory(any(UUID.class))).thenReturn(cashPlayer);
        when(cashPlayer.getAmountSpent(anyInt(), anyInt())).thenReturn(100.0);
        File file = mock(File.class);
        when(file.getName()).thenReturn("player.yml");
        File[] files = { file };
        when(main.getDataFolder()).thenReturn(new File("data"));
        when(new File("data/players/").listFiles()).thenReturn(files);

        CashPlayer topCashPlayer = api.getTopCash();

        assertEquals(cashPlayer, topCashPlayer);
    }

    @Test
    public void testGetCashRanking() {
        CashPlayer cashPlayer = mock(CashPlayer.class);
        when(main.getNormalPlayerInventory(any(UUID.class))).thenReturn(cashPlayer);
        when(cashPlayer.getAmountSpent(anyInt(), anyInt())).thenReturn(100.0);
        File file = mock(File.class);
        when(file.getName()).thenReturn("player.yml");
        File[] files = { file };
        when(main.getDataFolder()).thenReturn(new File("data"));
        when(new File("data/players/").listFiles()).thenReturn(files);

        ArrayList<CashPlayer> ranking = api.getCashRanking(2021, 5);

        assertEquals(1, ranking.size());
        assertEquals(cashPlayer, ranking.get(0));
    }
}