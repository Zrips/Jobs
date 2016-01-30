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
import net.coreprotect.CoreProtectAPI;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.MythicMobsAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import com.gamingmesh.jobs.Gui.GuiTools;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.config.JobConfig;
import com.gamingmesh.jobs.config.JobsConfiguration;
import com.gamingmesh.jobs.listeners.JobsListener;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.gamingmesh.jobs.listeners.McMMOlistener;
import com.gamingmesh.jobs.listeners.MythicMobsListener;
import com.gamingmesh.jobs.listeners.PistonProtectionListener;
import com.gamingmesh.jobs.stuff.OfflinePlayerList;
import com.gamingmesh.jobs.stuff.TabComplete;
import com.gamingmesh.jobs.config.YmlMaker;

public class JobsPlugin extends JavaPlugin {
    public static CoreProtectAPI CPAPI;
    public static MythicMobsAPI MMAPI;
    public static boolean CPPresent = false;
    private static NMS nms;

    public static NMS getNms() {
	return nms;
    }

    @Override
    public void onEnable() {

	String packageName = getServer().getClass().getPackage().getName();

	String[] packageSplit = packageName.split("\\.");
	String version = packageSplit[packageSplit.length - 1].split("(?<=\\G.{4})")[0];
	try {
	    Class<?> nmsClass;
	    nmsClass = Class.forName("com.gamingmesh.jobs.nmsUtil." + version);
	    if (NMS.class.isAssignableFrom(nmsClass)) {
		nms = (NMS) nmsClass.getConstructor().newInstance();
	    } else {
		System.out.println("Something went wrong, please note down version and contact author v:" + version);
		this.setEnabled(false);
	    }
	} catch (ClassNotFoundException e) {
	    System.out.println("Your server version is not compatible with this plugins version! Plugin will be disabled: " + version);
	    this.setEnabled(false);
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

	OfflinePlayerList.fillList();
	YmlMaker jobConfig = new YmlMaker(this, "jobConfig.yml");
	jobConfig.saveDefaultConfig();

	YmlMaker jobSigns = new YmlMaker(this, "Signs.yml");
	jobSigns.saveDefaultConfig();

	YmlMaker jobSchedule = new YmlMaker(this, "schedule.yml");
	jobSchedule.saveDefaultConfig();

	Jobs.setPermissionHandler(new PermissionHandler(this));

	Jobs.setSignUtil(this);
	Jobs.setScboard(this);
	Jobs.setSchedule(this);
	Jobs.setLanguage(this);
	Jobs.setExplore();

	Jobs.setPluginLogger(getLogger());

	Jobs.setDataFolder(getDataFolder());

	ConfigManager.registerJobsConfiguration(new JobsConfiguration(this));
	ConfigManager.registerJobConfig(new JobConfig(this));

	getCommand("jobs").setExecutor(new JobsCommands(this));

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

	if (MythicMobsListener.Check() && ConfigManager.getJobsConfiguration().MythicMobsEnabled)
	    getServer().getPluginManager().registerEvents(new MythicMobsListener(this), this);

	if (ConfigManager.getJobsConfiguration().useBlockProtection)
	    getServer().getPluginManager().registerEvents(new PistonProtectionListener(this), this);

	// register economy
	Bukkit.getScheduler().runTask(this, new HookEconomyTask(this));

	if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
	    MMAPI = ((MythicMobs) getServer().getPluginManager().getPlugin("MythicMobs")).getAPI();
	}

	if (getServer().getPluginManager().getPlugin("CoreProtect") != null) {
	    CPPresent = true;
	    CPAPI = ((CoreProtect) getServer().getPluginManager().getPlugin("CoreProtect")).getAPI();
	}

	// all loaded properly.

	if (ConfigManager.getJobsConfiguration().useGlobalBoostScheduler)
	    Jobs.getSchedule().scheduler();
	Jobs.getSchedule().DateUpdater();

	String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &6Plugin has been enabled succesfully.");
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	console.sendMessage(message);
	Jobs.getLanguage().reload(ConfigManager.getJobsConfiguration().getLocale());
	
	Jobs.getJobsDAO().loadExplore();
    }

    @Override
    public void onDisable() {
	GuiTools.CloseInventories();
	Jobs.getJobsDAO().saveExplore();
	Jobs.shutdown();
	String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &2Plugin has been disabled succesfully.");
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	console.sendMessage(message);
    }
}
