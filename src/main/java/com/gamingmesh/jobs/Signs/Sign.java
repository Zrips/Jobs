package com.gamingmesh.jobs.Signs;

public class Sign {

    private String World = null;
    private double x = 0.01;
    private double y = 0.01;
    private double z = 0.01;
    private int Number = 0;
    private String JobName = null;
    private boolean special = false;

    public void setSpecial(boolean special) {
	this.special = special;
    }

    public boolean isSpecial() {
	return special;
    }

    public void setJobName(String JobName) {
	this.JobName = JobName;
    }

    public String getJobName() {
	return JobName;
    }

    public void setWorld(String World) {
	this.World = World;
    }

    public String getWorld() {
	return World;
    }

    public void setX(double x) {
	this.x = x;
    }

    public double getX() {
	return x;
    }

    public void setY(double y) {
	this.y = y;
    }

    public double getY() {
	return y;
    }

    public void setZ(double z) {
	this.z = z;
    }

    public double getZ() {
	return z;
    }

    public void setNumber(int Number) {
	this.Number = Number;
    }

    public int getNumber() {
	return Number;
    }
}
