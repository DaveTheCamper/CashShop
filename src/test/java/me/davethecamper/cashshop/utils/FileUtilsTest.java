package me.davethecamper.cashshop.utils;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileUtilsTest {

    private Plugin plugin;
    private File file;
    private String customPath;

    @BeforeEach
    public void setUp() {
        plugin = mock(Plugin.class);
        file = mock(File.class);
        customPath = "testPath";
    }

    @Test
    public void testSaveFileFromResources_FileNotExists() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8));
        when(plugin.getClass().getClassLoader().getResourceAsStream(customPath)).thenReturn(inputStream);
        when(file.exists()).thenReturn(false);
        when(file.getParentFile()).thenReturn(mock(File.class));
        when(file.createNewFile()).thenReturn(true);

        FileUtils.saveFileFromResources(plugin, file, customPath);

        verify(file, times(1)).createNewFile();
    }

    @Test
    public void testSaveFileFromResources_FileExists() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8));
        when(plugin.getClass().getClassLoader().getResourceAsStream(customPath)).thenReturn(inputStream);
        when(file.exists()).thenReturn(true);

        FileUtils.saveFileFromResources(plugin, file, customPath);

        verify(file, never()).createNewFile();
    }

    @Test
    public void testSaveFileFromResources_ResourceNotFound() {
        when(plugin.getClass().getClassLoader().getResourceAsStream(customPath)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            FileUtils.saveFileFromResources(plugin, file, customPath);
        });

        assertEquals("Arquivo de recursos não encontrado: " + customPath, exception.getMessage());
    }

    @Test
    public void testSaveFileFromResources_ExceptionWhileCreatingFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8));
        when(plugin.getClass().getClassLoader().getResourceAsStream(customPath)).thenReturn(inputStream);
        when(file.exists()).thenReturn(false);
        when(file.getParentFile()).thenReturn(mock(File.class));
        doThrow(new IOException("Error creating file")).when(file).createNewFile();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            FileUtils.saveFileFromResources(plugin, file, customPath);
        });

        assertEquals("Ocorreu um erro ao criar o arquivo vazio.", exception.getMessage());
    }

    @Test
    public void testSaveFileFromResources_ExceptionWhileSavingFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8));
        when(plugin.getClass().getClassLoader().getResourceAsStream(customPath)).thenReturn(inputStream);
        when(file.exists()).thenReturn(true);
        doThrow(new IOException("Error saving file")).when(file).toPath();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            FileUtils.saveFileFromResources(plugin, file, customPath);
        });

        assertTrue(exception.getMessage().contains("Ocorreu um arro ao salvar o arquivo " + customPath));
    }
}