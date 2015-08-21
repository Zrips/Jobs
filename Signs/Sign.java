package Signs;

public class Sign {

	int Category = 0;
	String World = null;
	double x = 0.01;
	double y = 0.01;
	double z = 0.01;
	int Number = 0;
	String JobName = null;
	boolean special = false;

	public Sign() {
	}
	
	public void setSpecial(boolean special) {
		this.special = special;
	}

	public boolean isSpecial() {
		return this.special;
	}
	
	public void setJobName(String JobName) {
		this.JobName = JobName;
	}

	public String GetJobName() {
		return this.JobName;
	}
	
	public void setCategory(int Category) {
		this.Category = Category;
	}

	public int GetCategory() {
		return this.Category;
	}

	public void setWorld(String World) {
		this.World = World;
	}

	public String GetWorld() {
		return this.World;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double GetX() {
		return this.x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double GetY() {
		return this.y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double GetZ() {
		return this.z;
	}

	public void setNumber(int Number) {
		this.Number = Number;
	}

	public int GetNumber() {
		return this.Number;
	}
}
