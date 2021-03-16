package com.gamingmesh.jobs.container.blockOwnerShip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.config.YmlMaker;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.blockLoc;

public class BlockOwnerShip {

	private CMIMaterial material;
	private BlockTypes type;
	private String metadataName = "";

	private final Map<UUID, List<blockLoc>> blockOwnerShips = new HashMap<>();

	public BlockOwnerShip(CMIMaterial type) {
		// Type should be any type of furnace, smoker or brewing stand
		if (type != CMIMaterial.FURNACE && type != CMIMaterial.LEGACY_BURNING_FURNACE
				&& type != CMIMaterial.BLAST_FURNACE && type != CMIMaterial.SMOKER && type != CMIMaterial.BREWING_STAND
				&& type != CMIMaterial.LEGACY_BREWING_STAND) {
			throw new IllegalArgumentException("Material types should be any type of furnace, smoker or brewing stand");
		}

		material = type;
		this.type = BlockTypes.getFromCMIMaterial(type);

		switch (this.type) {
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

	public Map<UUID, List<blockLoc>> getBlockOwnerShips() {
		return blockOwnerShips;
	}

	public ownershipFeedback register(Player player, Block block) {
		CMIMaterial mat = CMIMaterial.get(block);
		if (type != BlockTypes.getFromCMIMaterial(mat)) {
			return ownershipFeedback.invalid;
		}

		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
		if (jPlayer == null) {
			return ownershipFeedback.invalid;
		}

		int max = jPlayer.getMaxOwnerShipAllowed(type);
		int have = getTotal(jPlayer.getUniqueId());

		boolean owner = false;
		List<MetadataValue> data = getBlockMetadatas(block);
		if (!data.isEmpty()) {
			// only care about first
			MetadataValue value = data.get(0);
			String uuid = value.asString();

			if (!uuid.equals(player.getUniqueId().toString())) {
				return ownershipFeedback.notOwn;
			}

			if (have > max && max > 0) {
				remove(block);
			}

			owner = true;
		}

		if (owner)
			return ownershipFeedback.old;

		if (have >= max && max > 0)
			return ownershipFeedback.tooMany;

		block.setMetadata(metadataName, new FixedMetadataValue(Jobs.getInstance(), player.getUniqueId().toString()));

		if (!Jobs.getGCManager().isBrewingStandsReassign() && !Jobs.getGCManager().isFurnacesReassign()
				&& !Jobs.getGCManager().BlastFurnacesReassign && !Jobs.getGCManager().SmokerReassign) {
			return ownershipFeedback.newReg;
		}

		List<blockLoc> ls = blockOwnerShips.getOrDefault(player.getUniqueId(), new ArrayList<>());
		ls.add(new blockLoc(block.getLocation()));
		blockOwnerShips.put(player.getUniqueId(), ls);
		return ownershipFeedback.newReg;
	}

	public boolean remove(Block block) {
		UUID uuid = null;
		List<MetadataValue> data = getBlockMetadatas(block);
		if (!data.isEmpty()) {
			// only care about first
			MetadataValue value = data.get(0);
			uuid = UUID.fromString(value.asString());
		}

		if (uuid == null) {
			return false;
		}

		List<blockLoc> ls = blockOwnerShips.getOrDefault(uuid, new ArrayList<>());
		for (blockLoc one : ls) {
			if (one.getLocation().equals(block.getLocation())) {
				block.removeMetadata(metadataName, Jobs.getInstance());
				ls.remove(one);
				return true;
			}
		}

		return false;
	}

	public int clear(UUID uuid) {
		List<blockLoc> ls = blockOwnerShips.remove(uuid);
		if (ls == null)
			return 0;

		for (blockLoc one : ls) {
			one.getBlock().removeMetadata(metadataName, Jobs.getInstance());
		}

		return ls.size();
	}

	public List<MetadataValue> getBlockMetadatas(Block block) {
		return block.getMetadata(metadataName);
	}

	public int getTotal(UUID uuid) {
		return blockOwnerShips.getOrDefault(uuid, new ArrayList<>()).size();
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

		String path = (type == BlockTypes.FURNACE ? "Furnace"
				: type == BlockTypes.BLAST_FURNACE ? "BlastFurnace"
						: type == BlockTypes.BREWING_STAND ? "Brewing" : type == BlockTypes.SMOKER ? "Smoker" : "");

		if (isReassignDisabled() || !f.getConfig().isConfigurationSection(path))
			return;

		int total = 0;
		ConfigurationSection section = f.getConfig().getConfigurationSection(path);
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

				block.removeMetadata(metadataName, Jobs.getInstance());
				block.setMetadata(metadataName, new FixedMetadataValue(Jobs.getInstance(), one));

				blist.add(bl);
				total++;
			}

			if (!blist.isEmpty()) {
				blockOwnerShips.put(uuid, blist);
			}
		}

		if (total > 0) {
			Jobs.consoleMsg("&e[Jobs] Loaded " + total + " " + path.toLowerCase() + " for reassigning.");
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

		if (!f.exists())
			f.createNewFile();

		f.saveDefaultConfig();

		if (isReassignDisabled()) {
			return;
		}

		String path = (type == BlockTypes.FURNACE ? "Furnace"
				: type == BlockTypes.BLAST_FURNACE ? "BlastFurnace"
						: type == BlockTypes.BREWING_STAND ? "Brewing" : type == BlockTypes.SMOKER ? "Smoker" : "");
		f.getConfig().set(path, null);

		for (Map.Entry<UUID, List<blockLoc>> one : blockOwnerShips.entrySet()) {
			String full = "";
			for (blockLoc oneL : one.getValue()) {
				if (!full.isEmpty())
					full += ";";

				full += oneL.toString();
			}

			if (!full.isEmpty())
				f.getConfig().set(path + "." + one.getKey().toString(), full);
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
