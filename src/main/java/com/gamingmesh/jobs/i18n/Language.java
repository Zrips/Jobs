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

import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.config.YmlMaker;

public class Language {

    public FileConfiguration enlocale, customlocale;

    private final Pattern patern = Pattern.compile("([ ]?[\\/][n][$|\\s])");

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
	    if (!customlocale.contains(key))
		msg = enlocale.isString(key) ? CMIChatColor.translate(enlocale.getString(key)) : missing;
	    else
		msg = customlocale.isString(key) ? CMIChatColor.translate(customlocale.getString(key)) : missing;
	} catch (Exception e) {
	    Jobs.consoleMsg("&e[Jobs] &2Can't read language file for: " + key);
	    Jobs.consoleMsg(e.getLocalizedMessage());
	    return "";
	}

	if (msg.isEmpty() || msg.equals(missing)) {
	    msg = "";
	    try {

		List<String> ls = null;

		if (customlocale.isList(key))
		    ls = colorsArray(customlocale.getStringList(key), true);
		else if (enlocale.isList(key)) {
		    ls = enlocale.getStringList(key);
		    ls = !ls.isEmpty() ? colorsArray(ls, true) : Arrays.asList(missing);
		}

		if (ls != null)
		    for (String one : ls) {
			if (!msg.isEmpty())
			    msg += "\n";
			msg += one;
		    }
	    } catch (Exception e) {
		Jobs.consoleMsg("&e[Jobs] &2Can't read language file for: " + key);
		Jobs.consoleMsg(e.getLocalizedMessage());
		return "";
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
	    ls = colorsArray(customlocale.getStringList(key), true);
	else {
	    ls = enlocale.getStringList(key);
	    ls = !ls.isEmpty() ? colorsArray(ls, true) : Arrays.asList(missing);
	}

	if (variables != null && variables.length > 0)
	    for (int i = 0; i < ls.size(); i++) {
		String msg = ls.get(i);
		for (int y = 0; y < variables.length; y += 2) {
		    msg = msg.replace(String.valueOf(variables[y]), String.valueOf(variables[y + 1]));
		}

		ls.set(i, CMIChatColor.translate(filterNewLine(msg)));
	    }

	return ls;
    }

    public String filterNewLine(String msg) {
	Matcher match = patern.matcher(msg);
	while (match.find()) {
	    msg = msg.replace(match.group(0), "\n");
	}
	return msg;
    }

    public List<String> colorsArray(List<String> text, boolean colorize) {
	List<String> temp = new ArrayList<>();

	for (String part : text) {
	    if (colorize)
		part = CMIChatColor.translate(part);

	    temp.add(part);
	}

	return temp;
    }
}
