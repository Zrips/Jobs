package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CuboidArea;
import com.gamingmesh.jobs.container.RestrictedArea;
import com.gamingmesh.jobs.hooks.HookManager;

public class RestrictedAreaManager {

    protected final Map<String, RestrictedArea> restrictedAreas = new HashMap<>();

    private boolean worldGuardArea = false;

    public boolean isExist(String name) {
	for (String area : restrictedAreas.keySet()) {
	    if (area.equalsIgnoreCase(name))
		return true;
	}
	return false;
    }

    public void addNew(RestrictedArea ra) {
	addNew(ra, false);
    }

    public void addNew(RestrictedArea ra, boolean save) {
	restrictedAreas.put(ra.getName(), ra);
	if (save)
	    save();
    }

    public void remove(String name) {
	for (String area : restrictedAreas.keySet()) {
	    if (area.equalsIgnoreCase(name)) {
		restrictedAreas.remove(area);
		break;
	    }
	}
	File f = new File(Jobs.getFolder(), "restrictedAreas.yml");
	if (f.exists()) {
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	    conf.options().indent(2);
	    conf.options().copyDefaults(true);
	    addHeader(new StringBuilder());
	    conf.set("restrictedareas." + name, null);
	    try {
		conf.save(f);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
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
	File f = new File(Jobs.getFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

	conf.options().indent(2);
	conf.options().copyDefaults(true);

	addHeader(new StringBuilder());

	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    String areaKey = area.getKey();
	    CuboidArea cuboid = area.getValue().getCuboidArea();
	    conf.set("restrictedareas." + areaKey + ".multiplier", area.getValue().getMultiplier());

	    if (area.getValue().getWgName() == null) {
		conf.set("restrictedareas." + areaKey + ".world", cuboid.getWorld().getName());
		conf.set("restrictedareas." + areaKey + ".point1.x", cuboid.getLowLoc().getBlockX());
		conf.set("restrictedareas." + areaKey + ".point1.y", cuboid.getLowLoc().getBlockY());
		conf.set("restrictedareas." + areaKey + ".point1.z", cuboid.getLowLoc().getBlockZ());
		conf.set("restrictedareas." + areaKey + ".point2.x", cuboid.getHighLoc().getBlockX());
		conf.set("restrictedareas." + areaKey + ".point2.y", cuboid.getHighLoc().getBlockY());
		conf.set("restrictedareas." + areaKey + ".point2.z", cuboid.getHighLoc().getBlockZ());
	    } else
		conf.set("restrictedareas." + areaKey + ".WG", true);
	}

	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Gets the area multiplier for the player
     * @param player
     * @return - the multiplier
     */
    public double getRestrictedMultiplier(Player player) {
	if (player == null)
	    return 0D;
	for (RestrictedArea area : getRestrictedAreasByLoc(player.getLocation())) {
	    if (area.inRestrictedArea(player.getLocation()) || (area.getWgName() != null && HookManager.getWorldGuardManager() != null
		&& HookManager.getWorldGuardManager().inArea(player.getLocation(), area.getWgName())))
		return area.getMultiplier();
	}
	return 0D;
    }

    public List<RestrictedArea> getRestrictedAreasByLoc(Location loc) {
	List<RestrictedArea> areas = new ArrayList<>();
	for (RestrictedArea area : restrictedAreas.values()) {
	    if (area.inRestrictedArea(loc))
		areas.add(area);
	}

	if (worldGuardArea && HookManager.getWorldGuardManager() != null)
	    areas.addAll(HookManager.getWorldGuardManager().getArea(loc));

	return areas;
    }

    public List<RestrictedArea> getRestrictedAreasByName(String name) {
	List<RestrictedArea> areas = new ArrayList<>();
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getKey().equalsIgnoreCase(name))
		areas.add(area.getValue());
	}
	return areas;
    }

    private static StringBuilder addHeader(StringBuilder header) {
	String sep = System.lineSeparator();

	header.append("Restricted area configuration");
	header.append(sep)
	    .append(sep)
	    .append("Configures restricted areas where you cannot get experience or money").append(sep)
	    .append("when performing a job.").append(sep).append(sep)
	    .append("The multiplier changes the experience/money gains in an area.").append(sep)
	    .append("A multiplier of 0.0 means no bonus, while 0.5 means you will get 50% more the normal income").append(sep)
	    .append("While -0.5 means that you will get 50% less the normal income").append(sep)
	    .append(sep)
	    .append("restrictedareas:").append(sep)
	    .append("  area1:").append(sep)
	    .append("    world: 'world'").append(sep)
	    .append("    multiplier: 0.0").append(sep)
	    .append("    point1:").append(sep)
	    .append("      x: 125").append(sep)
	    .append("      y: 0").append(sep)
	    .append("      z: 125").append(sep)
	    .append("    point2:").append(sep)
	    .append("      x: 150").append(sep)
	    .append("      y: 100").append(sep)
	    .append("      z: 150").append(sep)
	    .append("  area2:").append(sep)
	    .append("    world: 'world_nether'").append(sep)
	    .append("    multiplier: 0.0").append(sep)
	    .append("    point1:").append(sep)
	    .append("      x: -100").append(sep)
	    .append("      y: 0").append(sep)
	    .append("      z: -100").append(sep)
	    .append("    point2:").append(sep)
	    .append("      x: -150").append(sep)
	    .append("      y: 100").append(sep)
	    .append("      z: -150");
	return header;
    }

    /**
     * Method to load the restricted areas configuration
     * 
     * loads from Jobs/restrictedAreas.yml
     */
    public void load() {
	restrictedAreas.clear();

	File f = new File(Jobs.getFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

	conf.options().indent(2);
	conf.options().copyDefaults(true);

	StringBuilder header = addHeader(new StringBuilder());
	conf.options().header(header.toString());

	ConfigurationSection areaSection = conf.getConfigurationSection("restrictedareas");
	if (areaSection != null) {
	    for (String areaKey : areaSection.getKeys(false)) {
		double multiplier = areaSection.getDouble(areaKey + ".multiplier");

		if (areaSection.isBoolean(areaKey + ".WG")) {
		    addNew(new RestrictedArea(areaKey, areaKey, multiplier));
		    worldGuardArea = true;
		} else {
		    World world = Bukkit.getServer().getWorld(areaSection.getString(areaKey + ".world", ""));
		    if (world == null)
			continue;
		    Location point1 = new Location(world, areaSection.getDouble(areaKey + ".point1.x"), areaSection.getDouble(areaKey
			+ ".point1.y"), areaSection.getDouble(areaKey + ".point1.z"));

		    Location point2 = new Location(world, areaSection.getDouble(areaKey + ".point2.x"), areaSection.getDouble(areaKey
			+ ".point2.y"), areaSection.getDouble(areaKey + ".point2.z"));
		    addNew(new RestrictedArea(areaKey, new CuboidArea(point1, point2), multiplier));
		}
	    }
	}

	int size = restrictedAreas.size();
	if (size > 0)
	    Jobs.consoleMsg("&e[Jobs] Loaded " + size + " restricted areas!");

	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
