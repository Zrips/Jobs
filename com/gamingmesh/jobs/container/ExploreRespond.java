package com.gamingmesh.jobs.container;

public class ExploreRespond {

    int count;
    boolean newChunk = false;

    public ExploreRespond(int count, boolean newChunk) {
	this.count = count;
	this.newChunk = newChunk;
    }

    public int getCount() {
	return this.count;
    }

    public boolean isNewChunk() {
	return this.newChunk;
    }
}
