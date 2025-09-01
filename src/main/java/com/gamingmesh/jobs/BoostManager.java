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

package com.gamingmesh.jobs;

import java.io.File;
import java.util.Set;

import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;

import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;

public final class BoostManager {

    private static final String boostFile = "activeBoosts.yml";

    private BoostManager() {
    }

    public static void saveBoosts() {
        if (!Jobs.getGCManager().isBoostPersistenceEnabled())
            return;

        ConfigReader cfg;
        try {
            cfg = new ConfigReader(Jobs.getInstance(), boostFile);
        } catch (Exception e) {
            Jobs.getPluginLogger().severe("Failed to create boost config: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Clear existing data
        cfg.getC().getKeys(false).forEach(key -> cfg.getC().set(key, null));

        int savedBoosts = 0;
        for (Job job : Jobs.getJobs()) {
            BoostMultiplier boost = job.getBoost();
            if (boost == null)
                continue;

            for (CurrencyType type : CurrencyType.values()) {
                double amount = boost.get(type);
                Long time = boost.getTime(type);

                // Only save active boosts (non-zero amount or with time)
                if (amount != 0 || time != null) {
                    String path = job.getName() + "." + type.toString().toLowerCase();
                    cfg.set(path + ".amount", amount);
                    if (time != null) {
                        cfg.set(path + ".expireTime", time);
                    }
                    savedBoosts++;
                }
            }
        }

        try {
            cfg.save();
            if (savedBoosts > 0) {
                CMIMessages.consoleMessage("&e[Jobs] Saved " + savedBoosts + " active boosts");
            }
        } catch (Exception e) {
            Jobs.getPluginLogger().severe("Failed to save boosts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadBoosts() {
        if (!Jobs.getGCManager().isBoostPersistenceEnabled())
            return;

        File file = new File(Jobs.getInstance().getDataFolder(), boostFile);
        if (!file.exists())
            return; // No boosts file exists yet

        ConfigReader cfg;
        try {
            cfg = new ConfigReader(Jobs.getInstance(), boostFile);
        } catch (Exception e) {
            Jobs.getPluginLogger().severe("Failed to load boost config: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        int loadedBoosts = 0;
        int expiredBoosts = 0;

        Set<String> jobNames = cfg.getC().getKeys(false);
        for (String jobName : jobNames) {
            Job job = Jobs.getJob(jobName);
            if (job == null) {
                Jobs.getPluginLogger().warning("Job '" + jobName + "' not found, skipping boosts");
                continue;
            }

            Set<String> currencies = cfg.getC().getConfigurationSection(jobName).getKeys(false);
            for (String currencyName : currencies) {
                CurrencyType type = CurrencyType.getByName(currencyName);
                if (type == null) {
                    Jobs.getPluginLogger().warning("Unknown currency type '" + currencyName + "', skipping");
                    continue;
                }

                String basePath = jobName + "." + currencyName;
                double amount = cfg.get(basePath + ".amount", 0.0);
                Long expireTime = cfg.getC().isLong(basePath + ".expireTime")
                        ? cfg.getC().getLong(basePath + ".expireTime")
                        : null;

                // Check if boost has expired
                if (expireTime != null && expireTime < System.currentTimeMillis()) {
                    expiredBoosts++;
                    continue; // Skip expired boosts
                }

                // Apply the boost
                if (expireTime != null) {
                    job.addBoost(type, amount, (expireTime - System.currentTimeMillis()) / 1000L);
                } else {
                    job.addBoost(type, amount);
                }
                loadedBoosts++;
            }
        }

        if (loadedBoosts > 0 || expiredBoosts > 0) {
            CMIMessages.consoleMessage("&e[Jobs] Loaded " + loadedBoosts + " active boosts" +
                    (expiredBoosts > 0 ? " (skipped " + expiredBoosts + " expired)" : ""));
        }

        // Clean up expired boosts by saving again
        if (expiredBoosts > 0)
            saveBoosts();
    }

    public static void onBoostAdded() {
        if (!Jobs.getGCManager().isBoostPersistenceEnabled())
            return;

        saveBoosts();
    }
}
