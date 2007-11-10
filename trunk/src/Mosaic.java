/**
 * Mosaic.java
 * 
 * This file contains the outline for processing images
 * for use in a mosaic.
 */

import utilities.Parameters;
import utilities.Pixel;
import java.awt.Dimension;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Mosaic {

	/**
	 * @param srcImages working set of images
	 * @param mImage the master image
	 * @param param the set of parameters for this mosaic
	 * 
	 * Controls the flow of creating a mosaic.  This assumes
	 * Images are already chosen.
	 */
	public void createMosaic(String[] srcImages, String mImage, Parameters param) {
		// Set up a Pixel object for mImage
		Pixel mPixel = new Pixel(mImage);
		
		// TODO Split up mImage into segments based on the desired resolution
		int numRows = param.resRows;
		int numCols = param.resCols;
		
		// Set the dimensions of the segments of the 
		// master image.
		Dimension segmentDim = new Dimension(mPixel.width / numCols, mPixel.height / numRows);

		
		// TODO Analyze the segments for average color
		int[][][] avgColors = new int[numRows][numCols][3];
		
		for (int r=0; r < numRows; r++){
			for(int c=0; c < numCols; c++) {
				int startY = r * segmentDim.height;
				int startX = c * segmentDim.width;
				
				mPixel.getAvgColor(startX, startY, segmentDim.width, 
									segmentDim.height, avgColors[r][c]);
				System.out.println("Avg color for segment [" + r + "][" + c +"]: " 
									+ avgColors[r][c][0] + ", " + avgColors[r][c][1] + 
									", " + avgColors[r][c][2]);
			}
		}
		
		
		
		// TODO Iterate through the srcImages and find a good match for each section
		findMatches(avgColors, srcImages);
	}
	
	/**
	 * @param avgColors a grid of average colors in the master
	 * @param srcImages a set of images to pool from
	 */
	
	void findMatches (int[][][] avgColors, String[] srcImages) {
		
		Pixel[] srcPixels = new Pixel[srcImages.length];
		
		// Iterate through srcImages
		for (int i=0; i < srcImages.length; i++) {
			// Calculate average colors
			srcPixels[i] = new Pixel(srcImages[i]);
		}
		
	}

}
