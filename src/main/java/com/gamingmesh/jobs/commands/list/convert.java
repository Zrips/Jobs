package com.gamingmesh.jobs.commands.list;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

public class convert implements Cmd {

    @Override
    @JobCommand(2600)
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (sender instanceof Player) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.fromconsole"));
	    return true;
	}

	if (args.length > 0) {
	    Jobs.getCommandManager().sendUsage(sender, "convert");
	    return true;
	}

	Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	    @Override
	    public void run() {
	    List<Convert> archivelist = null;
	    try {
		archivelist = Jobs.getJobsDAO().convertDatabase();
	    } catch (SQLException e) {
		e.printStackTrace();
		Jobs.consoleMsg("&cCan't write data to data base, please send error log to dev's.");
		return;
	    }

	    Jobs.ChangeDatabase();

	    if (archivelist == null)
		return;

	    try {
		Jobs.getJobsDAO().truncateAllTables();
		Jobs.getPlayerManager().convertChacheOfPlayers(true);

		Jobs.getJobsDAO().continueConvertions(archivelist);
		Jobs.getPlayerManager().clearMaps();
		Jobs.getPlayerManager().clearCache();

		Jobs.getJobsDAO().saveExplore();
//	    Do we really need to convert Block protection?
//	    Jobs.getJobsDAO().saveBlockProtection();
	    } catch (SQLException e) {
		e.printStackTrace();
		Jobs.consoleMsg("&cCan't write data to data base, please send error log to dev's.");
		return;
	    }

	    Jobs.reload();
	    Jobs.loadAllPlayersData();
	    }
	});

	String from = "MySQL";
	String to = "SQLite";

	if (!Jobs.getDBManager().getDbType().equals(DataBaseType.SqLite)) {
	    from = "SQLite";
	    to = "MySQL";
	}

	Jobs.consoleMsg("&eData base was converted from &2" + from + " &eto &2" + to + "&e!");
	return true;
    }
}
