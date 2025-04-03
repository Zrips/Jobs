package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class convert implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            CMIMessages.sendMessage(sender, LC.info_FromConsole);
            return true;
        }

        if (args.length > 0) {
            return false;
        }

        String from = Jobs.getDBManager().getDbType() == DataBaseType.SqLite ? "SQLite" : "MySQL";
        String to = Jobs.getDBManager().getDbType() == DataBaseType.SqLite ? "MySQL" : "SQLite";

        CMIScheduler.runTaskAsynchronously(plugin, () -> {
            Jobs.convertDatabase();
            CMIMessages.consoleMessage("&eDatabase was converted from &2" + from + " &eto &2" + to + "&e!");
        });

        return true;
    }
}
