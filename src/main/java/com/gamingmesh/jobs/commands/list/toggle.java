
package com.gamingmesh.jobs.commands.list;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class toggle implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return null;
        }

        boolean isBossbar = false, isActionbar = false;
        if (args.length != 1 || (!(isBossbar = args[0].equalsIgnoreCase("bossbar")) && !(isActionbar = args[0].equalsIgnoreCase("actionbar")))) {
            return false;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (isActionbar) {
            boolean ex = ToggleBarHandling.getActionBarToggle().getOrDefault(playerUUID, Jobs.getGCManager().ActionBarsMessageByDefault);

            if (ex) {
                Language.sendMessage(sender, "command.toggle.output.off");
            } else {
                Language.sendMessage(sender, "command.toggle.output.on");
            }
            ToggleBarHandling.getActionBarToggle().put(playerUUID, !ex);
        }

        if (isBossbar) {
            boolean ex = ToggleBarHandling.getBossBarToggle().getOrDefault(playerUUID, Jobs.getGCManager().BossBarsMessageByDefault);

            if (ex) {
                Language.sendMessage(sender, "command.toggle.output.off");
                JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId());
                if (jPlayer != null)
                    jPlayer.hideBossBars();
            } else {
                Language.sendMessage(sender, "command.toggle.output.on");
            }
            ToggleBarHandling.getBossBarToggle().put(playerUUID, !ex);
        }

        return true;
    }
}
