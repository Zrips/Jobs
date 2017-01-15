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

import org.bukkit.Location;

public class RestrictedArea {

    private CuboidArea area;
    private double multiplier;
    private String name;

    public RestrictedArea(String name, CuboidArea area, double multiplier) {
	this.name = name;
	this.area = area;
	this.multiplier = multiplier;
    }

    public CuboidArea getCuboidArea() {
	return this.area;
    }

    /**
     * The multipler for the restricted area
     * @return - the multipler for this restricted area
     */

    public double getMultiplier() {
	return this.multiplier;
    }

    /**
     * Function check if location is in the restricted area
     * @param loc - the location to checked
     * @return true - the location is inside the restricted area
     * @return false - the location is outside the restricted area
     */
    public boolean inRestrictedArea(Location loc) {
	if (loc == null)
	    return false;
	if (!loc.getWorld().getName().equals(area.getWorld().getName()))
	    return false;
	if (area.getLowLoc().getBlockX() > loc.getBlockX())
	    return false;
	if (area.getHighLoc().getBlockX() < loc.getBlockX())
	    return false;
	if (area.getLowLoc().getBlockZ() > loc.getBlockZ())
	    return false;
	if (area.getHighLoc().getBlockZ() < loc.getBlockZ())
	    return false;
	if (area.getLowLoc().getBlockY() > loc.getBlockY())
	    return false;
	if (area.getHighLoc().getBlockY() < loc.getBlockY())
	    return false;
	return true;
    }

    public String getName() {
	return name;
    }
}
