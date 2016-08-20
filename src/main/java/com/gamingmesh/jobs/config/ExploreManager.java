package com.gamingmesh.jobs.config;

import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.container.ExploreRespond;

public class ExploreManager {

	private HashMap<String, ExploreRegion> worlds = new HashMap<String, ExploreRegion>();
	private boolean exploreEnabled = false;
	private int playerAmount = 1;

	public ExploreManager() {
	}

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
			Jobs.getJobsDAO().loadExplore();
		}
	}

	public HashMap<String, ExploreRegion> getWorlds() {
		return worlds;
	}

	public ExploreRespond ChunkRespond(Player player, Chunk chunk, boolean isNew) {
		return ChunkRespond(player.getName(), chunk.getWorld().getName(), chunk.getX(), chunk.getZ(), isNew);
	}

	public ExploreRespond ChunkRespond(String player, String worldName, int x, int z, boolean isNew) {

		int ChunkX = x;
		int ChunkZ = z;

		int RegionX = (int) Math.floor(ChunkX / 32D);
		int RegionZ = (int) Math.floor(ChunkZ / 32D);

		if (!worlds.containsKey(worldName)) {
			ExploreChunk eChunk = new ExploreChunk(player, ChunkX, ChunkZ);
			if (!isNew)
				eChunk.setOldChunk();
			ExploreRegion eRegion = new ExploreRegion(RegionX, RegionZ);
			eRegion.addChunk(eChunk);
			worlds.put(worldName, eRegion);
			return new ExploreRespond(eChunk.getCount(), true);
		}
		ExploreRegion eRegion = worlds.get(worldName);
		ExploreChunk eChunk = null;
		for (ExploreChunk one : eRegion.getChunks()) {
			if (one.getX() != ChunkX)
				continue;
			if (one.getZ() != ChunkZ)
				continue;
			eChunk = one;
			if (!isNew)
				eChunk.setOldChunk();
			break;
		}

		if (eChunk == null) {
			eChunk = new ExploreChunk(player, ChunkX, ChunkZ);
			if (!isNew)
				eChunk.setOldChunk();
			eRegion.addChunk(eChunk);
			return new ExploreRespond(eChunk.getCount(), true);
		}
		return eChunk.addPlayer(player);
	}
}
