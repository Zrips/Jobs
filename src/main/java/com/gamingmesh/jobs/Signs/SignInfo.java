package com.gamingmesh.jobs.Signs;

import java.util.ArrayList;
import java.util.List;

public class SignInfo {

    private final List<jobsSign> allSigns = new ArrayList<>();

    public void setAllSigns(List<jobsSign> AllSigns) {
	this.allSigns.clear();
	this.allSigns.addAll(AllSigns == null ? new ArrayList<>() : AllSigns);
    }

    public List<jobsSign> getAllSigns() {
	return allSigns;
    }

    public void removeSign(jobsSign sign) {
	this.allSigns.remove(sign);
    }

    public void addSign(jobsSign sign) {
	this.allSigns.add(sign);
    }
}
