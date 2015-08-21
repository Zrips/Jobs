package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.config.ConfigManager;

public class Scboard {

	private static HashMap<String, Long> timerMap = new HashMap<String, Long>();

	private static void RunScheduler() {

		Iterator<Entry<String, Long>> MeinMapIter = timerMap.entrySet().iterator();
		while (MeinMapIter.hasNext()) {
			Entry<String, Long> Map = MeinMapIter.next();

			if (System.currentTimeMillis() > Map.getValue() + (ConfigManager.getJobsConfiguration().ToplistInScoreboardInterval * 1000)) {
				Player player = Bukkit.getPlayer(Map.getKey());
				if (player != null) {
					player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
				}
				timerMap.remove(Map.getKey());
			}
		}

		if (timerMap.size() > 0)
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JobsPlugin.instance, new Runnable() {
				public void run() {
					RunScheduler();
					return;
				}
			}, 20L);
		return;
	}

	public static void addNew(Player player) {
		timerMap.put(player.getName(), System.currentTimeMillis());
		RunScheduler();
	}

}
