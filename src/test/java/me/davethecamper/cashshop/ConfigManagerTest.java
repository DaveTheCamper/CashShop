package me.davethecamper.cashshop;

import me.davethecamper.cashshop.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigManagerTest {

    private ConfigManager configManager;
    private File file;
    private Plugin plugin;
    private FileConfiguration fileConfiguration;

    @BeforeEach
    public void setUp() {
        file = mock(File.class);
        plugin = mock(Plugin.class);
        fileConfiguration = mock(FileConfiguration.class);
        when(plugin.getDataFolder()).thenReturn(new File("pluginDataFolder"));
        when(file.getName()).thenReturn("config.yml");
        configManager = spy(new ConfigManager(file, plugin));
        doReturn(fileConfiguration).when(configManager).getFileConfiguration(file);
    }

    @Test
    public void testConstructor_FileExists() {
        when(file.exists()).thenReturn(true);

        ConfigManager configManager = new ConfigManager(file, plugin);

        assertNotNull(configManager);
    }

    @Test
    public void testConstructor_FileDoesNotExist() {
        when(file.exists()).thenReturn(false);
        doNothing().when(configManager).saveDefaultConfig();

        ConfigManager configManager = new ConfigManager(file, plugin);

        verify(configManager, times(1)).saveDefaultConfig();
    }

    @Test
    public void testGetFileConfiguration() {
        FileConfiguration result = configManager.getFileConfiguration(file);

        assertNotNull(result);
    }

    @Test
    public void testSaveDefaultConfig() {
        doNothing().when(plugin).saveResource(anyString(), anyBoolean());

        configManager.saveDefaultConfig();

        verify(plugin, times(1)).saveResource("config.yml", false);
    }

    @Test
    public void testSaveConfig() throws IOException {
        doNothing().when(fileConfiguration).save(file);

        configManager.saveConfig();

        verify(fileConfiguration, times(1)).save(file);
    }

    @Test
    public void testReloadConfig() {
        doNothing().when(configManager).saveConfig();

        configManager.reloadConfig();

        verify(configManager, times(1)).saveConfig();
    }

    @Test
    public void testGetString() {
        when(fileConfiguration.getString("path")).thenReturn("value");

        String result = configManager.getString("path");

        assertEquals("value", result);
    }

    @Test
    public void testGetInt() {
        when(fileConfiguration.getInt("path")).thenReturn(42);

        int result = configManager.getInt("path");

        assertEquals(42, result);
    }

    @Test
    public void testGetBoolean() {
        when(fileConfiguration.getBoolean("path")).thenReturn(true);

        boolean result = configManager.getBoolean("path");

        assertTrue(result);
    }

    @Test
    public void testGetList() {
        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        when(fileConfiguration.getStringList("path")).thenReturn(list);

        List<String> result = configManager.getList("path");

        assertEquals(list, result);
    }

    @Test
    public void testGetItemFromConfig() {
        ItemStack itemStack = mock(ItemStack.class);
        when(fileConfiguration.getItemStack("path")).thenReturn(itemStack);

        ItemStack result = configManager.getItemFromConfig("path");

        assertEquals(itemStack, result);
    }
}

