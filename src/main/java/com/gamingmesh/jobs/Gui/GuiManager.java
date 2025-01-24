package com.gamingmesh.jobs.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMICommands;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;

public class GuiManager {

    private Jobs plugin;

    public GuiManager(Jobs plugin) {
        this.plugin = plugin;
    }

    public void openJobsBrowseGUI(final Player player) {
        List<Job> jobsList = new ArrayList<>();

        for (Job job : Jobs.getJobs()) {
            if (Jobs.getGCManager().getHideJobsWithoutPermission() && !Jobs.getCommandManager().hasJobPermission(player, job))
                continue;

            jobsList.add(job);
        }

        int jobsListSize = jobsList.size();

        CMIGui gui = new CMIGui(player);
        gui.setTitle(Jobs.getLanguage().getMessage("command.info.gui.pickjob"));

        int guiSize = Jobs.getGCManager().getJobsGUIRows() * 9,
            neededSlots = jobsListSize + ((jobsListSize / Jobs.getGCManager().getJobsGUIGroupAmount())
                * Jobs.getGCManager().getJobsGUISkipAmount()) + Jobs.getGCManager().getJobsGUIStartPosition(),
            neededRows = (int) Math.ceil(neededSlots / 9D);

        // Resizing GUI in case we have more jobs then we could fit in current setup
        guiSize = Jobs.getGCManager().getJobsGUIRows() > neededRows ? guiSize : neededRows * 9;

        // Lets avoid oversized GUI
        if (guiSize > 54) {
            guiSize = 54;
        }

        gui.setInvSize(guiSize);

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

        int i = 0;
        int pos = Jobs.getGCManager().getJobsGUIStartPosition() - 1;

        // Changing start position to 0 in case we have more jobs then we can fit in current setup
        pos = jobsListSize > 28 ? jobsListSize <= 42 ? 0 : -1 : pos;

        int group = 0;
        main: for (int z = 0; z < jobsListSize; z++) {
            group++;

            if (group > Jobs.getGCManager().getJobsGUIGroupAmount()) {
                group = 1;

                // Only add skip if we can fit all of them in max sized Gui
                if (jobsListSize <= 42) {
                    pos += Jobs.getGCManager().getJobsGUISkipAmount();
                }
            }

            pos++;

            if (i >= jobsListSize)
                break main;

            Job job = jobsList.get(i);
            List<String> lore = new ArrayList<>();

            for (JobProgression onePJob : jPlayer.getJobProgression()) {
                if (onePJob.getJob().getName().equalsIgnoreCase(job.getName())) {
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.working"));
                    break;
                }
            }

            int maxlevel = job.getMaxLevel(jPlayer);
            if (maxlevel > 0)
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.max") + maxlevel);

            if (Jobs.getGCManager().ShowTotalWorkers)
                lore.add(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", job.getTotalPlayers()));

            if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus) {
                double bonus = job.getBonus();

                if (bonus < 0)
                    lore.add(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (bonus * 100) * -1));
                else
                    lore.add(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (bonus * 100)));
            }

            if (job.getDescription().isEmpty()) {
                lore.addAll(job.getFullDescription());
            } else
                lore.addAll(Arrays.asList(job.getDescription().split("/n|\\n")));

            if (job.getMaxSlots() != null) {
                int usedSlots = Jobs.getUsedSlots(job);
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftSlots") + ((job.getMaxSlots() - usedSlots) > 0 ? (job.getMaxSlots() - usedSlots) : 0));
            }

            if (Jobs.getGCManager().ShowActionNames) {
                lore.add("");
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.actions"));

                for (ActionType actionType : ActionType.values()) {
                    List<JobInfo> info = job.getJobInfo(actionType);

                    if (info != null && !info.isEmpty()) {
                        lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));
                    }
                }
            }

            lore.add("");

            if (Jobs.getGCManager().JobsGUISwitcheButtons) {
                if (!jPlayer.isInJob(job)) {
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftClick"));
                }
            } else {
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftClick"));
            }

            if (jPlayer.isInJob(job)) {
                if (Version.isCurrentEqualOrHigher(Version.v1_18_R1))
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.qClick"));
                else
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.middleClick"));
            }

            if (!Jobs.getGCManager().JobsGUISwitcheButtons) {
                if (!jPlayer.isInJob(job)) {
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.rightClick"));
                }
            } else {
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.rightClick"));
            }

            ItemStack guiItem = job.getGuiItem().clone();

            CMIGuiButton button = new CMIGuiButton(job.getGuiSlot() >= 0 ? job.getGuiSlot() : pos, guiItem) {

                @Override
                public void click(GUIClickType type) {
                    switch (type) {
                    case Left:
                    case LeftShift:
                        if (Jobs.getGCManager().JobsGUISwitcheButtons) {
                            if (!Jobs.getGCManager().DisableJoiningJobThroughGui) {
                                Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "join", job.getName() });
                            } else {
                                player.sendMessage(Jobs.getLanguage().getMessage("command.info.gui.cantJoin"));
                            }
                            openJobsBrowseGUI(player);
                        } else {
                            openJobsBrowseGUI(player, job);
                        }
                        break;
                    case MiddleMouse:
                    case Q:
                        Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "leave", job.getName() });
                        openJobsBrowseGUI(player);
                        break;
                    case Right:
                    case RightShift:
                        if (Jobs.getGCManager().JobsGUISwitcheButtons) {
                            openJobsBrowseGUI(player, job);
                        } else {
                            if (!Jobs.getGCManager().DisableJoiningJobThroughGui) {
                                Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "join", job.getName() });
                            } else {
                                player.sendMessage(Jobs.getLanguage().getMessage("command.info.gui.cantJoin"));
                            }
                            openJobsBrowseGUI(player);
                        }
                        break;
                    default:
                        break;
                    }
                }
            };

            button.setName(Jobs.getLanguage().getMessage("command.info.help.jobName", job));
            button.clearLore();
            button.addLore(lore);
            if (Jobs.getGCManager().hideItemAttributes) {
                button.hideItemFlags();
            }

            if (jPlayer.isInJob(job)) {
                button.setGlowing();
            }

            gui.addButton(button);
            i++;
        }

        if (Jobs.getGCManager().InfoButtonSlot > 0) {

            ItemStack next = Jobs.getGCManager().guiInfoButton;
            ItemMeta meta = next.getItemMeta();

            List<String> l = Jobs.getLanguage().getMessageList("command.info.gui.infoLore");

            if (!l.isEmpty())
                meta.setDisplayName(l.remove(0));
            if (!l.isEmpty())
                meta.setLore(l);
            next.setItemMeta(meta);

            gui.addButton(new CMIGuiButton(Jobs.getGCManager().InfoButtonSlot - 1, next) {
                @Override
                public void click(GUIClickType type) {
                    for (String one : Jobs.getGCManager().InfoButtonCommands) {
                        if (one.equalsIgnoreCase("closeinv!")) {
                            player.closeInventory();
                            continue;
                        }
                        CMICommands.performCommand(Bukkit.getConsoleSender(), one.replace("[playerName]", player.getName()));
                    }
                }
            });
        }

        gui.fillEmptyButtons();
        gui.open();
    }

    public void openJobsBrowseGUI(Player player, Job job) {
        openJobsBrowseGUI(player, job, false);
    }

    public void openJobsBrowseGUI(Player player, Job job, boolean fromCommand) {

        Inventory tempInv = Bukkit.createInventory(player, 54, "");

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, job);
        JobProgression prog = jPlayer.getJobProgression(job);
        ItemStack guiItem = job.getGuiItem().clone();

        int level = prog != null ? prog.getLevel() : 1;
        int numjobs = jPlayer.getJobProgression().size();
        int nextButton = Jobs.getGCManager().getJobsGUINextButton();
        int backButton = Jobs.getGCManager().getJobsGUIBackButton();

        final List<ActionType> jobsRemained = new ArrayList<>();

        int i = 0;
        for (ActionType actionType : ActionType.values()) {
            List<JobInfo> info = job.getJobInfo(actionType);
            if (info == null || info.isEmpty())
                continue;

            List<String> lore = new ArrayList<>();
            lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));

            int y = 1;
            for (int z = 0; z < info.size(); z++) {
                if (i > 53) {
                    break;
                }

                JobInfo jInfo = info.get(z);
                if (jInfo == null) {
                    continue;
                }

                double income = jInfo.getIncome(level, numjobs, jPlayer.maxJobsEquation);

                income = boost.getFinalAmount(CurrencyType.MONEY, income);
                String incomeColor = income >= 0 ? "" : CMIChatColor.DARK_RED.toString();

                double xp = jInfo.getExperience(level, numjobs, jPlayer.maxJobsEquation);
                xp = boost.getFinalAmount(CurrencyType.EXP, xp);
                String xpColor = xp >= 0 ? "" : CMIChatColor.GRAY.toString();

                double points = jInfo.getPoints(level, numjobs, jPlayer.maxJobsEquation);
                points = boost.getFinalAmount(CurrencyType.POINTS, points);
                String pointsColor = points >= 0 ? "" : CMIChatColor.RED.toString();

                if (income == 0D && points == 0D && xp == 0D)
                    continue;

                String itemName = jInfo.getRealisticName();
                String val = "";

                if (income != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.money", "%money%", incomeColor
                        + CurrencyType.MONEY.format(income));

                if (points != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.points", "%points%", pointsColor
                        + CurrencyType.POINTS.format(points));

                if (xp != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", xpColor
                        + CurrencyType.EXP.format(xp));

                lore.add(Jobs.getLanguage().getMessage("command.info.help.material", "%material%", itemName) + val);

                if (y >= 10) {
                    y = 1;

                    if (z == info.size() - 1)
                        continue;

                    ItemMeta meta = guiItem.getItemMeta();
                    meta.setDisplayName(Jobs.getLanguage().getMessage("command.info.help.jobName", job));
                    meta.setLore(lore);

                    guiItem.setItemMeta(meta);
                    tempInv.setItem(i, guiItem.clone());

                    guiItem = job.getGuiItem();
                    lore = new ArrayList<>();
                    lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));
                    i++;
                }

                y++;
            }

            if (i > 53) {
                jobsRemained.add(actionType);
                continue;
            }

            ItemMeta meta = guiItem.getItemMeta();
            meta.setDisplayName(Jobs.getLanguage().getMessage("command.info.help.jobName", job));
            meta.setLore(lore);
            guiItem.setItemMeta(meta);
            tempInv.setItem(i, guiItem.clone());
            i++;
        }

        int guiSize = CMILib.getInstance().getGUIManager().isOpenedGui(player) && CMILib.getInstance().getGUIManager().getGui(player) != null ? CMILib.getInstance().getGUIManager().getGui(player)
            .getInvSize().getFields() : Jobs.getGCManager().getJobsGUIRows() * 9;

        CMIGui gui = new CMIGui(player);
        gui.setTitle(Language.updateJob(Jobs.getLanguage().getMessage("command.info.gui.jobinfo"), job));
        gui.setInvSize(guiSize);

        List<ItemStack> items = new ArrayList<>();
        for (ItemStack one : tempInv.getContents()) {
            if (one != null)
                items.add(one);
        }

        for (int i1 = 0; i1 < items.size(); i1++) {
            if (guiSize == i1 + 1 || i1 == backButton)
                continue;

            gui.addButton(new CMIGuiButton(i1, items.get(i1)));
        }

        if (!fromCommand) {
            CMIItemStack back = CMILib.getInstance().getConfigManager().getGUIPreviousPage();
            back.setDisplayName(LC.info_prevPageHover.getLocale());

            gui.addButton(new CMIGuiButton(backButton, back) {
                @Override
                public void click(GUIClickType type) {
                    openJobsBrowseGUI(player);
                }
            });
        }

        if (!Jobs.getGCManager().DisableJoiningJobThroughGui) {
            if (jPlayer.isInJob(job)) {
                CMIGuiButton button = new CMIGuiButton(40, Jobs.getGCManager().guiLeaveButton) {
                    @Override
                    public void click(GUIClickType type) {
                        leaveConfirmation(player, job, fromCommand, guiSize);
                    }
                };
                button.setName(Jobs.getLanguage().getMessage("general.info.leave"));

                gui.addButton(button);
            } else if (Jobs.getPlayerManager().getJobsLimit(jPlayer, (short) jPlayer.getJobProgression().size())) {
                gui.addButton(generateJoinButton(player, job, fromCommand, 39));
                gui.addButton(generateJoinButton(player, job, fromCommand, 40));
                gui.addButton(generateJoinButton(player, job, fromCommand, 41));
            }
        }

        if (i >= 53 && !jobsRemained.isEmpty()) {
            CMIItemStack next = CMILib.getInstance().getConfigManager().getGUINextPage();
            next.setDisplayName(LC.info_nextPageHover.getLocale());

            gui.addButton(new CMIGuiButton(nextButton, next) {
                @Override
                public void click(GUIClickType type) {
                    openJobsBrowseGUI(player, job, jobsRemained);
                }
            });
        }

        gui.fillEmptyButtons();
        gui.open();
    }

    private void leaveConfirmation(Player player, Job job, boolean fromCommand, int guiSize) {

        CMIGui gui = new CMIGui(player);
        gui.setTitle(Language.updateJob(Jobs.getLanguage().getMessage("command.info.gui.jobinfo"), job));
        gui.setInvSize(guiSize);

        gui.addButton(generateLeaveButton(player, job, fromCommand, 21));
        gui.addButton(generateLeaveButton(player, job, fromCommand, 22));
        gui.addButton(generateLeaveButton(player, job, fromCommand, 23));

        CMIGuiButton button = new CMIGuiButton(40, Jobs.getGCManager().guiJoinButton) {
            @Override
            public void click(GUIClickType type) {
                openJobsBrowseGUI(player, job, fromCommand);
            }
        };
        button.setName(LC.modify_cancelSymbolHover.getLocale());
        gui.addButton(button);

        gui.fillEmptyButtons();
        gui.open();
    }

    private CMIGuiButton generateJoinButton(Player player, Job job, boolean fromCommand, int slot) {

        CMIGuiButton button = new CMIGuiButton(slot, Jobs.getGCManager().guiJoinButton) {
            @Override
            public void click(GUIClickType type) {
                Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "join", job.getName() });
                openJobsBrowseGUI(player, job, fromCommand);
            }
        };
        button.setName(Jobs.getLanguage().getMessage("general.info.join"));

        return button;
    }

    private CMIGuiButton generateLeaveButton(Player player, Job job, boolean fromCommand, int slot) {

        CMIGuiButton button = new CMIGuiButton(slot, Jobs.getGCManager().guiLeaveButton) {
            @Override
            public void click(GUIClickType type) {
                Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "leave", job.getName() });
                openJobsBrowseGUI(player, job, fromCommand);
            }
        };
        button.setName(Jobs.getLanguage().getMessage("general.info.leave"));

        return button;
    }

    private void openJobsBrowseGUI(Player player, Job job, List<ActionType> jobsRemained) {
        Inventory tempInv = Bukkit.createInventory(player, 54, "");

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, job);

        int numjobs = jPlayer.getJobProgression().size();
        int level = jPlayer.getJobProgression(job) != null ? jPlayer.getJobProgression(job).getLevel() : 1;

        ItemStack guiItem = job.getGuiItem().clone();
        int i = 0;
        for (ActionType actionType : jobsRemained) {
            List<JobInfo> info = job.getJobInfo(actionType);
            if (info == null || info.isEmpty())
                continue;

            List<String> lore = new ArrayList<>();
            lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));

            int y = 1;
            for (int z = 0; z < info.size(); z++) {
                JobInfo jInfo = info.get(z);
                if (jInfo == null) {
                    continue;
                }

                double income = jInfo.getIncome(level, numjobs, jPlayer.maxJobsEquation);
                income = boost.getFinalAmount(CurrencyType.MONEY, income);
                String incomeColor = income >= 0 ? "" : CMIChatColor.DARK_RED.toString();

                double xp = jInfo.getExperience(level, numjobs, jPlayer.maxJobsEquation);
                xp = boost.getFinalAmount(CurrencyType.EXP, xp);
                String xpColor = xp >= 0 ? "" : CMIChatColor.GRAY.toString();

                double points = jInfo.getPoints(level, numjobs, jPlayer.maxJobsEquation);
                points = boost.getFinalAmount(CurrencyType.POINTS, points);
                String pointsColor = points >= 0 ? "" : CMIChatColor.RED.toString();

                if (income == 0D && points == 0D && xp == 0D)
                    continue;

                String itemName = jInfo.getRealisticName();
                String val = "";

                if (income != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.money", "%money%", incomeColor
                        + CurrencyType.MONEY.format( income));

                if (points != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.points", "%points%", pointsColor
                        + CurrencyType.POINTS.format( points));

                if (xp != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", xpColor
                        + CurrencyType.EXP.format( xp));

                lore.add(Jobs.getLanguage().getMessage("command.info.help.material", "%material%", itemName) + val);

                if (y >= 10) {
                    y = 1;

                    if (z == info.size() - 1)
                        continue;

                    if (i >= 54) {
                        break;
                    }

                    ItemMeta meta = guiItem.getItemMeta();
                    meta.setDisplayName(job.getDisplayName());
                    meta.setLore(lore);
                    guiItem.setItemMeta(meta);
                    tempInv.setItem(i, guiItem.clone());

                    guiItem = job.getGuiItem();
                    lore = new ArrayList<>();
                    lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));
                    i++;
                }

                y++;
            }

            if (i >= 54) {
                break;
            }

            ItemMeta meta = guiItem.getItemMeta();
            meta.setDisplayName(job.getDisplayName());
            meta.setLore(lore);
            guiItem.setItemMeta(meta);
            tempInv.setItem(i, guiItem.clone());
            i++;
        }

        int guiSize = CMILib.getInstance().getGUIManager().isOpenedGui(player) && CMILib.getInstance().getGUIManager().getGui(player) != null ? CMILib.getInstance().getGUIManager().getGui(player)
            .getInvSize().getFields() : Jobs.getGCManager().getJobsGUIRows() * 9;
        int backButton = Jobs.getGCManager().getJobsGUIBackButton();

        CMIGui gui = new CMIGui(player);
        gui.setTitle(Jobs.getLanguage().getMessage("command.info.gui.jobinfo", job));

        gui.setInvSize(guiSize);

        List<ItemStack> items = new ArrayList<>();
        for (ItemStack one : tempInv.getContents()) {
            if (one != null)
                items.add(one);
        }

        for (int i1 = 0; i1 < items.size(); i1++) {
            if (guiSize == i1 + 1 || i1 == backButton)
                continue;

            gui.addButton(new CMIGuiButton(i1, items.get(i1)));
        }

        CMIItemStack skull = CMILib.getInstance().getConfigManager().getGUIPreviousPage();

        skull.setDisplayName(LC.info_prevPageHover.getLocale());

        gui.addButton(new CMIGuiButton(backButton, skull) {
            @Override
            public void click(GUIClickType type) {
                openJobsBrowseGUI(player, job);
            }
        });

        gui.fillEmptyButtons();
        gui.open();
    }
}
