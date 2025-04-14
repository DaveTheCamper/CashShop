package me.davethecamper.cashshop.inventory.edition;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EditionComponentTest {

    private EditionComponent editionComponent;
    private EditionComponentType type;
    private String name;
    private ItemStack item;
    private Consumer<CashMenuInventoryClickEvent> consumer;

    @BeforeEach
    public void setUp() {
        type = EditionComponentType.DISPLAY_ITEM;
        name = "TestComponent";
        item = mock(ItemStack.class);
        consumer = mock(Consumer.class);
        editionComponent = new EditionComponent(type, name, item);
    }

    @Test
    public void testConstructorWithItem() {
        assertNotNull(editionComponent);
        assertEquals(type, editionComponent.getType());
        assertEquals(name, editionComponent.getName());
        assertEquals(item, editionComponent.getItemStack());
    }

    @Test
    public void testConstructorWithoutItem() {
        editionComponent = new EditionComponent(type, name);
        assertNotNull(editionComponent);
        assertEquals(type, editionComponent.getType());
        assertEquals(name, editionComponent.getName());
        assertNull(editionComponent.getItemStack());
    }

    @Test
    public void testSetType() {
        EditionComponentType newType = EditionComponentType.BUY_PRODUCT;
        editionComponent.setType(newType);
        assertEquals(newType, editionComponent.getType());
    }

    @Test
    public void testSetName() {
        String newName = "NewComponent";
        editionComponent.setName(newName);
        assertEquals(newName, editionComponent.getName());
    }

    @Test
    public void testSetItemStack() {
        ItemStack newItem = mock(ItemStack.class);
        editionComponent.setItemStack(newItem);
        assertEquals(newItem, editionComponent.getItemStack());
    }

    @Test
    public void testSetConsumer() {
        editionComponent.setConsumer(consumer);
        assertEquals(consumer, editionComponent.getConsumer());
    }

    @Test
    public void testClone() {
        EditionComponent clonedComponent = editionComponent.clone();
        assertNotNull(clonedComponent);
        assertEquals(editionComponent.getType(), clonedComponent.getType());
        assertEquals(editionComponent.getName(), clonedComponent.getName());
        assertEquals(editionComponent.getItemStack(), clonedComponent.getItemStack());
        assertNull(clonedComponent.getConsumer()); // Consumer is not cloned
    }
}