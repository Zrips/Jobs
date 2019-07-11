package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YmlMaker {
    public String fileName;
    private JavaPlugin plugin;
    private File ConfigFile;
    private FileConfiguration Configuration;

    public YmlMaker(JavaPlugin plugin, String fileName) {
	if (plugin == null) {
	    throw new IllegalArgumentException("plugin cannot be null");
	}
	this.plugin = plugin;
	this.fileName = fileName;

	File dataFolder = plugin.getDataFolder();
	if (!dataFolder.exists())
		dataFolder.mkdirs();
	ConfigFile = new File(dataFolder, fileName);
    }

    public void reloadConfig() {
	InputStreamReader f = null;
	try {
	    f = new InputStreamReader(new FileInputStream(ConfigFile), "UTF-8");
	} catch (UnsupportedEncodingException | FileNotFoundException e1) {
	    e1.printStackTrace();
	}

	Configuration = YamlConfiguration.loadConfiguration(f);
	if (f != null)
	    try {
		f.close();
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
    }

    public FileConfiguration getConfig() {
	if (Configuration == null)
	    reloadConfig();
	return Configuration;
    }

    public File getConfigFile() {
	if (ConfigFile == null)
    	ConfigFile = new File(plugin.getDataFolder(), fileName);
	return ConfigFile;
    }

    public void saveConfig() {
	if ((Configuration == null) || (ConfigFile == null))
	    return;
	try {
	    getConfig().save(ConfigFile);
	} catch (IOException ex) {
	    plugin.getLogger().log(Level.SEVERE, "Could not save config to " + ConfigFile, ex);
	}
    }

    public boolean exists() {
    return ConfigFile != null && ConfigFile.exists() ? true : ConfigFile.exists();
    }

    public void createNewFile() {
	if (ConfigFile != null && !ConfigFile.exists()) {
	    try {
		ConfigFile.createNewFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public void saveDefaultConfig() {
	if (ConfigFile != null && !ConfigFile.exists())
	    plugin.saveResource(fileName, false);
    }
}
