package src.visualize;

import processing.core.PApplet;
import processing.core.PImage;

public class Canvas extends PApplet {
	
	private PApplet papplet;
	public static int width;
	public static int height;
	public static int depth;
	private int distanceX;
	private int distanceY;
	private PImage wallTexture;
	private PImage backTexture;
	
	private int topTint, leftTint, rightTint, bottomTint, backTint;
	
	public Canvas(PApplet _papplet, int _width, int _height) {
		this.papplet  = _papplet;
		Canvas.width  = _width;
		Canvas.height = _height;
		Canvas.depth  = -600;
		this.distanceX = 160;
		this.distanceY = 85;
		
		topTint = 165;
		rightTint = 190;
		leftTint = 255;
		bottomTint = 220;
		backTint = 255;
		
		
		
		this.wallTexture = loadImage("src/files/canvasBackgroundWall_v2.jpg");
		this.backTexture = loadImage("src/files/canvasBackgroundBack_v2.jpg");
	}
	
	public void draw() {
		// 5 canvas surfaces for every visible surface
		// mapped to correct perspective

		papplet.beginShape(papplet.QUADS); // walls
		
		// top
		papplet.tint(topTint, topTint, topTint, 255);
		papplet.texture(wallTexture); // applied to all 4 walls
		papplet.vertex(0, 0, 0, 0, wallTexture.height); 
		papplet.vertex(width, 0, 0, wallTexture.width, wallTexture.height); 
		papplet.vertex(width-distanceX, distanceY, depth, wallTexture.width, 0); 
		papplet.vertex(distanceX, distanceY, depth, 0, 0);
		papplet.noTint();
		
		
		// right
		papplet.tint(rightTint, rightTint, rightTint, 255);
		papplet.vertex(width, 0, 0, wallTexture.width, wallTexture.height); 
		papplet.vertex(width, height, 0, 0, wallTexture.height);
		papplet.vertex(width-distanceX, height-distanceY, depth, wallTexture.width, 0);
		papplet.vertex(width-distanceX, distanceY, depth, 0, 0);
		papplet.noTint();
		
		// bottom
		papplet.tint(bottomTint, bottomTint, bottomTint, 255);
		papplet.vertex(distanceX, height-distanceY, depth, 0, 0); 
		papplet.vertex(width-distanceX, height-distanceY, depth, wallTexture.width, 0);
		papplet.vertex(width, height, 0, wallTexture.width, wallTexture.height);
		papplet.vertex(0, height, 0, 0, wallTexture.height);
		papplet.noTint();
		
		//left
		papplet.tint(leftTint, leftTint, leftTint, 255);
		papplet.vertex(0, 0, 0, 0, wallTexture.height);
		papplet.vertex(distanceX, distanceY, depth, 0, 0);
		papplet.vertex(distanceX, height-distanceY, depth, wallTexture.width, 0);
		papplet.vertex(0, height, 0, wallTexture.width, wallTexture.height);
		papplet.endShape();	
		papplet.noTint();

		//back
		papplet.tint(backTint, backTint, backTint, 255);
		papplet.beginShape(papplet.QUAD);
		papplet.texture(backTexture);
		papplet.vertex(distanceX, distanceY, depth, 0, 0);
		papplet.vertex(width-distanceX, distanceY, depth, backTexture.width, 0);
		papplet.vertex(width-distanceX, height-distanceY, depth, backTexture.width, backTexture.height);
		papplet.vertex(distanceX, height-distanceY, depth, 0, backTexture.height);
		papplet.endShape();
		papplet.noTint();
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void setTint(int[] tint) {
		topTint    = tint[0];
		rightTint  = tint[1];
		bottomTint = tint[2];
		leftTint   = tint[3];
		backTint   = tint[4];
	}
	
	public int[] getTint() {
		return new int[] {topTint, rightTint, bottomTint, leftTint, backTint};
	}
}