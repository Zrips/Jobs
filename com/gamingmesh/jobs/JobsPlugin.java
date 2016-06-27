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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import com.gamingmesh.jobs.listeners.JobsListener;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.gamingmesh.jobs.listeners.McMMOlistener;
import com.gamingmesh.jobs.listeners.MythicMobsListener;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.TabComplete;
import com.gamingmesh.jobs.config.YmlMaker;

public class JobsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

	String packageName = getServer().getClass().getPackage().getName();
	String[] packageSplit = packageName.split("\\.");
	String version = packageSplit[packageSplit.length - 1].substring(0, packageSplit[packageSplit.length - 1].length() - 3);
	try {
	    Class<?> nmsClass;
	    nmsClass = Class.forName("com.gamingmesh.jobs.nmsUtil." + version);
	    if (NMS.class.isAssignableFrom(nmsClass)) {
		Jobs.setNms((NMS) nmsClass.getConstructor().newInstance());
	    } else {
		System.out.println("Something went wrong, please note down version and contact author v:" + version);
		this.setEnabled(false);
	    }
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
	    | SecurityException e) {
	    System.out.println("Your server version is not compatible with this plugins version! Plugin will be disabled: " + version);
	    this.setEnabled(false);
	    e.printStackTrace();
	    return;
	}

	try {
	    Jobs.setActionBar(new ActionBar());
//	OfflinePlayerList.fillList();
	    YmlMaker jobConfig = new YmlMaker(this, "jobConfig.yml");
	    jobConfig.saveDefaultConfig();

	    YmlMaker jobSigns = new YmlMaker(this, "Signs.yml");
	    jobSigns.saveDefaultConfig();

	    YmlMaker jobSchedule = new YmlMaker(this, "schedule.yml");
	    jobSchedule.saveDefaultConfig();

	    YmlMaker jobShopItems = new YmlMaker(this, "shopItems.yml");
	    jobShopItems.saveDefaultConfig();

	    Jobs.setPermissionHandler(new PermissionHandler(this));

	    Jobs.setPlayerManager(this);

	    Jobs.setScboard(this);
	    Jobs.setLanguage(this);
	    Jobs.setGUIManager();
	    Jobs.setExplore();

	    Jobs.setBBManager(this);

	    Jobs.setPluginLogger(getLogger());

	    Jobs.setDataFolder(getDataFolder());

	    Jobs.setLoging();
	    Jobs.setGCManager(this);
	    Jobs.setConfigManager(this);

	    Jobs.setCommandManager(this);

	    getCommand("jobs").setExecutor(Jobs.getCommandManager());

	    this.getCommand("jobs").setTabCompleter(new TabComplete());

	    Jobs.startup();

	    // register the listeners
	    getServer().getPluginManager().registerEvents(new JobsListener(this), this);
	    getServer().getPluginManager().registerEvents(new JobsPaymentListener(this), this);

	    Jobs.setMcMMOlistener(this);
	    if (Jobs.getMcMMOlistener().CheckmcMMO()) {
		getServer().getPluginManager().registerEvents(new McMMOlistener(this), this);
	    }

	    Jobs.setMythicManager(this);
	    if (Jobs.getMythicManager().Check() && Jobs.getGCManager().MythicMobsEnabled) {
		getServer().getPluginManager().registerEvents(new MythicMobsListener(this), this);
	    }

	    Jobs.setPistonProtectionListener(this);
	    if (Jobs.getGCManager().useBlockProtection) {
		getServer().getPluginManager().registerEvents(Jobs.getPistonProtectionListener(), this);
	    }

	    // register economy
	    Bukkit.getScheduler().runTask(this, new HookEconomyTask(this));

	    // all loaded properly.

	    Jobs.getScheduleManager().DateUpdater();

	    String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] Plugin has been enabled succesfully.");
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    console.sendMessage(message);
	    Jobs.getLanguage().reload();

	    Jobs.getJobsDAO().loadExplore();

	    Jobs.getCommandManager().fillCommands();
	} catch (IOException e) {
	    System.out.println("There was some issues when starting plugin. Please contact dev about this. Plugin will be disabled.");
	    this.setEnabled(false);
	    e.printStackTrace();
	}
    }

    @Override
    public void onDisable() {
	Jobs.getGUIManager().CloseInventories();
	Jobs.getShopManager().CloseInventories();
	Jobs.getJobsDAO().saveExplore();
	Jobs.shutdown();
	String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &2Plugin has been disabled succesfully.");
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	console.sendMessage(message);
    }
}
