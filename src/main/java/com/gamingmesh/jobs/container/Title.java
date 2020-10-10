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

import org.bukkit.ChatColor;

import com.gamingmesh.jobs.CMILib.CMIChatColor;

/**
 * Container class for titles
 * @author Alex
 *
 */
public class Title {

    private String name = "", shortName = "", jobName = "";
    private CMIChatColor color = CMIChatColor.WHITE;
    private int levelReq = 0;

    /**
     * Constructor
     * @param name - The long name of the title
     * @param shortName - the short name of the title
     * @param color - the ChatColor of the title
     * @param levelReq -  the level requirement of the title
     * @param jobName -  Job this title is made for
     */
    @Deprecated
    public Title(String name, String shortName, ChatColor color, int levelReq, String jobName){
        this(jobName, jobName, CMIChatColor.WHITE, levelReq, jobName);
    }

    /**
     * @param name The long name of the title
     * @param shortName the short name of the title
     * @param color {@link CMIChatColor}
     * @param levelReq the level requirement of the title
     * @param jobName Job this title is made for
     */
    public Title(String name, String shortName, CMIChatColor color, int levelReq, String jobName) {
	this.name = name == null ? "" : name;
	this.color = color == null ? CMIChatColor.BLACK : color;
	this.levelReq = levelReq;
	this.shortName = shortName == null ? "" : shortName;
	this.jobName = jobName == null ? "" : jobName;
    }

    /**
     * Function to return the long name of the title
     * @return the long name of the title
     */
    public String getName() {
        return name;
    }

    /**
     * Function to return the job name of the title
     * @return the job name of the title
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Returns the color of the title
     * @return {@link CMIChatColor}
     */
    public CMIChatColor getChatColor() {
	return color;
    }

    /**
     * Function to get the levelRequirement of the title
     * @return the level requirement for the title
     */
    public int getLevelReq() {
        return levelReq;
    }

    /**
     * Function to get the short name of the title
     * @return the short name of the title
     */
    public String getShortName() {
        return shortName;
    }
}
