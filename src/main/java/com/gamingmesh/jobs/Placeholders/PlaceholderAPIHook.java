package com.gamingmesh.jobs.Placeholders;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Placeholders.Placeholder.JobsPlaceHolders;

import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderAPIHook extends EZPlaceholderHook {

    private Jobs plugin;

    public PlaceholderAPIHook(Jobs plugin) {
	super(plugin, Placeholder.pref);
	this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
	JobsPlaceHolders placeHolder = JobsPlaceHolders.getByName(identifier);
	if (placeHolder == null) {
	    return null;
	}
	return plugin.getPlaceholderAPIManager().getValue(player, placeHolder, "%" + Placeholder.pref + "_" + identifier + "%");
    }

}
