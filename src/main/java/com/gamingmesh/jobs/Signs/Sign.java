package com.gamingmesh.jobs.Signs;

public class Sign {

    private int Category = 0;
    private String World = null;
    private double x = 0.01;
    private double y = 0.01;
    private double z = 0.01;
    private int Number = 0;
    private String JobName = null;
    private boolean special = false;

    public Sign() {
    }

    public void setSpecial(boolean special) {
	this.special = special;
    }

    public boolean isSpecial() {
	return special;
    }

    public void setJobName(String JobName) {
	this.JobName = JobName;
    }

    public String GetJobName() {
	return JobName;
    }

    public void setCategory(int Category) {
	this.Category = Category;
    }

    public int GetCategory() {
	return Category;
    }

    public void setWorld(String World) {
	this.World = World;
    }

    public String GetWorld() {
	return World;
    }

    public void setX(double x) {
	this.x = x;
    }

    public double GetX() {
	return x;
    }

    public void setY(double y) {
	this.y = y;
    }

    public double GetY() {
	return y;
    }

    public void setZ(double z) {
	this.z = z;
    }

    public double GetZ() {
	return z;
    }

    public void setNumber(int Number) {
	this.Number = Number;
    }

    public int GetNumber() {
	return Number;
    }
}
