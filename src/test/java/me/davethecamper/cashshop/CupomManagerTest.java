package me.davethecamper.cashshop;

import me.davethecamper.cashshop.objects.Cupom;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CupomManagerTest {

    private CupomManager cupomManager;
    private FileConfiguration fileConfiguration;
    private File file;

    @BeforeEach
    public void setUp() {
        file = mock(File.class);
        fileConfiguration = mock(FileConfiguration.class);
        cupomManager = spy(new CupomManager());
        doReturn(file).when(cupomManager).getFile();
        doReturn(fileConfiguration).when(cupomManager).getFileConfiguration(file);
    }

    @Test
    public void testAddCupom() {
        cupomManager.addCupom("testCupom", 10.0, 1);
        assertTrue(cupomManager.isValid("testCupom"));
    }

    @Test
    public void testRemoveCupom() {
        cupomManager.addCupom("testCupom", 10.0, 1);
        assertTrue(cupomManager.removeCupom("testCupom"));
        assertFalse(cupomManager.isValid("testCupom"));
    }

    @Test
    public void testGetDiscount() {
        cupomManager.addCupom("testCupom", 10.0, 1);
        assertEquals(10.0, cupomManager.getDiscount("testCupom"));
    }

    @Test
    public void testAddTransaction() {
        cupomManager.addCupom("testCupom", 10.0, 1);
        cupomManager.addTransaction("testCupom", "token123", 100.0);
        Cupom cupom = cupomManager.getCurrentActiveCupons().stream().filter(c -> c.getName().equals("testCupom"))
                .findFirst().orElse(null);
        assertNotNull(cupom);
        assertTrue(cupom.getUsages().containsKey("token123"));
    }

    @Test
    public void testLoad() {
        when(file.exists()).thenReturn(true);
        when(fileConfiguration.get("cupons")).thenReturn(mock(Object.class));
        when(fileConfiguration.getConfigurationSection("cupons").getKeys(false))
                .thenReturn(new HashSet<>(Arrays.asList("testCupom")));
        when(fileConfiguration.getLong("cupons.testCupom.expiration")).thenReturn(System.currentTimeMillis() + 1000);
        when(fileConfiguration.getDouble("cupons.testCupom.percentage")).thenReturn(10.0);

        cupomManager.load();

        assertTrue(cupomManager.isValid("testCupom"));
    }

    @Test
    public void testSave() throws IOException {
        cupomManager.addCupom("testCupom", 10.0, 1);
        doNothing().when(fileConfiguration).save(file);

        cupomManager.save();

        verify(fileConfiguration, times(1)).set("cupons.testCupom.expiration", anyLong());
        verify(fileConfiguration, times(1)).set("cupons.testCupom.percentage", 10.0);
        verify(fileConfiguration, times(1)).save(file);
    }
}