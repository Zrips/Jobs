/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.util.ChatColor;

public class JobsCommands implements CommandExecutor {
    private static final String label = "jobs";
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
            return help(sender);
        
        String cmd = args[0].toLowerCase();
        
        try {
            Method m = getClass().getMethod(cmd, CommandSender.class, String[].class);
            if (m.isAnnotationPresent(JobCommand.class)) {
                if (!hasCommandPermission(sender, cmd)) {
                    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
                    return true;
                }
                
                String[] myArgs = reduceArgs(args);
                
                if (myArgs.length > 0) {
                    if (myArgs[myArgs.length - 1].equals("?")) {
                        sendUsage(sender, cmd);
                        return true;
                    }
                }
                
                return (Boolean) m.invoke(this, sender, myArgs);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {}
        
        return help(sender);
    }
    
    private static String[] reduceArgs(String[] args) {
        if (args.length <= 1)
            return new String[0];
        
        return Arrays.copyOfRange(args, 1, args.length);
    }
    
    private static boolean hasCommandPermission(CommandSender sender, String cmd) {
        return sender.hasPermission("jobs.command."+cmd);
    }
    
    private String getUsage(String cmd) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GREEN.toString());
        builder.append('/').append(label).append(' ');
        builder.append(cmd);
        builder.append(ChatColor.YELLOW);
        String key = "command."+cmd+".help.args";
        if (Language.containsKey(key)) {
            builder.append(' ');
            builder.append(Language.getMessage(key));
        }
        return builder.toString();
    }
    
    public void sendUsage(CommandSender sender, String cmd) {
        String message = ChatColor.YELLOW + Language.getMessage("command.help.output.usage");
        message = message.replace("%usage%", getUsage(cmd));
        sender.sendMessage(message);
        sender.sendMessage(ChatColor.YELLOW+"* "+Language.getMessage("command."+cmd+".help"));
    }
    
    public void sendValidActions(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (ActionType action : ActionType.values()) {
            if (!first)
                builder.append(',');
            
            builder.append(action.getName());
            first = false;
            
        }
        sender.sendMessage(ChatColor.YELLOW+"Valid actions are: "+ChatColor.WHITE+ builder.toString());
    }
    
    protected boolean help(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN+"*** "+ChatColor.YELLOW+"Jobs"+ChatColor.GREEN+" ***");
        for (Method m : getClass().getMethods()) {
            if (m.isAnnotationPresent(JobCommand.class)) {
                String cmd = m.getName();
                if (!hasCommandPermission(sender, cmd))
                    continue;
                sender.sendMessage(getUsage(cmd));
            }
        }
        sender.sendMessage(ChatColor.YELLOW + Language.getMessage("command.help.output"));
        return true;
    }
    
    @JobCommand
    public boolean join(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return false;
        
        if (args.length < 1) {
            sendUsage(sender, "join");
            return true;
        }
        
        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        
        String jobName = args[0];
        Job job = Jobs.getJob(jobName);
        if (job == null) {
            // job does not exist
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        
        if (!hasJobPermission(pSender, job)) {
            // you do not have permission to join the job
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
            return true;
        }
        
        if (jPlayer.isInJob(job)) {
            // already in job message
            String message = ChatColor.RED + Language.getMessage("command.join.error.alreadyin");
            message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
            sender.sendMessage(message);
            return true;
        }
        
        if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
            String message = ChatColor.RED + Language.getMessage("command.join.error.fullslots");
            message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
            sender.sendMessage(message);
            return true;
        }
        
        int confMaxJobs = ConfigManager.getJobsConfiguration().getMaxJobs();
        if (confMaxJobs > 0 && jPlayer.getJobProgression().size() >= confMaxJobs) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.join.error.maxjobs"));
            return true;
        }
        
        Jobs.getPlayerManager().joinJob(jPlayer, job);
        
        String message = Language.getMessage("command.join.success");
        message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
        sender.sendMessage(message);
        return true;
    }

    @JobCommand
    public boolean leave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return false;
        
        if (args.length < 1) {
            sendUsage(sender, "leave");
            return true;
        }
        
        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        
        String jobName = args[0];
        Job job = Jobs.getJob(jobName);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        
        Jobs.getPlayerManager().leaveJob(jPlayer, job);
        String message = Language.getMessage("command.leave.success");
        message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
        sender.sendMessage(message);
        return true;
    }

    @JobCommand
    public boolean leaveall(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return false;
        
        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        
        List<JobProgression> jobs = jPlayer.getJobProgression();
        if (jobs.size() == 0) {
            sender.sendMessage(Language.getMessage("command.leaveall.error.nojobs"));
            return true;
        }
        
        Jobs.getPlayerManager().leaveAllJobs(jPlayer);
        sender.sendMessage(Language.getMessage("command.leaveall.success"));
        return true;
    }

    @JobCommand
    public boolean info(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return false;
        
        if (args.length < 1) {
            sendUsage(sender, "info");
            sendValidActions(sender);
            return true;
        }
        
        Player pSender = (Player) sender;
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
        
        String jobName = args[0];
        Job job = Jobs.getJob(jobName);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        String type = "";
        if (args.length >= 2) {
            type = args[1];
        }
        sender.sendMessage(jobInfoMessage(jPlayer, job, type).split("\n"));
        return true;
    }

    @JobCommand
    public boolean stats(CommandSender sender, String[] args) {
        JobsPlayer jPlayer = null;
        if (args.length >= 1) {
            if (!sender.hasPermission("jobs.command.admin.stats")) {
                sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
                return true;
            }
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
            jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        } else if (sender instanceof Player) {
            jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
        }
        
        if (jPlayer == null) {
            sendUsage(sender, "stats");
            return true;
        }
        
        if (jPlayer.getJobProgression().size() == 0){
            sender.sendMessage(Language.getMessage("command.stats.error.nojob"));
            return true;
        }
        
        for (JobProgression jobProg: jPlayer.getJobProgression()){
            sender.sendMessage(jobStatsMessage(jobProg).split("\n"));
        }
        return true;
    }

    @JobCommand
    public boolean browse(CommandSender sender, String[] args) {
        ArrayList<String> lines = new ArrayList<String>();
        for (Job job: Jobs.getJobs()) {
            if (ConfigManager.getJobsConfiguration().getHideJobsWithoutPermission()) {
                if (!hasJobPermission(sender, job))
                    continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("  ");
            builder.append(job.getChatColor().toString());
            builder.append(job.getName());
            if (job.getMaxLevel() > 0) {
                builder.append(ChatColor.WHITE.toString());
                builder.append(" - max level: ");
                builder.append(job.getMaxLevel());
            }
            lines.add(builder.toString());
            if (!job.getDescription().isEmpty()) {
                lines.add("  - "+job.getDescription());
            }
        }
        
        if (lines.size() == 0) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.browse.error.nojobs"));
            return true;   
        }
        
        sender.sendMessage(Language.getMessage("command.browse.output.header"));
        for (String line : lines) {
            sender.sendMessage(line);
        }
        sender.sendMessage(Language.getMessage("command.browse.output.footer"));
        return true;
    }

    @JobCommand
    public boolean playerinfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendUsage(sender, "playerinfo");
            sendValidActions(sender);
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        String jobName = args[1];
        Job job = Jobs.getJob(jobName);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        String type = "";
        if (args.length >= 3) {
            type = args[2];
        }
        sender.sendMessage(jobInfoMessage(jPlayer, job, type).split("\n"));
        return true;
    }

    @JobCommand
    public boolean reload(CommandSender sender, String[] args) {
        try {
            Jobs.reload();
            sender.sendMessage(Language.getMessage("command.admin.success"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
            Jobs.getPluginLogger().severe("There was an error when performing a reload: ");
            e.printStackTrace();
        }
        return true;
    }

    @JobCommand
    public boolean fire(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendUsage(sender, "fire");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        if (!jPlayer.isInJob(job)) {
            String message = ChatColor.RED + Language.getMessage("command.fire.error.nojob");
            message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
            sender.sendMessage(message);
            return true;
        }
        try {
            Jobs.getPlayerManager().leaveJob(jPlayer, job);
            Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
            if (player != null) {
                String message = Language.getMessage("command.fire.output.target");
                message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                player.sendMessage(message);
            }
            
            sender.sendMessage(Language.getMessage("command.admin.success"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
        }
        return true;
    }

    @JobCommand
    public boolean fireall(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendUsage(sender, "fireall");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        List<JobProgression> jobs = jPlayer.getJobProgression();
        if (jobs.size() == 0) {
            sender.sendMessage(Language.getMessage("command.fireall.error.nojobs"));
            return true;
        }
        
        try {
            Jobs.getPlayerManager().leaveAllJobs(jPlayer);
            Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
            if (player != null) {
                player.sendMessage(Language.getMessage("command.fireall.output.target"));
            }
            
            sender.sendMessage(Language.getMessage("command.admin.success"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
        }
        return true;
    }

    @JobCommand
    public boolean employ(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendUsage(sender, "employ");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        if (jPlayer.isInJob(job)) {
            // already in job message
            String message = ChatColor.RED + Language.getMessage("command.employ.error.alreadyin");
            message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
            sender.sendMessage(message);
            return true;
        }
        try {
            // check if player already has the job
            Jobs.getPlayerManager().joinJob(jPlayer, job);
            Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
            if (player != null) {
                String message = Language.getMessage("command.employ.output.target");
                message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                player.sendMessage(message);
            }
            
            sender.sendMessage(Language.getMessage("command.admin.success"));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
        }
        return true;
    }

    @JobCommand
    public boolean promote(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender, "promote");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        try {
            // check if player already has the job
            if (jPlayer.isInJob(job)) {
                Integer levelsGained = Integer.parseInt(args[2]);
                Jobs.getPlayerManager().promoteJob(jPlayer, job, levelsGained);

                Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
                if (player != null) {
                    String message = Language.getMessage("command.promote.output.target");
                    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                    message = message.replace("%levelsgained%", Integer.valueOf(levelsGained).toString());
                    player.sendMessage(message);
                }
                
                sender.sendMessage(Language.getMessage("command.admin.success"));
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
        }
        return true;
    }

    @JobCommand
    public boolean demote(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender, "demote");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        try {
            // check if player already has the job
            if (jPlayer.isInJob(job)) {
                Integer levelsLost = Integer.parseInt(args[2]);
                Jobs.getPlayerManager().demoteJob(jPlayer, job, levelsLost);

                Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
                if (player != null) {
                    String message = Language.getMessage("command.demote.output.target");
                    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                    message = message.replace("%levelslost%", Integer.valueOf(levelsLost).toString());
                    player.sendMessage(message);
                }
                
                sender.sendMessage(Language.getMessage("command.admin.success"));
            }
        }
        catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
        }
        return true;
    }

    @JobCommand
    public boolean grantxp(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender, "grantxp");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        double xpGained;
        try {
            xpGained = Double.parseDouble(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
            return true;
        }
        if (xpGained <= 0) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
            return true;
        }
        // check if player already has the job
        if (jPlayer.isInJob(job)) {
            Jobs.getPlayerManager().addExperience(jPlayer, job, xpGained);

            Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
            if (player != null) {
                String message = Language.getMessage("command.grantxp.output.target");
                message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                message = message.replace("%xpgained%", Double.valueOf(xpGained).toString());
                player.sendMessage(message);
            }
            
            sender.sendMessage(Language.getMessage("command.admin.success"));
        }
        return true;
    }

    @JobCommand
    public boolean removexp(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender, "removexp");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job job = Jobs.getJob(args[1]);
        if (job == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        double xpLost;
        try {
            xpLost = Double.parseDouble(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
            return true;
        }
        if (xpLost <= 0) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
            return true;
        }
        // check if player already has the job
        if (jPlayer.isInJob(job)) {
            Jobs.getPlayerManager().removeExperience(jPlayer, job, xpLost);

            Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
            if (player != null) {
                String message = Language.getMessage("command.removexp.output.target");
                message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                message = message.replace("%xplost%", Double.valueOf(xpLost).toString());
                player.sendMessage(message);
            }
            
            sender.sendMessage(Language.getMessage("command.admin.success"));
        }
        return true;
    }

    @JobCommand
    public boolean transfer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender, "transfer");
            return true;
        }
        
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
        
        Job oldjob = Jobs.getJob(args[1]);
        Job newjob = Jobs.getJob(args[2]);
        if (oldjob == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        if (newjob == null) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
            return true;
        }
        try {
            if(jPlayer.isInJob(oldjob) && !jPlayer.isInJob(newjob)) {
                Jobs.getPlayerManager().transferJob(jPlayer, oldjob, newjob);

                Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
                if (player != null) {
                    String message = Language.getMessage("command.transfer.output.target");
                    message = message.replace("%oldjobname%", oldjob.getChatColor() + oldjob.getName() + ChatColor.WHITE);
                    message = message.replace("%newjobname%", newjob.getChatColor() + newjob.getName() + ChatColor.WHITE);
                    player.sendMessage(message);
                }
                
                sender.sendMessage(Language.getMessage("command.admin.success"));
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
        }
        return true;
    }

    
    /**
     * Displays info about a job
     * @param player - the player of the job
     * @param job - the job we are displaying info about
     * @param type - type of info
     * @return the message
     */
    private String jobInfoMessage(JobsPlayer player, Job job, String type) {
        if(job == null){
            // job doesn't exist
            return ChatColor.RED + Language.getMessage("command.error.job");
        }
        
        if (type == null) {
            type = "";
        } else {
            type = type.toLowerCase();
        }
        
        StringBuilder message = new StringBuilder();
        
        int showAllTypes = 1;
        for (ActionType actionType : ActionType.values()) {
            if (type.startsWith(actionType.getName().toLowerCase())) {
                showAllTypes = 0;
                break;
            }
        }
        
        for (ActionType actionType : ActionType.values()) {
            if (showAllTypes == 1 || type.startsWith(actionType.getName().toLowerCase())) {
                List<JobInfo> info = job.getJobInfo(actionType);
                if (info != null && !info.isEmpty()) {
                    message.append(jobInfoMessage(player, job, actionType));
                } else if (showAllTypes == 0) {
                    String myMessage = Language.getMessage("command.info.output." + actionType.getName().toLowerCase() + ".none");
                    myMessage = myMessage.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
                    message.append(myMessage);
                }
            }
        }
        return message.toString();
    }
    
    /**
     * Displays info about a particular action
     * @param player - the player of the job
     * @param prog - the job we are displaying info about
     * @param type - the type of action
     * @return the message
     */
    private String jobInfoMessage(JobsPlayer player, Job job, ActionType type) {
        StringBuilder message = new StringBuilder();
        message.append(Language.getMessage("command.info.output." + type.getName().toLowerCase()));
        message.append(":\n");
        
        int level = 1;
        
        JobProgression prog = player.getJobProgression(job);
        if (prog != null)
            level = prog.getLevel();
        int numjobs = player.getJobProgression().size();
        List<JobInfo> jobInfo = job.getJobInfo(type);
        for (JobInfo info: jobInfo) {
            String materialName = info.getName().toLowerCase().replace('_', ' ');
            
            double income = info.getIncome(level, numjobs);
            ChatColor incomeColor = income >= 0 ? ChatColor.GREEN : ChatColor.DARK_RED;
            
            double xp = info.getExperience(level, numjobs);
            ChatColor xpColor = xp >= 0 ? ChatColor.YELLOW : ChatColor.GRAY;
            String xpString = String.format("%.2f xp", xp);
            
            message.append("  ");
            
            message.append(materialName);
            message.append(" -> ");
            
            message.append(xpColor.toString());
            message.append(xpString);
            message.append(' ');
            
            message.append(incomeColor.toString());
            message.append(Jobs.getEconomy().format(income));
            
            message.append('\n');
        }
        return message.toString();
    }
    
    /**
     * Displays job stats about a particular player's job
     * @param jobProg - the job progress of the players job
     * @return the message
     */
    private String jobStatsMessage(JobProgression jobProg) {
        String message = Language.getMessage("command.stats.output");
        message = message.replace("%joblevel%", Integer.valueOf(jobProg.getLevel()).toString());
        message = message.replace("%jobname%", jobProg.getJob().getChatColor() + jobProg.getJob().getName() + ChatColor.WHITE);
        message = message.replace("%jobxp%", Integer.toString((int)jobProg.getExperience()));
        message = message.replace("%jobmaxxp%", Integer.toString(jobProg.getMaxExperience()));
        return message;
    }
    
    /**
     * Check Job joining permission
     */
    private boolean hasJobPermission(CommandSender sender, Job job) {
        if (!sender.hasPermission("jobs.use")) {
            return false;
        } else {
            return sender.hasPermission("jobs.join."+job.getName().toLowerCase());
        }
    }
}
