package com.gamingmesh.jobs.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.container.ExploreRespond;
import com.gamingmesh.jobs.container.JobsWorld;
import com.gamingmesh.jobs.dao.JobsDAO.ExploreDataTableFields;
import com.gamingmesh.jobs.stuff.Util;

public class ExploreManager {

    private final Map<String, ExploreRegion> worlds = new HashMap<>();
    private boolean exploreEnabled = false;
    private int playerAmount = 1;

    public int getPlayerAmount() {
	return playerAmount;
    }

    public void setPlayerAmount(int amount) {
	if (this.playerAmount < amount)
	    this.playerAmount = amount;
    }

    public boolean isExploreEnabled() {
	return exploreEnabled;
    }

    public void setExploreEnabled() {
	if (!exploreEnabled) {
	    exploreEnabled = true;
	}
    }

    public void load() {
	if (!exploreEnabled)
	    return;
	Jobs.consoleMsg("&e[Jobs] Loading explorer data");
	Jobs.getJobsDAO().loadExplore();
	Jobs.consoleMsg("&e[Jobs] Loaded explorer data" + (getSize() != 0 ? " (" + getSize() + ")" : "."));
    }

    public Map<String, ExploreRegion> getWorlds() {
	return worlds;
    }

    public int getSize() {
	int i = 0;
	for (ExploreRegion one : worlds.values()) {
	    i += one.getChunks().size();
	}
	return i;
    }

    public ExploreRespond ChunkRespond(Player player, Chunk chunk) {
	return ChunkRespond(Jobs.getPlayerManager().getJobsPlayer(player).getUserId(), chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ExploreRespond ChunkRespond(int playerId, Chunk chunk) {
	return ChunkRespond(playerId, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ExploreRespond ChunkRespond(int playerId, String world, int x, int z) {
	ExploreRegion eRegions = worlds.get(world);
	if (eRegions == null) {
	    int RegionX = (int) Math.floor(x / 32D);
	    int RegionZ = (int) Math.floor(z / 32D);
	    eRegions = new ExploreRegion(RegionX, RegionZ);
	}
	ExploreChunk chunk = eRegions.getChunk(x, z);
	if (chunk == null)
	    chunk = new ExploreChunk();

	eRegions.addChunk(x, z, chunk);
	worlds.put(world, eRegions);

	return chunk.addPlayer(playerId);
    }

    public void load(ResultSet res) {
	try {
	    int worldId = res.getInt(ExploreDataTableFields.worldid.getCollumn());
	    String worldName = res.getString(ExploreDataTableFields.worldname.getCollumn());

	    JobsWorld jobsWorld = Util.getJobsWorld(worldName);
	    if (jobsWorld == null)
		jobsWorld = Util.getJobsWorld(worldId);

	    if (jobsWorld == null)
		return;

	    int x = res.getInt(ExploreDataTableFields.chunkX.getCollumn());
	    int z = res.getInt(ExploreDataTableFields.chunkZ.getCollumn());
	    String names = res.getString(ExploreDataTableFields.playerNames.getCollumn());
	    int id = res.getInt("id");

	    ExploreRegion eRegions = worlds.get(jobsWorld.getName());
	    if (eRegions == null) {
		int RegionX = (int) Math.floor(x / 32D);
		int RegionZ = (int) Math.floor(z / 32D);
		eRegions = new ExploreRegion(RegionX, RegionZ);
	    }
	    ExploreChunk chunk = eRegions.getChunk(x, z);
	    if (chunk == null)
		chunk = new ExploreChunk();
	    chunk.deserializeNames(names);
	    chunk.setDbId(id);

	    eRegions.addChunk(x, z, chunk);
	    worlds.put(jobsWorld.getName(), eRegions);

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
