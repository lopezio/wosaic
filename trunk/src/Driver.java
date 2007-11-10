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
		// Let's do something fun!
		String img = "../images/default.jpg";
		
		/*try {
			img = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			img = "default.jpg";
		}*/
		
		Pixel info = new Pixel(img);
		//Histogram h = info.getHist();
		//System.out.println(h.toString());
		
		int[] pixelValue = new int[3];
		info.getPixelAt(1,1,pixelValue);
		
		//Print out results
		System.out.println("A signle pixel");
		System.out.println("pixelValue[0]: " +pixelValue[0]+"pixelValue[1]: " + pixelValue[1] + "pixelValue[2]: " + pixelValue[2]);
		
		//Get a multitude of pixels
		int[] pixelArea = new int[60];
		info.getPixelArea(420,240,4,5,pixelArea);
		
		// Print out results
		for (int i = 0; i < pixelArea.length; i++) {
			System.out.println("pixelArea[" + i + "]: " + pixelArea[i]);
		}
		
		// Sample the average color...
		System.out.println("Average Red: " + info.avgColor[0] + 
						   "\nAverage Green: " + info.avgColor[1] + 
						   "\nAverage Blue: " + info.avgColor[2]);
		
	}
}
