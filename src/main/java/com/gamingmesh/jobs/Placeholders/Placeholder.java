package com.gamingmesh.jobs.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

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
	user_totallevels,
	user_displayhonorific,
	user_joinedjobcount,
	maxjobs,
	name_$1("number/name"),
	shortname_$1("number/name"),
	chatcolor_$1("number/name"),
	description_$1("number/name"),
	maxdailyq_$1("number/name"),
	maxlvl_$1("number/name"),
	maxviplvl_$1("number/name"),
	totalplayers_$1("number/name"),
	maxslots_$1("number/name"),
	user_boost_$1_$2("jobname/number", "money/exp/points"),
	user_isin_$1("jobname/number"),
	user_canjoin_$1("jobname/number");

	private String[] vars;
	private List<Integer> groups = new ArrayList<Integer>();
	private ChatFilterRule rule = null;
	private boolean hidden = false;

	JobsPlaceHolders() {
	}

	JobsPlaceHolders(String... vars) {
	    Matcher matcher = numericalRule.getMatcher(this.toString());
	    if (matcher != null) {
		rule = new ChatFilterRule();
		List<String> ls = new ArrayList<String>();
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
//	    name = name.replace("_", "");
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex())
		    continue;
//		String n = one.name().replace("_", "");
		if (one.name().equalsIgnoreCase(name)) {
		    return one;
		}
	    }
	    name = pref + name;
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (one.isComplex())
		    continue;
		String n = one.name();
		if (n.equalsIgnoreCase(name)) {
		    return one;
		}
	    }
	    name = "%" + pref + "_" + original + "%";
	    for (JobsPlaceHolders one : JobsPlaceHolders.values()) {
		if (!one.isComplex())
		    continue;
		if (!one.getComplexRegexMatchers(name).isEmpty()) {
		    return one;
		}
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
		    String n = one.name();
		    if (n.equals(name))
			return one;
		}
	    }
	    return null;
	}

	public String getFull() {
	    if (this.isComplex()) {
		String name = this.name();
		int i = 0;
		for (String one : this.name().split("_")) {
		    if (!one.startsWith("$"))
			continue;
		    if (vars.length >= i - 1)
			name = name.replace(one, "[" + vars[i] + "]");
		    i++;
		}

		return "%" + name + "%";
	    }
	    return "%" + this.name() + "%";
	}

	public String getMVdW() {
	    if (this.isComplex()) {
		String name = this.name();
		int i = 0;
		for (String one : this.name().split("_")) {
		    if (!one.startsWith("$"))
			continue;
		    if (vars.length >= i - 1)
			name = name.replace(one, "*");
		    i++;
		}

		return name;
	    }
	    return this.name();
	}

	public List<String> getComplexRegexMatchers(String text) {
	    List<String> lsInLs = new ArrayList<String>();
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

	    List<String> lsInLs = new ArrayList<String>();
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
	List<String> ms = new ArrayList<String>(messages);
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
	    try {
		if (placeholder.contains("%"))
		    if (!placeholder.equals(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder)))
			return JobsPlaceholderType.PAPI;
	    } catch (Exception e) {

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
	if (plugin.isPlaceholderAPIEnabled()) {
	    try {
		if (message.contains("%"))
		    message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
	    } catch (Exception e) {

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
//		    e.printStackTrace();
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
	JobsPlayer user = Jobs.getPlayerManager().getJobsPlayer(uuid);

	if (placeHolder == null)
	    return null;
	// Placeholders by JobsPLayer object
	if (user != null) {
	    switch (placeHolder) {
	    case user_id:
		return String.valueOf(user.getUserId());
	    case user_bstandcount:
		return String.valueOf(user.getBrewingStandCount());
	    case user_maxbstandcount:
		return String.valueOf(user.getMaxBrewingStandsAllowed());
	    case user_furncount:
		return String.valueOf(user.getFurnaceCount());
	    case user_maxfurncount:
		return String.valueOf(user.getMaxFurnacesAllowed());
	    case user_doneq:
		return String.valueOf(user.getDoneQuests());
	    case user_totallevels:
		return String.valueOf(user.getTotalLevels());
	    case user_displayhonorific:
		return String.valueOf(user.getDisplayHonorific());
	    case user_joinedjobcount:
		return String.valueOf(user.getJobProgression().size());
	    case user_boost_$1_$2:
		List<String> values = placeHolder.getComplexValues(value);
		if (values.size() < 2)
		    return "";
		JobProgression j = getProgFromValue(user, values.get(0));
		if (j == null)
		    return "";
		return simplifyDouble(user.getBoost(j.getJob().getName(), CurrencyType.getByName(values.get(1))));
	    case user_isin_$1:
		values = placeHolder.getComplexValues(value);
		if (values.isEmpty())
		    return "";
		Job job = getJobFromValue(values.get(0));
		if (job == null)
		    return "";
		return convert(user.isInJob(job));

	    default:
		break;
	    }
	}

	// Placeholders by player object
	if (user != null && user.isOnline()) {
	    Player player = user.getPlayer();
	    if (player != null) {
		List<String> values;
		switch (placeHolder) {

		case user_canjoin_$1:
		    values = placeHolder.getComplexValues(value);
		    if (values.isEmpty())
			return "";

		    Job job = getJobFromValue(values.get(0));
		    if (job == null)
			return "";

		    if (!Jobs.getCommandManager().hasJobPermission(player, job)) {
			return convert(false);
		    }

		    if (user.isInJob(job)) {
			return convert(false);
		    }

		    if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
			return convert(false);
		    }

		    int confMaxJobs = Jobs.getGCManager().getMaxJobs();
		    short PlayerMaxJobs = (short) user.getJobProgression().size();
		    if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !Jobs.getPlayerManager().getJobsLimit(user, PlayerMaxJobs)) {
			return convert(false);
		    }

		    return convert(true);

		default:
		    break;
		}
	    }
	}

	List<String> values = new ArrayList<String>();

	if (placeHolder.isComplex()) {
	    values = placeHolder.getComplexValues(value);
	    if (values.isEmpty())
		return "";
	    Job j = getJobFromValue(values.get(0));

	    // Global placeholders by jobname
	    switch (placeHolder) {
	    case name_$1:
		return j == null ? "" : j.getName();
	    case shortname_$1:
		return j == null ? "" : j.getShortName();
	    case chatcolor_$1:
		return j == null ? "" : j.getChatColor().toString();
	    case description_$1:
		return j == null ? "" : j.getDescription();
	    case maxdailyq_$1:
		return j == null ? "" : String.valueOf(j.getMaxDailyQuests());
	    case maxlvl_$1:
		return j == null ? "" : String.valueOf(j.getMaxLevel());
	    case maxviplvl_$1:
		return j == null ? "" : String.valueOf(j.getVipMaxLevel());
	    case totalplayers_$1:
		return j == null ? "" : String.valueOf(j.getTotalPlayers());
	    case maxslots_$1:
		return j == null ? "" : String.valueOf(j.getMaxSlots());
	    default:
		break;
	    }
	}

	// Global placeholders
	switch (placeHolder) {
	case maxjobs:
	    return String.valueOf(Jobs.getGCManager().getMaxJobs());
	}

	return null;
    }

    private String convert(boolean state) {
	return state ? Jobs.getLanguage().getMessage("general.info.true") : Jobs.getLanguage().getMessage("general.info.false");
    }
}
