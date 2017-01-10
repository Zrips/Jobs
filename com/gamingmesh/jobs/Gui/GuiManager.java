package com.gamingmesh.jobs.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import com.gamingmesh.jobs.stuff.Perm;

public class GuiManager {

    public HashMap<String, GuiInfoList> GuiList = new HashMap<String, GuiInfoList>();

    public void CloseInventories() {
	for (Entry<String, GuiInfoList> one : GuiList.entrySet()) {
	    Player player = Bukkit.getPlayer(one.getKey());
	    if (player != null) {
		player.closeInventory();
	    }
	}
    }

    public Inventory CreateJobsGUI(Player player) {

	ArrayList<Job> JobsList = new ArrayList<Job>();
	for (Job job : Jobs.getJobs()) {
	    if (Jobs.getGCManager().getHideJobsWithoutPermission())
		if (!Jobs.getCommandManager().hasJobPermission(player, job))
		    continue;
	    JobsList.add(job);
	}

	GuiInfoList guiInfo = new GuiInfoList(player.getName());
	guiInfo.setJobList(JobsList);

	Inventory topinv = player.getOpenInventory().getTopInventory();
	if (topinv != null)
	    player.closeInventory();

	GuiList.put(player.getName(), guiInfo);

	int GuiSize = 9;

	if (JobsList.size() > 9)
	    GuiSize = 18;

	if (JobsList.size() > 18)
	    GuiSize = 27;

	if (JobsList.size() > 27)
	    GuiSize = 36;

	if (JobsList.size() > 36)
	    GuiSize = 45;

	if (JobsList.size() > 45)
	    GuiSize = 54;

	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	List<JobProgression> pJobs = JPlayer.getJobProgression();

	String title = Jobs.getLanguage().getMessage("command.info.gui.pickjob");
	if (title.length() > 32)
	    title = title.substring(0, 30) + "..";

	Inventory GuiInv = Bukkit.createInventory(null, GuiSize, title);

	for (int i = 0; i < JobsList.size(); i++) {

	    Job job = JobsList.get(i);

	    ArrayList<String> Lore = new ArrayList<String>();

	    for (JobProgression onePJob : pJobs) {
		if (onePJob.getJob().getName().equalsIgnoreCase(job.getName()))
		    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.working"));
	    }

	    int maxlevel = 0;
	    if (Perm.hasPermission(player, "jobs." + job.getName() + ".vipmaxlevel") && job.getVipMaxLevel() != 0)
		maxlevel = job.getVipMaxLevel();
	    else
		maxlevel = job.getMaxLevel();

	    if (maxlevel > 0)
		Lore.add(Jobs.getLanguage().getMessage("command.info.gui.max") + maxlevel);

	    if (Jobs.getGCManager().ShowTotalWorkers)
		Lore.add(Jobs.getLanguage().getMessage("command.browse.output.totalWorkers", "[amount]", job.getTotalPlayers()));

	    if (Jobs.getGCManager().useDynamicPayment && Jobs.getGCManager().ShowPenaltyBonus)
		if (job.getBonus() < 0)
		    Lore.add(Jobs.getLanguage().getMessage("command.browse.output.penalty", "[amount]", (int) (job.getBonus() * 100) / 100.0 * -1));
		else
		    Lore.add(Jobs.getLanguage().getMessage("command.browse.output.bonus", "[amount]", (int) (job.getBonus() * 100) / 100.0));

	    Lore.addAll(Arrays.asList(job.getDescription().split("/n")));

	    if (job.getMaxSlots() != null)
		Lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftSlots") + ((job.getMaxSlots() - Jobs.getUsedSlots(job)) > 0 ? (job.getMaxSlots() - Jobs
		    .getUsedSlots(
			job)) : 0));

	    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.actions"));
	    for (ActionType actionType : ActionType.values()) {
		List<JobInfo> info = job.getJobInfo(actionType);
		if (info != null && !info.isEmpty()) {
		    Lore.add(ChatColor.translateAlternateColorCodes('&', "&e" + Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase()
			+ ".info")));
		}
	    }

	    Lore.add("");
	    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.leftClick"));
	    Lore.add(Jobs.getLanguage().getMessage("command.info.gui.rightClick"));

	    ItemStack GuiItem = job.getGuiItem();

	    ItemMeta meta = GuiItem.getItemMeta();
	    meta.setDisplayName(job.getName());
	    meta.setLore(Lore);
	    GuiItem.setItemMeta(meta);

	    GuiInv.setItem(i, GuiItem);
	}
	return GuiInv;
    }

    public Inventory CreateJobsSubGUI(Player player, Job job) {

	Inventory tempInv = Bukkit.createInventory(null, 54, "");

	ItemStack GuiItem = job.getGuiItem();
	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);


	Boost boost = Jobs.getPlayerManager().getFinalBonus(JPlayer, job);

	int level = 1;
	JobProgression prog = JPlayer.getJobProgression(job);
	if (prog != null)
	    level = prog.getLevel();

	int numjobs = JPlayer.getJobProgression().size();

	List<ItemStack> items = new ArrayList<ItemStack>();
	int i = 0;
	for (ActionType actionType : ActionType.values()) {
	    List<JobInfo> info = job.getJobInfo(actionType);

	    if (info == null || info.isEmpty())
		continue;

	    ArrayList<String> Lore = new ArrayList<String>();
	    Lore.add(ChatColor.translateAlternateColorCodes('&', "&e" + Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase()
		+ ".info")));
	    int y = 1;
	    for (int z = 0; z < info.size(); z++) {
		String itemName = Jobs.getNameTranslatorManager().Translate(info.get(z).getName(), info.get(z));

		double income = info.get(z).getIncome(level, numjobs);
		income = income + (income * boost.getFinal(CurrencyType.MONEY));
		ChatColor incomeColor = income >= 0 ? ChatColor.GREEN : ChatColor.DARK_RED;

		double xp = info.get(z).getExperience(level, numjobs);
		xp = xp + (xp * boost.getFinal(CurrencyType.EXP));
		ChatColor xpColor = xp >= 0 ? ChatColor.YELLOW : ChatColor.GRAY;

		String xpString = String.format("%.2fxp", xp);

		Lore.add(ChatColor.translateAlternateColorCodes('&', "&7" + itemName + " " + xpColor + xpString + " " + incomeColor + Jobs.getEconomy().format(income)));

		if (y >= 10) {
		    y = 1;

		    if (z == info.size() - 1)
			continue;
		    ItemMeta meta = GuiItem.getItemMeta();
		    meta.setDisplayName(job.getName());
		    meta.setLore(Lore);
		    GuiItem.setItemMeta(meta);
		    //GuiInv.setItem(i, GuiItem);
		    tempInv.setItem(i, GuiItem);

		    GuiItem = job.getGuiItem();
		    Lore = new ArrayList<String>();
		    Lore.add(ChatColor.translateAlternateColorCodes('&', "&e" + Jobs.getLanguage().getMessage("command.info.output." + actionType.getName().toLowerCase()
			+ ".info")));
		    i++;
		}
		y++;
	    }
	    ItemMeta meta = GuiItem.getItemMeta();
	    meta.setDisplayName(job.getName());
	    meta.setLore(Lore);
	    GuiItem.setItemMeta(meta);
	    //GuiInv.setItem(i, GuiItem);
	    tempInv.setItem(i, GuiItem);
	    i++;
	}

	for (ItemStack one : tempInv.getContents()) {
	    if (one != null)
		items.add(one);
	}

	int GuiSize = 18;
	int backButton = 9;

	if (items.size() > 9) {
	    GuiSize = 27;
	    backButton = 18;
	}

	if (items.size() > 18) {
	    GuiSize = 36;
	    backButton = 27;
	}

	if (items.size() > 27) {
	    GuiSize = 45;
	    backButton = 36;
	}

	if (items.size() > 36) {
	    GuiSize = 54;
	    backButton = 45;
	}

//	if (items.size() > 45) {
//	    GuiSize = 54;
//	    backButton = 53;
//	}

	String title = Jobs.getLanguage().getMessage("command.info.gui.jobinfo", "[jobname]", job.getName());
	if (title.length() > 32)
	    title = title.substring(0, 30) + "..";
	Inventory GuiInv = Bukkit.createInventory(null, GuiSize, title);

	for (int i1 = 0; i1 < items.size(); i1++) {
	    GuiInv.setItem(i1, items.get(i1));
	}

	ItemStack skull = new ItemStack(Material.JACK_O_LANTERN, 1, (byte) 0);

	ItemMeta skullMeta = skull.getItemMeta();
	skullMeta.setDisplayName(Jobs.getLanguage().getMessage("command.info.gui.back"));

	skull.setItemMeta(skullMeta);

	GuiInv.setItem(backButton, skull);

	GuiInfoList guiInfo = new GuiInfoList(player.getName());
	guiInfo.setJobInfo(true);
	guiInfo.setbackButton(backButton);
	GuiList.put(player.getName(), guiInfo);

	return GuiInv;
    }
}
