package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;

public class ToggleBarHandling {

    static Map<String, Boolean> actionBarToggle = new HashMap<>();
    static Map<String, Boolean> bossBarToggle = new HashMap<>();

    public static void load() {
	YmlMaker f = new YmlMaker(Jobs.getFolder(), "actionBarBossbar.yml");
	if (!f.exists())
	    return;

	FileConfiguration config = f.getConfig();

	if (Jobs.getGCManager().BossBarEnabled) {
	    ConfigurationSection section = config.getConfigurationSection("bossBar");

	    if (section != null) {
		for (String one : section.getKeys(false)) {
		    if (!section.getBoolean(one)) {
			bossBarToggle.put(one, section.getBoolean(one));
		    }
		}
	    }
	}

	if (Jobs.getGCManager().ActionBarsMessageByDefault) {
	    ConfigurationSection section = config.getConfigurationSection("actionBar");

	    if (section != null) {
		for (String one : section.getKeys(false)) {
		    if (!section.getBoolean(one)) {
			actionBarToggle.put(one, section.getBoolean(one));
		    }
		}
	    }
	}
    }

    public static void save() {
	YmlMaker f = new YmlMaker(Jobs.getFolder(), "actionBarBossbar.yml");
	if (!f.exists())
	    f.createNewFile();

	f.saveDefaultConfig();

	FileConfiguration config = f.getConfig();

	if (Jobs.getGCManager().BossBarEnabled) {
	    config.set("bossBar", null);

	    if (!bossBarToggle.isEmpty()) {
		for (Entry<String, Boolean> one : bossBarToggle.entrySet()) {
		    if (!one.getValue()) {
			config.set("bossBar." + one.getKey(), one.getValue());
		    }
		}
	    }
	}

	if (Jobs.getGCManager().ActionBarsMessageByDefault) {
	    config.set("actionBar", null);

	    if (!actionBarToggle.isEmpty()) {
		for (Entry<String, Boolean> one : actionBarToggle.entrySet()) {
		    if (!one.getValue()) {
			config.set("actionBar." + one.getKey(), one.getValue());
		    }
		}
	    }
	}

	bossBarToggle.clear();
	actionBarToggle.clear();

	f.saveConfig();
    }

    public static Map<String, Boolean> getActionBarToggle() {
	return actionBarToggle;
    }

    public static Map<String, Boolean> getBossBarToggle() {
	return bossBarToggle;
    }
}
