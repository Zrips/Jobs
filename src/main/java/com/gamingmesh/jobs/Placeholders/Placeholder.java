package com.gamingmesh.jobs.Placeholders;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.container.Title;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
import com.gamingmesh.jobs.stuff.TimeManage;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;

public class Placeholder {

    private Jobs plugin;

    private final AtomicInteger jobLevel = new AtomicInteger();
    private final Pattern placeholderPatern = Pattern.compile("(%)([^\"^%]*)(%)");

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
	user_smokercount,
	user_maxsmokercount,
	user_blastcount,
	user_maxblastcount,
	user_doneq,
	user_dailyquests_pending,
	user_dailyquests_completed,
	user_dailyquests_total,
	user_quests,
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
	user_jmexp_$1("jname/number"),
	user_jprogress_$1("jname/number"),
	user_jexp_rounded_$1("jname/number"),
	user_jmaxexp_$1("jname/number"),
	user_jexpunf_$1("jname/number"),
	user_jmaxexpunf_$1("jname/number"),
	user_jmaxlvl_$1("jname/number"),
	user_job_$1("jname/number"),
	user_title_$1("jname/number"),
	user_archived_jobs_level_$1("jname/number"),
	user_archived_jobs_exp_$1("jname/number"),

	maxjobs,
	total_workers,

	limit_$1("money/exp/points"),
	plimit_$1("money/exp/points"),
	plimit_tleft_$1("money/exp/points"),

	name_$1("jname/number"),
	shortname_$1("jname/number"),
	chatcolor_$1("jname/number"),
	description_$1("jname/number"),
	maxdailyq_$1("jname/number"),
	maxlvl_$1("jname/number"),
	maxviplvl_$1("jname/number"),
	totalplayers_$1("jname/number"),
	maxslots_$1("jname/number"),
	questname_$1_$2("jname/number", "questIndicator"),
	questdesc_$1_$2("jname/number", "questIndicator"),
	bonus_$1("jname/number");

	private String[] vars;
	private List<Integer> groups = new ArrayList<>();
	private ChatFilterRule rule;

	JobsPlaceHolders(String... vars) {
	    Matcher matcher = numericalRule.getMatcher(toString());
	    if (matcher != null) {
		rule = new ChatFilterRule();

		List<String> ls = new ArrayList<>();
		ls.add("(%" + pref + "_)" + toString().replaceAll("\\$\\d", "([^\"^%]*)") + "(%)");

//		For MVdWPlaceholderAPI
//		ls.add("(\\{" + pref + toString().replaceAll("\\$\\d", "([^\"^%]*)" + "(\\})"));

		rule.setPattern(ls);

		while (matcher.find()) {
		    groups.add(Integer.parseInt(matcher.group(1).substring(1)));
		}
	    }

	    this.vars = vars;
	}

	public static JobsPlaceHolders getByName(String name) {
	    String original = name;

	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (!one.isComplex() && one.toString().equalsIgnoreCase(name))
		    return one;
	    }

	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (!one.isComplex() && one.getName().equalsIgnoreCase(name))
		    return one;
	    }

	    name = pref + name;
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (!one.isComplex() && one.getName().equalsIgnoreCase(name))
		    return one;
	    }

	    JobsPlaceHolders bestMatch = null;
	    name = "%" + pref + "_" + original + "%";
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex() && !one.getComplexRegexMatchers(name).isEmpty())
		    bestMatch = one;
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

	    return bestMatch;
	}

	public static JobsPlaceHolders getByNameExact(String name) {
	    name = name.toLowerCase();

	    // Should iterate over all placeholders to match the correct one
	    // for example with %jobsr_plimit_tleft_money%
	    JobsPlaceHolders bestMatch = null;
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex()) {
		    if (!one.getComplexRegexMatchers("%" + name + "%").isEmpty()) {
			bestMatch = one;
		    }
		} else if (one.getName().equals(name)) {
		    bestMatch = one;
		}
	    }

	    return bestMatch;
	}

	public String getName() {
	    return pref + "_" + name();
	}

	public String getFull() {
	    if (isComplex()) {
		String name = getName();
		int i = 0;

		for (String one : name.split("_")) {
		    if (!one.startsWith("$"))
			continue;

		    if (vars.length >= i - 1)
			name = name.replace(one, "[" + vars[i] + "]");

		    i++;
		}

		return "%" + name + "%";
	    }

	    return "%" + getName() + "%";
	}

	/*public String getMVdW() {
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
	}*/

	public List<String> getComplexRegexMatchers(String text) {
	    List<String> lsInLs = new ArrayList<>();
	    if (!isComplex())
		return lsInLs;

	    Matcher matcher = rule.getMatcher(text);
	    if (matcher == null)
		return lsInLs;

	    while (matcher.find()) {
		lsInLs.add(matcher.group());
	    }

	    return lsInLs;
	}

	public List<String> getComplexValues(String text) {
	    List<String> lsInLs = new ArrayList<>();

	    if (text == null || !isComplex())
		return lsInLs;

	    Matcher matcher = rule.getMatcher(text);
	    if (matcher != null && matcher.find()) {
		try {
		    for (Integer oneG : groups) {
			lsInLs.add(matcher.group(oneG + 1));
		    }
		} catch (Exception e) {
		}
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
    }

    public List<String> updatePlaceHolders(Player player, List<String> messages) {
	List<String> ms = new ArrayList<>(messages);
	for (int i = 0, l = messages.size(); i < l; ++i) {
	    ms.set(i, updatePlaceHolders(player, messages.get(i)));
	}
	return ms;
    }

    public enum JobsPlaceholderType {
	JOBS, PAPI, MVDW;
    }

    public JobsPlaceholderType getPlaceHolderType(Player player, String placeholder) {
	if (placeholder == null)
	    return null;

	if (placeholder.contains("%") && !placeholder.equals(translateOwnPlaceHolder(player, placeholder))) {
	    return JobsPlaceholderType.JOBS;
	}

	if (plugin.isPlaceholderAPIEnabled() && placeholder.contains("%")
	    && !placeholder.equals(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((OfflinePlayer) player, placeholder))) {
	    return JobsPlaceholderType.PAPI;
	}

//	For MVdWPlaceholderAPI
//	if (plugin.isMVdWPlaceholderAPIEnabled()) {
//	    if (placeholder.contains("{"))
//		if (!placeholder.equals(be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, placeholder)))
//		    return CMIPlaceholderType.MVDW;
//	}
	return null;
    }

    public String updatePlaceHolders(Player player, String message) {
	if (message == null)
	    return null;

	if (message.contains("%")) {
	    message = translateOwnPlaceHolder(player, message);

	    if (plugin.isPlaceholderAPIEnabled()) {
		message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((OfflinePlayer) player, message);
	    }
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
		if (!message.contains("%"))
		    break;

		JobsPlaceHolders place = JobsPlaceHolders.getByNameExact(match.group(2));
		if (place == null)
		    continue;

		String group = match.group();
		String with = getValue(player, place, group);
		if (with == null)
		    with = "";

		if (with.startsWith("$"))
		    with = "\\" + with;

		message = message.replaceFirst(group, with);
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
	try {
	    int id = Integer.parseInt(value);
	    if (id > 0)
		return user.progression.get(id - 1);
	} catch (IndexOutOfBoundsException | NumberFormatException e) {
	    Job job = Jobs.getJob(value);
	    if (job != null)
		return user.getJobProgression(job);
	}

	return null;
    }

    private static Job getJobFromValue(String value) {
	try {
	    int id = Integer.parseInt(value);
	    if (id > 0)
		return Jobs.getJobs().get(id - 1);
	} catch (IndexOutOfBoundsException | NumberFormatException e) {
	    return Jobs.getJob(value);
	}
	return null;
    }

    private String simplifyDouble(double value) {
	return String.valueOf((int) (value * 100) / 100D);
    }

    public String getValue(UUID uuid, JobsPlaceHolders placeHolder, String value) {
	if (placeHolder == null)
	    return null;

	JobsPlayer user = uuid == null ? null : Jobs.getPlayerManager().getJobsPlayer(uuid);
	// Placeholders by JobsPlayer object
	if (user != null) {
	    NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);

	    switch (placeHolder) {
	    case user_dailyquests_pending:
		Integer pendingQuests = (int) user.getQuestProgressions().stream().filter(q -> !q.isCompleted()).count();
		return Integer.toString(pendingQuests);
	    case user_dailyquests_completed:
		Integer completedQuests = (int) user.getQuestProgressions().stream().filter(q -> q.isCompleted()).count();
		return Integer.toString(completedQuests);
	    case user_dailyquests_total:
		return Integer.toString(user.getQuestProgressions().size());
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
	    case user_smokercount:
		Optional<BlockOwnerShip> blastSmoker = plugin.getBlockOwnerShip(BlockTypes.SMOKER);
		return !blastSmoker.isPresent() ? "0" : Integer.toString(blastSmoker.get().getTotal(uuid));
	    case user_maxsmokercount:
		return Integer.toString(user.getMaxOwnerShipAllowed(BlockTypes.SMOKER));
	    case user_blastcount:
		Optional<BlockOwnerShip> blastShip = plugin.getBlockOwnerShip(BlockTypes.BLAST_FURNACE);
		return !blastShip.isPresent() ? "0" : Integer.toString(blastShip.get().getTotal(uuid));
	    case user_maxblastcount:
		return Integer.toString(user.getMaxOwnerShipAllowed(BlockTypes.BLAST_FURNACE));
	    case user_doneq:
		return Integer.toString(user.getDoneQuests());
	    case user_seen:
		return TimeManage.to24hourShort(System.currentTimeMillis() - user.getSeen());
	    case user_totallevels:
		return Integer.toString(user.getTotalLevels());
	    case user_points:
		return new DecimalFormat("00.0").format(user.getPointsData().getCurrentPoints());
	    case user_points_fixed:
		return Integer.toString((int) user.getPointsData().getCurrentPoints());
	    case user_total_points:
		return format.format(user.getPointsData().getTotalPoints());
	    case user_issaved:
		return convert(user.isSaved());
	    case user_displayhonorific:
		return user.getDisplayHonorific();
	    case user_joinedjobcount:
		return Integer.toString(user.progression.size());
	    case user_archived_jobs:
		return Integer.toString(user.getArchivedJobs().getArchivedJobs().size());
	    case user_jobs:
		String jobNames = "";
		for (JobProgression prog : user.progression) {
		    if (!jobNames.isEmpty()) {
			jobNames += LC.info_ListSpliter.getLocale();
		    }
		    jobNames += prog.getJob().getName();
		}
		return jobNames;
	    case user_quests:
		String q = "";
		for (QuestProgression questProg : user.getQuestProgressions()) {
		    Quest quest = questProg.getQuest();
		    if (quest == null || quest.isStopped()) {
			continue;
		    }
		    if (!q.isEmpty()) {
			q += LC.info_ListSpliter.getLocale();
		    }
		    q += quest.getQuestName();
		}
		return q;
	    default:
		break;
	    }

	    if (placeHolder.isComplex()) {
		List<String> vals = placeHolder.getComplexValues(value);
		if (vals.isEmpty())
		    return "";

		String keyValue = vals.get(0);
		JobProgression j = getProgFromValue(user, keyValue);
		Job job = getJobFromValue(keyValue);

		switch (placeHolder) {
		case limit_$1:
		    return Integer.toString(user.getLimit(CurrencyType.getByName(keyValue)));
		case plimit_$1:
		    return Double.toString(user.getPaymentLimit().getAmount(CurrencyType.getByName(keyValue)));
		case plimit_tleft_$1:
		    return TimeManage.to24hourShort(user.getPaymentLimit().getLeftTime(CurrencyType.getByName(keyValue)));
		case user_jlevel_$1:
		    return j == null ? "0" : j.getLevelFormatted();
		case user_jexp_$1:
		    return j == null ? "0" : format.format(j.getExperience());
		case user_jmexp_$1:
		    return j == null ? "0" : format.format(j.getMaxExperience() - j.getExperience());
		case user_jprogress_$1:
		    return j == null ? "" : Jobs.getCommandManager().jobProgressMessage(j.getMaxExperience(), j.getExperience());
		case user_jexp_rounded_$1:
		    return j == null ? "0" : new DecimalFormat("##.###").format(j.getExperience());
		case user_jmaxexp_$1:
		    return j == null ? "0" : format.format(j.getMaxExperience());
		case user_jexpunf_$1:
		    return j == null ? "0" : Double.toString(j.getExperience());
		case user_jmaxexpunf_$1:
		    return j == null ? "0" : Integer.toString(j.getMaxExperience());
		case user_jmaxlvl_$1:
		    return j == null ? "0" : Integer.toString(j.getJob().getMaxLevel(user));
		case user_boost_$1_$2:
		    Boost boost = Jobs.getPlayerManager().getFinalBonus(user, job, true, true);
		    return (vals.size() < 2 || j == null) ? "" : simplifyDouble(boost.getFinal(CurrencyType.getByName(vals.get(1)), false, true));
		case user_jtoplvl_$1_$2:
		    if (vals.size() < 2 || job == null)
			return "";

		    try {
			jobLevel.set(Integer.parseInt(vals.get(1)));
		    } catch (NumberFormatException e) {
			return "";
		    }

		    return CompletableFuture.supplyAsync(() -> {
			for (TopList l : Jobs.getJobsDAO().getGlobalTopList(jobLevel.get())) {
			    if (l.getPlayerInfo().getName().equals(user.getName())) {
				JobProgression prog = l.getPlayerInfo().getJobsPlayer().getJobProgression(job);
				return prog == null ? "" : prog.getLevelFormatted();
			    }
			}

			return "";
		    }).join();
		case user_isin_$1:
		    return job == null ? "no" : convert(user.isInJob(job));
		case user_job_$1:
		    return j == null ? "" : j.getJob().getName();
		case user_title_$1:
		    if (j == null)
			return "";
		    Title title = Jobs.getTitleManager().getTitle(j.getLevel(), j.getJob().getName());
		    return title == null ? "" : title.getChatColor() + title.getName();
		case user_archived_jobs_level_$1:
		    if (job == null) {
			return "";
		    }

		    JobProgression archivedJobProg = user.getArchivedJobProgression(job);
		    return archivedJobProg == null ? "" : archivedJobProg.getLevelFormatted();
		case user_archived_jobs_exp_$1:
		    if (job == null)
			return "";

		    JobProgression archivedJobProgression = user.getArchivedJobProgression(job);
		    return archivedJobProgression == null ? "0" : Double.toString(archivedJobProgression.getExperience());
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

			if (!Jobs.getCommandManager().hasJobPermission(player, job) ||
			    user.isInJob(job) ||
			    job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots() ||
			    !job.isIgnoreMaxJobs() && !Jobs.getPlayerManager().getJobsLimit(user, (short) user.progression.size()))
			    return convert(false);

			return convert(true);

		    case maxjobs:
			return Integer.toString(Jobs.getPlayerManager().getMaxJobs(user));

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
	    case questname_$1_$2:
		Quest quest = jo.getQuest(values.get(1));
		if (quest == null)
		    return null;
		return CMIChatColor.translate(quest.getQuestName());
	    case questdesc_$1_$2:
		quest = jo.getQuest(values.get(1));
		if (quest == null)
		    return null;
		return CMIChatColor.translate(CMIList.listToString(quest.getDescription(), "\n"));
	    default:
		break;
	    }
	}

	// Global placeholders
	switch (placeHolder) {
	case maxjobs:
	    return Integer.toString(Jobs.getPlayerManager().getMaxJobs(user));
	case total_workers:
	    return Integer.toString(Jobs.getJobsDAO().getTotalPlayers());
	default:
	    break;
	}

	return null;
    }

    private static String convert(boolean state) {
	return Jobs.getLanguage().getMessage("general.info." + (state ? "true" : "false"));
    }
}
