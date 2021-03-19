package com.gamingmesh.jobs.stuff.complement;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

import io.papermc.paper.event.player.AsyncChatEvent;

public final class KyoriChatEvent extends Complement2 implements Listener {

	private Jobs plugin;

	public KyoriChatEvent(Jobs plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void asyncChatEvent(final AsyncChatEvent event) {
		if (event.isCancelled() || Util.getJobsEditorMap().isEmpty())
			return;

		final String msg = Util.getJobsEditorMap().remove(event.getPlayer().getUniqueId());
		if (msg != null) {
			plugin.getServer().getScheduler().runTask(plugin,
					() -> event.getPlayer().performCommand(msg + serialize(event.message())));
			event.setCancelled(true);
		}
	}

	// Adding to chat prefix job name
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncChatEvent event) {
		if (!Jobs.getGCManager().getModifyChat())
			return;

		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
		String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";
		if (honorific.equals(" "))
			honorific = "";

		final String h = honorific;

		// TODO displayName returns the player display name not the chat component from
		// chat plugins, like Essentials
		event.formatter((displayName, msg) -> {
			String newMessage = serialize(msg);
			newMessage = newMessage.replace("{jobs}", h);
			return deserialize(newMessage);
		});
	}

	// Changing chat prefix variable to job name
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChatLow(AsyncChatEvent event) {
		onPlayerChatHigh(event);
	}

	// Changing chat prefix variable to job name
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChatHigh(AsyncChatEvent event) {
		if (Jobs.getGCManager().getModifyChat())
			return;

		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer());
		String honorific = jPlayer != null ? jPlayer.getDisplayHonorific() : "";
		if (honorific.equals(" "))
			honorific = "";

		final String h = honorific;
		event.formatter((displayName, msg) -> {
			String newMessage = serialize(msg);
			newMessage = newMessage.replace("{jobs}", h);
			return deserialize(newMessage);
		});
	}
}
