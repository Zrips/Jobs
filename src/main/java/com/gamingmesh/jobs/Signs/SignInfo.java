package com.gamingmesh.jobs.Signs;

import java.util.ArrayList;
import java.util.List;

public class SignInfo {

    private final List<jobsSign> AllSigns = new ArrayList<>();

    public void setAllSigns(List<jobsSign> AllSigns) {
	this.AllSigns.clear();
	this.AllSigns.addAll(AllSigns == null ? new ArrayList<>() : AllSigns);
    }

    public List<jobsSign> GetAllSigns() {
	return AllSigns;
    }

    public void removeSign(jobsSign sign) {
	this.AllSigns.remove(sign);
    }

    public void addSign(jobsSign sign) {
	this.AllSigns.add(sign);
    }
}
