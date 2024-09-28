package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;
import com.gamingmesh.jobs.container.MessageToggleState;

import net.Zrips.CMILib.Container.CMINumber;

public class ToggleBarHandling {

    static Map<UUID, MessageToggleState> actionBarToggle = new HashMap<>();
    static Map<UUID, MessageToggleState> bossBarToggle = new HashMap<>();

    public static void load() {
        YmlMaker f = new YmlMaker(Jobs.getFolder(), "actionBarBossbar.yml");
        if (!f.exists())
            return;

        FileConfiguration config = f.getConfig();

        if (Jobs.getGCManager().BossBarEnabled) {
            ConfigurationSection section = config.getConfigurationSection("bossBar");

            if (section != null) {
                for (String one : section.getKeys(false)) {

                    MessageToggleState state = MessageToggleState.Rapid;

                    if (section.isBoolean(one)) {
                        if (!section.getBoolean(one))
                            state = MessageToggleState.Off;
                    } else if (section.isInt(one)) {
                        int id = section.getInt(one);
                        state = MessageToggleState.getFromID(id);
                    }

                    try {
                        bossBarToggle.put(UUID.fromString(one), state);
                    } catch (Throwable e) {
                    }
                }
            }
        }

        if (Jobs.getGCManager().ActionBarEnabled) {
            ConfigurationSection section = config.getConfigurationSection("actionBar");

            if (section != null) {
                for (String one : section.getKeys(false)) {

                    MessageToggleState state = MessageToggleState.Rapid;

                    if (section.isBoolean(one)) {
                        if (!section.getBoolean(one))
                            state = MessageToggleState.Off;
                    } else if (section.isInt(one)) {
                        int id = section.getInt(one);
                        state = MessageToggleState.getFromID(id);
                    }

                    try {
                        actionBarToggle.put(UUID.fromString(one), state);
                    } catch (Throwable e) {
                    }
                }
            }
        }
    }

    public static void save() {
        YmlMaker f = new YmlMaker(Jobs.getFolder(), "actionBarBossbar.yml");

        if (bossBarToggle.isEmpty() && actionBarToggle.isEmpty()) {
            if (f.exists() && f.getConfigFile().length() == 0L) {
                f.getConfigFile().delete();
            }

            return;
        }

        if (!f.exists())
            f.createNewFile();

        f.saveDefaultConfig();

        FileConfiguration config = f.getConfig();

        if (Jobs.getGCManager().BossBarEnabled) {
            config.set("bossBar", null);

            if (!bossBarToggle.isEmpty()) {
                for (Entry<UUID, MessageToggleState> one : bossBarToggle.entrySet()) {
                    if (!one.getValue().equals(Jobs.getGeneralConfigManager().BossBarsMessageDefault)) {
                        config.set("bossBar." + one.getKey().toString(), one.getValue());
                    }
                }
            }
        }

        config.set("actionBar", null);

        if (!actionBarToggle.isEmpty()) {
            for (Entry<UUID, MessageToggleState> one : actionBarToggle.entrySet()) {
                if (!one.getValue().equals(Jobs.getGeneralConfigManager().ActionBarsMessageDefault)) {
                    config.set("actionBar." + one.getKey().toString(), one.getValue());
                }
            }
        }

        bossBarToggle.clear();
        actionBarToggle.clear();

        f.saveConfig();
    }

    public static Map<UUID, MessageToggleState> getActionBarToggle() {
        return actionBarToggle;
    }

    public static Map<UUID, MessageToggleState> getBossBarToggle() {
        return bossBarToggle;
    }

    public static MessageToggleState getActionBarState(UUID uuid) {
        synchronized (actionBarToggle) {
            return actionBarToggle.getOrDefault(uuid, Jobs.getGCManager().ActionBarsMessageDefault);
        }
    }

    public static MessageToggleState getBossBarState(UUID uuid) {
        synchronized (bossBarToggle) {
            return bossBarToggle.getOrDefault(uuid, Jobs.getGCManager().BossBarsMessageDefault);
        }
    }
}
