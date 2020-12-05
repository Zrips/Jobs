package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.config.YmlMaker;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;

/**
 * @deprecated As of new blocks (smoker, blast furnace) this has been deprecated and
 * marked as "removeable". In the future this class will get removed
 * and not used anymore by anyone. Instead use {@link Jobs#getBlockOwnerShips()}
 */
@Deprecated
public class FurnaceBrewingHandling {

    static HashMap<UUID, List<blockLoc>> furnaceMap = new HashMap<>();
    static HashMap<UUID, List<blockLoc>> brewingMap = new HashMap<>();

    public static void load() {
	YmlMaker f = new YmlMaker(Jobs.getFolder(), "furnaceBrewingStands.yml");
	if (!f.exists())
	    return;

	int totalf = 0;
	int totalb = 0;

	FileConfiguration config = f.getConfig();

	if (Jobs.getGCManager().isFurnacesReassign()) {
	    ConfigurationSection section = config.getConfigurationSection("Furnace");
	    if (section == null)
		return;

	    for (String one : section.getKeys(false)) {
		String value = section.getString(one);
		List<String> ls = new ArrayList<>();
		if (value.contains(";"))
		    ls.addAll(Arrays.asList(value.split(";")));
		else
		    ls.add(value);

		UUID uuid = UUID.fromString(one);
		if (uuid == null)
		    continue;

		List<blockLoc> blist = new ArrayList<>();
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
		    totalf++;
		}
	    }
	}

	if (Jobs.getGCManager().isBrewingStandsReassign()) {
	    ConfigurationSection section = config.getConfigurationSection("Brewing");
	    if (section == null)
		return;

	    for (String one : section.getKeys(false)) {
		String value = section.getString(one);
		List<String> ls = new ArrayList<>();
		if (value.contains(";"))
		    ls.addAll(Arrays.asList(value.split(";")));
		else
		    ls.add(value);

		UUID uuid = UUID.fromString(one);
		if (uuid == null)
		    continue;

		List<blockLoc> blist = new ArrayList<>();
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
		    totalb++;
		}
	    }
	}

	if (totalf > 0 || totalb > 0)
	    Jobs.consoleMsg("&e[Jobs] Loaded " + totalf + " furnaces and " + totalb + " brewing stands for reassigning.");
    }

    public static void save() {
	YmlMaker f = new YmlMaker(Jobs.getFolder(), "furnaceBrewingStands.yml");
	if (!f.exists())
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
		if (!full.isEmpty())
		    config.set("Furnace." + one.getKey().toString(), full);
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
	return ls == null ? 0 : ls.size();
    }

    public static int getTotalBrewingStands(UUID uuid) {
	List<blockLoc> ls = brewingMap.get(uuid);
	return ls == null ? 0 : ls.size();
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

	if (uuid == null) {
	    return false;
	}

	List<blockLoc> ls = furnaceMap.get(uuid);
	if (ls == null)
	    return false;

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

	if (uuid == null) {
	    return false;
	}

	List<blockLoc> ls = brewingMap.get(uuid);
	if (ls == null)
	    return false;

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
	CMIMaterial cmat = CMIMaterial.get(block);
	if (cmat != CMIMaterial.FURNACE && cmat != CMIMaterial.LEGACY_BURNING_FURNACE && cmat != CMIMaterial.SMOKER && cmat != CMIMaterial.BLAST_FURNACE)
	    return ownershipFeedback.invalid;

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

	if (!Jobs.getGCManager().isFurnacesReassign() && !Jobs.getGCManager().BlastFurnacesReassign && !Jobs.getGCManager().SmokerReassign) {
	    return ownershipFeedback.newReg;
	}

	List<blockLoc> ls = furnaceMap.get(player.getUniqueId());
	if (ls == null)
	    ls = new ArrayList<>();

	ls.add(new blockLoc(block.getLocation()));
	furnaceMap.put(player.getUniqueId(), ls);
	return ownershipFeedback.newReg;
    }

    public static ownershipFeedback registerBrewingStand(Player player, Block block) {
	if (CMIMaterial.get(block) != CMIMaterial.BREWING_STAND && CMIMaterial.get(block) != CMIMaterial.LEGACY_BREWING_STAND)
	    return ownershipFeedback.invalid;

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

	if (!Jobs.getGCManager().isBrewingStandsReassign()) {
	    return ownershipFeedback.newReg;
	}

	List<blockLoc> ls = brewingMap.get(player.getUniqueId());
	if (ls == null)
	    ls = new ArrayList<>();

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
	    CMIMaterial cmat = CMIMaterial.get(block);
	    if (cmat != CMIMaterial.FURNACE && cmat != CMIMaterial.LEGACY_BURNING_FURNACE && cmat != CMIMaterial.SMOKER && cmat != CMIMaterial.BLAST_FURNACE)
		continue;

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
	    if (CMIMaterial.get(block) == CMIMaterial.BREWING_STAND)
		block.removeMetadata(JobsPaymentListener.brewingOwnerMetadata, Jobs.getInstance());
	}

	return ls.size();
    }

    public static HashMap<UUID, List<blockLoc>> getBrewingMap() {
	return brewingMap;
    }

    public static HashMap<UUID, List<blockLoc>> getFurnaceMap() {
	return furnaceMap;
    }
}
