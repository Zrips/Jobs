package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;
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

			Debug.D("set meta " + block.getLocation().toString() + " " + one);

			blist.add(bl);
		    }
		    if (!blist.isEmpty()) {
			Debug.D("adding furnace " + uuid.toString() + " " + blist.size());
			furnaceMap.put(uuid, blist);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

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
		    if (!blist.isEmpty())
			brewingMap.put(uuid, blist);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public static void save() {

	YmlMaker f = new YmlMaker(Jobs.getInstance(), "furnaceBrewingStands.yml");

	f.createNewFile();

	f.saveDefaultConfig();
	FileConfiguration config = f.getConfig();

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

	for (Entry<UUID, List<blockLoc>> one : brewingMap.entrySet()) {

	    String full = "";
	    
	    Debug.D("saving brewing stands " + one.getValue().size());

	    for (blockLoc oneL : one.getValue()) {

		if (!full.isEmpty())
		    full += ";";

		full += oneL.toString();
	    }
	    if (!full.isEmpty())
		config.set("Brewing." + one.getKey().toString(), full);
	}

	f.saveConfig();

    }

    public static int getTotalFurnaces(Player player) {
	List<blockLoc> ls = furnaceMap.get(player.getUniqueId());
	if (ls == null)
	    return 0;
	return ls.size();
    }

    public static int getTotalBrewingStands(Player player) {
	List<blockLoc> ls = brewingMap.get(player.getUniqueId());
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
	if (block.hasMetadata(JobsPaymentListener.furnaceOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.furnaceOwnerMetadata);
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

    public static boolean registerFurnaces(Player player, Block block) {

	if (block.getType() != Material.FURNACE && block.getType() != Material.BURNING_FURNACE) {
	    return false;
	}

	Double maxV = Jobs.getPermissionManager().getMaxPermission(Jobs.getPlayerManager().getJobsPlayer(player), "jobs.maxfurnaces");

	if (maxV == null)
	    maxV = 0D;

	int max = maxV.intValue();

	int have = getTotalFurnaces(player);

	boolean owner = false;
	if (block.hasMetadata(JobsPaymentListener.furnaceOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.furnaceOwnerMetadata);
	    if (!data.isEmpty()) {
		// only care about first
		MetadataValue value = data.get(0);
		String uuid = value.asString();

		if (uuid.equals(player.getUniqueId().toString())) {
		    if (have > max)
			removeFurnace(block);
		    owner = true;
		}
	    }
	}

	if (owner)
	    return true;

	if (have >= max && max > 0)
	    return false;

	block.setMetadata(JobsPaymentListener.furnaceOwnerMetadata, new FixedMetadataValue(Jobs.getInstance(), player.getUniqueId().toString()));

//	if (max == 0)
//	    return true;

	List<blockLoc> ls = furnaceMap.get(player.getUniqueId());
	if (ls == null)
	    ls = new ArrayList<blockLoc>();
	ls.add(new blockLoc(block.getLocation()));
	furnaceMap.put(player.getUniqueId(), ls);

	return true;
    }

    public static boolean registerBrewingStand(Player player, Block block) {

	if (block.getType() != Material.BREWING_STAND) {
	    return false;
	}

	Double maxV = Jobs.getPermissionManager().getMaxPermission(Jobs.getPlayerManager().getJobsPlayer(player), "jobs.maxbrewingstands");

	if (maxV == null)
	    maxV = 0D;

	int max = maxV.intValue();

	int have = getTotalFurnaces(player);

	boolean owner = false;
	if (block.hasMetadata(JobsPaymentListener.brewingOwnerMetadata)) {
	    List<MetadataValue> data = block.getMetadata(JobsPaymentListener.brewingOwnerMetadata);
	    if (!data.isEmpty()) {
		// only care about first
		MetadataValue value = data.get(0);
		String uuid = value.asString();

		if (uuid.equals(player.getUniqueId().toString())) {
		    if (have > max)
			removeBrewing(block);
		    owner = true;
		}
	    }
	}

	if (owner)
	    return true;

	if (have >= max && max > 0)
	    return false;

	block.setMetadata(JobsPaymentListener.brewingOwnerMetadata, new FixedMetadataValue(Jobs.getInstance(), player.getUniqueId().toString()));

//	if (max == 0)
//	    return true;

	List<blockLoc> ls = brewingMap.get(player.getUniqueId());
	if (ls == null)
	    ls = new ArrayList<blockLoc>();
	ls.add(new blockLoc(block.getLocation()));
	brewingMap.put(player.getUniqueId(), ls);

	return true;
    }

    public static boolean clearFurnaces(Player player) {
	furnaceMap.remove(player.getUniqueId());
	return true;
    }

    public static boolean clearBrewingStands(Player player) {
	brewingMap.remove(player.getUniqueId());
	return true;
    }
}
