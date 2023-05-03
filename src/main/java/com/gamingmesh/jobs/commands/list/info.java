package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class info implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return null;
        }

        if (args.length < 1) {
            Jobs.getCommandManager().sendValidActions(sender);
            return false;
        }

        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", pSender.getName());
            return true;
        }

        Job job = Jobs.getJob(args[0]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        if (Jobs.getGCManager().hideJobsInfoWithoutPermission && !Jobs.getCommandManager().hasJobPermission(pSender, job)) {
            CMIMessages.sendMessage(pSender, LC.info_NoPermission);
            return true;
        }

        if (Jobs.getGCManager().jobsInfoOpensBrowse) {
            plugin.getGUIManager().openJobsBrowseGUI(pSender, job, true);
            return true;
        }

        int page = 1;
        String type = null;

        for (int i = 1; i < args.length; i++) {
            String one = args[i];
            if (type == null) {
                ActionType t = ActionType.getByName(one);
                if (t != null) {
                    type = t.getName();
                    continue;
                }
            }
            try {
                page = Integer.parseInt(args[i]);
            } catch (NumberFormatException e) {
            }
        }

        Jobs.getCommandManager().jobInfoMessage(pSender, jPlayer, job, type, page);
        return true;
    }

}
