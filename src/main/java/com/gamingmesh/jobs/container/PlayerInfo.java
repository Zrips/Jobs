package com.gamingmesh.jobs.container;

import java.util.UUID;

import com.gamingmesh.jobs.Jobs;

public class PlayerInfo {

    int id;
    String name = "Unknown";
    private Long seen;
    private Integer questsDone;
    private UUID uuid;
    private JobsPlayer player;

    public PlayerInfo(String name, int id, UUID uuid, Long seen, Integer questsDone) {
	this.name = name;
	this.id = id;
	this.uuid = uuid;
	this.seen = seen;
	this.questsDone = questsDone;
	player = Jobs.getPlayerManager().getJobsPlayer(uuid);
    }

    public String getName() {
	if (player == null)
	    player = Jobs.getPlayerManager().getJobsPlayer(uuid);
	if (player != null)
	    return player.getUserName();
	return name;
    }

    public int getID() {
	return id;
    }

    public Long getSeen() {
	return seen;
    }

    public UUID getUuid() {
	return uuid;
    }

    public JobsPlayer getJobsPlayer() {
	if (player == null)
	    player = Jobs.getPlayerManager().getJobsPlayer(uuid);
	return player;
    }

    public Integer getQuestsDone() {
	return questsDone;
    }

    public void setQuestsDone(Integer questsDone) {
	this.questsDone = questsDone;
    }
}
