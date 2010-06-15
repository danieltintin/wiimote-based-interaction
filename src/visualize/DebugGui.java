package src.visualize;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import controlP5.*;

public class DebugGui extends PApplet {
	
	private ControlP5 dgui;
	private PApplet papplet;
	private boolean debugGuiActivated = false;
	private PImage[] background;
	private PImage current;
	private PFont font;
	private PImage[] labels;
	
	private static final int UI_X = 20;
    private static final int UI_Y = 175;
    
    private float maxScrollSpeed = 0.005f;
    
    private boolean isDebug = false;
    private boolean doUpdate = false;
    
    private Radio r0, r1, r2;
    private CheckBox rX;
    
    private int numberConfigs = 5;
    
    private String noLabel = "";
	
	public DebugGui(PApplet papplet){
		this.papplet = papplet;
		initializeComponents();
		font = papplet.loadFont(sketchPath("src/files/fonts/odzroman-64.vlw"));
		loadImages();
	}
	
	private void initializeComponents(){
		
		String CREDITS = "credits";
		
		dgui = new ControlP5(this.papplet);
		
		dgui.setColorActive(0x99ffffff);
		dgui.setColorBackground(0x20ffffff);
		dgui.setColorForeground(0xee666666);
		dgui.setColorLabel(0xffffffff);
		dgui.setColorValue(0xffffffff);
		dgui.setAutoInitialization(false);
		dgui.getTab("default").setLabel(noLabel);
		//dgui.addTab(CREDITS);
		
		r0 = dgui.addRadio("techniqueSelector", UI_X, UI_Y);
		for (int i = 1; i <= 3; i++) {
            r0.addItem(noLabel+" ",i);
        }
		
        r1 = dgui.addRadio("configuration", UI_X, UI_Y + 50);
        for (int i = 1; i <= numberConfigs; i++) {
            r1.addItem(noLabel+"  ", i);
        }
		
		r2 = dgui.addRadio("modeID", UI_X, UI_Y + 130);
		r2.addItem(noLabel+"   ", 1);
		r2.addItem(noLabel+"    ", 2);
		r2.addItem(noLabel+"     ", 3);
		
		rX = dgui.addCheckBox("loggingMode", UI_X, UI_Y + 180);
		rX.addItem(noLabel, 1);
		
		// bang buttons
		dgui.addBang(noLabel+"          ", UI_X, UI_Y + 200, 90, 20).setId(1);
//        dgui.addBang(noLabel+"           ", UI_X, UI_Y + 250, 90, 20).setId(2);
        dgui.addBang(noLabel+"            ", UI_X, UI_Y + 300, 90, 90).setId(3);
	}
	
	public void draw(){

		if(getDebugGuiStatus()) {
			papplet.pushMatrix();
			
			//papplet.textFont(font, 18);
			//papplet.textSize(0);
			showBackground();
			
			//papplet.textFont(font, 10);
			papplet.fill(0, 0, 0);
	        
	        //papplet.textMode(SCREEN);
			//papplet.text(s, (papplet.width-683)/2, 100, papplet.width, papplet.height);
			
			papplet.fill(255, 255, 255);
			
			
			// who gui elements
			papplet.fill(255, 255, 255);

			// technique 1
			papplet.beginShape();
			papplet.texture(labels[5]);
			papplet.vertex(UI_X+20, UI_Y, 0, 0);
			papplet.vertex(UI_X+110, UI_Y, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+12, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+12, 0, 100);
			papplet.endShape();
			
			// technique 2
			papplet.beginShape();
			papplet.texture(labels[6]);
			papplet.vertex(UI_X+20, UI_Y+14, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+14, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+26, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+26, 0, 100);
			papplet.endShape();
			
			// technique 3
			papplet.beginShape();
			papplet.texture(labels[7]);
			papplet.vertex(UI_X+20, UI_Y+28, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+28, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+40, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+40, 0, 100);
			papplet.endShape();
			
			// configuration 1
			papplet.beginShape();
			papplet.texture(labels[0]);
			papplet.vertex(UI_X+20, UI_Y+50, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+50, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+62, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+62, 0, 100);
			papplet.endShape();
			
			// configuration 2
			papplet.beginShape();
			papplet.texture(labels[1]);
			papplet.vertex(UI_X+20, UI_Y+64, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+64, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+76, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+76, 0, 100);
			papplet.endShape();
			
			// configuration 3
			papplet.beginShape();
			papplet.texture(labels[2]);
			papplet.vertex(UI_X+20, UI_Y+78, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+78, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+90, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+90, 0, 100);
			papplet.endShape();
			
			// configuration 4
			papplet.beginShape();
			papplet.texture(labels[3]);
			papplet.vertex(UI_X+20, UI_Y+92, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+92, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+104, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+104, 0, 100);
			papplet.endShape();
			
			// configuration 5
			papplet.beginShape();
			papplet.texture(labels[4]);
			papplet.vertex(UI_X+20, UI_Y+106, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+106, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+118, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+118, 0, 100);
			papplet.endShape();
			
			// numbers
			papplet.beginShape();
			papplet.texture(labels[12]);
			papplet.vertex(UI_X+20, UI_Y+130, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+130, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+142, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+142, 0, 100);
			papplet.endShape();
			
			// colors
			papplet.beginShape();
			papplet.texture(labels[13]);
			papplet.vertex(UI_X+20, UI_Y+144, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+144, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+156, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+156, 0, 100);
			papplet.endShape();
			
			// photos
			papplet.beginShape();
			papplet.texture(labels[14]);
			papplet.vertex(UI_X+20, UI_Y+158, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+158, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+170, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+170, 0, 100);
			papplet.endShape();
			
			// logging
			papplet.beginShape();
			papplet.texture(labels[8]);
			papplet.vertex(UI_X+20, UI_Y+180, 0, 0);
			papplet.vertex(UI_X+110, UI_Y+180, 100, 0);
			papplet.vertex(UI_X+110, UI_Y+192, 100, 100);
			papplet.vertex(UI_X+20, UI_Y+192, 0, 100);
			papplet.endShape();
			
			// re-initialize wiimotes
			papplet.beginShape();
			papplet.texture(labels[9]);
			papplet.vertex(UI_X, UI_Y+222, 0, 0);
			papplet.vertex(UI_X+136, UI_Y+222, 100, 0);
			papplet.vertex(UI_X+136, UI_Y+234, 100, 100);
			papplet.vertex(UI_X, UI_Y+234, 0, 100);
			papplet.endShape();
			
//			// show default
//			papplet.beginShape();
//			papplet.texture(labels[10]);
//			papplet.vertex(UI_X, UI_Y+272, 0, 0);
//			papplet.vertex(UI_X+136, UI_Y+272, 100, 0);
//			papplet.vertex(UI_X+136, UI_Y+284, 100, 100);
//			papplet.vertex(UI_X, UI_Y+284, 0, 100);
//			papplet.endShape();
			
			// start task
			papplet.beginShape();
			papplet.texture(labels[11]);
			papplet.vertex(UI_X, UI_Y+392, 0, 0);
			papplet.vertex(UI_X+90, UI_Y+392, 100, 0);
			papplet.vertex(UI_X+90, UI_Y+436, 100, 100);
			papplet.vertex(UI_X, UI_Y+436, 0, 100);
			papplet.endShape();
			
			
			
			// logo
			/*
			papplet.beginShape();
			papplet.texture(labels[15]);
			papplet.vertex(papplet.width-(500+UI_X), UI_Y, 0, 0);
			papplet.vertex(papplet.width-UI_X, UI_Y, 100, 0);
			papplet.vertex(papplet.width-UI_X, UI_Y+50, 100, 100);
			papplet.vertex(papplet.width-(500+UI_X), UI_Y+50, 0, 100);
			papplet.endShape();
			*/
			
	        papplet.textFont(font);
			papplet.textSize(160);
			papplet.fill(255);
	        String s = "SETTINGS";
	        papplet.textMode(SCREEN);
	        
	        papplet.text(s, (papplet.width-683)/2, 100, papplet.width, papplet.height);
	        
			dgui.draw();
			papplet.popMatrix();
			papplet.fill(255, 255, 255);
		}
	}
	
	public void updateDebugGuiStatus(){
		captureScreen();
		debugGuiActivated = !debugGuiActivated;
	}
	
	public boolean getDebugGuiStatus() {
		return this.debugGuiActivated;
	}
	
	public void showBackground() {
		
		//papplet.translate(0, 0, 100);
		
		papplet.beginShape();
		papplet.texture(current);
		papplet.vertex(0, 0, 0, 0);
		papplet.vertex(papplet.width, 0, 100, 0);
		papplet.vertex(papplet.width, papplet.height, 100, 100);
		papplet.vertex(0, papplet.height, 0, 100);
		papplet.endShape();
	}
	
	public void captureScreen() {
		papplet.save("src/files/current.png");
		current = loadImage("src/files/current.png");
		//println("pre-'debug menu': current status saved.");
	}
	
	public int getTechniqueValue() {
		return (int) r0.value();
	}
	
	public int getConfigurationValue() {
		return (int) r1.value();
	}
	
	public int getModeValue() {
		return (int) r2.value();
	}
	
	public String getLoggingValue(){
		if(rX.getItem(0).value() == 1.0) {
			return "on";
		} else {
			return "off";
		}
	}
	
	private void loadImages() {
		labels = new PImage[16];
		labels[0] = loadImage("src/files/configuration1.png");
		labels[1] = loadImage("src/files/configuration2.png");
		labels[2] = loadImage("src/files/configuration3.png");
		labels[3] = loadImage("src/files/configuration4.png");
		labels[4] = loadImage("src/files/configuration5.png");
		labels[5] = loadImage("src/files/technique1.png");
		labels[6] = loadImage("src/files/technique2.png");
		labels[7] = loadImage("src/files/technique3.png");
		labels[8] = loadImage("src/files/logging.png");
		labels[9] = loadImage("src/files/re-initializewiimotes.png");
		labels[10] = loadImage("src/files/show-default.png");
		labels[11] = loadImage("src/files/start-task.png");
		labels[12] = loadImage("src/files/numbers.png");
		labels[13] = loadImage("src/files/colors.png");
		labels[14] = loadImage("src/files/photos.png");
		labels[15] = loadImage("src/files/settings.png");
	}
}

