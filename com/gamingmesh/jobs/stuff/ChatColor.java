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

package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map;

public enum ChatColor {
	BLACK('0', 0), 
	DARK_BLUE('1', 1), 
	DARK_GREEN('2', 2), 
	DARK_AQUA('3', 3), 
	DARK_RED('4', 4), 
	DARK_PURPLE('5', 5), 
	GOLD('6', 6), 
	GRAY('7', 7), 
	DARK_GRAY('8', 8), 
	BLUE('9', 9), 
	GREEN('a', 10), 
	AQUA('b', 11), 
	RED('c', 12), 
	LIGHT_PURPLE('d', 13), 
	YELLOW('e', 14), 
	WHITE('f', 15);

	private static final char COLOR_CHAR = '\u00A7';
	private final char code;
	private final int intCode;
	private final String toString;
	private final static Map<Integer, ChatColor> intMap = new HashMap<Integer, ChatColor>();
	private final static Map<Character, ChatColor> charMap = new HashMap<Character, ChatColor>();
	private final static Map<String, ChatColor> stringMap = new HashMap<String, ChatColor>();

	private ChatColor(char code, int intCode) {
		this.code = code;
		this.intCode = intCode;
		this.toString = new String(new char[] { COLOR_CHAR, code });
	}

	public char getChar() {
		return code;
	}

	@Override
	public String toString() {
		return toString;
	}

	public static ChatColor matchColor(char code) {
		return charMap.get(code);
	}

	public static ChatColor matchColor(int code) {
		return intMap.get(code);
	}

	public static ChatColor matchColor(String name) {
		return stringMap.get(name.toLowerCase());
	}

	static {
		for (ChatColor color : values()) {
			intMap.put(color.intCode, color);
			charMap.put(color.code, color);
			stringMap.put(color.name().toLowerCase(), color);
		}
	}
}
