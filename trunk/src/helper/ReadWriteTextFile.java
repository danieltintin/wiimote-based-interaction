package src.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import processing.core.PApplet;

public class ReadWriteTextFile extends PApplet {
	
	public static int WRITE = 0;
	public static int READ  = 1;
	
	private PrintWriter output;
    private BufferedReader input;
    private String inputLine;
    private String filename;
	
	public ReadWriteTextFile(int mode, String filename) {
		this.filename = filename;
		switch(mode) { 
			case 0: // write
				output = createWriter(filename); 
				break; 
			case 1: // read
				input  = createReader(filename);
				break;
		}
	}
	
	public void writeToFile(String line) {
		output.println(line);
	}
	
	public void writeToFileFinished() {
		output.flush();
		output.close();
	}
	
	public String readOneLineFromFile() {
		try {
			inputLine = input.readLine(); // read next line from File
			if(inputLine.startsWith("Box"));
				inputLine = input.readLine(); // read another line if it equals Box
		} catch (IOException e) {
		    //e.printStackTrace();
		    inputLine = null;
		}
		return inputLine; // pass this to the MediaGenerator instance
	}
	
	public void readFromFileFinished() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFilename() {
		return filename;
	}

}
