package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class level implements Cmd {

    private enum Action {
        Set, Add, Take
    }

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length < 4) {
            return false;
        }

        Action action = Action.Add;
        int amount = 0;
        String playerName = null;
        Job job = null;

        for (String one : args) {
            switch (one.toLowerCase()) {
            case "set":
                action = Action.Set;
                continue;
            case "add":
                action = Action.Add;
                continue;
            case "take":
                action = Action.Take;
                continue;
            default:
                break;
            }

            try {
                amount = Integer.parseInt(one);
                continue;
            } catch (NumberFormatException e) {
            }

            if (job == null && (job = Jobs.getJob(one)) != null)
                continue;

            playerName = one;
        }

        if (playerName == null)
            return false;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(playerName);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        try {
            // check if player already has the job
            if (jPlayer.isInJob(job)) {
                JobProgression prog = jPlayer.getJobProgression(job);
                int total = 0;

                switch (action) {
                case Set:
                    prog.setLevel(amount);
                    break;
                case Add:
                    int oldLevel = prog.getLevel();
                    total = (oldLevel + amount);

                    if (prog.setLevel(total)) {
                        JobsLevelUpEvent levelUpEvent = new JobsLevelUpEvent(jPlayer, job, prog.getLevel(),
                            Jobs.getTitleManager().getTitle(oldLevel, prog.getJob().getName()),
                            Jobs.getTitleManager().getTitle(prog.getLevel(), prog.getJob().getName()),
                            Jobs.getGCManager().soundLevelup,
                            Jobs.getGCManager().soundTitleChange);

                        plugin.getServer().getPluginManager().callEvent(levelUpEvent);

                        // If event is cancelled, don't do anything
                        if (levelUpEvent.isCancelled())
                            return true;
                    }

                    break;
                case Take:
                    total = (prog.getLevel() - amount);
                    prog.setLevel(total);
                    break;
                default:
                    break;
                }

                jPlayer.setSaved(false);
                jPlayer.save(true);

                Player player = jPlayer.getPlayer();
                if (player != null)
                    Language.sendMessage(player, "command.level.output.target", job, "%level%", prog.getLevel(),
                        "%exp%", CurrencyType.EXP.format(prog.getExperience()));

                Language.sendMessage(sender, "general.admin.success");
            } else
                Language.sendMessage(sender, "command.level.error.nojob");
        } catch (Exception e) {
            Language.sendMessage(sender, "general.admin.error");
        }
        return true;
    }
}
