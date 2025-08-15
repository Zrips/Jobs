package com.gamingmesh.jobs.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Entities.CMIEntityType;
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

    private static HashMap<UUID, playerGUIInfo> playerGuiRows = new HashMap<>();

    private static class playerGUIInfo {
        private int rows = 1;
        private int page = 1;

        public playerGUIInfo(int rows) {
            this.rows = rows;
        }

        public int getRows() {
            return rows;
        }

        public playerGUIInfo setRows(int rows) {
            this.rows = rows;
            return this;
        }

        public int getPage() {
            return page;
        }

        public playerGUIInfo setPage(int page) {
            this.page = page;
            return this;
        }
    }

    private static playerGUIInfo getGUIInfo(UUID uuid) {
        return playerGuiRows.computeIfAbsent(uuid, k -> new playerGUIInfo(1));
    }

    private static int getGUIRows(UUID uuid) {
        return playerGuiRows.getOrDefault(uuid, new playerGUIInfo(6)).getRows();
    }

    private static List<Integer> getUsableSlots(int rows, int guiRows, boolean excludeEdges) {

        List<Integer> slots = new ArrayList<>();
        int columns = 9;
        int totalSlots = rows * columns;

        for (int slot = 0; slot < totalSlots; slot++) {
            if (excludeEdges) {
                int row = slot / columns;
                int col = slot % columns;

                if (row == guiRows - 1)
                    break;

                if (row == 0 || row == rows - 1)
                    continue;
                if (col == 0 || col == columns - 1)
                    continue;
            } else if (slots.size() > 54) {
                slots.subList(45, 55).clear();
                break;
            }
            slots.add(slot);
        }

        return slots;
    }

    public void openJobsBrowseGUI(final Player player) {
        openJobsBrowseGUI(player, getGUIInfo(player.getUniqueId()).getPage());
    }

    public void openJobsBrowseGUI(final Player player, int page) {

        List<Job> jobsList = new ArrayList<>();

        for (Job job : Jobs.getJobs()) {
            if (Jobs.getGCManager().getHideJobsWithoutPermission() && !Jobs.getCommandManager().hasJobPermission(player, job))
                continue;
            jobsList.add(job);
        }

        openJobsBrowseGUI(player, page, jobsList);
    }

    private void openJobsBrowseGUI(final Player player, int page, List<Job> jobsList) {

        int perLine = (Jobs.getGCManager().isJobsGUIAddEdge() ? 7 : 9);

        int jobsListSize = jobsList.size();

        int totalRows = Jobs.getGCManager().isJobsGUIAddEdge() ? 2 + (jobsListSize + perLine - 1) / perLine : (jobsListSize + perLine - 1) / perLine;

        int guiRows = CMINumber.clamp(totalRows, 1, 6);
        if (Jobs.getGCManager().getJobsGUIRows() > 0) {
            guiRows = Jobs.getGCManager().getJobsGUIRows();
        }

        getGUIInfo(player.getUniqueId()).setRows(guiRows).setPage(page);

        List<Integer> validSlots = getUsableSlots(totalRows, guiRows, Jobs.getGCManager().isJobsGUIAddEdge());

        int perPage = ((validSlots.size() + perLine - 1) / perLine) * perLine;

        int guiSize = guiRows * 9;

        CMIGui gui = new CMIGui(player) {
            @Override
            public void pageChange(int page) {
                openJobsBrowseGUI(player, page, jobsList);
            }
        };
        gui.setTitle(Jobs.getLanguage().getMessage("command.info.gui.pickjob"));
        gui.setInvSize(guiSize);

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

        PageInfo pi = new PageInfo(perPage, jobsListSize, page);

        if (page > pi.getTotalPages())
            pi.setCurrentPage(1);

        int i = 0;

        for (int z = 0; z < jobsListSize; z++) {

            if (pi.isContinue()) {
                continue;
            }

            if (pi.isBreak()) {
                break;
            }

            Job job = jobsList.get(z);
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

            if (Jobs.getGCManager().JobsGUISwitcheButtons && !jPlayer.isInJob(job) || !Jobs.getGCManager().JobsGUISwitcheButtons)
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftClick"));

            if (jPlayer.isInJob(job)) {
                if (Version.isCurrentEqualOrHigher(Version.v1_18_R1))
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.qClick"));
                else
                    lore.add(Jobs.getLanguage().getMessage("command.info.gui.middleClick"));
            }

            if (!Jobs.getGCManager().JobsGUISwitcheButtons && !jPlayer.isInJob(job) || Jobs.getGCManager().JobsGUISwitcheButtons)
                lore.add(Jobs.getLanguage().getMessage("command.info.gui.rightClick"));

            ItemStack guiItem = job.getGuiItem().clone();

            CMIGuiButton button = new CMIGuiButton(job.getGuiSlot() >= 0 ? job.getGuiSlot() : validSlots.get(i), guiItem) {

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
                            openJobsGUI(player, job);
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
                            openJobsGUI(player, job);
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
            i++;
            gui.addButton(button);
        }

        if (Jobs.getGCManager().InfoButtonSlot > 0 && Jobs.getGCManager().isJobsGUIAddEdge()) {

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

        if (perPage < jobsListSize)
            gui.addPagination(pi);

        gui.fillEmptyButtons();
        gui.open();
    }

    @Deprecated
    public void openJobsBrowseGUI(Player player, Job job, boolean fromCommand) {
        openJobsGUI(player, job);
    }

    public void openJobsBrowseGUI(Player player, Job job) {
        openJobsGUI(player, job);
    }

    private class actionList {

        private ActionType type;
        private List<JobInfo> info;

        public actionList(ActionType type, List<JobInfo> info) {
            this.type = type;
            this.info = info;
        }

        public ActionType getType() {
            return type;
        }

        public List<JobInfo> getInfo() {
            return info;
        }
    }

    public void openJobsGUI(Player player, Job job) {

        final List<actionList> actionList = new ArrayList<>();

        int chunkSize = 2;

        for (ActionType actionType : ActionType.values()) {
            List<JobInfo> info = job.getJobInfo(actionType);
            if (info == null || info.isEmpty())
                continue;

            for (int i = 0; i < info.size(); i += chunkSize) {
                List<JobInfo> portion = info.subList(i, Math.min(i + chunkSize, info.size()));
                actionList.add(new actionList(actionType, portion));
            }
        }

        openJobsGUI(player, job, 1, actionList);
    }

    public void openJobsGUI(Player player, Job job, int page, List<actionList> actionList) {

        int rows = getGUIRows(player.getUniqueId());

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, job);
        JobProgression prog = jPlayer.getJobProgression(job);

        int level = prog != null ? prog.getLevel() : 1;
        int numjobs = jPlayer.getJobProgression().size();
        int nextButton = (rows * 9) - 1;
        int backButton = ((rows - 1) * 9);

        int perPage = (rows - 1) * 9;

        PageInfo pi = new PageInfo(perPage, actionList.size(), page);

        final List<ItemStack> items = new ArrayList<>();

        for (actionList action : actionList) {

            if (pi.isContinue())
                continue;

            if (pi.isBreak())
                break;

            List<String> lore = new ArrayList<>();
            lore.add(Jobs.getLanguage().getMessage("command.info.output." + action.getType().getName().toLowerCase() + ".info"));
            ItemStack guiItem = null;

            for (int z = 0; z < action.getInfo().size(); z++) {

                JobInfo jInfo = action.getInfo().get(z);

                if (guiItem == null) {
                    try {
                        CMIItemStack item = CMILib.getInstance().getItemManager().getItem(jInfo.getName());
                        if (item != null && item.getCMIType().isValidAsItemStack())
                            guiItem = item.getItemStack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (action.getType() == ActionType.KILL) {
                        CMIEntityType type = CMIEntityType.get(jInfo.getName());
                        if (type != null) {
                            ItemStack item = type.getHead();
                            if (item != null)
                                guiItem = item;
                        }
                    }
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
                    val += Jobs.getLanguage().getMessage("command.info.help.money", "%money%", incomeColor + CurrencyType.MONEY.format(income));

                if (points != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.points", "%points%", pointsColor + CurrencyType.POINTS.format(points));

                if (xp != 0.0)
                    val += Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", xpColor + CurrencyType.EXP.format(xp));

                lore.add(Jobs.getLanguage().getMessage("command.info.help.material", "%material%", itemName) + val);
            }

            if (guiItem == null)
                guiItem = job.getGuiItem().clone();

            ItemMeta meta = guiItem.getItemMeta();
            meta.setDisplayName(Jobs.getLanguage().getMessage("command.info.help.jobName", job));
            meta.setLore(lore);
            guiItem.setItemMeta(meta);
            items.add(guiItem.clone());
        }

        int guiSize = rows * 9;

        CMIGui gui = new CMIGui(player);
        gui.setTitle(Language.updateJob(Jobs.getLanguage().getMessage("command.info.gui.jobinfo"), job));
        gui.setInvSize(guiSize);

        for (ItemStack item : items) {
            CMIGuiButton button = new CMIGuiButton(item);
            button.hideItemFlags();
            gui.addButton(button);
        }

        CMIItemStack back = CMILib.getInstance().getConfigManager().getGUIPreviousPage();
        back.setDisplayName(LC.info_prevPageHover.getLocale());
        gui.addButton(new CMIGuiButton(backButton, back) {
            @Override
            public void click(GUIClickType type) {
                openJobsBrowseGUI(player);
            }
        });

        if (!Jobs.getGCManager().DisableJoiningJobThroughGui) {
            if (jPlayer.isInJob(job)) {
                CMIGuiButton button = new CMIGuiButton(((rows - 1) * 9) + 4, Jobs.getGCManager().guiLeaveButton) {
                    @Override
                    public void click(GUIClickType type) {
                        leaveConfirmation(player, job);
                    }
                };
                button.setName(Jobs.getLanguage().getMessage("general.info.leave"));

                gui.addButton(button);
            } else if (Jobs.getPlayerManager().getJobsLimit(jPlayer, (short) jPlayer.getJobProgression().size())) {
                gui.addButton(generateJoinButton(player, job, ((rows - 1) * 9) + 3));
                gui.addButton(generateJoinButton(player, job, ((rows - 1) * 9) + 4));
                gui.addButton(generateJoinButton(player, job, ((rows - 1) * 9) + 5));
            }
        }

        if (pi.getTotalPages() > 1) {
            CMIItemStack next = CMILib.getInstance().getConfigManager().getGUINextPage();
            next.setDisplayName(LC.info_nextPageHover.getLocale());

            gui.addButton(new CMIGuiButton(nextButton, next) {
                @Override
                public void click(GUIClickType type) {
                    openJobsGUI(player, job, page + 1 > pi.getTotalPages() ? 1 : page + 1, actionList);
                }
            });
        }

        gui.fillEmptyButtons();
        gui.open();
    }

    private void leaveConfirmation(Player player, Job job) {

        CMIGui gui = new CMIGui(player);
        gui.setTitle(Language.updateJob(Jobs.getLanguage().getMessage("command.info.gui.jobinfo"), job));
        gui.setInvSize(getGUIRows(player.getUniqueId()) * 9);

        int rows = getGUIRows(player.getUniqueId());

        int middleRow = CMINumber.clamp(rows / 2, 1, 6);

        gui.addButton(generateLeaveButton(player, job, middleRow * 9 + 3));
        gui.addButton(generateLeaveButton(player, job, middleRow * 9 + 4));
        gui.addButton(generateLeaveButton(player, job, middleRow * 9 + 5));

        CMIGuiButton button = new CMIGuiButton((getGUIRows(player.getUniqueId()) - 1) * 9, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
            @Override
            public void click(GUIClickType type) {
                openJobsGUI(player, job);
            }
        };
        button.setName(LC.info_prevPageHover.getLocale());
        gui.addButton(button);

        gui.fillEmptyButtons();
        gui.open();
    }

    private CMIGuiButton generateJoinButton(Player player, Job job, int slot) {

        CMIGuiButton button = new CMIGuiButton(slot, Jobs.getGCManager().guiJoinButton) {
            @Override
            public void click(GUIClickType type) {
                Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "join", job.getName() });
                openJobsGUI(player, job);
            }
        };
        button.setName(Jobs.getLanguage().getMessage("general.info.join"));

        return button;
    }

    private CMIGuiButton generateLeaveButton(Player player, Job job, int slot) {

        CMIGuiButton button = new CMIGuiButton(slot, Jobs.getGCManager().guiLeaveButton) {
            @Override
            public void click(GUIClickType type) {
                Jobs.getCommandManager().onCommand(player, null, "jobs", new String[] { "leave", job.getName() });
                openJobsGUI(player, job);
            }
        };
        button.setName(Jobs.getLanguage().getMessage("general.info.leave"));

        return button;
    }

}
