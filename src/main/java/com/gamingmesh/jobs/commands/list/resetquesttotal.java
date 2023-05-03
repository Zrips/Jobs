package com.gamingmesh.jobs.commands.list;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;

public class resetquesttotal implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!Jobs.getGCManager().DailyQuestsEnabled) {
            LC.info_FeatureNotEnabled.sendMessage(sender);
            return true;
        }
        
        if (args.length != 0 && args.length != 1) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            for (Entry<UUID, JobsPlayer> pl : Jobs.getPlayerManager().getPlayersCache().entrySet()) {
                pl.getValue().setDoneQuests(0);
            }
            Jobs.getJobsDAO().resetDoneQuests();
            Language.sendMessage(sender,"command.resetquesttotal.output.reseted", "%playername%", Jobs.getPlayerManager().getPlayersCache().size());
            return true;
        }

        JobsPlayer jPlayer = null;
        Job job = null;

        for (String one : args) {
            if (job == null) {
                job = Jobs.getJob(one);
                if (job != null)
                    continue;
            }
            jPlayer = Jobs.getPlayerManager().getJobsPlayer(one);
        }

        if (jPlayer == null && sender instanceof Player)
            jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

        if (jPlayer == null) {
            Language.sendMessage(sender,"general.error.noinfoByPlayer", "%playername%", args.length > 0 ? args[0] : "");
            return true;
        }

        jPlayer.setDoneQuests(0);
        jPlayer.setSaved(false);
        jPlayer.save();
        Language.sendMessage(sender,"command.resetquesttotal.output.reseted", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName());
        return true;
    }
}
