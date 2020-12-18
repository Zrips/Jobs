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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.CMILib.ActionBarManager;
import com.gamingmesh.jobs.CMILib.CMIReflections;
import com.gamingmesh.jobs.CMILib.TitleMessageManager;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.ArchivedJobs;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.ItemBonusCache;
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
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.stuff.PerformCommands;
import com.gamingmesh.jobs.stuff.Util;

public class PlayerManager {

    private final ConcurrentHashMap<String, JobsPlayer> playersCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, JobsPlayer> playersUUIDCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, JobsPlayer> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, JobsPlayer> playersUUID = new ConcurrentHashMap<>();

    private final String mobSpawnerMetadata = "jobsMobSpawner";

    private final HashMap<UUID, PlayerInfo> PlayerUUIDMap = new HashMap<>();
    private final HashMap<Integer, PlayerInfo> PlayerIDMap = new HashMap<>();
    private final HashMap<String, PlayerInfo> PlayerNameMap = new HashMap<>();

    /**
     * @deprecated Use {@link Jobs#getPointsData} instead
     * @return {@link com.gamingmesh.jobs.economy.PointsData}
     */
    @Deprecated
    public com.gamingmesh.jobs.economy.PointsData getPointsData() {
	return Jobs.getPointsData();
    }

    public String getMobSpawnerMetadata() {
	return mobSpawnerMetadata;
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
	PlayerUUIDMap.put(info.getUuid(), info);
	PlayerIDMap.put(info.getID(), info);
	PlayerNameMap.put(info.getName().toLowerCase(), info);
    }

    public void addPlayerToCache(JobsPlayer jPlayer) {
	if (!playersCache.containsKey(jPlayer.getName().toLowerCase()))
	    playersCache.put(jPlayer.getName().toLowerCase(), jPlayer);
	if (jPlayer.getUniqueId() != null && !playersUUIDCache.containsKey(jPlayer.getUniqueId()))
	    playersUUIDCache.put(jPlayer.getUniqueId(), jPlayer);
    }

    public void addPlayer(JobsPlayer jPlayer) {
	if (!players.containsKey(jPlayer.getName().toLowerCase()))
	    players.put(jPlayer.getName().toLowerCase(), jPlayer);
	if (jPlayer.getUniqueId() != null && !playersUUID.containsKey(jPlayer.getUniqueId()))
	    playersUUID.put(jPlayer.getUniqueId(), jPlayer);
    }

    public JobsPlayer removePlayer(Player player) {
	if (player == null)
	    return null;

	if (players.containsKey(player.getName()))
	    players.remove(player.getName().toLowerCase());

	return playersUUID.remove(player.getUniqueId());
    }

    public ConcurrentHashMap<UUID, JobsPlayer> getPlayersCache() {
	return playersUUIDCache;
    }

//    public ConcurrentHashMap<String, JobsPlayer> getPlayers() {
//	return this.players;
//    }

    public HashMap<UUID, PlayerInfo> getPlayersInfoUUIDMap() {
	return PlayerUUIDMap;
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

	JobsPlayer jPlayer = playersUUIDCache.get(player.getUniqueId());

	if (jPlayer == null || Jobs.getGCManager().MultiServerCompatability()) {
	    if (jPlayer != null)
		jPlayer = Jobs.getJobsDAO().loadFromDao(jPlayer);
	    else
		jPlayer = Jobs.getJobsDAO().loadFromDao(player);

	    if (Jobs.getGCManager().MultiServerCompatability()) {
		ArchivedJobs archivedJobs = Jobs.getJobsDAO().getArchivedJobs(jPlayer);
		if (archivedJobs != null) {
		    jPlayer.setArchivedJobs(archivedJobs);
		}

		jPlayer.setPaymentLimit(Jobs.getJobsDAO().getPlayersLimits(jPlayer));
		jPlayer.setPoints(Jobs.getJobsDAO().getPlayerPoints(jPlayer));
	    }
	    // Lets load quest progression
	    PlayerInfo info = Jobs.getJobsDAO().loadPlayerData(player.getUniqueId());
	    if (info != null) {
		jPlayer.setDoneQuests(info.getQuestsDone());
		jPlayer.setQuestProgressionFromString(info.getQuestProgression());
	    }

	    jPlayer.loadLogFromDao();
	}

	addPlayer(jPlayer);
	AutoJoinJobs(player);
	jPlayer.onConnect();
	jPlayer.reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
    }

    /**
     * Handles player quit
     * @param playername
     */
    public void playerQuit(Player player) {
	JobsPlayer jPlayer = getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	jPlayer.onDisconnect();
	if (Jobs.getGCManager().saveOnDisconnect()) {
	    jPlayer.setSaved(false);
	    jPlayer.save();
	}
    }

    public void removePlayerAdditions() {
	for (JobsPlayer jPlayer : players.values()) {
	    jPlayer.clearBossMaps();
	    jPlayer.clearUpdateBossBarFor();
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
	ArrayList<JobsPlayer> list = new ArrayList<>(players.values());

	for (JobsPlayer jPlayer : list)
	    jPlayer.save();

	Iterator<JobsPlayer> iter = players.values().iterator();
	while (iter.hasNext()) {
	    JobsPlayer jPlayer = iter.next();
	    if (!jPlayer.isOnline() && jPlayer.isSaved())
		iter.remove();
	}

	Jobs.getBpManager().saveCache();
    }

    /**
     * Save all the information of all of the players
     */
    public void convertChacheOfPlayers(boolean resetID) {
	int y = 0, i = 0, total = playersUUIDCache.size();

	for (JobsPlayer jPlayer : playersUUIDCache.values()) {
	    if (resetID)
		jPlayer.setUserId(-1);

	    JobsDAO dao = Jobs.getJobsDAO();
	    dao.updateSeen(jPlayer);

	    if (!resetID && jPlayer.getUserId() == -1)
		continue;

	    for (JobProgression oneJ : jPlayer.getJobProgression())
		dao.insertJob(jPlayer, oneJ);
	    dao.saveLog(jPlayer);
	    dao.savePoints(jPlayer);
	    dao.recordPlayersLimits(jPlayer);

	    i++;
	    y++;

	    if (y >= 1000) {
		Jobs.consoleMsg("&e[Jobs] Saved " + i + "/" + total + " players data");
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
	JobsPlayer jPlayer = playersUUID.get(uuid);
	if (jPlayer != null)
	    return jPlayer;
	return playersUUIDCache.get(uuid);
    }

    /**
     * Get the player job info for specific player
     * @param player name - the player name who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayer(String playerName) {
	JobsPlayer jPlayer = players.get(playerName.toLowerCase());
	return jPlayer != null ? jPlayer : playersCache.get(playerName.toLowerCase());
    }

    /**
     * Get the player job info for specific player
     * @param archivedJobs 
     * @param player - the player who's job you're getting
     * @return the player job info of the player
     */
    public JobsPlayer getJobsPlayerOffline(PlayerInfo info, List<JobsDAOData> jobs, PlayerPoints points,
	HashMap<String, Log> logs, ArchivedJobs archivedJobs, PaymentData limits) {
	if (info == null)
	    return null;

	JobsPlayer jPlayer = new JobsPlayer(info.getName());
	jPlayer.setPlayerUUID(info.getUuid());
	jPlayer.setUserId(info.getID());
	jPlayer.setDoneQuests(info.getQuestsDone());
	jPlayer.setQuestProgressionFromString(info.getQuestProgression());

	if (jobs != null)
	    for (JobsDAOData jobdata : jobs) {
		Job job = Jobs.getJob(jobdata.getJobName());
		if (job == null)
		    continue;

		JobProgression jobProgression = new JobProgression(job, jPlayer, jobdata.getLevel(), jobdata.getExperience());
		jPlayer.progression.add(jobProgression);
		jPlayer.reloadMaxExperience();
		jPlayer.reloadLimits();
	    }

	if (points != null)
	    jPlayer.setPoints(points);

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

	Bukkit.getScheduler().runTaskAsynchronously(Jobs.getInstance(), () -> Jobs.getJobsDAO().joinJob(jPlayer, jPlayer.getJobProgression(job)));
	jPlayer.setLeftTime(job);

	PerformCommands.performCommandsOnJoin(jPlayer, job);

	Jobs.takeSlot(job);
	Jobs.getSignUtil().updateAllSign(job);

	job.updateTotalPlayers();
	jPlayer.maxJobsEquation = getMaxJobs(jPlayer);
    }

    /**
     * Causes player to leave their job
     * @param jPlayer
     * @param job
     */
    public boolean leaveJob(JobsPlayer jPlayer, Job job) {
	if (!jPlayer.isInJob(job))
	    return false;

	JobsLeaveEvent jobsleaveevent = new JobsLeaveEvent(jPlayer, job);
	Bukkit.getServer().getPluginManager().callEvent(jobsleaveevent);
	// If event is canceled, don't do anything
	if (jobsleaveevent.isCancelled())
	    return false;

	Jobs.getJobsDAO().recordToArchive(jPlayer, job);
	// let the user leave the job
	if (!jPlayer.leaveJob(job))
	    return false;

	if (!Jobs.getJobsDAO().quitJob(jPlayer, job))
	    return false;
	PerformCommands.performCommandsOnLeave(jPlayer, job);
	Jobs.leaveSlot(job);

	jPlayer.getLeftTimes().remove(jPlayer.getUniqueId());

	Jobs.getSignUtil().updateAllSign(job);
	job.updateTotalPlayers();
	return true;
    }

    /**
     * Causes player to leave all their jobs
     * @param jPlayer
     */
    public void leaveAllJobs(JobsPlayer jPlayer) {
	List<JobProgression> jobs = new ArrayList<>();
	jobs.addAll(jPlayer.getJobProgression());
	for (JobProgression job : jobs)
	    leaveJob(jPlayer, job.getJob());
	jPlayer.leaveAllJobs();
    }

    /**
     * Transfers player job
     * @param jPlayer
     * @param oldjob - the old job
     * @param newjob - the new job
     */
    public boolean transferJob(JobsPlayer jPlayer, Job oldjob, Job newjob) {
	if (!jPlayer.transferJob(oldjob, newjob))
	    return false;

	JobsDAO dao = Jobs.getJobsDAO();
	if (!dao.quitJob(jPlayer, oldjob))
	    return false;
	oldjob.updateTotalPlayers();
	dao.joinJob(jPlayer, jPlayer.getJobProgression(newjob));
	newjob.updateTotalPlayers();
	jPlayer.save();
	return true;
    }

    /**
     * Promotes player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to promote
     */
    public void promoteJob(JobsPlayer jPlayer, Job job, int levels) {
	jPlayer.promoteJob(job, levels);
	jPlayer.save();

	Jobs.getSignUtil().updateAllSign(job);
    }

    /**
     * Demote player in their job
     * @param jPlayer
     * @param job - the job
     * @param levels - number of levels to demote
     */
    public void demoteJob(JobsPlayer jPlayer, Job job, int levels) {
	jPlayer.demoteJob(job, levels);
	jPlayer.save();

	Jobs.getSignUtil().updateAllSign(job);
    }

    /**
     * Adds experience to the player
     * @param jPlayer
     * @param job - the job
     * @param experience - experience gained
     */
    public void addExperience(JobsPlayer jPlayer, Job job, double experience) {
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null || experience > Double.MAX_VALUE)
	    return;

	int oldLevel = prog.getLevel();
	if (prog.addExperience(experience)) {
	    performLevelUp(jPlayer, job, oldLevel);
	    Jobs.getSignUtil().updateAllSign(job);
	}

	jPlayer.save();
    }

    /**
     * Removes experience to the player
     * @param jPlayer
     * @param job - the job
     * @param experience - experience gained
     */
    public void removeExperience(JobsPlayer jPlayer, Job job, double experience) {
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null || experience > Double.MAX_VALUE)
	    return;

	prog.addExperience(-experience);

	jPlayer.save();
	Jobs.getSignUtil().updateAllSign(job);
    }

    /**
     * Broadcasts level up about a player
     * @param jPlayer
     * @param job
     * @param oldLevel
     */
    public void performLevelUp(JobsPlayer jPlayer, Job job, int oldLevel) {
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;

	Player player = jPlayer.getPlayer();

	// when the player loses income
	if (prog.getLevel() < oldLevel) {
	    String message = Jobs.getLanguage().getMessage("message.leveldown.message");

	    message = message.replace("%jobname%", job.getNameWithColor());
	    message = message.replace("%playername%", player != null ? player.getDisplayName() : jPlayer.getName());
	    message = message.replace("%joblevel%", "" + prog.getLevel());
	    message = message.replace("%lostLevel%", "" + oldLevel);

	    if (player != null) {
		for (String line : message.split("\n")) {
		    if (Jobs.getGCManager().LevelChangeActionBar)
			ActionBarManager.send(player, line);
		    if (Jobs.getGCManager().LevelChangeChat)
			player.sendMessage(line);
		}
	    }

	    jPlayer.reloadHonorific();
	    Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	    performCommandOnLevelUp(jPlayer, prog.getJob(), oldLevel);
	    Jobs.getSignUtil().updateAllSign(job);
	    return;
	}

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
		if (player != null)
		    player.getWorld().playSound(player.getLocation(), sound, levelUpEvent.getSoundVolume(), levelUpEvent.getSoundPitch());
	    }
	} catch (Exception e) {
	}

	if (Jobs.getGCManager().FireworkLevelupUse) {
	    Bukkit.getServer().getScheduler().runTaskLater(Jobs.getInstance(), new Runnable() {
		@Override
		public void run() {
		    if (player == null || !player.isOnline())
			return;

		    Firework f = player.getWorld().spawn(player.getLocation(), Firework.class);
		    FireworkMeta fm = f.getFireworkMeta();

		    if (Jobs.getGCManager().UseRandom) {
			ThreadLocalRandom r = ThreadLocalRandom.current();
			int rt = r.nextInt(4) + 1;
			Type type = Type.BALL;

			switch (rt) {
			case 2:
			    type = Type.BALL_LARGE;
			    break;
			case 3:
			    type = Type.BURST;
			    break;
			case 4:
			    type = Type.CREEPER;
			    break;
			case 5:
			    type = Type.STAR;
			    break;
			default:
			    break;
			}

			int r1i = r.nextInt(17) + 1;
			int r2i = r.nextInt(17) + 1;

			Color c1 = Util.getColor(r1i);
			Color c2 = Util.getColor(r2i);

			FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1)
			    .withFade(c2).with(type).trail(r.nextBoolean()).build();
			fm.addEffect(effect);

			int rp = r.nextInt(2) + 1;
			fm.setPower(rp);
		    } else {
			Pattern comma = Pattern.compile(",", 16);
			List<String> colorStrings = Jobs.getGCManager().FwColors;
			Color[] colors = new Color[colorStrings.size()];

			for (int s = 0; s < colorStrings.size(); s++) {
			    String colorString = colorStrings.get(s);
			    String[] sSplit = comma.split(colorString);
			    if (sSplit.length < 3) {
				Jobs.consoleMsg("[Jobs] &cInvalid color " + colorString + "! Colors must be 3 comma-separated numbers ranging from 0 to 255.");
				continue;
			    }

			    int[] colorRGB = new int[3];
			    for (int i = 0; i < 3; i++) {
				String colorInt = sSplit[i];
				try {
				    colorRGB[i] = Integer.parseInt(colorInt);
				} catch (NumberFormatException e) {
				    Jobs.consoleMsg("[Jobs] &cInvalid color component " + colorInt + ", it must be an integer.");
				}
			    }

			    try {
				colors[s] = Color.fromRGB(colorRGB[0], colorRGB[1], colorRGB[2]);
			    } catch (IllegalArgumentException e) {
				Jobs.consoleMsg("[Jobs] &cFailed to add color! " + e);
			    }
			}

			fm.addEffect(FireworkEffect.builder()
			    .flicker(Jobs.getGCManager().UseFlicker)
			    .trail(Jobs.getGCManager().UseTrail)
			    .with(Type.valueOf(Jobs.getGCManager().FireworkType))
			    .withColor(colors)
			    .withFade(colors)
			    .build());

			fm.setPower(Jobs.getGCManager().FireworkPower);
		    }

		    f.setFireworkMeta(fm);
		}
	    }, Jobs.getGCManager().ShootTime);
	}

	String message;
	if (Jobs.getGCManager().isBroadcastingLevelups())
	    message = Jobs.getLanguage().getMessage("message.levelup.broadcast");
	else
	    message = Jobs.getLanguage().getMessage("message.levelup.nobroadcast");

	message = message.replace("%jobname%", job.getNameWithColor());

	if (levelUpEvent.getOldTitle() != null)
	    message = message.replace("%titlename%", levelUpEvent.getOldTitle()
		.getChatColor().toString() + levelUpEvent.getOldTitle().getName());

	message = message.replace("%playername%", player != null ? player.getDisplayName() : jPlayer.getName());
	message = message.replace("%joblevel%", "" + prog.getLevel());

	for (String line : message.split("\n")) {
	    if (Jobs.getGCManager().isBroadcastingLevelups()) {
		if (Jobs.getGCManager().BroadcastingLevelUpLevels.contains(oldLevel + 1) || Jobs.getGCManager().BroadcastingLevelUpLevels.contains(0))
		    Bukkit.getServer().broadcastMessage(line);
	    } else if (player != null) {
		if (Jobs.getGCManager().LevelChangeActionBar)
		    ActionBarManager.send(player, line);
		if (Jobs.getGCManager().LevelChangeChat)
		    player.sendMessage(line);
	    }
	}

	if (levelUpEvent.getNewTitle() != null && !levelUpEvent.getNewTitle().equals(levelUpEvent.getOldTitle())) {

	    // If it fails, we can ignore it
	    try {
		if (Jobs.getGCManager().SoundTitleChangeUse) {
		    Sound sound = levelUpEvent.getTitleChangeSound();
		    if (player != null)
			player.getWorld().playSound(player.getLocation(), sound, levelUpEvent.getTitleChangeVolume(),
			    levelUpEvent.getTitleChangePitch());
		}
	    } catch (Exception e) {
	    }
	    // user would skill up
	    if (Jobs.getGCManager().isBroadcastingSkillups())
		message = Jobs.getLanguage().getMessage("message.skillup.broadcast");
	    else
		message = Jobs.getLanguage().getMessage("message.skillup.nobroadcast");

	    message = message.replace("%playername%", player != null ? player.getDisplayName() : jPlayer.getName());
	    message = message.replace("%titlename%", levelUpEvent.getNewTitle()
		.getChatColor().toString() + levelUpEvent.getNewTitle().getName());
	    message = message.replace("%jobname%", job.getNameWithColor());

	    for (String line : message.split("\n")) {
		if (Jobs.getGCManager().isBroadcastingSkillups()) {
		    Bukkit.getServer().broadcastMessage(line);
		} else if (player != null) {
		    if (Jobs.getGCManager().TitleChangeActionBar)
			ActionBarManager.send(player, line);
		    if (Jobs.getGCManager().TitleChangeChat)
			player.sendMessage(line);
		}
	    }
	}

	jPlayer.reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	performCommandOnLevelUp(jPlayer, prog.getJob(), oldLevel);
	Jobs.getSignUtil().updateAllSign(job);

	if (Jobs.getGCManager().titleMessageMaxLevelReached && prog.getLevel() == jPlayer.getMaxJobLevelAllowed(prog.getJob())) {
	    TitleMessageManager.send(jPlayer.getPlayer(), Jobs.getLanguage().getMessage("message.max-level-reached.title",
		"%jobname%", prog.getJob().getNameWithColor()),
		Jobs.getLanguage().getMessage("message.max-level-reached.subtitle", "%jobname%", prog.getJob().getNameWithColor()), 20, 40, 20);
	    jPlayer.getPlayer().sendMessage(Jobs.getLanguage().getMessage("message.max-level-reached.chat", "%jobname%", prog.getJob().getNameWithColor()));
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
	Player player = Bukkit.getPlayer(jPlayer.getUniqueId());
	JobProgression prog = jPlayer.getJobProgression(job);
	if (prog == null)
	    return;

	for (JobCommands command : job.getCommands()) {
	    if ((command.getLevelFrom() == 0 && command.getLevelUntil() == 0) || newLevel >= command.getLevelFrom()
		&& newLevel <= command.getLevelUntil()) {
		for (String commandString : new ArrayList<String>(command.getCommands())) {
		    commandString = commandString.replace("[player]", player.getName());
		    commandString = commandString.replace("[oldlevel]", String.valueOf(oldLevel));
		    commandString = commandString.replace("[newlevel]", String.valueOf(newLevel));
		    commandString = commandString.replace("[jobname]", job.getName());
		    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
		}
	    }
	}
    }

    /**
     * Checks whenever the given jobs player is under the max allowed jobs.
     * @param player {@link JobsPlayer}
     * @param currentCount the current jobs size
     * @return true if the player is under the given jobs size
     */
    public boolean getJobsLimit(JobsPlayer jPlayer, short currentCount) {
	return getMaxJobs(jPlayer) > currentCount;
    }

    /**
     * Gets the maximum jobs from player.
     * @param jPlayer {@link JobsPlayer}
     * @return the maximum allowed jobs
     */
    public int getMaxJobs(JobsPlayer jPlayer) {
	if (jPlayer == null) {
	    return Jobs.getGCManager().getMaxJobs();
	}

	int max = Jobs.getPermissionManager().getMaxPermission(jPlayer, "jobs.max", false).intValue();
	return max == 0 ? Jobs.getGCManager().getMaxJobs() : max;
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
	for (JobsPlayer jPlayer : players.values()) {
	    for (JobProgression progression : jPlayer.getJobProgression()) {
		String jobName = progression.getJob().getName();
		Job job = Jobs.getJob(jobName);
		if (job != null)
		    progression.setJob(job);
	    }
	    if (jPlayer.isOnline()) {
		jPlayer.reloadHonorific();
		jPlayer.reloadLimits();
		Jobs.getPermissionHandler().recalculatePermissions(jPlayer);
	    }
	}
    }

    private final HashMap<UUID, HashMap<Job, ItemBonusCache>> cache = new HashMap<>();

    public void resetiItemBonusCache(UUID uuid) {
	cache.remove(uuid);
    }

    public BoostMultiplier getItemBoostNBT(Player player, Job prog) {
	HashMap<Job, ItemBonusCache> cj = cache.get(player.getUniqueId());
	if (cj == null) {
	    cj = new HashMap<>();
	    cache.put(player.getUniqueId(), cj);
	}

	ItemBonusCache c = cj.get(prog);
	if (c == null) {
	    c = new ItemBonusCache(getInventoryBoost(player, prog));
	    cj.put(prog, c);
	    return c.getBoostMultiplier();
	}

	return c.getBoostMultiplier();
    }

    public BoostMultiplier getInventoryBoost(Player player, Job prog) {
	BoostMultiplier data = new BoostMultiplier();
	if (player == null || prog == null)
	    return data;

	ItemStack iih = Jobs.getNms().getItemInMainHand(player);
	JobItems jitem = getJobsItemByNbt(iih);
	if (jitem != null && jitem.getJobs().contains(prog))
	    data.add(jitem.getBoost(getJobsPlayer(player).getJobProgression(prog)));

	// Lets check offhand
	if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && Jobs.getGCManager().boostedItemsInOffHand) {
	    iih = CMIReflections.getItemInOffHand(player);
	    if (iih != null) {
		jitem = getJobsItemByNbt(iih);
		if (jitem != null && jitem.getJobs().contains(prog))
		    data.add(jitem.getBoost(getJobsPlayer(player).getJobProgression(prog)));
	    }
	}

	for (ItemStack oneArmor : player.getInventory().getArmorContents()) {
	    if (oneArmor == null || oneArmor.getType() == org.bukkit.Material.AIR)
		continue;

	    JobItems armorboost = getJobsItemByNbt(oneArmor);
	    if (armorboost == null || !armorboost.getJobs().contains(prog))
		continue;

	    data.add(armorboost.getBoost(getJobsPlayer(player).getJobProgression(prog)));
	}

	return data;
    }

    public boolean containsItemBoostByNBT(ItemStack item) {
	return item != null && Jobs.getReflections().hasNbtString(item, JobsItemBoost);
    }

    private final String JobsItemBoost = "JobsItemBoost";

    public JobItems getJobsItemByNbt(ItemStack item) {
	if (item == null)
	    return null;

	Object itemName = CMIReflections.getNbt(item, JobsItemBoost);

	if (itemName == null || itemName.toString().isEmpty()) {
	    // Checking old boost items and converting to new format if needed
	    if (Jobs.getReflections().hasNbt(item, JobsItemBoost)) {
		for (Job one : Jobs.getJobs()) {
		    itemName = Jobs.getReflections().getNbt(item, JobsItemBoost, one.getName());
		    if (itemName != null) {
			JobItems b = ItemBoostManager.getItemByKey(itemName.toString());
			if (b != null) {
			    ItemStack ic = CMIReflections.setNbt(item, JobsItemBoost, b.getNode());
			    item.setItemMeta(ic.getItemMeta());
			}
			break;
		    }
		}
	    }
	    if (itemName == null)
		return null;
	}

	return ItemBoostManager.getItemByKey(itemName.toString());
    }

//    public BoostMultiplier getJobsBoostByNbt(ItemStack item) {
//	JobItems b = getJobsItemByNbt(item);
//	if (b == null)
//	    return null;
//	return b.getBoost();
//    }

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

	if (player == null || !player.isOnline() || prog == null)
	    return boost;

	if (HookManager.getMcMMOManager().mcMMOPresent || HookManager.getMcMMOManager().mcMMOOverHaul)
	    boost.add(BoostOf.McMMO, new BoostMultiplier().add(HookManager.getMcMMOManager().getMultiplier(player.getPlayer())));

	double petPay = 0D;

	if (ent instanceof Tameable) {
	    Tameable t = (Tameable) ent;
	    if (t.isTamed() && t.getOwner() instanceof Player) {
		petPay = Jobs.getPermissionManager().getMaxPermission(player, "jobs.petpay", false, false);
		if (petPay != 0D)
		    boost.add(BoostOf.PetPay, new BoostMultiplier().add(petPay));
	    }
	}

	if (ent != null && HookManager.getMyPetManager() != null && HookManager.getMyPetManager().isMyPet(ent, player.getPlayer())) {
	    if (petPay == 0D)
		petPay = Jobs.getPermissionManager().getMaxPermission(player, "jobs.petpay", false, false);
	    if (petPay != 0D)
		boost.add(BoostOf.PetPay, new BoostMultiplier().add(petPay));
	}

	if (victim != null && victim.hasMetadata(getMobSpawnerMetadata())) {
	    Double amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.nearspawner", false, false);
	    if (amount != 0D)
		boost.add(BoostOf.NearSpawner, new BoostMultiplier().add(amount));
	}

	if (getall) {
	    if (petPay == 0D)
		petPay = Jobs.getPermissionManager().getMaxPermission(player, "jobs.petpay", force, false);
	    if (petPay != 0D)
		boost.add(BoostOf.PetPay, new BoostMultiplier().add(petPay));
	    Double amount = Jobs.getPermissionManager().getMaxPermission(player, "jobs.nearspawner", force);
	    if (amount != null)
		boost.add(BoostOf.NearSpawner, new BoostMultiplier().add(amount));
	}

	boost.add(BoostOf.Permission, getBoost(player, prog, force));
	boost.add(BoostOf.Global, prog.getBoost());

	if (Jobs.getGCManager().useDynamicPayment)
	    boost.add(BoostOf.Dynamic, new BoostMultiplier().add(prog.getBonus()));

	boost.add(BoostOf.Item, getItemBoostNBT(player.getPlayer(), prog));

	if (!Jobs.getRestrictedAreaManager().getRestrictedAres().isEmpty())
	    boost.add(BoostOf.Area, new BoostMultiplier().add(Jobs.getRestrictedAreaManager().getRestrictedMultiplier(player.getPlayer())));
	return boost;
    }

    public void AutoJoinJobs(final Player player) {
	if (player == null || player.isOp() || !Jobs.getGCManager().AutoJobJoinUse)
	    return;

	Bukkit.getServer().getScheduler().runTaskLater(Jobs.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		if (!player.isOnline())
		    return;

		JobsPlayer jPlayer = getJobsPlayer(player);
		if (jPlayer == null || player.hasPermission("jobs.*"))
		    return;

		int confMaxJobs = Jobs.getGCManager().getMaxJobs();
		for (Job one : Jobs.getJobs()) {
		    if (one.getMaxSlots() != null && Jobs.getUsedSlots(one) >= one.getMaxSlots())
			continue;

		    short PlayerMaxJobs = (short) jPlayer.getJobProgression().size();
		    if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !getJobsLimit(jPlayer, PlayerMaxJobs))
			break;

		    if (jPlayer.isInJob(one))
			continue;

		    if (player.hasPermission("jobs.autojoin." + one.getName().toLowerCase()))
			joinJob(jPlayer, one);
		}
	    }
	}, Jobs.getGCManager().AutoJobJoinDelay * 20L);
    }
}
