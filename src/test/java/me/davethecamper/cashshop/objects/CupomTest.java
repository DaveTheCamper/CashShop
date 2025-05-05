package me.davethecamper.cashshop.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class CupomTest {

    private Cupom cupom;
    private String name;
    private double percentage;
    private long expiration;
    private HashMap<String, Double> usages;

    @BeforeEach
    public void setUp() {
        name = "testCupom";
        percentage = 10.0;
        expiration = System.currentTimeMillis() + 10000; // 10 seconds from now
        usages = new HashMap<>();
        cupom = new Cupom(name, percentage, expiration, usages);
    }

    @Test
    public void testConstructor() {
        assertNotNull(cupom);
        assertEquals(name, cupom.getName());
        assertEquals(percentage, cupom.getPercetage());
        assertEquals(expiration, cupom.getExpiration());
        assertEquals(usages, cupom.getUsages());
    }

    @Test
    public void testConstructorWithoutUsages() {
        cupom = new Cupom(name, percentage, expiration);
        assertNotNull(cupom);
        assertEquals(name, cupom.getName());
        assertEquals(percentage, cupom.getPercetage());
        assertEquals(expiration, cupom.getExpiration());
        assertTrue(cupom.getUsages().isEmpty());
    }

    @Test
    public void testAddUsage() {
        String token = "token123";
        double amount = 100.0;
        cupom.addUsage(token, amount);
        assertTrue(cupom.getUsages().containsKey(token));
        assertEquals(amount, cupom.getUsages().get(token));
    }

    @Test
    public void testIsExpirated() {
        assertFalse(cupom.isExpirated());
        cupom = new Cupom(name, percentage, System.currentTimeMillis() - 10000); // 10 seconds ago
        assertTrue(cupom.isExpirated());
    }
}