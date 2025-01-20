package me.davethecamper.cashshop.api.info;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InitializationResultTest {

    @Test
    public void testInitializationResultValues() {
        assertNotNull(InitializationResult.valueOf("INVALID_CREDENTIALS"));
        assertNotNull(InitializationResult.valueOf("INVALID_CURRENCY"));
        assertNotNull(InitializationResult.valueOf("OFFLINE_API"));
        assertNotNull(InitializationResult.valueOf("INITIALIZATED"));
    }

    @Test
    public void testInitializationResultEnumValues() {
        InitializationResult[] values = InitializationResult.values();
        assertEquals(4, values.length);
        assertEquals(InitializationResult.INVALID_CREDENTIALS, values[0]);
        assertEquals(InitializationResult.INVALID_CURRENCY, values[1]);
        assertEquals(InitializationResult.OFFLINE_API, values[2]);
        assertEquals(InitializationResult.INITIALIZATED, values[3]);
    }
}