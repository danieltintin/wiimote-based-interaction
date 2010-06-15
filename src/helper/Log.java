package src.helper;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import processing.core.PApplet;
import processing.core.PVector;
import src.core.Main;
import src.core.XBox;
import src.core.myPVector;
import src.visualize.Canvas;
import src.visualize.MediaGenerator;

// Logger class
// every logging activity should go in this class!
public class Log {
	
	
	private static int test_subject = 6;
	
	
	private static String 	path = "src/files/startconfig/", 
							log_path = "src/files/log/test/test" + test_subject +"/";
	
	public PApplet papplet;
	
	private static int numbertasks = 0, colortasks = 0, phototasks = 0;
	private static int rotation_events = 0;
	private static int click_events = 0;
	private static int mouse_distance = 0;
	private static int move_events = 0;
	private static int zoom_events = 0;
	private static double 	yaw_velocity_sum = 0,
							pitch_velocity_sum = 0,
							roll_velocity_sum = 0;
	private static int zoom_dist_sum = 0;
	private static int acceleration_events = 0;
	private static int button_click_events = 0;
	private static long elapsed_time = 0;
	
	public  static final int TO_CONSOLE = 0;
	public  static final int TO_FILE    = 1;
	private static Logger logger;
	

	public Log(PApplet papplet) {
		this.papplet = papplet;
		// set up a simple configuration that logs on the console
		// cannot be deleted
	    BasicConfigurator.configure();
	    // turn off bluecove logging
	    //com.intel.bluetooth.BlueCoveImpl.setConfigProperty(name, value)
	    com.intel.bluetooth.DebugLog.setDebugEnabled(false);
		logger = Logger.getLogger(Log.class);
	}
	
	// not used at test-runtime, just for setting up decent start positions
	public static void printAllBoxesData(ArrayList<XBox> boxes, int writeDestination) {
		if(writeDestination == TO_CONSOLE) { // to console
			logger.info("print all boxes data");
			int cnt = 1;
			for(XBox box : boxes) {
				logger.info("Box #"+cnt);
				logger.info(box.getRotVec());
				logger.info(box.getPosVec());
				cnt++;
			}
		} else { // to file
			switch(MediaGenerator.activeTextureMode) {
				case MediaGenerator.NUMBERS: savePVectors(path + "number" + "_task" + 
															(numbertasks++) + ".bin", boxes);
				break;
				case MediaGenerator.COLORS: savePVectors(path + "color" + "_task" + 
															(colortasks++) + ".bin", boxes);
				break;
				case MediaGenerator.PHOTOS: savePVectors(path + "photo" + "_task" + 
															(phototasks++) + ".bin", boxes); 
				break;
			}
		}
	}
	
	private static void savePVectors(String filename ,ArrayList<XBox> boxes) {	
		
		ArrayList<myPVector> list = new ArrayList<myPVector>();
		for(XBox box : boxes) {
			myPVector rotv = new myPVector(box.getRotVec());
			myPVector posv = new myPVector(box.getPosVec());
			list.add(rotv);
			list.add(posv);
		}

	    FileOutputStream fos = null;
	    ObjectOutputStream out = null;
	    try
	    {
	       fos = new FileOutputStream(filename);
	       out = new ObjectOutputStream(fos);
	       out.writeObject(list);
	       out.close();
	     }
	     catch(IOException ex)
	     {
	       ex.printStackTrace();
	     }
	}
	
	public static ArrayList<PVector> loadPVectors(String filename) throws FileNotFoundException {
		
		ArrayList<myPVector> list = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try
		{
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			list = (ArrayList<myPVector>)in.readObject();
			in.close();
		}
		catch (FileNotFoundException e) {				
			throw e;		
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}		
		
		ArrayList<PVector> vectors = new ArrayList<PVector>();
		for(myPVector v : list) {
			PVector V = new PVector(v.x, v.y, v.z);
			vectors.add(V);
		}
		
		return vectors;
	}	
	
	private void writeHeader(ReadWriteTextFile out) {
		
		String tmp[] = out.getFilename().split("/");
		
		String filename = "#Filename: " + tmp[tmp.length-1];
		
		Calendar now = Calendar.getInstance();
		SimpleDateFormat  sdf = new SimpleDateFormat("MM/dd/yyyy");
		String timestamp = "#Log written at " + sdf.format(now.getTime());
		StringBuilder sb = new StringBuilder();
		
		int hashes = Math.max(timestamp.length(), filename.length()) + 3;
		
		sb.append("\t\t");
		for(int i=0; i<=hashes; i++)
			sb.append("#");
		sb.append("\n");
		
		sb.append("\t\t" + filename);
		for(int i=0; i<hashes - filename.length(); i++)
			sb.append(" ");
		sb.append("#\n");
		
		sb.append("\t\t" + timestamp);
		for(int i=0; i<hashes - timestamp.length(); i++)
			sb.append(" ");
		sb.append("#\n");
			
		sb.append("\t\t");
		for(int i=0; i<=hashes; i++)
			sb.append("#");
		
		sb.append("\n");
		out.writeToFile(sb.toString());
	}
	
	public void writeTaskData() {
		
		long hash = System.currentTimeMillis();
		ReadWriteTextFile out = new ReadWriteTextFile(ReadWriteTextFile.WRITE, 
				log_path + "log" + hash + ".txt");		
		
		//write screenshot of solution
        try {
        	Dimension display = (Toolkit.getDefaultToolkit()).getScreenSize();
            Robot robot = new Robot();
            // Capture the screen shot of the area of the screen defined by the rectangle
            BufferedImage bi=robot.createScreenCapture(
            		new Rectangle(
            				(display.width - Canvas.width) / 2,
            				(display.height - Canvas.height) / 2,
            				Canvas.width,
            				Canvas.height));              		
            ImageIO.write(bi, "png", new File(System.getProperty("user.dir") + "/" + 
            									log_path + "solSS" + hash + ".png"));            
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		writeHeader(out);
		
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.ENGLISH);
		
		formatter.format("%-25s %15d\n", "Task number", MediaGenerator.task_number);
		formatter.format("%-25s %15d\n", "Interaction Technique", Main.technique);
		formatter.format("%-25s %15d\n", "Mode", MediaGenerator.activeTextureMode);
		sb.append("\n");
		
		formatter.format("%-25s %15.2f\n", "Elapsed time in seconds", (double)elapsed_time/1000.0);
		sb.append("\n");
		
		formatter.format("%-25s %15d\n", "Rotation events", rotation_events);
//		formatter.format("%-25s %15d\n", "Click events", click_events);
		formatter.format("%-25s %15d\n", "Mouse distance", mouse_distance);
		formatter.format("%-25s %15d\n", "Move events", move_events);
		formatter.format("%-25s %15d\n", "Zoom events", zoom_events);
//		formatter.format("%-25s %15d\n", "Angle velocity sum", (int)angle_velocity_sum);
		formatter.format("%-25s %15d\n", "Yaw velocity sum", (int)yaw_velocity_sum);
		formatter.format("%-25s %15d\n", "Roll velocity sum", (int)roll_velocity_sum);
		formatter.format("%-25s %15d\n", "Pitch velocity sum", (int)pitch_velocity_sum);
		formatter.format("%-25s %15d\n", "Zoom distance sum", zoom_dist_sum);
		formatter.format("%-25s %15d\n", "Acceleration events", acceleration_events);
		formatter.format("%-25s %15d\n", "Button click events", button_click_events);
		
//		formatter.format("%-25s %15d\n", "", );

//		formatter.format("%-25s %15d\n", "Dicks in a box", new Random().nextInt(666));
		
		out.writeToFile(sb.toString());
		out.writeToFileFinished();		

		resetLogVars();
	}
	
	public static void resetLogVars() {
		rotation_events = 0;
		click_events = 0;
		mouse_distance = 0;
		move_events = 0;
		zoom_events = 0;
		zoom_dist_sum = 0;
		acceleration_events = 0;
		button_click_events = 0;		
	}
	
	public void printElapsedTime() {
		
	}
	
	public void printRotationEvents() {
		logger.info("ROTATION EVENT");
	} 
	
	public void printClickEvents() {
		logger.info("CLICK EVENT");
	}
	
	public void printTranslationEvents() {
		logger.info("TRANSLATION EVENT");
	}
	
	public void printMouseCoords() {
		
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static void startTimer() {
		elapsed_time = System.currentTimeMillis();
	}
	
	public static void stopTimer() {
		elapsed_time = System.currentTimeMillis() - elapsed_time;
	}
	
	//TODO clicking what?
	public static void addClickEvent() {
		click_events++;
	}
	
	public static void addButtonClickEvent() {
		button_click_events++;
	}
	
	public static void addRotationEvent() {
		rotation_events++;
	}
	
	public static void addAccelerationEvent() {
		acceleration_events++;
	}
	
	public static void addMouseDistance(int dist) {
		mouse_distance += dist;
	}
	
	public static void addZoomDistance(int dist) {
		zoom_dist_sum+= dist; 
	}
	
	public static void addYawVelocity(double vel) {
		yaw_velocity_sum += vel;
	}
	
	public static void addRollVelocity(double vel) {
		roll_velocity_sum += vel;
	}
	
	public static void addPitchVelocity(double vel) {
		pitch_velocity_sum += vel;
	}	
	
	public static void addZoomEvent() {
		zoom_events++;
	}
	
	public static void addMoveEvent() {
		move_events++;
	}
}