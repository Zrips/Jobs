package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Scoreboards.CMIScoreboard;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class gtop implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return null;
        }

        if (args.length > 1) {
            return false;
        }

        Player player = (Player) sender;
        int page = 1;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                CMIScoreboard.removeScoreBoard(player);
                return true;
            }

            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return true;
            }
        }

        if (page < 1)
            page = 1;

        int p = page;

        CMIScheduler.runTaskAsynchronously(plugin, () -> showGlobalTop(sender, p));
        return true;
    }

    private static void showGlobalTop(CommandSender sender, int page) {
        Player player = (Player) sender;

        int amount = Jobs.getGCManager().JobsTopAmount;

        List<TopList> fullList = Jobs.getJobsDAO().getGlobalTopList();

        PageInfo pi = new PageInfo(amount, fullList.size(), page);

        if (fullList.isEmpty()) {
            Language.sendMessage(sender, "command.gtop.error.nojob");
            return;
        }

        List<TopList> list = getSafeSubList(fullList, pi.getStart(), pi.getEnd());

        if (!Jobs.getGCManager().ShowToplistInScoreboard) {
            Language.sendMessage(sender, "command.gtop.output.topline", "%amount%", amount);

            int i = 0;
            for (TopList one : list) {

                if (i >= amount)
                    break;

                JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(one.getUuid());

                if (jPlayer != null)
                    Language.sendMessage(sender, "command.gtop.output.list",
                        "%number%", pi.getPositionForOutput(i),
                        "%playername%", jPlayer.getName(),
                        "%playerdisplayname%", jPlayer.getDisplayName(),
                        "%level%", one.getLevel(),
                        "%exp%", one.getExp());
                ++i;
            }
        } else {
            List<String> ls = new ArrayList<>();
            int i = 0;
            for (TopList one : list) {
                if (i >= amount)
                    break;

                JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(one.getUuid());

                if (jPlayer != null)
                    ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
                        "%number%", pi.getPositionForOutput(i),
                        "%playername%", jPlayer.getName(),
                        "%playerdisplayname%", jPlayer.getDisplayName(),
                        "%level%", one.getLevel()));
                ++i;
            }

            CMIScoreboard.show(player, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls, Jobs.getGCManager().ToplistInScoreboardInterval);
        }

        pi.autoPagination(sender, "jobs gtop");
    }

    public static List<TopList> getSafeSubList(List<TopList> list, int fromIndex, int toIndex) {
        int size = list.size();
        toIndex++;
        if (fromIndex >= size)
            return Collections.emptyList();
        if (toIndex > size)
            toIndex = size;
        if (fromIndex < 0)
            fromIndex = 0;
        if (fromIndex >= toIndex)
            return Collections.emptyList();

        return new ArrayList<>(list.subList(fromIndex, toIndex));
    }
}
