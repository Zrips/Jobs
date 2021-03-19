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

public enum DisplayMethod {
    NONE("none", "Nothing"),
    FULL("full", "Full title and job name"),
    JOB("job", "Full job name"),
    TITLE("title", "Full title"),
    SHORT_FULL("shortfull", "Short title and job name"),
    SHORT_JOB("shortjob", "Short job name"),
    SHORT_TITLE("shorttitle", "Short title"),
    SHORT_TITLE_JOB("shorttitlejob", "Short title and full job name"),
    TITLE_SHORT_JOB("titleshortjob", "Full title and short job name");

    private String name;
    private String desc = "";

    DisplayMethod(String name, String desc) {
	this.name = name;
	this.desc = desc;
    }

    public String getName() {
	return name;
    }

    public static DisplayMethod matchMethod(String name) {
	for (DisplayMethod method : DisplayMethod.values()) {
	    if (method.getName().equalsIgnoreCase(name))
		return method;
	}
	return null;
    }

    public String getDesc() {
	return desc;
    }
}