package com.gamingmesh.jobs.hooks;

import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.hooks.McMMO.McMMO1_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMO2_X_listener;
import com.gamingmesh.jobs.hooks.McMMO.McMMOManager;
import com.gamingmesh.jobs.hooks.MyPet.MyPetManager;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs5;
import com.gamingmesh.jobs.hooks.MythicMobs.MythicMobs5Listener;
import com.gamingmesh.jobs.hooks.WorldGuard.WorldGuardManager;
import com.gamingmesh.jobs.hooks.blockTracker.BlockTrackerManager;
import com.gamingmesh.jobs.hooks.pyroFishingPro.PyroFishingProListener;
import com.gamingmesh.jobs.hooks.stackMob.StackMobManager;
import com.gamingmesh.jobs.hooks.wildStacker.WildStackerHandler;
import com.gamingmesh.jobs.listeners.JobsCustomFishingPaymentListener;

import net.Zrips.CMILib.Messages.CMIMessages;

public enum JobsHook {
    MyPet {
        @Override
        public boolean init() {

            if (!isPresent())
                return false;

            JobsHook.myPetManager = new MyPetManager();
            printDetectedMessage(this);
            return true;
        }
    },
    StackMob {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            JobsHook.stackMobHandler = new StackMobManager();
            printDetectedMessage(this);
            return true;
        }
    },
    WildStacker {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            JobsHook.wildStackerHandler = new WildStackerHandler();
            printDetectedMessage(this);
            return true;
        }
    },
    WorldGuard {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            JobsHook.worldGuardManager = new WorldGuardManager();
            printDetectedMessage(this);
            return true;
        }
    },
    MythicMobs {

        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            try {
                Class.forName("io.lumine.mythic.bukkit.BukkitAPIHelper");
                JobsHook.mythicManager = new MythicMobs5(JavaPlugin.getPlugin(Jobs.class));
                printDetectedMessage(this);
            } catch (ClassNotFoundException e) {
                CMIMessages.consoleMessage("&cYour MythicMobs version is not supported by Jobs! Supported versions: 5.0.0+");
            }
            return true;
        }

        @Override
        public void registerListener() {
            if (!isPresent())
                return;

            if (!Jobs.getGCManager().MythicMobsEnabled)
                return;

            if (getMythicMobsManager() == null)
                return;

            getMythicMobsManager().check();

            JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().registerEvents(new MythicMobs5Listener(), JavaPlugin.getPlugin(Jobs.class));
            printListenerMessage(this);
        }
    },
    mcMMO {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            JobsHook.mcMMOManager = new McMMOManager();
            printDetectedMessage(this);
            return true;
        }

        @Override
        public void registerListener() {

            if (!isPresent())
                return;

            getMcMMOManager().CheckmcMMO();

            try {
                Class.forName("com.gmail.nossr50.datatypes.skills.SuperAbilityType");
                JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().registerEvents(new McMMO2_X_listener(), JavaPlugin.getPlugin(Jobs.class));
                printListenerMessage(this, "2.x");
            } catch (ClassNotFoundException e) {
                JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().registerEvents(new McMMO1_X_listener(), JavaPlugin.getPlugin(Jobs.class));
                printListenerMessage(this, "1.x");
            }
        }
    },
    BlockTracker {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            JobsHook.blockTrackerManager = new BlockTrackerManager();
            printDetectedMessage(this);
            return true;
        }
    },
    PyroFishingPro {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;

            printDetectedMessage(this);
            return true;
        }

        @Override
        public void registerListener() {

            if (!isPresent())
                return;

            JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().registerEvents(new PyroFishingProListener(), JavaPlugin.getPlugin(Jobs.class));
            printListenerMessage(this);
        }
    },
    CustomFishing {
        @Override
        public boolean init() {
            if (!isPresent())
                return false;
            printDetectedMessage(this, Jobs.getGCManager().useCustomFishingOnly ? "(Using CustomFishing-Only Settings)" : "(Not using CustomFishing-Only Settings)");
            return true;
        }

        @Override
        public void registerListener() {

            if (!isPresent())
                return;

            if (!Jobs.getGCManager().useCustomFishingOnly)
                return;

            JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().registerEvents(new JobsCustomFishingPaymentListener(), JavaPlugin.getPlugin(Jobs.class));
            printListenerMessage(this);
        }
    };

    private Boolean enabled;
    private Boolean present;

    public boolean isEnabled() {
        if (enabled == null)
            enabled = JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().isPluginEnabled(name());
        return enabled;
    }

    public boolean isPresent() {
        if (present == null)
            present = JavaPlugin.getPlugin(Jobs.class).getServer().getPluginManager().getPlugin(name()) != null;
        return present;
    }

    public void registerListener() {

    }

    public boolean init() {
        return false;
    }

    private static void printDetectedMessage(JobsHook hook, String... extra) {
        CMIMessages.consoleMessage("&e" + hook + " &7detected." + (extra.length == 0 ? "" : " " + extra[0]));
    }

    private static void printListenerMessage(JobsHook hook, String... extra) {
        CMIMessages.consoleMessage("&7Registered &e" + hook + (extra.length == 0 ? "" : " " + extra[0]) + " &7listener.");
    }

    private static McMMOManager mcMMOManager;
    private static MythicMobs5 mythicManager;
    private static MyPetManager myPetManager;
    private static WorldGuardManager worldGuardManager;
    private static StackMobManager stackMobHandler;
    private static WildStackerHandler wildStackerHandler;
    private static BlockTrackerManager blockTrackerManager;

    public static void loadHooks() {
        for (JobsHook one : JobsHook.values()) {
            one.init();
        }
    }

    public static StackMobManager getStackMobManager() {
        return stackMobHandler;
    }

    public static WildStackerHandler getWildStackerManager() {
        return wildStackerHandler;
    }

    public static MyPetManager getMyPetManager() {
        return myPetManager;
    }

    public static WorldGuardManager getWorldGuardManager() {
        return worldGuardManager;
    }

    public static McMMOManager getMcMMOManager() {
        return mcMMOManager;
    }

    public static MythicMobs5 getMythicMobsManager() {
        return mythicManager;
    }

    public static BlockTrackerManager getBlockTrackerManager() {
        return blockTrackerManager;
    }
}
