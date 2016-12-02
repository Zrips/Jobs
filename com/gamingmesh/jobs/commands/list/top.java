package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.ChatColor;

public class top implements Cmd {

    @Override
    @JobCommand(500)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (args.length != 1 && args.length != 2) {
	    Jobs.getCommandManager().sendUsage(sender, "top");
	    return true;
	}

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player player = (Player) sender;

	if (args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    return true;
	}

	int start = 0;
	if (args.length == 2)
	    try {
		start = Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	if (start < 0)
	    start = 0;

	if (Jobs.getJob(args[0]) == null) {
	    player.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.top.error.nojob"));
	    return false;
	}

	List<TopList> FullList = Jobs.getJobsDAO().toplist(args[0], start);
	if (FullList.size() <= 0) {
	    player.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.noinfo"));
	    return false;
	}

	Job job = Jobs.getJob(args[0]);
	String jobName = args[0];
	if (job != null)
	    jobName = job.getName();

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.top.output.topline", "%jobname%", jobName));
	    int i = start;
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";

		player.sendMessage(Jobs.getLanguage().getMessage("command.top.output.list", "%number%", i, "%playername%", PlayerName, "%level%", One.getLevel(), "%exp%",
		    One.getExp()));
	    }
	} else {

	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

	    ScoreboardManager manager = Bukkit.getScoreboardManager();
	    Scoreboard board = manager.getNewScoreboard();
	    Objective objective = board.registerNewObjective("JobsTopPlayers", "dummy");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    objective.setDisplayName(Jobs.getLanguage().getMessage("scoreboard.topline", "%jobname%", jobName));
	    int i = start;
	    int line = 16;
	    for (TopList One : FullList) {
		i++;
		line--;
		String playername = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";

		Score score = objective.getScore(Jobs.getLanguage().getMessage("scoreboard.line", "%number%", i, "%playername%", playername, "%level%", One.getLevel()));
		score.setScore(line);

	    }
	    player.setScoreboard(board);

	    Jobs.getScboard().addNew(player);

	    int from = start;
	    if (start >= 15)
		from = start - 15;
	    int until = start + 15;

	    String prev = "[\"\",{\"text\":\"" + Jobs.getLanguage().getMessage("command.top.output.prev")
		+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs top "
		+ jobName + " " + from + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Jobs.getLanguage().getMessage(
		    "command.top.output.show", "[from]", from, "[until]", (from + 15)) + "\"}]}}}";
	    String next = " {\"text\":\"" + Jobs.getLanguage().getMessage("command.top.output.next")
		+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs top "
		+ jobName + " " + until + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Jobs.getLanguage().getMessage(
		    "command.top.output.show", "[from]", (until + 1), "[until]", (until + 15)) + "\"}]}}}]";

	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + prev + "," + next);
	}
	return true;
    }
}
