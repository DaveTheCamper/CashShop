package me.davethecamper.cashshop.api.info;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerInfoTest {

    @Test
    public void testPlayerInfoConstructorAndGetters() {
        String name = "dave";
        String lastName = "0";
        String email = "dave_0@gmail.com";

        PlayerInfo playerInfo = new PlayerInfo(name, lastName, email);

        assertEquals(name, playerInfo.getName());
        assertEquals(lastName, playerInfo.getLastName());
        assertEquals(email, playerInfo.getEmail());
    }

    @Test
    public void testPlayerInfoWithEmptyFields() {
        String name = "Nicolas";
        String lastName = "Vycas";
        String email = "vycasnicolas@gmail.com";

        PlayerInfo playerInfo = new PlayerInfo(name, lastName, email);

        assertEquals(name, playerInfo.getName());
        assertEquals(lastName, playerInfo.getLastName());
        assertEquals(email, playerInfo.getEmail());
    }
}