package com.gamingmesh.jobs.economy;

import java.util.HashMap;
import java.util.UUID;

import com.gamingmesh.jobs.container.PlayerPoints;

public class PointsData {

    HashMap<UUID, PlayerPoints> Pointbase = new HashMap<UUID, PlayerPoints>();

    public PointsData() {
    }

    public HashMap<UUID, PlayerPoints> getPointBase() {
	return Pointbase;
    }

    public void addPlayer(UUID uuid) {
	addPlayer(uuid, 0D);
    }

    public void addPlayer(UUID uuid, double points) {
	addPlayer(uuid, points, 0D);
    }

    public void addPlayer(UUID uuid, double points, double total) {
	if (!Pointbase.containsKey(uuid))
	    Pointbase.put(uuid, new PlayerPoints(points, total));
    }

    public void addPoints(UUID uuid, Double points) {
	if (!Pointbase.containsKey(uuid))
	    addPlayer(uuid, points);
	else {
	    Pointbase.get(uuid).addPoints(points);
	}
    }

    public PlayerPoints getPlayerPointsInfo(UUID uuid) {
	return Pointbase.containsKey(uuid) ? Pointbase.get(uuid) : new PlayerPoints();
    }
}
