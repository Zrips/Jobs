package com.gamingmesh.jobs.commands.list;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.i18n.Language;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Scoreboards.CMIScoreboard;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class top implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (args.length != 1 && args.length != 2) {
            return false;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        if (args[0].equalsIgnoreCase("clear")) {
            if (player != null) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                CMIScoreboard.removeScoreBoard(player);
            }
            return true;
        }

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (page < 1)
            page = 1;

        Job job = Jobs.getJob(args[0]);
        if (job == null) {
            Language.sendMessage(sender, "command.top.error.nojob");
            return null;
        }

        int workingIn = Jobs.getUsedSlots(job);
        PageInfo pi = new PageInfo(Jobs.getGCManager().JobsTopAmount, workingIn, page);
        final int finalPage = page;
        CMIScheduler.runTaskAsynchronously(() -> showTop(sender, job, pi, finalPage));
        return true;
    }

    private static void showTop(CommandSender sender, Job job, PageInfo pi, int page) {
        Player player = (Player) sender;
        List<TopList> fullList = Jobs.getJobsDAO().toplist(job.getName(), pi.getStart())
            .stream().filter(topList -> hasToBeSeenInTop(topList, job)).collect(Collectors.toList());

        if (fullList.isEmpty()) {
            CMIMessages.sendMessage(sender, LC.info_NoInformation);
            return;
        }

        int place = 1;

        if (!Jobs.getGCManager().ShowToplistInScoreboard || player == null) {
            Language.sendMessage(sender, "command.top.output.topline", "%jobname%", job.getName(), "%amount%", Jobs.getGCManager().JobsTopAmount);

            for (TopList one : fullList) {
                System.out.println(one.getPlayerInfo().getName());
                if (place > Jobs.getGCManager().JobsTopAmount)
                    break;

                Language.sendMessage(sender, "command.top.output.list",
                    "%number%", ((page - 1) * Jobs.getGCManager().JobsTopAmount) + place,
                    "%playername%", one.getPlayerInfo().getName(),
                    "%playerdisplayname%", one.getPlayerInfo().getDisplayName(),
                    "%level%", one.getLevel(),
                    "%exp%", one.getExp());
                place++;
            }
            pi.autoPagination(sender, "jobs top " + job.getName());
        } else {
            List<String> ls = new ArrayList<>();

            for (TopList one : fullList) {
                if (place > Jobs.getGCManager().JobsTopAmount)
                    break;
                ls.add(Jobs.getLanguage().getMessage("scoreboard.line", "%number%", ((page - 1) * Jobs.getGCManager().JobsTopAmount) + place,
                    "%playername%", one.getPlayerInfo().getName(), "%playerdisplayname%", one.getPlayerInfo().getDisplayName(), "%level%", one.getLevel()));
                place++;
            }

            CMIScoreboard.show(player, Jobs.getLanguage().getMessage("scoreboard.topline", "%jobname%", job.getName()), ls, Jobs.getGCManager().ToplistInScoreboardInterval);

            pi.autoPagination(sender, "jobs top " + job.getName());
        }
    }

    private static boolean hasToBeSeenInTop(TopList topList, Job job) {
        Player player = topList.getPlayerInfo().getJobsPlayer().getPlayer();
        if (player != null)
            return !player.isPermissionSet("jobs.hidetop.*") || !player.isPermissionSet("jobs.hidetop." + job.getName().toLowerCase());

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(topList.getPlayerInfo().getUuid());
        return !(Jobs.getVaultPermission().playerHas(null, offlinePlayer, "jobs.hidetop.*")
            || Jobs.getVaultPermission().playerHas(null, offlinePlayer, "jobs.hidetop." + job.getName().toLowerCase()));
    }

}
