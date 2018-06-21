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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobConditions {
    private String node;
    private List<String> requiresPerm = new ArrayList<String>();
    private HashMap<String, Integer> requiresJobs = new HashMap<String, Integer>();
    private HashMap<String, Boolean> performPerm = new HashMap<String, Boolean>();

    public JobConditions(String node, List<String> requires, List<String> perform) {
	this.node = node;

	for (String one : requires) {
	    if (one.toLowerCase().contains("j:")) {
		String jobName = one.toLowerCase().replace("j:", "").split("-")[0];
		int jobLevel = 0;
		try {
		    jobLevel = Integer.valueOf(one.toLowerCase().replace("j:", "").split("-")[1]);
		} catch (Exception e) {
		    continue;
		}
		requiresJobs.put(jobName, jobLevel);
	    }
	    if (one.toLowerCase().contains("p:")) {
		requiresPerm.add(one.replace("p:", ""));
	    }
	}
	for (String one : perform) {
	    if (!one.toLowerCase().contains("p:"))
		continue;
	    String clean = one.toLowerCase().substring("p:".length());
	    if (clean.contains("-")) {
		String perm = clean.split("-")[0];
		boolean n = clean.split("-")[1].equalsIgnoreCase("true") ? true : false;
		performPerm.put(perm, n);
	    } else {
		performPerm.put(clean, true);
	    }
	}
    }

    public String getNode() {
	return node;
    }

    public List<String> getRequiredPerm() {
	return requiresPerm;
    }

    public HashMap<String, Integer> getRequiredJobs() {
	return requiresJobs;
    }

    public HashMap<String, Boolean> getPerformPerm() {
	return performPerm;
    }
}
