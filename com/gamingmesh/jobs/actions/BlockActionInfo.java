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

package com.gamingmesh.jobs.actions;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;

public class BlockActionInfo extends MaterialActionInfo implements ActionInfo {
    public BlockActionInfo(Block block, ActionType type) {
	super(block.getType(), getData(block), type);
    }

    private static byte getData(Block block) {
	@SuppressWarnings("deprecation")
	byte data = block.getData();
	if (block.getType() == Material.COCOA)
	    switch (data) {
	    case 0:
	    case 1:
	    case 2:
	    case 3:
		data = 0;
		break;
	    case 4:
	    case 5:
	    case 6:
	    case 7:
		data = 1;
		break;
	    case 8:
	    case 9:
	    case 10:
	    case 11:
		data = 2;
		break;
	    }
	return data;
    }
}
