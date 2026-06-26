package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.JLC;

public class resetexploreregion implements Cmd {

    private static String WORLD = "world";
    private static String REGEX = "^[0-9a-zA-Z_-]+$";

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length != 2 || !WORLD.equals(args[0])) {
            return false;
        }

        if (!Jobs.getGCManager().resetExploringData) {
            JLC.command_resetexploreregion_output_notenabled.sendMessage(sender);
            return true;
        }

        final String worldName = args[1];
        if (!worldName.matches(REGEX)) {
            JLC.command_resetexploreregion_output_invalidname.sendMessage(sender);
            return true;
        }

        if (Jobs.getGCManager().useNewExploration) {
            Jobs.getChunkExplorationManager().resetRegion(worldName);
        } else
            Jobs.getExploreManager().resetRegion(worldName);
        JLC.command_resetexploreregion_output_reseted.sendMessage(sender, "%worldname%", worldName);
        return true;
    }
}
