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
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.economy.Economy;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;
import com.gamingmesh.jobs.util.JobsClassLoader;

public class Jobs {
    private static Jobs instance = new Jobs();
    private static PlayerManager pManager = new PlayerManager();
    
    private static Logger pLogger;
    private static File dataFolder;
    private static JobsClassLoader classLoader = new JobsClassLoader(instance);
    private static JobsDAO dao = null;
    private static List<Job> jobs = null;
    private static Job noneJob = null;
    private static WeakHashMap<Job, Integer> usedSlots = new WeakHashMap<Job, Integer>();
    private static BufferedEconomy economy;
    private static PermissionHandler permissionHandler;

    private static BufferedPaymentThread paymentThread = null;
    private static DatabaseSaveThread saveTask = null;
    
    private Jobs() {}
    
    /**
     * Returns player manager
     * @return the player manager
     */
    public static PlayerManager getPlayerManager() {
        return pManager;
    }
    
    /**
     * Sets the plugin logger
     */
    public static void setPluginLogger(Logger logger) {
        pLogger = logger;
    }

    /**
     * Retrieves the plugin logger
     * @return the plugin logger
     */
    public static Logger getPluginLogger() {
        return pLogger;
    }
    
    /**
     * Sets the data folder
     * @param dir - the data folder
     */
    public static void setDataFolder(File dir) {
        dataFolder = dir;
    }
    
    /**
     * Retrieves the data folder
     * @return data folder
     */
    public static File getDataFolder() {
        return dataFolder;
    }
    
    /**
     * Sets the Data Access Object
     * @param dao - the DAO
     */
    public static void setDAO(JobsDAO value) {
        dao = value;
    }
    
    /**
     * Get the Data Access Object
     * @return the DAO
     */
    public static JobsDAO getJobsDAO() {
        return dao;
    }
    
    /**
     * Sets the list of jobs
     * @param jobs - list of jobs
     */
    public static void setJobs(List<Job> list) {
        jobs = list;
    }
    
    /**
     * Retrieves the list of active jobs
     * @return list of jobs
     */
    public static List<Job> getJobs() {
        return Collections.unmodifiableList(jobs);
    }
    
    /**
     * Sets the none job
     * @param noneJob - the none job
     */
    public static void setNoneJob(Job job) {
        noneJob = job;
    }
    
    /**
     * Retrieves the "none" job
     * @return the none job
     */
    public static Job getNoneJob() {
        return noneJob;
    }
    
    /**
     * Function to return the job information that matches the jobName given
     * @param jobName - the ame of the job given
     * @return the job that matches the name
     */
    public static Job getJob(String jobName) {
        for (Job job : jobs) {
            if (job.getName().equalsIgnoreCase(jobName))
                return job;
        }
        return null;
    }
    
    /**
     * Executes startup
     */
    public static void startup() {
        reload();
        
        // add all online players
        for (Player online: Bukkit.getServer().getOnlinePlayers()){
            Jobs.getPlayerManager().playerJoin(online);
        }
    }
    
    /**
     * Reloads all data
     */
    public static void reload() {
        if (saveTask != null) {
            saveTask.shutdown();
            saveTask = null;
        }
        
        if (paymentThread != null) {
            paymentThread.shutdown();
            paymentThread = null;
        }
        
        if (dao != null) {
            dao.closeConnections();
        }
        
        ConfigManager.getJobsConfiguration().reload();
        Language.reload(ConfigManager.getJobsConfiguration().getLocale());
        ConfigManager.getJobConfig().reload();
        usedSlots.clear();
        for (Job job: jobs) {
            usedSlots.put(job, getJobsDAO().getSlotsTaken(job));
        }
        pManager.reload();
        permissionHandler.registerPermissions();
        
        // set the system to auto save
        if (ConfigManager.getJobsConfiguration().getSavePeriod() > 0) {
            saveTask = new DatabaseSaveThread(ConfigManager.getJobsConfiguration().getSavePeriod());
            saveTask.start();
        }
        
        // schedule payouts to buffered payments
        paymentThread = new BufferedPaymentThread(ConfigManager.getJobsConfiguration().getEconomyBatchDelay());
        paymentThread.start();
    }
    
    /**
     * Executes clean shutdown
     */
    public static void shutdown() {
        if (saveTask != null)
            saveTask.shutdown();
        
        if (paymentThread != null)
            paymentThread.shutdown();
        
        pManager.saveAll();
        
        if (dao != null) {
            dao.closeConnections();
        }
    }
    
    /**
     * Function to get the number of slots used on the server for this job
     * @param job - the job
     * @return the number of slots
     */
    public static int getUsedSlots(Job job){
        return usedSlots.get(job);
    }
    
    /**
     * Function to increase the number of used slots for a job
     * @param job - the job someone is taking
     */
    public static void takeSlot(Job job) {
        usedSlots.put(job, usedSlots.get(job)+1);
    }
    
    /**
     * Function to decrease the number of used slots for a job
     * @param job - the job someone is leaving
     */
    public static void leaveSlot(Job job) {
        usedSlots.put(job, usedSlots.get(job)-1);
    }
    
    /**
     * Returns the jobs classloader
     * @return the classloader
     */
    public static JobsClassLoader getJobsClassloader() {
        return classLoader;
    }
    
    /**
     * Sets the permission handler
     * @param h - the permission handler
     */
    public static void setPermissionHandler(PermissionHandler h) {
        permissionHandler = h;
    }
    
    /**
     * Gets the permission handler
     * @return the permission handler
     */
    public static PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }
    
    /**
     * Sets the economy handler
     * @param eco - the economy handler
     */
    public static void setEconomy(JobsPlugin plugin, Economy eco) {
        economy = new BufferedEconomy(plugin, eco);
    }
    
    /**
     * Gets the economy handler
     * @return the economy handler
     */
    public static BufferedEconomy getEconomy() {
        return economy;
    }
    
    /**
     * Performed an action
     * 
     * Give correct experience and income
     * @param jPlayer - the player
     * @param action - the action
     * @param multiplier - the payment/xp multiplier
     */
    public static void action(JobsPlayer jPlayer, ActionInfo info, double multiplier) {
        List<JobProgression> progression = jPlayer.getJobProgression();
        int numjobs = progression.size();
        // no job
        if (numjobs == 0) {
            Job jobNone = Jobs.getNoneJob();
            if (jobNone != null) {
                Double income = jobNone.getIncome(info, 1, numjobs);
                if (income != null)
                    Jobs.getEconomy().pay(jPlayer, income*multiplier);
            }
        } else {
            for (JobProgression prog : progression) {
                int level = prog.getLevel();
                Double income = prog.getJob().getIncome(info, level, numjobs);
                if (income != null) {
                    Double exp = prog.getJob().getExperience(info, level, numjobs);
                    if (ConfigManager.getJobsConfiguration().addXpPlayer()) {
                        Player player = Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID());
                        if (player != null) {
                            /*
                             * Minecraft experience is calculated in whole numbers only.
                             * Calculate the fraction of an experience point and perform a dice roll.
                             * That way jobs that give fractions of experience points will slowly give
                             * experience in the aggregate
                             */
                            int expInt = exp.intValue();
                            double remainder = exp.doubleValue() - expInt;
                            if (Math.abs(remainder) > Math.random()) {
                                if (exp.doubleValue() < 0) {
                                    expInt--;
                                } else {
                                    expInt++;
                                }
                            }
                            player.giveExp(expInt);
                        }
                    }
                    // give income
                    Jobs.getEconomy().pay(jPlayer, income*multiplier);
                    int oldLevel = prog.getLevel();
                    if (prog.addExperience(exp*multiplier))
                        Jobs.getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
                }
            }
        }
    }
}
