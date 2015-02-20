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

import com.gamingmesh.jobs.util.ChatColor;

/**
 * Container class for titles
 * @author Alex
 *
 */
public class Title {
    private String name = null;
    private String shortName = null;
    private ChatColor color = null;
    private int levelReq = 0;
    
    /**
     * Constructor
     * @param name - The long name of the title
     * @param shortName - the short name of the title
     * @param color - the ChatColor of the title
     * @param levelReq -  the level requirement of the title
     */
    public Title(String name, String shortName, ChatColor color, int levelReq){
        this.name = name;
        this.color = color;
        this.levelReq = levelReq;
        this.shortName = shortName;
    }
    
    /**
     * Function to return the long name of the title
     * @return the long name of the title
     */
    public String getName(){
        return name;
    }
    
    /**
     * Function to get the ChatColor of the title
     * @return the chat colour o the title
     */
    public ChatColor getChatColor(){
        return color;
    }
    
    /**
     * Function to get the levelRequirement of the title
     * @return the level requirement for the title
     */
    public int getLevelReq(){
        return levelReq;
    }
    
    /**
     * Function to get the short name of the title
     * @return the short name of the title
     */
    public String getShortName(){
        return shortName;
    }
}
