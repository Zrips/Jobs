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
import java.util.Map;

public class JobConditions {

    private String node;

    private final List<String> requiresPerm = new ArrayList<>();
    private final Map<String, Integer> requiresJobs = new HashMap<>();
    private final Map<String, Boolean> performPerm = new HashMap<>();

    public JobConditions(String node, List<String> requires, List<String> perform) {
	this.node = node;

	for (String one : requires) {
	    String cond = one.toLowerCase();

	    if (cond.contains("j:")) {
		String[] split = cond.replace("j:", "").split("-", 2);

		int jobLevel = 0;
		try {
		    jobLevel = Integer.valueOf(split[1]);
		} catch (NumberFormatException e) {
		    continue;
		}

		requiresJobs.put(split[0], jobLevel);
	    }

	    if (cond.contains("p:")) {
		requiresPerm.add(one.replace("p:", ""));
	    }
	}
	for (String one : perform) {
	    one = one.toLowerCase();

	    if (!one.contains("p:"))
		continue;

	    String clean = one.substring("p:".length());
	    if (clean.contains("-")) {
		String[] split = clean.split("-", 2);
		performPerm.put(split[0], split[1].equalsIgnoreCase("true"));
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

    public Map<String, Integer> getRequiredJobs() {
	return requiresJobs;
    }

    public Map<String, Boolean> getPerformPerm() {
	return performPerm;
    }
}
