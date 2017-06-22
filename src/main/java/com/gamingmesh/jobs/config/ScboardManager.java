package com.gamingmesh.jobs.config;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ScoreboardInfo;

public class ScboardManager {

    private ConcurrentHashMap<UUID, ScoreboardInfo> timerMap = new ConcurrentHashMap<UUID, ScoreboardInfo>();
    private Jobs plugin;

    public ScboardManager(Jobs plugin) {
	this.plugin = plugin;
    }

    private void RunScheduler() {
	Iterator<Entry<UUID, ScoreboardInfo>> MeinMapIter = timerMap.entrySet().iterator();
	while (MeinMapIter.hasNext()) {
	    Entry<UUID, ScoreboardInfo> Map = MeinMapIter.next();

	    if (System.currentTimeMillis() > Map.getValue().getTime() + (Jobs.getGCManager().ToplistInScoreboardInterval * 1000)) {
		Player player = Bukkit.getPlayer(Map.getKey());
		if (player != null) {
		    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		    if (Map.getValue().getObj() != null) {
			Objective obj = player.getScoreboard().getObjective(Map.getValue().getObj().getName());
			if (obj != null)
			    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		    }
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
	Scoreboard scoreBoard = player.getScoreboard();
	timerMap.put(player.getUniqueId(), new ScoreboardInfo(scoreBoard, DisplaySlot.SIDEBAR));
	RunScheduler();
    }

}
