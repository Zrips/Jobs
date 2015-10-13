package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.config.ConfigManager;

public class OfflinePlayerList {

    private static HashMap<String, OfflinePlayer> map = new HashMap<String, OfflinePlayer>();
    private static HashMap<UUID, OfflinePlayer> uuidmap = new HashMap<UUID, OfflinePlayer>();

    public static void fillList() {
	Bukkit.getScheduler().runTaskAsynchronously(JobsPlugin.instance, new Runnable() {
	    @Override
	    public void run() {
		OfflinePlayer[] players = Bukkit.getOfflinePlayers();
		for (OfflinePlayer one : players) {
		    if (one == null)
			continue;
		    map.put(one.getName().toLowerCase(), one);
		    uuidmap.put(one.getUniqueId(), one);
		}
		return;
	    }
	});
    }

    public static OfflinePlayer getPlayer(UUID uuid) {
	if (!ConfigManager.getJobsConfiguration().LocalOfflinePlayersData)
	    return Bukkit.getOfflinePlayer(uuid);

	if (uuidmap.containsKey(uuid))
	    return uuidmap.get(uuid);
	return null;
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getPlayer(String name) {
	if (!ConfigManager.getJobsConfiguration().LocalOfflinePlayersData)
	    return Bukkit.getOfflinePlayer(name);

	if (map.containsKey(name.toLowerCase()))
	    return map.get(name.toLowerCase());
	return null;
    }

    public static void addPlayer(OfflinePlayer player) {
	map.put(player.getName().toLowerCase(), player);
	uuidmap.put(player.getUniqueId(), player);
    }

}
