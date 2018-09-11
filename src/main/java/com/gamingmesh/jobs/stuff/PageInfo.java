package com.gamingmesh.jobs.stuff;

public class PageInfo {

    private int totalEntries = 0;
    private int totalPages = 0;
    private int start = 0;
    private int end = 0;
    private int currentPage = 0;

    private int currentEntry = 0;

    private int perPage = 6;

    public PageInfo(int perPage, int totalEntries, int currentPage) {
	this.perPage = perPage;
	this.totalEntries = totalEntries;
	this.currentPage = currentPage;
	calculate();
    }

    public int getPositionForOutput() {
	return currentEntry;
    }

    public int getPositionForOutput(int place) {
	return start + place + 1;
    }

    private void calculate() {
	start = (currentPage - 1) * perPage;
	end = start + perPage - 1;
	if (end + 1 > totalEntries)
	    end = totalEntries - 1;
	totalPages = (int) Math.ceil((double) totalEntries / (double) perPage);
    }

    public boolean isInRange(int place) {
	if (place >= start && place <= end)
	    return true;
	return false;
    }

    public boolean isEntryOk() {
	currentEntry++;
	return currentEntry - 1 >= start && currentEntry - 1 <= end;
    }

    public boolean isBreak() {
	return currentEntry - 1 > end;
    }

    public boolean isPageOk() {
	return isPageOk(currentPage);
    }

    public boolean isPageOk(int page) {
	if (totalPages < page)
	    return false;
	if (page < 1)
	    return false;
	return true;
    }

    public int getStart() {
	return start;
    }

    public int getEnd() {
	return end;
    }

    public int getTotalPages() {
	return totalPages;
    }

    public int getCurrentPage() {
	return currentPage;
    }

    public int getTotalEntries() {
	return totalEntries;
    }
    public int getPerPageCount(){
	return perPage;
    }
}