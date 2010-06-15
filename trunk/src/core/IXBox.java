package src.core;

public interface IXBox {

	public void rotateX(int direction);
	
	public void rotateY(int direction);
	
	public void freeRotate(int mouseX, int mouseY, int pMouseX, int pMouseY);
	
	public void rotateAroundGlobalZ(int direction);
	
	public void move(int X, int Y);
	
//	public void zoom(int Z);
}
