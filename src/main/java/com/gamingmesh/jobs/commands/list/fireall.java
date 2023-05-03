package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.dao.JobsDAO.DBTables;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class fireall implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("all")) {
            if (sender instanceof Player) {
                CMIMessages.sendMessage(sender, LC.info_FromConsole);
                return true;
            }

            Jobs.getDBManager().getDB().truncate(DBTables.JobsTable.getTableName());

            for (JobsPlayer one : Jobs.getPlayerManager().getPlayersCache().values()) {
                for (JobProgression job : one.getJobProgression()) {
                    Jobs.getJobsDAO().recordToArchive(one, job.getJob());
                }
                one.leaveAllJobs();
                // No need to save as we are clearing database with more efficient method
                one.setSaved(true);
            }

            Language.sendMessage(sender, "general.admin.success");
            return true;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        if (jPlayer.progression.isEmpty()) {
            Language.sendMessage(sender, "command.fireall.error.nojobs");
            return true;
        }

        Jobs.getPlayerManager().leaveAllJobs(jPlayer);
        Player player = jPlayer.getPlayer();
        if (player != null)
            Language.sendMessage(player, "command.fireall.output.target");

        Language.sendMessage(sender, "general.admin.success");
        return true;
    }
}
