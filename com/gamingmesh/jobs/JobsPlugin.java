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
import net.coreprotect.CoreProtect;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import com.gamingmesh.jobs.listeners.JobsListener;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.gamingmesh.jobs.listeners.McMMOlistener;
import com.gamingmesh.jobs.listeners.MythicMobsListener;
import com.gamingmesh.jobs.listeners.PistonProtectionListener;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.TabComplete;
import com.gamingmesh.jobs.config.YmlMaker;

public class JobsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

	String packageName = getServer().getClass().getPackage().getName();

	String[] packageSplit = packageName.split("\\.");
	String version = packageSplit[packageSplit.length - 1].split("(?<=\\G.{4})")[0];
	try {
	    Class<?> nmsClass;
	    nmsClass = Class.forName("com.gamingmesh.jobs.nmsUtil." + version);
	    if (NMS.class.isAssignableFrom(nmsClass)) {
		Jobs.setNms((NMS) nmsClass.getConstructor().newInstance());
	    } else {
		System.out.println("Something went wrong, please note down version and contact author v:" + version);
		this.setEnabled(false);
	    }
	} catch (ClassNotFoundException e) {
	    System.out.println("Your server version is not compatible with this plugins version! Plugin will be disabled: " + version);
	    this.setEnabled(false);
	    return;
	} catch (InstantiationException e) {
	    e.printStackTrace();
	    this.setEnabled(false);
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    this.setEnabled(false);
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	    this.setEnabled(false);
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	    this.setEnabled(false);
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	    this.setEnabled(false);
	} catch (SecurityException e) {
	    e.printStackTrace();
	    this.setEnabled(false);
	}

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

	Jobs.setScboard(this);
	Jobs.setLanguage(this);
	Jobs.setGUIManager(this);
	Jobs.setExplore();

	Jobs.setBBManager(this);

	Jobs.setPluginLogger(getLogger());

	Jobs.setDataFolder(getDataFolder());

	Jobs.setGCManager(this);
	Jobs.setConfigManager(this);

	Jobs.setCommandManager(this);

	getCommand("jobs").setExecutor(Jobs.getCommandManager());

	this.getCommand("jobs").setTabCompleter(new TabComplete());

	try {
	    Jobs.startup();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// register the listeners
	getServer().getPluginManager().registerEvents(new JobsListener(this), this);
	getServer().getPluginManager().registerEvents(new JobsPaymentListener(this), this);

	if (McMMOlistener.CheckmcMMO())
	    getServer().getPluginManager().registerEvents(new McMMOlistener(this), this);

	Jobs.setMythicManager(this);
	if (Jobs.getMythicManager().Check() && Jobs.getGCManager().MythicMobsEnabled) {
	    getServer().getPluginManager().registerEvents(new MythicMobsListener(this), this);
	}

	if (Jobs.getGCManager().useBlockProtection)
	    getServer().getPluginManager().registerEvents(new PistonProtectionListener(this), this);

	// register economy
	Bukkit.getScheduler().runTask(this, new HookEconomyTask(this));

	if (getServer().getPluginManager().getPlugin("CoreProtect") != null) {
	    Jobs.setCoreProtectApi(((CoreProtect) getServer().getPluginManager().getPlugin("CoreProtect")).getAPI());
	}

	// all loaded properly.

	Jobs.getScheduleManager().DateUpdater();

	String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] Plugin has been enabled succesfully.");
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	console.sendMessage(message);
	Jobs.getLanguage().reload(Jobs.getGCManager().getLocale());

	Jobs.getJobsDAO().loadExplore();

	Jobs.getCommandManager().fillCommands();

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
