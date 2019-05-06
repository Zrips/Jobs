package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class RestrictedAreaManager {

    protected HashMap<String, RestrictedArea> restrictedAreas = new HashMap<>();

    public boolean isExist(String name) {
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getKey().equalsIgnoreCase(name))
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
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getKey().equalsIgnoreCase(name)) {
		restrictedAreas.remove(area.getKey());
		break;
	    }
	}
	File f = new File(Jobs.getFolder(), "restrictedAreas.yml");
	if (f.exists()) {
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	    conf.options().indent(2);
	    conf.options().copyDefaults(true);
	    StringBuilder header = new StringBuilder();
	    header = addHeader(header);
	    conf.set("restrictedareas." + name, null);
	    try {
		conf.save(f);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public HashMap<String, RestrictedArea> getRestrictedAres() {
	return restrictedAreas;
    }

    private void save() {
	File f = new File(Jobs.getFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	conf.options().indent(2);
	conf.options().copyDefaults(true);
	StringBuilder header = new StringBuilder();
	header = addHeader(header);
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
    public synchronized double getRestrictedMultiplier(Player player) {
	if (player == null)
	    return 0D;
	for (RestrictedArea area : getRestrictedAreasByLoc(player.getLocation())) {
	    if (area.inRestrictedArea(player.getLocation()))
		return area.getMultiplier();
	    if (area.getWgName() != null && Jobs.getWorldGuardManager() != null && Jobs.getWorldGuardManager().inArea(player.getLocation(), area.getWgName()))
		return area.getMultiplier();

	}
	return 0D;
    }

    public synchronized List<RestrictedArea> getRestrictedAreasByLoc(Location loc) {
	List<RestrictedArea> areas = new ArrayList<>();
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getValue().inRestrictedArea(loc))
		areas.add(area.getValue());
	}

	if (Jobs.getWorldGuardManager() != null)
	    areas.addAll(Jobs.getWorldGuardManager().getArea(loc));

	return areas;
    }

    public synchronized List<RestrictedArea> getRestrictedAreasByName(String name) {
	List<RestrictedArea> areas = new ArrayList<>();
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getKey().equalsIgnoreCase(name))
		areas.add(area.getValue());
	}
	return areas;
    }

    private static StringBuilder addHeader(StringBuilder header) {
	header.append("Restricted area configuration");
	header.append(System.getProperty("line.separator"))
	    .append(System.getProperty("line.separator"))
	    .append("Configures restricted areas where you cannot get experience or money").append(System.getProperty("line.separator"))
	    .append("when performing a job.").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
	    .append("The multiplier changes the experience/money gains in an area.").append(System.getProperty("line.separator"))
	    .append("A multiplier of 0.0 means no bonus, while 0.5 means you will get 50% more the normal income").append(System.getProperty("line.separator"))
	    .append("While -0.5 means that you will get 50% less the normal income").append(System.getProperty("line.separator"))
	    .append(System.getProperty("line.separator"))
	    .append("restrictedareas:").append(System.getProperty("line.separator"))
	    .append("  area1:").append(System.getProperty("line.separator"))
	    .append("    world: 'world'").append(System.getProperty("line.separator"))
	    .append("    multiplier: 0.0").append(System.getProperty("line.separator"))
	    .append("    point1:").append(System.getProperty("line.separator"))
	    .append("      x: 125").append(System.getProperty("line.separator"))
	    .append("      y: 0").append(System.getProperty("line.separator"))
	    .append("      z: 125").append(System.getProperty("line.separator"))
	    .append("    point2:").append(System.getProperty("line.separator"))
	    .append("      x: 150").append(System.getProperty("line.separator"))
	    .append("      y: 100").append(System.getProperty("line.separator"))
	    .append("      z: 150").append(System.getProperty("line.separator"))
	    .append("  area2:").append(System.getProperty("line.separator"))
	    .append("    world: 'world_nether'").append(System.getProperty("line.separator"))
	    .append("    multiplier: 0.0").append(System.getProperty("line.separator"))
	    .append("    point1:").append(System.getProperty("line.separator"))
	    .append("      x: -100").append(System.getProperty("line.separator"))
	    .append("      y: 0").append(System.getProperty("line.separator"))
	    .append("      z: -100").append(System.getProperty("line.separator"))
	    .append("    point2:").append(System.getProperty("line.separator"))
	    .append("      x: -150").append(System.getProperty("line.separator"))
	    .append("      y: 100").append(System.getProperty("line.separator"))
	    .append("      z: -150");
	return header;
    }

    /**
     * Method to load the restricted areas configuration
     * 
     * loads from Jobs/restrictedAreas.yml
     */
    public synchronized void load() {
	restrictedAreas.clear();
	File f = new File(Jobs.getFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	conf.options().indent(2);
	conf.options().copyDefaults(true);
	StringBuilder header = new StringBuilder();

	header = addHeader(header);

	conf.options().header(header.toString());
	ConfigurationSection areaSection = conf.getConfigurationSection("restrictedareas");
	if (areaSection != null) {
	    for (String areaKey : areaSection.getKeys(false)) {
		double multiplier = conf.getDouble("restrictedareas." + areaKey + ".multiplier", 0d);

		if (conf.isBoolean("restrictedareas." + areaKey + ".WG"))
		    addNew(new RestrictedArea(areaKey, areaKey, multiplier));
		else {

		    String worldName = conf.getString("restrictedareas." + areaKey + ".world");
		    World world = Bukkit.getServer().getWorld(worldName);
		    if (world == null)
			continue;
		    Location point1 = new Location(world, conf.getDouble("restrictedareas." + areaKey + ".point1.x", 0d), conf.getDouble("restrictedareas." + areaKey
			+ ".point1.y", 0d), conf.getDouble("restrictedareas." + areaKey + ".point1.z", 0d));

		    Location point2 = new Location(world, conf.getDouble("restrictedareas." + areaKey + ".point2.x", 0d), conf.getDouble("restrictedareas." + areaKey
			+ ".point2.y", 0d), conf.getDouble("restrictedareas." + areaKey + ".point2.z", 0d));
		    addNew(new RestrictedArea(areaKey, new CuboidArea(point1, point2), multiplier));
		}
	    }
	}

	if (restrictedAreas.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + restrictedAreas.size() + " restricted areas!");

	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
