package me.davethecamper.cashshop;

import me.davethecamper.cashshop.events.CashMenuInventoryClickEvent;
import me.davethecamper.cashshop.events.CashPlayerInventoryClickEvent;
import me.davethecamper.cashshop.events.ChangeEditorInventoryEvent;
import me.davethecamper.cashshop.events.WaitingChatEvent;
import me.davethecamper.cashshop.inventory.ReciclableMenu;
import me.davethecamper.cashshop.inventory.choosers.ChoosableMenu;
import me.davethecamper.cashshop.inventory.choosers.MainChooseMenu;
import me.davethecamper.cashshop.inventory.configs.ConfigInteractiveMenu;
import me.davethecamper.cashshop.inventory.configs.SellProductMenu;
import me.davethecamper.cashshop.inventory.configs.ValuebleItemMenu;
import me.davethecamper.cashshop.inventory.edition.EditionComponent;
import me.davethecamper.cashshop.player.CashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventsCatcherTest {

    private CashShop main;
    private EventsCatcher eventsCatcher;
    private UUID uuid;
    private Player player;
    private CashPlayer cashPlayer;
    private InventoryClickEvent inventoryClickEvent;
    private InventoryCloseEvent inventoryCloseEvent;
    private InventoryOpenEvent inventoryOpenEvent;
    private InventoryDragEvent inventoryDragEvent;
    private Inventory inventory;
    private InventoryView inventoryView;

    @BeforeEach
    public void setUp() {
        main = mock(CashShop.class);
        eventsCatcher = new EventsCatcher(main);
        uuid = UUID.randomUUID();
        player = mock(Player.class);
        cashPlayer = mock(CashPlayer.class);
        inventory = mock(Inventory.class);
        inventoryView = mock(InventoryView.class);
        inventoryClickEvent = mock(InventoryClickEvent.class);
        inventoryCloseEvent = mock(InventoryCloseEvent.class);
        inventoryOpenEvent = mock(InventoryOpenEvent.class);
        inventoryDragEvent = mock(InventoryDragEvent.class);

        when(player.getUniqueId()).thenReturn(uuid);
        when(main.getCashPlayer(uuid)).thenReturn(cashPlayer);
        when(inventoryClickEvent.getWhoClicked()).thenReturn(player);
        when(inventoryCloseEvent.getPlayer()).thenReturn(player);
        when(inventoryOpenEvent.getPlayer()).thenReturn(player);
        when(inventoryDragEvent.getWhoClicked()).thenReturn(player);
        when(inventoryClickEvent.getView()).thenReturn(inventoryView);
        when(inventoryView.getTopInventory()).thenReturn(inventory);
    }

    @Test
    public void testOnClick_EditorInventory() {
        when(main.haveEditorInventoryOpen(uuid)).thenReturn(true);
        when(eventsCatcher.isEditingEditor(uuid)).thenReturn(true);
        when(main.getPlayerEditorCurrentInventory(uuid)).thenReturn(mock(ReciclableMenu.class));

        eventsCatcher.onClick(inventoryClickEvent);

        verify(inventoryClickEvent, times(1)).setCancelled(true);
    }

    @Test
    public void testOnClick_NormalPlayerInventory() {
        when(main.getNormalPlayerInventory(uuid)).thenReturn(cashPlayer);
        when(cashPlayer.haveAnyCurrentInventory()).thenReturn(true);
        when(eventsCatcher.isUsingMenus(uuid)).thenReturn(true);

        eventsCatcher.onClick(inventoryClickEvent);

        verify(inventoryClickEvent, times(1)).setCancelled(true);
    }

    @Test
    public void testOnChangeInventoryEvent() {
        ChangeEditorInventoryEvent event = mock(ChangeEditorInventoryEvent.class);
        when(event.getUuid()).thenReturn(uuid);
        when(event.getReciclableMenu()).thenReturn(null);

        eventsCatcher.onChangeInventoryEvent(event);

        verify(main, times(1)).changePlayerEditorInventory(eq(uuid), any(MainChooseMenu.class));
    }

    @Test
    public void testOnDrag() {
        when(main.haveEditorInventoryOpen(uuid)).thenReturn(true);
        when(eventsCatcher.isEditingEditor(uuid)).thenReturn(true);

        eventsCatcher.onDrag(inventoryDragEvent);

        verify(inventoryDragEvent, times(1)).setCancelled(true);
    }

    @Test
    public void testOnWaitingChat() {
        WaitingChatEvent event = mock(WaitingChatEvent.class);
        WaitingForChat waitingForChat = mock(WaitingForChat.class);
        when(event.getWaitingForChat()).thenReturn(waitingForChat);
        when(waitingForChat.getPlayer()).thenReturn(uuid);
        when(waitingForChat.getVarName()).thenReturn("set_gift");
        when(waitingForChat.getResult()).thenReturn("Friend");

        eventsCatcher.onWaitingChat(event);

        verify(cashPlayer, times(1)).setGiftFor("Friend");
    }

    @Test
    public void testOnClose() {
        when(main.haveEditorInventoryOpen(uuid)).thenReturn(true);
        when(main.getPlayerEditorCurrentInventory(uuid)).thenReturn(mock(ReciclableMenu.class));
        when(inventoryCloseEvent.getInventory()).thenReturn(inventory);

        eventsCatcher.onClose(inventoryCloseEvent);

        verify(eventsCatcher, times(1)).setEditingEditor(uuid, false);
    }

    @Test
    public void testOnOpen() {
        when(main.haveEditorInventoryOpen(uuid)).thenReturn(true);
        when(main.getPlayerEditorCurrentInventory(uuid)).thenReturn(mock(ReciclableMenu.class));
        when(inventoryOpenEvent.getInventory()).thenReturn(inventory);

        eventsCatcher.onOpen(inventoryOpenEvent);

        verify(eventsCatcher, times(1)).setEditingEditor(uuid, true);
    }
}