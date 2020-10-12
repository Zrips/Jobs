package com.gamingmesh.jobs.Placeholders;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
import com.gamingmesh.jobs.stuff.TimeManage;

public class Placeholder {

    private Jobs plugin;
    Pattern placeholderPatern = Pattern.compile("(%)([^\"^%]*)(%)");

    public Placeholder(Jobs plugin) {
	this.plugin = plugin;
    }

    static String pref = "jobsr";
    private static ChatFilterRule numericalRule = new ChatFilterRule().setPattern("(\\$\\d)");

    public enum JobsPlaceHolders {
	user_id,
	user_bstandcount,
	user_maxbstandcount,
	user_furncount,
	user_maxfurncount,
	user_doneq,
	user_dailyquests_pending,
	user_dailyquests_completed,
	user_dailyquests_total,
	user_seen,
	user_totallevels,
	user_issaved,
	user_displayhonorific,
	user_joinedjobcount,
	user_points,
	user_points_fixed,
	user_total_points,
	user_archived_jobs,
	user_jobs,

	user_boost_$1_$2("jname/number", "money/exp/points"),
	user_jtoplvl_$1_$2("jname/number", "number"),
	user_isin_$1("jname/number"),
	user_canjoin_$1("jname/number"),
	user_jlevel_$1("jname/number"),
	user_jexp_$1("jname/number"),
	user_jmaxexp_$1("jname/number"),
	user_jexpunf_$1("jname/number"),
	user_jmaxexpunf_$1("jname/number"),
	user_jmaxlvl_$1("jname/number"),
	user_job_$1("jname/number"),
	user_title_$1("jname/number"),

	maxjobs,

	limit_$1("money/exp/points"),
	plimit_$1("money/exp/points"),
	plimit_tleft_$1("money/exp/points"),

	total_workers,

	name_$1("jname/number"),
	shortname_$1("jname/number"),
	chatcolor_$1("jname/number"),
	description_$1("jname/number"),
	maxdailyq_$1("jname/number"),
	maxlvl_$1("jname/number"),
	maxviplvl_$1("jname/number"),
	totalplayers_$1("jname/number"),
	maxslots_$1("jname/number"),
	bonus_$1("jname/number");

	private String[] vars;
	private List<Integer> groups = new ArrayList<>();
	private ChatFilterRule rule = null;
	private boolean hidden = false;

	JobsPlaceHolders(String... vars) {
	    Matcher matcher = numericalRule.getMatcher(this.toString());
	    if (matcher != null) {
		rule = new ChatFilterRule();
		List<String> ls = new ArrayList<>();
		ls.add("(%" + pref + "_)" + this.toString().replaceAll("\\$\\d", "([^\"^%]*)") + "(%)");
//		For MVdWPlaceholderAPI
//		ls.add("(\\{" + pref + this.toString().replaceAll("\\$\\d", "([^\"^%]*)" + "(\\})"));
		rule.setPattern(ls);
		while (matcher.find()) {
		    try {
			int id = Integer.parseInt(matcher.group(1).substring(1));
			groups.add(id);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	    this.vars = vars;
	    this.hidden = false;
	}

	public static JobsPlaceHolders getByName(String name) {
	    String original = name;
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex())
		    continue;
		if (one.toString().equalsIgnoreCase(name))
		    return one;
	    }
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex())
		    continue;
		if (one.getName().equalsIgnoreCase(name))
		    return one;
	    }
	    name = pref + name;
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex())
		    continue;
		if (one.getName().equalsIgnoreCase(name))
		    return one;
	    }
	    name = "%" + pref + "_" + original + "%";
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (!one.isComplex())
		    continue;
		if (!one.getComplexRegexMatchers(name).isEmpty())
		    return one;
	    }
//	    For MVdWPlaceholderAPI
//	    if (Jobs.getInstance().isMVdWPlaceholderAPIEnabled() && original.startsWith(pref+"_")) {
//		String t = "{" + original + "}";
//		for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
//		    if (!one.isComplex())
//			continue;
//		    if (!one.getComplexRegexMatchers(t).isEmpty()) {
//			return one;
//		    }
//		}
//	    }
	    return null;
	}

	public static JobsPlaceHolders getByNameExact(String name) {
	    name = name.toLowerCase();
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex()) {
		    if (!one.getComplexRegexMatchers("%" + name + "%").isEmpty()) {
			return one;
		    }
		} else {
		    String n = one.getName();
		    if (n.equals(name))
			return one;
		}
	    }
	    return null;
	}

	public String getName() {
	    return pref + "_" + this.name();
	}

	public String getFull() {
	    if (this.isComplex()) {
		String name = this.getName();
		int i = 0;
		for (String one : this.getName().split("_")) {
		    if (!one.startsWith("$"))
			continue;
		    if (vars.length >= i - 1)
			name = name.replace(one, "[" + vars[i] + "]");
		    i++;
		}

		return "%" + name + "%";
	    }
	    return "%" + this.getName() + "%";
	}

	public String getMVdW() {
	    if (this.isComplex()) {
		String name = this.getName();
		int i = 0;
		for (String one : this.getName().split("_")) {
		    if (!one.startsWith("$"))
			continue;
		    if (vars.length >= i - 1)
			name = name.replace(one, "*");
		    i++;
		}

		return name;
	    }
	    return this.getName();
	}

	public List<String> getComplexRegexMatchers(String text) {
	    List<String> lsInLs = new ArrayList<>();
	    if (!this.isComplex())
		return lsInLs;

	    Matcher matcher = this.getRule().getMatcher(text);
	    if (matcher == null)
		return lsInLs;
	    while (matcher.find()) {
		lsInLs.add(matcher.group());
	    }
	    return lsInLs;
	}

	public List<String> getComplexValues(String text) {

	    List<String> lsInLs = new ArrayList<>();
	    if (!this.isComplex() || text == null)
		return lsInLs;

	    Matcher matcher = this.getRule().getMatcher(text);
	    if (matcher == null)
		return lsInLs;
	    while (matcher.find()) {
		try {
		    for (Integer oneG : groups) {
			lsInLs.add(matcher.group(oneG + 1));
		    }
		} catch (Exception e) {
		}
		break;
	    }
	    return lsInLs;
	}

	public boolean isComplex() {
	    return rule != null;
	}

	public ChatFilterRule getRule() {
	    return rule;
	}

	public void setRule(ChatFilterRule rule) {
	    this.rule = rule;
	}

	public boolean isHidden() {
	    return hidden;
	}
    }

    public List<String> updatePlaceHolders(Player player, List<String> messages) {
	List<String> ms = new ArrayList<>(messages);
	for (int i = 0, l = messages.size(); i < l; ++i) {
	    ms.set(i, updatePlaceHolders(player, messages.get(i)));
	}
	return ms;
    }

    public enum JobsPlaceholderType {
	Jobs, PAPI, MVdW;
    }

    public JobsPlaceholderType getPlaceHolderType(Player player, String placeholder) {
	if (placeholder == null)
	    return null;
	if (placeholder.contains("%")) {
	    if (!placeholder.equals(translateOwnPlaceHolder(player, placeholder)))
		return JobsPlaceholderType.Jobs;
	}
	if (plugin.isPlaceholderAPIEnabled()) {
	    if (placeholder.contains("%")) {
		if (!placeholder.equals(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((OfflinePlayer) player, placeholder)))
		    return JobsPlaceholderType.PAPI;
	    }
	}
//	For MVdWPlaceholderAPI
//	if (plugin.isMVdWPlaceholderAPIEnabled()) {
//	    if (placeholder.contains("{"))
//		if (!placeholder.equals(be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, placeholder)))
//		    return CMIPlaceholderType.MVdW;
//	}
	return null;
    }

    public String updatePlaceHolders(Player player, String message) {
	if (message == null)
	    return null;
	if (message.contains("%"))
	    message = translateOwnPlaceHolder(player, message);
	if (plugin.isPlaceholderAPIEnabled() && message.contains("%")) {
	    message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((OfflinePlayer) player, message);
	}
//	For MVdWPlaceholderAPI
//	if (plugin.isMVdWPlaceholderAPIEnabled()) {
//	    if (message.contains("{"))
//		message = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, message);
//	}

	return message;
    }

    private String translateOwnPlaceHolder(Player player, String message) {
	if (message == null)
	    return null;

	if (message.contains("%")) {
	    Matcher match = placeholderPatern.matcher(message);
	    while (match.find()) {
		try {
		    String cmd = match.group(2);
		    if (!message.contains("%"))
			break;
		    JobsPlaceHolders place = JobsPlaceHolders.getByNameExact(cmd);
		    if (place == null)
			continue;
		    String group = match.group();
		    String with = this.getValue(player, place, group);
		    if (with == null)
			with = "";
		    if (with.startsWith("$"))
			with = "\\" + with;
		    message = message.replaceFirst(group, with);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return message;
    }

    public String getValue(Player player, JobsPlaceHolders placeHolder) {
	return getValue(player, placeHolder, null);
    }

    public String getValue(Player player, JobsPlaceHolders placeHolder, String value) {
	return getValue(player != null ? player.getUniqueId() : null, placeHolder, value);
    }

    private static JobProgression getProgFromValue(JobsPlayer user, String value) {
	JobProgression j = null;
	try {
	    int id = Integer.parseInt(value);
	    if (id > 0)
		j = user.getJobProgression().get(id - 1);
	} catch (Exception e) {
	    Job job = Jobs.getJob(value);
	    if (job != null)
		j = user.getJobProgression(job);
	}
	return j;
    }

    private static Job getJobFromValue(String value) {
	Job j = null;
	try {
	    int id = Integer.parseInt(value);
	    if (id > 0)
		j = Jobs.getJobs().get(id - 1);
	} catch (Exception e) {
	    j = Jobs.getJob(value);
	}
	return j;
    }

    private static String simplifyDouble(double value) {
	return String.valueOf((int) (value * 100) / 100D);
    }

    public String getValue(UUID uuid, JobsPlaceHolders placeHolder, String value) {
	if (placeHolder == null)
	    return null;

	JobsPlayer user = uuid == null ? null : Jobs.getPlayerManager().getJobsPlayer(uuid);
	// Placeholders by JobsPlayer object
	if (user != null) {
	    NumberFormat format;
	    switch (placeHolder) {
	    case user_dailyquests_pending:
		Integer pendingQuests = (int) user.getQuestProgressions().stream().filter(q -> !q.isCompleted()).count();
		return Integer.toString(pendingQuests);
	    case user_dailyquests_completed:
		Integer completedQuests = (int) user.getQuestProgressions().stream().filter(q -> q.isCompleted()).count();
		return Integer.toString(completedQuests);
	    case user_dailyquests_total:
		Integer dailyquests = user.getQuestProgressions().size();
		return Integer.toString(dailyquests);
	    case user_id:
		return Integer.toString(user.getUserId());
	    case user_bstandcount:
		return Integer.toString(user.getBrewingStandCount());
	    case user_maxbstandcount:
		return Integer.toString(user.getMaxOwnerShipAllowed(BlockTypes.BREWING_STAND));
	    case user_furncount:
		return Integer.toString(user.getFurnaceCount());
	    case user_maxfurncount:
		return Integer.toString(user.getMaxOwnerShipAllowed(BlockTypes.FURNACE));
	    case user_doneq:
		return Integer.toString(user.getDoneQuests());
	    case user_seen:
		return TimeManage.to24hourShort(System.currentTimeMillis() - user.getSeen());
	    case user_totallevels:
		return Integer.toString(user.getTotalLevels());
	    case user_points:
		DecimalFormat dec = new DecimalFormat("00.0");
		return dec.format(user.getPointsData().getCurrentPoints());
	    case user_points_fixed:
		return Integer.toString((int) user.getPointsData().getCurrentPoints());
	    case user_total_points:
		format = NumberFormat.getInstance(Locale.ENGLISH);
		return format.format(user.getPointsData().getTotalPoints());
	    case user_issaved:
		return convert(user.isSaved());
	    case user_displayhonorific:
		return user.getDisplayHonorific();
	    case user_joinedjobcount:
		return Integer.toString(user.getJobProgression().size());
	    case user_archived_jobs:
		return Integer.toString(user.getArchivedJobs().getArchivedJobs().size());
	    case user_jobs:
		List<JobProgression> l = user.getJobProgression();
		if (l.isEmpty()) {
		    return "";
		}

		JobProgression prog = l.get(ThreadLocalRandom.current().nextInt(l.size()));
		return prog.getJob().getName();
	    default:
		break;
	    }

	    if (placeHolder.isComplex()) {
		List<String> vals = placeHolder.getComplexValues(value);
		if (vals.isEmpty())
		    return "";

		JobProgression j = getProgFromValue(user, vals.get(0));
		switch (placeHolder) {
		case limit_$1:
		    CurrencyType t = CurrencyType.getByName(vals.get(0));
		    return Integer.toString(user.getLimit(t));
		case plimit_$1:
		    t = CurrencyType.getByName(vals.get(0));
		    return Double.toString(user.getPaymentLimit().getAmount(t));
		case plimit_tleft_$1:
		    t = CurrencyType.getByName(vals.get(0));
		    return TimeManage.to24hourShort(user.getPaymentLimit().getLeftTime(t));
		case user_jlevel_$1:
		    return j == null ? "0" : Integer.toString(j.getLevel());
		case user_jexp_$1:
		    format = NumberFormat.getInstance(Locale.ENGLISH);
		    return j == null ? "0" : format.format(j.getExperience());
		case user_jmaxexp_$1:
		    format = NumberFormat.getInstance(Locale.ENGLISH);
		    return j == null ? "0" : format.format(j.getMaxExperience());
		case user_jexpunf_$1:
		    return j == null ? "0" : Double.toString(j.getExperience());
		case user_jmaxexpunf_$1:
		    return j == null ? "0" : Integer.toString(j.getMaxExperience());
		case user_jmaxlvl_$1:
		    return j == null ? "0" : Integer.toString(j.getJob().getMaxLevel(user));
		case user_boost_$1_$2:
		    return vals.size() < 2 || j == null ? "" : simplifyDouble(user.getBoost(j.getJob().getName(),
				CurrencyType.getByName(vals.get(1))));
		case user_jtoplvl_$1_$2:
		    vals = placeHolder.getComplexValues(value);
		    if (vals.isEmpty() || vals.size() < 2)
			return "";

		    Job job = getJobFromValue(vals.get(0));
		    if (job == null)
			return "";

			int amount = 0;
			try {
			    amount = Integer.parseInt(vals.get(1));
			} catch (NumberFormatException e) {
			    return "";
			}

			final int top = amount;
			return CompletableFuture.supplyAsync(() -> {
			    for (TopList l : Jobs.getJobsDAO().getGlobalTopList(top)) {
				if (l.getPlayerInfo().getName().equals(user.getName())) {
				    JobProgression prog = l.getPlayerInfo().getJobsPlayer().getJobProgression(job);
				    return prog == null ? "" : Integer.toString(prog.getLevel());
				}
			    }

			    return "";
			}).join();
		case user_isin_$1:
		    vals = placeHolder.getComplexValues(value);
		    if (vals.isEmpty())
			return "";

		    Job jobs = getJobFromValue(vals.get(0));
		    return jobs == null ? "no" : convert(user.isInJob(jobs));
		case user_job_$1:
		    return j == null ? "" : j.getJob().getName();
		case user_title_$1:
		    if (j == null)
			return "";
		    Title title = Jobs.gettitleManager().getTitle(j.getLevel(), j.getJob().getName());
		    return title == null ? "" : title.getChatColor() + title.getName();
		default:
		    break;
		}
	    }

	    // Placeholders by player object
	    if (user.isOnline()) {
		Player player = user.getPlayer();
		if (player != null) {
		    switch (placeHolder) {
		    case user_canjoin_$1:
			List<String> values = placeHolder.getComplexValues(value);
			if (values.isEmpty())
			    return "";

			Job job = getJobFromValue(values.get(0));
			if (job == null)
			    return "";

			if (!Jobs.getCommandManager().hasJobPermission(player, job))
			    return convert(false);

			if (user.isInJob(job))
			    return convert(false);

			if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots())
			    return convert(false);

			int confMaxJobs = Jobs.getGCManager().getMaxJobs();
			short PlayerMaxJobs = (short) user.getJobProgression().size();
			if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !Jobs.getPlayerManager().getJobsLimit(user, PlayerMaxJobs))
			    return convert(false);

			return convert(true);

			case maxjobs:
			    int max = Jobs.getPermissionManager().getMaxPermission(user, "jobs.max", false).intValue();
			    max = max == 0 ? Jobs.getGCManager().getMaxJobs() : max;
			    return Integer.toString(max);

		    default:
			break;
	    }
		}
	    }
	}

	if (placeHolder.isComplex()) {
	    List<String> values = placeHolder.getComplexValues(value);
	    if (values.isEmpty())
		return "";

	    Job jo = getJobFromValue(values.get(0));
	    if (jo == null)
		return "";
	    // Global placeholders by jobname
	    switch (placeHolder) {
	    case name_$1:
		return jo.getName();
	    case shortname_$1:
		return jo.getShortName();
	    case chatcolor_$1:
		return jo.getChatColor().toString();
	    case description_$1:
		return jo.getDescription();
	    case maxdailyq_$1:
		return Integer.toString(jo.getMaxDailyQuests());
	    case maxlvl_$1:
		return Integer.toString(jo.getMaxLevel());
	    case maxviplvl_$1:
		return Integer.toString(jo.getVipMaxLevel());
	    case bonus_$1:
		return Double.toString(jo.getBonus());
	    case totalplayers_$1:
		return Integer.toString(jo.getTotalPlayers());
	    case maxslots_$1:
		return Integer.toString(jo.getMaxSlots());
	    default:
		break;
	    }
	}

	// Global placeholders
	switch (placeHolder) {
	case maxjobs:
	    return Integer.toString(Jobs.getGCManager().getMaxJobs());
	case total_workers:
	    return Integer.toString(Jobs.getJobsDAO().getTotalPlayers());
	default:
	    break;
	}

	return null;
    }

    private String convert(boolean state) {
	return state ? Jobs.getLanguage().getMessage("general.info.true") : Jobs.getLanguage().getMessage("general.info.false");
    }
}
