package com.gamingmesh.jobs.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ActionBarManager;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Title;
import com.gamingmesh.jobs.stuff.PageInfo;
import com.gamingmesh.jobs.stuff.Sorting;
import com.gamingmesh.jobs.stuff.Util;

public class JobsCommands implements CommandExecutor {

    public static final String label = "jobs";

    private static final String packagePath = "com.gamingmesh.jobs.commands.list";

    private final Map<String, Integer> CommandList = new HashMap<>();

    protected Jobs plugin;

    public JobsCommands(Jobs plugin) {
	this.plugin = plugin;
    }

    public Map<String, Integer> getCommands() {
	return CommandList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player && !Jobs.getGCManager().canPerformActionInWorld(((Player) sender).getWorld())) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.worldisdisabled"));
	    return true;
	}

	if (args.length == 0)
	    return help(sender, 1);

	if ((args.length == 1 || args.length == 2) && (args[0].equals("?") || args[0].equalsIgnoreCase("help"))) {
	    int page = 1;
	    if (args.length == 2)
		try {
		    page = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.notNumber"));
		    return true;
		}
	    return help(sender, page);
	}

	String cmd = args[0].toLowerCase();
	Cmd cmdClass = getCmdClass(cmd);
	if (cmdClass == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noCommand"));
	    return true;
	}

	if (!hasCommandPermission(sender, cmd)) {
	    if (sender instanceof Player) {
		new RawMessage().addText(Jobs.getLanguage().getMessage("general.error.permission"))
		    .addHover("&2" + label + ".command." + cmd).show(sender);
	    } else
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}

	String[] myArgs = reduceArgs(args);
	if (myArgs.length > 0 && myArgs[myArgs.length - 1].equals("?")) {
	    sendUsage(sender, cmd);
	    return true;
	}

	boolean back = cmdClass.perform(plugin, sender, myArgs);
	if (back)
	    return true;

	if (!(sender instanceof Player))
	    return help(sender, 1);

	return help(sender, 1);
    }

    private static String[] reduceArgs(String[] args) {
	return args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
    }

    private static boolean hasCommandPermission(CommandSender sender, String cmd) {
	return sender.hasPermission("jobs.command." + cmd);
    }

    private static String getUsage(String cmd) {
	String cmdString = Jobs.getLanguage().getMessage("command.help.output.cmdFormat", "[command]", Jobs.getLanguage().getMessage("command.help.output.label") + " " + cmd);
	String key = "command." + cmd + ".help.args";
	if (Jobs.getLanguage().containsKey(key) && !Jobs.getLanguage().getMessage(key).isEmpty()) {
	    cmdString = cmdString.replace("[arguments]", Jobs.getLanguage().getMessage(key));
	} else
	    cmdString = cmdString.replace("[arguments]", "");

	return cmdString;
    }

    public void sendUsage(CommandSender sender, String cmd) {
	String message = Jobs.getLanguage().getMessage("command.help.output.cmdUsage");
	message = message.replace("[command]", getUsage(cmd));
	sender.sendMessage(message);
	sender.sendMessage(Jobs.getLanguage().getMessage("command.help.output.helpPageDescription", "[description]", Jobs.getLanguage().getMessage("command." + cmd + ".help.info")));
    }

    protected boolean help(CommandSender sender, int page) {
	Map<String, Integer> commands = GetCommands(sender);
	if (commands.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}

	commands = Sorting.sortASC(commands);

	PageInfo pi = new PageInfo(7, commands.size(), page);
	if (page > pi.getTotalPages() || page < 1) {
	    ActionBarManager.send(sender, Jobs.getLanguage().getMessage("general.error.noHelpPage"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.help.output.title"));
	for (String one : commands.keySet()) {
	    if (!pi.isEntryOk())
		continue;
	    if (pi.isBreak())
		break;

	    String msg = Jobs.getLanguage().getMessage("command.help.output.cmdInfoFormat", "[command]", getUsage(one), "[description]", Jobs.getLanguage().getMessage("command." + one
		+ ".help.info"));
	    sender.sendMessage(msg);
	}

	plugin.ShowPagination(sender, pi, label + " ?");
	return true;
    }

    public Map<String, Integer> GetCommands(CommandSender sender) {
	Map<String, Integer> temp = new HashMap<>();
	for (Entry<String, Integer> cmd : CommandList.entrySet()) {
	    if (sender instanceof Player && !hasCommandPermission(sender, cmd.getKey()))
		continue;

	    temp.put(cmd.getKey(), cmd.getValue());
	}
	return temp;
    }

    public void fillCommands() {
	List<String> lm = new ArrayList<>();
	HashMap<String, Class<?>> classes = new HashMap<>();
	try {
	    lm = Util.getFilesFromPackage(packagePath);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	for (String one : lm) {
	    Class<?> newclass = getClass(one);
	    if (newclass != null)
		classes.put(one, newclass);
	}

	for (Entry<String, Class<?>> oneClass : classes.entrySet()) {
	    for (Method met : oneClass.getValue().getMethods()) {
		if (!met.isAnnotationPresent(JobCommand.class))
		    continue;

		CommandList.put(oneClass.getKey(), met.getAnnotation(JobCommand.class).value());
		break;
	    }
	}

    }

    private static Class<?> getClass(String cmd) {
	try {
	    return Class.forName(packagePath + "." + cmd.toLowerCase());
	} catch (ClassNotFoundException e) {
	}
	return null;
    }

    private static Cmd getCmdClass(String cmd) {
	try {
	    Class<?> nmsClass = Class.forName(packagePath + "." + cmd.toLowerCase());
	    if (Cmd.class.isAssignableFrom(nmsClass)) {
		return (Cmd) nmsClass.getConstructor().newInstance();
	    }
	} catch (Exception e) {
	}
	return null;
    }

    /**
     * Check Job joining permission
     */
    public boolean hasJobPermission(CommandSender sender, Job job) {
	return sender.hasPermission("jobs.use") && sender.hasPermission("jobs.join." + job.getName().toLowerCase());
    }

    public void sendValidActions(CommandSender sender) {
	StringBuilder builder = new StringBuilder();
	boolean first = true;
	for (ActionType action : ActionType.values()) {
	    if (!first)
		builder.append(',');
	    builder.append(action.getName());
	    first = false;

	}
	sender.sendMessage(Jobs.getLanguage().getMessage("command.info.help.actions", "%actions%", builder.toString()));
    }

    /**
     * Displays info about a job
     * @param player - the player of the job
     * @param job - the job we are displaying info about
     * @param type - type of info
     * @return the message
     */
    public void jobInfoMessage(CommandSender sender, JobsPlayer player, Job job, String type, int page) {
	if (job == null) {
	    // job doesn't exist
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return;
	}

	type = type == null ? "" : type.toLowerCase();

	List<String> message = new ArrayList<>();

	int showAllTypes = 1;
	for (ActionType actionType : ActionType.values()) {
	    if (type.startsWith(actionType.getName().toLowerCase())) {
		showAllTypes = 0;
		break;
	    }
	}

	if (job.getBoost().get(CurrencyType.EXP) != 0D)
	    message.add(Jobs.getLanguage().getMessage("command.expboost.output.infostats", "%boost%", (job.getBoost().get(CurrencyType.EXP)) + 1));

	if (job.getBoost().get(CurrencyType.MONEY) != 0D)
	    message.add(Jobs.getLanguage().getMessage("command.moneyboost.output.infostats", "%boost%", (job.getBoost().get(CurrencyType.MONEY)) + 1));

	if (job.getBoost().get(CurrencyType.POINTS) != 0D)
	    message.add(Jobs.getLanguage().getMessage("command.pointboost.output.infostats", "%boost%", (job.getBoost().get(CurrencyType.POINTS)) + 1));

	if (Jobs.getGCManager().useDynamicPayment) {
	    if ((int) (job.getBonus() * 100) / 100.0 != 0) {
		if ((int) (job.getBonus() * 100) / 100.0 < 0)
		    message.add(Jobs.getLanguage().getMessage("command.info.help.penalty", "[penalty]", (int) (job.getBonus() * 100) / 100.0 * -1));
		else
		    message.add(Jobs.getLanguage().getMessage("command.info.help.bonus", "[bonus]", (int) (job.getBonus() * 100) / 100.0));
	    }
	}

	for (ActionType actionType : ActionType.values()) {
	    if (showAllTypes == 1 || type.startsWith(actionType.getName().toLowerCase())) {
		List<JobInfo> info = job.getJobInfo(actionType);
		if (info != null && !info.isEmpty()) {
		    String m = jobInfoMessage(player, job, actionType);
		    if (m.contains("\n"))
			message.addAll(Arrays.asList(m.split("\n")));
		    else
			message.add(m);
		} else if (showAllTypes == 0) {
		    String myMessage = Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".none");
		    myMessage = myMessage.replace("%jobname%", job.getNameWithColor());
		    message.add(myMessage);
		}
	    }
	}

	PageInfo pi = new PageInfo(15, message.size(), page);

	if (page > pi.getTotalPages()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.info.invalidPage"));
	    return;
	}

	boolean isPlayer = sender instanceof Player;
	for (String one : message) {
	    if (isPlayer && !pi.isEntryOk())
		continue;
	    if (isPlayer && pi.isBreak())
		break;
	    sender.sendMessage(one);
	}

	String t = type == "" ? "" : " " + type;

	if (sender instanceof Player)
	    if (sender.getName().equalsIgnoreCase(player.getName()))
		plugin.ShowPagination(sender, pi, "jobs info " + job.getName() + t);
	    else
		plugin.ShowPagination(sender, pi, "jobs playerinfo " + player.getName() + " " + job.getName() + t);
    }

    /**
     * Displays info about a particular action
     * @param player - the player of the job
     * @param prog - the job we are displaying info about
     * @param type - the type of action
     * @return the message
     */
    public static String jobInfoMessage(JobsPlayer player, Job job, ActionType type) {
	// money exp boost
	Boost boost = Jobs.getPlayerManager().getFinalBonus(player, job, true);

	StringBuilder message = new StringBuilder();

	message.append(Jobs.getLanguage().getMessage("command.info.output." + type.getName().toLowerCase() + ".info"));
	message.append(":\n");

	int level = 1;

	JobProgression prog = player.getJobProgression(job);
	if (prog != null)
	    level = prog.getLevel();
	int numjobs = player.getJobProgression().size();
	List<JobInfo> jobInfo = job.getJobInfo(type);
	for (JobInfo info : jobInfo) {

	    String materialName = info.getRealisticName();

	    double income = info.getIncome(level, numjobs);

	    income = boost.getFinalAmount(CurrencyType.MONEY, income);
	    String incomeColor = income >= 0 ? "" : ChatColor.DARK_RED.toString();

	    double xp = info.getExperience(level, numjobs);
	    xp = boost.getFinalAmount(CurrencyType.EXP, xp);
	    String xpColor = xp >= 0 ? "" : ChatColor.GRAY.toString();

	    double points = info.getPoints(level, numjobs);
	    points = boost.getFinalAmount(CurrencyType.POINTS, points);
	    String pointsColor = xp >= 0 ? "" : ChatColor.RED.toString();

	    if (income == 0D && points == 0D && xp == 0D)
		continue;

	    message.append("  ");

	    message.append(Jobs.getLanguage().getMessage("command.info.help.material", "%material%", materialName));
	    if (prog != null && !info.isInLevelRange(prog.getLevel()))
		message.append(ChatColor.RED + " -> ");
	    else
		message.append(" -> ");

	    if (income != 0.0)
		message.append(Jobs.getLanguage().getMessage("command.info.help.money", "%money%", incomeColor + String.format(Jobs.getGCManager().getDecimalPlacesMoney(), income)));

	    if (points != 0.0)
		message.append(Jobs.getLanguage().getMessage("command.info.help.points", "%points%", pointsColor + String.format(Jobs.getGCManager().getDecimalPlacesPoints(), points)));

	    if (xp != 0.0)
		message.append(Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", xpColor + String.format(Jobs.getGCManager().getDecimalPlacesExp(), xp)));

	    if (info.getFromLevel() > 1 && info.getUntilLevel() != -1)
		message.append(Jobs.getLanguage().getMessage("command.info.help.levelRange", "%levelFrom%", info.getFromLevel(), "%levelUntil%", info.getUntilLevel()));

	    if (info.getFromLevel() > 1 && info.getUntilLevel() == -1)
		message.append(Jobs.getLanguage().getMessage("command.info.help.levelFrom", "%levelFrom%", info.getFromLevel()));

	    if (info.getFromLevel() == 1 && info.getUntilLevel() != -1)
		message.append(Jobs.getLanguage().getMessage("command.info.help.levelUntil", "%levelUntil%", info.getUntilLevel()));

	    message.append('\n');
	}
	return message.toString();
    }

    /**
     * Displays job stats about a particular player's job
     * @param jobProg - the job progress of the players job
     * @return the message
     */
    public String jobStatsMessage(JobProgression jobProg) {
	boolean isMaxLevelReached = jobProg.getLevel() == jobProg.getJob().getMaxLevel();
	String path = "command.stats.output." + (isMaxLevelReached ? "max-level"
			: "message");

	Title title = Jobs.gettitleManager().getTitle(jobProg.getLevel(), jobProg.getJob().getName());
	String message = Jobs.getLanguage().getMessage(path,
	    "%joblevel%", jobProg.getLevel(),
	    "%jobname%", jobProg.getJob().getNameWithColor(),
	    "%jobxp%", Math.round(jobProg.getExperience() * 100.0) / 100.0,
	    "%jobmaxxp%", jobProg.getMaxExperience(),
	    "%titlename%", title == null ? "Unknown" : title.getName());
	return " " + (isMaxLevelReached ? "" : jobProgressMessage(jobProg.getMaxExperience(), jobProg.getExperience())) + " " + message;
    }

    public String jobProgressMessage(double max, double current) {
	if (current < 0)
	    current = 0;

	if (max < current)
	    max = current;

	if (max < 1)
	    max = 2;

	String message = "";
	String pos = ChatColor.DARK_GREEN + "\u258F";
	String pros = ChatColor.YELLOW + "\u258F";
	int percentage = (int) ((current * 50.0) / max);
	for (int i = 0; i < percentage; i++) {
	    message += pos;
	}

	if (50 - percentage < 0)
	    percentage = 50;

	for (int i = 0; i < 50 - percentage; i++) {
	    message += pros;
	}
	return message;
    }

    /**
     * Displays job stats about a particular player's job from archive
     * @param jobInfo - jobinfo string line
     * @return the message
     */
    public String jobStatsMessageArchive(JobsPlayer jPlayer, JobProgression jobProg) {
	int level = jPlayer.getLevelAfterRejoin(jobProg);
	double exp = jPlayer.getExpAfterRejoin(jobProg, jPlayer.getLevelAfterRejoin(jobProg));
	String message = Jobs.getLanguage().getMessage("command.stats.output",
	    "%joblevel%", level,
	    "%jobname%", jobProg.getJob().getNameWithColor(),
	    "%jobxp%", Math.round(exp * 100.0) / 100.0,
	    "%jobmaxxp%", jobProg.getMaxExperience(level));
	return " " + jobProgressMessage(jobProg.getMaxExperience(level), exp) + " " + message;
    }
}
