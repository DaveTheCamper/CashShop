package me.davethecamper.cashshop;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ConfigManager {

	public ConfigManager(File file) {
		this(file, tryRetreivePlugin(file));
	}
	
	public ConfigManager(File file, Plugin plugin) {
		this.file = file;
		this.file_name = file.getName();
		this.plugin = plugin;
		loadMessages(YamlConfiguration.loadConfiguration(file), "");
	}
	
	private Plugin plugin;
	private File file;
	private String file_name;
	
	private HashMap<String, Object> messages = new HashMap<>();
	private HashMap<String, ItemStack> items = new HashMap<>();
	private HashMap<String, ConfigurationSection> configurationSections = new HashMap<>();
	
	public Object get(String s) {
		if (!contains(s)) {
			tryRegisterFromResources(s);

			if (messages.get(s) == null && configurationSections.get(s) == null) {
				Bukkit.getConsoleSender().sendMessage("§4[ERROR] §6CashShop -> §cmissing configuration §4" + s + " §cin file §4" + this.file_name);
				registerDefault(s);
				return "§d§oMissing message §d§o" + s;
			}
		}
		return messages.get(s) == null ? configurationSections.get(s) : messages.get(s);
	}
	
	private static Plugin tryRetreivePlugin(File f) {
		try {
			String path = f.getAbsolutePath();
			int index = path.indexOf("plugins");
			String result = path.substring(index + 8, path.length());
			index = result.indexOf('/') != -1 ? result.indexOf('/') : result.indexOf('\\');
			
			return Bukkit.getPluginManager().getPlugin(result.substring(0, result.indexOf('/')));
		} catch (Exception e) {}
		
		return null;
	}
	
	private void tryRegisterFromResources(String path) {
		try {
			if (plugin.getResource(this.file_name) != null) {
				FileConfiguration fc = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(this.file_name)));
				
				if (fc.get(path) != null) {
					messages.put(path, fc.get(path));
					
					FileConfiguration original = YamlConfiguration.loadConfiguration(file);
					original.set(path, fc.get(path));
					
					try {
						original.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					loadMessages(original, "." + path);
				}
			}
		} catch(Exception e) {}
	}
	
	public boolean contains(String s) {
		return messages.get(s) != null || configurationSections.get(s) != null;
	}

	public String getString(String s, String...args) {
		String result = ChatColor.translateAlternateColorCodes('&', ((String) get(s)));
		
		if (args.length % 2 == 0) {
			for (int arg = 0; arg < args.length; arg += 2) {
				result = result.replaceAll(args[arg], args[arg+1]);
			}
		}
		
		return result;
	}
	
	public int getInt(String s) {return ((Integer) get(s));}
	
	public double getDouble(String s) {return get(s) instanceof Double ? ((Double) get(s)) : getInt(s);}
	
	public boolean getBoolean(String s) {return ((Boolean) get(s));}
	
	public List<?> getList(String s) {
		Object val = get(s);
		List<?> list = val instanceof List ? (List<?>) val : new ArrayList<>();
		
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList(String s, String...args) {
		List<String> list = (List<String>) getList(s);
		List<String> new_list = new ArrayList<>();
		for (String p : new ArrayList<>(list)) {new_list.add(ChatColor.translateAlternateColorCodes('&', p));}

		if (args.length % 2 == 0) {
			for (int i = 0; i < new_list.size(); i++) {
				String newStr = new_list.get(i);
				for (int arg = 0; arg < args.length; arg += 2) {
					newStr = newStr.replaceAll(args[arg], args[arg+1]);
				}
				
				if (newStr.contains("\n")) {
					String words[] = newStr.split("\n");
					new_list.set(i, words[0]);
					
					for (int y = words.length-1; y > 0; y--) {
						shiftDown(new_list, i+1);
						new_list.set(i+1, words[y]);
					}
				} else {
					new_list.set(i, newStr);
				}
			}
		}
		
		return new_list;
	}
	
	private void shiftDown(List<String> list, int position) {
		list.add("");
		
		for (int i = list.size()-1; i > position; i--) {
			list.set(i, list.get(i-1));
		}
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
	
	public ConfigurationSection getConfigurationSection(String path) {
		return configurationSections.get(path);
	}
	
	private void loadMessages(FileConfiguration fc, String path) {
		if (fc.get(path) != null) {
			if (!(fc.get(path) instanceof ConfigurationSection)) {
				messages.put(path.subSequence(1, path.length()).toString(), fc.get(path));
			} else {
				if (path.length() > 0) configurationSections.put(path.subSequence(1, path.length()).toString(), (ConfigurationSection) fc.get(path));
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
