package com.gamingmesh.jobs.stuff;

import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;

public class ToggleBarHandling {

    static WeakHashMap<String, Boolean> actionBarToggle = new WeakHashMap<>();
    static WeakHashMap<String, Boolean> bossBarToggle = new WeakHashMap<>();

    public static void load() {
	YmlMaker f = new YmlMaker(Jobs.getInstance(), "actionBarBossbar.yml");
	if (!f.exists())
	    return;

	FileConfiguration config = f.getConfig();

	if (Jobs.getGCManager().BossBarEnabled) {
	    ConfigurationSection section = config.getConfigurationSection("bossBar");

	    if (section != null) {
		for (String one : section.getKeys(false)) {
		    bossBarToggle.put(one, section.getBoolean(one));
		}
	    }
	}

	if (Jobs.getGCManager().ActionBarsMessageByDefault) {
	    ConfigurationSection section = config.getConfigurationSection("actionBar");

	    if (section != null) {
		for (String one : section.getKeys(false)) {
		    actionBarToggle.put(one, section.getBoolean(one));
		}
	    }
	}
    }

    public static void save() {
	YmlMaker f = new YmlMaker(Jobs.getInstance(), "actionBarBossbar.yml");
	if (!f.exists())
	    f.createNewFile();

	f.saveDefaultConfig();

	FileConfiguration config = f.getConfig();

	if (Jobs.getGCManager().BossBarEnabled) {
	    config.set("bossBar", null);

	    for (Entry<String, Boolean> one : bossBarToggle.entrySet()) {
		config.set("bossBar." + one.getKey(), one.getValue());
	    }
	}

	if (Jobs.getGCManager().ActionBarsMessageByDefault) {
	    config.set("actionBar", null);

	    for (Entry<String, Boolean> one : actionBarToggle.entrySet()) {
		config.set("actionBar." + one.getKey(), one.getValue());
	    }
	}

	bossBarToggle.clear();
	actionBarToggle.clear();

	f.saveConfig();
    }

    public static WeakHashMap<String, Boolean> getActionBarToggle() {
	return actionBarToggle;
    }

    public static WeakHashMap<String, Boolean> getBossBarToggle() {
	return bossBarToggle;
    }
}
