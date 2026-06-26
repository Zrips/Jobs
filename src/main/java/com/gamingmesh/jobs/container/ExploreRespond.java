package com.gamingmesh.jobs.container;

import org.bukkit.Chunk;

import com.gamingmesh.jobs.Jobs;

public class ExploreRespond {

    private int count = 0;
    private boolean newChunk = false;

    public ExploreRespond() {
    }

    public ExploreRespond(int count, boolean newChunk) {
        this.count = count;
        this.newChunk = newChunk;
    }

    public int getCount() {
        return count;
    }

    public boolean isNewChunk() {
        return newChunk;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setNewChunk(boolean newChunk) {
        this.newChunk = newChunk;
    }

    public static ExploreRespond get(JobsPlayer jPlayer, Chunk chunk) {
        if (Jobs.getGCManager().useNewExploration)
            return Jobs.getChunkExplorationManager().chunkRespond(jPlayer.getUserId(), chunk);
        else
            return Jobs.getExploreManager().chunkRespond(jPlayer.getUserId(), chunk);
    }
}
