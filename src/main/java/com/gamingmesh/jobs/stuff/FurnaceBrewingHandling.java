package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;

public class FurnaceBrewingHandling {

    public FurnaceBrewingHandling() {
    }

    static HashMap<UUID, List<blockLoc>> furnaceMap = new HashMap<UUID, List<blockLoc>>();
    static HashMap<UUID, List<blockLoc>> brewingMap = new HashMap<UUID, List<blockLoc>>();

    public static void load() {
	YmlMaker f = new YmlMaker(Jobs.getInstance(), "furnaceBrewingStands.yml");
	if (!f.exists())
	    return;

	FileConfiguration config = f.getConfig();

	int totalf = 0;
	int totalb = 0;

	if (Jobs.getGCManager().isFurnacesReassign())
	    if (config.contains("Furnace")) {
		ConfigurationSection section = config.getConfigurationSection("Furnace");

		try {
		    for (String one : section.getKeys(false)) {
			String value = section.getString(one);
			List<String> ls = new ArrayList<String>();
			if (value.contains(";"))
			    ls.addAll(Arrays.asList(value.split(";")));
			else
			    ls.add(value);
			UUID uuid = UUID.fromString(one);

			if (uuid == null)
			    continue;
			List<blockLoc> blist = new ArrayList<blockLoc>();
			for (String oneL : ls) {
			    blockLoc bl = new blockLoc(oneL);
			    Block block = bl.getBlock();
			    if (block == null)
				continue;

			    block.removeMetadata(JobsPaymentListener.furnaceOwnerMetadata, Jobs.getInstance());
			    block.setMetadata(JobsPaymentListener.furnaceOwnerMetadata, new FixedMetadataValue(Jobs.getInstance(), one));

			    blist.add(bl);
			}
			if (!blist.isEmpty()) {
			    furnaceMap.put(uuid, blist);
			    totalf += blist.size();
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	if (Jobs.getGCManager().isBrewingStandsReassign())
	    if (config.contains("Brewing")) {
		ConfigurationSection section = config.getConfigurationSection("Brewing");

		try {
		    for (String one : section.getKeys(false)) {
			String value = section.getString(one);
			List<String> ls = new ArrayList<String>();
			if (value.contains(";"))
			    ls.addAll(Arrays.asList(value.split(";")));
			else
			    ls.add(value);
			UUID uuid = UUID.fromString(one);

			if (uuid == null)
			    continue;

			List<blockLoc> blist = new ArrayList<blockLoc>();
			for (String oneL : ls) {
			    blockLoc bl = new blockLoc(oneL);
			    Block block = bl.getBlock();
			    if (block == null)
				continue;

			    block.removeMetadata(JobsPaymentListener.brewingOwnerMetadata, Jobs.getInstance());
			    block.setMetadata(JobsPaymentListener.brewingOwnerMetadata, new FixedMetadataValue(Jobs.getInstance(), one));

			    blist.add(bl);
			}
			if (!blist.isEmpty()) {
			    brewingMap.put(uuid, blist);
			    totalb += blist.size();
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	if (totalf > 0 || totalb > 0)
	    Jobs.consoleMsg("&e[Jobs] Loaded " + totalf + " furnaces and " + totalb + " brewing stands for reassigning");
    }

    public static void save() {

	YmlMaker f = new YmlMaker(Jobs.getInstance(), "furnaceBrewingStands.yml");

	f.createNewFile();

	f.saveDefaultConfig();
	FileConfiguration config = f.getConfig();

	if (Jobs.getGCManager().isFurnacesReassign()) {
	    config.set("Furnace", null);
	    for (Entry<UUID, List<blockLoc>> one : furnaceMap.entrySet()) {

		String full = "";

		for (blockLoc oneL : one.getValue()) {

		    if (!full.isEmpty())
			full += ";";

		    full += oneL.toString();
		}
		if (!full.isEmpty()) {
		    config.set("Furnace." + one.getKey().toString(), full);
		}
	    }
	}

	if (Jobs.getGCManager().isBrewingStandsReassign()) {
	    config.set("Brewing", null);
	    for (Entry<UUID, List<blockLoc>> one : brewingMap.entrySet()) {

		String full = "";

		for (blockLoc oneL : one.getValue()) {

		    if (!full.isEmpty())
			full += ";";

		    full += oneL.toString();
		}
		if (!full.isEmpty())
		    config.set("Brewing." + one.getKey().toString(), full);
	    }
	}

	f.saveConfig();

    }

    public static int getTotalFurnaces(UUID uuid) {
	List<blockLoc> ls = furnaceMap.get(uuid);
	if (ls == null)
	    return 0;
	return ls.size();
    }

    public static int getTotalBrewingStands(UUID uuid) {
	List<blockLoc> ls = brewingMap.get(uuid);
	if (ls == null)
	    return 0;
	return ls.size();
    }

    public static boolean removeFurnace(Block block) {

	UUID uuid = null;

	if (block.hasMetadata(JobsPaymentListener.furnaceOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.furnaceOwnerMetadata);
	    if (!data.isEmpty()) {
		// only care about first
		MetadataValue value = data.get(0);
		String uuidS = value.asString();
		uuid = UUID.fromString(uuidS);
	    }
	}

	List<blockLoc> ls = furnaceMap.get(uuid);
	if (ls == null)
	    return true;

	for (blockLoc one : ls) {
	    if (!one.getLocation().equals(block.getLocation()))
		continue;
	    block.removeMetadata(JobsPaymentListener.furnaceOwnerMetadata, Jobs.getInstance());
	    ls.remove(one);
	    return true;
	}
	return false;

    }

    public static boolean removeBrewing(Block block) {

	UUID uuid = null;
	if (block.hasMetadata(JobsPaymentListener.brewingOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.brewingOwnerMetadata);
	    if (!data.isEmpty()) {
		// only care about first
		MetadataValue value = data.get(0);
		String uuidS = value.asString();
		uuid = UUID.fromString(uuidS);
	    }
	}

	List<blockLoc> ls = brewingMap.get(uuid);
	if (ls == null)
	    return true;
	for (blockLoc one : ls) {
	    if (!one.getLocation().equals(block.getLocation()))
		continue;
	    block.removeMetadata(JobsPaymentListener.brewingOwnerMetadata, Jobs.getInstance());
	    ls.remove(one);
	    return true;
	}

	return false;

    }

    public enum ownershipFeedback {
	invalid, tooMany, newReg, old, notOwn
    }

    public static ownershipFeedback registerFurnaces(Player player, Block block) {

	if (block.getType() != Material.FURNACE && block.getType() != Material.BURNING_FURNACE) {
	    return ownershipFeedback.invalid;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	int max = jPlayer.getMaxFurnacesAllowed();

	int have = jPlayer.getFurnaceCount();

	boolean owner = false;
	if (block.hasMetadata(JobsPaymentListener.furnaceOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.furnaceOwnerMetadata);
	    if (!data.isEmpty()) {
		// only care about first
		MetadataValue value = data.get(0);
		String uuid = value.asString();

		if (uuid.equals(player.getUniqueId().toString())) {
		    if (have > max && max > 0)
			removeFurnace(block);
		    owner = true;
		} else
		    return ownershipFeedback.notOwn;
	    }
	}

	if (owner)
	    return ownershipFeedback.old;

	if (have >= max && max > 0)
	    return ownershipFeedback.tooMany;

	block.setMetadata(JobsPaymentListener.furnaceOwnerMetadata, new FixedMetadataValue(Jobs.getInstance(), player.getUniqueId().toString()));

	List<blockLoc> ls = furnaceMap.get(player.getUniqueId());
	if (ls == null)
	    ls = new ArrayList<blockLoc>();
	ls.add(new blockLoc(block.getLocation()));
	furnaceMap.put(player.getUniqueId(), ls);

	return ownershipFeedback.newReg;
    }

    public static ownershipFeedback registerBrewingStand(Player player, Block block) {

	if (block.getType() != Material.BREWING_STAND) {
	    return ownershipFeedback.invalid;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	int max = jPlayer.getMaxBrewingStandsAllowed();

	int have = jPlayer.getBrewingStandCount();

	boolean owner = false;
	if (block.hasMetadata(JobsPaymentListener.brewingOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.brewingOwnerMetadata);
	    if (!data.isEmpty()) {
		// only care about first
		MetadataValue value = data.get(0);
		String uuid = value.asString();

		if (uuid.equals(player.getUniqueId().toString())) {
		    if (have > max && max > 0)
			removeBrewing(block);
		    owner = true;
		} else
		    return ownershipFeedback.notOwn;
	    }
	}
	if (owner)
	    return ownershipFeedback.old;

	if (have >= max && max > 0)
	    return ownershipFeedback.tooMany;

	block.setMetadata(JobsPaymentListener.brewingOwnerMetadata, new FixedMetadataValue(Jobs.getInstance(), player.getUniqueId().toString()));

	List<blockLoc> ls = brewingMap.get(player.getUniqueId());
	if (ls == null)
	    ls = new ArrayList<blockLoc>();
	ls.add(new blockLoc(block.getLocation()));
	brewingMap.put(player.getUniqueId(), ls);

	return ownershipFeedback.newReg;
    }

    public static int clearFurnaces(UUID uuid) {
	List<blockLoc> ls = furnaceMap.remove(uuid);
	if (ls == null)
	    return 0;
	for (blockLoc one : ls) {
	    Block block = one.getBlock();
	    if (block == null)
		continue;

	    if (block.getType() != Material.FURNACE && block.getType() != Material.BURNING_FURNACE) {
		continue;
	    }
	    block.removeMetadata(JobsPaymentListener.furnaceOwnerMetadata, Jobs.getInstance());
	}
	return ls.size();
    }

    public static int clearBrewingStands(UUID uuid) {
	List<blockLoc> ls = brewingMap.remove(uuid);
	if (ls == null)
	    return 0;
	for (blockLoc one : ls) {
	    Block block = one.getBlock();
	    if (block == null)
		continue;

	    if (block.getType() != Material.BREWING_STAND) {
		continue;
	    }
	    block.removeMetadata(JobsPaymentListener.brewingOwnerMetadata, Jobs.getInstance());
	}
	return ls.size();
    }
}
