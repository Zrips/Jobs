package com.gamingmesh.jobs.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Title;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;

public class TitleManager {

    protected final List<Title> titles = new ArrayList<>();

    /**
     * Function to return the title for a given level
     * @return the correct title
     * @return null if no title matches
     */
    public Title getTitle(int level, String jobName) {
	Title title = null;
	for (Title t : titles) {
	    if (title == null) {
		if (t.getLevelReq() <= level && (t.getJobName().isEmpty() || t.getJobName().equalsIgnoreCase(jobName)))
		    title = t;
	    } else {
		if (t.getLevelReq() <= level && t.getLevelReq() > title.getLevelReq() && (t.getJobName().isEmpty()
			|| t.getJobName().equalsIgnoreCase(jobName)))
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
    void load() {
	titles.clear();

	ConfigReader c;
	try {
	    c = new ConfigReader(Jobs.getInstance(), "titleConfig.yml");
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
	c.copyDefaults(true);

	c.header(Arrays.asList(
	    "Title configuration",
	    "Stores the titles people gain at certain levels.",
	    "Each title requires to have a name, short name (used when the player has more than 1 job)",
	    "the colour of the title and the level requirement to attain the title.",
	    "Colour can ether be a word like Brown, can be color character like &5 or hex color code like {#6600cc}",
	    "It is recommended but not required to have a title at level 0.",
	    "Titles are completely optional.",
	    "Possible variable are {level} to add current jobs level.",
	    "Optionally you can set different titles based by job.",
	    "  JobName: Miner"));

	ConfigurationSection titleSection = c.getC().getConfigurationSection("Titles");
	if (titleSection == null) {
	    c.getC().createSection("Titles");

	    titles.add(new Title(
		c.get("Titles.Novice.Name", "N"),
		c.get("Titles.Novice.ShortName", "N"),
		CMIChatColor.getColor(c.get("Titles.Novice.ChatColour", "YELLOW")),
		c.get("Titles.Novice.levelReq", 0),
		null));

	    titles.add(new Title(
		c.get("Titles.Apprentice.Name", "A"),
		c.get("Titles.Apprentice.ShortName", "A"),
		CMIChatColor.getColor(c.get("Titles.Apprentice.ChatColour", "WHITE")),
		c.get("Titles.Apprentice.levelReq", 25),
		null));

	    titles.add(new Title(
		c.get("Titles.Initiate.Name", "I"),
		c.get("Titles.Initiate.ShortName", "I"),
		CMIChatColor.getColor(c.get("Titles.Initiate.ChatColour", "GOLD")),
		c.get("Titles.Initiate.levelReq", 50),
		null));

	    titles.add(new Title(
		c.get("Titles.Journeyman.Name", "J"),
		c.get("Titles.Journeyman.ShortName", "J"),
		CMIChatColor.getColor(c.get("Titles.Journeyman.ChatColour", "DARK_GREEN")),
		c.get("Titles.Journeyman.levelReq", 75),
		null));

	    titles.add(new Title(
		c.get("Titles.Adept.Name", "Ad"),
		c.get("Titles.Adept.ShortName", "Ad"),
		CMIChatColor.getColor(c.get("Titles.Adept.ChatColour", "DARK_PURPLE")),
		c.get("Titles.Adept.levelReq", 100),
		null));

	    titles.add(new Title(
		c.get("Titles.Master.Name", "M"),
		c.get("Titles.Master.ShortName", "M"),
		CMIChatColor.getColor(c.get("Titles.Master.ChatColour", "GRAY")),
		c.get("Titles.Master.levelReq", 125),
		null));

	    titles.add(new Title(
		c.get("Titles.Grandmaster.Name", "GM"),
		c.get("Titles.Grandmaster.ShortName", "GM"),
		CMIChatColor.getColor(c.get("Titles.Grandmaster.ChatColour", "DARK_GRAY")),
		c.get("Titles.Grandmaster.levelReq", 150),
		null));

	    titles.add(new Title(
		c.get("Titles.Legendary.Name", "L"),
		c.get("Titles.Legendary.ShortName", "L"),
		CMIChatColor.getColor(c.get("Titles.Legendary.ChatColour", "BLACK")),
		c.get("Titles.Legendary.levelReq", 200),
		null));
	    c.save();
	} else
	    for (String titleKey : titleSection.getKeys(false)) {
		String titleName = titleSection.getString(titleKey + ".Name", "");

		if (titleName.isEmpty()) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid Name property. Skipping!");
		    continue;
		}

		String titleShortName = titleSection.getString(titleKey + ".ShortName", "");
		if (titleShortName.isEmpty()) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid ShortName property. Skipping!");
		    continue;
		}

		CMIChatColor titleColor = CMIChatColor.getColor(titleSection.getString(titleKey + ".ChatColour"));
		if (titleColor == null) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid ChatColour property. Skipping!");
		    continue;
		}

		int levelReq = titleSection.getInt(titleKey + ".levelReq", -1);
		if (levelReq <= -1) {
		    Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid levelReq property. Skipping!");
		    continue;
		}

		titles.add(new Title(titleName, titleShortName, titleColor, levelReq, titleSection.getString(titleKey + ".JobName")));
	    }

	int size = titles.size();
	if (size > 0)
	    Jobs.consoleMsg("&eLoaded &6" + size + " &etitles");
    }
}
