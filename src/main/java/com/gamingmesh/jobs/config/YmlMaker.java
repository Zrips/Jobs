package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;

public class YmlMaker {

    public String fileName;

    private File path;
    private File configFile;
    private FileConfiguration configuration;

    public YmlMaker(File path, File parent) {
	this(path, parent.getName());

	configFile = parent;
    }

    public YmlMaker(File path, String fileName) {
	this.path = path;
	this.fileName = fileName;

	configFile = new File(path, fileName);
    }

    public void reloadConfig() {
	InputStreamReader f = null;
	try {
	    f = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	}

	if (f == null) {
	    return;
	}

	configuration = YamlConfiguration.loadConfiguration(f);

	try {
	    f.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public FileConfiguration getConfig() {
	if (configuration == null)
	    reloadConfig();
	return configuration;
    }

    public File getConfigFile() {
	if (configFile == null)
	    configFile = new File(path, fileName);
	return configFile;
    }

    public void saveConfig() {
	if (configuration == null || configFile == null)
	    return;
	try {
	    getConfig().save(configFile);
	} catch (IOException ex) {
	    Jobs.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + configFile.getName(), ex);
	}
    }

    public boolean exists() {
    return configFile != null && configFile.exists();
    }

    public void createNewFile() {
	if (configFile != null && !configFile.exists()) {
	    try {
		configFile.createNewFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public void saveDefaultConfig() {
	if (configFile != null && !configFile.exists())
	    Jobs.getInstance().saveResource(fileName, false);
    }
}
