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
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.ChatColor;

public class gtop implements Cmd {

    @Override
    @JobCommand(600)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (args.length != 1 && args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "gtop");
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;
	Player player = (Player) sender;

	if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    return true;
	}

	int start = 0;
	if (args.length == 1)
	    try {
		start = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	if (start < 0)
	    start = 0;

	List<TopList> FullList = Jobs.getJobsDAO().getGlobalTopList(start);
	if (FullList.size() <= 0) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.gtop.error.nojob"));
	    return false;
	}

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.topline"));
	    int i = start;
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";
		sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.list", "%number%", i, "%playername%", PlayerName, "%level%", One.getLevel(),
		    "%exp%", One.getExp()));
	    }
	} else {

	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

	    ScoreboardManager manager = Bukkit.getScoreboardManager();
	    Scoreboard board = manager.getNewScoreboard();
	    Objective objective = board.registerNewObjective("JobsTopPlayers", "dummy");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    objective.setDisplayName(Jobs.getLanguage().getMessage("scoreboard.gtopline"));
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

	    String prev = "[\"\",{\"text\":\"" + Jobs.getLanguage().getMessage("command.gtop.output.prev")
		+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs gtop "
		+ from + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Jobs.getLanguage().getMessage(
		    "command.gtop.output.show", "[from]", from, "[until]", (from + 15)) + "\"}]}}}";
	    String next = " {\"text\":\"" + Jobs.getLanguage().getMessage("command.gtop.output.next")
		+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs gtop "
		+ until + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Jobs.getLanguage().getMessage(
		    "command.gtop.output.show", "[from]", (until + 1), "[until]", (until + 15)) + "\"}]}}}]";

	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + prev + "," + next);
	}
	return true;
    }
}
