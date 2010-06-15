package src.visualize;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Gui extends PApplet {
	
	private PApplet papplet;
	private boolean GuiActivated = false;
	private PFont font;
	private int x, y;
	private PImage[] buttons;
	private PImage current;
	
	public Gui(PApplet papplet){
		this.papplet = papplet;
		font = papplet.loadFont(sketchPath("src/files/fonts/odzroman-64.vlw"));
		this.x = (int)((papplet.width-((3*(papplet.width/7))+40))/2);
		this.y = (papplet.height/2)-(papplet.width/7)+10; 
		loadImages();
		
		
	}
	
	private void loadImages() {
		buttons = new PImage[6];
		buttons[0] = loadImage("src/files/1.jpg");
		buttons[1] = loadImage("src/files/2.jpg");
		buttons[2] = loadImage("src/files/3.jpg");
		buttons[3] = loadImage("src/files/a.jpg");
		buttons[4] = loadImage("src/files/b.jpg");
		buttons[5] = loadImage("src/files/c.jpg");
	}
	
	public int getX() { return this.x; }
	public int getY() { return this.y; }
	
	public void draw() {
		if(GuiActivated) {
			
			papplet.pushMatrix();
			//papplet.translate(0, 0, 100);
			//papplet.fill(255, 255, 255, 200);
			//papplet.rect(0,0,papplet.width,papplet.height);
			papplet.textFont(font);
			papplet.textSize(160);
			papplet.fill(0, 0, 0);
	        String s = "MAIN MENU";
	        papplet.textMode(SCREEN);
	        
	        showBackground();
			papplet.text(s, (papplet.width-683)/2, 100, papplet.width, papplet.height);
			
			papplet.fill(255, 255, 255);
			
			
			
			// numbers
			papplet.beginShape();
			papplet.texture(buttons[0]);
			papplet.vertex(x, y, 0, 0);
			papplet.vertex(x+papplet.width/7, y, 100, 0);
			papplet.vertex(x+papplet.width/7, y+papplet.width/7, 100, 100);
			papplet.vertex(x, y+papplet.width/7, 0, 100);
			papplet.endShape();
			
			// color
			papplet.beginShape();
			papplet.texture(buttons[1]);
			papplet.vertex(x+papplet.width/7+20, y, 0, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7, y, 100, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7, y+papplet.width/7, 100, 100);
			papplet.vertex(x+papplet.width/7+20, y+papplet.width/7, 0, 100);
			papplet.endShape();
			
			// photo
			papplet.beginShape();
			papplet.texture(buttons[2]);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20, y, 0, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20+papplet.width/7, y, 100, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20+papplet.width/7, y+papplet.width/7, 
							100, 100);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20, y+papplet.width/7, 0, 100);
			papplet.endShape();
			
			// wii-technique 1
			papplet.beginShape();
			papplet.texture(buttons[3]);
			papplet.vertex(x, y+20+papplet.width/7, 0, 0);
			papplet.vertex(x+papplet.width/7, y+20+papplet.width/7, 100, 0);
			papplet.vertex(x+papplet.width/7, y+20+papplet.width/7+papplet.width/7, 100, 100);
			papplet.vertex(x, y+papplet.width/7+20+papplet.width/7, 0, 100);
			papplet.endShape();
			
			// wii-technique 2
			papplet.beginShape();
			papplet.texture(buttons[4]);
			papplet.vertex(x+papplet.width/7+20, y+20+papplet.width/7, 0, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7, y+20+papplet.width/7, 100, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7, y+20+papplet.width/7+papplet.width/7, 
							100, 100);
			papplet.vertex(x+papplet.width/7+20, y+20+papplet.width/7+papplet.width/7, 0, 100);
			papplet.endShape();
			
			// wii-technique 3
			papplet.beginShape();
			papplet.texture(buttons[5]);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20, y+20+papplet.width/7, 0, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20+papplet.width/7, y+20+papplet.width/7, 
							100, 0);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20+papplet.width/7, y+20+papplet.width/7+
							papplet.width/7, 100, 100);
			papplet.vertex(x+papplet.width/7+20+papplet.width/7+20, y+20+papplet.width/7+papplet.width/7, 
							0, 100);
			papplet.endShape();
			
			papplet.fill(255, 255, 255, 200);
			papplet.popMatrix();
		}
	}
	
	public boolean getGuiStatus(){
		return this.GuiActivated;
	}
	
	public void updateGuiStatus(){
		GuiActivated = !GuiActivated;
	}
	
	public void captureScreen() {
		papplet.save("src/files/current.png");
		this.current = loadImage("src/files/current.png");
		//println("pre-'main menu': current status saved.");
	}
	
	public void showBackground() {
		
		papplet.beginShape();
		papplet.texture(current);
		papplet.vertex(0, 0, 0, 0);
		papplet.vertex(papplet.width, 0, 100, 0);
		papplet.vertex(papplet.width, papplet.height, 100, 100);
		papplet.vertex(0, papplet.height, 0, 100);
		papplet.endShape();
	}
}

