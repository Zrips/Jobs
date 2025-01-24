package com.gamingmesh.jobs.commands.list;

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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        int amount = Jobs.getGCManager().JobsTopAmount;
        PageInfo pi = new PageInfo(amount, Jobs.getPlayerManager().getPlayersCache().size(), page);

        CMIScheduler.runTaskAsynchronously(() -> showGlobalTop(sender, pi, amount));
        return true;
    }

    private static void showGlobalTop(CommandSender sender, PageInfo pi, int amount) {
        Player player = (Player) sender;
        List<TopList> FullList = Jobs.getJobsDAO().getGlobalTopList(pi.getStart())
            .stream().filter(gtop::hasToBeSeenInGlobalTop).collect(Collectors.toList());
        if (FullList.isEmpty()) {
            Language.sendMessage(sender, "command.gtop.error.nojob");
            return;
        }

        if (!Jobs.getGCManager().ShowToplistInScoreboard) {
            Language.sendMessage(sender, "command.gtop.output.topline", "%amount%", amount);

            int i = 0;
            for (TopList One : FullList) {
                if (i >= amount)
                    break;

                Language.sendMessage(sender, "command.gtop.output.list",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", One.getPlayerInfo().getName(),
                    "%playerdisplayname%", One.getPlayerInfo().getDisplayName(),
                    "%level%", One.getLevel(),
                    "%exp%", One.getExp());
                ++i;
            }
        } else {
            List<String> ls = new ArrayList<>();
            int i = 0;
            for (TopList one : FullList) {
                if (i >= amount)
                    break;

                ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
                    "%number%", pi.getPositionForOutput(i),
                    "%playername%", one.getPlayerInfo().getName(),
                    "%playerdisplayname%", one.getPlayerInfo().getDisplayName(),
                    "%level%", one.getLevel()));
                ++i;
            }
            
            CMIScoreboard.show(player, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls, Jobs.getGCManager().ToplistInScoreboardInterval);
        }

        pi.autoPagination(sender, "jobs gtop");
    }

    public static boolean hasToBeSeenInGlobalTop(TopList topList) {
        
        JobsPlayer jplayer = topList.getPlayerInfo().getJobsPlayer();
        
        if (Jobs.getGCManager().JobsTopHiddenPlayers.contains(jplayer.getName().toLowerCase()))
            return false;
        
        Player player = jplayer.getPlayer();
        if (player != null)
            return !player.hasPermission("jobs.hidegtop");
        return !Jobs.getVaultPermission().playerHas(
            null,
            Bukkit.getOfflinePlayer(jplayer.getUniqueId()),
            "jobs.hidegtop");
    }
}
