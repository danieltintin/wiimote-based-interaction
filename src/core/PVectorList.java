package src.core;

import java.io.Serializable;
import java.util.ArrayList;

public class PVectorList implements Serializable {

	ArrayList<myPVector> list;
	
	PVectorList(ArrayList<myPVector> list) {
		this.list = list;
	}
}
