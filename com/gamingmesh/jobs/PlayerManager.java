/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobCommands;
import com.gamingmesh.jobs.container.JobConditions;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.PerformCommands;

import net.milkbowl.vault.Vault;

public class PlayerManager {
    private Map<String, JobsPlayer> players = Collections.synchronizedMap(new HashMap<String, JobsPlayer>());
    //private Map<String, JobsPlayer> players = new HashMap<String, JobsPlayer>();

    /**
     * Handles join of new player
     * @param playername
     */
    public void playerJoin(Player player) {
	synchronized (players) {
	    JobsPlayer jPlayer = players.get(player.getName().toLowerCase());
	    if (jPlayer == null) {
		jPlayer = JobsPlayer.loadFromDao(Jobs.getJobsDAO(), player);
		JobsPlayer.loadLogFromDao(jPlayer);
		players.put(player.getName().toLowerCase(), jPlayer);
	    }
	    jPlayer.onConnect();
	    jPlayer.reloadHonorific();
	    Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	    return;
	}
    }

    /**
     * Handles player quit
     * @param playername
     */
    public void playerQuit(Player player) {
	synchronized (players) {
	    if (ConfigManager.getJobsConfiguration().saveOnDisconnect()) {
		JobsPlayer jPlayer = players.remove(player.getName().toLowerCase());
		if (jPlayer != null) {
		    jPlayer.save(Jobs.getJobsDAO());
		    jPlayer.onDisconnect();
		}
	    } else {
		JobsPlayer jPlayer = players.get(player.getName().toLowerCase());
		if (jPlayer != null) {
		    jPlayer.onDisconnect();
		}
	    }
	}
    }

    /**
     * Save all the information of all of the players in the game
     */
    public void saveAll() {
	JobsDAO dao = Jobs.getJobsDAO();

	/*
	 * Saving is a three step process to minimize synchronization locks when called asynchronously.
	 * 
	 * 1) Safely copy list for saving.
	 * 2) Perform save on all players on copied list.
	 * 3) Garbage collect the real list to remove any offline players with saved data
	 */
	ArrayList<JobsPlayer> list = null;
	synchronized (players) {
	    list = new ArrayList<JobsPlayer>(players.values());
	}

	for (JobsPlayer jPlayer : list) {
	    jPlayer.save(dao);
	}

	synchronized (players) {
	    Iterator<JobsPlayer> iter = players.values().iterator();
	    while (iter.hasNext()) {
		JobsPlayer jPlayer = iter.next();
		synchronized (jPlayer.saveLock) {
		    if (!jPlayer.isOnline() && jPlayer.isSaved()) {
			iter.remove();
		    }
		}
	    }
	}
    }

    /**
     * Get the player job info for specific player
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(Player player) {
	return players.get(player.getName().toLowerCase());
    }

    /**
     * Get the player job info for specific player
     * @param player name - the player name who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(String playerName) {
	return players.get(playerName.toLowerCase());
    }

    /**
     * Get the player job info for specific player
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayerOffline(OfflinePlayer offlinePlayer) {
	JobsPlayer jPlayer = players.get(offlinePlayer.getName().toLowerCase());
	if (jPlayer != null)
	    return jPlayer;

	JobsPlayer player = JobsPlayer.loadFromDao(Jobs.getJobsDAO(), offlinePlayer);
	JobsPlayer.loadLogFromDao(player);
	return player;
    }

    /**
     * Causes player to join their job
     * @param jPlayer
     * @param job
     */
    public void joinJob(JobsPlayer jPlayer, Job job) {
	synchronized (jPlayer.saveLock) {
	    if (jPlayer.isInJob(job))
		return;
	    // let the user join the job
	    if (!jPlayer.joinJob(job, jPlayer))
		return;

	    // JobsJoin event
	    JobsJoinEvent jobsjoinevent = new JobsJoinEvent(jPlayer, job);
	    Bukkit.getServer().getPluginManager().callEvent(jobsjoinevent);
	    // If event is canceled, dont do anything
	    if (jobsjoinevent.isCancelled())
		return;

	    Jobs.getJobsDAO().joinJob(jPlayer, job);
	    PerformCommands.PerformCommandsOnJoin(jPlayer, job);
	    Jobs.takeSlot(job);
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
	    job.updateTotalPlayers();
	}
    }

    /**
     * Causes player to leave their job
     * @param jPlayer
     * @param job
     */
    public void leaveJob(JobsPlayer jPlayer, Job job) {
	synchronized (jPlayer.saveLock) {
	    if (!jPlayer.isInJob(job))
		return;
	    Jobs.getJobsDAO().recordToArchive(jPlayer, job);
	    // let the user leave the job
	    if (!jPlayer.leaveJob(job))
		return;

	    // JobsJoin event
	    JobsLeaveEvent jobsleaveevent = new JobsLeaveEvent(jPlayer, job);
	    Bukkit.getServer().getPluginManager().callEvent(jobsleaveevent);
	    // If event is canceled, dont do anything
	    if (jobsleaveevent.isCancelled())
		return;

	    Jobs.getJobsDAO().quitJob(jPlayer, job);
	    PerformCommands.PerformCommandsOnLeave(jPlayer, job);
	    Jobs.leaveSlot(job);

	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
	    job.updateTotalPlayers();
	}
    }

    /**
     * Causes player to leave all their jobs
     * @param jPlayer
     */
    public void leaveAllJobs(JobsPlayer jPlayer) {
	synchronized (jPlayer.saveLock) {
	    for (JobProgression job : jPlayer.getJobProgression()) {
		Jobs.getJobsDAO().quitJob(jPlayer, job.getJob());
		PerformCommands.PerformCommandsOnLeave(jPlayer, job.getJob());
		Jobs.leaveSlot(job.getJob());

		com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getJob().getName());
		com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
		job.getJob().updateTotalPlayers();
	    }

	    jPlayer.leaveAllJobs();
	}
    }

    /**
     * Transfers player job
     * @param jPlayer
     * @param oldjob - the old job
     * @param newjob - the new job
     */
    public void transferJob(JobsPlayer jPlayer, Job oldjob, Job newjob) {
	synchronized (jPlayer.saveLock) {
	    if (!jPlayer.transferJob(oldjob, newjob, jPlayer))
		return;

	    JobsDAO dao = Jobs.getJobsDAO();
	    dao.quitJob(jPlayer, oldjob);
	    oldjob.updateTotalPlayers();
	    dao.joinJob(jPlayer, newjob);
	    newjob.updateTotalPlayers();
	    jPlayer.save(dao);
	}
    }

    /**
     * Promotes player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to promote
     */
    public void promoteJob(JobsPlayer jPlayer, Job job, int levels) {
	synchronized (jPlayer.saveLock) {
	    jPlayer.promoteJob(job, levels, jPlayer);
	    jPlayer.save(Jobs.getJobsDAO());

	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
	}
    }

    /**
     * Demote player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to demote
     */
    public void demoteJob(JobsPlayer jPlayer, Job job, int levels) {
	synchronized (jPlayer.saveLock) {
	    jPlayer.demoteJob(job, levels);
	    jPlayer.save(Jobs.getJobsDAO());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
	}
    }

    /**
     * Adds experience to the player
     * @param jPlayer
     * @param job - the job
     * @param experience - experience gained
     */
    public void addExperience(JobsPlayer jPlayer, Job job, double experience) {
	synchronized (jPlayer.saveLock) {
	    JobProgression prog = jPlayer.getJobProgression(job);
	    if (prog == null)
		return;
	    int oldLevel = prog.getLevel();
	    if (prog.addExperience(experience))
		performLevelUp(jPlayer, job, oldLevel);

	    jPlayer.save(Jobs.getJobsDAO());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
	}
    }

    /**
     * Removes experience to the player
     * @param jPlayer
     * @param job - the job
     * @param experience - experience gained
     */
    public void removeExperience(JobsPlayer jPlayer, Job job, double experience) {
	synchronized (jPlayer.saveLock) {
	    JobProgression prog = jPlayer.getJobProgression(job);
	    if (prog == null)
		return;
	    prog.addExperience(-experience);

	    jPlayer.save(Jobs.getJobsDAO());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	    com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
	}
    }

    /**
     * Broadcasts level up about a player
     * @param jPlayer
     * @param job
     * @param oldLevel
     */
    public void performLevelUp(JobsPlayer jPlayer, Job job, int oldLevel) {

	Player player = (Player) jPlayer.getPlayer();
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;

	// LevelUp event
	JobsLevelUpEvent levelUpEvent = new JobsLevelUpEvent(jPlayer, job.getName(), prog.getLevel(), ConfigManager.getJobsConfiguration().getTitleForLevel(oldLevel, job
	    .getName()), ConfigManager.getJobsConfiguration().getTitleForLevel(prog.getLevel(), job.getName()), ConfigManager.getJobsConfiguration().SoundLevelupSound
		.toUpperCase(), ConfigManager.getJobsConfiguration().SoundLevelupVolume, ConfigManager.getJobsConfiguration().SoundLevelupPitch, ConfigManager
		    .getJobsConfiguration().SoundTitleChangeSound.toUpperCase(), ConfigManager.getJobsConfiguration().SoundTitleChangeVolume, ConfigManager
			.getJobsConfiguration().SoundTitleChangePitch);
	Bukkit.getServer().getPluginManager().callEvent(levelUpEvent);
	// If event is canceled, dont do anything
	if (levelUpEvent.isCancelled())
	    return;

	if (ConfigManager.getJobsConfiguration().SoundLevelupUse)
	    player.getWorld().playSound(player.getLocation(), Sound.valueOf(levelUpEvent.getSoundName()), levelUpEvent.getSoundVolume(), levelUpEvent.getSoundPitch());

	String message;
	if (ConfigManager.getJobsConfiguration().isBroadcastingLevelups()) {
	    message = Language.getMessage("message.levelup.broadcast");
	} else {
	    message = Language.getMessage("message.levelup.nobroadcast");
	}

	message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);

	if (levelUpEvent.getOldTitle() != null) {
	    message = message.replace("%titlename%", levelUpEvent.getOldTitleColor() + levelUpEvent.getOldTitleName() + ChatColor.WHITE);
	}
	if (player != null) {
	    message = message.replace("%playername%", player.getDisplayName());
	} else {
	    message = message.replace("%playername%", jPlayer.getUserName());
	}
	message = message.replace("%joblevel%", "" + prog.getLevel());
	for (String line : message.split("\n")) {
	    if (ConfigManager.getJobsConfiguration().isBroadcastingLevelups()) {
		if (ConfigManager.getJobsConfiguration().BroadcastingLevelUpLevels.contains(oldLevel + 1) || ConfigManager
		    .getJobsConfiguration().BroadcastingLevelUpLevels.contains(0))
		    Bukkit.getServer().broadcastMessage(line);
	    } else if (player != null) {
		if (ConfigManager.getJobsConfiguration().LevelChangeActionBar)
		    ActionBar.send(player, line);
		if (ConfigManager.getJobsConfiguration().LevelChangeChat)
		    player.sendMessage(line);
	    }
	}

	if (levelUpEvent.getNewTitle() != null && !levelUpEvent.getNewTitle().equals(levelUpEvent.getOldTitle())) {

	    if (ConfigManager.getJobsConfiguration().SoundTitleChangeUse)
		player.getWorld().playSound(player.getLocation(), Sound.valueOf(levelUpEvent.getTitleChangeSoundName()), levelUpEvent.getTitleChangeVolume(), levelUpEvent
		    .getTitleChangePitch());

	    // user would skill up
	    if (ConfigManager.getJobsConfiguration().isBroadcastingSkillups()) {
		message = Language.getMessage("message.skillup.broadcast");
	    } else {
		message = Language.getMessage("message.skillup.nobroadcast");
	    }
	    if (player != null) {
		message = message.replace("%playername%", player.getDisplayName());
	    } else {
		message = message.replace("%playername%", jPlayer.getUserName());
	    }
	    message = message.replace("%titlename%", levelUpEvent.getNewTitleColor() + levelUpEvent.getNewTitleName() + ChatColor.WHITE);
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
	    for (String line : message.split("\n")) {
		if (ConfigManager.getJobsConfiguration().isBroadcastingLevelups()) {
		    Bukkit.getServer().broadcastMessage(line);
		} else if (player != null) {
		    if (ConfigManager.getJobsConfiguration().TitleChangeActionBar)
			ActionBar.send(player, line);
		    if (ConfigManager.getJobsConfiguration().TitleChangeChat)
			player.sendMessage(line);
		}
	    }
	}
	jPlayer.reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	performCommandOnLevelUp(jPlayer, prog.getJob(), oldLevel);
	com.gamingmesh.jobs.Signs.SignUtil.SignUpdate(job.getName());
	com.gamingmesh.jobs.Signs.SignUtil.SignUpdate("gtoplist");
    }

    /**
     * Performs command on level up
     * @param jPlayer
     * @param job
     * @param oldLevel
     */
    public void CheckConditions(JobsPlayer jPlayer, Job job) {
	Player player = Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID());
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;
	for (JobConditions Condition : job.getConditions()) {
	    boolean ok = true;
	    for (String oneReq : Condition.getRequires()) {
		if (oneReq.toLowerCase().contains("j:")) {
		    String jobName = oneReq.toLowerCase().replace("j:", "").split("-")[0];
		    int jobLevel = Integer.valueOf(oneReq.toLowerCase().replace("j:", "").split("-")[1]);
		    boolean found = false;
		    for (JobProgression oneJob : jPlayer.getJobProgression()) {
			if (oneJob.getJob().getName().equalsIgnoreCase(jobName))
			    found = true;
			if (oneJob.getJob().getName().equalsIgnoreCase(jobName) && oneJob.getLevel() != jobLevel) {
			    ok = false;
			    break;
			}
		    }
		    if (found == false)
			ok = false;
		}
		if (ok = false)
		    break;

		if (oneReq.toLowerCase().contains("p:")) {
		    if (!player.hasPermission(oneReq.replace(":p", ""))) {
			ok = false;
			break;
		    }
		}
	    }

	    if (ok) {
		for (String one : Condition.getPerform()) {
		    if (one.toLowerCase().contains("c:")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), one.replace("c:", "").replace("[name]", jPlayer.getUserName()));
		    }
		}
	    }
	}
    }

    /**
     * Performs command on level up
     * @param jPlayer
     * @param job
     * @param oldLevel
     */
    public void performCommandOnLevelUp(JobsPlayer jPlayer, Job job, int oldLevel) {
	int newLevel = oldLevel + 1;
	Player player = Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID());
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;
	for (JobCommands command : job.getCommands()) {
	    if (newLevel >= command.getLevelFrom() && newLevel <= command.getLevelUntil()) {
		String commandString = command.getCommand();
		commandString = commandString.replace("[player]", player.getName());
		commandString = commandString.replace("[oldlevel]", String.valueOf(oldLevel));
		commandString = commandString.replace("[newlevel]", String.valueOf(newLevel));
		commandString = commandString.replace("[jobname]", job.getName());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
	    }
	}
    }

    /**
     * Get job exp boost
     * @param player
     * @param job
     * @return double of boost
     */
    public Double GetExpBoost(Player dude, Job job) {
	Double ExpBoost = 1.0;
	if (dude == null || job.getName() == null)
	    return 1.0;
	if (Perm(dude, "jobs.boost." + job.getName() + ".exp") || Perm(dude, "jobs.boost." + job.getName() + ".both") || Perm(dude, "jobs.boost.all.both") || Perm(dude,
	    "jobs.boost.all.exp")) {
	    ExpBoost = ConfigManager.getJobsConfiguration().BoostExp;
	}

	return ExpBoost;
    }

    /**
     * Get max jobs
     * @param player
     * @return True if he have permission
     */
    public boolean getJobsLimit(Player player, Short currentCount) {

	if (Perm(player, "jobs.max.*"))
	    return true;

	short count = (short) ConfigManager.getJobsConfiguration().getMaxJobs();
	for (short ctr = 0; ctr < 30; ctr++) {
	    if (Perm(player, "jobs.max." + ctr))
		count = ctr;
	    if (count > currentCount)
		return true;
	}
	return false;
    }

    private boolean Perm(Player player, String permission) {
	return player.isPermissionSet(permission);
    }

    /**
     * Get job money boost
     * @param player
     * @param job
     * @return double of boost
     */
    public Double GetMoneyBoost(Player dude, Job job) {
	Double MoneyBoost = 1.0;
	if (dude != null && job.getName() != null) {
	    if (Perm(dude, "jobs.boost." + job.getName() + ".money") || Perm(dude, "jobs.boost." + job.getName() + ".both") || Perm(dude, "jobs.boost.all.both") || Perm(
		dude, "jobs.boost.all.money")) {
		MoneyBoost = ConfigManager.getJobsConfiguration().BoostMoney;
	    }
	}
	return MoneyBoost;
    }

    /**
     * Perform reload
     */
    public void reload() {
	synchronized (players) {
	    for (JobsPlayer jPlayer : players.values()) {
		for (JobProgression progression : jPlayer.getJobProgression()) {
		    String jobName = progression.getJob().getName();
		    Job job = Jobs.getJob(jobName);
		    if (job != null) {
			progression.setJob(job);
		    }
		}
		if (jPlayer.isOnline()) {
		    jPlayer.reloadHonorific();
		    Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
		}
	    }
	}
    }
}
