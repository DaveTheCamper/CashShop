package me.davethecamper.cashshop.inventory.edition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditionComponentTypeTest {

    @Test
    public void testEnumValues() {
        EditionComponentType[] values = EditionComponentType.values();
        assertEquals(6, values.length);
        assertEquals(EditionComponentType.DO_NOTHING, values[0]);
        assertEquals(EditionComponentType.DISPLAY_ITEM, values[1]);
        assertEquals(EditionComponentType.COMBO, values[2]);
        assertEquals(EditionComponentType.CATEGORY, values[3]);
        assertEquals(EditionComponentType.BUY_PRODUCT, values[4]);
        assertEquals(EditionComponentType.STATIC, values[5]);
    }

    @Test
    public void testValueOf() {
        assertEquals(EditionComponentType.DO_NOTHING, EditionComponentType.valueOf("DO_NOTHING"));
        assertEquals(EditionComponentType.DISPLAY_ITEM, EditionComponentType.valueOf("DISPLAY_ITEM"));
        assertEquals(EditionComponentType.COMBO, EditionComponentType.valueOf("COMBO"));
        assertEquals(EditionComponentType.CATEGORY, EditionComponentType.valueOf("CATEGORY"));
        assertEquals(EditionComponentType.BUY_PRODUCT, EditionComponentType.valueOf("BUY_PRODUCT"));
        assertEquals(EditionComponentType.STATIC, EditionComponentType.valueOf("STATIC"));
    }
}