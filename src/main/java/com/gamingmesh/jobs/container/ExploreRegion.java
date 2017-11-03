package com.gamingmesh.jobs.container;

import java.util.HashMap;

import org.bukkit.Chunk;

public class ExploreRegion {

    int x;
    int z;
    HashMap<String, ExploreChunk> chunks = new HashMap<String, ExploreChunk>();

    public ExploreRegion(int x, int z) {
	this.x = x;
	this.z = z;
    }

    public void addChunk(ExploreChunk chunk) {
	chunks.put(chunk.getX() + ":" + chunk.getZ(), chunk);
    }

    public HashMap<String, ExploreChunk> getChunks() {
	return chunks;
    }

    public ExploreChunk getChunk(int x, int z) {
	return getChunk(x + ":" + z);
    }

    public ExploreChunk getChunk(Chunk chunk) {
	return getChunk(chunk.getX() + ":" + chunk.getZ());
    }

    public ExploreChunk getChunk(String cord) {
	return chunks.get(cord);
    }
}
