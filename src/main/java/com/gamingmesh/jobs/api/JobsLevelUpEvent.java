package com.gamingmesh.jobs.api;

import org.bukkit.Sound;
import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;

public final class JobsLevelUpEvent extends BaseEvent implements Cancellable {

    private JobsPlayer player;
    private String jobName;
    private Title oldTitle;
    private Title newTitle;

    private Sound soundLevelupSound;
    private Sound soundTitleChangeSound;

    private int level, soundLevelupVolume = 1, soundLevelupPitch = 3,
	    soundTitleChangeVolume = 1, soundTitleChangePitch = 3;
    private boolean cancelled = false;

    public JobsLevelUpEvent(JobsPlayer jPlayer, String JobName, int level, Title OldTitle, Title NewTitle, String soundLevelupSound, Integer soundLevelupVolume,
	Integer soundLevelupPitch, String soundTitleChangeSound, Integer soundTitleChangeVolume, Integer soundTitleChangePitch) {
	this.player = jPlayer;
	this.jobName = JobName;
	this.oldTitle = OldTitle;
	this.newTitle = NewTitle;
	this.level = level;
	this.soundLevelupSound = getSound(soundLevelupSound);
	this.soundLevelupVolume = soundLevelupVolume;
	this.soundLevelupPitch = soundLevelupPitch;
	this.soundTitleChangeSound = getSound(soundTitleChangeSound);
	this.soundTitleChangeVolume = soundTitleChangeVolume;
	this.soundTitleChangePitch = soundTitleChangePitch;
    }

    private Sound getSound(String soundName) {
	if (soundName != null) {
	    for (Sound one : Sound.values()) {
		if (one.name().equalsIgnoreCase(soundName))
		    return one;
	    }
	}

	return Sound.values()[0];
    }

    /**
     * Returns the player who got level up in a job.
     * 
     * @return {@link JobsPlayer}
     */
    public JobsPlayer getPlayer() {
	return player;
    }

    /**
     * Gets the job name where the player level up.
     * 
     * @return the job name
     */
    public String getJobName() {
	return jobName;
    }

    /**
     * Gets the old title of the player.
     * 
     * @return {@link Title}
     */
    public Title getOldTitle() {
	return oldTitle;
    }

    /**
     * @deprecated use {@link #getOldTitle()}
     */
    @Deprecated
    public String getOldTitleName() {
	return oldTitle.getName();
    }

    /**
     * @deprecated use {@link #getOldTitle()}
     */
    @Deprecated
    public String getOldTitleShort() {
	return oldTitle.getShortName();
    }

    /**
     * @deprecated use {@link #getOldTitle()}
     */
    @Deprecated
    public String getOldTitleColor() {
	return oldTitle.getChatColor().toString();
    }

    /**
     * Gets the new title of the player.
     * 
     * @return {@link Title}
     */
    public Title getNewTitle() {
	return newTitle;
    }

    /**
     * @deprecated use {@link #getNewTitle()}
     */
    @Deprecated
    public String getNewTitleName() {
	return newTitle.getName();
    }

    /**
     * @deprecated use {@link #getNewTitle()}
     */
    @Deprecated
    public String getNewTitleShort() {
	return newTitle.getShortName();
    }

    /**
     * @deprecated use {@link #getNewTitle()}
     */
    @Deprecated
    public String getNewTitleColor() {
	return newTitle.getChatColor().toString();
    }

    @Deprecated
    public String getSoundName() {
	return this.soundLevelupSound != null ? this.soundLevelupSound.name() : "";
    }

    public Sound getSound() {
	return soundLevelupSound;
    }

    public void setSound(Sound soundLevelupSound) {
	this.soundLevelupSound = soundLevelupSound == null ? Sound.values()[0] : soundLevelupSound;
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
	return soundTitleChangeSound;
    }

    public void setTitleChangeSound(Sound soundTitleChangeSound) {
	this.soundTitleChangeSound = soundTitleChangeSound == null ? Sound.values()[0] : soundTitleChangeSound;
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

    /**
     * Returns the player job progression level.
     * 
     * @return player job progression level
     */
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