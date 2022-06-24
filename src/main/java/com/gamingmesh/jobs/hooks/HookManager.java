package com.gamingmesh.jobs.hooks;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.hooks.McMMO.McMMO1_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMO2_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMOManager;
import com.gamingmesh.jobs.hooks.MyPet.MyPetManager;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobInterface;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs4;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs5;
import com.gamingmesh.jobs.hooks.WorldGuard.WorldGuardManager;
import com.gamingmesh.jobs.hooks.stackMob.StackMobHandler;
import com.gamingmesh.jobs.hooks.wildStacker.WildStackerHandler;

public class HookManager {

    private static McMMOManager McMMOManager;
    private static MythicMobInterface MythicManager;
    private static MyPetManager myPetManager;
    private static WorldGuardManager worldGuardManager;
    private static StackMobHandler stackMobHandler;
    private static WildStackerHandler wildStackerHandler;

    private static final Jobs PLUGIN = JavaPlugin.getPlugin(Jobs.class);

    private static PluginManager pm;

    public static void loadHooks() {
	pm = PLUGIN.getServer().getPluginManager();

	setMyPetManager();
	setWorldGuard();
	setMythicManager();
	setStackMobHandler();
	setWildStackerHandler();
    }

    public static StackMobHandler getStackMobHandler() {
	if (stackMobHandler == null) {
	    setStackMobHandler();
	}

	return stackMobHandler;
    }

    public static WildStackerHandler getWildStackerHandler() {
	if (wildStackerHandler == null) {
	    setWildStackerHandler();
	}

	return wildStackerHandler;
    }

    public static MyPetManager getMyPetManager() {
	if (myPetManager == null) {
	    setMyPetManager();
	}

	return myPetManager;
    }

    public static WorldGuardManager getWorldGuardManager() {
	if (worldGuardManager == null) {
	    worldGuardManager = new WorldGuardManager();
	}

	return worldGuardManager;
    }

    public static McMMOManager getMcMMOManager() {
	if (McMMOManager == null)
	    McMMOManager = new McMMOManager();

	return McMMOManager;
    }

    public static MythicMobInterface getMythicManager() {
	return MythicManager;
    }

    public static boolean checkMythicMobs() {
	return Jobs.getGCManager().MythicMobsEnabled && MythicManager != null && MythicManager.check();
    }

    private static boolean setWorldGuard() {
	if (JobsHook.WorldGuard.isEnabled()) {
	    worldGuardManager = new WorldGuardManager();
	    Jobs.consoleMsg("&eWorldGuard detected.");
	    return true;
	}

	return false;
    }

    private static void setMythicManager() {
	if (!JobsHook.MythicMobs.isPresent())
	    return;

	try {
	    Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper");
	    MythicManager = new MythicMobs4(PLUGIN);
	    Jobs.consoleMsg("&eMythicMobs 4.x detected.");
	} catch (ClassNotFoundException ex) {
	    try {
		Class.forName("io.lumine.mythic.bukkit.BukkitAPIHelper");
		MythicManager = new MythicMobs5(PLUGIN);
		Jobs.consoleMsg("&eMythicMobs 5.x detected.");
	    } catch (ClassNotFoundException e) {
		Jobs.consoleMsg("&cYour MythicMobs version is not supported by Jobs! Supported versions: 4.9.1+");
	    }
	}
    }

    public static void setMcMMOlistener() {
	try {
	    Class.forName("com.gmail.nossr50.datatypes.skills.SuperAbilityType");
	    pm.registerEvents(new McMMO2_X_listener(), PLUGIN);
	    Jobs.consoleMsg("&eRegistered McMMO 2.x listener");
	} catch (ClassNotFoundException e) {
	    pm.registerEvents(new McMMO1_X_listener(), PLUGIN);
	    Jobs.consoleMsg("&eRegistered McMMO 1.x listener");
	}
    }

    private static void setMyPetManager() {
	if (JobsHook.MyPet.isEnabled()) {
	    myPetManager = new MyPetManager();
	    Jobs.consoleMsg("&eMyPet detected.");
	}
    }

    private static void setStackMobHandler() {
	if (JobsHook.StackMob.isEnabled()) {
	    stackMobHandler = new StackMobHandler();
	}
    }

    private static void setWildStackerHandler() {
	if (JobsHook.WildStacker.isEnabled()) {
	    wildStackerHandler = new WildStackerHandler();
	}
    }
}
