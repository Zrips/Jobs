package com.gamingmesh.jobs.commands.list;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestObjective;

import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;

public class editquests implements Cmd {

    private enum Action {
        update, editline, moveup, movedown, createnew, addline, info, deleteLine, gui, list, delete, editCommands;

        public static Action getByName(String name) {
            if (name.equalsIgnoreCase("new"))
                return Action.createnew;
            for (Action one : Action.values()) {
                if (one.name().equalsIgnoreCase(name))
                    return one;
            }
            return null;
        }
    }

    HashMap<String, Quest> tempQuests = new HashMap<String, Quest>();
    HashMap<String, List<String>> tempObjectives = new HashMap<String, List<String>>();

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, String[] args) {

        int page = 0;

        Action action = null;

        for (String one : args) {
            if (page == 0) {
                try {
                    page = Integer.parseInt(one);
                    continue;
                } catch (Throwable e) {
                }
            }

            if (action == null) {
                action = Action.getByName(one);
                if (action != null)
                    continue;
            }

        }

        if (action == null)
            action = Action.list;

        tempObjectives.remove(sender.getName());
        tempQuests.remove(sender.getName());

        switch (action) {
        case list:
            Set<Quest> quests = new LinkedHashSet<Quest>();
            for (Job job : Jobs.getJobs()) {
                quests.addAll(job.getQuests());
            }

            PageInfo pi = new PageInfo(10, quests.size(), page);
            ChatMessageObjectEdit CMOE = new ChatMessageObjectEdit(sender, pi) {
                @Override
                public void newAdd(String message) {
                    Quest q = new Quest(message, null);
                    tempQuests.put(sender.getName(), q);
                    mainWindow(sender, q);
                }
            };
            for (Quest quest : quests) {
                if (!pi.isEntryOk())
                    continue;
                ChatEditorObject CEO = new ChatEditorObject(Jobs.getLanguage().getMessage("command.editquests.help.output.list", "[jobName]", quest.getJob()
                    .getDisplayName(), "[questName]", quest.getQuestName())) {
                    @Override
                    public void onDelete() {

                    }

                    @Override
                    public void onClick() {
                        mainWindow(sender, quest);
                    }
                };
                CEO.setHover(LC.modify_editSymbolHover.getLocale("[text]", quest.getQuestName()));
                CMOE.addline(CEO);
            }
            CMOE.print();
            pi.autoPagination(sender, JobsCommands.LABEL + " " + editquests.class.getSimpleName());
            break;
        }

        return true;
    }

    private List<String> getRecords(Quest quest, String section) {
        List<String> objectives = new ArrayList<String>();

        if (quest.getJob() == null)
            return objectives;

        try {
            File folder = new File(Jobs.getInstance().getDataFolder(), "jobs");
            if (folder.isDirectory()) {
                File directFile = new File(folder, quest.getJob().getName().toLowerCase() + ".yml");
                if (directFile.isFile()) {
                    ConfigReader cfg = new ConfigReader(directFile);
                    if (cfg.getC().isConfigurationSection(quest.getJob().getName()) && (cfg.getC().isConfigurationSection(quest.getJob().getName() + ".Quests." + quest.getConfigName()))) {
                        objectives = cfg.getC().getStringList(quest.getJob().getName() + ".Quests." + quest.getConfigName() + "." + section);
                    }
                }
                if (objectives.isEmpty()) {
                    for (File file : folder.listFiles()) {
                        ConfigReader cfg = new ConfigReader(file);
                        if (!cfg.getC().isConfigurationSection(quest.getJob().getName()))
                            continue;
                        if (cfg.getC().isConfigurationSection(quest.getJob().getName() + ".Quests." + quest.getConfigName())) {
                            objectives = cfg.getC().getStringList(quest.getJob().getName() + ".Quests." + quest.getConfigName() + "." + section);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objectives;
    }

    private ConfigReader getQuestConfig(String jobName) {

        ConfigReader cfg = null;

        try {
            File folder = new File(Jobs.getInstance().getDataFolder(), "jobs");
            if (folder.isDirectory()) {
                File directFile = new File(folder, jobName.toLowerCase() + ".yml");
                if (directFile.isFile()) {
                    ConfigReader cf = new ConfigReader(directFile);
                    if (cf.getC().isConfigurationSection(jobName)) {
                        cfg = cf;
                    }
                }
                if (cfg == null) {
                    for (File file : folder.listFiles()) {
                        ConfigReader cf = new ConfigReader(file);
                        if (!cf.getC().isConfigurationSection(jobName))
                            continue;
                        cfg = cf;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cfg;
    }

    private boolean removeQuestInFile(CommandSender sender, String questName, String jobName) {

        ConfigReader cfg = getQuestConfig(jobName);

        if (cfg == null)
            return false;

        cfg.load();
        cfg.set(jobName + ".Quests." + questName, null);
        cfg.save();

        return true;
    }

    private boolean updateQuestInFile(CommandSender sender, Quest quest) {

        if (quest.getJob() == null)
            return false;

        ConfigReader cfg = getQuestConfig(quest.getJob().getName());

        if (cfg == null)
            return false;

        cfg.load();

        if (quest.getConfigName() == null) {
            for (int i = 1; i < 500; i++) {
                if (cfg.getC().isConfigurationSection(quest.getJob().getName() + ".Quests." + i))
                    continue;
                quest.setConfigName(String.valueOf(i));
                break;
            }
        }

        String path = quest.getJob().getName() + ".Quests." + quest.getConfigName() + ".";

        cfg.set(path + "Name", quest.getQuestName());

        cfg.set(path + "Chance", quest.getChance() != 100 ? quest.getChance() : null);

        cfg.set(path + "fromLevel", quest.getMinLvl() > 0 ? quest.getMinLvl() : null);

        cfg.set(path + "toLevel", quest.getMaxLvl() != null ? quest.getMaxLvl() : null);

        if (tempObjectives.containsKey(sender.getName())) {
            cfg.set(path + "Objectives", tempObjectives.get(sender.getName()));
            quest.getObjectives().clear();
            for (String one : tempObjectives.get(sender.getName())) {
                quest.addObjectives(QuestObjective.get(one, quest.getJob().getName()));
            }
        }

        cfg.set(path + "RewardCommands", quest.getRewardCmds().isEmpty() ? null : quest.getRewardCmds());
        cfg.set(path + "RewardDesc", quest.getDescription().isEmpty() ? null : quest.getDescription());
        cfg.set(path + "RestrictedAreas", quest.getRestrictedAreas().isEmpty() ? null : quest.getRestrictedAreas());

        cfg.save();

        return true;
    }

    private void objectivesWindow(CommandSender sender, Quest quest) {

        RawMessage rm = new RawMessage();
        rm.addText(quest.getQuestName() + " objectives");

        RawMessageCommand rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                mainWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        List<String> objectives = getRecords(quest, "Objectives");

        tempObjectives.put(sender.getName(), objectives);

        ChatMessageListEdit cmle = new ChatMessageListEdit(sender, objectives, ChatEditType.String) {
            @Override
            public void onUpdate() {
                updateQuestInFile(sender, quest);
            }
        };
        cmle.setTopLine(rm);
        cmle.print();
    }

    private void rewardCommandsWindow(CommandSender sender, Quest quest) {

        RawMessage rm = new RawMessage();
        rm.addText(quest.getQuestName() + " reward commmands");

        RawMessageCommand rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                mainWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        ChatMessageListEdit cmle = new ChatMessageListEdit(sender, quest.getRewardCmds(), ChatEditType.String) {
            @Override
            public void onUpdate() {
                updateQuestInFile(sender, quest);
            }
        };
        cmle.setTopLine(rm);
        cmle.print();
    }

    private void rewardDescWindow(CommandSender sender, Quest quest) {

        RawMessage rm = new RawMessage();
        rm.addText(quest.getQuestName() + " reward description");

        RawMessageCommand rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                mainWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        ChatMessageListEdit cmle = new ChatMessageListEdit(sender, quest.getDescription(), ChatEditType.String) {
            @Override
            public void onUpdate() {
                updateQuestInFile(sender, quest);
            }
        };
        cmle.setTopLine(rm);
        cmle.print();
    }

    private void restrictedAreaWindow(CommandSender sender, Quest quest) {

        RawMessage rm = new RawMessage();
        rm.addText(quest.getQuestName() + " restricted areas");

        RawMessageCommand rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                mainWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        ChatMessageListEdit cmle = new ChatMessageListEdit(sender, quest.getRestrictedAreas(), ChatEditType.String) {
            @Override
            public void onUpdate() {
                updateQuestInFile(sender, quest);
            }
        };
        cmle.setTopLine(rm);
        cmle.print();
    }

    private void mainWindow(CommandSender sender, Quest quest) {

        LC.info_Spliter.sendMessage(sender);

        RawMessage rm = new RawMessage();

        rm.addText("&eName: &f" + quest.getQuestName());
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", quest.getQuestName()));
        RawMessageCommand rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                ChatMessageEdit chatEdit = new ChatMessageEdit(sender, quest.getQuestName()) {
                    @Override
                    public void run(String message) {
                        quest.setQuestName(message);
                        updateQuestInFile(sender, quest);
                    }

                    @Override
                    public void onDisable() {
                        mainWindow(sender, quest);
                    }
                };
                chatEdit.setCheckForCancel(true);
                chatEdit.printMessage();
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");
        List<String> objectives = getRecords(quest, "Objectives");
        if (!tempObjectives.containsKey(sender.getName()))
            tempObjectives.put(sender.getName(), objectives);
        else
            objectives = tempObjectives.get(sender.getName());

        rm.addText((objectives.isEmpty() ? "&c" : "&e") + "Objectives");
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", "Objectives"));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                objectivesWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");
        rm.addText((quest.getRewardCmds().isEmpty() ? "&c" : "&e") + "Reward commands");
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", "Reward commands"));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                rewardCommandsWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");
        rm.addText("&eDescription");
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", "Description"));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                rewardDescWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");
        rm.addText("&eRestricted areas");
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", "Restricted areas"));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                restrictedAreaWindow(sender, quest);
            }
        };
        rm.addCommand(rmc);

        String jobName = quest.getJob() == null ? "&c-" : quest.getJob().getName();
        rm.addText("\n");
        rm.addText("&eJob: &f" + jobName);
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", jobName));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                ChatMessageEdit chatEdit = new ChatMessageEdit(sender, jobName) {
                    @Override
                    public void run(String message) {

                        message = CMIChatColor.stripColor(CMIText.getFirstVariable(message));
                        if (!CMIText.isValidString(message))
                            return;

                        Job j = Jobs.getJob(message);

                        if (j == null) {
                            sender.sendMessage(Jobs.getLanguage().getMessage("general.error.jobname"));
                            return;
                        }

                        if (quest.getJob() != j) {
                            if (quest.getJob() != null) {
                                removeQuestInFile(sender, quest.getConfigName(), quest.getJob().getName());
                                quest.getJob().getQuests().remove(quest);
                            }
                            j.getQuests().add(quest);
                            quest.setConfigName(null);
                            quest.setJob(j);
                            updateQuestInFile(sender, quest);
                        }

                        quest.setJob(j);
                    }

                    @Override
                    public void onDisable() {
                        mainWindow(sender, quest);
                    }
                };
                chatEdit.setCheckForCancel(true);
                chatEdit.printMessage();
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");
        rm.addText("&eChance: &f" + quest.getChance());
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", quest.getChance()));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                ChatMessageEdit chatEdit = new ChatMessageEdit(sender, quest.getQuestName()) {
                    @Override
                    public void run(String message) {
                        int chance = 0;
                        try {
                            chance = Integer.parseInt(message);
                        } catch (Throwable e) {
                            LC.info_UseInteger.sendMessage(sender);
                            return;
                        }
                        chance = CMINumber.clamp(chance, 0, 100);
                        quest.setChance(chance);
                        updateQuestInFile(sender, quest);
                    }

                    @Override
                    public void onDisable() {
                        mainWindow(sender, quest);
                    }
                };
                chatEdit.setCheckForCancel(true);
                chatEdit.printMessage();
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");

        rm.addText("&eFrom level: &f" + quest.getMinLvl());
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", quest.getMinLvl()));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                ChatMessageEdit chatEdit = new ChatMessageEdit(sender, String.valueOf(quest.getMinLvl())) {
                    @Override
                    public void run(String message) {

                        int level = 0;
                        try {
                            level = Integer.parseInt(message);
                        } catch (Throwable e) {
                            LC.info_UseInteger.sendMessage(sender);
                            return;
                        }

                        if (level > quest.getMaxLvl())
                            level = quest.getMaxLvl();

                        quest.setMinLvl(level);
                        updateQuestInFile(sender, quest);
                    }

                    @Override
                    public void onDisable() {
                        mainWindow(sender, quest);
                    }
                };
                chatEdit.setCheckForCancel(true);
                chatEdit.printMessage();
            }
        };
        rm.addCommand(rmc);

        rm.addText("\n");
        rm.addText("&eTo level: &f" + (quest.getMaxLvl() == null ? "-" : quest.getMaxLvl()));
        rm.addHover(LC.modify_editSymbolHover.getLocale("[text]", (quest.getMaxLvl() == null ? "-" : quest.getMaxLvl())));
        rmc = new RawMessageCommand() {
            @Override
            public void run(CommandSender sender) {
                ChatMessageEdit chatEdit = new ChatMessageEdit(sender, String.valueOf((quest.getMaxLvl() == null ? "100" : quest.getMaxLvl()))) {
                    @Override
                    public void run(String message) {

                        int level = 0;
                        try {
                            level = Integer.parseInt(message);
                        } catch (Throwable e) {
                            LC.info_UseInteger.sendMessage(sender);
                            return;
                        }

                        if (level < quest.getMinLvl())
                            level = quest.getMinLvl();

                        quest.setMaxLvl(level);
                        updateQuestInFile(sender, quest);
                    }

                    @Override
                    public void onDisable() {
                        mainWindow(sender, quest);
                    }
                };
                chatEdit.setCheckForCancel(true);
                chatEdit.printMessage();
            }
        };
        rm.addCommand(rmc);

        rm.show(sender);
    }
}
