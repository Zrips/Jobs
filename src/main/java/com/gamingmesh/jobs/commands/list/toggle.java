
package com.gamingmesh.jobs.commands.list;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.MessageToggleState;
import com.gamingmesh.jobs.container.MessageToggleType;
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

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length == 1) {

            switch (args[0].toLowerCase()) {
            case "actionbar":
                toggleState(sender, playerUUID, MessageToggleType.ActionBar);
                return true;
            case "bossbar":
                toggleState(sender, playerUUID, MessageToggleType.BossBar);
                if (ToggleBarHandling.getState(playerUUID, MessageToggleType.BossBar).equals(MessageToggleState.Off)) {
                    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(playerUUID);
                    if (jPlayer != null)
                        jPlayer.hideBossBars();
                }
                return true;
            case "chattext":
                toggleState(sender, playerUUID, MessageToggleType.ChatText);
                return true;
            }
        }

        LC.info_Spliter.sendMessage(sender);
        for (MessageToggleType one : MessageToggleType.values()) {
            Language.sendMessage(sender, "command.toggle.output." + ToggleBarHandling.getState(playerUUID, one).toString(), "[type]", one.toString());
        }

        return true;
    }

    private static void toggleState(CommandSender sender, UUID uuid, MessageToggleType type) {
        MessageToggleState state = ToggleBarHandling.getState(uuid, type).getNext();

        if (type.equals(MessageToggleType.ChatText) && state.equals(MessageToggleState.Rapid))
            state = state.getNext();

        ToggleBarHandling.modify(uuid, type, state);
        Language.sendMessage(sender, "command.toggle.output." + state.toString(), "[type]", type.toString());
    }
}
