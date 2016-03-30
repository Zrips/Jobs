package com.gamingmesh.jobs.Signs;

import java.util.ArrayList;
import java.util.List;

public class SignInfo {

    List<Sign> AllSigns = new ArrayList<Sign>();

    public SignInfo() {
    }

    public void setAllSigns(List<Sign> AllSigns) {
	this.AllSigns = AllSigns;
    }

    public List<Sign> GetAllSigns() {
	return this.AllSigns;
    }

    public void removeSign(Sign sign) {
	this.AllSigns.remove(sign);
    }

    public void addSign(Sign sign) {
	this.AllSigns.add(sign);
    }
}
