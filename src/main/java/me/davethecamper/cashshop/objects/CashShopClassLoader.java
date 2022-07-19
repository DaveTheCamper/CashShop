package me.davethecamper.cashshop.objects;
 
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.davethecamper.cashshop.api.CashShopGateway;

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
		} catch (Exception e) {
			e.printStackTrace();
		};
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
				this.loadClass(className);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
    	}
    }

}