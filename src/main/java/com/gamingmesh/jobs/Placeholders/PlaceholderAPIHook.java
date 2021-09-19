package com.gamingmesh.jobs.Placeholders;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Placeholders.Placeholder.JobsPlaceHolders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Jobs plugin;

    public PlaceholderAPIHook(Jobs plugin) {
	this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
	JobsPlaceHolders placeHolder = JobsPlaceHolders.getByName(identifier);
	return plugin.getPlaceholderAPIManager().getValue(player, placeHolder, "%" + Placeholder.pref + "_" + identifier + "%");
    }

    @Override
    public boolean persist() {
	return true;
    }

    @Override
    public boolean canRegister() {
	return true;
    }

    @Override
    public String getAuthor() {
	return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
	return Placeholder.pref;
    }

    @Override
    public String getVersion() {
	return plugin.getDescription().getVersion();
    }
}
