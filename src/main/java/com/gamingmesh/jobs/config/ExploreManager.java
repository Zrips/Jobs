package com.gamingmesh.jobs.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.container.ExploreRespond;
import com.gamingmesh.jobs.dao.JobsDAO.ExploreDataTableFields;
import com.gamingmesh.jobs.stuff.Debug;

public class ExploreManager {

    private HashMap<String, ExploreRegion> worlds = new HashMap<String, ExploreRegion>();
    private boolean exploreEnabled = false;
    private int playerAmount = 1;

    public int getPlayerAmount() {
	return this.playerAmount;
    }

    public void setPlayerAmount(int amount) {
	if (this.playerAmount < amount)
	    this.playerAmount = amount;
    }

    public boolean isExploreEnabled() {
	return this.exploreEnabled;
    }

    public void setExploreEnabled() {
	if (!exploreEnabled) {
	    this.exploreEnabled = true;
	}
    }

    public void load() {
	if (!exploreEnabled)
	    return;
	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loading explorer data");
	Jobs.getJobsDAO().loadExplore();
	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded explorer data (" + getSize() + ")");
    }

    public HashMap<String, ExploreRegion> getWorlds() {
	return worlds;
    }

    public int getSize() {
	int i = 0;
	for (Entry<String, ExploreRegion> one : this.getWorlds().entrySet()) {
	    i += one.getValue().getChunks().size();
	}
	return i;
    }

    public ExploreRespond ChunkRespond(Player player, Chunk chunk) {
	return ChunkRespond(player.getName(), chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ExploreRespond ChunkRespond(String player, String world, int x, int z) {

//	int ChunkX = x;
//	int ChunkZ = z;

//	int RegionX = (int) Math.floor(ChunkX / 32D);
//	int RegionZ = (int) Math.floor(ChunkZ / 32D);

	ExploreRegion eRegions = worlds.get(world);
	if (eRegions == null) {
	    int RegionX = (int) Math.floor(x / 32D);
	    int RegionZ = (int) Math.floor(z / 32D);
	    eRegions = new ExploreRegion(RegionX, RegionZ);
	}
	ExploreChunk chunk = eRegions.getChunk(x, z);
	if (chunk == null)
	    chunk = new ExploreChunk(x, z);

	eRegions.addChunk(chunk);
	worlds.put(world, eRegions);

	return chunk.addPlayer(player);

//	if (!worlds.containsKey(worldName)) {
//	    ExploreChunk eChunk = new ExploreChunk(player, ChunkX, ChunkZ);
//	    if (!isNew)
//		eChunk.setOldChunk();
//	    ExploreRegion eRegion = new ExploreRegion(RegionX, RegionZ);
//	    eRegion.addChunk(eChunk);
//	    worlds.put(worldName, eRegion);
//	    Debug.D("new chunk " + eChunk.isNew());
//	    return new ExploreRespond(eChunk.getCount(), true);
//	}
//	ExploreRegion eRegion = worlds.get(worldName);
//	ExploreChunk eChunk = eRegion.getChunk(ChunkX + ":" + ChunkZ);
//
//	if (eChunk == null) {
//	    eChunk = new ExploreChunk(player, ChunkX, ChunkZ);
//	    if (!isNew)
//		eChunk.setOldChunk();
//	    eRegion.addChunk(eChunk);
//	    Debug.D("new chunk " + eChunk.isNew());
//	    return new ExploreRespond(eChunk.getCount(), true);
//	}
//	eChunk.setOldChunk();
//	return eChunk.addPlayer(player);
    }

    public void load(ResultSet res) {
	try {
	    String world = res.getString(ExploreDataTableFields.worldname.getCollumn());
	    int x = res.getInt(ExploreDataTableFields.chunkX.getCollumn());
	    int z = res.getInt(ExploreDataTableFields.chunkZ.getCollumn());
	    String names = res.getString(ExploreDataTableFields.playerNames.getCollumn());
	    int id = res.getInt("id");

	    ExploreRegion eRegions = worlds.get(world);
	    if (eRegions == null) {
		int RegionX = (int) Math.floor(x / 32D);
		int RegionZ = (int) Math.floor(z / 32D);
		eRegions = new ExploreRegion(RegionX, RegionZ);
	    }
	    ExploreChunk chunk = eRegions.getChunk(x, z);
	    if (chunk == null)
		chunk = new ExploreChunk(x, z);
	    chunk.deserializeNames(names);
	    chunk.setDbId(id);

	    eRegions.addChunk(chunk);
	    worlds.put(world, eRegions);

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
//
//    public void addChunk(String player, String worldName, int x, int z) {
//	int ChunkX = x;
//	int ChunkZ = z;
//	int RegionX = (int) Math.floor(ChunkX / 32D);
//	int RegionZ = (int) Math.floor(ChunkZ / 32D);
//	if (!worlds.containsKey(worldName)) {
//	    ExploreChunk eChunk = new ExploreChunk(player, ChunkX, ChunkZ);
//	    eChunk.setOldChunk();
//	    ExploreRegion eRegion = new ExploreRegion(RegionX, RegionZ);
//	    eRegion.addChunk(eChunk);
//	    worlds.put(worldName, eRegion);
//	}
//	ExploreRegion eRegion = worlds.get(worldName);
//	ExploreChunk eChunk = eRegion.getChunk(ChunkX + ":" + ChunkZ);
//	if (eChunk == null) {
//	    eChunk = new ExploreChunk(player, ChunkX, ChunkZ);
//	    eChunk.setOldChunk();
//	    eRegion.addChunk(eChunk);
//	} else
//	    eChunk.setOldChunk();
//    }
}
