package me.davethecamper.cashshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ConfigManager {
	
	public ConfigManager(File file) {
		this.file = file;
		this.file_name = file.getName();
		loadMessages(YamlConfiguration.loadConfiguration(file), "");
	}
	
	private File file;
	private String file_name;
	
	private HashMap<String, Object> messages = new HashMap<>();
	private HashMap<String, ItemStack> items = new HashMap<>();
	
	private Object get(String s) {
		if (messages.get(s) == null) {
			Bukkit.getConsoleSender().sendMessage("§4[ERROR] §6CashShop -> §cmissing configuration §4" + s + " §cin file §4" + this.file_name);
			registerDefault(s);
			return "§d§oMissing message §d§o" + s;
		}
		return messages.get(s);
	}
	
	public String getString(String s) {return ((String) get(s)).replaceAll("&", "§");}
	
	public int getInt(String s) {return ((Integer) get(s));}
	
	public List<?> getList(String s) {
		Object val = get(s);
		List<?> list = val instanceof List ? (List<?>) val : new ArrayList<>();
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStringList(String s) {
		List<String> list = (List<String>) getList(s);
		List<String> new_list = new ArrayList<>();
		for (String p : new ArrayList<>(list)) {new_list.add(p.replaceAll("&", "§"));}
		return new_list;
	}
	
	public String getStringAsItemLore(String s) {
        List<?> list = getList(s);
		
		String lore = "";

        for (Object object : list) {
            if ((object instanceof String) || (isPrimitiveWrapper(object))) {
                lore = lore + String.valueOf(object).replaceAll("&", "§") + ";=;";
            }
        }
        
        if (lore.length() > 0) lore.substring(0, lore.length()-3);
        
		return lore;
	}
	
	public ItemStack getItemFromConfig(String s) {
		if (items.get(s) == null) {
			items.put(s, ItemGenerator.getItemStack(
					this.getString(s + ".material"), 
					this.getString(s + ".name"), 
					this.getStringAsItemLore(s + ".lore")));
		}
		return items.get(s);
	}
	
	private void loadMessages(FileConfiguration fc, String path) {
		if (fc.get(path) != null) {
			if (!(fc.get(path) instanceof ConfigurationSection)) {
				messages.put(path.subSequence(1, path.length()).toString(), fc.get(path));
			} else {
				for (String s : fc.getConfigurationSection(path).getKeys(false)) {
					loadMessages(fc, path + "." + s);
				}
			}
		}
	}
	
	private boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean ||
                input instanceof Character || input instanceof Byte ||
                input instanceof Short || input instanceof Double ||
                input instanceof Long || input instanceof Float;
    }
	
	
	private static final String DEFAULT = "MISSING_CONFIGURATION_SETTING_DEFAULT";
	
	private void registerDefault(String path) {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
		
		fc.set(path, DEFAULT);
		
		try {
			fc.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
