//
//  Driver.java
//  
//
//  Created by Carl-Erik Svensson on 12/17/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

//package tests;
import javax.media.jai.*;
import utilities.Pixel;

public class Driver {
	public static void main(String args[]) {
		Controller controller = new Controller();
		
		Thread contThread = new Thread(controller, "Controller Thread");
		contThread.setPriority(10);
		contThread.start();
	}
}