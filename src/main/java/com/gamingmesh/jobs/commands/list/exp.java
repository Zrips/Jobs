package com.gamingmesh.jobs.commands.list;

import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

public class exp implements Cmd {

    private enum Action {
        Set, Add, Take
    }

    Random rand = new Random();

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {

        if (args.length < 4) {
            return false;
        }

        boolean silent = false;
        boolean silentAdmin = false;

        for (String one : args) {
            if (one.equalsIgnoreCase("-s")) {
                silent = true;
                continue;
            }
            if (one.equalsIgnoreCase("-sa")) {
                silentAdmin = true;
            }
        }

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
        if (jPlayer == null) {
            Language.sendMessage(sender, "general.error.noinfoByPlayer", "%playername%", args[0]);
            return true;
        }

        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            Language.sendMessage(sender, "general.error.job");
            return true;
        }

        Action action = Action.Add;

        switch (args[2].toLowerCase()) {
        case "add":
            action = Action.Add;
            break;
        case "set":
            action = Action.Set;
            break;
        case "take":
            action = Action.Take;
            break;
        default:
            break;
        }

        double amount = 0.0;
        /* Add random argument, ex: rand_5-10 */
        if (args[3].startsWith("rand_")) {
            String data = args[3].split("(?i)rand_")[1];
            String[] arr = data.split("-");

            int amountMin = Integer.parseInt(arr[0]);
            int amountMax = Integer.parseInt(arr[1]);

            if (amountMin < amountMax) {
                amount = amountMin + rand.nextInt(amountMax - amountMin);
            } else {
                amount = amountMax;
            }

        } else {
            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
                return true;
            }
        }

        if (!jPlayer.isInJob(job)) {
            Language.sendMessage(sender, "command.exp.error.nojob");
            return true;
        }

        try {
            // check if player already has the job
            JobProgression prog = jPlayer.getJobProgression(job);

            switch (action) {
            case Add:
                int oldLevel = prog.getLevel();
                if (prog.addExperience(amount))
                    Jobs.getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
                break;
            case Set:
                prog.setExperience(amount);
                break;
            case Take:
                prog.takeExperience(amount);
                break;
            default:
                break;
            }

            Player player = jPlayer.getPlayer();
            if (player == null) {
                Language.sendMessage(sender, "general.give.output.notonline");
                return true;
            }

            if (!silent)
                Language.sendMessage(player, "command.exp.output.target", "%jobname%", job.getDisplayName(), "%level%", prog.getLevelFormatted(), "%exp%", prog
                    .getExperience());

            if (!silentAdmin)
                Language.sendMessage(sender, "general.admin.success");

        } catch (Exception e) {
            if (!silentAdmin)
                Language.sendMessage(sender, "general.admin.error");
            e.printStackTrace();
        }
        return true;
    }
}
