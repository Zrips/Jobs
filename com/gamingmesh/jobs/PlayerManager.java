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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobCommands;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.dao.JobsDAOData;
import com.gamingmesh.jobs.economy.PointsData;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.PerformCommands;
import com.gamingmesh.jobs.stuff.Perm;

public class PlayerManager {
//    private Map<String, JobsPlayer> players = Collections.synchronizedMap(new HashMap<String, JobsPlayer>());
    private ConcurrentHashMap<String, JobsPlayer> playersCache = new ConcurrentHashMap<String, JobsPlayer>();
    private ConcurrentHashMap<String, JobsPlayer> players = new ConcurrentHashMap<String, JobsPlayer>();
    private PointsData PointsDatabase = new PointsData();

    private HashMap<String, PlayerInfo> PlayerMap = new HashMap<String, PlayerInfo>();
    JobsPlugin plugin;

    public PlayerManager(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    public PointsData getPointsData() {
	return PointsDatabase;
    }

    public HashMap<String, PlayerInfo> getPlayerMap() {
	return PlayerMap;
    }

    public ConcurrentHashMap<String, JobsPlayer> getPlayersCache() {
	return playersCache;
    }

    public ConcurrentHashMap<String, JobsPlayer> getPlayers() {
	return players;
    }

    public int getPlayerIdByName(String name) {
	for (Entry<String, PlayerInfo> one : PlayerMap.entrySet()) {
	    if (one.getValue().getName() == null)
		continue;
	    if (one.getValue().getName().equalsIgnoreCase(name))
		return one.getValue().getID();
	}
	return -1;
    }

    public Entry<String, PlayerInfo> getPlayerInfoByName(String name) {
	for (Entry<String, PlayerInfo> one : PlayerMap.entrySet()) {
	    if (one.getValue().getName() == null)
		continue;
	    if (one.getValue().getName().equalsIgnoreCase(name))
		return one;
	}
	return null;
    }

    public Entry<String, PlayerInfo> getPlayerInfoById(int id) {
	for (Entry<String, PlayerInfo> one : PlayerMap.entrySet()) {
	    if (one.getValue().getName() == null)
		continue;
	    if (one.getValue().getID() == id)
		return one;
	}
	return null;
    }

    /**
     * Handles join of new player
     * @param playername
     */
    public void playerJoin(Player player) {
	JobsPlayer jPlayer = playersCache.get(player.getName().toLowerCase());
	if (jPlayer == null) {
	    jPlayer = JobsPlayer.loadFromDao(Jobs.getJobsDAO(), player);
	    if (player.hasPlayedBefore())
		JobsPlayer.loadLogFromDao(jPlayer);
	    playersCache.put(player.getName().toLowerCase(), jPlayer);
	}

	players.put(player.getName().toLowerCase(), jPlayer);

	AutoJoinJobs(player);
	jPlayer.onConnect();
	jPlayer.reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	return;
    }

    /**
     * Handles player quit
     * @param playername
     */
    public void playerQuit(Player player) {
	if (Jobs.getGCManager().saveOnDisconnect()) {
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
	list = new ArrayList<JobsPlayer>(players.values());

	for (JobsPlayer jPlayer : list) {
	    jPlayer.save(dao);
	}

	Iterator<JobsPlayer> iter = players.values().iterator();
	while (iter.hasNext()) {
	    JobsPlayer jPlayer = iter.next();
	    if (!jPlayer.isOnline() && jPlayer.isSaved()) {
		iter.remove();
	    }
	}
    }

    /**
     * Get the player job info for specific player
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(Player player) {
	return playersCache.get(player.getName().toLowerCase());
    }

    /**
     * Get the player job info for specific player
     * @param player name - the player name who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(String playerName) {
	return playersCache.get(playerName.toLowerCase());
    }

    public JobsPlayer getJobsPlayerOffline(OfflinePlayer player) {
	return getJobsPlayerOffline(player.getName());
    }

    /**
     * Get the player job info for specific player
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayerOffline(String playerName) {
	JobsPlayer jPlayer = playersCache.get(playerName.toLowerCase());
	if (jPlayer != null)
	    return jPlayer;

	Entry<String, PlayerInfo> info = getPlayerInfoByName(playerName);

	if (info == null)
	    return null;

	if (info.getValue().getName() == null)
	    return null;

	jPlayer = new JobsPlayer(info.getValue().getName(), null);
	jPlayer.setPlayerUUID(UUID.fromString(info.getKey()));
	jPlayer.setUserId(info.getValue().getID());

	List<JobsDAOData> list = Jobs.getJobsDAO().getAllJobs(info.getValue().getName(), UUID.fromString(info.getKey()));
	for (JobsDAOData jobdata : list) {
	    if (Jobs.getJob(jobdata.getJobName()) == null)
		continue;
	    Job job = Jobs.getJob(jobdata.getJobName());
	    if (job == null)
		continue;
	    JobProgression jobProgression = new JobProgression(job, jPlayer, jobdata.getLevel(), jobdata.getExperience(), -1, -1, -1);
	    jPlayer.progression.add(jobProgression);
	    jPlayer.reloadMaxExperience();
	    jPlayer.reloadLimits();
	}

	Jobs.getJobsDAO().loadPoints(jPlayer);

	JobsPlayer.loadLogFromDao(jPlayer);
	return jPlayer;
    }

    /**
     * Causes player to join their job
     * @param jPlayer
     * @param job
     */
    public void joinJob(JobsPlayer jPlayer, Job job) {
//	synchronized (jPlayer.saveLock) {
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
	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
	job.updateTotalPlayers();
//	}
    }

    /**
     * Causes player to leave their job
     * @param jPlayer
     * @param job
     */
    public void leaveJob(JobsPlayer jPlayer, Job job) {
//	synchronized (jPlayer.saveLock) {
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

	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
	job.updateTotalPlayers();
//	}
    }

    /**
     * Causes player to leave all their jobs
     * @param jPlayer
     */
    public void leaveAllJobs(JobsPlayer jPlayer) {
//	synchronized (jPlayer.saveLock) {
	for (JobProgression job : jPlayer.getJobProgression()) {
	    Jobs.getJobsDAO().quitJob(jPlayer, job.getJob());
	    Jobs.getJobsDAO().recordToArchive(jPlayer, job.getJob());
	    PerformCommands.PerformCommandsOnLeave(jPlayer, job.getJob());
	    Jobs.leaveSlot(job.getJob());

	    Jobs.getSignUtil().SignUpdate(job.getJob().getName());
	    Jobs.getSignUtil().SignUpdate("gtoplist");
	    job.getJob().updateTotalPlayers();
	}

	jPlayer.leaveAllJobs();
//	}
    }

    /**
     * Transfers player job
     * @param jPlayer
     * @param oldjob - the old job
     * @param newjob - the new job
     */
    public void transferJob(JobsPlayer jPlayer, Job oldjob, Job newjob) {
//	synchronized (jPlayer.saveLock) {
	if (!jPlayer.transferJob(oldjob, newjob, jPlayer))
	    return;

	JobsDAO dao = Jobs.getJobsDAO();
	dao.quitJob(jPlayer, oldjob);
	oldjob.updateTotalPlayers();
	dao.joinJob(jPlayer, newjob);
	newjob.updateTotalPlayers();
	jPlayer.save(dao);
//	}
    }

    /**
     * Promotes player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to promote
     */
    public void promoteJob(JobsPlayer jPlayer, Job job, int levels) {
//	synchronized (jPlayer.saveLock) {
	jPlayer.promoteJob(job, levels, jPlayer);
	jPlayer.save(Jobs.getJobsDAO());

	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
//	}
    }

    /**
     * Demote player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to demote
     */
    public void demoteJob(JobsPlayer jPlayer, Job job, int levels) {
//	synchronized (jPlayer.saveLock) {
	jPlayer.demoteJob(job, levels);
	jPlayer.save(Jobs.getJobsDAO());
	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
//	}
    }

    /**
     * Adds experience to the player
     * @param jPlayer
     * @param job - the job
     * @param experience - experience gained
     */
    public void addExperience(JobsPlayer jPlayer, Job job, double experience) {
//	synchronized (jPlayer.saveLock) {
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;
	int oldLevel = prog.getLevel();
	if (prog.addExperience(experience))
	    performLevelUp(jPlayer, job, oldLevel);

	jPlayer.save(Jobs.getJobsDAO());
	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
//	}
    }

    /**
     * Removes experience to the player
     * @param jPlayer
     * @param job - the job
     * @param experience - experience gained
     */
    public void removeExperience(JobsPlayer jPlayer, Job job, double experience) {
//	synchronized (jPlayer.saveLock) {
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;
	prog.addExperience(-experience);

	jPlayer.save(Jobs.getJobsDAO());
	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
//	}
    }

    private Sound getSound(String soundName) {
	for (Sound one : Sound.values()) {
	    if (one.name().equalsIgnoreCase(soundName))
		return one;
	}
	return null;
    }

    /**
     * Broadcasts level up about a player
     * @param jPlayer
     * @param job
     * @param oldLevel
     */
    public void performLevelUp(JobsPlayer jPlayer, Job job, int oldLevel) {

	Player player = jPlayer.getPlayer();
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;

	// LevelUp event
	JobsLevelUpEvent levelUpEvent = new JobsLevelUpEvent(jPlayer, job.getName(), prog.getLevel(), Jobs.gettitleManager().getTitleForLevel(oldLevel, job
	    .getName()), Jobs.gettitleManager().getTitleForLevel(prog.getLevel(), job.getName()), Jobs.getGCManager().SoundLevelupSound
		.toUpperCase(), Jobs.getGCManager().SoundLevelupVolume, Jobs.getGCManager().SoundLevelupPitch, Jobs.getGCManager().SoundTitleChangeSound.toUpperCase(),
	    Jobs.getGCManager().SoundTitleChangeVolume, Jobs.getGCManager().SoundTitleChangePitch);
	Bukkit.getServer().getPluginManager().callEvent(levelUpEvent);
	// If event is canceled, dont do anything
	if (levelUpEvent.isCancelled())
	    return;

	if (Jobs.getGCManager().SoundLevelupUse) {
	    Sound sound = getSound(levelUpEvent.getSoundName());
	    if (sound != null)
		player.getWorld().playSound(player.getLocation(), sound, levelUpEvent.getSoundVolume(), levelUpEvent.getSoundPitch());
	    else
		Bukkit.getConsoleSender().sendMessage("[Jobs] Cant find sound by name: " + levelUpEvent.getTitleChangeSoundName() + ". Please update it");
	}

	String message;
	if (Jobs.getGCManager().isBroadcastingLevelups()) {
	    message = Jobs.getLanguage().getMessage("message.levelup.broadcast");
	} else {
	    message = Jobs.getLanguage().getMessage("message.levelup.nobroadcast");
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
	    if (Jobs.getGCManager().isBroadcastingLevelups()) {
		if (Jobs.getGCManager().BroadcastingLevelUpLevels.contains(oldLevel + 1) || Jobs.getGCManager().BroadcastingLevelUpLevels.contains(0))
		    Bukkit.getServer().broadcastMessage(line);
	    } else if (player != null) {
		if (Jobs.getGCManager().LevelChangeActionBar)
		    Jobs.getActionBar().send(player, line);
		if (Jobs.getGCManager().LevelChangeChat)
		    player.sendMessage(line);
	    }
	}

	if (levelUpEvent.getNewTitle() != null && !levelUpEvent.getNewTitle().equals(levelUpEvent.getOldTitle())) {

	    if (Jobs.getGCManager().SoundTitleChangeUse) {
		Sound sound = getSound(levelUpEvent.getTitleChangeSoundName());
		if (sound != null)
		    player.getWorld().playSound(player.getLocation(), sound, levelUpEvent.getTitleChangeVolume(),
			levelUpEvent.getTitleChangePitch());
		else
		    Bukkit.getConsoleSender().sendMessage("[Jobs] Cant find sound by name: " + levelUpEvent.getTitleChangeSoundName() + ". Please update it");
	    }
	    // user would skill up
	    if (Jobs.getGCManager().isBroadcastingSkillups()) {
		message = Jobs.getLanguage().getMessage("message.skillup.broadcast");
	    } else {
		message = Jobs.getLanguage().getMessage("message.skillup.nobroadcast");
	    }
	    if (player != null) {
		message = message.replace("%playername%", player.getDisplayName());
	    } else {
		message = message.replace("%playername%", jPlayer.getUserName());
	    }
	    message = message.replace("%titlename%", levelUpEvent.getNewTitleColor() + levelUpEvent.getNewTitleName() + ChatColor.WHITE);
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
	    for (String line : message.split("\n")) {
		if (Jobs.getGCManager().isBroadcastingLevelups()) {
		    Bukkit.getServer().broadcastMessage(line);
		} else if (player != null) {
		    if (Jobs.getGCManager().TitleChangeActionBar)
			Jobs.getActionBar().send(player, line);
		    if (Jobs.getGCManager().TitleChangeChat)
			player.sendMessage(line);
		}
	    }
	}
	jPlayer.reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	performCommandOnLevelUp(jPlayer, prog.getJob(), oldLevel);
	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
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
     * Get max jobs
     * @param player
     * @return True if he have permission
     */
    public boolean getJobsLimit(Player player, Short currentCount) {

	if (Perm.hasPermission(player, "jobs.max.*"))
	    return true;

	int totalJobs = Jobs.getJobs().size() + 5;

	short count = (short) Jobs.getGCManager().getMaxJobs();
	for (short ctr = 0; ctr < totalJobs; ctr++) {
	    if (Perm.hasPermission(player, "jobs.max." + ctr))
		count = ctr;
	    if (count > currentCount)
		return true;
	}
	return false;
    }

    /**
     * Get job money boost
     * @param player
     * @param job
     * @return double of boost
     */
    public Double GetMoneyBoost(Player player, Job job) {
	Double Boost = 1.0;
	if (player != null && job.getName() != null) {
	    if (Perm.hasPermission(player, "jobs.boost." + job.getName() + ".money") ||
		Perm.hasPermission(player, "jobs.boost." + job.getName() + ".all") ||
		Perm.hasPermission(player, "jobs.boost.all.all") ||
		Perm.hasPermission(player, "jobs.boost.all.money")) {
		Boost = Jobs.getGCManager().BoostMoney;
	    }
	}
	return Boost;
    }

    public double GetMoneyBoostInPerc(Player player, Job job) {
	double Boost = GetMoneyBoost(player, job) * 100.0 - 100.0;
	return Boost;
    }

    /**
     * Get job point boost
     * @param player
     * @param job
     * @return double of boost
     */
    public Double GetPointBoost(Player player, Job job) {
	Double Boost = 1.0;
	if (player != null && job.getName() != null) {
	    if (Perm.hasPermission(player, "jobs.boost." + job.getName() + ".points") ||
		Perm.hasPermission(player, "jobs.boost." + job.getName() + ".all") ||
		Perm.hasPermission(player, "jobs.boost.all.all") ||
		Perm.hasPermission(player, "jobs.boost.all.points")) {
		Boost = Jobs.getGCManager().BoostPoints;
	    }
	}
	return Boost;
    }

    public double GetPointBoostInPerc(Player player, Job job) {
	double Boost = GetPointBoost(player, job) * 100.0 - 100.0;
	return Boost;
    }

    /**
     * Get job exp boost
     * @param player
     * @param job
     * @return double of boost
     */
    public Double GetExpBoost(Player player, Job job) {
	Double Boost = 1.0;
	if (player == null || job.getName() == null)
	    return 1.0;
	if (Perm.hasPermission(player, "jobs.boost." + job.getName() + ".exp") ||
	    Perm.hasPermission(player, "jobs.boost." + job.getName() + ".all") ||
	    Perm.hasPermission(player, "jobs.boost.all.all") ||
	    Perm.hasPermission(player, "jobs.boost.all.exp")) {
	    Boost = Jobs.getGCManager().BoostExp;
	}

	return Boost;
    }

    public double GetExpBoostInPerc(Player player, Job job) {
	double Boost = GetExpBoost(player, job) * 100.0 - 100.0;
	return Boost;
    }

    /**
     * Perform reload
     */
    public void reload() {
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
		jPlayer.reloadLimits();
		Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	    }
	}
    }

    public BoostMultiplier getItemBoost(Player player, Job prog) {
	BoostMultiplier data = new BoostMultiplier(1D, 1D, 1D);
	if (player == null)
	    return data;

	ItemStack iih = Jobs.getNms().getItemInMainHand(player);

	BoostMultiplier itemboost = Jobs.getPlayerManager().getItemBoost(prog, iih);

	data = new BoostMultiplier(data.getMoneyBoost() + itemboost.getMoneyBoost(),
	    data.getPointsBoost() + itemboost.getPointsBoost(),
	    data.getExpBoost() + itemboost.getExpBoost());

	for (ItemStack OneArmor : player.getInventory().getArmorContents()) {
	    BoostMultiplier armorboost = Jobs.getPlayerManager().getItemBoost(prog, OneArmor);
	    data = new BoostMultiplier(data.getMoneyBoost() + armorboost.getMoneyBoost(),
		data.getPointsBoost() + armorboost.getPointsBoost(),
		data.getExpBoost() + armorboost.getExpBoost());
	}

	return data;
    }

    @SuppressWarnings("deprecation")
    public BoostMultiplier getItemBoost(Job prog, ItemStack item) {
	if (item == null)
	    return new BoostMultiplier(0D, 0D, 0D);

	ItemMeta meta = item.getItemMeta();
	String name = null;
	List<String> lore = new ArrayList<String>();

	if (item.hasItemMeta()) {
	    if (meta.hasDisplayName())
		name = meta.getDisplayName();
	    if (meta.hasLore())
		lore = meta.getLore();
	}

	Map<Enchantment, Integer> enchants = item.getEnchantments();

	main: for (JobItems oneItem : prog.getItems()) {
	    if (oneItem.getId() != item.getTypeId())
		continue;

	    if (oneItem.getName() != null && name != null)
		if (!org.bukkit.ChatColor.translateAlternateColorCodes('&', oneItem.getName()).equalsIgnoreCase(name))
		    continue;

	    for (String onelore : oneItem.getLore()) {
		if (lore.size() == 0 || !lore.contains(onelore))
		    continue main;
	    }

	    for (Entry<Enchantment, Integer> oneE : enchants.entrySet()) {
		if (oneItem.getenchants().containsKey(oneE.getKey())) {
		    if (oneItem.getenchants().get(oneE.getKey()) < oneE.getValue()) {
			continue main;
		    }
		} else
		    continue main;
	    }

	    return new BoostMultiplier(oneItem.getMoneyBoost() - 1D, oneItem.getPointBoost() - 1D, oneItem.getExpBoost() - 1D);
	}

	return new BoostMultiplier(0D, 0D, 0D);
    }

    public BoostMultiplier getFinalBonus(Player player, Job prog) {
	BoostMultiplier multiplier = new BoostMultiplier(0D, 0D, 0D);
	if (player == null || prog == null)
	    return multiplier;

	double PMoneyBoost = Jobs.getPlayerManager().GetMoneyBoostInPerc(player, prog);
	PMoneyBoost = (int) (PMoneyBoost * 100D) / 100D;
	double PPointBoost = Jobs.getPlayerManager().GetPointBoostInPerc(player, prog);
	PPointBoost = (int) (PPointBoost * 100D) / 100D;
	double PExpBoost = Jobs.getPlayerManager().GetExpBoostInPerc(player, prog);
	PExpBoost = (int) (PExpBoost * 100D) / 100D;

	double GMoneyBoost = prog.getMoneyBoost() * 100.0 - 100.0;
	GMoneyBoost = (int) (GMoneyBoost * 100D) / 100D;
	double GPointBoost = prog.getPointBoost() * 100.0 - 100.0;
	GPointBoost = (int) (GPointBoost * 100D) / 100D;
	double GExpBoost = prog.getExpBoost() * 100.0 - 100.0;
	GExpBoost = (int) (GExpBoost * 100D) / 100D;

	double DBoost = (int) (prog.getBonus() * 100D) / 100D;
	if (!Jobs.getGCManager().useDynamicPayment)
	    DBoost = 0.0;

	BoostMultiplier itemboost = Jobs.getPlayerManager().getItemBoost(player, prog);

	double IMoneyBoost = itemboost.getMoneyBoost() * 100.0 - 100.0;
	IMoneyBoost = (int) (IMoneyBoost * 100D) / 100D;
	double IPointBoost = itemboost.getPointsBoost() * 100.0 - 100.0;
	IPointBoost = (int) (IPointBoost * 100D) / 100D;
	double IExpBoost = itemboost.getExpBoost() * 100.0 - 100.0;
	IExpBoost = (int) (IExpBoost * 100D) / 100D;

	double RBoost = Jobs.getRestrictedAreaManager().getRestrictedMultiplier(player) * 100.0 - 100.0;
	RBoost = (int) (RBoost * 100D) / 100D;

	double Fmoney = (int) ((IMoneyBoost + DBoost + GMoneyBoost + PMoneyBoost + RBoost) * 100) / 100D;
	double Fpoints = (int) ((IPointBoost + DBoost + GPointBoost + PPointBoost + RBoost) * 100) / 100D;
	double Fexp = (int) ((IExpBoost + DBoost + GExpBoost + PExpBoost + RBoost) * 100) / 100D;

	return new BoostMultiplier(Fmoney, Fpoints, Fexp);
    }

    public void AutoJoinJobs(final Player player) {
	if (player == null)
	    return;
	if (player.isOp())
	    return;
	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    public void run() {
		if (!player.isOnline())
		    return;
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
		if (jPlayer == null)
		    return;
		int confMaxJobs = Jobs.getGCManager().getMaxJobs();
		for (Job one : Jobs.getJobs()) {

		    if (one.getMaxSlots() != null && Jobs.getUsedSlots(one) >= one.getMaxSlots())
			continue;

		    short PlayerMaxJobs = (short) jPlayer.getJobProgression().size();
		    if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !Jobs.getPlayerManager().getJobsLimit(player, PlayerMaxJobs))
			break;

		    if (jPlayer.isInJob(one))
			continue;

		    if (Perm.hasPermission(player, "jobs.autojoin." + one.getName().toLowerCase()))
			Jobs.getPlayerManager().joinJob(jPlayer, one);
		}
		return;
	    }
	}, Jobs.getGCManager().AutoJobJoinDelay * 20L);
    }
}
