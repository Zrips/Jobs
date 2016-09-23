package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.LocaleReader;
import com.gamingmesh.jobs.stuff.ChatColor;

public class RestrictedBlockManager {

    public ArrayList<String> restrictedBlocks = new ArrayList<String>();
    public HashMap<Integer, Integer> restrictedBlocksTimer = new HashMap<Integer, Integer>();
    public ArrayList<Integer> restrictedPlaceBlocksTimer = new ArrayList<Integer>();

    private Jobs plugin;

    public RestrictedBlockManager(Jobs plugin) {
	this.plugin = plugin;
    }

    /**
     * Method to load the restricted areas configuration
     * 
     * loads from Jobs/restrictedAreas.yml
     */
    public synchronized void load() {
	File f = new File(plugin.getDataFolder(), "restrictedBlocks.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	CommentedYamlConfiguration writer = new CommentedYamlConfiguration();

	LocaleReader c = new LocaleReader(config, writer);

	config.options().copyDefaults(true);

	c.getW().addComment("restrictedblocks", "All block to be protected from place/break exploit.", "This will prevent piston moving all blocks in list",
	    "Dont count in vegetables or any single click break blocks");
	restrictedBlocks.add("14");
	restrictedBlocks.add("15");
	restrictedBlocks.add("16");
	restrictedBlocks.add("21");
	restrictedBlocks.add("48");
	restrictedBlocks.add("56");
	restrictedBlocks.add("73");
	restrictedBlocks.add("74");
	restrictedBlocks.add("129");
	restrictedBlocks.add("153");
	c.getC().addDefault("restrictedblocks", restrictedBlocks);
	restrictedBlocks = (ArrayList<String>) c.getC().getStringList("restrictedblocks");
	c.copySetting("restrictedblocks");

	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + restrictedBlocks.size() + " restricted blocks!");

	c.getW().addComment("blockstimer", "Block protected by timer in sec",
	    "141-60 means that carrot can be harvested after 60 sec (remember to use id's from placed objects, not from your inventory)");
	ArrayList<String> ls = new ArrayList<String>();
	ls.addAll(Arrays.asList("2-60",
	    "3-60",
	    "6-60",
	    "12-60",
	    "18-60",
	    "31-60",
	    "32-60",
	    "37-60",
	    "38-60",
	    "39-60",
	    "40-60",
	    "55-60",
	    "59-60",
	    "80-60",
	    "81-60",
	    "83-60",
	    "103-60",
	    "106-60",
	    "111-60",
	    "141-60",
	    "142-60",
	    "161-60",
	    "171-60",
	    "175-60"));
	c.getC().addDefault("blockstimer", ls);
	ls = (ArrayList<String>) c.getC().getStringList("blockstimer");

	for (String one : ls) {

	    if (!one.contains("-"))
		continue;

	    int id = 0;
	    int timer = 0;

	    try {
		id = Integer.parseInt(one.split("-")[0]);
		timer = Integer.parseInt(one.split("-")[1]);
	    } catch (NumberFormatException e) {
		continue;
	    }
	    restrictedBlocksTimer.put(id, timer);

	}

	c.copySetting("blockstimer");

	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + restrictedBlocksTimer.size() + " restricted blocks timers!");

	c.getW().addComment("PlacedBlockTimer", "Block place protected by timer in sec", "For this to work CoreProtect plugin should be installed");
	restrictedPlaceBlocksTimer.add(2);
	restrictedPlaceBlocksTimer.add(3);
	restrictedPlaceBlocksTimer.add(6);
	restrictedPlaceBlocksTimer.add(12);
	restrictedPlaceBlocksTimer.add(18);
	restrictedPlaceBlocksTimer.add(31);
	restrictedPlaceBlocksTimer.add(32);
	restrictedPlaceBlocksTimer.add(37);
	restrictedPlaceBlocksTimer.add(38);
	restrictedPlaceBlocksTimer.add(39);
	restrictedPlaceBlocksTimer.add(40);
	restrictedPlaceBlocksTimer.add(55);
	restrictedPlaceBlocksTimer.add(59);
	restrictedPlaceBlocksTimer.add(80);
	restrictedPlaceBlocksTimer.add(81);
	restrictedPlaceBlocksTimer.add(83);
	restrictedPlaceBlocksTimer.add(103);
	restrictedPlaceBlocksTimer.add(106);
	restrictedPlaceBlocksTimer.add(111);
	restrictedPlaceBlocksTimer.add(141);
	restrictedPlaceBlocksTimer.add(142);
	restrictedPlaceBlocksTimer.add(161);
	restrictedPlaceBlocksTimer.add(171);
	restrictedPlaceBlocksTimer.add(175);
	c.getC().addDefault("PlacedBlockTimer", restrictedPlaceBlocksTimer);
	restrictedPlaceBlocksTimer = (ArrayList<Integer>) c.getC().getIntegerList("PlacedBlockTimer");
	c.copySetting("PlacedBlockTimer");
	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + restrictedPlaceBlocksTimer.size() + " restricted place blocks timers!");

	try {
	    writer.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
