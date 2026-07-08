package com.gamingmesh.jobs.api;

import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;

import net.Zrips.CMILib.Sounds.CMISound;

public final class JobsLevelUpEvent extends BaseEvent implements Cancellable {

    private JobsPlayer player;
    private Job job;
    private Title oldTitle;
    private Title newTitle;

    private CMISound levelupSound;
    private CMISound titleChangeSound;

    private int newLevel;
    private int oldLevel;
    private boolean cancelled = false;

    @Deprecated
    public JobsLevelUpEvent(JobsPlayer jPlayer, Job job, int level, Title oldTitle, Title newTitle, CMISound levelupSound, CMISound titleChangeSound) {
        this(jPlayer, job, level, level, oldTitle, newTitle, levelupSound, titleChangeSound);
    }

    public JobsLevelUpEvent(JobsPlayer jPlayer, Job job, int oldLevel, int newLevel, Title oldTitle, Title newTitle, CMISound levelupSound, CMISound titleChangeSound) {
        this.player = jPlayer;
        this.job = job;
        this.oldTitle = oldTitle;
        this.newTitle = newTitle;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.setLevelupSound(levelupSound);
        this.setTitleChangeSound(titleChangeSound);
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
     * Gets the new title of the player.
     * 
     * @return {@link Title}
     */
    public Title getNewTitle() {
        return newTitle;
    }

    /**
     * Returns the current job progression level.
     * 
     * @return job progression level
     */
    public int getLevel() {
        return newLevel;
    }

    /**
     * Returns the old job progression level.
     * 
     * @return job progression level
     */
    public int getOldLevel() {
        return oldLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public CMISound getLevelupSound() {
        return levelupSound;
    }

    public void setLevelupSound(CMISound levelupSound) {
        this.levelupSound = levelupSound;
    }

    public CMISound getTitleChangeSound() {
        return titleChangeSound;
    }

    public void setTitleChangeSound(CMISound titleChangeSound) {
        this.titleChangeSound = titleChangeSound;
    }
}