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
import com.gamingmesh.jobs.stuff.ChatColor;

public class RestrictedAreaManager {

    protected HashMap<String, RestrictedArea> restrictedAreas = new HashMap<String, RestrictedArea>();

    private Jobs plugin;

    public RestrictedAreaManager(Jobs plugin) {
	this.plugin = plugin;
    }

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

    private void save() {
	File f = new File(plugin.getDataFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	conf.options().indent(2);
	conf.options().copyDefaults(true);
	StringBuilder header = new StringBuilder();
	header = addHeader(header);
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    String areaKey = area.getKey();
	    CuboidArea cuboid = area.getValue().getCuboidArea();
	    conf.set("restrictedareas." + areaKey + ".world", cuboid.getWorld().getName());
	    conf.set("restrictedareas." + areaKey + ".multiplier", area.getValue().getMultiplier());
	    conf.set("restrictedareas." + areaKey + ".point1.x", cuboid.getLowLoc().getBlockX());
	    conf.set("restrictedareas." + areaKey + ".point1.y", cuboid.getLowLoc().getBlockY());
	    conf.set("restrictedareas." + areaKey + ".point1.z", cuboid.getLowLoc().getBlockZ());
	    conf.set("restrictedareas." + areaKey + ".point2.x", cuboid.getHighLoc().getBlockX());
	    conf.set("restrictedareas." + areaKey + ".point2.y", cuboid.getHighLoc().getBlockY());
	    conf.set("restrictedareas." + areaKey + ".point2.z", cuboid.getHighLoc().getBlockZ());
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
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getValue().inRestrictedArea(player.getLocation()))
		return area.getValue().getMultiplier();
	}
	return 0D;
    }

    public synchronized List<RestrictedArea> getRestrictedAreasByLoc(Location loc) {
	List<RestrictedArea> areas = new ArrayList<RestrictedArea>();
	for (Entry<String, RestrictedArea> area : restrictedAreas.entrySet()) {
	    if (area.getValue().inRestrictedArea(loc))
		areas.add(area.getValue());
	}
	return areas;
    }

    private StringBuilder addHeader(StringBuilder header) {
	header.append("Restricted area configuration");
	header.append(System.getProperty("line.separator"))
	    .append(System.getProperty("line.separator"))
	    .append("Configures restricted areas where you cannot get experience or money").append(System.getProperty("line.separator"))
	    .append("when performing a job.").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
	    .append("The multiplier changes the experience/money gains in an area.").append(System.getProperty("line.separator"))
	    .append("A multiplier of 0.0 means no money or xp, while 0.5 means you will get half the normal money/exp").append(System.getProperty("line.separator"))
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
	this.restrictedAreas.clear();
	File f = new File(plugin.getDataFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	conf.options().indent(2);
	conf.options().copyDefaults(true);
	StringBuilder header = new StringBuilder();

	header = addHeader(header);

	conf.options().header(header.toString());
	ConfigurationSection areaSection = conf.getConfigurationSection("restrictedareas");
	if (areaSection != null) {
	    for (String areaKey : areaSection.getKeys(false)) {
		String worldName = conf.getString("restrictedareas." + areaKey + ".world");
		double multiplier = conf.getDouble("restrictedareas." + areaKey + ".multiplier", 0.0);
		World world = Bukkit.getServer().getWorld(worldName);
		if (world == null)
		    continue;
		Location point1 = new Location(world, conf.getDouble("restrictedareas." + areaKey + ".point1.x", 0.0), conf.getDouble("restrictedareas." + areaKey
		    + ".point1.y", 0.0), conf.getDouble("restrictedareas." + areaKey + ".point1.z", 0.0));

		Location point2 = new Location(world, conf.getDouble("restrictedareas." + areaKey + ".point2.x", 0.0), conf.getDouble("restrictedareas." + areaKey
		    + ".point2.y", 0.0), conf.getDouble("restrictedareas." + areaKey + ".point2.z", 0.0));
		addNew(new RestrictedArea(areaKey, new CuboidArea(point1, point2), multiplier));
	    }
	}

	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + restrictedAreas.size() + " restricted areas!");

	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
