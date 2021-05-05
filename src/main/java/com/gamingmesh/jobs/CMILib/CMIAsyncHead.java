package com.gamingmesh.jobs.CMILib;

import org.bukkit.inventory.ItemStack;

public class CMIAsyncHead {
    private boolean head = false;
    private boolean force = false;

    public void afterAsyncUpdate(ItemStack item) {

    }

    public boolean isAsyncHead() {
	return head;
    }

    public void setAsyncHead(boolean head) {
	this.head = head;
    }

    public boolean isForce() {
	return force;
    }

    public void setForce(boolean force) {
	this.force = force;
    }
}
