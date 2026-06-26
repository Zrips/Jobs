package com.gamingmesh.jobs.commands.list;

import com.gamingmesh.jobs.PlayerManager;
import com.gamingmesh.jobs.stuff.Util;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class deluser implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        PlayerManager playerManager = Jobs.getPlayerManager();
        JobsPlayer jPlayer = playerManager.getJobsPlayer(args[0]);
        if (jPlayer == null) {
            JLC.general_error_noinfoByPlayer.sendMessage(sender, "%playername%", args[0]);
            return true;
        }

        if (sender instanceof Player) {

            Player pSender = (Player) sender;
            java.util.UUID uuid = pSender.getUniqueId();

            if (!Util.ADMINCONFIRM.contains(uuid)) {
                Util.ADMINCONFIRM.add(uuid);

                CMIScheduler.runTaskLater(plugin, () -> Util.ADMINCONFIRM.remove(uuid), 20 * Jobs.getGCManager().ConfirmExpiryTime);

                Language.sendMessage(sender, "command.deluser.confirmationNeed", "%playername%", args[0],
                        "[time]", Jobs.getGCManager().ConfirmExpiryTime);
                return true;
            }

            Util.LEAVECONFIRM.remove(uuid);
        }

        try {
            Player player = jPlayer.getPlayer();
            if (player != null) {
                Language.sendMessage(sender, "command.deluser.output.target");
            }

            // remove player job data
            playerManager.deleteAllJobs(jPlayer);

            // remove player from user DB
            Jobs.getJobsDAO().delUser(jPlayer.playerUUID);
            JLC.general_admin_success.sendMessage(sender);

        } catch (Throwable e) {
            JLC.general_admin_error.sendMessage(sender);
        }
        return true;
    }
}
