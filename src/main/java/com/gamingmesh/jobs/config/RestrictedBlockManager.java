package com.gamingmesh.jobs.config;

import java.util.HashMap;
import java.util.Map;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIItemStack;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.ItemManager;

public class RestrictedBlockManager {

    public final Map<CMIMaterial, Integer> restrictedBlocksTimer = new HashMap<>();

    /**
     * Method to load the restricted blocks configuration
     * loads from Jobs/restrictedBlocks.yml
     */
    public void load() {
	if (!Jobs.getGCManager().useBlockProtection)
	    return;

	ConfigReader cfg = new ConfigReader("restrictedBlocks.yml");

	cfg.addComment("blocksTimer", "Block protected by timer in sec",
	    "Category name can be any you like to be easily recognized",
	    "id can be actual block id (use /jobs blockinfo to get correct id) or use block name",
	    "By setting time to -1 will keep block protected until global cleanup, mainly used for structure blocks like diamond",
	    "If you want to have default value for all blocks, enable GlobalBlockTimer in generalConfig file");

	org.bukkit.configuration.ConfigurationSection section = cfg.getC().getConfigurationSection("blocksTimer");
	if (section != null) {
	    for (String one : section.getKeys(false)) {
		if ((section.isString(one + ".id") || section.isInt(one + ".id")) && section.isInt(one + ".cd")) {
		    CMIItemStack cm = ItemManager.getItem(CMIMaterial.get(section.getString(one + ".id")));

		    if (cm == null || !cm.getCMIType().isBlock()) {
			Jobs.consoleMsg("&e[Jobs] Your defined (" + one + ") protected block id/name is not correct!");
			continue;
		    }

		    int cd = section.getInt(one + ".cd");
		    restrictedBlocksTimer.put(cm.getCMIType(), cd);
		    cfg.set("blocksTimer." + cm.getCMIType().name(), cd);
		} else {
		    CMIMaterial mat = CMIMaterial.get(one);
		    if (mat == CMIMaterial.NONE)
			continue;

		    int timer = cfg.get("blocksTimer." + one, -99);
		    if (timer == -99) {
			cfg.set("blocksTimer." + one, null);
			continue;
		    }

		    cfg.set("blocksTimer." + one, null);
		    cfg.get("blocksTimer." + mat.name(), timer);

		    if (!mat.isBlock()) {
			Jobs.consoleMsg("&e[Jobs] Your defined (" + one + ") protected block id/name is not correct!");
			continue;
		    }

		    restrictedBlocksTimer.put(mat, timer);
		}
	    }
	}

	if (restrictedBlocksTimer.size() > 0)
	    Jobs.consoleMsg("&e[Jobs] Loaded " + restrictedBlocksTimer.size() + " protected blocks timers!");

	cfg.save();
    }
}
