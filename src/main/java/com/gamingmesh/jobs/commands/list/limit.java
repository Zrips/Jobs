package com.gamingmesh.jobs.commands.list;

import java.text.DecimalFormat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.CMITimeManager;

public class limit implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (args.length != 0 && args.length != 1) {
            return false;
        }

        JobsPlayer JPlayer = null;
        if (args.length >= 1)
            JPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        else if (sender instanceof Player)
            JPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

        boolean disabled = true;
        for (CurrencyType type : CurrencyType.values()) {
            if (Jobs.getGCManager().getLimit(type).isEnabled()) {
                disabled = false;
                break;
            }
        }

        if (disabled) {
            Language.sendMessage(sender, "command.limit.output.notenabled");
            return true;
        }

        if (JPlayer == null) {
            if (args.length >= 1)
                CMIMessages.sendMessage(sender, LC.info_NoInformation);
            else if (!(sender instanceof Player))
                Jobs.getCommandManager().sendUsage(sender, "limit");
            return true;
        }

        for (CurrencyType type : CurrencyType.values()) {
            if (!Jobs.getGCManager().getLimit(type).isEnabled())
                continue;
            PaymentData limit = JPlayer.getPaymentLimit();

            if (limit.getLeftTime(type) <= 0) {
                limit.resetLimits(type);
            }

            if (limit.getLeftTime(type) > 0) {
                String typeName = type.getName().toLowerCase();

                Language.sendMessage(sender, "command.limit.output." + typeName + "time", "%time%", CMITimeManager.to24hourShort(limit.getLeftTime(type)));
                Language.sendMessage(sender, "command.limit.output." + typeName + "Limit",
                    "%current%", new DecimalFormat("##.##").format(limit.getAmount(type)),
                    "%total%", JPlayer.getLimit(type));
            }
        }
        return true;
    }
}
