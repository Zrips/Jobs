package com.gamingmesh.jobs.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;

public enum JLC {

    general_error_noinfoByPlayer("{gcw}No information found by {gcs}[%playername%] {gcw}player name!", "Only %playername% can be used here"),

    command_resetquest_output_noQuests("{gcp}Can't find any quests"),

    command_skipquest_confirmationNeed("{gcw}Are you sure you want to skip {gcs}[questName] {gcw}quest? Type the command again within {gcs}[time] seconds {gcw}to confirm!"),
    command_skipquest_output_questSkipForCost("{gcp}You skipped the quest and paid: {gcs}[amount]"),

    quest_notFound("{gcp}Quest was not found by this name ({gcs}[name]{gcp})"),
    quest_completed("{gcp}This quest is already completed"),
    quest_reachedSkipLimit("{gcp}You can't skip more than {gcs}[amount] {gcp}times"),

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
