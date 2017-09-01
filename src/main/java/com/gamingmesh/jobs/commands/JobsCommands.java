package com.gamingmesh.jobs.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.RawMessage;

public class JobsCommands implements CommandExecutor {
    private static final String label = "jobs";
    private static final String packagePath = "com.gamingmesh.jobs.commands.list";
    private static final List<String> hidenCommands = new ArrayList<String>();
    Map<String, Integer> CommandList = new HashMap<String, Integer>();

    protected Jobs plugin;

    public JobsCommands(Jobs plugin) {
	this.plugin = plugin;
    }

    public Map<String, Integer> getCommands() {
	return CommandList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

	if (sender instanceof Player) {
	    if (!Jobs.getGCManager().canPerformActionInWorld(((Player) sender).getWorld()) && !sender.hasPermission("jobs.disabledworld.commands")) {
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.worldisdisabled"));
		return true;
	    }
	}

	if (args.length == 0)
	    return help(sender, 1);

	if ((args.length == 1 || args.length == 2) && (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help"))) {
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
		RawMessage rm = new RawMessage();
		rm.add(Jobs.getLanguage().getMessage("general.error.permission"), "&2" + label + ".command." + cmd);
		rm.show(sender);
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Jobs.sendMessage(console, Jobs.getLanguage().getMessage("general.error.permission"));
	    } else
		Jobs.sendMessage(sender, Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}

	String[] myArgs = reduceArgs(args);
	if (myArgs.length > 0) {
	    if (myArgs[myArgs.length - 1].equals("?")) {
		sendUsage(sender, cmd);
		return true;
	    }
	}

//	if (cmdClass == null) {
//	    return help(sender, 1);
//	}

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
	StringBuilder builder = new StringBuilder();
	builder.append(ChatColor.GREEN.toString());
	builder.append('/').append(label).append(' ');
	builder.append(cmd);
	builder.append(ChatColor.YELLOW);
	String key = "command." + cmd + ".help.args";
	if (Jobs.getLanguage().containsKey(key)) {
	    builder.append(' ');
	    builder.append(Jobs.getLanguage().getMessage(key));
	}
	return builder.toString();
    }

    public static String getUsageNoCmd(String cmd) {
	StringBuilder builder = new StringBuilder();
	builder.append(ChatColor.GREEN.toString());
	builder.append('/').append(label).append(' ');
	builder.append(ChatColor.YELLOW);
	String key = "command." + cmd + ".help.args";
	if (Jobs.getLanguage().containsKey(key)) {
	    builder.append(' ');
	    builder.append(Jobs.getLanguage().getMessage(key));
	}
	return builder.toString();
    }

    public void sendUsage(CommandSender sender, String cmd) {
	String message = ChatColor.YELLOW + Jobs.getLanguage().getMessage("command.help.output.usage");
	message = message.replace("%usage%", getUsage(cmd));
	sender.sendMessage(message);
	sender.sendMessage(ChatColor.YELLOW + "* " + Jobs.getLanguage().getMessage("command." + cmd + ".help.info"));
    }

    protected boolean help(CommandSender sender, int page) {

	Map<String, Integer> commands = GetCommands(sender);

	if (commands.size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}
	commands = sort(commands);
	int amountToShow = 7;
	int start = page * amountToShow - amountToShow;
	int end = page * amountToShow;
	int TotalPages = commands.size() / amountToShow;
	if (((commands.size() * 1.0) / (amountToShow * 1.0)) - TotalPages > 0)
	    TotalPages++;
	if (start >= commands.size()) {
	    start = page * amountToShow;
	    end = start + amountToShow;
	}

	if (page > TotalPages || page < 1) {
	    Jobs.getActionBar().send(sender, Jobs.getLanguage().getMessage("general.error.noHelpPage"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.help.output.title"));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.help.output.page", "[1]", page, "[2]", TotalPages));

	int i = -1;
	for (Entry<String, Integer> one : commands.entrySet()) {
	    i++;
	    if (i < start)
		continue;
	    if (i >= end)
		break;
	    sender.sendMessage(getUsage(one.getKey()) + " - " + Jobs.getLanguage().getMessage("command." + one.getKey() + ".help.info"));
	}

	String prevCmd = "/" + label + " ? " + (page - 1);
	String prev = "[\"\",{\"text\":\"" + Jobs.getLanguage().getMessage("command.help.output.prev") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\""
	    + prevCmd
	    + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + "<<<" + "\"}]}}}";
	String nextCmd = "/" + label + " ? " + (page + 1);
	String next = " {\"text\":\"" + Jobs.getLanguage().getMessage("command.help.output.next") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + nextCmd
	    + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ">>>" + "\"}]}}}]";

	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + prev + "," + next);

	return true;
    }

    private static List<String> getClassesFromPackage(String pckgname) throws ClassNotFoundException {
	List<String> result = new ArrayList<String>();
	try {
	    for (URL jarURL : ((URLClassLoader) Jobs.class.getClassLoader()).getURLs()) {
		try {
		    result.addAll(getClassesInSamePackageFromJar(pckgname, jarURL.toURI().getPath()));
		} catch (URISyntaxException e) {
		}
	    }
	} catch (NullPointerException x) {
	    throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
	}

	return result;
    }

    private static List<String> getClassesInSamePackageFromJar(String packageName, String jarPath) {
	JarFile jarFile = null;
	List<String> listOfCommands = new ArrayList<String>();
	try {
	    jarFile = new JarFile(jarPath);
	    Enumeration<JarEntry> en = jarFile.entries();
	    while (en.hasMoreElements()) {
		JarEntry entry = en.nextElement();
		String entryName = entry.getName();
		packageName = packageName.replace(".", "/");
		if (entryName != null && entryName.endsWith(".class") && entryName.startsWith(packageName)) {
		    String name = entryName.replace(packageName, "").replace(".class", "").replace("/", "");
		    if (name.contains("$"))
			name = name.split("\\$")[0];
		    listOfCommands.add(name);
		}
	    }
	} catch (Exception e) {
	} finally {
	    if (jarFile != null)
		try {
		    jarFile.close();
		} catch (Exception e) {
		}
	}
	return listOfCommands;
    }

    public Map<String, Integer> GetCommands(CommandSender sender) {
	Map<String, Integer> temp = new HashMap<String, Integer>();
	for (Entry<String, Integer> cmd : CommandList.entrySet()) {
	    if (!hasCommandPermission(sender, cmd.getKey()))
		continue;
	    temp.put(cmd.getKey(), cmd.getValue());
	}
	return temp;
    }

    public void fillCommands() {
	List<String> lm = new ArrayList<String>();
	HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
	try {
	    lm = getClassesFromPackage(packagePath);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	for (String one : lm) {
	    Class<?> newclass = getClass(one);
	    if (newclass != null)
		classes.put(one, newclass);
	}

	for (Entry<String, Class<?>> OneClass : classes.entrySet()) {
	    for (Method met : OneClass.getValue().getMethods()) {
		if (!met.isAnnotationPresent(JobCommand.class))
		    continue;
		String cmd = OneClass.getKey();
		if (hidenCommands.contains(met.getName().toLowerCase()))
		    continue;
		CommandList.put(cmd, met.getAnnotation(JobCommand.class).value());
		break;
	    }
	}
    }

    private static Class<?> getClass(String cmd) {
	Class<?> nmsClass = null;
	try {
	    nmsClass = Class.forName(packagePath + "." + cmd.toLowerCase());
	} catch (ClassNotFoundException | IllegalArgumentException | SecurityException e) {
	}
	return nmsClass;
    }

    private static Cmd getCmdClass(String cmd) {
	Cmd cmdClass = null;
	try {
	    Class<?> nmsClass;
	    nmsClass = Class.forName(packagePath + "." + cmd.toLowerCase());
	    if (Cmd.class.isAssignableFrom(nmsClass)) {
		cmdClass = (Cmd) nmsClass.getConstructor().newInstance();
	    }
	} catch (ClassNotFoundException | InstantiationException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
	    | SecurityException e) {
	}
	return cmdClass;
    }

    private static Map<String, Integer> sort(Map<String, Integer> unsortMap) {
	List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
	Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
	    @Override
	    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		return (o1.getValue()).compareTo(o2.getValue());
	    }
	});
	Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
	    Map.Entry<String, Integer> entry = it.next();
	    sortedMap.put(entry.getKey(), entry.getValue());
	}
	return sortedMap;
    }

    /**
     * Check Job joining permission
     */
    public boolean hasJobPermission(Player sender, Job job) {
	return hasJobPermission((CommandSender) sender, job);
    }

    public boolean hasJobPermission(CommandSender sender, Job job) {
	if (!sender.hasPermission("jobs.use")) {
	    return false;
	}
	return sender.hasPermission("jobs.join." + job.getName().toLowerCase());
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
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return;
	}

	if (type == null) {
	    type = "";
	} else {
	    type = type.toLowerCase();
	}

	StringBuilder message = new StringBuilder();

	int showAllTypes = 1;
	for (ActionType actionType : ActionType.values()) {
	    if (type.startsWith(actionType.getName().toLowerCase())) {
		showAllTypes = 0;
		break;
	    }
	}

	if (job.getBoost().get(CurrencyType.EXP) != 0D)
	    message.append(ChatColor.GOLD + Jobs.getLanguage().getMessage("command.expboost.output.infostats", "%boost%", (job.getBoost().get(CurrencyType.EXP)) + 1) + "\n");

	if (job.getBoost().get(CurrencyType.MONEY) != 0D)
	    message.append(ChatColor.GOLD + Jobs.getLanguage().getMessage("command.moneyboost.output.infostats", "%boost%", (job.getBoost().get(CurrencyType.MONEY)) + 1) + "\n");

	if (job.getBoost().get(CurrencyType.POINTS) != 0D)
	    message.append(ChatColor.GOLD + Jobs.getLanguage().getMessage("command.pointboost.output.infostats", "%boost%", (job.getBoost().get(CurrencyType.POINTS)) + 1) + "\n");

	if (Jobs.getGCManager().useDynamicPayment)
	    if (job.getBonus() < 0)
		message.append(ChatColor.GOLD + Jobs.getLanguage().getMessage("command.info.help.penalty", "[penalty]", (int) (job.getBonus() * 100) / 100.0 * -1)
		    + "\n");
	    else
		message.append(ChatColor.GOLD + Jobs.getLanguage().getMessage("command.info.help.bonus", "[bonus]", (int) (job.getBonus() * 100) / 100.0) + "\n");

	for (ActionType actionType : ActionType.values()) {
	    if (showAllTypes == 1 || type.startsWith(actionType.getName().toLowerCase())) {
		List<JobInfo> info = job.getJobInfo(actionType);
		if (info != null && !info.isEmpty()) {
		    message.append(jobInfoMessage(player, job, actionType));
		} else if (showAllTypes == 0) {
		    String myMessage = Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".none");
		    myMessage = myMessage.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		    message.append(myMessage);
		}
	    }
	}

	StringBuilder message2 = new StringBuilder();
	int perPage = 20;
	int start = (page - 1) * perPage;
	int end = start + perPage;
	int pagecount = (int) Math.ceil((double) message.toString().split("\n").length / (double) perPage);
	if (pagecount == 0)
	    pagecount = 1;
	if (page > pagecount) {
	    player.getPlayer().sendMessage("Invalid page");
	    return;
	}

	if (message.toString().split("\n").length > perPage && sender instanceof Player) {
	    int i = 0;
	    for (String one : message.toString().split("\n")) {
		i++;
		if (i <= start)
		    continue;
		if (i > end)
		    break;

		message2.append(one);
		message2.append("\n");
	    }
	    message = message2;
	}

	sender.sendMessage(message.toString().split("\n"));

	String t = type == "" ? "" : " " + type;

	if (sender instanceof Player)
	    if (sender.getName().equalsIgnoreCase(player.getUserName()))
		ShowPagination(sender.getName(), pagecount, page, "jobs info " + job.getName() + t);
	    else
		ShowPagination(sender.getName(), pagecount, page, "jobs playerinfo " + player.getUserName() + " " + job.getName() + t);
    }

    public static void ShowPagination(String target, int pageCount, int CurrentPage, String cmd) {
	if (target.equalsIgnoreCase("console"))
	    return;
//	String separator = ChatColor.GOLD + "";
//	String simbol = "\u25AC";
//	for (int i = 0; i < 10; i++) {
//	    separator += simbol;
//	}

	if (pageCount == 1)
	    return;

	int NextPage = CurrentPage + 1;
	NextPage = CurrentPage < pageCount ? NextPage : CurrentPage;
	int Prevpage = CurrentPage - 1;
	Prevpage = CurrentPage > 1 ? Prevpage : CurrentPage;

	String prevCmd = "/" + cmd + " " + Prevpage;
	String prev = "\"\",{\"text\":\" " + Jobs.getLanguage().getMessage("command.help.output.prev")
	    + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + prevCmd
	    + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + "<<<" + "\"}]}}}";
	String nextCmd = "/" + cmd + " " + NextPage;
	String next = " {\"text\":\"" + Jobs.getLanguage().getMessage("command.help.output.next") + " "
	    + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\""
	    + nextCmd + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + ">>>" + "\"}]}}}";

	if (CurrentPage >= pageCount)
	    next = "{\"text\":\"" + Jobs.getLanguage().getMessage("command.help.output.next") + " \"}";

	if (CurrentPage <= 1)
	    prev = "{\"text\":\" " + Jobs.getLanguage().getMessage("command.help.output.prev") + "\"}";

	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target + " [" + prev + "," + next + "]");
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
	Boost boost = Jobs.getPlayerManager().getFinalBonus(player, job);

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

	    String materialName = info.getName().toLowerCase().replace('_', ' ');
	    materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
	    materialName = Jobs.getNameTranslatorManager().Translate(materialName, info);
	    materialName = org.bukkit.ChatColor.translateAlternateColorCodes('&', materialName);

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
		message.append(org.bukkit.ChatColor.RED + " -> ");
	    else
		message.append(" -> ");

	    if (income != 0.0)
		message.append(Jobs.getLanguage().getMessage("command.info.help.money", "%money%", incomeColor + String.format("%.2f", income)));

	    if (points != 0.0)
		message.append(Jobs.getLanguage().getMessage("command.info.help.points", "%points%", pointsColor + String.format("%.2f", points)));

	    if (xp != 0.0)
		message.append(Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", xpColor + String.format("%.2f", xp)));

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
	String message = Jobs.getLanguage().getMessage("command.stats.output",
	    "%joblevel%", jobProg.getLevel(),
	    "%jobname%", jobProg.getJob().getChatColor() + jobProg.getJob().getName() + ChatColor.WHITE,
	    "%jobxp%", Math.round(jobProg.getExperience() * 100.0) / 100.0,
	    "%jobmaxxp%", jobProg.getMaxExperience());
	return " " + jobProgressMessage(jobProg.getMaxExperience(), jobProg.getExperience()) + " " + message;
    }

    public String jobProgressMessage(double max, double current) {
	String message = "";
	String pos = ChatColor.DARK_GREEN + "\u258F";
	String pros = ChatColor.YELLOW + "\u258F";
	if (current < 0)
	    current = 0;
	if (max < current)
	    max = current;
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
	int exp = jPlayer.getExpAfterRejoin(jobProg, jPlayer.getLevelAfterRejoin(jobProg));
	String message = Jobs.getLanguage().getMessage("command.stats.output",
	    "%joblevel%", level,
	    "%jobname%", jobProg.getJob().getChatColor() + jobProg.getJob().getName() + ChatColor.WHITE,
	    "%jobxp%", Math.round(exp * 100.0) / 100.0,
	    "%jobmaxxp%", jobProg.getMaxExperience(level));
	return " " + jobProgressMessage(jobProg.getMaxExperience(level), exp) + " " + message;
    }
}
