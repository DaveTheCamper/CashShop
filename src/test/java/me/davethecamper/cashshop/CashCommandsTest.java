package me.davethecamper.cashshop;

import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashCommandsTest {

    private CashShop main;
    private CashCommands cashCommands;
    private CommandSender sender;
    private Command command;
    private Player player;
    private CashPlayer cashPlayer;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        cashCommands = new CashCommands(main);
        sender = mock(CommandSender.class);
        command = mock(Command.class);
        player = mock(Player.class);
        cashPlayer = mock(CashPlayer.class);

        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(main.getCashPlayer(any(UUID.class))).thenReturn(cashPlayer);
    }

    @Test
    public void testOnCommand_Cash_Ver() {
        when(command.getName()).thenReturn("cash");
        when(sender instanceof Player).thenReturn(true);
        when((Player) sender).thenReturn(player);
        when(player.hasPermission("cash.admin")).thenReturn(false);

        boolean result = cashCommands.onCommand(sender, command, "cash", new String[] { "ver" });

        assertTrue(result);
        verify(player, times(1)).sendMessage(anyString());
    }

    @Test
    public void testOnCommand_Cash_Pay() {
        when(command.getName()).thenReturn("cash");
        when(sender instanceof Player).thenReturn(true);
        when((Player) sender).thenReturn(player);
        when(player.hasPermission("cash.admin")).thenReturn(false);
        when(cashPlayer.getCash(false)).thenReturn(100);
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(Bukkit.getOfflinePlayer(anyString())).thenReturn(offlinePlayer);
        when(offlinePlayer.hasPlayedBefore()).thenReturn(true);
        CashPlayer otherCashPlayer = mock(CashPlayer.class);
        when(main.getCashPlayer(any(UUID.class))).thenReturn(otherCashPlayer);

        boolean result = cashCommands.onCommand(sender, command, "cash", new String[] { "pay", "player2", "50" });

        assertTrue(result);
        verify(cashPlayer, times(1)).removeCash(50);
        verify(otherCashPlayer, times(1)).addCash(50);
        verify(player, times(1)).sendMessage(anyString());
    }

    @Test
    public void testOnCommand_Shop() {
        when(command.getName()).thenReturn("shop");
        when(sender instanceof Player).thenReturn(true);
        when((Player) sender).thenReturn(player);

        boolean result = cashCommands.onCommand(sender, command, "shop", new String[] {});

        assertFalse(result);
        verify(main, times(1)).getNormalPlayerInventory(any(UUID.class));
    }

    @Test
    public void testAnyCashCommands_Reload() {
        when(sender.hasPermission("cash.admin")).thenReturn(true);

        cashCommands.anyCashCommands(sender, command, new String[] { "reload" });

        verify(main, times(1)).reload();
        verify(sender, times(1)).sendMessage(anyString());
    }

    @Test
    public void testAnyCashCommands_Total() {
        when(sender.hasPermission("cash.admin")).thenReturn(true);
        when(main.getTotalMoneySpent(anyInt(), anyInt())).thenReturn(100.0);

        cashCommands.anyCashCommands(sender, command, new String[] { "total", "2021", "5" });

        verify(sender, times(1)).sendMessage(contains("100.0R$"));
    }

    @Test
    public void testAnyCashCommands_GiveProduct() {
        when(sender.hasPermission("cash.admin")).thenReturn(true);
        Player receiver = mock(Player.class);
        when(Bukkit.getPlayer(anyString())).thenReturn(receiver);
        CashPlayer receiverCashPlayer = mock(CashPlayer.class);
        when(main.getCashPlayer(any(UUID.class))).thenReturn(receiverCashPlayer);
        SellProductMenu product = mock(SellProductMenu.class);
        when(main.getProduct(anyString())).thenReturn(product);

        cashCommands.anyCashCommands(sender, command, new String[] { "give_product", "player2", "product1" });

        verify(receiverCashPlayer, times(1)).buyCurrentProduct(1, product, false);
        verify(sender, times(1)).sendMessage(anyString());
    }

    @Test
    public void testAnyCashCommands_AddCash() {
        when(sender.hasPermission("cash.admin")).thenReturn(true);
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(Bukkit.getOfflinePlayer(anyString())).thenReturn(offlinePlayer);
        CashPlayer receiverCashPlayer = mock(CashPlayer.class);
        when(main.getCashPlayer(any(UUID.class))).thenReturn(receiverCashPlayer);

        cashCommands.anyCashCommands(sender, command, new String[] { "add", "player2", "100" });

        verify(receiverCashPlayer, times(1)).addCash(100, false);
        verify(sender, times(1)).sendMessage(anyString());
    }

    @Test
    public void testAnyCashCommands_SetCash() {
        when(sender.hasPermission("cash.admin")).thenReturn(true);
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(Bukkit.getOfflinePlayer(anyString())).thenReturn(offlinePlayer);
        CashPlayer receiverCashPlayer = mock(CashPlayer.class);
        when(main.getCashPlayer(any(UUID.class))).thenReturn(receiverCashPlayer);

        cashCommands.anyCashCommands(sender, command, new String[] { "set", "player2", "100" });

        verify(receiverCashPlayer, times(1)).setCash(100);
        verify(sender, times(1)).sendMessage(anyString());
    }

    @Test
    public void testAnyCashCommands_RemoveCash() {
        when(sender.hasPermission("cash.admin")).thenReturn(true);
        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(Bukkit.getOfflinePlayer(anyString())).thenReturn(offlinePlayer);
        CashPlayer receiverCashPlayer = mock(CashPlayer.class);
        when(main.getCashPlayer(any(UUID.class))).thenReturn(receiverCashPlayer);

        cashCommands.anyCashCommands(sender, command, new String[] { "remove", "player2", "50" });

        verify(receiverCashPlayer, times(1)).removeCash(50);
        verify(sender, times(1)).sendMessage(anyString());
    }
}