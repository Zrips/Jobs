package com.gamingmesh.jobs.config;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gamingmesh.jobs.api.JobsScheduleStartEvent;
import com.gamingmesh.jobs.api.JobsScheduleStopEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Debug;
import com.gamingmesh.jobs.stuff.TimeManage;

public class ScheduleManager {

    private Jobs plugin;
    private int autoTimerBukkitId = -1;

    public ScheduleManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public void start() {
	if (Jobs.getGCManager().BoostSchedule.isEmpty())
	    return;
	autoTimerBukkitId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, autoTimer, 20, 30 * 20L);
    }

    public void cancel() {
	if (autoTimerBukkitId != -1)
	    Bukkit.getScheduler().cancelTask(autoTimerBukkitId);
    }

    private Runnable autoTimer = new Runnable() {
	@Override
	public void run() {
	    try {
		scheduler();
	    } catch (Exception e) {
	    }
	}
    };

    public int getDateByInt() {
	return TimeManage.timeInInt();
    }

    private boolean scheduler() {
	if (Jobs.getGCManager().BoostSchedule.isEmpty())
	    return false;

	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	Date date = new Date();

	String currenttime = dateFormat.format(date);

	int Current = Integer.valueOf(currenttime.replace(":", "")).intValue();

	String CurrentDayName = GetWeekDay();

	for (Schedule one : Jobs.getGCManager().BoostSchedule) {

	    int From = one.GetFrom();
	    int Until = one.GetUntil();

	    List<String> days = one.GetDays();

	    if (one.isStarted() && one.getBroadcastInfoOn() < System.currentTimeMillis() && one.GetBroadcastInterval() > 0) {
		one.setBroadcastInfoOn(System.currentTimeMillis() + one.GetBroadcastInterval() * 60 * 1000);
		for (String oneMsg : one.GetMessageToBroadcast()) {
		    Bukkit.broadcastMessage(oneMsg);
		}
	    }

	    if (((one.isNextDay() && (Current >= From && Current < Until || Current >= one.GetNextFrom() && Current < one.GetNextUntil()) && !one
		.isStarted()) || !one.isNextDay() && (Current >= From && Current < Until)) && (days.contains(CurrentDayName) || days.contains("all")) && !one
		    .isStarted()) {

		JobsScheduleStartEvent event = new JobsScheduleStartEvent(one);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
		    continue;
		}
		if (one.isBroadcastOnStart())
		    if (one.GetMessageOnStart().size() == 0)
			Bukkit.broadcastMessage(Jobs.getLanguage().getMessage("message.boostStarted"));
		    else
			for (String oneMsg : one.GetMessageOnStart()) {
			    Bukkit.broadcastMessage(oneMsg);
			}

		for (Job onejob : one.GetJobs()) {
		    onejob.setBoost(one.getBoost());
		}

		one.setBroadcastInfoOn(System.currentTimeMillis() + one.GetBroadcastInterval() * 60 * 1000);

		one.setStarted(true);
		one.setStoped(false);
		break;
	    } else if (((one.isNextDay() && Current > one.GetNextUntil() && Current < one.GetFrom() && !one.isStoped()) || !one.isNextDay() && Current > Until
		&& ((days.contains(CurrentDayName)) || days.contains("all"))) && !one.isStoped()) {
		JobsScheduleStopEvent event = new JobsScheduleStopEvent(one);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
		    continue;
		}
		if (one.isBroadcastOnStop())
		    if (one.GetMessageOnStop().size() == 0)
			Bukkit.broadcastMessage(Jobs.getLanguage().getMessage("message.boostStoped"));
		    else
			for (String oneMsg : one.GetMessageOnStop()) {
			    Bukkit.broadcastMessage(oneMsg);
			}
		for (Job onejob : one.GetJobs()) {
		    onejob.setBoost(new BoostMultiplier());
		}
		one.setStoped(true);
		one.setStarted(false);
	    }

	}

	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    @Override
	    public void run() {
		scheduler();
		return;
	    }
	}, 30 * 20L);

	return true;
    }

    public static String GetWeekDay() {
	Calendar c = Calendar.getInstance();
	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	switch (dayOfWeek) {
	case 2:
	    return "monday";
	case 3:
	    return "tuesday";
	case 4:
	    return "wednesday";
	case 5:
	    return "thursday";
	case 6:
	    return "friday";
	case 7:
	    return "saturday";
	case 1:
	    return "sunday";
	}
	return "all";
    }

    /**
     * Method to load the scheduler configuration
     * 
     * loads from Jobs/schedule.yml
     */
    public void load() {
	File f = new File(plugin.getDataFolder(), "schedule.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

	conf.options().copyDefaults(true);

	if (!conf.contains("Boost"))
	    return;

	ArrayList<String> sections = new ArrayList<String>(conf.getConfigurationSection("Boost").getKeys(false));

	for (String OneSection : sections) {
	    ConfigurationSection path = conf.getConfigurationSection("Boost." + OneSection);

	    if (!path.contains("Enabled"))
		continue;

	    if (!conf.getConfigurationSection("Boost." + OneSection).getBoolean("Enabled"))
		continue;

	    Schedule sched = new Schedule();
	    sched.setName(OneSection);

	    if (!path.contains("From") || !path.getString("From").contains(":"))
		continue;

	    if (!path.contains("Until") || !path.getString("Until").contains(":"))
		continue;

	    if (!path.contains("Days") || !path.isList("Days"))
		continue;

	    if (!path.contains("Jobs") || !path.isList("Jobs"))
		continue;

	    sched.setDays(path.getStringList("Days"));
	    sched.setJobs(path.getStringList("Jobs"));
	    sched.setFrom(Integer.valueOf(path.getString("From").replace(":", "")));
	    sched.setUntil(Integer.valueOf(path.getString("Until").replace(":", "")));

	    if (path.contains("MessageOnStart") && path.isList("MessageOnStart"))
		sched.setMessageOnStart(path.getStringList("MessageOnStart"), path.getString("From"), path.getString("Until"));

	    if (path.contains("BroadcastOnStart"))
		sched.setBroadcastOnStart(path.getBoolean("BroadcastOnStart"));

	    if (path.contains("MessageOnStop") && path.isList("MessageOnStop"))
		sched.setMessageOnStop(path.getStringList("MessageOnStop"), path.getString("From"), path.getString("Until"));

	    if (path.contains("BroadcastOnStop"))
		sched.setBroadcastOnStop(path.getBoolean("BroadcastOnStop"));

	    if (path.contains("BroadcastInterval"))
		sched.setBroadcastInterval(path.getInt("BroadcastInterval"));

	    if (path.contains("BroadcastMessage") && path.isList("BroadcastMessage"))
		sched.setMessageToBroadcast(path.getStringList("BroadcastMessage"), path.getString("From"), path.getString("Until"));

	    if (path.contains("Exp") && path.isDouble("Exp"))
		sched.setBoost(CurrencyType.EXP, path.getDouble("Exp", 0D));
	    if (path.contains("Money") && path.isDouble("Money"))
		sched.setBoost(CurrencyType.MONEY, path.getDouble("Money", 0D));
	    if (path.contains("Points") && path.isDouble("Points"))
		sched.setBoost(CurrencyType.POINTS, path.getDouble("Points", 0D));

	    Jobs.getGCManager().BoostSchedule.add(sched);
	}
	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Loaded " + Jobs.getGCManager().BoostSchedule.size() + " schedulers!");
    }
}
