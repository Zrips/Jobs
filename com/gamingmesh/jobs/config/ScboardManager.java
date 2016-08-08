package com.gamingmesh.jobs.config;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;

public class ScboardManager {

    private ConcurrentHashMap<String, Long> timerMap = new ConcurrentHashMap<String, Long>();
    private Jobs plugin;

    public ScboardManager(Jobs plugin) {
	this.plugin = plugin;
    }

    private void RunScheduler() {
	Iterator<Entry<String, Long>> MeinMapIter = timerMap.entrySet().iterator();
	while (MeinMapIter.hasNext()) {
	    Entry<String, Long> Map = MeinMapIter.next();

	    if (System.currentTimeMillis() > Map.getValue() + (Jobs.getGCManager().ToplistInScoreboardInterval * 1000)) {
		Player player = Bukkit.getPlayer(Map.getKey());
		if (player != null) {
		    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}
		timerMap.remove(Map.getKey());
	    }
	}

	if (timerMap.size() > 0)
	    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		@Override
		public void run() {
		    RunScheduler();
		    return;
		}
	    }, 20L);
	return;
    }

    public void addNew(Player player) {
	timerMap.put(player.getName(), System.currentTimeMillis());
	RunScheduler();
    }

}
