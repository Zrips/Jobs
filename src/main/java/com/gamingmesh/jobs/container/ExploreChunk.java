package com.gamingmesh.jobs.container;

import java.util.HashSet;
import java.util.Set;

import com.gamingmesh.jobs.Jobs;

public class ExploreChunk {

    int x;
    int z;
    Set<String> playerNames = new HashSet<String>();
    boolean isNewChunk = true;

    public ExploreChunk(String playerName, int x, int z) {
	this.x = x;
	this.z = z;
	this.playerNames.add(playerName);
    }

    public ExploreRespond addPlayer(String playerName) {
	boolean newChunk = false;
	if (!playerNames.contains(playerName)) {
	    playerNames.add(playerName);
	    newChunk = true;
	}
	if (playerNames.size() > Jobs.getExplore().getPlayerAmount())
	    playerNames.remove(0);
	return new ExploreRespond(playerNames.size(), newChunk);
    }

    public boolean isAlreadyVisited(String playerName) {
	return playerNames.contains(playerName);
    }

    public int getCount() {
	return this.playerNames.size();
    }

    public int getX() {
	return this.x;
    }

    public int getZ() {
	return this.z;
    }
    
    public Set<String> getPlayers() {
	return this.playerNames;
    }
    
    public boolean isNew() {
	return this.isNewChunk;
    }
    
    public void setOldChunk() {
	isNewChunk = false;
    }
}
