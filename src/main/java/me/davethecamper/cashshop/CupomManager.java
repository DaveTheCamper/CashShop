package me.davethecamper.cashshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.davethecamper.cashshop.objects.Cupom;

public class CupomManager {
	
	public CupomManager() {
		load();
	}
	
	private final String PATH = Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME).getDataFolder().getAbsolutePath() + "/cupons.yml";
	
	private HashMap<String, Cupom> cupons = new HashMap<>();
	

	public void addTransaction(String name, String token, double amount) {
		if (this.isValid(name)) {
			cupons.get(name).addUsage(token, amount);
		}
	}
	
	public void addCupom(String name, double percentage, int duration) {
		cupons.put(name, new Cupom(name, percentage, System.currentTimeMillis() + (((long)duration)*60*60*1000)));
	}
	
	public boolean removeCupom(String name) {
		if (!isValid(name)) return false;
		cupons.remove(name);
		return true;
	}
	
	public boolean isValid(String name) {
		return cupons.containsKey(name);
	}
	
	public double getDiscount(String name) {
		if (!isValid(name)) return 0;
		return cupons.get(name).getPercetage();
	}
	
	
	private void load() {
		File f = new File(PATH);
		
		if (f.exists()) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			
			if (fc.get("cupons") != null) {
				for (String s : fc.getConfigurationSection("cupons").getKeys(false)) {
					long expiration = fc.getLong("cupons." + s + ".expiration");
					double percentage = fc.getDouble("cupons." + s + ".percentage");
					HashMap<String, Double> usages = new HashMap<>();
					
					if (fc.get("cupons." + s + ".usages") != null) {
						for (String token : fc.getConfigurationSection("cupons." + s + ".usages.").getKeys(false)) {
							usages.put(token, fc.getDouble("cupons." + s + ".usages." + token));
						}
					}
					
					cupons.put(s, new Cupom(s, percentage, expiration, usages));
				}
			}
		}
	}
	
	public void save() {
		File f = new File(PATH);
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		fc.set("cupons", null);
		
		for (String identifier : new ArrayList<>(cupons.keySet())) {
			Cupom c = cupons.get(identifier);
			if (c.isExpirated()) {
				cupons.remove(identifier);
				continue;
			}
			
			fc.set("cupons." + identifier + ".expiration", c.getExpiration());
			fc.set("cupons." + identifier + ".percentage", c.getPercetage());
			
			for (String token : new ArrayList<>(c.getUsages().keySet())) {
				fc.set("cupons." + identifier + ".usages." + token, c.getUsages().get(token));
			}
		}
		
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
