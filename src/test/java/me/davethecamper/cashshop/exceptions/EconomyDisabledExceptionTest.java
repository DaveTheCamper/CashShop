package me.davethecamper.cashshop.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EconomyDisabledExceptionTest {

    @Test
    public void testConstructor() {
        String message = "Economy is disabled";
        EconomyDisabledException exception = new EconomyDisabledException(message);

        assertEquals(message, exception.getMessage());
    }
}