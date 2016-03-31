/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.container;

import java.util.HashMap;

public class JobProgression {
    private Job job;
    private JobsPlayer jPlayer;
    private double experience;
    private int level;
    private transient int maxExperience = -1;
    private double MoneyBoost = -1;
    private double PointBoost = -1;
    private double ExpBoost = -1;

    public JobProgression(Job job, JobsPlayer jPlayer, int level, double experience, double MoneyBoost, double PointBoost, double ExpBoost) {
	this.job = job;
	this.jPlayer = jPlayer;
	this.experience = experience;
	this.level = level;
	this.MoneyBoost = MoneyBoost;
	this.PointBoost = PointBoost;
	this.ExpBoost = ExpBoost;
    }

    /**
     * Can the job level up?
     * @return true if the job can level up
     * @return false if the job cannot
     */
    public boolean canLevelUp() {
	return experience >= maxExperience;
    }

    /**
     * Return the MoneyBoost
     * @return the MoneyBoost
     */
    public double getMoneyBoost() {
	if (this.MoneyBoost == -1)
	    this.MoneyBoost = JobsPlayer.getMoneyBoost(this.job.getName(), this.jPlayer.getPlayer());
	return this.MoneyBoost;
    }

    /**
     * Return the PointBoost
     * @return the PointBoost
     */
    public double getPointBoost() {
	if (this.PointBoost == -1)
	    this.PointBoost = JobsPlayer.getPointBoost(this.job.getName(), this.jPlayer.getPlayer());
	return this.PointBoost;
    }

    /**
     * Return the ExpBoost
     * @return the ExpBoost
     */
    public double getExpBoost() {
	if (this.ExpBoost == -1)
	    this.ExpBoost = JobsPlayer.getExpBoost(this.job.getName(), this.jPlayer.getPlayer());
	return this.ExpBoost;
    }

    /**
     * Return the job
     * @return the job
     */
    public Job getJob() {
	return job;
    }

    /**
     * Set the job
     * @param job - the new job to be set
     */
    public void setJob(Job job) {
//		synchronized (jPlayer.saveLock) {
	jPlayer.setSaved(false);
	this.job = job;
	reloadMaxExperienceAndCheckLevelUp();
//		}
    }

    /**
     * Get the experience in this job
     * @return the experiece in this job
     */
    public double getExperience() {
	return experience;
    }

    /**
     * Adds experience for this job
     * @param experience - the experience in this job
     * @return - job level up
     */
    public boolean addExperience(double experience) {
//		synchronized (jPlayer.saveLock) {
	jPlayer.setSaved(false);
	this.experience += experience;
	return checkLevelUp();
//		}
    }

    /**
     * Get the maximum experience for this level
     * @return the experience needed to level up
     */
    public int getMaxExperience() {
	return maxExperience;
    }

    /**
     * Get the current level of this job
     * @return the level of this job
     */
    public int getLevel() {
	return level;
    }

    /**
     * Set the level of this job
     * @param level - the new level for this job
     */
    public void setLevel(int level) {
//		synchronized (jPlayer.saveLock) {
	jPlayer.setSaved(false);
	this.level = level;
	reloadMaxExperienceAndCheckLevelUp();
//		}
    }

    /**
     * Reloads max experience
     */
    public void reloadMaxExperience() {
	HashMap<String, Double> param = new HashMap<String, Double>();
	param.put("joblevel", (double) level);
	param.put("numjobs", (double) jPlayer.getJobProgression().size());
	this.maxExperience = (int) job.getMaxExp(param);
    }

    /**
     * Performs a level up
     * @returns if level up was performed
     */
    private boolean checkLevelUp() {
	boolean ret = false;
	while (canLevelUp()) {

	    int maxLevel = 0;
	    if (jPlayer.havePermission("jobs." + job.getName() + ".vipmaxlevel") && job.getVipMaxLevel() != 0)
		maxLevel = job.getVipMaxLevel();
	    else
		maxLevel = job.getMaxLevel();

	    // Don't level up at max level        	
	    if (job.getMaxLevel() > 0 && level >= maxLevel)
		break;
	    level++;
	    experience -= maxExperience;
	    ret = true;
	    reloadMaxExperience();
	}

	// At max level
	if (experience > maxExperience)
	    experience = maxExperience;
	return ret;
    }

    /**
     * Reloads max experience and checks for level up
     * Do this whenever job or level changes
     * @return if leveled up
     */

    private boolean reloadMaxExperienceAndCheckLevelUp() {
	reloadMaxExperience();
	return checkLevelUp();
    }
}
