package com.gamingmesh.jobs.Signs;

import java.util.ArrayList;
import java.util.List;

public class SignInfo {

    private List<Sign> AllSigns = new ArrayList<>();

    public SignInfo() {
    }

    public void setAllSigns(List<Sign> AllSigns) {
	this.AllSigns = AllSigns;
    }

    public List<Sign> GetAllSigns() {
	return AllSigns;
    }

    public void removeSign(Sign sign) {
	this.AllSigns.remove(sign);
    }

    public void addSign(Sign sign) {
	this.AllSigns.add(sign);
    }
}
