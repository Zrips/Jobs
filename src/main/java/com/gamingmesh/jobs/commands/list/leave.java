package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class leave implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            LC.info_Ingame.sendMessage(sender);
            return null;
        }

        if (args.length < 1) {
            return false;
        }

        Player pSender = (Player) sender;
        Job job = Jobs.getJob(args[0]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        if (Jobs.getGCManager().UsePerPermissionForLeaving && !pSender.hasPermission("jobs.command.leave." + args[0].toLowerCase())) {
            CMIMessages.sendMessage(sender, LC.info_NoPermission);
            return true;
        }

        if (Jobs.getGCManager().EnableConfirmation) {
            java.util.UUID uuid = pSender.getUniqueId();

            if (!Util.LEAVECONFIRM.contains(uuid)) {
                Util.LEAVECONFIRM.add(uuid);

                CMIScheduler.runTaskLater(plugin, () -> Util.LEAVECONFIRM.remove(uuid), 20 * Jobs.getGCManager().ConfirmExpiryTime);

                Language.sendMessage(sender, "command.leave.confirmationNeed",
                    job,
                    "[time]", Jobs.getGCManager().ConfirmExpiryTime);
                return true;
            }

            Util.LEAVECONFIRM.remove(uuid);
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

        if (Jobs.getPlayerManager().leaveJob(jPlayer, job))
            Language.sendMessage(sender, "command.leave.success", job);
        else
            Language.sendMessage(sender, "general.error.job");

        return true;
    }
}
