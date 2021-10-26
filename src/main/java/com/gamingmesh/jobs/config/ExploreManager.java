package com.gamingmesh.jobs.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    private final Map<String, Map<String, ExploreRegion>> worlds = new HashMap<>();
    private boolean exploreEnabled = false;
    private int playerAmount = 1;

    public int getPlayerAmount() {
	return playerAmount;
    }

    public void setPlayerAmount(int amount) {
	if (playerAmount < amount)
	    playerAmount = amount;
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

	Jobs.consoleMsg("&eLoading explorer data");
	Jobs.getJobsDAO().loadExplore();

	int size = getSize();
	Jobs.consoleMsg("&eLoaded explorer data" + (size != 0 ? " (&6" + size + "&e)" : "."));
    }

    public Map<String, Map<String, ExploreRegion>> getWorlds() {
	return worlds;
    }

    public int getSize() {
	int i = 0;
	for (Map<String, ExploreRegion> one : worlds.values()) {
	    for (Entry<String, ExploreRegion> chunks : one.entrySet()) {
		i += chunks.getValue().getChunks().size();
	    }
	}
	return i;
    }

    public ExploreRespond chunkRespond(Player player, Chunk chunk) {
	return chunkRespond(Jobs.getPlayerManager().getJobsPlayer(player).getUserId(), chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ExploreRespond chunkRespond(int playerId, Chunk chunk) {
	return chunkRespond(playerId, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ExploreRespond chunkRespond(int playerId, String world, int x, int z) {
	Map<String, ExploreRegion> eRegions = worlds.getOrDefault(world, new HashMap<String, ExploreRegion>());

	int RegionX = (int) Math.floor(x / 32D);
	int RegionZ = (int) Math.floor(z / 32D);

	ExploreRegion region = eRegions.get(RegionX + ":" + RegionZ);
	if (region == null) {
	    region = new ExploreRegion(RegionX, RegionZ);
	}
	ExploreChunk chunk = region.getChunk(x, z);
	if (chunk == null) {
	    chunk = new ExploreChunk();
	    region.addChunk(x, z, chunk);
	}

	eRegions.put(RegionX + ":" + RegionZ, region);

	worlds.put(world, eRegions);

	return chunk.addPlayer(playerId);
    }

    public void load(ResultSet res) {
	try {
	    String worldName = res.getString(ExploreDataTableFields.worldname.getCollumn());

	    JobsWorld jobsWorld = Util.getJobsWorld(worldName);
	    if (jobsWorld == null)
		jobsWorld = Util.getJobsWorld(res.getInt(ExploreDataTableFields.worldid.getCollumn()));

	    if (jobsWorld == null)
		return;

	    int x = res.getInt(ExploreDataTableFields.chunkX.getCollumn());
	    int z = res.getInt(ExploreDataTableFields.chunkZ.getCollumn());
	    String names = res.getString(ExploreDataTableFields.playerNames.getCollumn());
	    int id = res.getInt("id");

	    Map<String, ExploreRegion> eRegions = worlds.getOrDefault(jobsWorld.getName(), new HashMap<String, ExploreRegion>());

	    int RegionX = (int) Math.floor(x / 32D);
	    int RegionZ = (int) Math.floor(z / 32D);

	    ExploreRegion region = eRegions.get(RegionX + ":" + RegionZ);
	    if (region == null) {
		region = new ExploreRegion(RegionX, RegionZ);
	    }
	    ExploreChunk chunk = region.getChunk(x, z);
	    if (chunk == null) {
		chunk = new ExploreChunk();
		region.addChunk(x, z, chunk);
	    }
	    chunk.deserializeNames(names);
	    chunk.setDbId(id);

	    eRegions.put(RegionX + ":" + RegionZ, region);
	    worlds.put(jobsWorld.getName(), eRegions);

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
