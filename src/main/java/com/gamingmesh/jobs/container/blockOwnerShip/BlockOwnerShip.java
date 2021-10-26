package com.gamingmesh.jobs.container.blockOwnerShip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.blockLoc;

import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Items.CMIMaterial;

public class BlockOwnerShip {

    private CMIMaterial material;
    private BlockTypes type;
    private String metadataName = "";

    private final Map<UUID, HashMap<String, blockLoc>> blockOwnerShips = new HashMap<>();

    private final Map<String, Map<String, UUID>> ownerMapByLocation = new HashMap<>();

    private final Jobs plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class);

    public BlockOwnerShip(CMIMaterial type) {
	// Type should be any type of furnace, smoker or brewing stand
	if (type != CMIMaterial.FURNACE && type != CMIMaterial.LEGACY_BURNING_FURNACE
	    && type != CMIMaterial.BLAST_FURNACE && type != CMIMaterial.SMOKER && type != CMIMaterial.BREWING_STAND
	    && type != CMIMaterial.LEGACY_BREWING_STAND) {
	    throw new IllegalArgumentException("Material types should be any type of furnace, smoker or brewing stand");
	}

	material = type;

	switch (this.type = BlockTypes.getFromCMIMaterial(type)) {
	case BLAST_FURNACE:
	    metadataName = "jobsBlastFurnaceOwner";
	    break;
	case BREWING_STAND:
	    metadataName = "jobsBrewingOwner";
	    break;
	case FURNACE:
	    metadataName = "jobsFurnaceOwner";
	    break;
	case SMOKER:
	    metadataName = "jobsSmokerOwner";
	    break;
	default:
	    break;
	}
    }

    public BlockTypes getType() {
	return type;
    }

    public CMIMaterial getMaterial() {
	return material;
    }

    public String getMetadataName() {
	return metadataName;
    }

    public Map<UUID, HashMap<String, blockLoc>> getBlockOwnerShips() {
	return blockOwnerShips;
    }

    public ownershipFeedback register(Player player, Block block) {
	if (type != BlockTypes.getFromCMIMaterial(CMIMaterial.get(block))) {
	    return ownershipFeedback.invalid;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null) {
	    return ownershipFeedback.invalid;
	}

	UUID ownerUUID = this.getOwnerByLocation(block.getLocation());

	if (ownerUUID != null && ownerUUID.equals(player.getUniqueId()))
	    return ownershipFeedback.old;

	if (ownerUUID != null && !ownerUUID.equals(player.getUniqueId())) {
	    if (Jobs.getGCManager().blockOwnershipTakeOver) {
		// Removing ownership to record new player
		this.remove(ownerUUID, CMILocation.toString(block.getLocation(), ":", true, true));
		block.removeMetadata(metadataName, plugin);

		Player owningPlayer = Bukkit.getPlayer(ownerUUID);

		if (owningPlayer != null && owningPlayer.isOnline()) {
		    owningPlayer.sendMessage(Jobs.getLanguage().getMessage("command.clearownership.output.lost", "[type]", CMIMaterial.get(type.toString()).getName(), "[location]", CMILocation.toString(
			block.getLocation(), ":",
			true, true)));
		}

	    } else
		return ownershipFeedback.notOwn;
	}

	int max = jPlayer.getMaxOwnerShipAllowed(type);
	int have = getTotal(jPlayer.getUniqueId());

	boolean owner = false;
	List<MetadataValue> data = getBlockMetadatas(block);
	if (!data.isEmpty()) {
	    if (have > max && max > 0) {
		remove(block);
	    }
	    owner = true;
	}

	if (owner)
	    return ownershipFeedback.old;

	if (have >= max && max > 0)
	    return ownershipFeedback.tooMany;

	block.setMetadata(metadataName, new FixedMetadataValue(plugin, jPlayer.getUniqueId().toString()));

	if (!Jobs.getGCManager().isBrewingStandsReassign() && !Jobs.getGCManager().isFurnacesReassign()
	    && !Jobs.getGCManager().BlastFurnacesReassign && !Jobs.getGCManager().SmokerReassign) {
	    return ownershipFeedback.newReg;
	}

	HashMap<String, blockLoc> ls = blockOwnerShips.getOrDefault(jPlayer.getUniqueId(), new HashMap<String, blockLoc>());

	String locString = CMILocation.toString(block.getLocation(), ":", true, true);

	if (ls.containsKey(locString))
	    return ownershipFeedback.old;

	blockLoc bloc = new blockLoc(block.getLocation());

	ls.put(locString, bloc);
	blockOwnerShips.put(jPlayer.getUniqueId(), ls);

	Map<String, UUID> oldRecord = ownerMapByLocation.getOrDefault(block.getLocation().getWorld().getName(), new HashMap<String, UUID>());
	oldRecord.put(bloc.toVectorString(), jPlayer.getUniqueId());
	ownerMapByLocation.put(block.getLocation().getWorld().getName(), oldRecord);

	return ownershipFeedback.newReg;
    }

    public boolean remove(Block block) {
	UUID uuid = getOwnerByLocation(block.getLocation());

	if (uuid == null) {
	    List<MetadataValue> data = getBlockMetadatas(block);
	    if (!data.isEmpty()) {
		try {
		    uuid = UUID.fromString(data.get(0).asString());
		} catch (IllegalArgumentException e) {
		}
	    }
	}
	if (uuid == null) {
	    return false;
	}
	return remove(uuid, block);
    }

    public boolean remove(UUID uuid, Block block) {
	if (uuid == null) {
	    return false;
	}
	HashMap<String, blockLoc> ls = blockOwnerShips.getOrDefault(uuid, new HashMap<String, blockLoc>());
	String blockLoc = CMILocation.toString(block.getLocation(), ":", true, true);
	com.gamingmesh.jobs.stuff.blockLoc removed = ls.remove(blockLoc);
	if (removed != null) {
	    block.removeMetadata(metadataName, plugin);
	    Map<String, UUID> oldRecord = ownerMapByLocation.get(block.getLocation().getWorld().getName());
	    if (oldRecord != null)
		oldRecord.remove(block.getLocation().getBlockX() + ":" + block.getLocation().getBlockY() + ":" + block.getLocation().getBlockZ());
	}
	return removed != null;
    }

    public UUID getOwnerByLocation(Location loc) {
	blockLoc bl = new blockLoc(loc);
	Map<String, UUID> record = ownerMapByLocation.get(bl.getWorldName());
	if (record == null) {
	    return null;
	}
	return record.get(bl.toVectorString());
    }

    public int clear(UUID uuid) {
	HashMap<String, blockLoc> ls = blockOwnerShips.remove(uuid);
	if (ls == null)
	    return 0;

	for (blockLoc one : ls.values()) {
	    one.getBlock().removeMetadata(metadataName, plugin);

	    Map<String, UUID> oldRecord = ownerMapByLocation.get(one.getWorldName());
	    if (oldRecord != null)
		oldRecord.remove(one.toVectorString());

	}

	return ls.size();
    }

    public int remove(UUID uuid, String location) {
	HashMap<String, blockLoc> ls = blockOwnerShips.get(uuid);
	if (ls == null)
	    return 0;

	for (Entry<String, blockLoc> one : new HashMap<String, blockLoc>(ls).entrySet()) {

	    if (!one.getKey().equalsIgnoreCase(location))
		continue;

	    one.getValue().getBlock().removeMetadata(metadataName, plugin);

	    ls.remove(one.getKey());

	    Map<String, UUID> oldRecord = ownerMapByLocation.get(one.getValue().getWorldName());
	    if (oldRecord != null)
		oldRecord.remove(one.getValue().toVectorString());
	}

	return 1;
    }

    public List<MetadataValue> getBlockMetadatas(Block block) {
	return block.getMetadata(metadataName);
    }

    public int getTotal(UUID uuid) {
	HashMap<String, blockLoc> list = blockOwnerShips.get(uuid);
	return list == null ? 0 : list.size();
    }

    public void load() {
	YmlMaker f = new YmlMaker(Jobs.getFolder(), "furnaceBrewingStands.yml");
	YmlMaker f2 = new YmlMaker(Jobs.getFolder(), "blockOwnerShips.yml");
	if (!f.exists() && !f2.exists())
	    return;

	if (f.exists()) {
	    f.getConfigFile().renameTo(f2.getConfigFile());
	}

	f = f2;

	String path = "";
	switch (type) {
	case BLAST_FURNACE:
	    path = "BlastFurnace";
	    break;
	case BREWING_STAND:
	    path = "Brewing";
	    break;
	case FURNACE:
	    path = "Furnace";
	    break;
	case SMOKER:
	    path = "Smoker";
	    break;
	default:
	    break;
	}

	if (isReassignDisabled())
	    return;

	ConfigurationSection section = f.getConfig().getConfigurationSection(path);
	if (section == null) {
	    return;
	}

	int total = 0;
	for (String one : section.getKeys(false)) {
	    String value = section.getString(one);
	    List<String> ls = new ArrayList<>();

	    if (value.contains(";"))
		ls.addAll(Arrays.asList(value.split(";")));
	    else
		ls.add(value);

	    UUID uuid;
	    try {
		uuid = UUID.fromString(one);
	    } catch (IllegalArgumentException e) {
		continue;
	    }

	    HashMap<String, blockLoc> blist = new HashMap<String, blockLoc>();
	    for (String oneL : ls) {
		blockLoc bl = new blockLoc(oneL);
		CMILocation cmil = CMILocation.fromString(oneL, ":");

		blist.put(CMILocation.toString(cmil, ":", true, true), bl);

		Map<String, UUID> oldRecord = ownerMapByLocation.getOrDefault(bl.getWorldName(), new HashMap<String, UUID>());
		oldRecord.put(bl.toVectorString(), uuid);
		ownerMapByLocation.put(bl.getWorldName(), oldRecord);

		total++;
	    }

	    if (!blist.isEmpty()) {
		blockOwnerShips.put(uuid, blist);
	    }
	}

	if (total > 0) {
	    Jobs.consoleMsg("&eLoaded &6" + total + " " + path.toLowerCase() + " &efor reassigning.");
	}
    }

    public void save() {
	YmlMaker f = new YmlMaker(Jobs.getFolder(), "furnaceBrewingStands.yml");
	if (f.exists()) {
	    f.getConfigFile().renameTo(new File(Jobs.getFolder(), "blockOwnerShips.yml"));
	}

	f = new YmlMaker(Jobs.getFolder(), "blockOwnerShips.yml");

	if (blockOwnerShips.isEmpty() && f.getConfigFile().length() == 0L) {
	    f.getConfigFile().delete();
	    return;
	}

	f.createNewFile();
	f.saveDefaultConfig();

	if (isReassignDisabled()) {
	    return;
	}

	String path = (type == BlockTypes.FURNACE ? "Furnace"
	    : type == BlockTypes.BLAST_FURNACE ? "BlastFurnace"
		: type == BlockTypes.BREWING_STAND ? "Brewing" : type == BlockTypes.SMOKER ? "Smoker" : "");
	f.getConfig().set(path, null);

	for (Entry<UUID, HashMap<String, blockLoc>> one : blockOwnerShips.entrySet()) {
	    StringBuilder full = new StringBuilder();

	    for (String oneL : one.getValue().keySet()) {
		if (!full.toString().isEmpty())
		    full.append(";");

		full.append(oneL);
	    }

	    if (!full.toString().isEmpty())
		f.getConfig().set(path + "." + one.getKey().toString(), full.toString());
	}

	f.saveConfig();
    }

    public boolean isReassignDisabled() {
	return (type == BlockTypes.FURNACE && !Jobs.getGCManager().isFurnacesReassign())
	    || (type == BlockTypes.BLAST_FURNACE && !Jobs.getGCManager().BlastFurnacesReassign)
	    || (type == BlockTypes.BREWING_STAND && !Jobs.getGCManager().isBrewingStandsReassign())
	    || (type == BlockTypes.SMOKER && !Jobs.getGCManager().SmokerReassign);
    }

    public enum ownershipFeedback {
	invalid, tooMany, newReg, old, notOwn
    }
}
