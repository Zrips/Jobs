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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.ArchivedJobs;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobCommands;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.dao.JobsDAOData;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.economy.PointsData;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Debug;
import com.gamingmesh.jobs.stuff.PerformCommands;

public class PlayerManager {
//    private Map<String, JobsPlayer> players = Collections.synchronizedMap(new HashMap<String, JobsPlayer>());
    private ConcurrentHashMap<String, JobsPlayer> playersCache = new ConcurrentHashMap<String, JobsPlayer>();
    private ConcurrentHashMap<UUID, JobsPlayer> playersUUIDCache = new ConcurrentHashMap<UUID, JobsPlayer>();
    private ConcurrentHashMap<String, JobsPlayer> players = new ConcurrentHashMap<String, JobsPlayer>();
    private ConcurrentHashMap<UUID, JobsPlayer> playersUUID = new ConcurrentHashMap<UUID, JobsPlayer>();

    private PointsData PointsDatabase = new PointsData();
    private final String mobSpawnerMetadata = "jobsMobSpawner";

    private HashMap<UUID, PlayerInfo> PlayerUUIDMap = new HashMap<UUID, PlayerInfo>();
    private HashMap<Integer, PlayerInfo> PlayerIDMap = new HashMap<Integer, PlayerInfo>();
    private HashMap<String, PlayerInfo> PlayerNameMap = new HashMap<String, PlayerInfo>();
    Jobs plugin;

    public PlayerManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public PointsData getPointsData() {
	return this.PointsDatabase;
    }

    public int getMapSize() {
	return PlayerUUIDMap.size();
    }

    public void clearMaps() {
	PlayerUUIDMap.clear();
	PlayerIDMap.clear();
	PlayerNameMap.clear();
    }

    public void clearCache() {
	playersCache.clear();
	playersUUIDCache.clear();
	players.clear();
	playersUUID.clear();
    }

    public void addPlayerToMap(PlayerInfo info) {
	this.PlayerUUIDMap.put(info.getUuid(), info);
	this.PlayerIDMap.put(info.getID(), info);
	this.PlayerNameMap.put(info.getName().toLowerCase(), info);
    }

    public void addPlayerToCache(JobsPlayer jPlayer) {
	if (jPlayer.getUserName() != null)
	    this.playersCache.put(jPlayer.getUserName().toLowerCase(), jPlayer);
	if (jPlayer.getPlayerUUID() != null)
	    this.playersUUIDCache.put(jPlayer.getPlayerUUID(), jPlayer);
    }

    public void addPlayer(JobsPlayer jPlayer) {
	if (jPlayer.getUserName() != null)
	    this.players.put(jPlayer.getUserName().toLowerCase(), jPlayer);
	if (jPlayer.getPlayerUUID() != null)
	    this.playersUUID.put(jPlayer.getPlayerUUID(), jPlayer);
    }

    public JobsPlayer removePlayer(Player player) {
	if (player == null)
	    return null;
	this.players.remove(player.getName().toLowerCase());
	JobsPlayer jPlayer = this.playersUUID.remove(player.getUniqueId());
	return jPlayer;
    }

    public ConcurrentHashMap<UUID, JobsPlayer> getPlayersCache() {
	return this.playersUUIDCache;
    }

//    public ConcurrentHashMap<String, JobsPlayer> getPlayers() {
//	return this.players;
//    }

    public HashMap<UUID, PlayerInfo> getPlayersInfoUUIDMap() {
	return this.PlayerUUIDMap;
    }

    public int getPlayerId(String name) {
	PlayerInfo info = getPlayerInfo(name);
	return info == null ? -1 : info.getID();
    }

    public int getPlayerId(UUID uuid) {
	PlayerInfo info = PlayerUUIDMap.get(uuid);
	return info == null ? -1 : info.getID();
    }

    public PlayerInfo getPlayerInfo(String name) {
	return PlayerNameMap.get(name.toLowerCase());
    }

    public PlayerInfo getPlayerInfo(int id) {
	return PlayerIDMap.get(id);
    }

    public PlayerInfo getPlayerInfo(UUID uuid) {
	return PlayerUUIDMap.get(uuid);
    }

    /**
     * Handles join of new player
     * @param playername
     */
    public void playerJoin(Player player) {

	JobsPlayer jPlayer = this.playersUUIDCache.get(player.getUniqueId());

	if (jPlayer == null || Jobs.getGCManager().MultiServerCompatability()) {
	    jPlayer = Jobs.getJobsDAO().loadFromDao(player);
	    jPlayer.loadLogFromDao();
	}

	this.addPlayer(jPlayer);
	jPlayer.setPlayer(player);
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
	JobsPlayer jPlayer = this.getJobsPlayer(player);
	if (jPlayer == null)
	    return;
	if (Jobs.getGCManager().saveOnDisconnect()) {
	    jPlayer.onDisconnect();
	    jPlayer.save();
	} else {
	    jPlayer.onDisconnect();
	}
    }

    /**
     * Save all the information of all of the players in the game
     */
    public void saveAll() {
	/*
	 * Saving is a three step process to minimize synchronization locks when called asynchronously.
	 * 
	 * 1) Safely copy list for saving.
	 * 2) Perform save on all players on copied list.
	 * 3) Garbage collect the real list to remove any offline players with saved data
	 */
	ArrayList<JobsPlayer> list = new ArrayList<JobsPlayer>(this.players.values());

	for (JobsPlayer jPlayer : list) {
	    jPlayer.save();
	}

	Iterator<JobsPlayer> iter = this.players.values().iterator();
	while (iter.hasNext()) {
	    JobsPlayer jPlayer = iter.next();
	    if (!jPlayer.isOnline() && jPlayer.isSaved()) {
		iter.remove();
	    }
	}
    }

    /**
     * Save all the information of all of the players
     */
    public void convertChacheOfPlayers(boolean resetID) {
	int y = 0;
	int i = 0;
	int total = playersUUIDCache.size();
	for (Entry<UUID, JobsPlayer> one : playersUUIDCache.entrySet()) {
	    JobsPlayer jPlayer = one.getValue();
	    if (resetID)
		jPlayer.setUserId(-1);
	    JobsDAO dao = Jobs.getJobsDAO();
	    dao.updateSeen(jPlayer);
	    if (jPlayer.getUserId() == -1)
		continue;
	    for (JobProgression oneJ : jPlayer.getJobProgression()) {
		dao.insertJob(jPlayer, oneJ);
	    }
	    dao.saveLog(jPlayer);
	    dao.savePoints(jPlayer);
	    dao.recordPlayersLimits(jPlayer);
	    i++;
	    y++;
	    if (y >= 1000) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Saved " + i + "/" + total + " players data");
		y = 0;
	    }
	}
    }

    /**
     * Get the player job info for specific player
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(Player player) {
	return getJobsPlayer(player.getUniqueId());
    }

    public JobsPlayer getJobsPlayer(UUID uuid) {
	JobsPlayer jPlayer = this.playersUUID.get(uuid);
	if (jPlayer != null)
	    return jPlayer;
	return this.playersUUIDCache.get(uuid);
    }

    /**
     * Get the player job info for specific player
     * @param player name - the player name who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(String playerName) {
	JobsPlayer jPlayer = this.players.get(playerName.toLowerCase());
	if (jPlayer != null)
	    return jPlayer;
	return this.playersCache.get(playerName.toLowerCase());
    }

    /**
     * Get the player job info for specific player
     * @param archivedJobs 
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayerOffline(PlayerInfo info, List<JobsDAOData> jobs, PlayerPoints points, HashMap<String, Log> logs, ArchivedJobs archivedJobs, PaymentData limits) {

	if (info == null)
	    return null;

	if (info.getName() == null)
	    return null;

	JobsPlayer jPlayer = new JobsPlayer(info.getName(), null);
	jPlayer.setPlayerUUID(info.getUuid());
	jPlayer.setUserId(info.getID());
	jPlayer.setDoneQuests(info.getQuestsDone());

	if (jobs != null)
	    for (JobsDAOData jobdata : jobs) {
		if (Jobs.getJob(jobdata.getJobName()) == null)
		    continue;
		Job job = Jobs.getJob(jobdata.getJobName());
		if (job == null)
		    continue;
		JobProgression jobProgression = new JobProgression(job, jPlayer, jobdata.getLevel(), jobdata.getExperience());
		jPlayer.progression.add(jobProgression);
		jPlayer.reloadMaxExperience();
		jPlayer.reloadLimits();
	    }

	if (points != null) {
	    Jobs.getPlayerManager().getPointsData().addPlayer(jPlayer.getPlayerUUID(), points);
	} else {
	    Jobs.getPlayerManager().getPointsData().addPlayer(jPlayer.getPlayerUUID());
	}

	if (logs != null)
	    jPlayer.setLog(logs);

	if (limits != null)
	    jPlayer.setPaymentLimit(limits);

	if (archivedJobs != null) {
	    ArchivedJobs aj = new ArchivedJobs();
	    for (JobProgression one : archivedJobs.getArchivedJobs()) {
		JobProgression jp = new JobProgression(one.getJob(), jPlayer, one.getLevel(), one.getExperience());
		jp.reloadMaxExperience();
		if (one.getLeftOn() != null && one.getLeftOn() != 0L)
		    jp.setLeftOn(one.getLeftOn());
		aj.addArchivedJob(jp);
	    }
	    jPlayer.setArchivedJobs(aj);
	}

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
	if (!jPlayer.joinJob(job))
	    return;

	// JobsJoin event
	JobsJoinEvent jobsjoinevent = new JobsJoinEvent(jPlayer, job);
	Bukkit.getServer().getPluginManager().callEvent(jobsjoinevent);
	// If event is canceled, dont do anything
	if (jobsjoinevent.isCancelled())
	    return;

	Jobs.getJobsDAO().joinJob(jPlayer, jPlayer.getJobProgression(job));
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
    public boolean leaveJob(JobsPlayer jPlayer, Job job) {
//	synchronized (jPlayer.saveLock) {
	if (!jPlayer.isInJob(job))
	    return false;

	// JobsJoin event
	JobsLeaveEvent jobsleaveevent = new JobsLeaveEvent(jPlayer, job);
	Bukkit.getServer().getPluginManager().callEvent(jobsleaveevent);
	// If event is canceled, dont do anything
	if (jobsleaveevent.isCancelled())
	    return false;

	Jobs.getJobsDAO().recordToArchive(jPlayer, job);
	// let the user leave the job
	if (!jPlayer.leaveJob(job))
	    return false;

	if (!Jobs.getJobsDAO().quitJob(jPlayer, job))
	    return false;
	PerformCommands.PerformCommandsOnLeave(jPlayer, job);
	Jobs.leaveSlot(job);

	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
	job.updateTotalPlayers();
	return true;
//	}
    }

    /**
     * Causes player to leave all their jobs
     * @param jPlayer
     */
    public void leaveAllJobs(JobsPlayer jPlayer) {
	List<JobProgression> jobs = new ArrayList<JobProgression>();
	jobs.addAll(jPlayer.getJobProgression());
	for (JobProgression job : jobs) {
	    leaveJob(jPlayer, job.getJob());
	}
	jPlayer.leaveAllJobs();
    }

    /**
     * Transfers player job
     * @param jPlayer
     * @param oldjob - the old job
     * @param newjob - the new job
     */
    public boolean transferJob(JobsPlayer jPlayer, Job oldjob, Job newjob) {
//	synchronized (jPlayer.saveLock) {
	if (!jPlayer.transferJob(oldjob, newjob))
	    return false;

	JobsDAO dao = Jobs.getJobsDAO();
	if (!dao.quitJob(jPlayer, oldjob))
	    return false;
	oldjob.updateTotalPlayers();
	dao.joinJob(jPlayer, jPlayer.getJobProgression(newjob));
	newjob.updateTotalPlayers();
	jPlayer.save();
//	}
	return true;
    }

    /**
     * Promotes player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to promote
     */
    public void promoteJob(JobsPlayer jPlayer, Job job, int levels) {
//	synchronized (jPlayer.saveLock) {
	jPlayer.promoteJob(job, levels);
	jPlayer.save();

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
	jPlayer.save();
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

	jPlayer.save();
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

	jPlayer.save();
	Jobs.getSignUtil().SignUpdate(job.getName());
	Jobs.getSignUtil().SignUpdate("gtoplist");
//	}
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
	JobsLevelUpEvent levelUpEvent = new JobsLevelUpEvent(
	    jPlayer,
	    job.getName(),
	    prog.getLevel(),
	    Jobs.gettitleManager().getTitle(oldLevel, prog.getJob().getName()),
	    Jobs.gettitleManager().getTitle(prog.getLevel(), prog.getJob().getName()),
	    Jobs.getGCManager().SoundLevelupSound.toUpperCase(),
	    Jobs.getGCManager().SoundLevelupVolume,
	    Jobs.getGCManager().SoundLevelupPitch,
	    Jobs.getGCManager().SoundTitleChangeSound.toUpperCase(),
	    Jobs.getGCManager().SoundTitleChangeVolume,
	    Jobs.getGCManager().SoundTitleChangePitch);
	Bukkit.getServer().getPluginManager().callEvent(levelUpEvent);
	// If event is canceled, dont do anything
	if (levelUpEvent.isCancelled())
	    return;

	// If it fails, we can ignore it
	try {
	    if (Jobs.getGCManager().SoundLevelupUse) {
		Sound sound = levelUpEvent.getSound();
		if (sound != null && player != null && player.getLocation() != null)
		    player.getWorld().playSound(player.getLocation(), sound, levelUpEvent.getSoundVolume(), levelUpEvent.getSoundPitch());
		else
		    Bukkit.getConsoleSender().sendMessage("[Jobs] Cant find sound by name: " + levelUpEvent.getTitleChangeSound().name() + ". Please update it");
	    }
	} catch (Exception e) {
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

	    // If it fails, we can ignore it
	    try {
		if (Jobs.getGCManager().SoundTitleChangeUse) {
		    Sound sound = levelUpEvent.getTitleChangeSound();
		    if (sound != null && player != null && player.getLocation() != null)
			player.getWorld().playSound(player.getLocation(), sound, levelUpEvent.getTitleChangeVolume(),
			    levelUpEvent.getTitleChangePitch());
		    else
			Bukkit.getConsoleSender().sendMessage("[Jobs] Cant find sound by name: " + levelUpEvent.getTitleChangeSound().name() + ". Please update it");
		}
	    } catch (Exception e) {
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
    public boolean getJobsLimit(JobsPlayer jPlayer, Short currentCount) {

	Double max = Jobs.getPermissionManager().getMaxPermission(jPlayer, "jobs.max");

	max = max == null ? Jobs.getGCManager().getMaxJobs() : max;

	if (max > currentCount)
	    return true;

	// Using new system to get max value from permission

//	if (Perm.hasPermission(player, "jobs.max.*"))
//	    return true;
//
//	int totalJobs = Jobs.getJobs().size() + 1;
//
//	short count = (short) Jobs.getGCManager().getMaxJobs();
//	for (short ctr = 0; ctr < totalJobs; ctr++) {
//	    if (Perm.hasPermission(player, "jobs.max." + ctr))
//		count = ctr;
//	    if (count > currentCount)
//		return true;
//	}
	return false;
    }

    public BoostMultiplier getBoost(JobsPlayer player, Job job) {
	return getBoost(player, job, false);
    }

    public BoostMultiplier getBoost(JobsPlayer player, Job job, boolean force) {
	BoostMultiplier b = new BoostMultiplier();
	for (CurrencyType one : CurrencyType.values()) {
	    b.add(one, getBoost(player, job, one, force));
	}
	return b;
    }

    public double getBoost(JobsPlayer player, Job job, CurrencyType type) {
	return getBoost(player, job, type, false);
    }

    public double getBoost(JobsPlayer player, Job job, CurrencyType type, boolean force) {
	return player.getBoost(job.getName(), type, force);
    }

    /**
     * Perform reload
     */
    public void reload() {
	for (JobsPlayer jPlayer : this.players.values()) {
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
	BoostMultiplier data = new BoostMultiplier();
	if (player == null)
	    return data;
	if (prog == null)
	    return data;
	ItemStack iih = Jobs.getNms().getItemInMainHand(player);
	data = Jobs.getPlayerManager().getItemBoost(prog, iih);
	for (ItemStack OneArmor : player.getInventory().getArmorContents()) {
	    if (OneArmor == null || OneArmor.getType() == Material.AIR)
		continue;
	    BoostMultiplier armorboost = Jobs.getPlayerManager().getItemBoost(prog, OneArmor);
	    data.add(armorboost);
	}
	return data;
    }

    @SuppressWarnings("deprecation")
    public BoostMultiplier getItemBoost(Job prog, ItemStack item) {
	BoostMultiplier bonus = new BoostMultiplier();
	if (prog.getItems().isEmpty())
	    return bonus;
	if (item == null)
	    return bonus;

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
		if (oneItem.getEnchants().containsKey(oneE.getKey())) {
		    if (oneItem.getEnchants().get(oneE.getKey()) < oneE.getValue()) {
			continue main;
		    }
		} else
		    continue main;
	    }

	    return oneItem.getBoost();
	}

	return bonus;
    }

    public enum BoostOf {
	McMMO, PetPay, NearSpawner, Permission, Global, Dynamic, Item, Area
    }

    public Boost getFinalBonus(JobsPlayer player, Job prog, boolean force, boolean getall) {
	return getFinalBonus(player, prog, null, null, force, getall);
    }

    public Boost getFinalBonus(JobsPlayer player, Job prog, boolean force) {
	return getFinalBonus(player, prog, null, null, force, false);
    }

    public Boost getFinalBonus(JobsPlayer player, Job prog) {
	return getFinalBonus(player, prog, null, null, false, false);
    }

    public Boost getFinalBonus(JobsPlayer player, Job prog, Entity ent, LivingEntity victim) {
	return getFinalBonus(player, prog, ent, victim, false, false);
    }

    public Boost getFinalBonus(JobsPlayer player, Job prog, Entity ent, LivingEntity victim, boolean force, boolean getall) {
	Boost boost = new Boost();

	if (player == null || prog == null)
	    return boost;

	if (Jobs.getMcMMOlistener().mcMMOPresent)
	    boost.add(BoostOf.McMMO, new BoostMultiplier().add(Jobs.getMcMMOlistener().getMultiplier(player.getPlayer())));

	if (ent != null && (ent instanceof Tameable)) {
	    Tameable t = (Tameable) ent;
	    if (t.isTamed() && t.getOwner() instanceof Player) {
		Double amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.petpay");
		if (amount != null)
		    boost.add(BoostOf.PetPay, new BoostMultiplier().add(amount));
	    }
	}

	if (ent != null && Jobs.getMyPetManager().isMyPet(ent)) {
	    Double amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.petpay");
	    if (amount != null)
		boost.add(BoostOf.PetPay, new BoostMultiplier().add(amount));
	}

	if (victim != null && victim.hasMetadata(this.getMobSpawnerMetadata())) {
	    Double amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.nearspawner");
	    if (amount != null)
		boost.add(BoostOf.NearSpawner, new BoostMultiplier().add(amount));
	}

	if (getall) {
	    Double amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.petpay", force);
	    if (amount != null)
		boost.add(BoostOf.PetPay, new BoostMultiplier().add(amount));
	    amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.nearspawner", force);
	    if (amount != null)
		boost.add(BoostOf.NearSpawner, new BoostMultiplier().add(amount));
	}

	boost.add(BoostOf.Permission, Jobs.getPlayerManager().getBoost(player, prog, force));
	boost.add(BoostOf.Global, prog.getBoost());
	if (Jobs.getGCManager().useDynamicPayment)
	    boost.add(BoostOf.Dynamic, new BoostMultiplier().add(prog.getBonus()));
	boost.add(BoostOf.Item, Jobs.getPlayerManager().getItemBoost(player.getPlayer(), prog));
	boost.add(BoostOf.Area, new BoostMultiplier().add(Jobs.getRestrictedAreaManager().getRestrictedMultiplier(player.getPlayer())));

	return boost;
    }

    public void AutoJoinJobs(final Player player) {
	if (player == null)
	    return;
	if (player.isOp())
	    return;
	if (!Jobs.getGCManager().AutoJobJoinUse)
	    return;
	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
	    @Override
	    public void run() {
		if (!player.isOnline())
		    return;
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
		if (jPlayer == null)
		    return;
		if (player.hasPermission("jobs.*"))
		    return;
		int confMaxJobs = Jobs.getGCManager().getMaxJobs();
		for (Job one : Jobs.getJobs()) {
		    if (one.getMaxSlots() != null && Jobs.getUsedSlots(one) >= one.getMaxSlots())
			continue;
		    short PlayerMaxJobs = (short) jPlayer.getJobProgression().size();
		    if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !Jobs.getPlayerManager().getJobsLimit(jPlayer, PlayerMaxJobs))
			break;
		    if (jPlayer.isInJob(one))
			continue;
		    if (player.hasPermission("jobs.autojoin." + one.getName().toLowerCase()))
			Jobs.getPlayerManager().joinJob(jPlayer, one);
		}
		return;
	    }
	}, Jobs.getGCManager().AutoJobJoinDelay * 20L);
    }

    public String getMobSpawnerMetadata() {
	return mobSpawnerMetadata;
    }
}
