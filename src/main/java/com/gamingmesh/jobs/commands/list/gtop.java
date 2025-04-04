package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Scoreboards.CMIScoreboard;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class gtop implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (args.length > 1) {
            return false;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        int page = 1;

        for (String one : args) {
            if (one.equalsIgnoreCase("clear")) {
                if (player == null)
                    return false;
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                CMIScoreboard.removeScoreBoard(player);
                return true;
            }

            if (page < 1)
                try {
                    page = Integer.parseInt(args[1]);
                    continue;
                } catch (NumberFormatException e) {
                }
        }

        if (page < 1)
            page = 1;

        int p = page;

        CMIScheduler.runTaskAsynchronously(plugin, () -> showGlobalTop(sender, p));
        return true;
    }

    private static void showGlobalTop(CommandSender sender, int page) {

        int amount = Jobs.getGCManager().JobsTopAmount;

        List<TopList> fullList = Jobs.getJobsDAO().getGlobalTopList();

        PageInfo pi = new PageInfo(amount, fullList.size(), page);

        if (fullList.isEmpty()) {
            Language.sendMessage(sender, "command.gtop.error.nojob");
            return;
        }

        List<String> ls = new ArrayList<>();

        for (int i = 0; i < amount; i++) {

            if (fullList.size() <= i + pi.getStart())
                break;

            TopList one = fullList.get(i + pi.getStart());

            JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(one.getUuid());

            if (jPlayer == null)
                continue;

            if (Jobs.getGCManager().ShowToplistInScoreboard && sender instanceof Player)
                ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", jPlayer.getName(),
                    "%playerdisplayname%", jPlayer.getDisplayName(),
                    "%level%", one.getLevel(),
                    "%exp%", one.getExp()));
            else
                ls.add(Jobs.getLanguage().getMessage("command.gtop.output.list",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", jPlayer.getName(),
                    "%playerdisplayname%", jPlayer.getDisplayName(),
                    "%level%", one.getLevel(),
                    "%exp%", one.getExp()));
        }

        if (Jobs.getGCManager().ShowToplistInScoreboard && sender instanceof Player) {
            CMIScoreboard.show((Player) sender, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls, Jobs.getGCManager().ToplistInScoreboardInterval);
        } else {
            Language.sendMessage(sender, "command.gtop.output.topline", "%amount%", amount);
            CMIMessages.sendMessage(sender, CMIList.listToString(ls));
        }

        pi.autoPagination(sender, "jobs gtop");
    }

}
