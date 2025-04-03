package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class leaveall implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return null;
        }

        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

        List<JobProgression> jobs = jPlayer.getJobProgression();
        if (jobs.isEmpty()) {
            Language.sendMessage(sender, "command.leaveall.error.nojobs");
            return true;
        }

        if (Jobs.getGCManager().EnableConfirmation) {
            java.util.UUID uuid = pSender.getUniqueId();

            if (!Util.LEAVECONFIRM.contains(uuid)) {
                Util.LEAVECONFIRM.add(uuid);

                CMIScheduler.runTaskLater(plugin, () -> Util.LEAVECONFIRM.remove(uuid), 20 * Jobs.getGCManager().ConfirmExpiryTime);

                Language.sendMessage(pSender, "command.leaveall.confirmationNeed", "[time]",
                    Jobs.getGCManager().ConfirmExpiryTime);
                return true;
            }

            Util.LEAVECONFIRM.remove(uuid);
        }

        Jobs.getPlayerManager().leaveAllJobs(jPlayer);
        Language.sendMessage(sender, "command.leaveall.success");
        return true;
    }
}
