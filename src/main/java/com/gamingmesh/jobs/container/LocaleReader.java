package com.gamingmesh.jobs.container;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;

import com.gamingmesh.jobs.config.CommentedYamlConfiguration;

public class LocaleReader {
    YamlConfiguration config;
    CommentedYamlConfiguration writer;

    public LocaleReader(YamlConfiguration config, CommentedYamlConfiguration writer) {
	this.config = config;
	this.writer = writer;
    }

    public CommentedYamlConfiguration getW() {
	return writer;
    }

    public YamlConfiguration getC() {
	return config;
    }

    public Boolean get(String path, Boolean boo) {
	config.addDefault(path, boo);
	copySetting(path);
	return config.getBoolean(path);
    }

    public int get(String path, int boo) {
	config.addDefault(path, boo);
	copySetting(path);
	return config.getInt(path);
    }

    public List<Integer> getIntList(String path, List<Integer> list) {
	config.addDefault(path, list);
	copySetting(path);
	return config.getIntegerList(path);
    }

    public List<String> getStringList(String path, List<String> list) {
	config.addDefault(path, list);
	copySetting(path);
	return config.getStringList(path);
    }

    public List<String> get(String path, List<String> list) {
	config.addDefault(path, list);
	copySetting(path);
	return config.getStringList(path);
    }

    public String get(String path, String boo) {
	config.addDefault(path, boo);
	copySetting(path);
	return get(path, boo, true);
    }

    public String get(String path, String boo, boolean colorize) {
	config.addDefault(path, boo);
	copySetting(path);
	if (colorize)
	    return ChatColor.translateAlternateColorCodes('&', config.getString(path));
	return config.getString(path);
    }

    public Double get(String path, Double boo) {
	config.addDefault(path, boo);
	copySetting(path);
	return config.getDouble(path);
    }

    public synchronized void copySetting(String path) {
	writer.set(path, config.get(path));
    }
}
