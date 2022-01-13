package com.gamingmesh.jobs.api;

import org.bukkit.Sound;
import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;

public final class JobsLevelUpEvent extends BaseEvent implements Cancellable {

    private JobsPlayer player;
    private Job job;
    private Title oldTitle;
    private Title newTitle;

    private Sound levelupSound;
    private Sound titleChangeSound;

    private int level, soundLevelupVolume = 1, soundLevelupPitch = 3,
	    titleChangeVolume = 1, titleChangePitch = 3;
    private boolean cancelled = false;

    @Deprecated
    public JobsLevelUpEvent(JobsPlayer jPlayer, String jobName, int level, Title oldTitle, Title newTitle, String soundLevelupSound, int soundLevelupVolume,
	int soundLevelupPitch, String soundTitleChangeSound, int soundTitleChangeVolume, int soundTitleChangePitch) {
	this(jPlayer, Jobs.getJob(jobName), level, oldTitle, newTitle, soundLevelupSound, soundLevelupVolume, soundLevelupPitch,
	    soundTitleChangeSound, soundTitleChangeVolume, soundTitleChangePitch);
    }

    public JobsLevelUpEvent(JobsPlayer jPlayer, Job job, int level, Title oldTitle, Title newTitle, String levelupSound, int soundLevelupVolume,
	int soundLevelupPitch, String titleChangeSound, int titleChangeVolume, int titleChangePitch) {
	this.player = jPlayer;
	this.job = job;
	this.oldTitle = oldTitle;
	this.newTitle = newTitle;
	this.level = level;
	this.levelupSound = getSound(levelupSound);
	this.soundLevelupVolume = soundLevelupVolume;
	this.soundLevelupPitch = soundLevelupPitch;
	this.titleChangeSound = getSound(titleChangeSound);
	this.titleChangeVolume = titleChangeVolume;
	this.titleChangePitch = titleChangePitch;
    }

    private static Sound getSound(String soundName) {
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
     * @deprecated use {@link #getJob()} instead
     */
    @Deprecated
    public String getJobName() {
	return job.getName();
    }

    /**
     * Returns the Job in which the player have been level up.
     * 
     * @return the corresponding {@link Job} instance
     */
    public Job getJob() {
	return job;
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
	return this.levelupSound != null ? this.levelupSound.name() : "";
    }

    public Sound getSound() {
	return levelupSound;
    }

    public void setSound(Sound soundLevelupSound) {
	this.levelupSound = soundLevelupSound == null ? Sound.values()[0] : soundLevelupSound;
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
	return this.titleChangeSound != null ? this.titleChangeSound.name() : "";
    }

    public Sound getTitleChangeSound() {
	return titleChangeSound;
    }

    public void setTitleChangeSound(Sound soundTitleChangeSound) {
	this.titleChangeSound = soundTitleChangeSound == null ? Sound.values()[0] : soundTitleChangeSound;
    }

    public int getTitleChangeVolume() {
	return titleChangeVolume;
    }

    public void setTitleChangeVolume(int titleChangeVolume) {
	this.titleChangeVolume = titleChangeVolume;
    }

    public int getTitleChangePitch() {
	return titleChangePitch;
    }

    public void setTitleChangePitch(int titleChangePitch) {
	this.titleChangePitch = titleChangePitch;
    }

    /**
     * Returns the current job progression level.
     * 
     * @return job progression level
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