package com.gamingmesh.jobs.economy;

import java.util.UUID;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerPoints;

@Deprecated
public class PointsData {

    public void addPlayer(UUID uuid) {
	Jobs.getPlayerManager().getJobsPlayer(uuid).getPointsData();
    }

    public void addPlayer(UUID uuid, double points) {
	Jobs.getPlayerManager().getJobsPlayer(uuid).addPoints(points);
    }

    public void addPlayer(UUID uuid, double points, double total) {
	addPlayer(uuid, new PlayerPoints(points, total));

	Jobs.getPlayerManager().getJobsPlayer(uuid).getPointsData().setPoints(points);
	Jobs.getPlayerManager().getJobsPlayer(uuid).getPointsData().setTotalPoints(total);
    }

    public void addPlayer(UUID uuid, PlayerPoints points) {
	PlayerPoints pi = Jobs.getPlayerManager().getJobsPlayer(uuid).getPointsData();
	pi.setPoints(points.getCurrentPoints());
	pi.setTotalPoints(points.getTotalPoints());
	pi.setDbId(points.getDbId());
    }

    public void addPoints(UUID uuid, Double points) {
	Jobs.getPlayerManager().getJobsPlayer(uuid).addPoints(points);
    }

    public PlayerPoints getPlayerPointsInfo(UUID uuid) {
	return Jobs.getPlayerManager().getJobsPlayer(uuid).getPointsData();
    }
}
