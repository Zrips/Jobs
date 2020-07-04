package com.gamingmesh.jobs.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMIGUI.CMIGui;
import com.gamingmesh.jobs.CMIGUI.CMIGuiButton;
import com.gamingmesh.jobs.CMIGUI.GUIManager;
import com.gamingmesh.jobs.CMIGUI.GUIManager.GUIClickType;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class GuiManager {

    public void openJobsBrowseGUI(final Player player) {
	ArrayList<Job> JobsList = new ArrayList<>();
	for (Job job : Jobs.getJobs()) {
	    if (Jobs.getGCManager().getHideJobsWithoutPermission())
		if (!Jobs.getCommandManager().hasJobPermission(player, job))
		    continue;
	    JobsList.add(job);
	}

	CMIGui gui = new CMIGui(player);
	gui.setTitle(Jobs.getLanguage().getMessage("command.info.gui.pickjob"));
	gui.setFiller(CMIMaterial.get(Jobs.getGCManager().guiFiller));

	int GuiSize = Jobs.getGCManager().getJobsGUIRows() * 9;

	int neededSlots = JobsList.size() + ((JobsList.size() / Jobs.getGCManager().getJobsGUIGroupAmount()) * Jobs.getGCManager().getJobsGUISkipAmount()) + Jobs.getGCManager().getJobsGUIStartPosition();
	int neededRows = (int) Math.ceil(neededSlots / 9D);

	// Resizing GUI in case we have more jobs then we could fit in current setup
	GuiSize = Jobs.getGCManager().getJobsGUIRows() > neededRows ? GuiSize : neededRows * 9;

	// Lets avoid oversized GUI
	GuiSize = GuiSize > 54 ? 54 : GuiSize;

	gui.setInvSize(GuiSize);

	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	int i = 0;
	int pos = Jobs.getGCManager().getJobsGUIStartPosition() - 1;

	// Changing start position to 0 in case we have more jobs then we can fit in current setup
	pos = JobsList.size() > 28 ? JobsList.size() <= 42 ? 0 : -1 : pos;

	int group = 0;
	main: for (int z = 0; z < JobsList.size(); z++) {
	    group++;

	    if (group > Jobs.getGCManager().getJobsGUIGroupAmount()) {
		group = 1;

		// Only add skip if we can fit all of them in max sized Gui
		if (JobsList.size() <= 42) {
		    pos += Jobs.getGCManager().getJobsGUISkipAmount();
		}
	    }

	    pos++;

	    if (i >= JobsList.size())
		break main;

	    Job job = JobsList.get(i);
	    ArrayList<String> Lore = new ArrayList<>();

	    for (JobProgression onePJob : JPlayer.getJobProgression()) {
		if (onePJob.getJob().getName().equalsIgnoreCase(job.getName()))
		    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.working"));
	    }

	    int maxlevel = job.getMaxLevel(JPlayer);
	    if (maxlevel > 0)
		Lore.add(Jobs.getLanguage().getMessage("command.info.gui.max") + maxlevel);

	    if (Jobs.getGCManager().ShowTotalWorkers)
		Lore.add(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", job.getTotalPlayers()));

	    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus)
		if (job.getBonus() < 0)
		    Lore.add(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (job.getBonus() * 100) * -1));
		else
		    Lore.add(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (job.getBonus() * 100)));

	    Lore.addAll(Arrays.asList(job.getDescription().split("/n")));

	    if (job.getMaxSlots() != null)
		Lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftSlots") + ((job.getMaxSlots() - Jobs.getUsedSlots(job)) > 0 ? (job.getMaxSlots() - Jobs
		    .getUsedSlots(job)) : 0));

	    if (Jobs.getGCManager().ShowActionNames) {
		Lore.add("");
		Lore.add(Jobs.getLanguage().getMessage("command.info.gui.actions"));

		for (ActionType actionType : ActionType.values()) {
		    List<JobInfo> info = job.getJobInfo(actionType);
		    if (info != null && !info.isEmpty()) {
			Lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));
		    }
		}
	    }

	    Lore.add("");
	    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftClick"));
	    if (JPlayer.isInJob(job))
		Lore.add(Jobs.getLanguage().getMessage("command.info.gui.middleClick"));
	    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.rightClick"));

	    ItemStack GuiItem = job.getGuiItem();
	    ItemMeta meta = GuiItem.getItemMeta();
	    meta.setDisplayName(job.getNameWithColor());
	    meta.setLore(Lore);
	    GuiItem.setItemMeta(meta);

	    int lastPos = pos;
	    if (job.getGuiSlot() >= 0)
		lastPos = job.getGuiSlot();

	    gui.addButton(new CMIGuiButton(lastPos, GuiItem) {

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
	    });
	    i++;
	}

	gui.fillEmptyButtons();
	gui.open();
    }

    public void openJobsBrowseGUI(Player player, Job job) {
	openJobsBrowseGUI(player, job, false);
    }

    public void openJobsBrowseGUI(Player player, Job job, boolean fromCommand) {
	Inventory tempInv = Bukkit.createInventory(player, 54, "");

	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	Boost boost = Jobs.getPlayerManager().getFinalBonus(JPlayer, job);

	int level = 1;
	JobProgression prog = JPlayer.getJobProgression(job);
	if (prog != null)
	    level = prog.getLevel();

	ItemStack GuiItem = job.getGuiItem();
	int numjobs = JPlayer.getJobProgression().size();

	int i = 0;
	for (ActionType actionType : ActionType.values()) {
	    List<JobInfo> info = job.getJobInfo(actionType);
	    if (info == null || info.isEmpty())
		continue;

	    ArrayList<String> Lore = new ArrayList<>();
	    Lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));

	    int y = 1;
	    for (int z = 0; z < info.size(); z++) {
		JobInfo jInfo = info.get(z);
		if (jInfo == null) {
		    continue;
		}

		double income = jInfo.getIncome(level, numjobs);
		income = boost.getFinalAmount(CurrencyType.MONEY, income) + ((Jobs.getPlayerManager().getInventoryBoost(player, job)
		    .get(CurrencyType.MONEY)) + 1);
		String incomeColor = income >= 0 ? "" : ChatColor.DARK_RED.toString();

		double xp = jInfo.getExperience(level, numjobs);
		xp = boost.getFinalAmount(CurrencyType.EXP, xp) + ((Jobs.getPlayerManager().getInventoryBoost(player, job)
		    .get(CurrencyType.EXP)) + 1);
		String xpColor = xp >= 0 ? "" : ChatColor.GRAY.toString();

		double points = jInfo.getPoints(level, numjobs);
		points = boost.getFinalAmount(CurrencyType.POINTS, points) + ((Jobs.getPlayerManager().getInventoryBoost(player, job)
		    .get(CurrencyType.POINTS)) + 1);
		String pointsColor = xp >= 0 ? "" : ChatColor.RED.toString();

		if (income == 0D && points == 0D && xp == 0D)
		    continue;

		String itemName = jInfo.getRealisticName();
		String val = "";

		if (income != 0.0)
		    val += Jobs.getLanguage().getMessage("command.info.help.money", "%money%", incomeColor
		    + String.format(Jobs.getGCManager().getDecimalPlacesMoney(), income));

		if (points != 0.0)
		    val += Jobs.getLanguage().getMessage("command.info.help.points", "%points%", pointsColor
		    + String.format(Jobs.getGCManager().getDecimalPlacesPoints(), points));

		if (xp != 0.0)
		    val += Jobs.getLanguage().getMessage("command.info.help.exp", "%exp%", xpColor
		    + String.format(Jobs.getGCManager().getDecimalPlacesExp(), xp));

		Lore.add(Jobs.getLanguage().getMessage("command.info.help.material", "%material%", itemName) + val);

		if (y >= 10) {
		    y = 1;

		    if (z == info.size() - 1)
			continue;

		    if (i >= 54) {
			break;
		    }

		    ItemMeta meta = GuiItem.getItemMeta();
		    meta.setDisplayName(job.getNameWithColor());
		    meta.setLore(Lore);
		    GuiItem.setItemMeta(meta);
		    tempInv.setItem(i, GuiItem.clone());

		    GuiItem = job.getGuiItem();
		    Lore = new ArrayList<>();
		    Lore.add(Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase() + ".info"));
		    i++;
		}

		y++;
	    }

	    // TODO: Make new page when the gui size is bigger than 54
	    if (i >= 54) {
		break;
	    }

	    ItemMeta meta = GuiItem.getItemMeta();
	    meta.setDisplayName(job.getNameWithColor());
	    meta.setLore(Lore);
	    GuiItem.setItemMeta(meta);
	    tempInv.setItem(i, GuiItem.clone());
	    i++;
	}

	List<ItemStack> items = new ArrayList<>();
	for (ItemStack one : tempInv.getContents()) {
	    if (one != null)
		items.add(one);
	}

	int GuiSize = GUIManager.isOpenedGui(player) && GUIManager.getGui(player) != null ?
	    GUIManager.getGui(player).getInvSize().getFields() : Jobs.getGCManager().getJobsGUIRows() * 9;
	int backButton = Jobs.getGCManager().getJobsGUIBackButton();

	CMIGui gui = new CMIGui(player);
	gui.setTitle(Jobs.getLanguage().getMessage("command.info.gui.jobinfo", "[jobname]", job.getName()));
	gui.setFiller(CMIMaterial.get(Jobs.getGCManager().guiFiller));
	gui.setInvSize(GuiSize);

	for (int i1 = 0; i1 < items.size(); i1++) {
	    gui.addButton(new CMIGuiButton(i1, items.get(i1)));
	}

	if (!fromCommand) {
	    ItemStack skull = Jobs.getGCManager().guiBackButton;
	    ItemMeta skullMeta = skull.getItemMeta();

	    skullMeta.setDisplayName(Jobs.getLanguage().getMessage("command.info.gui.back"));
	    skull.setItemMeta(skullMeta);

	    gui.addButton(new CMIGuiButton(backButton, skull) {
		@Override
		public void click(GUIClickType type) {
		    openJobsBrowseGUI(player);
		}
	    });
	}

	gui.fillEmptyButtons();
	gui.open();
    }
}
