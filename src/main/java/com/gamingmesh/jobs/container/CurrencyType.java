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

public enum CurrencyType {
    MONEY("Money", 1),
    EXP("Exp", 2),
    POINTS("Points", 3);

    private String name;
    private int id = 0;

    CurrencyType(String name, int id) {
	this.name = name;
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public static CurrencyType getByName(String name) {
	for (CurrencyType one : values()) {
	    if (one.getName().equalsIgnoreCase(name))
		return one;
	}
	return null;
    }

    public static CurrencyType get(int id) {
	for (CurrencyType one : values()) {
	    if (one.getId() == id)
		return one;
	}
	return null;
    }

    public int getId() {
	return id;
    }
}
