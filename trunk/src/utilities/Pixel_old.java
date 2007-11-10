//
//  Pixel.java
//  
//
//  Created by Carl-Erik Svensson on 12/17/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

package utilities;
import javax.media.jai.*;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;

public class Pixel_old {

	// Image to operate on
	private PlanarImage source = null;
	private Histogram histogram = null;
	private Raster pixels = null;
	private String file = "";
	
	public int[] avgColor = new int[3];

	// Constructor
	public Pixel_old(String filename) {
		file = filename;
		
		// Create a PlanarImage from the given file
		source = JAI.create("fileload", filename);
		
		// Get pixel-information of the src image
		pixels = source.getData();
		
		// Get the average color
		getAvgColor(0, 0, source.getWidth(), source.getHeight(), avgColor);
	}
	
	public void getAvgColor(int x, int y, int w, int h, int avgVal[]) {
		
		// Error-check the boundaries of avgVal
		if (avgVal.length < 3) {
			System.out.println("getProminentColor: avgVal not big enough");
			return;
		}
		
		// Get all the pixels in the image
		int numPixels = w * h;
		int[] image = new int[numPixels*3];
		getPixelArea(x,y,w,h, image);
		
		int rSum=0, gSum=0, bSum = 0;
		
		for (int i=0; i<image.length; i += 3) {
			rSum += image[i];
			gSum += image[i+1];
			bSum += image[i+2];
		}
		
		avgVal[0] = rSum / (numPixels);
		avgVal[1] = gSum / (numPixels);
		avgVal[2] = bSum / (numPixels);
		
		return;
	}

	//
	// getPixelAt(int, int, in[])
	//
	//    populates the int[0], int[1], int[2] with the
	//    Red, Green, and Blue values for the pixel at
	//    the given x and y location.
	//
	public void getPixelAt(int x, int y, int[] array) {
	
		pixels.getPixel(x, y, array);
		return;
	}
	
	//
	// getPixelArea(int, int, int, int, int[])
	//
	//    populates the int[] with RGB values for each pixel
	//    in the area specified by width w and height h, with
	//    top-left corner at location (x, y).  indeces, 0, 1, and 2
	//    of int[] refer to the RGB values for the first pixel.
	//    Likewise indeces i*3, (i+1)*3, and (i+2)*3 refer to the RGB
	//    values of the i-th pixel.
	//
	public void getPixelArea(int x, int y, int w, int h, int[] array) {
	
		pixels.getPixels(x, y, w, h, array);
		return;
	}
	
	//
	// getHistogram()
	//    returns a histogram representing the spread of pixel
	//    values throughout the image.  This could be helpful
	//    in determining what the most prominent colors in a
	//    picture are.
	//
	public Histogram getHist() {
		// Define a region of interest - only works on single banded images!
		//    Thought: Is there a way to isolate the bands of an image?
		//             Can we get histograms of individual bands?
		
		
		//ROI r = new ROI(source);
		
		// Get pixel-information of the src image
		//Raster pixels = source.getData();
		
        // set up the histogram
        int[] bins = { 256 };
        double[] low = { 0.0D };
        double[] high = { 256.0D };
		
		int xPeriod = 1;
		int yPeriod = 1;
		int xStart = 0;
		int yStart = 0;
		
		// Create a histogram... assume one band?
		histogram = new Histogram(bins, low, high);
		
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(source);
		pb.add(histogram);  // This was in an example but does not run!
		pb.add(null);
		pb.add(1);
		pb.add(1);
		
		RenderedOp op = JAI.create("histogram", pb, null);
		histogram = (Histogram) op.getProperty("histogram");
		
		// We can either use a ParameterBlock to create the histogram
		// via a call to JAI.create... OR we can call countPixels (I
		// think) and give it the Raster that we defined.  Is one way
		// faster than the other?
		
		//histogram.countPixels(pixels, r, xStart, yStart, xPeriod, yPeriod);

		
		return histogram;
		
	}
	
	//
	// toString()
	//
	// returns a string representation of this object in the form
	// "filename:R:G:B" where R:G:B denote the RGB values representing
	// the most prominent color.
	//
	public String toString() {
		String s = "";
		
		return s;
	}


}
