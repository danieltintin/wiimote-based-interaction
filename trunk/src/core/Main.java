package src.core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Random;

import org.wiigee.control.WiimoteWiigee;
import org.wiigee.device.Wiimote;
import org.wiigee.filter.RotationThresholdFilter;

import processing.core.PApplet;
import src.helper.Log;
import src.visualize.Canvas;
import src.visualize.DebugGui;
import src.visualize.Gui;
import src.visualize.MediaGenerator;
import controlP5.ControlEvent;


//VM arguments
//-Dbluecove.stack.first=widcomm
//-Dbluecove.debug.log4j=false
// 

public class Main extends PApplet {

	private static final long serialVersionUID = -4722381076232644569L;

	//P0
//	public static int technique_sequence[] = {1,2,3};
//	public static int environment_sequence[] = {
//		MediaGenerator.NUMBERS,
//		MediaGenerator.COLORS,
//		MediaGenerator.PHOTOS};
	//P1
//	public static int technique_sequence[] = {1,2,3};
//	public static int environment_sequence[] = {
//		MediaGenerator.COLORS,
//		MediaGenerator.PHOTOS,
//		MediaGenerator.NUMBERS};
	//P2
//	public static int technique_sequence[] = {1,2,3};
//	public static int environment_sequence[] = {
//		MediaGenerator.PHOTOS,
//		MediaGenerator.NUMBERS,
//		MediaGenerator.COLORS};
	//P3
//	public static int technique_sequence[] = {2,3,1};
//	public static int environment_sequence[] = {
//		MediaGenerator.NUMBERS,
//		MediaGenerator.COLORS,
//		MediaGenerator.PHOTOS};
	//P4
//	public static int technique_sequence[] = {2,3,1};
//	public static int environment_sequence[] = {
//		MediaGenerator.COLORS,
//		MediaGenerator.PHOTOS,
//		MediaGenerator.NUMBERS};
	//P5
//	public static int technique_sequence[] = {2,3,1};
//	public static int environment_sequence[] = {
//		MediaGenerator.PHOTOS,
//		MediaGenerator.NUMBERS,
//		MediaGenerator.COLORS};
	//P6
	public static int technique_sequence[] = {3,1,2};
	public static int environment_sequence[] = {
		MediaGenerator.NUMBERS,
		MediaGenerator.COLORS,
		MediaGenerator.PHOTOS};
	//P7
//	public static int technique_sequence[] = {3,1,2};
//	public static int environment_sequence[] = {
//		MediaGenerator.COLORS,
//		MediaGenerator.PHOTOS,
//		MediaGenerator.NUMBERS};
	//P8
//	public static int technique_sequence[] = {3,1,2};
//	public static int environment_sequence[] = {
//		MediaGenerator.PHOTOS,
//		MediaGenerator.NUMBERS,
//		MediaGenerator.COLORS};
	
	//shouldnt be smaller than 800x600px
	Dimension display = (Toolkit.getDefaultToolkit()).getScreenSize();
	private int width  = (int)(display.width * 0.6);
	private int height = (int)(display.height * 0.6);
	
	public static int NUMBER_BOXES = 8;

	public Input input;
	private MediaGenerator mediaGenerator;
	private Canvas canvas;
	private Listeners listeners;
	private Gui gui;
	public DebugGui debugGui;
	private Log log;
	
	//wiimote variables
	public WiimoteWiigee wiigee;

	//technique 0 is mouse mode, -1 means only mouse and never loading wiimotes
	public static int technique = -1;
	public boolean enable_IR_camera;
	public boolean enable_wii_motion_plus;	
	
	public My_Wiimote wiimote1 = null, wiimote2 = null;
	private final String wiimote1_mac_address = "001E358017CF", wiimote2_mac_address = "001E3570E6B1";
	
	public static void main(String[] args) {
		try {
			//PApplet.main(new String[] {"--present","src.core.Main"});
			PApplet.main(new String[] {"src.core.Main"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
	}
	
	public void setup() {				
		
		noStroke();
		
		size(width, height, P3D);
		
		log = new Log(this);
		
		frameRate(15);
		
		canvas = new Canvas(this, width, height);
		
		mediaGenerator = new MediaGenerator(NUMBER_BOXES, this, width, height);
		
		gui = new Gui(this);
		
		debugGui = new DebugGui(this);
		
		input = new Input(this, mediaGenerator, canvas, gui, debugGui, log, this); 
		
		
		if(Main.technique >= 0) {
			//initalize the wiigee library
			wiigee = new WiimoteWiigee(); 
			
			initializeDualWiimotes();
			My_Wiimote wiimotes[] = {wiimote1, wiimote2};
			listeners = new Listeners(input, wiimote1, wiimote2, this);
			
			for(My_Wiimote wiimote : wiimotes) {
				listeners.addWiiRotationListener(wiimote);
				listeners.addWiiInfraredListener(wiimote);	
				listeners.addWiiAccelerationListener(wiimote);				
				listeners.addWiiButtonListener(wiimote);
				
				//needs a fairly high treshold to get rid of garbage data
		        wiimote.addRotationFilter(new RotationThresholdFilter(15.0));
		        wiimote.setRecognitionButton(-1);	        
			}
			
			mediaGenerator.startSequence(input);
		}
		else {
			mediaGenerator.create(0, -1);
		}
	}
	
	public static void shutdown() {
		// here go all the shutdown commands
		//if(wiimote1 != null)
		//	wiimote1.disconnect();
	}
	
	public void reInitializeDualWiimotes() {
		
		wiimote1.disconnect(); wiimote1 = null;
		wiimote2.disconnect(); wiimote2 = null;
		
		initializeDualWiimotes();
		My_Wiimote wiimotes[] = {wiimote1, wiimote2};
		listeners = new Listeners(input, wiimote1, wiimote2, this);		

		for(My_Wiimote wiimote : wiimotes) {
			listeners.addWiiRotationListener(wiimote);
			listeners.addWiiInfraredListener(wiimote);	
			listeners.addWiiAccelerationListener(wiimote);				
			listeners.addWiiButtonListener(wiimote);
			
			//needs a fairly high treshold to get rid of garbage data
	        wiimote.addRotationFilter(new RotationThresholdFilter(15.0));
	        wiimote.setRecognitionButton(-1);	        
		}
		mediaGenerator.resumeSequence();
	}
	
	public void continueSequence() {
		mediaGenerator.continueSequence();
	}
	
	public void initializeDualWiimotes() {
		
		println("Please turn on both Wiimotes");
		Random rand = new Random();
		int i;		
		while(wiimote1 == null || wiimote2 == null) {
			try {
				i = rand.nextInt(2);			
				if(wiimote1 == null && i == 0) {							
					wiimote1 = new My_Wiimote(wiimote1_mac_address, true, true);
					wiimote1.setLED(1);
					wiimote1.hand = My_Wiimote.Hand.right_hand;
				}
				if(wiimote2 == null && i == 1) {					
					wiimote2 = new My_Wiimote(wiimote2_mac_address, true, true);
					wiimote2.setLED(2);
					wiimote2.hand = My_Wiimote.Hand.left_hand;
				}
			} catch(NoClassDefFoundError e) {
				//bluetooth not enabled on system
				println("Your system does not support bluetooth");			
				return;
			} catch (IOException e) {
				//if the wiimote is not turned on, wait abit				
				try { Thread.sleep(50); } catch (InterruptedException e1) {}
			}
		}//end while
	}
	
	public void initializeWiimote() {
		
		println("Please turn on the Wiimote");
		int i = 0;
		while(wiimote1 == null) {
			try {
				// hardcoded MAC address of the wiimote, way faster than cycling through 
				//every single BT-device
				if(i % 2 == 0)
					wiimote1 = new My_Wiimote(wiimote1_mac_address, true, true);					
				else
					wiimote1 = new My_Wiimote(wiimote2_mac_address, true, true);	
				wiimote1.hand = My_Wiimote.Hand.right_hand;
			} catch(NoClassDefFoundError e) {
				//bluetooth not enabled on system
				println("Your system does not support bluetooth");			
				return;
			} catch (IOException e) { 
				//if the wiimote is not turned on, wait abit
				i++;
				try { Thread.sleep(250); } catch (InterruptedException e1) {}
			}
		}//end while
	}
	
	public void setupWiimote(My_Wiimote wiimote) {
		if(Main.technique == 0)
			return;
		try {
			if(!enable_wii_motion_plus)
				wiimote.setInfraredCameraEnabled(enable_IR_camera, Wiimote.IR_MODE_EXTENDED);
			else
				wiimote.setInfraredCameraEnabled(enable_IR_camera, Wiimote.IR_MODE_STANDARD);
			wiimote.setWiiMotionPlusEnabled(enable_wii_motion_plus);			
		} catch (IOException e) {
			e.printStackTrace();
			//println("Error while activating Infrared Camera:");
		}           
	}
    	
    public void click() {
    	input.mousePressed(mouseX, mouseY);
    }
	// mouse event listener - pressed
	public void mousePressed() {
		input.mousePressed(mouseX, mouseY);
	}
	
	// mouse event listener - released
	public void mouseReleased() {
		input.mouseReleased(mouseX, mouseY);
	}
	
	// mouse event listener - dragged
	public void mouseDragged() {
		if(input.getSwitchMode()) {
			input.mouseDragged(mouseX - pmouseX, mouseY - pmouseY, mouseX, mouseY);
		} else {			
			if (mouseX > 0 && mouseY > 0) 
				input.mouseRotation(mouseX, mouseY, pmouseX, pmouseY);
		}
	}
	
	// keyboard event listener
	public void keyPressed() {
		input.keyPressed(key, keyCode);
	}
	
	public void controlEvent(ControlEvent theEvent) {
		//println("got a control event from controller with id "+theEvent.controller().id());
		if(!theEvent.isGroup()) {
			  switch(theEvent.controller().id()) {
			    case(1):
			    	if(wiimote1 != null && wiimote2 != null) {
			    		println("re-initializing wiimotes...");
				    	wiimote1.disconnect();
				    	wiimote2.disconnect();
				    	initializeDualWiimotes();
			    	} else if (wiimote1 != null && wiimote2 == null){
			    		wiimote1.disconnect();
			    		initializeWiimote();
			    	} else {
			    		println("");
			    	}
			    	
			    break;
			    case(2):
			    	println("'show default' pressed");
			    	// FILL IN
			    break;
			    case(3):
			    	println();
		    		println(".................");
		    		println("STARTING (# "+(debugGui.getTechniqueValue()-1)+""+
		    				(debugGui.getConfigurationValue()-1)+""+debugGui.getModeValue()+""+
		    				"-"+debugGui.getLoggingValue()+")");
		    		println(debugGui.getTechniqueValue()+" (technique)");
		    		println((debugGui.getConfigurationValue()-1)+ " (configuration)");
		    		println(debugGui.getModeValue()+" (mode)");
		    		println("logging: "+debugGui.getLoggingValue());
		    		println(".................");
		    		println();
		    		
		    		input.changeInteractionMode(debugGui.getTechniqueValue());
		    		
			    	mediaGenerator.reset(debugGui.getModeValue()-1, debugGui.getConfigurationValue()-1);		    		
		    		
			    	if(debugGui.getDebugGuiStatus()) {
			    		debugGui.updateDebugGuiStatus();
					}
			    break; 
			  }
			}
		}
	
	// scene lights
	private void initializeLight() {
		ambientLight(190, 190, 190, 
				     width/2, height/2, -500);
		pointLight(255, 255, 255, 
				   width/2, height/2, 5);
		spotLight(255, 255, 255, 
				  width, height, 100, 
				  -1, 0, -1, 80, 20);
	}
	
	public void writelog() {
		log.writeTaskData();
	}
	
	public void draw() {
		
		if(!gui.getGuiStatus() && !debugGui.getDebugGuiStatus()) {
			background(255);
			//initializeLight();
			canvas.draw();
			try {
				for (XBox box : mediaGenerator.boxes) { box.draw(); }
			} catch(ConcurrentModificationException e) {
			}
			catch(RuntimeException t) {
				
			}
		} else {
			background(255);
			if(gui.getGuiStatus()) { gui.draw(); };
			if(debugGui.getDebugGuiStatus()) { debugGui.draw(); };
		}
	}
}
