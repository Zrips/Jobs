package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.JobsQuestTop;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Scoreboards.CMIScoreboard;

public class gqtop implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (args.length > 1) {
            return false;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        int page = 0;

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
                    page = Integer.parseInt(one);
                    continue;
                } catch (NumberFormatException e) {
                }
        }

        if (page < 1)
            page = 1;

        showGlobalTop(sender, page);

        return true;
    }

    private static void showGlobalTop(CommandSender sender, int page) {

        int amount = Jobs.getGCManager().JobsTopAmount;

        List<UUID> fullList = JobsQuestTop.getGlobalTopList(0);

        PageInfo pi = new PageInfo(amount, fullList.size(), page);

        if (fullList.isEmpty()) {
            CMIMessages.sendMessage(sender, LC.info_NoInformation);
            return;
        }

        List<String> ls = new ArrayList<>();

        for (int i = 0; i < amount; i++) {

            if (fullList.size() <= i + pi.getStart())
                break;

            UUID one = fullList.get(i + pi.getStart());

            JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(one);

            if (jPlayer == null)
                continue;

            Integer stats = JobsQuestTop.getGlobalCount(one);
            if (stats == null)
                continue;

            if (Jobs.getGCManager().ShowToplistInScoreboard && sender instanceof Player)
                ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", jPlayer.getName(),
                    "%playerdisplayname%", jPlayer.getDisplayName(),
                    "%level%", stats));
            else
                ls.add(Jobs.getLanguage().getMessage("command.gqtop.output.list",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", jPlayer.getName(),
                    "%playerdisplayname%", jPlayer.getDisplayName(),
                    "%level%", stats));
        }

        if (Jobs.getGCManager().ShowToplistInScoreboard && sender instanceof Player) {
            CMIScoreboard.show((Player) sender, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls, Jobs.getGCManager().ToplistInScoreboardInterval);
        } else {
            Language.sendMessage(sender, "command.gqtop.output.topline", "%amount%", amount);
            CMIMessages.sendMessage(sender, CMIList.listToString(ls));
        }

        pi.autoPagination(sender, "jobs " + gqtop.class.getSimpleName());
    }

}
