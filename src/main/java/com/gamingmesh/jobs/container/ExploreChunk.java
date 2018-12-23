package com.gamingmesh.jobs.container;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.gamingmesh.jobs.Jobs;

public class ExploreChunk {

    private int x;
    private int z;
    private Set<String> playerNames = new HashSet<>();
    private Integer dbId = null;
    private boolean updated = false;

    public ExploreChunk(String playerName, int x, int z) {
	this.x = x;
	this.z = z;
	this.playerNames.add(playerName);
    }

    public ExploreChunk(int x, int z) {
	this.x = x;
	this.z = z;
    }

    public ExploreRespond addPlayer(String playerName) {
	boolean newChunkForPlayer = false;
	if (!playerNames.contains(playerName)) {
	    if (playerNames.size() < Jobs.getExplore().getPlayerAmount()) {
		playerNames.add(playerName);
		updated = true;
	    }
	    newChunkForPlayer = true;
	}
	return new ExploreRespond(newChunkForPlayer ? playerNames.size() : playerNames.size() + 1, newChunkForPlayer);
    }

    public boolean isAlreadyVisited(String playerName) {
	return playerNames.contains(playerName);
    }

    public int getCount() {
	return playerNames.size();
    }

    public int getX() {
	return x;
    }

    public int getZ() {
	return z;
    }

    public Set<String> getPlayers() {
	return playerNames;
    }

    public String serializeNames() {
	String s = "";
	for (String one : this.playerNames) {
	    if (!s.isEmpty())
		s += ";";
	    s += one;
	}
	return s;
    }

    public void deserializeNames(String names) {
	if (names.contains(";"))
	    playerNames.addAll(Arrays.asList(names.split(";")));
	else
	    playerNames.add(names);
    }

    public Integer getDbId() {
	return dbId;
    }

    public void setDbId(Integer dbId) {
	this.dbId = dbId;
    }

    public boolean isUpdated() {
	return updated;
    }

    public void setUpdated(boolean updated) {
	this.updated = updated;
    }
}
