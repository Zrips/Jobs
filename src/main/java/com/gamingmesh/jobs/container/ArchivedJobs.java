package com.gamingmesh.jobs.container;

import java.util.HashSet;
import java.util.Set;

public class ArchivedJobs {

    private Set<JobProgression> jobs = new HashSet<JobProgression>();

    public Set<JobProgression> getArchivedJobs() {
	return jobs;
    }

    public JobProgression getArchivedJobProgression(Job job) {
	for (JobProgression one : jobs) {
	    if (one.getJob().isSame(job))
		return one;
	}
	return null;
    }

    public void setArchivedJobs(Set<JobProgression> jobs) {
	this.jobs = jobs;
    }

    public void addArchivedJob(JobProgression job) {
	jobs.add(job);
    }

    public void removeArchivedJob(Job job) {
	for (JobProgression one : jobs) {
	    if (one.getJob().isSame(job)) {
		jobs.remove(one);
		break;
	    }
	}
    }
}
