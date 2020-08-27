package com.gamingmesh.jobs.hooks;

import org.bukkit.plugin.PluginManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.hooks.Boss.BossManager;
import com.gamingmesh.jobs.hooks.McMMO.McMMO1_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMO2_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMOManager;
import com.gamingmesh.jobs.hooks.MyPet.MyPetManager;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobInterface;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs2;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs4;
import com.gamingmesh.jobs.hooks.WorldGuard.WorldGuardManager;

public class HookManager {

    private static McMMOManager McMMOManager = null;
    private static MythicMobInterface MythicManager = null;
    private static MyPetManager myPetManager = null;
    private static WorldGuardManager worldGuardManager = null;
    private static BossManager bossManager = null;

    private static PluginManager pm = null;

    public static void loadHooks() {
	pm = Jobs.getInstance().getServer().getPluginManager();

	if (getMcMMOManager().CheckmcMMO())
	    setMcMMOlistener();

	setMyPetManager();
	setWorldGuard();
	setMythicManager();
	setBossManager();

	if (checkMythicMobs())
	    MythicManager.registerListener();
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

    public static BossManager getBossManager() {
	if (bossManager == null)
	    setBossManager();

	return bossManager;
    }

	public static MythicMobInterface getMythicManager() {
	return MythicManager;
    }

    public static boolean checkMythicMobs() {
	return Jobs.getGCManager().MythicMobsEnabled && MythicManager != null && MythicManager.Check();
    }

    private static boolean setWorldGuard() {
	if (pm.getPlugin("WorldGuard") != null && pm.isPluginEnabled("WorldGuard")) {
	    worldGuardManager = new WorldGuardManager();
	    Jobs.consoleMsg("&e[Jobs] WorldGuard detected.");
	    return true;
	}

	return false;
    }

    private static void setMythicManager() {
	if (pm.getPlugin("MythicMobs") == null)
	    return;

	try {
	    Class.forName("net.elseland.xikage.MythicMobs.API.MythicMobsAPI");
	    MythicManager = new MythicMobs2(Jobs.getInstance());
	} catch (ClassNotFoundException e) {
	    try {
		Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper");
		MythicManager = new MythicMobs4(Jobs.getInstance());
	    } catch (ClassNotFoundException ex) {
	    }
	}

	if (MythicManager == null) {
	    Jobs.consoleMsg("&cYour MythicMobs version is not supported by Jobs! Supported versions: 2.4.5+, 4.6.5+");
	    return;
	}

	Jobs.consoleMsg("&e[Jobs] MythicMobs detected.");
    }

    public static void setMcMMOlistener() {
	Jobs ins = Jobs.getInstance();
	try {
	    Class.forName("com.gmail.nossr50.datatypes.skills.SuperAbilityType");
	    pm.registerEvents(new McMMO2_X_listener(ins), ins);
	    Jobs.consoleMsg("&e[Jobs] Registered McMMO 2.x listener");
	} catch (ClassNotFoundException e) {
	    pm.registerEvents(new McMMO1_X_listener(ins), ins);
	    Jobs.consoleMsg("&e[Jobs] Registered McMMO 1.x listener");
	}
    }

    private static void setMyPetManager() {
	if (pm.getPlugin("MyPet") != null && pm.isPluginEnabled("MyPet")) {
	    myPetManager = new MyPetManager();
	    Jobs.consoleMsg("&e[Jobs] MyPet detected.");
	}
    }

    private static void setBossManager() {
	if (pm.getPlugin("Boss") != null && pm.isPluginEnabled("Boss")) {
	    bossManager = new BossManager();
	    Jobs.consoleMsg("&e[Jobs] Boss detected.");
	}
    }
}
