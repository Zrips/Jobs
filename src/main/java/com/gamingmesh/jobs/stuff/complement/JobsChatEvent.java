package com.gamingmesh.jobs.stuff.complement;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

@SuppressWarnings("deprecation")
public class JobsChatEvent implements Listener {

    private Jobs plugin;

    public JobsChatEvent(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void asyncPlayerChatEvent(final AsyncPlayerChatEvent event) {
	if (event.isCancelled() || Util.getJobsEditorMap().isEmpty())
	    return;

	final String msg = Util.getJobsEditorMap().remove(event.getPlayer().getUniqueId());
	if (msg != null) {
	    plugin.getServer().getScheduler().runTask(plugin,
		() -> event.getPlayer().performCommand(msg + event.getMessage()));
	    event.setCancelled(true);
	}
    }

    // Adding to chat prefix job name
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
	if (!Jobs.getGCManager().getModifyChat())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";
	if (honorific.equals(" "))
	    honorific = "";

	event.setFormat(event.getFormat().replace("%1$s", honorific + "%1$s"));
    }

    // Changing chat prefix variable to job name
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChatLow(AsyncPlayerChatEvent event) {
	onPlayerChatHigh(event);
    }

    // Changing chat prefix variable to job name
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
	if (Jobs.getGCManager().getModifyChat())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
	String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";
	if (honorific.equals(" "))
	    honorific = "";

	event.setFormat(event.getFormat().replace("{jobs}", honorific));
    }
}
