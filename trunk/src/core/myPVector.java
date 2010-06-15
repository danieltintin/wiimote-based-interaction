package src.core;

import java.io.Serializable;

public class myPVector implements Serializable {

	public float x,y,z;
	
	public myPVector() {
	}
	
	public myPVector(processing.core.PVector v) {

		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + z + " ]";
	}
}
