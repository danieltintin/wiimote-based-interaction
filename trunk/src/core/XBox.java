package src.core;

import processing.core.PApplet;
import processing.core.PVector;
import shapes3d.Anchor;
import shapes3d.Box;
import shapes3d.utils.Rot;
import shapes3d.utils.RotOrder;
import src.visualize.Canvas;

public class XBox extends Box implements Comparable {	
	
	private final float ROTATION_SPEED = 45f;
	
	public static final int CLOCKWISE = 1;
	public static final int COUNTER_CLOCKWISE = -1;
	public static XBox selected = null;
	
	public static int counter = 1;
	public int number;

	public float depth;
	
	private boolean pinned = false;
	
	public XBox(PApplet app, float width, float height, float depth) {
		super(app, width, height, depth);
		this.number = counter++;
		this.depth = depth;
	}
	
	public void pin() { pinned = true; }
	public void unpin() { pinned = false; }
	public boolean isPinned() { return pinned; }

	public void move(int X, int Y) {		
		// movement is constrained by the room	
		moveBy((float)X, (float)Y, 0);
		
		src.helper.Log.addMoveEvent();
	}
	
	public void moveTo(int X, int Y, int Z) {
		
		src.helper.Log.addMoveEvent();
		
		// movement is constrained by the room			
		// wall left: x
		if(X < (0 + this.getDepth())) {
			X = (int) (0 + this.getDepth());
		}
		// wall top: y
		if(Y < (0 + this.getDepth())) {
			Y = (int) (0 + this.getDepth());
		}
		// wall bottom: y
		if(Y > (Canvas.height - this.getDepth())) {
			Y = (int) (Canvas.height - this.getDepth());
		}
		// wall right: x
		if(X > (Canvas.width - this.getDepth())) {
			X = (int) (Canvas.width - this.getDepth());			
		}
		// wall back z
//		if(Z < (Canvas.depth + this.getDepth())) {
//			Z = (int) (Canvas.depth + this.getDepth());
//		}
		if(Z - this.getDepth() < Canvas.depth) {
			Z = (int)(Canvas.depth + this.getDepth());
		}
		// wall front z
		if(Z > -Canvas.depth/4 - this.getDepth()) { //used to be 400
			Z = (int)(-Canvas.depth/4 -this.getDepth());
		}
		
		super.moveTo(X, Y, Z);		
		
	}
    
	public void rotateX(int direction) {
		this.rotateBy(direction * PApplet.radians(ROTATION_SPEED) , 0, 0);		
	}
	
	PVector v1 = new PVector();
	PVector v2 = new PVector();
	public void freeRotate(int mouseX, int mouseY, int pMouseX, int pMouseY) {
		long err;
		err = vectorize(pMouseX, pMouseY, (int)this.x(), (int)this.y(), 800, v1);
	    err += vectorize(mouseX, mouseY, (int)this.x(), (int)this.y(), 800, v2);

	    if (err == 0) {
		    Rot rotation = zeroHysteresisRotation(v1, v2);
		    if(rotation == null) return; // nothing to rotate
		    Rot prev_rotation = new Rot(RotOrder.XYZ, this.getRotArray()[0],
		    							this.getRotArray()[1], this.getRotArray()[2]);
		    rotation = rotation.applyTo(prev_rotation);
		    this.rotateTo(rotation.getAngles(RotOrder.XYZ));
		    src.helper.Log.addRotationEvent();
	    }
	}
	
	// rotates the cube around the global z axis.
	public void rotateAroundGlobalZ(int direction) {
		// retrieve current rotation of th box
		//PVector rotationAxis = this.getRotVec();
		//this.shapeOrientation(this.getRotVec(), this.getPosVec());
		// TODO maybe reverse it?
		//rotationAxis.set(this.getRotArray()[0], this.getRotArray()[1], this.getRotArray()[2]);
		// build a rotation from an axis and an angle		
		//Rot rotation = new Rot(rotationAxis, (float) 15 * direction);
		//this.rotateTo(rotation.getAngles(RotOrder.XYZ));
		//
		// Create an anchor object at the world origin(0,0,0)
		// and with zero rotation.
		Anchor anchor = new Anchor(app, this.getPosVec());
		// Add the box
		//anchor.moveTo(this.x(), this.y(), this.z());
		PApplet.println("HERE NOW");
		anchor.addShape(this);
		// Rotate the anchor (rotateBy or rotateTo)
		anchor.rotateBy(0,0,app.radians(30));
		//
		//
		//
		//Rot rot = new Rot(new PVector(0,0,1), 30);
		//PVector p = this.getRotVec();
		//rot.applyTo(p);
		//this.rotateTo(p);
	}
	
	// called from within freeRotate()
	private long vectorize(int mouseX, int mouseY, int originSpX, 
			   int originSpY, int radius, PVector vec) {

		float x,y,z, modulus;
		
		x = (float)(mouseX - originSpX)/radius;
		y = (float)(mouseY - originSpY)/radius;
		
		// TODO really needed??
		// invereses the whole stuff, not needed
		//y *= -1.0;         // compensate for "inverted" screen y-axis!
		
		modulus = x*x + y*y;
		if (modulus > 1.) {
			return 1L; // error
		}
		
		z = (float) Math.sqrt(1. - (x*x + y*y));    // compute fictitious 'z' value
		
		vec.set(x,y,z);
		return 0L;	   
	}
		
	// called from within freeRotate()
	private Rot zeroHysteresisRotation(PVector v1, PVector v2 ) {
		PVector cross;
		float dot, angle;
		
		dot = v1.dot(v2);
		
		if (dot == 1.0) 
			return null; // nothing to do
		
		cross = (PVector) v1.cross(v2); // axis of rotation
		cross.normalize();   
		
		angle = (float) (2.*Math.acos(dot));  // angle of rotation
		
		Rot rotationMatrix = new Rot(cross, angle);
		return rotationMatrix;
	}
	
	public void rotateXYZ2(float rotationFactorX, float  rotationFactorY, float rotationFactorZ) {
		
		
		//save rotation
		float[] current_rotationXYZ = this.getRotArray();
		//rotate back
		this.rotateTo(0, 0, 0);
		//rotate back to orignial + new rotation
		float[] angles = {current_rotationXYZ[0] + -PApplet.radians(ROTATION_SPEED * rotationFactorX),
				current_rotationXYZ[1] + -PApplet.radians(ROTATION_SPEED * rotationFactorY),
				current_rotationXYZ[2] + PApplet.radians(ROTATION_SPEED * rotationFactorZ)};
		this.rotateBy(angles);
		//*/
	}

	public void rotateY(int direction) {
		this.rotateBy(0, direction * PApplet.radians(ROTATION_SPEED), 0);		
	}

	public void rotateZ(float rotationFactor) {
		this.rotateBy(0, 0, PApplet.radians(ROTATION_SPEED * rotationFactor));		
	}
	
	public void wii_zoom(int Z) {
		if(Z == 0)
			return;
		src.helper.Log.addZoomDistance((int)Math.abs(Z));
		//move in increments of one pixel over ~50 ms
		new ZoomThread(this, Z).start();
		
	}

    protected class ZoomThread extends Thread {
        
    	int Z;
    	XBox box;    	
    	
    	ZoomThread(XBox box, int Z) {
    		this.box = box;
    		this.Z = Z;    		
        }

        public void run() { 
        	int dir = (int)Math.signum(Z);
        	long sleep = (int)(50/Math.abs(Z));
        	for(int i=1; i <= Math.abs(Z); i++) {
        		((XBox)box).moveTo((int)x(), (int)y(), (int)z() + dir);
//        		box.moveBy(0,0,dir*i);
        		try { Thread.sleep(sleep); } catch (InterruptedException e) {}
        	}
        }
    }
	
	public static final int	SWIPE_HORIZONTAL = 0, SWIPE_VERTICAL = 1;
							
	public static int swipe_axis = SWIPE_HORIZONTAL;
	public static final int SWIPE_POSITIVE = 1, SWIPE_NEGATIVE = -1;
	public static int swipe_direction = SWIPE_POSITIVE;
	//Compares this object with the specified object for order. 
	//Returns a negative integer, zero, or a positive integer as 
	//this object is less than, equal to, or greater than the 
	//specified object.
	//used by Arrays.Sort(...)
	public int compareTo(Object o) {
		
		XBox xBox = (XBox) o;
		
		//boxes are equal
		if(XBox.equals(this, xBox))
			return 0;

		//signed vector distances
		double dist1, dist2;
		int sign1 = 0, sign2 = 0;
		final int OFFSET = 25;
		if(swipe_axis == SWIPE_HORIZONTAL) {
			sign1 = swipe_direction * (int)Math.signum(this.x() - selected.x() - 
														XBox.swipe_direction * OFFSET);
			sign2 = swipe_direction * (int)Math.signum(xBox.x() - selected.x() - 
														XBox.swipe_direction * OFFSET);
		}
		else if(swipe_axis == SWIPE_VERTICAL) {
			sign1 = swipe_direction * (int)Math.signum(this.y() - selected.y() - 
														XBox.swipe_direction * OFFSET);
			sign2 = swipe_direction * (int)Math.signum(xBox.y() - selected.y() - 
														XBox.swipe_direction * OFFSET);		
		}
		dist1 = sign1 * this.distanceTo(selected);
		dist2 = sign2 * xBox.distanceTo(selected);
		
		//'this' box is right/below of the reference box and
		//other box is to the left/above
		if(Math.signum(dist1) > 0 && Math.signum(dist2) < 0) {
			return 1;
		}
		//other way around
		if(Math.signum(dist2) > 0 && Math.signum(dist1) < 0) {
			return -1;
		}
		//both are to the left/above of the reference box	
		if(Math.signum(dist1) < 0 && Math.signum(dist2) < 0) {
			//they are in this regard equal but always larger than 
			//boxes on the right side of the reference box
			//return 0;
			if(Math.abs(dist1) < Math.abs(dist2))
				return 1;
			return -1;
		}
		//this box is closer to reference box
		if(dist1 < dist2)
			return -1;
		//other box is closer
		return 1;		
	}
	
	public double distanceTo(XBox box) {
		return  Math.cbrt(
				Math.pow(this.x() - box.x(), 2) +
				Math.pow(this.y() - box.y(), 2) +
				Math.pow(this.z() - box.z(), 2));				
	}
	
	public static boolean equals(Box box1, XBox box2) {
		if(box1 == null || box2 == null) return false;
		return box1.x() == box2.x() && box1.y() == box2.y() && box1.z() == box2.z();		
	}
	
	public String toString() {
		return "(x,y,z) = ("+this.x()+","+this.y()+","+this.z()+")";
	}
	
	public float getDepth(){
		return this.depth;
	}
}
