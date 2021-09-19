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
import java.util.List;

public class JobCommands {

    private final String node;
    private final int levelFrom;
    private final int levelUntil;

    private final List<String> commands = new ArrayList<>();

    public JobCommands(String node, List<String> commands, int levelFrom, int levelUntil) {
	this.node = node;
	this.commands.addAll(commands);
	this.levelFrom = levelFrom;
	this.levelUntil = levelUntil;
    }

    public String getNode() {
	return node;
    }

    public List<String> getCommands() {
	return commands;
    }

    public int getLevelFrom() {
	return levelFrom;
    }

    public int getLevelUntil() {
	return levelUntil;
    }
}
