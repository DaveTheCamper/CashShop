package me.davethecamper.cashshop.objects;

import me.davethecamper.cashshop.api.CashShopGateway;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class CashShopClassLoader extends URLClassLoader {


    private String main_class_name;
    
    private CashShopGateway gateway;
    
    

    public CashShopClassLoader(URL[] urls, ClassLoader parent, String main_class, JarFile jf) {
		super(urls, parent);
        this.main_class_name = main_class;
        
        loadAllClasses(jf);
        findMainClass();
	}
    
    public CashShopGateway getGateway() {
    	return this.gateway;
    }
    
    @SuppressWarnings("unchecked")
	public void findMainClass() {
    	 try {
    		 Class<?> main_class = Class.forName(main_class_name, true, this);
			
    		 Class<? extends CashShopGateway> gateway_class = (Class<? extends CashShopGateway>) main_class.asSubclass(main_class);
    		 Constructor<? extends CashShopGateway> constructor = gateway_class.getDeclaredConstructor();
			  
    		 this.gateway = constructor.newInstance();
		} catch (ClassNotFoundException exception)  {
		     throw new RuntimeException("A main class não existe " + main_class_name, exception);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
		     throw new RuntimeException(e);
	     }
    }

	private boolean isClassExistent() {
		try {
			Class.forName(main_class_name);
			return true;
		} catch (ClassNotFoundException exception) {
			return false;
		}
	}
    
    public void loadAllClasses(JarFile jf) {
    	Enumeration<JarEntry> e = jf.entries();

    	while (e.hasMoreElements()) {
    	    JarEntry je = e.nextElement();
    	    if(je.isDirectory() || !je.getName().endsWith(".class")){
    	        continue;
    	    }
    	    // -6 because of .class
    	    String className = je.getName().substring(0,je.getName().length()-6);
    	    className = className.replace('/', '.');
    	    
    	    try {
				System.out.println("Loading class " + className);
				this.loadClass(className, true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
    	}
    }

}