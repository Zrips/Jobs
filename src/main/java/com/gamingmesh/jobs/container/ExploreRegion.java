package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;

public class ExploreRegion {

    private int x;
    private int z;

    private final Map<Short, ExploreChunk> chunks = new HashMap<>();

    public ExploreRegion(int x, int z) {
	this.x = x;
	this.z = z;
    }

    public void addChunk(int relativeX, int relativeZ, ExploreChunk chunk) {
	chunks.put(getPlace(relativeX, relativeZ), chunk);
    }

    public Map<Short, ExploreChunk> getChunks() {
	return chunks;
    }

    public ExploreChunk getChunk(int relativeX, int relativeZ) {
	return getChunk(getPlace(relativeX, relativeZ));
    }

    public ExploreChunk getChunk(Chunk chunk) {
	return getChunk(getPlace(chunk));
    }

    public ExploreChunk getChunk(long place) {
	return chunks.get((short) place);
    }

    public ExploreChunk getChunk(Short place) {
	return chunks.get(place);
    }

    private long getPlace(Chunk chunk) {
	return getPlace((x * 32) - chunk.getX(), (z * 32) - chunk.getZ());
    }

    private static short getPlace(int relativeX, int relativeZ) {
	return (short) (relativeX * 32 + relativeZ);
    }

    @Deprecated
    public int getChunkX(long place) {
	return (int) (place / 32);
    }

    public int getChunkRelativeX(short place) {
	return place / 32;
    }

    public int getChunkGlobalX(short place) {
	return (getX() * 32) - getChunkRelativeX(place);
    }

    @Deprecated
    public int getChunkZ(long place) {
	return (int) place % 32;
    }

    public int getChunkRelativeZ(short place) {
	return place % 32;
    }

    public int getChunkGlobalZ(short place) {
	return (getZ() * 32) - getChunkRelativeZ(place);
    }

    public int getX() {
	return x;
    }

    public int getZ() {
	return z;
    }
}
