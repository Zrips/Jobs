package com.gamingmesh.jobs.container;

import org.bukkit.boss.BossBar;

import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class BossBarInfo {
    private String jobName;
    private String PlayerName;
    private BossBar bar;
    private CMITask scheduler = null;

    public BossBarInfo(String PlayerName, String jobName, BossBar bar) {
	this.PlayerName = PlayerName;
	this.jobName = jobName;
	this.bar = bar;
    }

    public void setScheduler(CMITask cmiTask) {
	cancel();
	this.scheduler = cmiTask;
    }

    public void cancel() {
	if (scheduler != null)
	    scheduler.cancel();
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
