package wosaic;

//package tests;
import javax.media.jai.*;
import wosaic.utilities.Pixel;

public class Driver {
	public static void main(String args[]) {
		Controller controller = new Controller();
		
		Thread contThread = new Thread(controller, "Controller Thread");
		contThread.setPriority(10);
		contThread.start();
	}
}