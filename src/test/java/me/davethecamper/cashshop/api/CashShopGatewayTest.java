package me.davethecamper.cashshop.api;

import me.davethecamper.cashshop.api.info.InitializationResult;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashShopGatewayTest {

    private CashShopGateway gateway;
    private FileConfiguration yaml;

    @BeforeEach
    public void setUp() {
        gateway = mock(CashShopGateway.class);
        yaml = mock(FileConfiguration.class);
    }

    @Test
    public void testInitWithInvalidCredentials() {
        when(gateway.init(yaml, "USD")).thenReturn(InitializationResult.INVALID_CREDENTIALS);

        InitializationResult result = gateway.init(yaml, "USD");

        assertEquals(InitializationResult.INVALID_CREDENTIALS, result);
    }

    @Test
    public void testInitWithInvalidCurrency() {
        when(gateway.init(yaml, "INVALID_CURRENCY")).thenReturn(InitializationResult.INVALID_CURRENCY);

        InitializationResult result = gateway.init(yaml, "INVALID_CURRENCY");

        assertEquals(InitializationResult.INVALID_CURRENCY, result);
    }

    @Test
    public void testInitWithOfflineApi() {
        when(gateway.init(yaml, "USD")).thenReturn(InitializationResult.OFFLINE_API);

        InitializationResult result = gateway.init(yaml, "USD");

        assertEquals(InitializationResult.OFFLINE_API, result);
    }

    @Test
    public void testInitWithValidApi() {
        when(gateway.init(yaml, "USD")).thenReturn(InitializationResult.INITIALIZATED);

        InitializationResult result = gateway.init(yaml, "USD");

        assertEquals(InitializationResult.INITIALIZATED, result);
    }
}