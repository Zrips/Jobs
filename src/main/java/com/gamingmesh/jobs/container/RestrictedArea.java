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

import org.bukkit.Location;

public class RestrictedArea {

    private boolean enabled = false;
    private CuboidArea area;

    private HashMap<CurrencyType, Double> multipliers = new HashMap<>();

    private String name;
    private String wgName;

    private HashMap<String, LevelLimits> jobs = new HashMap<>();

    @Deprecated
    public RestrictedArea(String name, CuboidArea area, double multiplier) {
        this.name = name;
        this.area = area;
        for (CurrencyType one : CurrencyType.values()) {
            multipliers.put(one, multiplier);
        }
    }

    @Deprecated
    public RestrictedArea(String name, String wgName, double multiplier) {
        this.name = name;
        this.wgName = wgName;
        for (CurrencyType one : CurrencyType.values()) {
            multipliers.put(one, multiplier);
        }
    }

    public RestrictedArea(String name, CuboidArea area) {
        this.name = name;
        this.area = area;
    }

    public RestrictedArea(String name, String wgName) {
        this.name = name;
        this.wgName = wgName;
    }

    public CuboidArea getCuboidArea() {
        return area;
    }

    /**
     * The multiplier for the restricted area
     * @return - the multiplier for this restricted area
     */
    @Deprecated
    public double getMultiplier() {
        return multipliers.get(CurrencyType.MONEY);
    }

    /**
     * The multipliers for the restricted area
     * @return - the multipliers for this restricted area
     */
    public HashMap<CurrencyType, Double> getMultipliers() {
        return multipliers;
    }

    /**
     * Function check if location is in the restricted area
     * @param loc - the location to checked
     * @return true - the location is inside the restricted area
     * @return false - the location is outside the restricted area
     */
    public boolean inRestrictedArea(Location loc) {
        if (loc == null || area == null)
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

    public String getWgName() {
        return wgName;
    }

    public void setWgName(String wgName) {
        this.wgName = wgName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HashMap<String, LevelLimits> getJobs() {
        return jobs;
    }

    public void setJobs(HashMap<String, LevelLimits> jobs) {
        this.jobs = jobs;
    }

    public boolean validLevelRange(JobProgression prog) {
        if (prog == null)
            return true;

        LevelLimits levelLimit = jobs.get(prog.getJob().getName().toLowerCase());
        if (levelLimit == null) {
            levelLimit = jobs.get("all");
            if (levelLimit == null)
                return false;
        }
        return prog.getLevel() >= levelLimit.getFromLevel() && prog.getLevel() <= levelLimit.getUntilLevel();
    }

}
