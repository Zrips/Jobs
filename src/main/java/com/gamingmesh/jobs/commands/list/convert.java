package com.gamingmesh.jobs.commands.list;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;
import com.gamingmesh.jobs.stuff.ChatColor;

public class convert implements Cmd {

    @Override
    @JobCommand(2600)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (sender instanceof Player) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.fromconsole"));
	    return true;
	}

	if (args.length > 0) {
	    Jobs.getCommandManager().sendUsage(sender, "convert");
	    return true;
	}

	List<Convert> archivelist = null;

	try {
	    archivelist = Jobs.getJobsDAO().convertDatabase("archive");
	} catch (SQLException e) {
	    e.printStackTrace();
	    sender.sendMessage(ChatColor.RED + "Can't read data from data base, please send error log to dev's.");
	    return true;
	}

	Jobs.ChangeDatabase();

	if (archivelist == null)
	    return false;
	try {
	    Jobs.getJobsDAO().truncateAllTables();
	    Jobs.getPlayerManager().convertChacheOfPlayers(true);

	    Jobs.getJobsDAO().continueConvertions(archivelist, "archive");
	    Jobs.getPlayerManager().clearMaps();
	    Jobs.getPlayerManager().clearCache();

	    Jobs.getJobsDAO().saveExplore();
	    Jobs.getJobsDAO().saveBlockProtection();
	    Jobs.loadAllPlayersData();
	} catch (SQLException e) {
	    e.printStackTrace();
	    Jobs.consoleMsg("&cCan't write data to data base, please send error log to dev's.");
	    return true;
	}

	try {
	    Jobs.reload();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	String from = "MysSQL";
	String to = "SqLite";

	if (!Jobs.getDBManager().getDbType().equals(DataBaseType.SqLite)) {
	    from = "SqLite";
	    to = "MySQL";
	}

	Jobs.consoleMsg("&eData base was converted from &2" + from + " &eto &2" + to + "&e!");

	return true;
    }
}
