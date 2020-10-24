package com.gamingmesh.jobs.stuff;

public class PageInfo {

    private int totalEntries = 0,
		totalPages = 0,
		start = 0,
		end = 0,
		currentPage = 0,
		currentEntry = 0,
		perPage = 6;

    public PageInfo(int perPage, int totalEntries, int currentPage) {
	this.perPage = perPage;
	this.totalEntries = totalEntries;
	this.currentPage = currentPage < 1 ? 1 : currentPage;

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
	return place >= start && place <= end;
    }

    public boolean isEntryOk() {
	currentEntry++;
	return isContinueNoAdd();
    }

    public boolean isContinue() {
	return !isEntryOk();
    }

    public boolean isContinueNoAdd() {
	return currentEntry - 1 >= start && currentEntry - 1 <= end;
    }

    public boolean isBreak() {
	return currentEntry - 1 > end;
    }

    public boolean isPageOk() {
	return isPageOk(currentPage);
    }

    public boolean isPageOk(int page) {
	return (totalPages < page || page < 1) ? false : true;
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

    public int getNextPageNumber() {
	return currentPage + 1 > totalPages ? totalPages : currentPage + 1;
    }

    public int getPrevPageNumber() {
	return currentPage - 1 < 1 ? 1 : currentPage - 1;
    }
}
