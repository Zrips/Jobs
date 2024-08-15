package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.YmlMaker;

public class ToggleBarHandling {

    static Map<UUID, Boolean> actionBarToggle = new HashMap<>();
    static Map<UUID, Boolean> bossBarToggle = new HashMap<>();

    public static void load() {
        YmlMaker f = new YmlMaker(Jobs.getFolder(), "actionBarBossbar.yml");
        if (!f.exists())
            return;

        FileConfiguration config = f.getConfig();

        if (Jobs.getGCManager().BossBarEnabled) {
            ConfigurationSection section = config.getConfigurationSection("bossBar");

            if (section != null) {
                for (String one : section.getKeys(false)) {
                    boolean boo = section.getBoolean(one);
                    if (boo)
                        continue;

                    try {
                        bossBarToggle.put(UUID.fromString(one), boo);
                    } catch (Throwable e) {
                    }
                }
            }
        }

        if (Jobs.getGCManager().ActionBarsMessageByDefault) {
            ConfigurationSection section = config.getConfigurationSection("actionBar");

            if (section != null) {
                for (String one : section.getKeys(false)) {
                    boolean boo = section.getBoolean(one);
                    if (boo)
                        continue;
                    try {
                        actionBarToggle.put(UUID.fromString(one), boo);
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
                for (Entry<UUID, Boolean> one : bossBarToggle.entrySet()) {
                    if (!one.getValue()) {
                        config.set("bossBar." + one.getKey().toString(), one.getValue());
                    }
                }
            }
        }

        if (Jobs.getGCManager().ActionBarsMessageByDefault) {
            config.set("actionBar", null);

            if (!actionBarToggle.isEmpty()) {
                for (Entry<UUID, Boolean> one : actionBarToggle.entrySet()) {
                    if (!one.getValue()) {
                        config.set("actionBar." + one.getKey().toString(), one.getValue());
                    }
                }
            }
        }

        bossBarToggle.clear();
        actionBarToggle.clear();

        f.saveConfig();
    }

    public static Map<UUID, Boolean> getActionBarToggle() {
        return actionBarToggle;
    }

    public static Map<UUID, Boolean> getBossBarToggle() {
        return bossBarToggle;
    }
}
