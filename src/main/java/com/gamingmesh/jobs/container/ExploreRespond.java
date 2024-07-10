package com.gamingmesh.jobs.container;

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
}
