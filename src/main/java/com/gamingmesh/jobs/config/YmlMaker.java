package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;

public class YmlMaker {
    Jobs Plugin;
    public String fileName;
    private JavaPlugin plugin;
    public File ConfigFile;
    private FileConfiguration Configuration;

    public YmlMaker(Jobs Plugin) {
	this.Plugin = Plugin;
    }

    public YmlMaker(JavaPlugin plugin, String fileName) {
	if (plugin == null) {
	    throw new IllegalArgumentException("plugin cannot be null");
	}
	this.plugin = plugin;
	this.fileName = fileName;
	File dataFolder = plugin.getDataFolder();
	if (dataFolder == null) {
	    throw new IllegalStateException();
	}
	this.ConfigFile = new File(dataFolder.toString() + File.separatorChar + this.fileName);
    }

    public void reloadConfig() {
	InputStreamReader f = null;
	try {
	    f = new InputStreamReader(new FileInputStream(this.ConfigFile), "UTF-8");
	} catch (UnsupportedEncodingException e1) {
	    e1.printStackTrace();
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	}

	this.Configuration = YamlConfiguration.loadConfiguration(f);
	if (f != null)
	    try {
		f.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
    }

    public FileConfiguration getConfig() {
	if (this.Configuration == null) {
	    reloadConfig();
	}
	return this.Configuration;
    }

    public void saveConfig() {
	if ((this.Configuration == null) || (this.ConfigFile == null)) {
	    return;
	}
	try {
	    getConfig().save(this.ConfigFile);
	} catch (IOException ex) {
	    this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.ConfigFile, ex);
	}
    }

    public boolean exists() {
	return this.ConfigFile.exists();
    }

    public void createNewFile() {
	if (!this.ConfigFile.exists()) {
	    try {
		this.ConfigFile.createNewFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public void saveDefaultConfig() {
	if (!this.ConfigFile.exists()) {
	    this.plugin.saveResource(this.fileName, false);
	}
    }
}
