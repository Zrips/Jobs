package com.gamingmesh.jobs.api;

import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;

public final class JobsLevelUpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private JobsPlayer player;
    private String JobName;
    private Title OldTitle;
    private Title NewTitle;
    private int level;
    private Sound soundLevelupSound;
    private int soundLevelupVolume = 1;
    private int soundLevelupPitch = 3;
    private Sound soundTitleChangeSound;
    private int soundTitleChangeVolume;
    private int soundTitleChangePitch;
    private boolean cancelled;

    public JobsLevelUpEvent(JobsPlayer jPlayer, String JobName, int level, Title OldTitle, Title NewTitle, String soundLevelupSound, Integer soundLevelupVolume,
	Integer soundLevelupPitch, String soundTitleChangeSound, Integer soundTitleChangeVolume, Integer soundTitleChangePitch) {
	this.player = jPlayer;
	this.JobName = JobName;
	this.OldTitle = OldTitle;
	this.NewTitle = NewTitle;
	this.level = level;
	this.soundLevelupSound = getSound(soundLevelupSound);
	this.soundLevelupVolume = soundLevelupVolume;
	this.soundLevelupPitch = soundLevelupPitch;
	this.soundTitleChangeSound = getSound(soundTitleChangeSound);
	this.soundTitleChangeVolume = soundTitleChangeVolume;
	this.soundTitleChangePitch = soundTitleChangePitch;
    }

    private static Sound getSound(String soundName) {
	for (Sound one : Sound.values()) {
	    if (one.name().equalsIgnoreCase(soundName))
		return one;
	}
	return null;
    }

    public JobsPlayer getPlayer() {
	return this.player;
    }

    public String getJobName() {
	return this.JobName;
    }

    public Title getOldTitle() {
	return this.OldTitle;
    }

    public String getOldTitleName() {
	return this.OldTitle.getName();
    }

    public String getOldTitleShort() {
	return this.OldTitle.getShortName();
    }

    public String getOldTitleColor() {
	return this.OldTitle.getChatColor().toString();
    }

    public Title getNewTitle() {
	return this.NewTitle;
    }

    public String getNewTitleName() {
	return this.NewTitle.getName();
    }

    public String getNewTitleShort() {
	return this.NewTitle.getShortName();
    }

    public String getNewTitleColor() {
	return this.NewTitle.getChatColor().toString();
    }

    @Deprecated
    public String getSoundName() {
	return this.soundLevelupSound.name();
    }

    public Sound getSound() {
	return this.soundLevelupSound;
    }

    public void setSound(Sound sound) {
	this.soundLevelupSound = sound;
    }

    public int getSoundVolume() {
	return this.soundLevelupVolume;
    }

    public void setSoundVolume(int volume) {
	this.soundLevelupVolume = volume;
    }

    public int getSoundPitch() {
	return this.soundLevelupPitch;
    }

    public void setSoundPitch(int pitch) {
	this.soundLevelupPitch = pitch;
    }

    @Deprecated
    public String getTitleChangeSoundName() {
	return this.soundTitleChangeSound.name();
    }

    public Sound getTitleChangeSound() {
	return this.soundTitleChangeSound;
    }

    public void setTitleChangeSound(Sound sound) {
	this.soundTitleChangeSound = sound;
    }

    public int getTitleChangeVolume() {
	return this.soundTitleChangeVolume;
    }

    public void setTitleChangeVolume(int volume) {
	this.soundTitleChangeVolume = volume;
    }

    public int getTitleChangePitch() {
	return this.soundTitleChangePitch;
    }

    public void setTitleChangePitch(int pitch) {
	this.soundTitleChangePitch = pitch;
    }

    public int getLevel() {
	return this.level;
    }

    @Override
    public boolean isCancelled() {
	return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
	cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }
}