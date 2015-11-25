package com.gamingmesh.jobs.stuff;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.gamingmesh.jobs.config.ConfigManager;

public class OfflinePlayerList {

    private static ConcurrentHashMap<String, OfflinePlayer> map = new ConcurrentHashMap<String, OfflinePlayer>();
    private static ConcurrentHashMap<UUID, OfflinePlayer> uuidmap = new ConcurrentHashMap<UUID, OfflinePlayer>();

    public static void fillList() {
	for (OfflinePlayer one : Bukkit.getOfflinePlayers()) {
	    if (one == null)
		continue;
	    map.put(one.getName().toLowerCase(), one);
	    uuidmap.put(one.getUniqueId(), one);
	}
	return;
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
