package com.gamingmesh.jobs.hooks;

import com.gamingmesh.jobs.hooks.CustomFishing.CustomFishingManager;
import com.gamingmesh.jobs.hooks.blockTracker.BlockTrackerManager;
import com.gamingmesh.jobs.hooks.pyroFishingPro.PyroFishingProManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.hooks.McMMO.McMMO1_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMO2_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMOManager;
import com.gamingmesh.jobs.hooks.MyPet.MyPetManager;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobInterface;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs5;
import com.gamingmesh.jobs.hooks.WorldGuard.WorldGuardManager;
import com.gamingmesh.jobs.hooks.stackMob.StackMobHandler;
import com.gamingmesh.jobs.hooks.wildStacker.WildStackerHandler;

import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class HookManager {

    private static McMMOManager McMMOManager;
    private static MythicMobInterface MythicManager;
    private static MyPetManager myPetManager;
    private static WorldGuardManager worldGuardManager;
    private static StackMobHandler stackMobHandler;
    private static WildStackerHandler wildStackerHandler;
    private static BlockTrackerManager blockTrackerManager;
    private static PyroFishingProManager pyroFishingProManager;
    private static CustomFishingManager customFishingManager;

    private static final Jobs PLUGIN = JavaPlugin.getPlugin(Jobs.class);

    private static PluginManager pm;

    public static void loadHooks() {
        pm = PLUGIN.getServer().getPluginManager();

        CMIScheduler.runTask(() -> {
            setMyPetManager();
            setWorldGuard();
            setMythicManager();
            setStackMobHandler();
            setWildStackerHandler();
            setBlockTrackerManager();
            setPyroFishingProManager();
            setCustomFishingManager();
        });
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

    public static PyroFishingProManager getPyroFishingProManager() {
        if (pyroFishingProManager == null)
            pyroFishingProManager = new PyroFishingProManager();

        return pyroFishingProManager;
    }

    public static CustomFishingManager getCustomFishingManager() {
        if (customFishingManager == null)
            customFishingManager = new CustomFishingManager();

        return customFishingManager;
    }

    public static boolean checkMythicMobs() {
        return Jobs.getGCManager().MythicMobsEnabled && MythicManager != null && MythicManager.check();
    }

    public static boolean checkPyroFishingPro() {
        return pyroFishingProManager != null;
    }

    public static boolean checkCustomFishing() {
        return customFishingManager != null;
    }

    public static BlockTrackerManager getBlockTrackerManager() {
        if (blockTrackerManager == null) {
            blockTrackerManager = new BlockTrackerManager();
        }

        return blockTrackerManager;
    }

    private static boolean setWorldGuard() {
        if (JobsHook.WorldGuard.isEnabled()) {
            worldGuardManager = new WorldGuardManager();
            CMIMessages.consoleMessage("&e" + JobsHook.WorldGuard + " detected.");
            return true;
        }

        return false;
    }

    private static void setMythicManager() {
        if (!JobsHook.MythicMobs.isPresent())
            return;

        try {
            Class.forName("io.lumine.mythic.bukkit.BukkitAPIHelper");
            MythicManager = new MythicMobs5(PLUGIN);
            CMIMessages.consoleMessage("&eMythicMobs 5.x detected.");
        } catch (ClassNotFoundException e) {
            CMIMessages.consoleMessage("&cYour MythicMobs version is not supported by Jobs! Supported versions: 4.9.1+");
        }
    }

    public static void setMcMMOlistener() {
        try {
            Class.forName("com.gmail.nossr50.datatypes.skills.SuperAbilityType");
            pm.registerEvents(new McMMO2_X_listener(), PLUGIN);
            CMIMessages.consoleMessage("&eRegistered McMMO 2.x listener");
        } catch (ClassNotFoundException e) {
            pm.registerEvents(new McMMO1_X_listener(), PLUGIN);
            CMIMessages.consoleMessage("&eRegistered McMMO 1.x listener");
        }
    }

    private static void setMyPetManager() {
        if (JobsHook.MyPet.isEnabled()) {
            myPetManager = new MyPetManager();
            CMIMessages.consoleMessage("&e" + JobsHook.MyPet + " detected.");
        }
    }

    private static void setStackMobHandler() {
        if (JobsHook.StackMob.isEnabled()) {
            stackMobHandler = new StackMobHandler();
            CMIMessages.consoleMessage("&e" + JobsHook.StackMob + " detected.");
        }
    }

    private static void setWildStackerHandler() {
        if (JobsHook.WildStacker.isEnabled()) {
            wildStackerHandler = new WildStackerHandler();
            CMIMessages.consoleMessage("&e" + JobsHook.WildStacker + " detected.");
        }
    }

    private static void setBlockTrackerManager() {
        if (JobsHook.BlockTracker.isEnabled()) {
            blockTrackerManager = new BlockTrackerManager();
            CMIMessages.consoleMessage("&e" + JobsHook.BlockTracker + " detected.");
        }
    }

    private static void setPyroFishingProManager() {
        if (JobsHook.PyroFishingPro.isEnabled()) {
            pyroFishingProManager = new PyroFishingProManager();
            CMIMessages.consoleMessage("&e" + JobsHook.PyroFishingPro + " detected.");
        }
    }

    private static void setCustomFishingManager() {
        if (JobsHook.CustomFishing.isEnabled()) {
            customFishingManager = new CustomFishingManager();
            if (Jobs.getGCManager().useCustomFishingOnly) {
                CMIMessages.consoleMessage("&e" + JobsHook.CustomFishing + " detected. (Using CustomFishing-Only Settings)");
            } else {
                CMIMessages.consoleMessage("&e" + JobsHook.CustomFishing + " detected. (Not using CustomFishing-Only Settings)");
            }
        }
    }
}
