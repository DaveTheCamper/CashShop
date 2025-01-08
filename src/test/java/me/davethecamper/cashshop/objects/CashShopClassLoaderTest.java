package me.davethecamper.cashshop.objects;

import me.davethecamper.cashshop.api.CashShopGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CashShopClassLoaderTest {

    private URL[] urls;
    private ClassLoader parent;
    private String mainClassName;
    private JarFile jarFile;
    private CashShopClassLoader cashShopClassLoader;

    @BeforeEach
    public void setUp() throws Exception {
        urls = new URL[] { new URL("file:/path/to/jar") };
        parent = mock(ClassLoader.class);
        mainClassName = "me.davethecamper.cashshop.api.CashShopGateway";
        jarFile = mock(JarFile.class);
        cashShopClassLoader = new CashShopClassLoader(urls, parent, mainClassName, jarFile);
    }

    @Test
    public void testConstructor() {
        assertNotNull(cashShopClassLoader);
        assertEquals(mainClassName, cashShopClassLoader.main_class_name);
    }

    @Test
    public void testGetGateway() {
        CashShopGateway gateway = mock(CashShopGateway.class);
        cashShopClassLoader.gateway = gateway;
        assertEquals(gateway, cashShopClassLoader.getGateway());
    }

    @Test
    public void testFindMainClass() throws Exception {
        Class<?> mainClass = mock(Class.class);
        when(mainClass.getDeclaredConstructor()).thenReturn(mock(Constructor.class));
        when(mainClass.getDeclaredConstructor().newInstance()).thenReturn(mock(CashShopGateway.class));
        doReturn(mainClass).when(cashShopClassLoader).loadClass(mainClassName, true);

        cashShopClassLoader.findMainClass();

        assertNotNull(cashShopClassLoader.getGateway());
    }

    @Test
    public void testIsClassExistent() {
        assertTrue(cashShopClassLoader.isClassExistent());
    }

    @Test
    public void testLoadAllClasses() throws Exception {
        JarEntry jarEntry = mock(JarEntry.class);
        when(jarEntry.getName()).thenReturn("me/davethecamper/cashshop/api/CashShopGateway.class");
        when(jarFile.entries()).thenReturn(new Enumeration<JarEntry>() {
            private final JarEntry[] entries = { jarEntry };
            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < entries.length;
            }

            @Override
            public JarEntry nextElement() {
                return entries[index++];
            }
        });

        cashShopClassLoader.loadAllClasses(jarFile);

        verify(cashShopClassLoader, times(1)).loadClass("me.davethecamper.cashshop.api.CashShopGateway", true);
    }
}