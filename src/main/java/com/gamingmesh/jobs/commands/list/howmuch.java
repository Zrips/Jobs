package com.gamingmesh.jobs.commands.list;

import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.actions.BlockCollectInfo;
import com.gamingmesh.jobs.actions.CustomKillInfo;
import com.gamingmesh.jobs.actions.EntityActionInfo;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class howmuch implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return false;
        }

        Player player = (Player) sender;

        Entity entity = getTargetEntity(player, 30);
        Block block = null;

        if (entity == null) {
            block = player.getTargetBlock(null, 30);
        }

        if (entity == null && block == null)
            return false;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

        List<JobProgression> progression = jPlayer.getJobProgression();
        int numjobs = progression.size();

        int payments = 0;

        String name = "";
        if (entity != null)
            name = entity.getType().toString();
        if (block != null)
            name = block.getType().toString();

        for (JobProgression prog : progression) {

            Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, prog.getJob(), entity, (LivingEntity) entity);

            for (ActionType one : prog.getJob().getJobInfoList().keySet()) {
                ActionInfo info = null;

                switch (one) {
                case BAKE:
                    break;
                case BREAK:
                case PLACE:
                case TNTBREAK:
                case STRIPLOGS:
                case WAX:
                case SCRAPE:
                case BRUSH:
                    if (block != null)
                        info = new BlockActionInfo(block, one);

                    if (entity != null)
                        info = new EntityActionInfo(entity, one);

                    break;
                case BREW:
                    break;
                case COLLECT:

                    if (block == null)
                        break;

                    if (block.getBlockData() instanceof Ageable) {
                        Ageable age = (Ageable) block.getBlockData();
                        info = new BlockCollectInfo(CMIMaterial.get(block.getType()), one, age.getAge());
                    } else {
                        info = new BlockCollectInfo(CMIMaterial.get(block.getType()), one);
                    }

                    break;
                case CRAFT:
                    break;
                case CUSTOMKILL:
                    break;
                case DYE:
                    break;
                case EAT:
                    break;
                case ENCHANT:
                    break;
                case EXPLORE:
                    break;
                case FISH:
                    if (entity != null) {
                        info = new EntityActionInfo(entity, one);
                    }
                    break;
                case KILL:
                case MMKILL:
                case BREED:
                case MILK:
                case TAME:
                    if (entity != null) {
                        info = new EntityActionInfo(entity, one);
                    }
                    break;
                case REPAIR:
                    break;
                case SHEAR:
                    if (entity != null && entity instanceof Sheep) {
                        Sheep sheep = (Sheep) entity;
                        info = new CustomKillInfo(sheep.getColor().name(), one);
                    }
                    break;
                case SMELT:
                    break;
                case VTRADE:
                    break;
                default:
                    break;
                }

                if (info == null)
                    continue;

                JobInfo jobinfo = prog.getJob().getJobInfo(info, prog.getLevel());

                if (jobinfo == null)
                    continue;

                Map<CurrencyType, Double> rewards = Jobs.calculateRewards(
                    jPlayer,
                    prog.getJob(),
                    jobinfo,
                    prog.getLevel(),
                    numjobs,
                    info,
                    block,
                    entity,
                    entity instanceof LivingEntity ? (LivingEntity) entity : null,
                    null,
                    null,
                    null,
                    null,
                    boost,
                    true,
                    true
                );
                double income = rewards.getOrDefault(CurrencyType.MONEY, 0D);
                double pointAmount = rewards.getOrDefault(CurrencyType.POINTS, 0D);
                double expAmount = rewards.getOrDefault(CurrencyType.EXP, 0D);
                payments++;

                Language.sendMessage(sender, "command.version.output.payment", "[job]", prog.getJob().getDisplayName(), "[action]", one, "[target]", name,
                    "[exp]", Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", Util.format2Decimals(expAmount)),
                    "[money]", Jobs.getLanguage().getMessage("command.info.help.money", "%money%", Util.format2Decimals(income)),
                    "[points]", Jobs.getLanguage().getMessage("command.info.help.points", "%points%", Util.format2Decimals(pointAmount)));
            }
        }

        if (payments == 0)
            Language.sendMessage(sender, "command.version.output.nopayment", "[target]", name);
        else
            LC.info_Spliter.sendMessage(sender);

        return true;
    }

    public static Entity getTargetEntity(Player player, int range) {

        Vector playerLookDir = player.getEyeLocation().getDirection();
        Vector playerEyeLocation = player.getEyeLocation().toVector();
        Entity bestEntity = null;
        float bestAngle = 0.15f;

        for (Entity entity : player.getNearbyEntities(range, range, range)) {

            if (!player.hasLineOfSight(entity))
                continue;

            Vector entityLoc = entity.getLocation().toVector();
            Vector playerToEntity = entityLoc.subtract(playerEyeLocation);

            if (playerLookDir.angle(playerToEntity) < bestAngle) {
                bestAngle = playerLookDir.angle(playerToEntity);
                bestEntity = entity;
            }
        }

        return bestEntity;
    }
}
