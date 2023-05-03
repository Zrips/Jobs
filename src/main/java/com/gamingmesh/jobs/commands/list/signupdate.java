package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Signs.SignTopType;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;

public class signupdate implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!Jobs.getGCManager().SignsEnabled) {
            LC.info_FeatureNotEnabled.sendMessage(sender);
            return null;
        }

        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("all")) {
            Jobs.getJobs().forEach(Jobs.getSignUtil()::signUpdate);
            return true;
        }

        Job oldjob = Jobs.getJob(args[0]);
        if (oldjob == null) {
            Language.sendMessage(sender, "general.error.job");
            return null;
        }

        if (args.length == 2) {
            SignTopType type = SignTopType.getType(args[1]);
            if (type != null) {
                Jobs.getSignUtil().signUpdate(oldjob, type);
            }
            return true;
        }

        Jobs.getSignUtil().signUpdate(oldjob);
        return true;
    }
}
