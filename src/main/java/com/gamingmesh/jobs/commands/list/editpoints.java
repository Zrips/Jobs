package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class editpoints implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length != 3) {
            return false;
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[1]);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[1]);
            return true;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            CMIMessages.sendMessage(sender, LC.info_UseInteger);
            return false;
        }

        PlayerPoints pointInfo = jPlayer.getPointsData();
        switch (args[0].toLowerCase()) {
        case "take":
            pointInfo.takePoints(amount);
            Language.sendMessage(sender, "command.editpoints.output.take",
                "%playername%", jPlayer.getName(),
                "%playerdisplayname%", jPlayer.getDisplayName(),
                "%amount%", amount,
                "%total%", (int) (pointInfo.getCurrentPoints() * 100) / 100D);
            break;
        case "add":
            pointInfo.addPoints(amount);
            Language.sendMessage(sender, "command.editpoints.output.add",
                "%playername%", jPlayer.getName(),
                "%playerdisplayname%", jPlayer.getDisplayName(),
                "%amount%", amount,
                "%total%", (int) (pointInfo.getCurrentPoints() * 100) / 100D);
            break;
        case "set":
            pointInfo.setPoints(amount);
            Language.sendMessage(sender, "command.editpoints.output.set",
                "%playername%", jPlayer.getName(),
                "%playerdisplayname%", jPlayer.getDisplayName(),
                "%amount%", amount);
            break;
        default:
            break;
        }

        Jobs.getJobsDAO().savePoints(jPlayer);
        return true;
    }
}
