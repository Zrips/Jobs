/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.i18n;

import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.config.YmlMaker;

public class Language {
	public static FileConfiguration enlocale;
	public static FileConfiguration customlocale;

	static {
		customlocale = new YmlMaker((JavaPlugin) JobsPlugin.instance, "locale/messages_" + ConfigManager.getJobsConfiguration().localeString + ".yml").getConfig();
		enlocale = new YmlMaker((JavaPlugin) JobsPlugin.instance, "locale/messages_en.yml").getConfig();
		if (customlocale == null)
			customlocale = enlocale;
	}

	private Language() {
	}

	/**
	 * Reloads the config
	 */
	public static void reload(Locale locale) {
		customlocale = new YmlMaker((JavaPlugin) JobsPlugin.instance, "locale/messages_" + ConfigManager.getJobsConfiguration().localeString + ".yml").getConfig();
		enlocale = new YmlMaker((JavaPlugin) JobsPlugin.instance, "locale/messages_en.yml").getConfig();
		if (customlocale == null)
			customlocale = enlocale;
	}

	/**
	 * Get the message with the correct key
	 * @param key - the key of the message
	 * @return the message
	 */
    public static String getMessage(String key) {
		if (customlocale == null || !customlocale.contains(key))
			return enlocale.contains(key) == true ? ChatColor.translateAlternateColorCodes('&', enlocale.getString(key)) : "Cant find locale";
		return customlocale.contains(key) == true ? ChatColor.translateAlternateColorCodes('&', customlocale.getString(key)) : "Cant find locale";
	}

	/**
	 * Get the message with the correct key
	 * @param key - the key of the message
	 * @return the message
	 */
	public static String getDefaultMessage(String key) {
		return enlocale.contains(key) == true ? ChatColor.translateAlternateColorCodes('&', enlocale.getString(key)) : "Cant find locale";
	}
	
	/**
	 * Check if key exists
	 * @param key - the key of the message
	 * @return true/false
	 */
	public static boolean containsKey(String key) {
		if (customlocale == null || !customlocale.contains(key))
			return enlocale.contains(key);
		return customlocale.contains(key);
	}
}
