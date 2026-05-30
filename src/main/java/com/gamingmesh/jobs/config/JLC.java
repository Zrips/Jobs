package com.gamingmesh.jobs.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;

public enum JLC {

    general_info_paymentType_MONEY("Money"),
    general_info_paymentTypeValued_MONEY("&2Money: [amount]"),
    general_info_paymentType_EXP("Exp"),
    general_info_paymentTypeValued_EXP("&eExp: [amount]"),
    general_info_paymentType_POINTS("Points"),
    general_info_paymentTypeValued_POINTS("&6Points: [amount]"),

    general_info_toplineseparator("{gcp}*********************** {gcs}[playerdisplayname] {gcp}***********************"),
    general_info_separator("{gcp}*******************************************************"),
    general_info_invalidPage("{gcw}Invalid page"),
    general_info_blocks_furnace("Furnace"),
    general_info_blocks_smoker("Smoker"),
    general_info_blocks_blastfurnace("Blast furnace"),
    general_info_blocks_brewingstand("Brewing stand"),
    general_info_join("{gcp}Click to join job"),
    general_info_leave("{gcw}Click to leave job"),

    general_admin_error("{gcw}There was an error in the command."),
    general_admin_success("{gcp}Your command has been performed."),

    general_error_noinfoByPlayer("{gcw}No information found by {gcs}[playername] {gcw}player name!", "Only [playername] can be used here"),
    general_error_noHelpPage("{gcw}There is no help page by this number!"),
    general_error_job("{gcw}The job you selected does not exist!"),
    general_error_jobname("{gcw}Can't find job by this name!"),
    general_error_worldisdisabled("{gcw}You can't use command in this world!"),
    general_error_newRegistration("{gcp}Registered new ownership for {gcn}[block] {gcs}[current]{gcp}/{gcs}[max]"),
    general_error_reenabledBlock("{gcp}Reenabled ownership"),
    general_error_noRegistration("{gcw}You've reached max [block] count!"),
    general_error_blockDisabled("{gcp}Payments from {gcs}[type] {gcp}got disabled. {gcn}[location]"),

    command_limit_help_info("Shows payment limits for jobs"),
    command_limit_help_args("[playername]"),
    command_limit_output_moneytime("{gcp}Time left until money limit resets: {gcs}[time]"),
    command_limi_output_moneyLimit("{gcp}Money limit: {gcs}[current]{gcp}/{gcs}[total]"),
    command_limit_output_exptime("{gcp}Time left until Exp limit resets: {gcs}[time]"),
    command_limit_output_expLimit("{gcp}Exp limit: {gcs}[current]{gcp}/{gcs}[total]"),
    command_limit_output_pointstime("{gcp}Time left until Point limit resets: {gcs}[time]"),
    command_limit_output_pointsLimit("{gcp}Point limit: {gcs}[current]{gcp}/{gcs}[total]"),
    command_limit_output_reachedmoneylimit("{gcw}You have reached money limit in given time!"),
    command_limit_output_reachedmoneylimit2("{gcp}You can check your limit with {gcs}/jobs limit {gcp}command"),
    command_limit_output_reachedmoneylimit3("{gcp}Money earned is now reduced exponentially... But you still earn a little!"),
    command_limit_output_reachedexplimit("{gcw}You have reached exp limit in given time!"),
    command_limit_output_reachedexplimit2("{gcp}You can check your limit with {gcs}/jobs limit {gcp}command"),
    command_limit_output_reachedpointslimit("{gcw}You have reached point limit in given time!"),
    command_limit_output_reachedpointslimit2("{gcp}You can check your limit with {gcs}/jobs limit {gcp}command"),
    command_limit_output_notenabled("{gcp}Money limit is not enabled"),

    command_resetexploreregion_help_info("Resets region data of Explorering"),
    command_resetexploreregion_help_args("world [worldname]"),
    command_resetexploreregion_output_notenabled("{gcp}Not enabled."),
    command_resetexploreregion_output_invalidname("{gcp}Invalid world name"),
    command_resetexploreregion_output_reseted("{gcp}Exploring region data has been reset for: {gcs}[worldname]"),

    command_resetlimit_help_info("Resets a player's payment limits"),
    command_resetlimit_help_args("[playername]"),
    command_resetlimit_output_reseted("{gcp}Payment limits have been reset for: {gcs}[playerdisplayname]"),

    command_resetquest_help_info("Resets a player's quest"),
    command_resetquest_help_args("[playername] [jobname]"),
    command_resetquest_output_reseted("{gcp}Quest has been reset for: {gcs}[playerdisplayname]"),

    command_resetquesttotal_help_info("Resets a player's done quest counter"),
    command_resetquesttotal_help_args("[playername]/all"),
    command_resetquesttotal_output_reseted("{gcp}Done quests have been reset for: {gcs}[playerdisplayname]"),
    command_resetquest_output_noQuests("{gcp}Can't find any quests"),

    command_skipquest_help_info("Skip defined quest and get new one"),
    command_skipquest_help_args("[jobname] [questname] (playerName)"),
    command_skipquest_confirmationNeed("{gcw}Are you sure you want to skip {gcs}[questName] {gcw}quest? Type the command again within {gcs}[time] seconds {gcw}to confirm!"),
    command_skipquest_output_questSkipForCost("{gcp}You skipped the quest and paid: {gcs}[amount]"),

    quest_notFound("{gcp}Quest was not found by this name ({gcs}[name]{gcp})"),
    quest_completed("{gcp}This quest is already completed"),
    quest_reachedSkipLimit("{gcp}You can't skip more than {gcs}[amount] {gcp}times"),

    message_skillup_broadcast("[playerdisplayname] has been promoted to a [titlename] [jobname]."),
    message_skillup_nobroadcast("Congratulations, you have been promoted to a [titlename] [jobname]."),

    message_levelup_broadcast("[playerdisplayname] is now a level [joblevel] [jobname]."),
    message_levelup_nobroadcast("You are now level [joblevel] [jobname]."),
    message_leveldown_message("{gcw}You lost level{gcs} [lostLevel]{gcw} in {gcs}[jobname]{gcw} job! Level: {gcs}[joblevel]"),

    message_cowtimer("{gcp}You still need to wait {gcs}[time] {gcp}sec to get paid for this job."),
    message_blocktimer("{gcp}You need to wait {gcs}[time] {gcp}sec more to get paid for this!"),
    message_taxes("{gcs}[amount] {gcp}server taxes were transferred to this account"),

    message_boostStarted("{gcp}Jobs boost time have been started!"),
    message_boostStoped("{gcp}Jobs boost time have been ended!"),

    message_crafting_fullinventory("{gcw}Your inventory is full!"),

    economy_error_nomoney("{gcw}Sorry, no money left in national bank!"),
    ;

    private String text;
    private List<String> comments = new ArrayList<String>();

    private JLC(String text) {
        this(text, "");
    }

    private JLC(String text, String... comment) {
        this.text = text;
        if (comment != null && comment.length > 0)
            for (String one : comment) {
                if (one.isEmpty())
                    continue;
                comments.add(one);
            }
    }

    private JLC(List<String> ls) {
        this(ls, "");
    }

    private JLC(List<String> ls, String... comment) {
        if (this.text == null)
            this.text = "";
        for (String one : ls) {
            if (!this.text.isEmpty())
                this.text += " /n";
            this.text += one;
        }

        if (comment != null && comment.length > 0)
            for (String one : comment) {
                if (one.isEmpty())
                    continue;
                comments.add(one);
            }
    }

    public String getText() {
        return text;
    }

    public String getPath() {
        return this.name().replace("_", ".");
    }

    public List<String> getComments() {
        return comments;
    }

    public String getMessage(Object... values) {
        return Jobs.getLanguage().getMessage(this.getPath(), values);
    }

    public void sendMessage(CommandSender sender, Object... values) {
        String msg = getMessage(values);
        if (msg == null || msg.isEmpty())
            return;
        sender.sendMessage(msg);
    }
}
