package com.gamingmesh.jobs.container;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

public class BossBarInfo {
	private String jobName;
    private String PlayerName;
    private BossBar bar;
    private int id = -1;

    public BossBarInfo(String PlayerName, String jobName, BossBar bar) {
	this.PlayerName = PlayerName;
	this.jobName = jobName;
	this.bar = bar;
    }

    public void setId(int id) {
	cancel();
	this.id = id;
    }

    public void cancel() {
	if (id != -1)
	    Bukkit.getScheduler().cancelTask(this.id);
    }

    public String getPlayerName() {
	return PlayerName;
    }

    public String getJobName() {
	return jobName;
    }

    public BossBar getBar() {
	return bar;
    }
}
