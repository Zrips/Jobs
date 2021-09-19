package com.gamingmesh.jobs.container;

import java.util.UUID;

import com.gamingmesh.jobs.Jobs;

public class PlayerInfo {

    private final int id;
    private String name = "Unknown";
    private final Long seen;
    private Integer questsDone;
    private String questProgression;
    private final UUID uuid;
    private JobsPlayer player;

    public PlayerInfo(String name, int id, UUID uuid, Long seen, Integer questsDone, String questProgression) {
	this.name = name == null ? "Unknown" : name;
	this.id = id;
	this.uuid = uuid;
	this.seen = seen;
	this.questsDone = questsDone;
	this.questProgression = questProgression;
	player = Jobs.getPlayerManager().getJobsPlayer(uuid);
	if (player != null)
	    player.setUserId(id);
    }

    public PlayerInfo(String name, int id, UUID uuid, Long seen, Integer questsDone) {
	this(name, id, uuid, seen, questsDone, null);
    }

    public String getName() {
	if (player == null)
	    player = Jobs.getPlayerManager().getJobsPlayer(uuid);
	return player != null ? player.getName() : name;
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

    public String getQuestProgression() {
	return questProgression;
    }

    public void setQuestProgression(String questProgression) {
	this.questProgression = questProgression;
    }
}
