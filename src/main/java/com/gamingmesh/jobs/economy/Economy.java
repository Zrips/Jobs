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

package com.gamingmesh.jobs.economy;

import org.bukkit.OfflinePlayer;

public interface Economy {
    boolean depositPlayer(OfflinePlayer offlinePlayer, double money);

    boolean withdrawPlayer(OfflinePlayer offlinePlayer, double money);

    String format(double money);

    boolean hasMoney(OfflinePlayer offlinePlayer, double money);

    boolean hasMoney(String PlayerName, double money);

    boolean withdrawPlayer(String PlayerName, double money);

    boolean depositPlayer(String PlayerName, double money);
}
