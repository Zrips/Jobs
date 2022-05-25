package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

import com.gamingmesh.jobs.Jobs;

import net.Zrips.CMILib.Logs.CMIDebug;

public class ExploreChunk {

    private List<Integer> playerIds = new ArrayList<>();
    private int dbId = -1;
    private boolean updated = false;

    public ExploreRespond addPlayer(int playerId) {
	if (isFullyExplored()) {
	    return new ExploreRespond(Jobs.getExploreManager().getPlayerAmount() + 1, false);
	}

	boolean newChunkForPlayer = false;
	if (!playerIds.contains(playerId)) {
	    if (playerIds.size() < Jobs.getExploreManager().getPlayerAmount()) {
		playerIds.add(playerId);
		updated = true;
	    }

	    newChunkForPlayer = true;
	}

	if (Jobs.getGCManager().ExploreCompact && playerIds.size() >= Jobs.getExploreManager().getPlayerAmount()) {
	    playerIds = null;
	}

	List<Integer> players = getPlayers();
	    
	return new ExploreRespond(newChunkForPlayer ? players.size() : players.size() + 1, newChunkForPlayer);
    }

    public boolean isAlreadyVisited(int playerId) {
	return isFullyExplored() || playerIds.contains(playerId);
    }

    public int getCount() {
	return isFullyExplored() ? Jobs.getExploreManager().getPlayerAmount() : playerIds.size();
    }

    public List<Integer> getPlayers() {
	return playerIds == null ? new ArrayList<>() : playerIds;
    }

    public String serializeNames() {
	if (playerIds == null)
	    return null;

	StringBuilder s = new StringBuilder();
	for (Integer one : playerIds) {
	    if (s.length() != 0)
		s.append(';');

	    s.append(one);
	}
	return s.toString();
    }

    public void deserializeNames(String names) {
	if (names == null || names.isEmpty()) {
	    playerIds = null;
	    return;
	}

	if (playerIds == null) {
	    playerIds = new ArrayList<>();
	}

	for (String one : names.split(";")) {
	    try {
		int id = Integer.parseInt(one);

		if (Jobs.getPlayerManager().getPlayerInfo(id) != null)
		    playerIds.add(id);
	    } catch (NumberFormatException e) {
		updated = true;
	    }
	}

	if (Jobs.getGCManager().ExploreCompact && playerIds.size() >= Jobs.getExploreManager().getPlayerAmount()) {
	    playerIds = null;

	    if (!names.isEmpty())
		updated = true;
	}
    }

    public int getDbId() {
	return dbId;
    }

    public void setDbId(int dbId) {
	this.dbId = dbId;
    }

    public boolean isUpdated() {
	return updated;
    }

    public void setUpdated(boolean updated) {
	this.updated = updated;
    }

    public boolean isFullyExplored() {
	return playerIds == null || playerIds.size() >= Jobs.getExploreManager().getPlayerAmount();
    }
}
