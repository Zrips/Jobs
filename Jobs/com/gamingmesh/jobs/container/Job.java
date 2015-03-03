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

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.util.ChatColor;

public class Job {
    // job info
    private EnumMap<ActionType, List<JobInfo>> jobInfo = new EnumMap<ActionType, List<JobInfo>>(ActionType.class);
    // permissions
    private List<JobPermission> jobPermissions;
    // job name
    private String jobName;
    // job short name (for use in multiple jobs)
    private String jobShortName;
    // short description of the job
    private String description;
    // job chat colour
    private ChatColor jobColour;
    // job leveling equation
    private Parser maxExpEquation;
    // display method
    private DisplayMethod displayMethod;
    // max level
    private int maxLevel;
    // max number of people allowed with this job on the server.
    private Integer maxSlots;

    /**
     * Constructor
     * @param jobName - the name of the job
     * @param jobShortName - the shortened version of the name of the job.
     * @param description - a short description of the job.
     * @param jobColour - the colour of the job title as displayed in chat.
     * @param maxExpEquation - the equation by which the exp needed to level up is calculated
     * @param displayMethod - the display method for this job.
     * @param maxLevel - the maximum level allowed (null for no max level)
     * @param maxSlots - the maximum number of people allowed to have this job at one time (null for no limits)
     * @param jobPermissions - permissions gained for having the job
     */
    public Job(String jobName,
            String jobShortName,
            String description,
            ChatColor jobColour,
            Parser maxExpEquation,
            DisplayMethod displayMethod,
            int maxLevel,
            Integer maxSlots,
            List<JobPermission> jobPermissions) {
        this.jobName = jobName;
        this.jobShortName = jobShortName;
        this.description = description;
        this.jobColour = jobColour;
        this.maxExpEquation = maxExpEquation;
        this.displayMethod = displayMethod;
        this.maxLevel = maxLevel;
        this.maxSlots = maxSlots;
        this.jobPermissions = jobPermissions;
    }
    
    /**
     * Sets job info for action type
     * @param type - The action type
     * @param info - the job info
     */
    public void setJobInfo(ActionType type, List<JobInfo> info) {
        jobInfo.put(type, info);
    }
    
    /**
     * Gets the job info for the particular type
     * @param type - The action type
     * @return Job info list
     */
    
    public List<JobInfo> getJobInfo(ActionType type) {
        return Collections.unmodifiableList(jobInfo.get(type));
    }
    
    /**
     * Function to get the income for an action
     * @param action - The action info
     * @param level - players job level
     * @param numjobs - number of jobs for the player
     * @return the income received for performing action
     */
    
    public Double getIncome(ActionInfo action, int level, int numjobs) {
        List<JobInfo> jobInfo = getJobInfo(action.getType());
        for (JobInfo info : jobInfo) {
            if (info.getName().equals(action.getName()) || info.getName().equals(action.getNameWithSub()))
                return info.getIncome(level, numjobs);
        }
        return null;
    }
    
    /**
     * Function to get the income for an action
     * @param action - The action info
     * @param level - players job level
     * @param numjobs - number of jobs for the player
     * @return the income received for performing action
     */
    
    public Double getExperience(ActionInfo action, int level, int numjobs) {
        List<JobInfo> jobInfo = getJobInfo(action.getType());
        for (JobInfo info : jobInfo) {
            if (info.getName().equals(action.getName()) || info.getName().equals(action.getNameWithSub()))
                return info.getExperience(level, numjobs);
        }
        return null;
    }
    
    /**
     * Get the job name
     * @return the job name
     */
    public String getName(){
        return jobName;
    }
    
    /**
     * Get the shortened version of the jobName
     * @return the shortened version of the jobName
     */
    public String getShortName(){
        return jobShortName;
    }
    
    /**
     * Gets the description
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the Color of the job for chat
     * @return the Color of the job for chat
     */
    public ChatColor getChatColor(){
        return jobColour;
    }
    
    /**
     * Get the MaxExpEquation of the job
     * @return the MaxExpEquation of the job
     */
    public Parser getMaxExpEquation(){
        return maxExpEquation;
    }
    
    /**
     * Function to return the appropriate max exp for this level
     * @param level - current level
     * @return the correct max exp for this level
     */
    public double getMaxExp(Map<String, Double> param) {
        for (Map.Entry<String, Double> temp: param.entrySet()) {
            maxExpEquation.setVariable(temp.getKey(), temp.getValue());
        }
        return maxExpEquation.getValue();        
    }

    /**
     * Function to get the display method
     * @return the display method
     */
    public DisplayMethod getDisplayMethod() {
        return displayMethod;
    }
    
    /**
     * Function to return the maximum level
     * @return the max level
     * @return null - no max level
     */
    public int getMaxLevel() {
        return maxLevel;
    }
    
    /**
     * Function to return the maximum slots
     * @return the max slots
     * @return null - no max slots
     */
    public Integer getMaxSlots(){
        return maxSlots;
    }
    
    /**
     * Get the permission nodes for this job
     * @return Permissions for this job
     */
    public List<JobPermission> getPermissions() {
        return Collections.unmodifiableList(jobPermissions);
    }
}
