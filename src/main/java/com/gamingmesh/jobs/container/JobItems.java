/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.JobsItemType;
import com.gamingmesh.jobs.config.ShopManager;

import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;

public class JobItems {

    private String node;

    private String itemString;

    private BoostMultiplier boostMultiplier = new BoostMultiplier();

    private final List<Job> jobs = new ArrayList<>();

    private int fromLevel = 0;
    private int untilLevel = Integer.MAX_VALUE;

    private JobsItemType type = JobsItemType.Unknown;

    public JobItems(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    public CMIItemStack getItemStack(Player player, CMIAsyncHead ahead) {
        if (itemString == null)
            return null;

        CMIItemStack item = CMIItemStack.deserialize(itemString.replace("[player]", player == null ? "" : player.getName()), ahead);

        if (item != null) {
            switch (this.getType()) {
            case Boosted:
                item.setItemStack(ItemBoostManager.applyNBT(item.getItemStack(), getNode()));
                break;
            case Shop:
                item.setItemStack(ShopManager.applyNBT(item.getItemStack(), getNode()));
                break;
            case Unknown:
                break;
            default:
                break;
            }
        }

        return item;
    }

    public BoostMultiplier getBoost() {
        return boostMultiplier.clone();
    }

    public BoostMultiplier getBoost(JobProgression job) {
        if (job == null || !jobs.contains(job.getJob()) || job.getLevel() < getFromLevel() || job.getLevel() > getUntilLevel())
            return new BoostMultiplier();

        return boostMultiplier.clone();
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs.clear();

        if (jobs != null) {
            this.jobs.addAll(jobs);
        }
    }

    public int getFromLevel() {
        return fromLevel;
    }

    public void setFromLevel(int fromLevel) {
        this.fromLevel = fromLevel;
    }

    public int getUntilLevel() {
        return untilLevel;
    }

    public void setUntilLevel(int untilLevel) {
        this.untilLevel = untilLevel;
    }

    public CMIItemStack getItem() {
        return this.getItemStack(null, null);
    }

    public void setItemString(String itemString) {
        this.itemString = itemString.replace(" ", "_");
    }

    public void setBoostMultiplier(BoostMultiplier boostMultiplier) {
        this.boostMultiplier = boostMultiplier;
    }

    public JobsItemType getType() {
        return type;
    }

    public JobItems setType(JobsItemType type) {
        this.type = type;
        return this;
    }

}
