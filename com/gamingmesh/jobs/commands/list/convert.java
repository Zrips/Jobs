package com.gamingmesh.jobs.commands.list;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.stuff.ChatColor;

public class convert implements Cmd {

    @JobCommand(2600)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.fromconsole"));
	    return false;
	}

	if (args.length > 0) {
	    Jobs.getCommandManager().sendUsage(sender, "convert");
	    return true;
	}

	Player pSender = (Player) sender;

	List<Convert> list = null;
	List<Convert> archivelist = null;

	try {
	    list = Jobs.getJobsDAO().convertDatabase(pSender, "jobs");
	    archivelist = Jobs.getJobsDAO().convertDatabase(pSender, "archive");
	} catch (SQLException e) {
	    e.printStackTrace();
	    sender.sendMessage(ChatColor.RED + "Can't read data from data base, please send error log to dev's.");
	    return false;
	}
	Jobs.ChangeDatabase();

	if (list == null & archivelist == null)
	    return false;
	try {
	    Jobs.getJobsDAO().continueConvertions(list, "jobs");
	    Jobs.getJobsDAO().continueConvertions(archivelist, "archive");
	} catch (SQLException e) {
	    e.printStackTrace();
	    sender.sendMessage(ChatColor.RED + "Can't write data to data base, please send error log to dev's.");
	    return false;
	}

	try {
	    Jobs.reload();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	String from = "MysSQL";
	String to = "SqLite";

	if (Jobs.getGCManager().storageMethod.equalsIgnoreCase("sqlite")) {
	    from = "SqLite";
	    to = "MySQL";
	}

	sender.sendMessage(ChatColor.GOLD + "Data base was converted from " + ChatColor.GREEN + from + ChatColor.GOLD + " to " + ChatColor.GREEN + to + ChatColor.GOLD
	    + "! Now you can stop the server, change storage-method to " + ChatColor.GREEN + to + ChatColor.GOLD
	    + " in general config file and start server again on your new database system.");

	return true;
    }
}
