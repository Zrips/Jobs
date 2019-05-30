package com.gamingmesh.jobs.api;

import org.bukkit.Sound;
import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;

public final class JobsLevelUpEvent extends BaseEvent implements Cancellable {
    private JobsPlayer player;
    private String JobName;
    private Title OldTitle;
    private Title NewTitle;
    private int level;
    private Sound soundLevelupSound = Sound.values()[0];
    private int soundLevelupVolume = 1;
    private int soundLevelupPitch = 3;
    private Sound soundTitleChangeSound = Sound.values()[0];
    private int soundTitleChangeVolume = 1;
    private int soundTitleChangePitch = 3;
    private boolean cancelled = false;

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
	return player;
    }

    public String getJobName() {
	return JobName;
    }

    public Title getOldTitle() {
	return OldTitle;
    }

    public String getOldTitleName() {
	return OldTitle.getName();
    }

    public String getOldTitleShort() {
	return OldTitle.getShortName();
    }

    public String getOldTitleColor() {
	return OldTitle.getChatColor().toString();
    }

    public Title getNewTitle() {
	return NewTitle;
    }

    public String getNewTitleName() {
	return NewTitle.getName();
    }

    public String getNewTitleShort() {
	return NewTitle.getShortName();
    }

    public String getNewTitleColor() {
	return NewTitle.getChatColor().toString();
    }

    @Deprecated
    public String getSoundName() {
	return this.soundLevelupSound != null ? this.soundLevelupSound.name() : "";
    }

    public Sound getSound() {
	return this.soundLevelupSound == null ? Sound.values()[0] : this.soundLevelupSound;
    }

    public void setSound(Sound soundLevelupSound) {
	this.soundLevelupSound = soundLevelupSound;
    }

    public int getSoundVolume() {
	return soundLevelupVolume;
    }

    public void setSoundVolume(int soundLevelupVolume) {
	this.soundLevelupVolume = soundLevelupVolume;
    }

    public int getSoundPitch() {
	return soundLevelupPitch;
    }

    public void setSoundPitch(int soundLevelupPitch) {
	this.soundLevelupPitch = soundLevelupPitch;
    }

    @Deprecated
    public String getTitleChangeSoundName() {
	return this.soundTitleChangeSound != null ? this.soundTitleChangeSound.name() : "";
    }

    public Sound getTitleChangeSound() {
	return this.soundTitleChangeSound == null ? Sound.values()[0] : this.soundTitleChangeSound;
    }

    public void setTitleChangeSound(Sound soundTitleChangeSound) {
	this.soundTitleChangeSound = soundTitleChangeSound;
    }

    public int getTitleChangeVolume() {
	return soundTitleChangeVolume;
    }

    public void setTitleChangeVolume(int soundTitleChangeVolume) {
	this.soundTitleChangeVolume = soundTitleChangeVolume;
    }

    public int getTitleChangePitch() {
	return soundTitleChangePitch;
    }

    public void setTitleChangePitch(int soundTitleChangePitch) {
	this.soundTitleChangePitch = soundTitleChangePitch;
    }

    public int getLevel() {
	return level;
    }

    @Override
    public boolean isCancelled() {
	return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
	this.cancelled = cancelled;
    }
}