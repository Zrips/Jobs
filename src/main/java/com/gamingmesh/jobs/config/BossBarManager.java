package com.gamingmesh.jobs.config;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;
import com.gamingmesh.jobs.container.BossBarInfo;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;

public class BossBarManager {

    private Jobs plugin;

    public BossBarManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public synchronized void ShowJobProgression(final JobsPlayer player) {
	if (Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_9_R1))
	    return;

	if (player == null)
	    return;
	for (JobProgression oneJob : player.getJobProgression()) {
	    if (oneJob.getLastExperience() != 0) {
		ShowJobProgression(player, oneJob, oneJob.getLastExperience());
	    }
	}
	player.clearUpdateBossBarFor();
    }

    public synchronized void ShowJobProgression(final JobsPlayer player, final JobProgression jobProg, double expGain) {
	if (Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_9_R1))
	    return;

	String playerUUID = player.getPlayer().getUniqueId().toString();
	if (!Jobs.getGCManager().BossBarsMessageByDefault)
	    return;

	Boolean show = ToggleBarHandling.getBossBarToggle().getOrDefault(playerUUID, true);
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

	String gain = "";
	if (expGain != 0) {
	    expGain = (int) (expGain * 100) / 100D;
	    gain = expGain > 0 ? "+" + expGain : "" + expGain;
	    gain = Jobs.getLanguage().getMessage("command.stats.bossBarGain", "%gain%", gain);
	}

	String message = Jobs.getLanguage().getMessage("command.stats.bossBarOutput",
	    "%joblevel%", Integer.valueOf(jobProg.getLevel()).toString(),
	    "%jobname%", jobProg.getJob().getChatColor() + jobProg.getJob().getName(),
	    "%jobxp%", formatter.format(Math.round(jobProg.getExperience() * 100.0) / 100.0),
	    "%jobmaxxp%", jobProg.getMaxExperience(),
	    "%gain%", gain);

	if (bar == null) {
	    BarColor color = getColor(jobProg.getJob());
	    if (color == null) {
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
		default:
		    color = BarColor.BLUE;
		    break;
		}
	    }
	    BarStyle style = BarStyle.SOLID;
	    switch (Jobs.getGCManager().SegementCount) {
	    case 6:
		style = BarStyle.SEGMENTED_6;
		break;
	    case 10:
		style = BarStyle.SEGMENTED_10;
		break;
	    case 12:
		style = BarStyle.SEGMENTED_12;
		break;
	    case 20:
		style = BarStyle.SEGMENTED_20;
		break;
	    default:
		style = BarStyle.SEGMENTED_6;
		break;
	    }
	    bar = Bukkit.createBossBar(message, color, style);
	} else
	    bar.setTitle(message);

	double percentage = jobProg.getExperience() / jobProg.getMaxExperience();
	percentage = percentage > 1D ? 1D : percentage < 0 ? 0 : percentage;
	bar.setProgress(percentage);

	if (OldOne == null) {
	    bar.addPlayer(player.getPlayer());
	    OldOne = new BossBarInfo(player.getName(), jobProg.getJob().getName(), bar);
	    player.getBossBarInfo().add(OldOne);
	}

	bar.setVisible(true);

	OldOne.setId(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    @Override
	    public void run() {
		for (BossBarInfo one : player.getBossBarInfo()) {
		    if (!one.getPlayerName().equalsIgnoreCase(player.getName()))
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

	jobProg.setLastExperience(0D);
    }

    private static BarColor getColor(Job job) {
	if (job.getBossbar() == null)
	    return null;
	for (BarColor color : BarColor.values()) {
	    if (job.getBossbar().equalsIgnoreCase(color.name()))
		return color;
	}
	return null;
    }
}
