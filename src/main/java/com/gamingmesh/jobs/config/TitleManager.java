package com.gamingmesh.jobs.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.container.Title;
import com.gamingmesh.jobs.stuff.ChatColor;

public class TitleManager {

    protected List<Title> titles = new ArrayList<>();

    /**
     * Function to return the title for a given level
     * @return the correct title
     * @return null if no title matches
     */
    public Title getTitle(int level, String jobName) {
	Title title = null;
	for (Title t : titles) {
	    if (title == null) {
		if (t.getLevelReq() <= level && (t.getJobName() == null || t.getJobName().equalsIgnoreCase(jobName)))
		    title = t;
	    } else {
		if (t.getLevelReq() <= level && t.getLevelReq() > title.getLevelReq() && (t.getJobName() == null || t.getJobName().equalsIgnoreCase(jobName)))
		    title = t;
	    }
	}
	return title;
    }

    /**
     * Method to load the title configuration
     * 
     * loads from Jobs/titleConfig.yml
     */
    synchronized void load() {
	titles.clear();

	ConfigReader c = null;
	try {
	    c = new ConfigReader("titleConfig.yml");
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	if (c == null)
	    return;
	c.copyDefaults(true);

	c.header(Arrays.asList(
	    "Title configuration",
	    "Stores the titles people gain at certain levels.",
	    "Each title requres to have a name, short name (used when the player has more than",
	    "1 job) the colour of the title and the level requrirement to attain the title.",
	    "It is recommended but not required to have a title at level 0.",
	    "Titles are completely optional.",
	    "Posible variable are {level} to add current jobs level.",
	    "Optionaly you can set different titles based by job.",
	    "  JobName: Miner"));

	ConfigurationSection titleSection = c.getC().getConfigurationSection("Titles");
	if (titleSection == null) {
	    titleSection = c.getC().createSection("Titles");

	    titles.add(new Title(
		c.get("Titles.Novice.Name", "N"),
		c.get("Titles.Novice.ShortName", "N"),
		ChatColor.matchColor(c.get("Titles.Novice.ChatColour", "YELLOW")),
		c.get("Titles.Novice.levelReq", 0),
		null));

	    titles.add(new Title(
		c.get("Titles.Apprentice.Name", "A"),
		c.get("Titles.Apprentice.ShortName", "A"),
		ChatColor.matchColor(c.get("Titles.Apprentice.ChatColour", "WHITE")),
		c.get("Titles.Apprentice.levelReq", 25),
		null));

	    titles.add(new Title(
		c.get("Titles.Initiate.Name", "I"),
		c.get("Titles.Initiate.ShortName", "I"),
		ChatColor.matchColor(c.get("Titles.Initiate.ChatColour", "GOLD")),
		c.get("Titles.Initiate.levelReq", 50),
		null));

	    titles.add(new Title(
		c.get("Titles.Journeyman.Name", "J"),
		c.get("Titles.Journeyman.ShortName", "J"),
		ChatColor.matchColor(c.get("Titles.Journeyman.ChatColour", "DARK_GREEN")),
		c.get("Titles.Journeyman.levelReq", 75),
		null));

	    titles.add(new Title(
		c.get("Titles.Adept.Name", "Ad"),
		c.get("Titles.Adept.ShortName", "Ad"),
		ChatColor.matchColor(c.get("Titles.Adept.ChatColour", "DARK_PURPLE")),
		c.get("Titles.Adept.levelReq", 100),
		null));

	    titles.add(new Title(
		c.get("Titles.Master.Name", "M"),
		c.get("Titles.Master.ShortName", "M"),
		ChatColor.matchColor(c.get("Titles.Master.ChatColour", "GRAY")),
		c.get("Titles.Master.levelReq", 125),
		null));

	    titles.add(new Title(
		c.get("Titles.Grandmaster.Name", "GM"),
		c.get("Titles.Grandmaster.ShortName", "GM"),
		ChatColor.matchColor(c.get("Titles.Grandmaster.ChatColour", "DARK_GRAY")),
		c.get("Titles.Grandmaster.levelReq", 150),
		null));

	    titles.add(new Title(
		c.get("Titles.Legendary.Name", "L"),
		c.get("Titles.Legendary.ShortName", "L"),
		ChatColor.matchColor(c.get("Titles.Legendary.ChatColour", "BLACK")),
		c.get("Titles.Legendary.levelReq", 200),
		null));
	    c.save();
	} else
	    for (String titleKey : titleSection.getKeys(false)) {
		String jobName = null;
		String titleName = titleSection.getString(titleKey + ".Name");
		String titleShortName = titleSection.getString(titleKey + ".ShortName");
		ChatColor titleColor = ChatColor.matchColor(titleSection.getString(titleKey + ".ChatColour", ""));
		int levelReq = titleSection.getInt(titleKey + ".levelReq", -1);

		if (titleSection.isString(titleKey + ".JobName"))
		    jobName = titleSection.getString(titleKey + ".JobName");

		if (titleName == null) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid Name property. Skipping!");
		    continue;
		}

		if (titleShortName == null) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid ShortName property. Skipping!");
		    continue;
		}
		if (titleColor == null) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid ChatColour property. Skipping!");
		    continue;
		}
		if (levelReq <= -1) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid levelReq property. Skipping!");
		    continue;
		}

		titles.add(new Title(titleName, titleShortName, titleColor, levelReq, jobName));
	    }
	if (titles.size() > 0)
	    Jobs.consoleMsg("&e[Jobs] Loaded " + titles.size() + " titles!");
    }
}
