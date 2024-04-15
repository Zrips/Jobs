package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;

public class boost implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {

        Double rate = null;
        Long timeDuration = null;
        Job job = null;
        boolean allJobs = false;
        boolean reset = false;
        CurrencyType type = null;

        for (String one : args) {

            if (type == null) {
                type = CurrencyType.getByName(one);
                if (type != null)
                    continue;
            }

            if (one.equalsIgnoreCase("all")) {
                allJobs = true;
                continue;
            }

            if (one.equalsIgnoreCase("reset")) {
                reset = true;
                continue;
            }

            if (job == null) {
                job = Jobs.getJob(one);
                if (job != null)
                    continue;
            }

            if (rate == null) {
                try {
                    rate = Double.parseDouble(one);
                    continue;
                } catch (NumberFormatException e) {
                }
            }

            try {
                Long t = timeModifier.getTimeRangeFromString(one);
                if (timeDuration == null && t != null) {
                    timeDuration = t;
                    continue;
                }
            } catch (Exception e) {
            }
        }

        if (job == null && rate == null && !reset) {
            LC.info_Spliter.sendMessage(sender);
            for (Job one : Jobs.getJobs()) {
                showBoosts(sender, one);
            }
            return true;
        }

        if (job != null && rate == null && !reset) {
            LC.info_Spliter.sendMessage(sender);
            showBoosts(sender, job);
            return true;
        }

        if (rate == null)
            rate = 1D;

        if (timeDuration == null)
            timeDuration = 0L;

        if (reset) {
            if (job == null) {
                if (type == null) {
                    for (Job one : Jobs.getJobs()) {
                        for (CurrencyType curr : CurrencyType.values()) {
                            one.addBoost(curr, 0);
                        }
                    }
                    Language.sendMessage(sender, "command.boost.output.allreset");
                } else {
                    for (Job one : Jobs.getJobs()) {
                        one.addBoost(type, 0);
                    }
                    Language.sendMessage(sender, "command.boost.output.alltypereset", "%type%", type.getDisplayName());
                }
            } else {
                if (type == null) {
                    for (CurrencyType curr : CurrencyType.values()) {
                        job.addBoost(curr, 0);
                    }
                    Language.sendMessage(sender, "command.boost.output.jobsboostreset", job);
                } else {
                    job.addBoost(type, 0);
                    Language.sendMessage(sender, "command.boost.output.jobstypeboostreset", job, "%type%", type.getDisplayName());
                }
            }
            return true;
        }

        if (job == null || allJobs) {
            for (Job one : Jobs.getJobs()) {
                if (type == null) {
                    for (CurrencyType curr : CurrencyType.values()) {
                        one.addBoost(curr, rate, timeDuration);
                    }
                } else {
                    one.addBoost(type, rate, timeDuration);
                }

                Language.sendMessage(sender, "command.boost.output.boostadded", "%boost%", rate, one);
            }
        } else {
            if (type == null) {
                for (CurrencyType curr : CurrencyType.values()) {
                    job.addBoost(curr, rate, timeDuration);
                }
            } else {
                job.addBoost(type, rate, timeDuration);
            }
            Language.sendMessage(sender, "command.boost.output.boostadded", "%boost%", rate, job);
        }

        return true;
    }

    private static void showBoosts(CommandSender sender, Job job) {
        RawMessage rm = new RawMessage();
        String msg = Jobs.getLanguage().getMessage("command.boost.output.boostStats", job);
        String[] split = msg.split("%payments%");

        rm.addText(split[0]);

        for (CurrencyType curr : CurrencyType.values()) {

            double boost = job.getBoost().get(curr);

            String boostAmount = String.valueOf(boost);
            if (boost % 1 == 0)
                boostAmount = String.valueOf((int) boost);

            if (curr.isEnabled()) {
                rm.addText(Jobs.getLanguage().getMessage("general.info.paymentTypeValued." + curr.toString(), "%amount%", boostAmount) + " ");
            }

            if (job.getBoost().getTime(curr) != null && job.getBoost().isValid(curr)) {
                rm.addHover(CMITimeManager.to24hourShort(job.getBoost().getTime(curr) - System.currentTimeMillis()));
            }
        }
        if (split.length > 1)
            rm.addText(split[1]);
        rm.show(sender);
    }
}
