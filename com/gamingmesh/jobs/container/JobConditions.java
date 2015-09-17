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

import java.util.List;

public class JobConditions {
    private String node;
    private List<String> requires;
    private List<String> perform;

    public JobConditions(String node, List<String> requires, List<String> perform) {
	this.node = node;
	this.requires = requires;
	this.perform = perform;
    }

    public String getNode() {
	return node;
    }

    public List<String> getRequires() {
	return requires;
    }

    public List<String> getPerform() {
	return perform;
    }
}
