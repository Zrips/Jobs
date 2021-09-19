package com.gamingmesh.jobs.container;

public class ExploreRespond {

    private final int count;
    private boolean newChunk = false;

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
}
