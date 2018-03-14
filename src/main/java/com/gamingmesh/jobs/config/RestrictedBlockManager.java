package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CmiItems.CMIItem;
import com.gamingmesh.jobs.container.LocaleReader;
import com.gamingmesh.jobs.stuff.ChatColor;

public class RestrictedBlockManager {

    public HashMap<Integer, Integer> restrictedBlocksTimer = new HashMap<Integer, Integer>();

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

	c.getW().addComment("blocksTimer", "Block protected by timer in sec",
	    "Category name can be any you like to be easily recognized",
	    "id can be actual block id (use /jobs blockinfo to get correct id) or use block name",
	    "By setting time to -1 will keep block protected until global cleanup, mainly used for structure blocks like diamond",
	    "If you want to have default value for all blocks, enale GlobalBlockTimer in generalConfig file");

	if (!c.getC().isConfigurationSection("blocksTimer")) {
	    c.getC().addDefault("blocksTimer.Sapling.id", 6);
	    c.getC().addDefault("blocksTimer.Sapling.cd", 60);
	    c.getC().addDefault("blocksTimer.leaves.id", 18);
	    c.getC().addDefault("blocksTimer.leaves.cd", 60);
	    c.getC().addDefault("blocksTimer.grass.id", "longgrass");
	    c.getC().addDefault("blocksTimer.grass.cd", 60);
	    c.getC().addDefault("blocksTimer.deadBush.id", 32);
	    c.getC().addDefault("blocksTimer.deadBush.cd", 60);

	    c.getC().addDefault("blocksTimer.rail.id", 66);
	    c.getC().addDefault("blocksTimer.rail.cd", 60);
	    c.getC().addDefault("blocksTimer.rail2.id", 27);
	    c.getC().addDefault("blocksTimer.rail2.cd", 60);
	    c.getC().addDefault("blocksTimer.rail3.id", 28);
	    c.getC().addDefault("blocksTimer.rail3.cd", 60);
	    c.getC().addDefault("blocksTimer.rail4.id", 157);
	    c.getC().addDefault("blocksTimer.rail4.cd", 60);

	    c.getC().addDefault("blocksTimer.web.id", 30);
	    c.getC().addDefault("blocksTimer.web.cd", 60);

	    c.getC().addDefault("blocksTimer.dandelion.id", 37);
	    c.getC().addDefault("blocksTimer.dandelion.cd", 60);
	    c.getC().addDefault("blocksTimer.poppy.id", 38);
	    c.getC().addDefault("blocksTimer.poppy.cd", 60);
	    c.getC().addDefault("blocksTimer.flower.id", 175);
	    c.getC().addDefault("blocksTimer.flower.cd", 60);
	    c.getC().addDefault("blocksTimer.mushroom.id", 39);
	    c.getC().addDefault("blocksTimer.mushroom.cd", 60);
	    c.getC().addDefault("blocksTimer.mushroomRed.id", 40);
	    c.getC().addDefault("blocksTimer.mushroomRed.cd", 60);

	    c.getC().addDefault("blocksTimer.torch.id", 50);
	    c.getC().addDefault("blocksTimer.torch.cd", 60);
	    c.getC().addDefault("blocksTimer.redTorch.id", 76);
	    c.getC().addDefault("blocksTimer.redTorch.cd", 60);

	    c.getC().addDefault("blocksTimer.lader.id", 65);
	    c.getC().addDefault("blocksTimer.lader.cd", 5);
	    c.getC().addDefault("blocksTimer.carpet.id", 171);
	    c.getC().addDefault("blocksTimer.carpet.cd", 60);

	    c.getC().addDefault("blocksTimer.button.id", 77);
	    c.getC().addDefault("blocksTimer.button.cd", 5);
	    c.getC().addDefault("blocksTimer.button2.id", 143);
	    c.getC().addDefault("blocksTimer.button2.cd", 5);
	    c.getC().addDefault("blocksTimer.lever.id", 69);
	    c.getC().addDefault("blocksTimer.lever.cd", 60);

	    c.getC().addDefault("blocksTimer.snow.id", 78);
	    c.getC().addDefault("blocksTimer.snow.cd", 60);
	    c.getC().addDefault("blocksTimer.snow2.id", 80);
	    c.getC().addDefault("blocksTimer.snow2.cd", 60);
	    c.getC().addDefault("blocksTimer.hook.id", 131);
	    c.getC().addDefault("blocksTimer.hook.cd", 60);
	    c.getC().addDefault("blocksTimer.tripWire.id", 132);
	    c.getC().addDefault("blocksTimer.tripWire.cd", 60);
	    c.getC().addDefault("blocksTimer.redstone.id", 55);
	    c.getC().addDefault("blocksTimer.redstone.cd", 60);
	    c.getC().addDefault("blocksTimer.repeater.id", 93);
	    c.getC().addDefault("blocksTimer.repeater.cd", 60);
	    c.getC().addDefault("blocksTimer.comparator.id", 149);
	    c.getC().addDefault("blocksTimer.comparator.cd", 60);

	    c.getC().addDefault("blocksTimer.lily.id", 111);
	    c.getC().addDefault("blocksTimer.lily.cd", 30);
	    c.getC().addDefault("blocksTimer.vines.id", 106);
	    c.getC().addDefault("blocksTimer.vines.cd", 30);
	    c.getC().addDefault("blocksTimer.wheat.id", 59);
	    c.getC().addDefault("blocksTimer.wheat.cd", 5);
	    c.getC().addDefault("blocksTimer.sugarcane.id", 83);
	    c.getC().addDefault("blocksTimer.sugarcane.cd", 30);
	    c.getC().addDefault("blocksTimer.cactus.id", 81);
	    c.getC().addDefault("blocksTimer.cactus.cd", 30);
	    c.getC().addDefault("blocksTimer.beatroot.id", 207);
	    c.getC().addDefault("blocksTimer.beatroot.cd", 60);
	    c.getC().addDefault("blocksTimer.potato.id", 142);
	    c.getC().addDefault("blocksTimer.potato.cd", 60);
	    c.getC().addDefault("blocksTimer.carrot.id", 141);
	    c.getC().addDefault("blocksTimer.carrot.cd", 60);
	    c.getC().addDefault("blocksTimer.warts.id", 115);
	    c.getC().addDefault("blocksTimer.warts.cd", 60);
	    c.getC().addDefault("blocksTimer.pumpkin.id", 86);
	    c.getC().addDefault("blocksTimer.pumpkin.cd", 30);
	    c.getC().addDefault("blocksTimer.pumpkinstem.id", 104);
	    c.getC().addDefault("blocksTimer.pumpkinstem.cd", 30);
	    c.getC().addDefault("blocksTimer.melon.id", 103);
	    c.getC().addDefault("blocksTimer.melon.cd", 30);
	    c.getC().addDefault("blocksTimer.melonstem.id", 105);
	    c.getC().addDefault("blocksTimer.melonstem.cd", 30);

	    c.getC().addDefault("blocksTimer.goldore.id", "goldore");
	    c.getC().addDefault("blocksTimer.goldore.cd", -1);
	    c.getC().addDefault("blocksTimer.ironore.id", "ironore");
	    c.getC().addDefault("blocksTimer.ironore.cd", -1);
	    c.getC().addDefault("blocksTimer.coalore.id", "coalore");
	    c.getC().addDefault("blocksTimer.coalore.cd", -1);
	    c.getC().addDefault("blocksTimer.lapisore.id", "lapisore");
	    c.getC().addDefault("blocksTimer.lapisore.cd", -1);
	    c.getC().addDefault("blocksTimer.diamondore.id", "diamondore");
	    c.getC().addDefault("blocksTimer.diamondore.cd", -1);
	    c.getC().addDefault("blocksTimer.redstoneore.id", "redstoneore");
	    c.getC().addDefault("blocksTimer.redstoneore.cd", -1);
	    c.getC().addDefault("blocksTimer.emeraldore.id", "emeraldore");
	    c.getC().addDefault("blocksTimer.emeraldore.cd", -1);
	    c.getC().addDefault("blocksTimer.quartzore.id", "quartzore");
	    c.getC().addDefault("blocksTimer.quartzore.cd", -1);
	}

	if (c.getC().isConfigurationSection("blocksTimer")) {
	    Set<String> lss = c.getC().getConfigurationSection("blocksTimer").getKeys(false);
	    for (String one : lss) {
		if (((c.getC().isString("blocksTimer." + one + ".id")) || (c.getC().isInt("blocksTimer." + one + ".id"))) && (c.getC().isInt("blocksTimer." + one
		    + ".cd"))) {		    
		    CMIItem cm = Jobs.getItemManager().getItem(c.getC().getString("blocksTimer." + one + ".id"));		    
		    if ((cm == null) || (!cm.getMaterial().isBlock())) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Jobs] Your defined (" + c.getC().getString(new StringBuilder("blocksTimer.").append(one)
			    .append(".id").toString()) + ") protected block id/name is not correct!");
		    } else {
			this.restrictedBlocksTimer.put(cm.getId(), c.getC().getInt("blocksTimer." + one + ".cd"));
		    }
		}
	    }
	}
	c.copySetting("blocksTimer");

	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + this.restrictedBlocksTimer.size() + " protected blocks timers!");
	try {
	    writer.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
