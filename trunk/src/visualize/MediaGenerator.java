package src.visualize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;
import shapes3d.Shape3D;
import src.core.Input;
import src.core.Listeners;
import src.core.Main;
import src.core.XBox;
import src.helper.Log;

public class MediaGenerator extends PApplet {
	
	public static final int NUMBERS = 0;
	public static final int COLORS  = 1;
	public static final int PHOTOS  = 2;
	
	public static int activeTextureMode;
	public static int taskNumber;
	
	private int number;
	public  ArrayList<XBox> boxes;
	private PApplet papplet;
	private Input input;
	private int width, height;
	private String[] texturesNumbers, texturesColors, texturesPhotos;
		
	private static ArrayList<ArrayList<XBox>> numberBoxes = new ArrayList<ArrayList<XBox>>();
	private static ArrayList<ArrayList<XBox>> colorBoxes  = new ArrayList<ArrayList<XBox>>();
	private static ArrayList<ArrayList<XBox>> photoBoxes  = new ArrayList<ArrayList<XBox>>();	

	public MediaGenerator(int _number, PApplet _papplet, int _width, int _height) {
		this.number = _number;
		this.boxes = new ArrayList<XBox>();
		this.papplet = _papplet;
		this.width = _width;
		this.height = _height;
		this.texturesNumbers = new String[12];
		this.texturesColors  = new String[12];
		this.texturesPhotos  = new String[96];
		
		this.loadTextures();				
//		this.create(NUMBERS, -1); // create a default view		
	}
	
	private int seq_idx = 0;
	public static int task_number = 0;
	public void startSequence(Input input) {
		this.input = input;
				
		input.changeInteractionMode(Main.technique_sequence[seq_idx]);
		this.reset(Main.environment_sequence[seq_idx], task_number++);
		
		src.helper.Log.startTimer();
	}
	
	public void continueSequence() {
//		System.out.println("task_number = " + task_number + ", seq_idx = " + seq_idx);
		if(task_number >= 5) {
			seq_idx++;
			if(seq_idx >= 3) {
				System.out.println("test done!");
				System.exit(1);
				return;
			}
			task_number = 0;			
			input.changeInteractionMode(Main.technique_sequence[seq_idx]);
			System.out.println("changed interaction mode");
		}
		System.out.println("task_number = " + task_number + ", seq_idx = " + seq_idx);
		this.reset(Main.environment_sequence[seq_idx], task_number++);		
		src.helper.Log.startTimer();
	}
	
	public void resumeSequence() {
		System.out.println("task_number = " + task_number + ", seq_idx = " + seq_idx);
		input.changeInteractionMode(Main.technique_sequence[seq_idx]);
		this.reset(Main.environment_sequence[seq_idx], task_number - 1);
		src.helper.Log.resetLogVars();
		src.helper.Log.startTimer();
	}

	
	private String PATH = "src/files/startconfig/";
	private void loadStartConfigs(String filename) {
		
//		System.out.println("loadStartConfigs");
		
		ArrayList<PVector> pvs;
		try {
			pvs = Log.loadPVectors(PATH + filename);
		} catch (FileNotFoundException e) {				
//				e.printStackTrace();
			System.out.println("file not found: " + (PATH + filename));
			return;
		}
		String kind = filename.split("_")[0];
		
		//check extension if filename
		
		for(int i=0; i<pvs.size()/2; i++) {
			boxes.add(new XBox(this.papplet, 100, 100, 100));		
		}
		for(int i=0; i<pvs.size(); i++) {
			if(i % 2 == 0) {
				//rot vec
				boxes.get(i/2).rotateTo(pvs.get(i));
			}
			else {
				//pos vec
				boxes.get(i/2).moveTo(pvs.get(i));
			}
		}
	}
	
	private String[] getFilenames() {
		File dir = new File(System.getProperty("user.dir") + "/" +PATH);				
//		System.out.println("Directory: " + dir.getAbsolutePath());
		if(!dir.exists()) {
			System.out.println("Directory not found: " + dir.getName());
			return null;
		}
		

		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        //return !name.startsWith(".");
		    	return name.endsWith(".bin");
		    }
		};
		return dir.list(filter);
	}

	public void loadTextures() {
		// numbers and colors
		for (int i = 1; i < 7; i++) {
			texturesNumbers[i-1] = "src/files/n"+i+".jpg";
			texturesNumbers[i+5] = "src/files/n"+i+"s.jpg";
			texturesColors[i-1] = "src/files/c"+i+".jpg";
			texturesColors[i+5] = "src/files/c"+i+"s.jpg";
		}
		// photos
		int photocnt = 1;
		int arraycnt = 0;
		while(photocnt < 7) {
			for(int k = 1; k < 9; k++) {
				texturesPhotos[arraycnt] = "src/files/p"+photocnt+"_"+k+".jpg";
				texturesPhotos[arraycnt+48] = "src/files/p"+photocnt+"_"+k+"s.jpg";
				arraycnt++;
			}
			photocnt++;
		}
		println("textures loaded");
	}
	
	private boolean xaReduced = false;
	
	public void create(int texturemode, int taskNumber) {
		
		setActiveTextureMode(texturemode);
		
		if (taskNumber<0) {
			int x = 0;
			xaReduced = false;
			if (texturemode == NUMBERS || texturemode == COLORS) {
				
				int xa = ((width - ((number*50) + (((number/2)-1)*10)))/2)+50; 
				int ya = ((height - ((number*25) + (((number/4)-1)*10)))/2)+50; 
	
				for(int i = 0; i < number; i++) {
					// add a new box to the canvas
					boxes.add(new XBox(this.papplet, 100, 100, 100));
					if(i < number/2) {
						boxes.get(i).moveTo(xa+x, ya, 0);
					} else if(i >= number/2 && i < number) {
						if(!xaReduced) {
							x = 0;
							xaReduced = true;
						} 
						boxes.get(i).moveTo(xa+x, ya+110, 0);
					}
					
					for(int j = 0; j < 6; j++) { // set the texture for all 6 sides of the box
						if (texturemode == NUMBERS) {
							boxes.get(i).setTexture(texturesNumbers[j], (int) Math.pow(2, j));
						} else {
							boxes.get(i).setTexture(texturesColors[j], (int) Math.pow(2, j));
						}
					}
					x += 110;
				}
			} else { // texturemode == PHOTOS, always 8 photos needed
				
				int xa = ((width - 430)/2)+50; 
				int ya = ((height - 210)/2)+50; 
				x = 0;
				
				for(int i=0; i<8; i++) {
					boxes.add(new XBox(this.papplet, 100, 100, 100));
					if(i < 4) {
						boxes.get(i).moveTo(xa+x, ya, 0);
					} else if(i >= 4 && i < 8) {
						if(!xaReduced) {
							x = 0;
							xaReduced = true;
						} 
						boxes.get(i).moveTo(xa+x, ya+110, 0);
					}
					
					int faceCounter = 0;
					for(int j=i; j<48; j=j+8) { // needs to be smaller than 48, array size = 48
						boxes.get(i).setTexture(texturesPhotos[j], (int) Math.pow(2, faceCounter));
						faceCounter++;
					}
					x += 110;
				}
			}
			
		} else { // if a tasknumber is provided
			
			if(texturemode == NUMBERS) {
//				boxes = numberBoxes.get(taskNumber);
				loadStartConfigs("number_task" + taskNumber + ".bin");				
				//apply textures
				for(int i = 0; i < boxes.size(); i++) {
					for(int j = 0; j < 6; j++) { // set the texture for all 6 sides of the box
						boxes.get(i).setTexture(texturesNumbers[j], (int) Math.pow(2, j));
					}
				}
			}			
			else if(texturemode == COLORS) {
//				boxes = colorBoxes.get(taskNumber);
				loadStartConfigs("color_task" + taskNumber + ".bin");
				//apply textures
				for(int i = 0; i < boxes.size(); i++) {
					for(int j = 0; j < 6; j++) { // set the texture for all 6 sides of the box Ê Ê 
						boxes.get(i).setTexture(texturesColors[j], (int) Math.pow(2, j));
					}
				}
			}
			else if(texturemode == PHOTOS) {
//				boxes = photoBoxes.get(taskNumber);
				loadStartConfigs("photo_task" + taskNumber + ".bin");
				//apply textures
				for(int i=0; i<boxes.size(); i++) {
					
					int faceCounter = 0;
					for(int j=i; j<48; j=j+8) { // needs to be smaller than 48, array size = 48
						boxes.get(i).setTexture(texturesPhotos[j], (int) Math.pow(2, faceCounter));
						faceCounter++;
					}
					
				}
			}
			//*/
		} 
	}
	
	// this is called within create()
	private void setActiveTextureMode(int texturemode) {
		this.activeTextureMode = texturemode;
	}
	
	// returns an array of strings depending on which textureMode is selected
	// calling class: Input.java
	public String[] getTextures() {
		if (activeTextureMode == NUMBERS) {
			return texturesNumbers;
		} else if (activeTextureMode == COLORS) {
			return texturesColors;
		} else {
			return texturesPhotos;
		}
	}
	
	public void reset(int texturemode, int taskNumber){
		
		
		// reset static box counter and set selected object to null
		XBox.counter = 1;
		XBox.selected = null;
		
		//might be disabled because of box-pinning
		Listeners.IR_enabled = true; 
		
		// also erase all of the items in the shapes3d arraylist
		// to free memory and resolve the picking problems after reset
		for(int i = 0; i < boxes.size(); i++){
			Shape3D s = (Shape3D)boxes.get(i);
			s.finishedWith();
		}
		
		// clear the arraylist
		boxes.clear(); 
		// create a new set with the given texturemode
		create(texturemode, taskNumber); 
		
		
	}
	
	public static int activeTextureMode() {
		return activeTextureMode;
	}
	
	public static int getNumberModeSize() {
		return numberBoxes.size();
	}
	
	public static int getColorModeSize() {
		return colorBoxes.size();
	}
	
	public static int getPhotoModeSize() {
		return photoBoxes.size();
	}
	
	private String[] filenames;
}
