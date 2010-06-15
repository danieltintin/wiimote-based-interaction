package src.core;

import java.util.ArrayList;
import java.util.Arrays;
import processing.core.PApplet;
import shapes3d.Shape3D;
import src.helper.Log;
import src.visualize.Canvas;
import src.visualize.DebugGui;
import src.visualize.Gui;
import src.visualize.MediaGenerator;

public class Input extends PApplet {

	private PApplet papplet;
	private MediaGenerator mediaGenerator;
	private Canvas canvas;
	private Gui gui;
	private DebugGui debugGui;
	private Log log;
	private Main main;
	public ArrayList<XBox> boxes;
	private XBox selectedBox;
	
	private final int ZOOM_SPEED = 10;
	private final int TINT_SPEED = 20;
	private int tintSelector = 0;
	private int x, y;
	private boolean switchMode;

	public Input(PApplet _papplet, MediaGenerator _mediaGenerator, Canvas canvas, Gui gui, 
					DebugGui debugGui, Log log, Main main) {
		papplet = _papplet;
		mediaGenerator = _mediaGenerator;
		selectedBox = null;
		this.boxes = mediaGenerator.boxes;
		switchMode = true;
		this.canvas = canvas;
		this.gui = gui;
		this.debugGui = debugGui;
		this.log = log;
		this.main = main;
		this.x = gui.getX();
		this.y = gui.getY();
	}
	
	public XBox getSelectedBox() {
		return this.selectedBox;
	}
	
	public void setSelectedBox(XBox _selectedBox) {
		selectedBox = _selectedBox;
		XBox.selected = _selectedBox;
	}
		
	public void keyPressed(int key, int keyCode) {
		
		if(selectedBox != null && !gui.getGuiStatus() && !debugGui.getDebugGuiStatus()) {

			if(key == '0')
				main.reInitializeDualWiimotes();
			if(key == InputConstants.ROTATE_LEFT) {
				selectedBox.rotateZ(XBox.CLOCKWISE);
			}
			if (key == InputConstants.ROTATE_RIGHT) {
				selectedBox.rotateZ(XBox.COUNTER_CLOCKWISE);
			}
			if (keyCode == UP) {
				selectedBox.rotateX(XBox.CLOCKWISE);
			}
			if (keyCode == DOWN) {
				selectedBox.rotateX(XBox.COUNTER_CLOCKWISE);
			}
			if (keyCode == RIGHT) {
				selectedBox.rotateY(XBox.CLOCKWISE);
			}
			if (keyCode == LEFT) {
				selectedBox.rotateY(XBox.COUNTER_CLOCKWISE);
			} 
			if (key == InputConstants.ZOOM_IN) {
				if (!(selectedBox.z() > 150)) 
					selectedBox.wii_zoom(ZOOM_SPEED);
			}
			if (key == InputConstants.ZOOM_OUT) {
				if (!(selectedBox.z() < Canvas.depth + selectedBox.getDepth()))
					selectedBox.wii_zoom(-ZOOM_SPEED);
			}
			if (key == InputConstants.SWIPE_RIGHT) {
				selectNearestBoxY((XBox) selectedBox, 0);
			}
			if (key == InputConstants.SWIPE_LEFT) {
				selectNearestBoxY((XBox) selectedBox, 1);
			}
			if (key == InputConstants.SWIPE_UP) {
				selectNearestBoxY((XBox) selectedBox, 2);
			}
			if (key == InputConstants.SWIPE_DOWN) {
				selectNearestBoxY((XBox) selectedBox, 3);
			}
		}
		if (key == InputConstants.SWITCH_MODE) {
			switchMode = !switchMode;
		}
		if (key == InputConstants.INTERACTION_1) {
			if(!gui.getGuiStatus()){
				keyPressed(InputConstants.MAINMENU_VISIBILITY, 0);
			} else {
				if(debugGui.getDebugGuiStatus()) {
					debugGui.updateDebugGuiStatus();
				}
				gui.captureScreen();
				gui.updateGuiStatus();
				changeInteractionMode(1);
			}
		}
		if (key == InputConstants.INTERACTION_2) {
			if(!gui.getGuiStatus()){
				keyPressed(InputConstants.MAINMENU_VISIBILITY, 0);
			} else {
				if(debugGui.getDebugGuiStatus()) {
					debugGui.updateDebugGuiStatus();
				}
				gui.captureScreen();
				gui.updateGuiStatus();
				changeInteractionMode(2);
			}
		}
		if (key == InputConstants.INTERACTION_3) {
			if(!gui.getGuiStatus()){
				keyPressed(InputConstants.MAINMENU_VISIBILITY, 0);
			} else {
				if(debugGui.getDebugGuiStatus()) {
					debugGui.updateDebugGuiStatus();
				}
				gui.captureScreen();
				gui.updateGuiStatus();
				changeInteractionMode(3);
			}
		}
		if (key == InputConstants.MOUSE_INTERACTION) { // reset to the mouse
			changeInteractionMode(0);
		}
		if (key == InputConstants.MAINMENU_VISIBILITY) { // gui visible
			if(debugGui.getDebugGuiStatus()) {
				debugGui.updateDebugGuiStatus();
			}
			gui.captureScreen();
			gui.updateGuiStatus();
		}
		if (key == InputConstants.DEBUG_VISIBILITY) {
			if(gui.getGuiStatus()) {
				gui.updateGuiStatus();
			}
			debugGui.captureScreen();
			debugGui.updateDebugGuiStatus();
		}
		if (key == InputConstants.CANVAS_WALL_SELECT) { // select the wall for the tint()
			tintSelector++;
		}
		if (key == InputConstants.TINT_INCREASE) { // increase tint
			int[] tempTint = canvas.getTint();
			switch (tintSelector%5) {
				case 0: tempTint[0] += TINT_SPEED; break;
				case 1: tempTint[1] += TINT_SPEED; break;
				case 2: tempTint[2] += TINT_SPEED; break;
				case 3: tempTint[3] += TINT_SPEED; break;
				case 4: tempTint[4] += TINT_SPEED; break;
			}
			for (int i = 0; i < tempTint.length; i++) {
				if (tempTint[i] < 0) tempTint[i] = 0;
				if (tempTint[i] > 255) tempTint[i] = 255;
			}
			canvas.setTint(tempTint);
		}
		if (key == InputConstants.TINT_DECREASE) { // decrease tint
			int[] tempTint = canvas.getTint();
			switch (tintSelector%5) {
				case 0: tempTint[0] -= TINT_SPEED; break;
				case 1: tempTint[1] -= TINT_SPEED; break;
				case 2: tempTint[2] -= TINT_SPEED; break;
				case 3: tempTint[3] -= TINT_SPEED; break;
				case 4: tempTint[4] -= TINT_SPEED; break;
			}
			for (int i = 0; i < tempTint.length; i++) {
				if (tempTint[i] < 0) tempTint[i] = 0;
				if (tempTint[i] > 255) tempTint[i] = 255;
			}
			canvas.setTint(tempTint);
		}
		if (key == InputConstants.TINT_DEFAULT) { // reset tint to default
			int[] tempTint = new int[] {10, 40, 255, 255, 255};
			canvas.setTint(tempTint);
		}
		
		
		
		if (key == InputConstants.MODE_NUMBERS) {
			
			if(!gui.getGuiStatus()){
				keyPressed(InputConstants.MAINMENU_VISIBILITY, 0);
			} else {
				if(debugGui.getDebugGuiStatus()) {
					debugGui.updateDebugGuiStatus();
				}
				gui.captureScreen();
				gui.updateGuiStatus();
				mediaGenerator.reset(MediaGenerator.NUMBERS, -1);
			}
		}
		if (key == InputConstants.MODE_COLORS) {
			if(!gui.getGuiStatus()){
				keyPressed(InputConstants.MAINMENU_VISIBILITY, 0);
			} else {
				if(debugGui.getDebugGuiStatus()) {
					debugGui.updateDebugGuiStatus();
				}
				gui.captureScreen();
				gui.updateGuiStatus();
				mediaGenerator.reset(MediaGenerator.COLORS, -1);
			}
		}
		if (key == InputConstants.MODE_PHOTOS) {
			if(!gui.getGuiStatus()){
				keyPressed(InputConstants.MAINMENU_VISIBILITY, 0);
			} else {
				if(debugGui.getDebugGuiStatus()) {
					debugGui.updateDebugGuiStatus();
				}
				gui.captureScreen();
				gui.updateGuiStatus();
				mediaGenerator.reset(MediaGenerator.PHOTOS, -1);
			}
		}
		// print rotation and translation of every single box on the screen.
		if (key == InputConstants.LOG_PRINTBOXDATA) { 
			Log.printAllBoxesData(boxes, Log.TO_FILE);
		}
		if (key == InputConstants.LOG_READBOXDATA) {
//			ConfigFileHandler.debugPrint();
		}
	}
	
	/**
	 * Selects the nearest Box on the Y-axis.
	 * @param selectedBox
	 */
	public void selectNearestBoxY(XBox box, int direction) {
		if(!gui.getGuiStatus() && !debugGui.getDebugGuiStatus()) {
			
			if(box == null)
				return;
			
			if(direction == 0) { // right
				XBox.swipe_axis = XBox.SWIPE_HORIZONTAL;
				XBox.swipe_direction = XBox.SWIPE_POSITIVE;
			}
			else if(direction == 1) { // left
				XBox.swipe_axis = XBox.SWIPE_HORIZONTAL;
				XBox.swipe_direction = XBox.SWIPE_NEGATIVE;
			}
			else if(direction == 2) { // down
				XBox.swipe_axis = XBox.SWIPE_VERTICAL;
				XBox.swipe_direction = XBox.SWIPE_POSITIVE;
			}
			else { // up
				XBox.swipe_axis = XBox.SWIPE_VERTICAL;			
				XBox.swipe_direction = XBox.SWIPE_NEGATIVE;
			}
			
			XBox.selected = box;
			showSelection(false);
			XBox[] _boxes = boxes.toArray(new XBox[boxes.size()]);
			Arrays.sort(_boxes);
			
			int i = 0;
			for(XBox xb : _boxes) {
				i++;
				if(xb.equals(box))
					break;
			}
			if(i < _boxes.length)
				setSelectedBox((XBox) _boxes[i]);
			else
				setSelectedBox(box);
			showSelection(true);
		}
	}
	
	
	
	public void mousePressed(int mouseX, int mouseY) {				
		
		if(!gui.getGuiStatus() && !debugGui.getDebugGuiStatus()) {
			
			Shape3D picked;
			try {
				picked = Shape3D.pickShapeB(papplet, mouseX, mouseY);
			} catch(RuntimeException e) {
					System.out.println(e);
					System.out.println("mouseX = " + mouseX + ", mouseY = " + mouseY);
					return;
				}
			
			showSelection(false);
			
			XBox _picked = null;
			for(XBox xbox : boxes) {
				if(XBox.equals((XBox) picked, xbox))
					_picked = xbox;
			}
			setSelectedBox(_picked);
			
			if(_picked != null) {
				showSelection(true);
			}	
			
		} else { // currently in main menu, selection between modes
			
			if(!debugGui.getDebugGuiStatus()) {
				
				if((mouseX >= x) && (mouseX <= x+papplet.width/7) && 
						   (mouseY >= y) && (mouseY <= y+papplet.width/7)) {
							println("switching mode: NUMBERS");
							gui.updateGuiStatus();
							mediaGenerator.reset(MediaGenerator.NUMBERS, -1);
							
						} 
						else if((mouseX >= x+papplet.width/7+20) && 
								(mouseX <= x+papplet.width/7+20+papplet.width/7) && 
								(mouseY >= y) && (mouseY <= y+papplet.width/7)) {
							println("switching mode: COLORS");
							gui.updateGuiStatus();
							mediaGenerator.reset(MediaGenerator.COLORS, -1);
							
						} 
						else if((mouseX >= x+papplet.width/7+20+papplet.width/7+20) && 
								(mouseX <= x+papplet.width/7+20+papplet.width/7+20+papplet.width/7) && 
								(mouseY >= y) && (mouseY <= y+papplet.width/7)) {
							println("switching mode: PHOTOS");
							gui.updateGuiStatus();
							mediaGenerator.reset(MediaGenerator.PHOTOS, -1);
							
						} 
						else if((mouseX >= x) && (mouseX <= x+papplet.width/7) &&
								(mouseY >= y+20+papplet.width/7) && 
								(mouseY < y+20+papplet.width/7+papplet.width/7)) {
							// case A
							gui.updateGuiStatus();
							changeInteractionMode(1);
							println("A");
						}
						else if((mouseX >= x+papplet.width/7+20) && 
								(mouseX <= x+papplet.width/7+20+papplet.width/7) && 
								(mouseY >= y+20+papplet.width/7) && 
								(mouseY < y+20+papplet.width/7+papplet.width/7)) {
							// case B
							gui.updateGuiStatus();
							changeInteractionMode(2);
							println("B");
						}
						else if((mouseX >= x+papplet.width/7+20+papplet.width/7+20) && 
								(mouseX <= x+papplet.width/7+20+papplet.width/7+20+papplet.width/7) &&
								(mouseY >= y+20+papplet.width/7) && 
								(mouseY < y+20+papplet.width/7+papplet.width/7)) {
							// case C
							gui.updateGuiStatus();
							changeInteractionMode(2);
							println("C");
						}
						else {
							println("no mode found");
						}
				
			}
			
		}
	}
	
	public void draw() {
		papplet.pushMatrix();
		papplet.translate(0, 0, 100); // to have the same values as in input
		papplet.fill(255,0,0);
		println("DRAWN");
		// rect #1
		papplet.rect(x, y, papplet.width/7, papplet.width/7);
		// rect #2
		papplet.rect(x+papplet.width/7+20, y, papplet.width/7, papplet.width/7);
		papplet.noFill();
		papplet.popMatrix();
	}
	
	/**
	 * this method also takes care of the the bounding area
	 * @param x
	 * @param y
	 * @param mouseX
	 * @param mouseY
	 */
	public void mouseDragged(int x, int y, int mouseX, int mouseY) {
		if (selectedBox != null && !gui.getGuiStatus() && !debugGui.getDebugGuiStatus()) { 
			
			selectedBox.moveTo(mouseX, mouseY, (int) selectedBox.z());			
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY) {	
		if(!gui.getGuiStatus() && !debugGui.getDebugGuiStatus()) {
			Shape3D picked = Shape3D.pickShapeB(papplet, mouseX, mouseY);

			XBox _picked = null;
			for(XBox xbox : boxes) {
				if(XBox.equals((XBox) picked, xbox))
					_picked = xbox;
			}
			
			if(_picked == null) {
				showSelection(false);
				setSelectedBox(null);
			}
		}
	}
	
	public void showSelection(boolean update) {
		if (MediaGenerator.activeTextureMode() != MediaGenerator.PHOTOS) {
			if(update) {
				for(int i = 0; i < 6; i++) {
					selectedBox.setTexture(mediaGenerator.getTextures()[i+6], 
											(int) Math.pow(2, i));
				}
				
			} else {
				if(selectedBox != null) {
					for(int i = 0; i < 6; i++) {
						selectedBox.setTexture(mediaGenerator.getTextures()[i], 
												(int) Math.pow(2, i));
					}
				}
			}
		} else { // if activeTextureMode == PHOTOS
			if(update) { // selected textures
				int faceCounter = 0;
				for(int j=selectedBox.number-1; j<48; j=j+8) {
					selectedBox.setTexture(mediaGenerator.getTextures()[j+48], 
											(int) Math.pow(2, faceCounter));
					faceCounter++;
				}
			} else {
				if(selectedBox != null) { // deselected textures
					int faceCounter = 0;
					for(int j=selectedBox.number-1; j<48; j=j+8) { 
						selectedBox.setTexture(mediaGenerator.getTextures()[j], 
												(int) Math.pow(2, faceCounter));
						faceCounter++;
					}
				}
			}
		}
	}
	
	public void mouseRotation(int mouseX, int mouseY, int pMouseX, int pMouseY) {
		if(selectedBox != null) {
			selectedBox.freeRotate(mouseX, mouseY, pMouseX, pMouseY);
		}
	}
	
	public boolean getSwitchMode() {
		return this.switchMode;
	}
	
	public void updateBoxes(ArrayList<XBox> boxes) {
		this.boxes = boxes;
	}
	
	public void changeInteractionMode(int mode) {
		Main.technique = mode;
		main.enable_IR_camera = true;
		main.enable_wii_motion_plus = false;
		switch(mode) {
		case 0:
			main.enable_IR_camera = main.enable_wii_motion_plus = false;
			return;
		case 1:								
			break;
		case 2:		
			break;
		case 3:
			main.enable_wii_motion_plus = true;			
			break;
		default:
			System.out.println("unknown mode: " + mode);
			return;
		}		
		main.setupWiimote(main.wiimote1);
		main.setupWiimote(main.wiimote2);		
	}	
	
    private final double DISTANCE_BETWEEN_SPOTS = 0.21; // meter   
    public double getDistanceToSensorbar(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2-x1);
        int dy = Math.abs(y2-y1);
        double pixDist = Math.sqrt(dx*dx+dy*dy);

        double angle = 41.0*pixDist/1024;

        double realDist = (DISTANCE_BETWEEN_SPOTS/2)/(Math.tan(Math.toRadians(angle/2)));

        return realDist;
    }
}