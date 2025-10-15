package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CuboidArea;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.LevelLimits;
import com.gamingmesh.jobs.container.RestrictedArea;
import com.gamingmesh.jobs.hooks.JobsHook;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.CuboidArea.ChunkRef;
import net.Zrips.CMILib.Messages.CMIMessages;

public class RestrictedAreaManager {

    protected final Map<String, RestrictedArea> restrictedAreas = new HashMap<>();

    private static final String fileName = "restrictedAreas.yml";

    public boolean isExist(String name) {
        return restrictedAreas.containsKey(name.toLowerCase());
    }

    public void addNew(RestrictedArea ra) {
        addNew(ra, false);
    }

    public void addNew(RestrictedArea ra, boolean save) {
        restrictedAreas.put(ra.getName().toLowerCase(), ra);
        if (save)
            save();
        if (ra.isEnabled())
            recalculateChunks(ra);
    }

    public void remove(String name) {

        restrictedAreas.remove(name.toLowerCase());

        File f = new File(Jobs.getFolder(), fileName);
        if (!f.exists())
            return;

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
        conf.options().indent(2);
        conf.options().copyDefaults(true);
        conf.set("restrictedareas." + name, null);
        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        recalculateChunks();
    }

    /**
     * Retrieves the restricted areas map.
     * 
     * @deprecated badly named
     * @return the cached map of restricted areas
     */
    @Deprecated
    public Map<String, RestrictedArea> getRestrictedAres() {
        return restrictedAreas;
    }

    public Map<String, RestrictedArea> getRestrictedAreas() {
        return restrictedAreas;
    }

    private void save() {
        File f = new File(Jobs.getFolder(), fileName);
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

        conf.options().indent(2);
        conf.options().copyDefaults(true);

        conf.options().header(addHeader().toString());

        for (Entry<String, RestrictedArea> rs : restrictedAreas.entrySet()) {

            RestrictedArea area = rs.getValue();

            String areaKey = area.getName();
            CuboidArea cuboid = area.getCuboidArea();

            conf.set("restrictedareas." + areaKey + ".enabled", area.isEnabled());

            for (Entry<CurrencyType, Double> one : area.getMultipliers().entrySet()) {
                conf.set("restrictedareas." + areaKey + ".multipliers." + CMIText.firstToUpperCase(one.getKey().name()), one.getValue());
            }

            if (area.getWgName() == null) {
                conf.set("restrictedareas." + areaKey + ".world", cuboid.getWorld().getName());
                conf.set("restrictedareas." + areaKey + ".point1", cuboid.getLowLoc().getBlockX() + ";" + cuboid.getLowLoc().getBlockY() + ";" + cuboid.getLowLoc().getBlockZ());
                conf.set("restrictedareas." + areaKey + ".point2", cuboid.getHighLoc().getBlockX() + ";" + cuboid.getHighLoc().getBlockY() + ";" + cuboid.getHighLoc().getBlockZ());
            } else
                conf.set("restrictedareas." + areaKey + ".WG", true);

            conf.set("restrictedareas." + areaKey + ".jobs", new ArrayList<String>());
        }

        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public double getRestrictedMultiplier(Player player) {
        return getRestrictedMultiplier(null, player);
    }

    /**
     * Gets the area multiplier for the player
     * @param player
     * @return - the multiplier
     */
    @Deprecated
    public double getRestrictedMultiplier(JobProgression prog, Player player) {
        return getRestrictedMultipliers(prog, player).get(CurrencyType.MONEY);
    }

    /**
     * Gets the area multiplier for the player
     * @param player
     * @return - the multiplier
     */
    public BoostMultiplier getRestrictedMultipliers(JobProgression prog, Player player) {
        if (player == null)
            return new BoostMultiplier();

        for (RestrictedArea area : getByLocation(player.getLocation())) {
            if (!area.inRestrictedArea(player.getLocation()) ||
                (area.getWgName() != null && JobsHook.WorldGuard.isEnabled() && !JobsHook.getWorldGuardManager().inArea(player.getLocation(), area.getWgName())))
                continue;

            if (area.getJobs().isEmpty())
                return new BoostMultiplier(area.getMultipliers());

            if (!area.validLevelRange(prog))
                continue;

            return new BoostMultiplier(area.getMultipliers());
        }
        return new BoostMultiplier();
    }

    @Deprecated
    public List<RestrictedArea> getRestrictedAreasByLoc(Location loc) {
        return new ArrayList<>(getByLocation(loc));
    }

    public List<RestrictedArea> getRestrictedAreasByName(String name) {
        List<RestrictedArea> areas = new ArrayList<>();
        for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
            if (area.getKey().equalsIgnoreCase(name))
                areas.add(area.getValue());
        }
        return areas;
    }

    private static StringBuilder addHeader() {
        String sep = System.lineSeparator();

        StringBuilder header = new StringBuilder();
        header.append("Restricted area configuration");
        header.append(sep)
            .append(sep)
            .append("Configures restricted areas where you cannot get experience or money").append(sep)
            .append("when performing a job.").append(sep).append(sep)
            .append("The multiplier changes the experience/money gains in an area.").append(sep)
            .append("A multiplier of 0.0 means no bonus, while 0.5 means you will get 50% more the normal income").append(sep)
            .append("While -0.5 means that you will get 50% less the normal income");

        header.append(sep)
            .append(sep)
            .append("jobs section defines list of jobs this area should apply to").append(sep)
            .append("Define it as [jobName/all]-[fromLevel]-[untilLevel] for example miner-5-10 would mean that this area applies for miners between level 5 and 10").append(sep)
            .append("Level limits are optional and if not defined we will apply to all levels").append(sep)
            .append("You can define for all jobs at same time with defined level limit like all-25-69 which would apply for all jobs between level 25 and 69").append(sep);

        return header;
    }

    /**
     * Method to load the restricted areas configuration
     * 
     * loads from Jobs/restrictedAreas.yml
     */
    public void load() {
        restrictedAreas.clear();
        areas.clear();

        File f = new File(Jobs.getFolder(), fileName);
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

        conf.options().indent(2);
        conf.options().copyDefaults(true);

        conf.options().header(addHeader().toString());

        if (!conf.isConfigurationSection("restrictedareas")) {
            conf.set("restrictedareas.area1.enabled", false);
            for (CurrencyType one : CurrencyType.values()) {
                conf.set("restrictedareas.area1.multipliers." + CMIText.firstToUpperCase(one.toString()), CMINumber.random(-10, 10) / 10D);
            }

            conf.set("restrictedareas.area1.world", Bukkit.getWorlds().get(0).getName());
            conf.set("restrictedareas.area1.point1", "125;0;125");
            conf.set("restrictedareas.area1.point2", "150;100;150");
            conf.set("restrictedareas.area1.jobs", Arrays.asList("digger-0-100"));

            if (Bukkit.getWorlds().size() > 1) {
                conf.set("restrictedareas.area2.enabled", false);
                for (CurrencyType one : CurrencyType.values()) {
                    conf.set("restrictedareas.area2.multipliers." + CMIText.firstToUpperCase(one.toString()), CMINumber.random(-10, 10) / 10D);
                }
                conf.set("restrictedareas.area2.world", Bukkit.getWorlds().get(1).getName());
                conf.set("restrictedareas.area2.point1", "-100;0;-100");
                conf.set("restrictedareas.area2.point2", "-150;100;-150");
                conf.set("restrictedareas.area2.jobs", Arrays.asList("all-5-15"));
            }
        }

        ConfigurationSection areaSection = conf.getConfigurationSection("restrictedareas");
        if (areaSection != null) {
            for (String areaKey : areaSection.getKeys(false)) {

                RestrictedArea area = new RestrictedArea(areaKey, areaKey);

                if (!areaSection.isBoolean(areaKey + ".WG")) {

                    Vector point1 = new Vector();

                    if (areaSection.isDouble(areaKey + ".point1.x"))
                        point1 = new Vector(
                            areaSection.getDouble(areaKey + ".point1.x"),
                            areaSection.getDouble(areaKey + ".point1.y"),
                            areaSection.getDouble(areaKey + ".point1.z"));
                    else if (areaSection.isString(areaKey + ".point1")) {
                        try {
                            String[] p1 = areaSection.getString(areaKey + ".point1").split(";");
                            point1 = new Vector(Double.parseDouble(p1[0]), Double.parseDouble(p1[1]), Double.parseDouble(p1[2]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Vector point2 = new Vector();

                    if (areaSection.isDouble(areaKey + ".point2.x"))
                        point2 = new Vector(
                            areaSection.getDouble(areaKey + ".point2.x"),
                            areaSection.getDouble(areaKey + ".point2.y"),
                            areaSection.getDouble(areaKey + ".point2.z"));
                    if (areaSection.isString(areaKey + ".point2")) {
                        try {
                            String[] p1 = areaSection.getString(areaKey + ".point2").split(";");
                            point2 = new Vector(Double.parseDouble(p1[0]), Double.parseDouble(p1[1]), Double.parseDouble(p1[2]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    area = new RestrictedArea(areaKey, new CuboidArea(areaSection.getString(areaKey + ".world", ""), point1, point2));
                }

                area.setEnabled(areaSection.getBoolean(areaKey + ".enabled", false));

                // Outdated way of defining multiplier
                if (areaSection.isDouble(areaKey + ".multiplier")) {
                    double multiplier = areaSection.getDouble(areaKey + ".multiplier", 0D);
                    for (CurrencyType one : CurrencyType.values()) {
                        area.getMultipliers().put(one, multiplier);
                        areaSection.set(areaKey + ".multipliers." + CMIText.firstToUpperCase(one.toString()), multiplier);
                    }
                    areaSection.set(areaKey + ".multiplier", null);
                } else if (areaSection.isConfigurationSection(areaKey + ".multipliers")) {
                    for (CurrencyType one : CurrencyType.values()) {
                        area.getMultipliers().put(one, areaSection.getDouble(areaKey + ".multipliers." + CMIText.firstToUpperCase(one.name()), 0D));
                    }
                }

                if (areaSection.isList(areaKey + ".jobs")) {
                    for (String jobSection : areaSection.getStringList(areaKey + ".jobs")) {
                        String[] split = jobSection.split("-");
                        try {
                            area.getJobs().put(split[0].toLowerCase(), new LevelLimits(split.length > 1 ? Integer.parseInt(split[1]) : 0, split.length > 2 ? Integer.parseInt(split[2])
                                : Integer.MAX_VALUE));
                        } catch (Exception e) {
                            CMIMessages.consoleMessage("Incorrectly defined job in restricted area " + areaKey);
                        }
                    }
                } else {
                    conf.set("restrictedareas." + areaKey + ".jobs", Arrays.asList("all"));
                }

                addNew(area);
            }
        }

        if (!restrictedAreas.isEmpty())
            CMIMessages.consoleMessage("&eLoaded &6" + restrictedAreas.size() + " &erestricted areas!");

        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Map<String, Map<ChunkRef, Set<RestrictedArea>>> areas = new HashMap<>();

    public void recalculateChunks() {
        areas.clear();
        for (RestrictedArea one : restrictedAreas.values()) {
            recalculateChunks(one);
        }
    }

    public void recalculateChunks(RestrictedArea area) {
        if (area.getCuboidArea() == null)
            return;

        if (area.getCuboidArea().getWorld() == null)
            return;

        Map<ChunkRef, Set<RestrictedArea>> retAreas = areas.computeIfAbsent(area.getCuboidArea().getWorldName(), k -> new HashMap<>());

        for (ChunkRef chunk : area.getCuboidArea().getChunks()) {
            retAreas.computeIfAbsent(chunk, k -> new HashSet<>()).add(area);
        }
    }

    public Set<RestrictedArea> getByLocation(Location loc) {
        Set<RestrictedArea> area = new HashSet<>();
        if (loc == null || loc.getWorld() == null) {
            return area;
        }

        String worldName = loc.getWorld().getName();
        ChunkRef chunk = new ChunkRef(loc);
        Map<ChunkRef, Set<RestrictedArea>> chunkMap = areas.getOrDefault(worldName, Collections.emptyMap());

        chunkMap.getOrDefault(chunk, Collections.emptySet()).stream()
            .filter(entry -> entry != null && entry.inRestrictedArea(loc))
            .forEach(area::add);

        return area;
    }
}
