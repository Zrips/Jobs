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
import java.util.Map;

import com.gamingmesh.jobs.Jobs;

public enum CurrencyType {
    MONEY("Money", 1),
    EXP("Exp", 2),
    POINTS("Points", 3);

    private String name;
    private int id = 0;
    private boolean enabled = true;

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

    public String getDisplayName() {
        return Jobs.getLanguage().getMessage("general.info.paymentType." + this.toString());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static HashMap<CurrencyType, Double> generate(Double money, Double exp, Double points) {
        HashMap<CurrencyType, Double> amounts = new HashMap<>();
        if (money != null)
            amounts.put(CurrencyType.MONEY, money);
        if (exp != null)
            amounts.put(CurrencyType.EXP, exp);
        if (points != null)
            amounts.put(CurrencyType.POINTS, points);
        return amounts;
    }
}
