package com.gamingmesh.jobs.config;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.TimeManage;

public class ScheduleManager {

	public int dateByInt = 0;

	private Jobs plugin;

	public ScheduleManager(Jobs plugin) {
		this.plugin = plugin;
	}

	public int getDateByInt() {
		return dateByInt;
	}

	public void setDateByInt(int time) {
		dateByInt = time;
	}

	public void DateUpdater() {
		if (dateByInt == 0)
			dateByInt = TimeManage.timeInInt();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {

				dateByInt = TimeManage.timeInInt();

				DateUpdater();
				return;
			}
		}, 60 * 20L);
	}

	public boolean scheduler() {
		if (Jobs.getGCManager().BoostSchedule.size() > 0 && Jobs.getGCManager().useGlobalBoostScheduler) {

			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date date = new Date();

			String currenttime = dateFormat.format(date);

			int Current = Integer.valueOf(currenttime.replace(":", "")).intValue();

			String CurrentDayName = GetWeekDay();

			for (Schedule one : Jobs.getGCManager().BoostSchedule) {

				int From = one.GetFrom();
				int Until = one.GetUntil();

				List<String> days = one.GetDays();

				if (one.isStarted() && one.getBroadcastInfoOn() < System.currentTimeMillis()
						&& one.GetBroadcastInterval() > 0) {
					one.setBroadcastInfoOn(System.currentTimeMillis() + one.GetBroadcastInterval() * 60 * 1000);
					for (String oneMsg : one.GetMessageToBroadcast()) {
						Bukkit.broadcastMessage(oneMsg);
					}
				}

				if (((one.isNextDay() && (Current >= From && Current < one.GetUntil()
						|| Current >= one.GetNextFrom() && Current < one.GetNextUntil()) && !one.isStarted())
						|| !one.isNextDay() && (Current >= From && Current < Until))
						&& (days.contains(CurrentDayName) || days.contains("all")) && !one.isStarted()) {

					if (one.isBroadcastOnStart())
						if (one.GetMessageOnStart().size() == 0)
							Bukkit.broadcastMessage(Jobs.getLanguage().getMessage("message.boostStarted"));
						else
							for (String oneMsg : one.GetMessageOnStart()) {
								Bukkit.broadcastMessage(oneMsg);
							}

					for (Job onejob : one.GetJobs()) {
						onejob.setExpBoost(one.GetExpBoost());
						onejob.setMoneyBoost(one.GetMoneyBoost());
					}

					one.setBroadcastInfoOn(System.currentTimeMillis() + one.GetBroadcastInterval() * 60 * 1000);

					one.setStarted(true);
					one.setStoped(false);
					break;
				} else if (((one.isNextDay() && Current > one.GetNextUntil() && Current < one.GetFrom()
						&& !one.isStoped())
						|| !one.isNextDay() && Current > Until
								&& ((days.contains(CurrentDayName)) || days.contains("all")))
						&& !one.isStoped()) {
					if (one.isBroadcastOnStop())
						if (one.GetMessageOnStop().size() == 0)
							Bukkit.broadcastMessage(Jobs.getLanguage().getMessage("message.boostStoped"));
						else
							for (String oneMsg : one.GetMessageOnStop()) {
								Bukkit.broadcastMessage(oneMsg);
							}
					for (Job onejob : one.GetJobs()) {
						onejob.setExpBoost(1.0);
						onejob.setMoneyBoost(1.0);
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
		}
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

			if (!path.contains("Exp") || !path.isDouble("Exp"))
				continue;

			if (!path.contains("Money") || !path.isDouble("Money"))
				continue;

			sched.setDays(path.getStringList("Days"));
			sched.setJobs(path.getStringList("Jobs"));
			sched.setFrom(Integer.valueOf(path.getString("From").replace(":", "")));
			sched.setUntil(Integer.valueOf(path.getString("Until").replace(":", "")));

			if (path.contains("MessageOnStart") && path.isList("MessageOnStart"))
				sched.setMessageOnStart(path.getStringList("MessageOnStart"), path.getString("From"),
						path.getString("Until"));

			if (path.contains("BroadcastOnStart"))
				sched.setBroadcastOnStart(path.getBoolean("BroadcastOnStart"));

			if (path.contains("MessageOnStop") && path.isList("MessageOnStop"))
				sched.setMessageOnStop(path.getStringList("MessageOnStop"), path.getString("From"),
						path.getString("Until"));

			if (path.contains("BroadcastOnStop"))
				sched.setBroadcastOnStop(path.getBoolean("BroadcastOnStop"));

			if (path.contains("BroadcastInterval"))
				sched.setBroadcastInterval(path.getInt("BroadcastInterval"));

			if (path.contains("BroadcastMessage") && path.isList("BroadcastMessage"))
				sched.setMessageToBroadcast(path.getStringList("BroadcastMessage"), path.getString("From"),
						path.getString("Until"));

			sched.setExpBoost(path.getDouble("Exp"));
			sched.setMoneyBoost(path.getDouble("Money"));

			Jobs.getGCManager().BoostSchedule.add(sched);

			Bukkit.getConsoleSender().sendMessage(
					ChatColor.YELLOW + "[Jobs] Loaded " + Jobs.getGCManager().BoostSchedule.size() + " schedulers!");
		}
	}
}
