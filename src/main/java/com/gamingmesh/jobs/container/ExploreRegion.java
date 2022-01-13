package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;

public class ExploreRegion {

    int x;
    int z;

    private final Map<Long, ExploreChunk> chunks = new HashMap<>();

    public ExploreRegion(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public void addChunk(int x, int z, ExploreChunk chunk) {
        chunks.put(getPlace(x, z), chunk);
    }

    public Map<Long, ExploreChunk> getChunks() {
        return chunks;
    }

    public ExploreChunk getChunk(int x, int z) {
        return getChunk(getPlace(x, z));
    }

    public ExploreChunk getChunk(Chunk chunk) {
        return getChunk(getPlace(chunk));
    }

    public ExploreChunk getChunk(long place) {
        return chunks.get(place);
    }

    private static long getPlace(Chunk chunk) {
        return getPlace(chunk.getX(), chunk.getZ());
    }

    private static long getPlace(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffff);
    }

    public int getChunkX(long place) {
        return (int)(place >> 32);
    }

    public int getChunkZ(long place) {
        return (int) place;
    }
}
