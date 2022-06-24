package com.gamingmesh.jobs.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gamingmesh.jobs.api.JobsScheduleStartEvent;
import com.gamingmesh.jobs.api.JobsScheduleStopEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.Schedule;

import net.Zrips.CMILib.Logs.CMIDebug;

public class ScheduleManager {

    private Jobs plugin;

    private BukkitTask timer;
    private YmlMaker jobSchedule;

    public static final List<Schedule> BOOSTSCHEDULE = new ArrayList<>();

    public ScheduleManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public YmlMaker getConf() {
	return jobSchedule;
    }

    public void start() {
	if (BOOSTSCHEDULE.isEmpty())
	    return;

	cancel();
	timer = Bukkit.getScheduler().runTaskTimer(plugin, this::scheduler, 20, 30 * 20L);
    }

    public void cancel() {
	if (timer != null)
	    timer.cancel();
    }

    private boolean scheduler() {
	if (BOOSTSCHEDULE.isEmpty())
	    return false;

	String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
	String currentDayName = getWeekDay();

	int current = Integer.parseInt(currentTime.replace(":", ""));

	for (Schedule one : BOOSTSCHEDULE) {

	    int from = one.getFrom();
	    int until = one.getUntil();

	    if (one.isStarted() && one.getBroadcastInfoOn() < System.currentTimeMillis() && one.getBroadcastInterval() > 0) {
		one.setBroadcastInfoOn(System.currentTimeMillis() + one.getBroadcastInterval() * 60 * 1000);
		plugin.getComplement().broadcastMessage(one.getMessageToBroadcast());
	    }

	    boolean containsDays = one.getDays().contains(currentDayName) || one.getDays().contains("all");

	    if (((one.isNextDay() && (current >= from && current < until || current >= one.getNextFrom() && current < one.getNextUntil()) && !one
		.isStarted()) || !one.isNextDay() && (current >= from && current < until)) && containsDays && !one.isStarted()) {

		JobsScheduleStartEvent event = new JobsScheduleStartEvent(one);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
		    continue;
		}

		if (one.isBroadcastOnStart())
		    if (one.getMessageOnStart().isEmpty())
			plugin.getComplement().broadcastMessage(Jobs.getLanguage().getMessage("message.boostStarted"));
		    else
			plugin.getComplement().broadcastMessage(one.getMessageOnStart());

		for (Job onejob : one.getJobs()) {
		    onejob.setBoost(one.getBoost());
		}

		one.setBroadcastInfoOn(System.currentTimeMillis() + one.getBroadcastInterval() * 60 * 1000);

		one.setStarted(true);
		one.setStoped(false);
		break;
	    } else if (((one.isNextDay() && current > one.getNextUntil() && current < one.getFrom() && !one.isStoped()) || (!one.isNextDay() && current > until
		&& containsDays)) && !one.isStoped()) {
		JobsScheduleStopEvent event = new JobsScheduleStopEvent(one);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
		    continue;
		}

		if (one.isBroadcastOnStop())
		    if (one.getMessageOnStop().isEmpty())
			plugin.getComplement().broadcastMessage(Jobs.getLanguage().getMessage("message.boostStoped"));
		    else
			plugin.getComplement().broadcastMessage(one.getMessageOnStop());

		for (Job onejob : one.getJobs()) {
		    onejob.setBoost(new BoostMultiplier());
		}

		one.setStoped(true);
		one.setStarted(false);
	    }

	}
	return true;
    }

    public static String getWeekDay() {
	switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
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
	default:
	    return "all";
	}
    }

    /**
     * Method to load the scheduler configuration
     * 
     * loads from Jobs/schedule.yml
     */
    public void load() {
	BOOSTSCHEDULE.clear();

	jobSchedule = new YmlMaker(Jobs.getFolder(), "schedule.yml");
	jobSchedule.saveDefaultConfig();

	YamlConfiguration conf = YamlConfiguration.loadConfiguration(jobSchedule.getConfigFile());

	conf.options().copyDefaults(true);
	conf.options().copyHeader(true);

	ConfigurationSection section = conf.getConfigurationSection("Boost");
	if (section == null)
	    return;

	List<String> sections = new ArrayList<>(section.getKeys(false));

	for (String oneSection : sections) {
	    ConfigurationSection path = section.getConfigurationSection(oneSection);

	    if (path == null)
		continue;

	    if (!path.getBoolean("Enabled"))
		continue;

	    if (!path.getString("From", "").contains(":") ||
		!path.getString("Until", "").contains(":") ||
		!path.isList("Days") && !path.isString("Days") ||
		!path.isList("Jobs") && !path.isString("Jobs")) {

		Jobs.consoleMsg("&cIncorect scheduler format detected for " + oneSection + " scheduler!");
		continue;
	    }

	    Schedule sched = new Schedule();

	    sched.setName(oneSection);

	    if (path.isString("Days")) {
		sched.setDays(Arrays.asList(path.getString("Days")));
	    } else {
		sched.setDays(path.getStringList("Days"));
	    }
	    
	    if (path.isString("Jobs")) {
		sched.setJobs(Arrays.asList(path.getString("Jobs")));
	    } else {
		sched.setJobs(path.getStringList("Jobs"));
	    }
	    
	    sched.setFrom(Integer.parseInt(path.getString("From").replace(":", "")));
	    sched.setUntil(Integer.parseInt(path.getString("Until").replace(":", "")));

	    if (path.isList("MessageOnStart"))
		sched.setMessageOnStart(path.getStringList("MessageOnStart"), path.getString("From"), path.getString("Until"));

	    sched.setBroadcastOnStart(path.getBoolean("BroadcastOnStart", true));

	    if (path.isList("MessageOnStop"))
		sched.setMessageOnStop(path.getStringList("MessageOnStop"), path.getString("From"), path.getString("Until"));

	    sched.setBroadcastOnStop(path.getBoolean("BroadcastOnStop", true));
	    sched.setBroadcastInterval(path.getInt("BroadcastInterval"));

	    if (path.isList("BroadcastMessage"))
		sched.setMessageToBroadcast(path.getStringList("BroadcastMessage"), path.getString("From"), path.getString("Until"));

	    if (path.isDouble("Exp"))
		sched.setBoost(CurrencyType.EXP, path.getDouble("Exp"));
	    if (path.isDouble("Money"))
		sched.setBoost(CurrencyType.MONEY, path.getDouble("Money"));
	    if (path.isDouble("Points"))
		sched.setBoost(CurrencyType.POINTS, path.getDouble("Points"));

	    BOOSTSCHEDULE.add(sched);
	}

	if (!BOOSTSCHEDULE.isEmpty())
	    Jobs.consoleMsg("&eLoaded " + BOOSTSCHEDULE.size() + " schedulers!");
    }
}
