package src.core;

import java.io.IOException;
import java.util.Vector;
import org.wiigee.device.Wiimote;
import src.visualize.GraphPanel;

public class My_Wiimote extends Wiimote {
	
	public double X_sum = 0, Y_sum = 0, Z_sum = 0;	
	public boolean 	B_PRESSED = false, A_pressed = false, b1_pressed = false, 
					up_pressed = false, down_pressed = false;
	public static enum Hand { left_hand, right_hand };
	public Hand hand;
	public double p_accX, p_accY, p_accZ;
	public Direction prim_direction = Direction.NONE;
	public static enum Direction { NONE, X, Y, Z};
	public long ignore;
	private static int counter = 1;
	public int number;
	
	public Vector<Double> Xs, Ys, Zs;
	public long T0;
	public GraphPanel graph_panel;
	
	public My_Wiimote(String btaddress, boolean autofiltering,
			boolean autoconnect) throws IOException {
		super(btaddress, autofiltering, autoconnect);
		number = counter++;
		
		Xs = new Vector<Double>();
		Ys = new Vector<Double>();
		Zs = new Vector<Double>();
		T0 = 0;
	}	
	
	public void data_reset() {
		X_sum = Y_sum = Z_sum = 0;	
		Xs.clear(); Ys.clear(); Zs.clear();
	}
	
	public int data_count() {
		return Math.max(Math.max(Xs.size(), Ys.size()), Zs.size());
	}
	
	public void addGraphPanel(GraphPanel graph_panel) {
		this.graph_panel = graph_panel;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%8s%8s%8s\n", "Xs", "Ys", "Zs"));
		for(int i=0; i<Xs.size(); i++)
			sb.append(String.format("%8.2f%8.2f%8.2f\n", 
									Xs.elementAt(i), 
									Ys.elementAt(i), Zs.elementAt(i)));
			
		return sb.toString();
	}
}
