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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.config.YmlMaker;

public class Language {
    public FileConfiguration enlocale;
    public FileConfiguration customlocale;
    private Jobs plugin;

    public Language(Jobs plugin) {
	this.plugin = plugin;
    }

    /**
     * Reloads the config
     */
    public void reload() {
	String ls = Jobs.getGCManager().localeString.toLowerCase();
	customlocale = new YmlMaker(Jobs.getFolder(), "locale/messages_" + ls + ".yml").getConfig();
	enlocale = new YmlMaker(Jobs.getFolder(), "locale/messages_en.yml").getConfig();
	if (customlocale == null)
	    customlocale = enlocale;
    }

    /**
     * Get the message with the correct key
     * @param key - the key of the message
     * @return the message
     */
    public String getMessage(String key) {
	return getMessage(key, "");
    }

    public String getMessage(String key, Object... variables) {
	String missing = "MLF " + key;
	String msg = "";
	try {
	    if (customlocale == null || !customlocale.contains(key))
		msg = enlocale.isString(key) ? CMIChatColor.translate(enlocale.getString(key)) : missing;
	    else
		msg = customlocale.isString(key) ? CMIChatColor.translate(customlocale.getString(key)) : missing;
	} catch (Throwable e) {
	    Jobs.consoleMsg("&e[Jobs] &2Can't read language file for: " + key);
	    Bukkit.getServer().getPluginManager().disablePlugin(plugin);
	    e.printStackTrace();
	    throw e;
	}

	if (msg.isEmpty() || msg.equals(missing)) {
	    msg = "";
	    try {

		List<String> ls = null;
		if (customlocale != null && customlocale.isList(key))
		    ls = ColorsArray(customlocale.getStringList(key), true);
		else if (enlocale.isList(key))
		    ls = !enlocale.getStringList(key).isEmpty() ? ColorsArray(enlocale.getStringList(key), true) : Arrays.asList(missing);
		if (ls != null)
		    for (String one : ls) {
			if (!msg.isEmpty())
			    msg += "\n";
			msg += one;
		    }
	    } catch (Throwable e) {
		Jobs.consoleMsg("&e[Jobs] &2Can't read language file for: " + key);
		Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		e.printStackTrace();
		throw e;
	    }
	}

	if (variables != null && variables.length > 0)
	    for (int i = 0; i < variables.length; i++) {
		if (variables.length >= i + 2)
		    msg = msg.replace(String.valueOf(variables[i]), String.valueOf(variables[i + 1]));
		i++;
	    }

	return msg;
    }

    /**
     * Get the message with the correct key
     * @param key - the key of the message
     * @return the message
     */
    public List<String> getMessageList(String key, Object... variables) {
	String missing = "MLF " + key + " ";

	List<String> ls;
	if (customlocale.isList(key))
	    ls = ColorsArray(customlocale.getStringList(key), true);
	else
	    ls = !enlocale.getStringList(key).isEmpty() ? ColorsArray(enlocale.getStringList(key), true) : Arrays.asList(missing);

	if (variables != null && variables.length > 0)
	    for (int i = 0; i < ls.size(); i++) {
		String msg = ls.get(i);
		for (int y = 0; y < variables.length; y += 2) {
		    msg = msg.replace(String.valueOf(variables[y]), String.valueOf(variables[y + 1]));
		}
		msg = filterNewLine(msg);
		ls.set(i, CMIChatColor.translate(msg));
	    }

	return ls;
    }

    public String filterNewLine(String msg) {
	Pattern patern = Pattern.compile("([ ]?[\\/][n][$|\\s])");
	Matcher match = patern.matcher(msg);
	while (match.find()) {
	    msg = msg.replace(match.group(0), "\n");
	}
	return msg;
    }

    public List<String> ColorsArray(List<String> text, boolean colorize) {
	List<String> temp = new ArrayList<>();
	for (String part : text) {
	    if (colorize)
		part = CMIChatColor.translate(part);
	    temp.add(CMIChatColor.translate(part));
	}

	return temp;
    }

    /**
     * Get the message with the correct key
     * @param key - the key of the message
     * @return the message
     */
    public String getDefaultMessage(String key) {
	return enlocale.contains(key) ? CMIChatColor.translate(enlocale.getString(key)) : "Can't find locale";
    }

    /**
     * Check if key exists
     * @param key - the key of the message
     * @return true/false
     */
    public boolean containsKey(String key) {
	if (customlocale == null || !customlocale.contains(key))
	    return enlocale.contains(key);
	return customlocale.contains(key);
    }
}
