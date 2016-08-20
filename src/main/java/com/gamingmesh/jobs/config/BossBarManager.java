
package com.gamingmesh.jobs.config;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.BossBarInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class BossBarManager {

	Jobs plugin;

	public BossBarManager(Jobs plugin) {
		this.plugin = plugin;
	}

	public synchronized void ShowJobProgression(final JobsPlayer player) {
		if (Jobs.getActionBar().getVersion() < 1900)
			return;

		if (player == null)
			return;

		List<String> temp = new ArrayList<String>();

		temp.addAll(player.getUpdateBossBarFor());

		for (String one : temp) {
			for (JobProgression oneJob : player.getJobProgression()) {
				if (one.equalsIgnoreCase(oneJob.getJob().getName()))
					ShowJobProgression(player, oneJob);
			}
		}
		player.clearUpdateBossBarFor();
	}

	public synchronized void ShowJobProgression(final JobsPlayer player, final JobProgression jobProg) {
		if (Jobs.getActionBar().getVersion() < 1900)
			return;
		String playername = player.getUserName();
		if (!Jobs.getBossBarToggleList().containsKey(playername) && Jobs.getGCManager().BossBarsMessageByDefault)
			Jobs.getBossBarToggleList().put(playername, true);

		if (!Jobs.getBossBarToggleList().containsKey(playername))
			return;

		Boolean show = Jobs.getBossBarToggleList().get(playername);

		if (!show)
			return;

		BossBar bar = null;
		BossBarInfo OldOne = null;
		for (BossBarInfo one : player.getBossBarInfo()) {
			if (!one.getJobName().equalsIgnoreCase(jobProg.getJob().getName()))
				continue;

			one.cancel();
			bar = one.getBar();
			OldOne = one;
			break;
		}
		NumberFormat formatter = new DecimalFormat("#0.00");

		String message = Jobs.getLanguage().getMessage("command.stats.output", "%joblevel%",
				Integer.valueOf(jobProg.getLevel()).toString(), "%jobname%",
				jobProg.getJob().getChatColor() + jobProg.getJob().getName() + ChatColor.WHITE, "%jobxp%",
				formatter.format(Math.round(jobProg.getExperience() * 100.0) / 100.0), "%jobmaxxp%",
				jobProg.getMaxExperience());

		if (bar == null) {
			BarColor color = BarColor.BLUE;
			switch (player.getBossBarInfo().size()) {
			case 1:
				color = BarColor.GREEN;
				break;
			case 2:
				color = BarColor.RED;
				break;
			case 3:
				color = BarColor.WHITE;
				break;
			case 4:
				color = BarColor.YELLOW;
				break;
			case 5:
				color = BarColor.PINK;
				break;
			case 6:
				color = BarColor.PURPLE;
				break;
			}
			bar = Bukkit.createBossBar(message, color, BarStyle.SEGMENTED_20);
		} else
			bar.setTitle(message);

		double percentage = jobProg.getExperience() / jobProg.getMaxExperience();
		try {
			bar.setProgress(percentage);
			if (OldOne == null) {
				Player target = Bukkit.getPlayer(player.getPlayer().getUniqueId());
				if (target == null)
					return;
				bar.addPlayer(target);
				OldOne = new BossBarInfo(player.getUserName(), jobProg.getJob().getName(), bar);
				player.getBossBarInfo().add(OldOne);
			}
			bar.setVisible(true);
		} catch (NoSuchMethodError e) {
		}

		if (OldOne == null)
			return;

		OldOne.setId(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {

				for (BossBarInfo one : player.getBossBarInfo()) {
					if (!one.getPlayerName().equalsIgnoreCase(player.getUserName()))
						continue;

					if (!one.getJobName().equalsIgnoreCase(jobProg.getJob().getName()))
						continue;

					BossBar tempBar = one.getBar();
					tempBar.setVisible(false);
					break;
				}
				return;
			}
		}, Jobs.getGCManager().BossBarTimer * 20L));

	}
}
