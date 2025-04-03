package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.BlockProtection;
import com.gamingmesh.jobs.container.DBAction;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class bp implements Cmd {

    @SuppressWarnings("deprecation")
    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return false;
        }
        boolean all = false;
        if (args.length > 0 && args[0].equalsIgnoreCase("-a"))
            all = true;

        final Player player = (Player) sender;

        Location loc = player.getLocation();

        final List<Block> changedBlocks = new ArrayList<>();

        if (Jobs.getGCManager().useNewBlockProtection) {
            for (int x = -10; x < 10; x++) {
                for (int y = -10; y < 10; y++) {
                    for (int z = -10; z < 10; z++) {
                        Location l = loc.clone().add(x, y, z);
                        Long time = Jobs.getExploitManager().getTime(l.getBlock());
                        if (time == null)
                            continue;

                        if (!all && time != -1 && time < System.currentTimeMillis()) {
                            Jobs.getExploitManager().remove(l.getBlock());
                            continue;
                        }

                        changedBlocks.add(l.getBlock());

                        if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
                            player.sendBlockChange(l, (time == -1 ? CMIMaterial.BLACK_STAINED_GLASS : CMIMaterial.WHITE_STAINED_GLASS).getMaterial().createBlockData());
                        } else {
                            if (time == -1)
                                player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 15);
                            else
                                player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 0);
                        }
                    }
                }
            }
        } else {
            for (int x = -10; x < 10; x++) {
                for (int y = -10; y < 10; y++) {
                    for (int z = -10; z < 10; z++) {
                        Location l = loc.clone().add(x, y, z);
                        BlockProtection bp = Jobs.getBpManager().getBp(l);
                        if (bp != null) {
                            long time = bp.getTime();
                            if (!all) {
                                if (bp.getAction() == DBAction.DELETE)
                                    continue;
                                if (time != -1 && time < System.currentTimeMillis()) {
                                    Jobs.getBpManager().remove(l);
                                    continue;
                                }
                            }
                            changedBlocks.add(l.getBlock());

                            if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
                                player.sendBlockChange(l, (bp.getAction() == DBAction.DELETE ? CMIMaterial.RED_STAINED_GLASS : time == -1 ? CMIMaterial.BLACK_STAINED_GLASS
                                    : CMIMaterial.WHITE_STAINED_GLASS)
                                        .getMaterial().createBlockData());
                            } else {
                                if (bp.getAction() == DBAction.DELETE)
                                    player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 14);
                                else if (time == -1)
                                    player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 15);
                                else
                                    player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 0);
                            }
                        }
                    }
                }
            }
        }

        if (changedBlocks.isEmpty())
            Language.sendMessage(sender, "command.bp.output.notFound");
        else
            Language.sendMessage(sender, "command.bp.output.found", "%amount%", changedBlocks.size());

        if (!changedBlocks.isEmpty()) {
            Location bloc = changedBlocks.get(0).getLocation();
            CMIScheduler.runAtLocationLater(plugin, bloc, () -> {
                if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
                    for (Block one : changedBlocks) {
                        player.sendBlockChange(one.getLocation(), one.getBlockData());
                    }
                } else {
                    for (Block one : changedBlocks) {
                        player.sendBlockChange(one.getLocation(), one.getType(), one.getData());
                    }
                }
            }, 120L);
        }

        return true;
    }
}
