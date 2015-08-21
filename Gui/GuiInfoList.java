package Gui;

import java.util.ArrayList;
import java.util.List;

import com.gamingmesh.jobs.container.Job;

public class GuiInfoList {

	String name;
	List<Job> jobList = new ArrayList<Job>();
	Boolean jobInfo = false;
	int backButton = 27;

	public GuiInfoList(String name) {
		this.name = name;
	}

	public int getbackButton() {
		return this.backButton;
	}

	public void setbackButton(int backButton) {
		this.backButton = backButton;
	}

	public String getName() {
		return this.name;
	}

	public List<Job> getJobList() {
		return this.jobList;
	}

	public void setJobList(List<Job> jobList) {
		this.jobList = jobList;
	}

	public void setJobInfo(Boolean jobInfo) {
		this.jobInfo = jobInfo;
	}

	public Boolean isJobInfo() {
		return this.jobInfo;
	}
}
