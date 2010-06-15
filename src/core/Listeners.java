package src.core;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import org.wiigee.device.Wiimote;
import org.wiigee.event.AccelerationEvent;
import org.wiigee.event.AccelerationListener;
import org.wiigee.event.ButtonListener;
import org.wiigee.event.ButtonPressedEvent;
import org.wiigee.event.ButtonReleasedEvent;
import org.wiigee.event.InfraredEvent;
import org.wiigee.event.InfraredListener;
import org.wiigee.event.MotionStartEvent;
import org.wiigee.event.MotionStopEvent;
import org.wiigee.event.RotationEvent;
import org.wiigee.event.RotationListener;
import org.wiigee.event.RotationSpeedEvent;
import org.wiigee.util.Log;

public class Listeners {

	private My_Wiimote wiimote1 = null, wiimote2 = null;
	private Input input;
	private Main main;
	
	//slider vars
	//in ms. wait this long between each rotate while holding a button (up/down)
	public int button_hold_sleep_time = 100; 
	//at some point we can hopefully do all rotation with freerot.
	public double ACC_ROT_FACTOR = 25; 
	public double FREE_ROT_FACTOR = 25;
	public double ACC_ZOOM_FACTOR = 10;
	//in G. lower limit for the force needed to swipe
	public double SWIPE_FORCE = 8; 
	//in ms. ignoring acc. events for this long (triggered by ex. spike feedback)
	public long IGNORE_TIME = 600; 
	public double ROT_ANGLE_SPEED_FACTOR = 20;
		
	public Listeners(Input input, My_Wiimote wiimote1, My_Wiimote wiimote2, Main main) {
		this.input = input;
		this.wiimote1 = wiimote1;
		this.wiimote2 = wiimote2;
		this.main = main;
	}
	
	//addWiiRotationListener variables

	
    void addWiiRotationListener(final My_Wiimote wiimote) {
    	
    	//TODO addWiiRotationListerner
    	
    	wiimote.addRotationListener(new RotationListener() {    		

			public void rotationReceived(RotationEvent event) {	
				if(Main.technique == 3 && wiimote.A_pressed && wiimote.hand == My_Wiimote.Hand.left_hand) {
					
					final double FILTER = 0.50; //manual filtering
					
					double 	dpitch 	= event.getPitch() - ppitch, 
							droll 	= event.getRoll()  - proll,
							dyaw 	= event.getYaw()   - pyaw;
					
					//filter small readings
					dpitch = Math.abs(dpitch) > FILTER ? dpitch : 0;
					droll  = Math.abs(droll)  > FILTER ? droll  : 0;
					dyaw   = Math.abs(dyaw)   > FILTER ? dyaw   : 0;
					
					if(	Math.abs(dpitch) > 0 || Math.abs(droll) > 0 || Math.abs(dyaw) > 0) {											

						if(input.getSelectedBox() != null) {
							src.helper.Log.addYawVelocity(Math.abs(dyaw));
							src.helper.Log.addRollVelocity(Math.abs(droll));
							src.helper.Log.addPitchVelocity(Math.abs(dpitch));
							//translate the delta angel to rotation speeed of a cube							
							try { //somehow the box can be deselected in midcode
								XBox box = input.getSelectedBox();
								int X, Y, pX = (int)box.x(), pY = (int)box.y();
								//compensating for the yawing angle being smaller for a human hand than pitching angle
								X = pX + (int)(dyaw * ROT_ANGLE_SPEED_FACTOR * 1.5);
								Y = pY + (int)(-dpitch * ROT_ANGLE_SPEED_FACTOR);
								input.getSelectedBox().freeRotate(X, Y, pX, pY);
							} catch(NullPointerException e) {}
						} //end if
					} //end if
				} //end if
				ppitch = event.getPitch();
				proll = event.getRoll();
				pyaw = event.getYaw();
			}

			public void rotationSpeedReceived(RotationSpeedEvent event) {}
    		
    	});
    }
    
	//addWiiInfraredListener variables
	public static boolean IR_enabled = true;	
	private int[] middle;
    private int[] pointer;
    private int[][] coordinates;    
    private int lastdeltaX;
    private int lastdeltaY;	    
    private int pX, pY;
	private double ppitch, proll, pyaw;
    private double p_dist;
    
    private long delay_mouse_move;

	public void addWiiInfraredListener(final My_Wiimote wiimote) {
        //TODO addWiiInfraredListener

        lastdeltaX = 0;
        lastdeltaY= 0;
        middle = new int[] { 0, 0 };
        pointer = new int[] { 0, 0 };        
        
		wiimote.addInfraredListener(new InfraredListener() {			

			public void infraredReceived(InfraredEvent event) {
	    		
				if(!IR_enabled || main.debugGui.getDebugGuiStatus())
	    			return;
	    		
				//only zooming and moving with the right hand (main hand)
				if(wiimote.hand != My_Wiimote.Hand.right_hand) {					
					return;
				}
            		
				
		        coordinates = event.getCoordinates();
		        int x1 = coordinates[0][0];
		        int y1 = coordinates[0][1];
		        int x2 = coordinates[1][0];
		        int y2 = coordinates[1][1];
		        		     
		        // calculate pointing direction
		        if(x1<1023 && x2<1023) {
		            // middle in view, used for pointer calculation
		            int dx = x2-x1;
		            int dy = y2-y1;
		            middle[0] = x1+(dx/2);
		            middle[1] = y1+(dy/2);
		            pointer[0] = 1024-middle[0];
		            pointer[1] = 768-middle[1];

		            lastdeltaX = dx;
		            lastdeltaY = dy;
		        } else if(x1<1023 && x2>=1023) {
		            // middle not in view, P1 in view
		            pointer[0] = 1024-x1-(int)(lastdeltaX*0.5);
		            pointer[1] = 768-y1-(int)(lastdeltaY*0.5);
		        } else if(x1>=1023 && x2<1023) {
		            // middle not in view, P2 in view
		            pointer[0] = 1024-x2+(int)(lastdeltaX*0.5);
		            pointer[1] = 768-y2+(int)(lastdeltaY*0.5);
		        }
		        try {
		        	int X,Y;
		            Dimension display = (Toolkit.getDefaultToolkit()).getScreenSize();
		            X = pointer[0]*display.width/1024;
		            Y = (768-pointer[1])*display.height/768;
		            new Robot().mouseMove(X, Y);
		            src.helper.Log.addMouseDistance((int)Math.sqrt(	
				            		Math.pow(main.mouseX - main.pmouseX, 2) + 
				            		Math.pow(main.mouseY - main.pmouseY, 2)));
		            if(Main.technique == 1 && input.getSelectedBox() != null && 
		            		wiimote.A_pressed && !wiimote.B_PRESSED) {		            	
		            	input.getSelectedBox().freeRotate(	main.mouseX, main.mouseY, 
		            										main.pmouseX, main.pmouseY);
		            }		            
		            else if((Main.technique > 0) && wiimote.B_PRESSED && 
		            		input.getSelectedBox() != null && !wiimote.A_pressed) {
		            	//move box - 
		            	if(System.currentTimeMillis() > delay_mouse_move)
		            		input.mouseDragged(0,0, main.mouseX, main.mouseY);
		            }
		            else if((Main.technique == 1 || Main.technique == 3) &&
		            		wiimote.B_PRESSED && wiimote.A_pressed && 
		            		input.getSelectedBox() != null) {		            	
		            	double dist = input.getDistanceToSensorbar(x1, y1, x2, y2);
		            	double D_dist = (dist - p_dist);
		            	//check it is not NaN or Infinity
		            	if(		D_dist != Double.NaN && D_dist != Double.NEGATIVE_INFINITY && 
		            			D_dist != Double.POSITIVE_INFINITY && Math.abs(D_dist) < 0.005) { 
		            		int Z = (int)(D_dist * 5000);
		            		Z = (int)(Math.signum(Z) * Math.max(Math.abs(Z), 5));		            		
		            		if(Z != 0) {
			            		input.getSelectedBox().wii_zoom(Z);
			            		src.helper.Log.addZoomEvent();
		            		}
		            	}
		            }
		            p_dist = input.getDistanceToSensorbar(x1, y1, x2, y2);
		        } catch (Exception ex) {
		            Log.write("Error while setting robot mouse coordinates:");
		            ex.printStackTrace();
		        }
		        
			}
			
		});
		
	}

	//addWiiAccelerationListener variables
	private double TRESHOLD = 0.0;	
	private final double GRAVITY = 1; //1G  
	public static boolean swipe_enabled = true;
	
	public void addWiiAccelerationListener(final My_Wiimote wiimote) {    		    	
		
		//TODO addWiiAccelerationListener
		wiimote.addAccelerationListener(new AccelerationListener() {
			
			public void accelerationReceived(AccelerationEvent event) {
				    			
				//ignore feedback spikes		    			
				if(wiimote.ignore > System.currentTimeMillis())
					return;

				if(wiimote.b1_pressed) {
					
					src.helper.Log.addAccelerationEvent();
					
					//Recognize swiping					
					if(Math.abs(event.getX()) > TRESHOLD) {												
						wiimote.X_sum += event.getX();
					}
					if(Math.abs(event.getY()) > TRESHOLD) {											
						wiimote.Y_sum += event.getY();
					}
					if(Math.abs(event.getZ()) > TRESHOLD) {						
						wiimote.Z_sum += event.getZ() - GRAVITY;
					}	
			
					if(wiimote.X_sum < -SWIPE_FORCE) {
						input.selectNearestBoxY(XBox.selected, 0); // right
						System.out.println("Swiping right, swipe force = " + wiimote.X_sum);
						wiimote.data_reset();
						//ignore next 500  msec of acc events
						wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME; 
					}
					else if(wiimote.X_sum > SWIPE_FORCE) {
						input.selectNearestBoxY(XBox.selected, 1); // left
						System.out.println("Swiping left, swipe force = " + wiimote.X_sum);
						wiimote.data_reset();
						//ignore next 500  msec of acc events
						wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME; 
					} else if (wiimote.Z_sum < -SWIPE_FORCE) {
						input.selectNearestBoxY(XBox.selected, 3); // down
						System.out.println("Swiping down, swipe force = " + wiimote.Y_sum);
						wiimote.data_reset();
						//ignore next 500  msec of acc events
						wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME; 
					} else if (wiimote.Z_sum > SWIPE_FORCE) {
						input.selectNearestBoxY(XBox.selected, 2); // up
						System.out.println("Swiping up, swipe force = " + wiimote.Y_sum);
						wiimote.data_reset();
						//ignore next 500  msec of acc events
						wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME; 
					}
					return;
				}				
				else if(Main.technique == 2) {	
					
//					accel_counter++;
					src.helper.Log.addAccelerationEvent();
					
    				final double FILTER = 0.0; //manual filtering
    				//factor that acceleration must deviate from primary direction to change
    				final double F = 2.0; 
    				
    				double accX, accY, accZ;
					
    				//note gravity is along the Y-axis
    				if(	wiimote1.B_PRESSED && wiimote2.B_PRESSED && 
    					input.getSelectedBox() != null && input.getSelectedBox().isPinned()) {    						
    					accX = Math.abs(event.getX()) < FILTER ? 0 : event.getX();
						accY = Math.abs(event.getY() + GRAVITY) < FILTER ? 0 : event.getY() + GRAVITY;
						accZ = Math.abs(event.getZ()) < FILTER ? 0 : event.getZ();
    				}
    				else
    					return;
    				
    				if(wiimote.hand == My_Wiimote.Hand.left_hand) {
    					accX = -accX;
    					accY = -accY;
    					accZ = -accZ;
    				}
					
					//detect spike feedback
					if(	(wiimote.prim_direction == My_Wiimote.Direction.X && 
							Math.signum(accX) != Math.signum(wiimote.p_accX)) ||
						(wiimote.prim_direction == My_Wiimote.Direction.Y && 
								Math.signum(accY) != Math.signum(wiimote.p_accY)) ||
						(wiimote.prim_direction == My_Wiimote.Direction.Z && 
								Math.signum(accZ) != Math.signum(wiimote.p_accZ))) {							
							wiimote.p_accX = accX = wiimote.p_accY = accY = wiimote.p_accZ = accZ= 0;
							wiimote.prim_direction = My_Wiimote.Direction.NONE;								
							//ignore next IGNORE_TIME  msec of acc events								
							wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME; 
							return;
					} 
					if(wiimote.prim_direction == My_Wiimote.Direction.NONE) {
						if(Math.abs(accX) > Math.max(Math.abs(accY), Math.abs(accZ))) {
							wiimote.prim_direction = My_Wiimote.Direction.X;
						}
						else if(Math.abs(accY) > Math.max(Math.abs(accX), Math.abs(accZ))) {
							wiimote.prim_direction = My_Wiimote.Direction.Y;
						}
						else if(Math.abs(accZ) > Math.max(Math.abs(accX), Math.abs(accY))) {
							wiimote.prim_direction = My_Wiimote.Direction.Z;
						}
					}
					else { //detect if primary direction is wrong							
						if(	(wiimote.prim_direction != My_Wiimote.Direction.X && 
								Math.abs(accX) > F * Math.max(Math.abs(accY), Math.abs(accZ))) ||
							(wiimote.prim_direction != My_Wiimote.Direction.Y && 
									Math.abs(accY) > F * Math.max(Math.abs(accX), Math.abs(accZ))) ||
							(wiimote.prim_direction != My_Wiimote.Direction.Z && 
									Math.abs(accZ) > F * Math.max(Math.abs(accX), Math.abs(accY)))) {
//								System.out.println("primary direction wrong: resetting data");
								wiimote.p_accX = accX = wiimote.p_accY = accY = wiimote.p_accZ = accZ= 0;
								wiimote.prim_direction = My_Wiimote.Direction.NONE;								
								//ignore next IGNORE_TIME  msec of acc events								
								wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME/2;
						}
					}
					
					XBox selected = input.getSelectedBox();
					if(wiimote.prim_direction == My_Wiimote.Direction.X) {
						int moveZ = (int) (Math.signum(accX) * Math.min(
								Math.abs((int)(accX * ACC_ZOOM_FACTOR)),
								20));    						
						selected.wii_zoom(moveZ);
					}
					else if(wiimote.prim_direction == My_Wiimote.Direction.Y) {
						int X = (int)(-accY * FREE_ROT_FACTOR);
						selected.freeRotate(main.mouseX, X, main.pmouseX, 0);						
					}
					else if(wiimote.prim_direction == My_Wiimote.Direction.Z) {
						int Y = (int)(-accZ * FREE_ROT_FACTOR);
						selected.freeRotate(Y, main.mouseY, 0, main.pmouseY);							
					}    					    								
					
					wiimote.p_accX = accX;
					wiimote.p_accY = accY;
					wiimote.p_accZ = accZ;						
				}
			}
			
			public void motionStartReceived(MotionStartEvent event) {
				//System.out.println("--MOTION START RECEIVED");    		
			}
			
			public void motionStopReceived(MotionStopEvent event) {
				//System.out.println("--MOTION STOP RECEIVED");				
				
				//do not prolong feedback spike ignore time
				if(wiimote.ignore > System.currentTimeMillis())
					return;
				if(Main.technique == 2 && wiimote.B_PRESSED && input.getSelectedBox() != null && input.getSelectedBox().isPinned()) {
//					System.out.println("--MOTION STOP RECEIVED");	
					wiimote.p_accX = wiimote.p_accY = wiimote.p_accZ = 0;
					wiimote.prim_direction = My_Wiimote.Direction.NONE;
					wiimote.data_reset();
					//ignore next IGNORE_TIME  msec of acc events								
					wiimote.ignore = System.currentTimeMillis() + IGNORE_TIME;     					
				}    				
			}
		});
	}
	
	/**
	 * this device listens to a wii button event.
	 */
	public void addWiiButtonListener(final My_Wiimote wiimote) {
		//TODO addWiiButtonListener
		
		wiimote.addButtonListener(new ButtonListener() {
			public void buttonPressReceived(ButtonPressedEvent evt) {
				int button = evt.getButton();
				src.helper.Log.addButtonClickEvent();
				
				if(button == Wiimote.BUTTON_HOME) {												
					src.helper.Log.stopTimer();					
					main.writelog();
					main.continueSequence();
				}

				if(button == Wiimote.BUTTON_1) {
					wiimote.b1_pressed = true;
				}
				if(Main.technique == 1 || Main.technique == 3) {
					if(button == Wiimote.BUTTON_A) {
						wiimote.A_pressed = true;
					}
    				else if(button == Wiimote.BUTTON_B) {
    					//no selection with offhand
    					if(Main.technique == 3 && wiimote.hand == My_Wiimote.Hand.left_hand)
    						return;
    					wiimote.B_PRESSED = true;
    					if(input.getSelectedBox() != null && input.getSelectedBox().isPinned()) {
    						//do nothing
    					}    						  					
    					else {
    						delay_mouse_move = System.currentTimeMillis() + 500;    						
    						input.mousePressed(main.mouseX, main.mouseY);
    					}
    				} 					
				}
				if(Main.technique == 2) {
    				if(button == Wiimote.BUTTON_A) {
    					if(IR_enabled)
    						input.mousePressed(main.mouseX, main.mouseY);
    					//pin/unpin cube
    					if(input.getSelectedBox() != null) {
    						if(input.getSelectedBox().isPinned()) {
        						input.getSelectedBox().unpin();  
        						input.mousePressed(-1, -1); 						
            					IR_enabled = true;           					 						
    						}
    						else {
	    						input.getSelectedBox().pin();
	    						IR_enabled = false;
	    					}			    					
    					}    					
    				}
    				else if(button == Wiimote.BUTTON_B) {
    					wiimote.B_PRESSED = true;  
    					if(input.getSelectedBox() != null && input.getSelectedBox().isPinned()) {
    						//do nothing
    					}    						  					
    					else {
    						delay_mouse_move = System.currentTimeMillis() + 500;    	
    						input.mousePressed(main.mouseX, main.mouseY);
    					}
    				} 
    				else if(button == Wiimote.BUTTON_UP) {
    					if(input.getSelectedBox() != null) {
    						wiimote.up_pressed = true;
    						XBox box = input.getSelectedBox(); 
    						new ButtonHold(box, wiimote).start();    						
    					}
    				}
    				else if(button == Wiimote.BUTTON_DOWN) {
    					if(input.getSelectedBox() != null) {
    						wiimote.down_pressed = true;
    						XBox box = input.getSelectedBox(); 
    						new ButtonHold(box, wiimote).start();    						
    					}
    				}   				
				}
			}

			public void buttonReleaseReceived(ButtonReleasedEvent evt) {
				//System.out.println("buttonReceived: "+evt.getButton());
				int button = evt.getButton();				
				
				if(button == Wiimote.BUTTON_1) {
					wiimote.b1_pressed = false;
				}
								
				if(Main.technique == 1 || Main.technique == 3) {
					if(button == Wiimote.BUTTON_A) {
						wiimote.A_pressed = false;
					}
    				if(button == Wiimote.BUTTON_B) {
    					wiimote.B_PRESSED = false;
    				}				
				}
				if(Main.technique == 2) {
    				if(button == Wiimote.BUTTON_B) {
    					wiimote.B_PRESSED = false;
    				}    				
    				else if(button == Wiimote.BUTTON_A) { 
    					//do nothing
    				}
    				else if(button == Wiimote.BUTTON_UP) { 
    					wiimote.up_pressed = false;
    					
    				}
    				else if(button == Wiimote.BUTTON_DOWN) { 
    					wiimote.down_pressed = false;
    				}
				}
		}});
	} 
		
    private class ButtonHold extends Thread {
        
    	My_Wiimote wiimote;
    	XBox box;
    	final int ROT_X_OFFSET = 30;
    	
    	ButtonHold(XBox box, My_Wiimote wiimote) {
    		this.box = box;
    		this.wiimote = wiimote;
        }

        public void run() { 
        	while(wiimote.up_pressed) {
        		int X = (int)box.x(), Y = (int)box.y();
        		box.freeRotate(X, Y - ROT_X_OFFSET, X, Y);
        		try {
					Thread.sleep(button_hold_sleep_time);
				} catch (InterruptedException e) {}
        	}
        	while(wiimote.down_pressed ) {
        		int X = (int)box.x(), Y = (int)box.y();
        		box.freeRotate(X, Y + ROT_X_OFFSET, X, Y);
        		try {
					Thread.sleep(button_hold_sleep_time);
				} catch (InterruptedException e) {}        		
        	}
        }
    }
}
