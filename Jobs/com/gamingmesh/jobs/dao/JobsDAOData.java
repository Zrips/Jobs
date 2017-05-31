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

package com.gamingmesh.jobs.dao;

import java.util.UUID;

/**
 * Container class to hold information out of the database.
 * 
 * Holds job name
 * Experience in the job
 * Level in the job
 * @author Alex
 *
 */
public class JobsDAOData {
    private UUID playerUUID;
    private String job;
    private int level;
    private int experience;
    
    /**
     * Constructor class for the DAO side of things.
     * @param job - the name of the job
     * @param level - the level of the job
     * @param experience - the experience of the job
     */
    public JobsDAOData(UUID playerUUID, String job, int level, int experience) {
        this.playerUUID = playerUUID;
        this.job = job;
        this.level = level;
        this.experience = experience;
    }
    
    /**
     * Getter function for the playerUUID
     * @return the job name
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    /**
     * Getter function for the job name
     * @return the job name
     */
    public String getJobName() {
        return job;
    }
    
    /**
     * Getter function for the level
     * @return the level in the job
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Getter function for the experience.
     * @return the experience in the job
     */
    public double getExperience() {
        return (double) experience;
    }
}
