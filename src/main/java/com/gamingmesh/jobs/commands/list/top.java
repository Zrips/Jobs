package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Scoreboards.CMIScoreboard;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class top implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (args.length != 1 && args.length != 2) {
            return false;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        int page = 0;
        Job job = null;

        for (String one : args) {
            if (one.equalsIgnoreCase("clear")) {
                if (player == null)
                    return false;

                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                CMIScoreboard.removeScoreBoard(player);

                return true;
            }

            if (job == null) {
                job = Jobs.getJob(one);
                if (job != null)
                    continue;
            }
            if (page < 1)
                try {
                    page = Integer.parseInt(one);
                    continue;
                } catch (NumberFormatException e) {
                }
        }

        if (job == null) {
            Language.sendMessage(sender, "command.top.error.nojob");
            return null;
        }

        if (page < 1)
            page = 1;

        final int finalPage = page;
        final Job finalJob = job;
        CMIScheduler.runTaskAsynchronously(plugin, () -> showTop(sender, finalJob, finalPage));
        return true;
    }

    private static void showTop(CommandSender sender, Job job, int page) {

//        List<TopList> fullList = Jobs.getJobsDAO().toplist(job.getName());

        List<UUID> uuids = job.getTopList(0);

        if (uuids.isEmpty()) {
            CMIMessages.sendMessage(sender, LC.info_NoInformation);
            return;
        }
        int amount = Jobs.getGCManager().JobsTopAmount;
        PageInfo pi = new PageInfo(amount, uuids.size(), page);

        List<String> ls = new ArrayList<>();

        for (int i = 0; i < amount; i++) {

            if (uuids.size() <= i + pi.getStart())
                break;

            UUID one = uuids.get(i + pi.getStart());

            JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(one);

            if (jPlayer == null)
                continue;

            JobProgression progression = jPlayer.getJobProgression(job);
            if(progression == null) continue; // Skip if the UUID has no progression in this job
            if (Jobs.getGCManager().ShowToplistInScoreboard && sender instanceof Player)
                ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", jPlayer.getName(),
                    "%playerdisplayname%", jPlayer.getDisplayName(),
                    "%level%", progression.getLevel(),
                    "%exp%", progression.getExperience()));
            else
                ls.add(Jobs.getLanguage().getMessage("command.top.output.list",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", jPlayer.getName(),
                    "%playerdisplayname%", jPlayer.getDisplayName(),
                    "%level%", progression.getLevel(),
                    "%exp%", progression.getExperience()));
        }

        if (Jobs.getGCManager().ShowToplistInScoreboard && sender instanceof Player) {
            CMIScoreboard.show((Player) sender, Jobs.getLanguage().getMessage("scoreboard.topline", job), ls, Jobs.getGCManager().ToplistInScoreboardInterval);
        } else {
            Language.sendMessage(sender, "command.top.output.topline", job, "%amount%", Jobs.getGCManager().JobsTopAmount);
            CMIMessages.sendMessage(sender, CMIList.listToString(ls));
        }

        pi.autoPagination(sender, "jobs top " + job.getName());
    }

}
