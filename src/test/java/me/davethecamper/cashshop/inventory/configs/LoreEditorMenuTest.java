package me.davethecamper.cashshop.inventory.configs;

import me.davethecamper.cashshop.ConfigManager;
import me.davethecamper.cashshop.inventory.configs.temporary.TemporarySellProductMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoreEditorMenuTest {

    private LoreEditorMenu loreEditorMenu;
    private ConfigManager mockConfigManager;
    private TemporarySellProductMenu mockTemporaryMenu;
    private Consumer<LoreEditorMenu> mockConsumer;
    private ConfigItemMenu mockDad;

    @BeforeEach
    void setUp() {
        mockConfigManager = mock(ConfigManager.class);
        mockTemporaryMenu = mock(TemporarySellProductMenu.class);
        mockConsumer = mock(Consumer.class);
        mockDad = mock(ConfigItemMenu.class);

        when(mockTemporaryMenu.getProduct()).thenReturn(mock(Product.class));
        when(mockDad.getItemProperties()).thenReturn(mock(ItemProperties.class));
        when(mockDad.getItemProperties().getLore()).thenReturn(new ArrayList<>());

        loreEditorMenu = new LoreEditorMenu("test-id", mockConfigManager, mockTemporaryMenu, mockConsumer);
    }

    @Test
    void testLoad() {
        loreEditorMenu.load();
        assertEquals(0, loreEditorMenu.getLore().size());
    }

    @Test
    void testSaveHandler() {
        loreEditorMenu.saveHandler();
        verify(mockDad, times(1)).changeLore(anyList(), anyString());
    }

    @Test
    void testChangerVarHandler_AddNew() {
        loreEditorMenu.changerVarHandler("ADD_NEW", "New Lore Line");
        assertEquals(1, loreEditorMenu.getLore().size());
        assertEquals("New Lore Line", loreEditorMenu.getLore().get(0));
    }

    @Test
    void testChangerVarHandler_UpdateExisting() {
        loreEditorMenu.getLore().add("Existing Lore Line");
        loreEditorMenu.changerVarHandler("lore0", "Updated Lore Line");
        assertEquals(1, loreEditorMenu.getLore().size());
        assertEquals("Updated Lore Line", loreEditorMenu.getLore().get(0));
    }

    @Test
    void testInventoryClickHandler_AddNew() {
        boolean result = loreEditorMenu.inventoryClickHandler(UUID.randomUUID(), 0, 0,
                InventoryAction.HOTBAR_MOVE_AND_READD);
        assertTrue(result);
    }

    @Test
    void testFinishEditing_Save() {
        loreEditorMenu.finishEditing(UUID.randomUUID(), true);
        verify(mockConsumer, times(1)).accept(any(LoreEditorMenu.class));
    }

    @Test
    void testFinishEditing_Cancel() {
        loreEditorMenu.finishEditing(UUID.randomUUID(), false);
        verify(mockConsumer, times(1)).accept(any(LoreEditorMenu.class));
    }

    @Test
    void testCreateTemporaryProductItems() {
        LoreEditorMenu menu = LoreEditorMenu.createTemporaryProductItems(mockTemporaryMenu, mockConsumer);
        assertNotNull(menu);
    }
}