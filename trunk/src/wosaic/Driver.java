package wosaic;

//package tests;
import javax.media.jai.*;
import wosaic.utilities.Pixel;
import wosaic.utilities.Facebook;

public class Driver {
	public static void main(String args[]) {
		/*Controller controller = new Controller();
		
		Thread contThread = new Thread(controller, "Controller Thread");
		contThread.setPriority(10);
		contThread.start();*/
		
		// Facebook Test
		Facebook fb = new Facebook(null);
		try {
			fb.authenticate();
			fb.getImages();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}