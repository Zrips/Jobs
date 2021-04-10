package com.gamingmesh.jobs.stuff.complement;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextReplacementConfig;

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
		// Now there is a parameter "player", so literally we need to add 800+ chat plugins
		// to this plugin as dependency?
		// 3rd attempt: now we tried to use text replacement config builder to match the variable
		// result: instead of replacing the variable, now the chat message never been sent
		event.composer((player, displayName, msg) -> msg
				.replaceText(TextReplacementConfig.builder().match("{jobs}").once().replacement(h).build()));
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
		event.composer((player, displayName, msg) -> msg
				.replaceText(TextReplacementConfig.builder().match("{jobs}").once().replacement(h).build()));
	}
}
