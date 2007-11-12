/**
 * Mosaic.java
 * 
 * This file contains the outline for processing images
 * for use in a mosaic.
 */

import utilities.Parameters;
import utilities.Pixel;
import java.awt.Dimension;
import javax.media.jai.*;
import java.io.*;
import java.util.Vector;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Mosaic implements Runnable {

	public static int TOLERANCE = 30;
	public static int INFINITY = 255*3;
	public static int MINUS_INFINITY = -1 - (255*3);

	Parameters params;
	Pixel master;
	public Pixel[][] wosaic;
	int[][][] colorMap;

	
	// FIXME temporary sources for threaded app
	Pixel[] tempSources;
	
	public Mosaic(Pixel mPixel, Parameters param, Pixel[] src) {
		params = param;
		master = mPixel;
		tempSources = src;
		wosaic = new Pixel[params.resRows][params.resCols];
	}
	
	
	/**
	 * Non-threaded Mosaic constructor.
	 */
	public Mosaic() {

	}
	
	public void run() {
		// Calculate average colors of the segments of the master
		colorMap = analyzeSegments(params.resRows, params.resCols, master.width / params.resCols,
				master.height / params.resRows, master);
		
		// Update the mosaic as we get images...
		for(int i=0; i < tempSources.length; i++) {
			updateMatches(tempSources[i]);
		}
		
		// Paste together the mosaic
		BufferedImage result = createImage(wosaic, params, master.source);
		
		// Save it
		try {
			writeResult(result, "images/threadOutput.jpg", "jpeg");
		} catch (Exception e) {
			System.out.println("Thread could not save image!");
			System.out.println(e);
			return;
		}
	}
	
	/**
	 * Writes an image to the specified file.
	 * 
	 * @param img the image to be written to disk
	 * @param file the filename for the image
	 * @param type the encoding for the image
	 * @throws IOException
	 */
	
	public void writeResult(BufferedImage img, String file, String type) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		JAI.create("encode", img, os, type, null);
	}

	/**
	 * Creates a BufferedImage of the final mosaic from the input sources.
	 * 
	 * @param sources segments of the mosaic
	 * @param param the parameters for this mosaic
	 * @param mImage the master image
	 * @return the mosaic
	 */
	private BufferedImage createImage(Pixel[][] sources, Parameters param, RenderedOp mImage) {
		
		// Calculate the target height/width
		int height = param.mHeight;
		int width = param.mWidth;
		
		// Create a writable raster
		Raster raster = mImage.getData();
		WritableRaster wr = raster.createCompatibleWritableRaster(width, height);
		
		// Create the resulting image!
		for (int r=0; r < param.resRows; r++) {
			for (int c=0; c < param.resCols; c++) {
				// Scale the source
				sources[r][c].scaleSource((float) param.sWidth, (float) param.sHeight);
				
				// Copy the pixels
				wr.setRect(c * param.sWidth, r * param.sHeight, sources[r][c].getRaster());
			}
		}
		
		BufferedImage result = new BufferedImage(param.mWidth, param.mHeight, BufferedImage.TYPE_INT_RGB);
		result.setData(wr);
		
		return result;
	}
	
	/**
	 * <p>Matches up segments in the master image with source images.
	 * This is the workhorse of the algorithm.  It uses the notion
	 * of a score to try to find an acceptable match.  Score is 
	 * calculated as follows:</p>
	 * 
	 * <p><ul>
	 * <li>Take the difference between a source image's average red channel,
	 * and the segment's average red channel</li>
	 * <li>Repeat for green and blue channels</li>
	 * <li>Sum the absolute value of these differences</li>
	 * </ul></p>
	 * @param avgColors a grid of average colors in the master
	 * @param srcImages a set of images to pool from
	 */
	
	public Pixel[][] findMatches (int[][][] avgColors, Pixel[] srcPixels, Parameters param) {
		
		//Pixel[] srcPixels = new Pixel[srcImages.length];
		Pixel[][] retVals = new Pixel[param.resRows][param.resCols];
		
		
		// Brute force look for a match
		for (int r=0; r < param.resRows; r++) {
			for(int c =0; c < param.resCols; c++) {
				
				int match = 0;
				int rmDiff = Math.abs(srcPixels[match].avgColor[0] - avgColors[r][c][0]);
				int gmDiff = Math.abs(srcPixels[match].avgColor[1] - avgColors[r][c][1]);
				int bmDiff = Math.abs(srcPixels[match].avgColor[2] - avgColors[r][c][2]);
				int matchScore = rmDiff + gmDiff + bmDiff;
				
				// Find the best (or an acceptable) match for this region
				for(int i=1; i < srcPixels.length; i++) {
					// TODO move the score calculation into the Pixel class
					int riDiff = Math.abs(srcPixels[i].avgColor[0] - avgColors[r][c][0]);
					int giDiff = Math.abs(srcPixels[i].avgColor[1] - avgColors[r][c][1]);
					int biDiff = Math.abs(srcPixels[i].avgColor[2] - avgColors[r][c][2]);
					
					// Keep a score that dictates how good a match is
					// Like in golf, a lower score is better.  This is simply
					// made up of the total difference in each channel, added
					// together.  Other weights can be added in the future.
					int score = riDiff + giDiff + biDiff;
					//score += (TOLERANCE * srcPixels[i].alreadyUsed);
					
					if (score < matchScore) {	
						match = i;
						matchScore = score;
					}
					
					// TODO break the loop if score < TOLERANCE
				}
				
				retVals[r][c] = srcPixels[match];
				srcPixels[match].alreadyUsed++;
				
			}
		}
		
		// We are done!  ... ?
		return retVals;
	}

	/**
	 * An incremental version of findMatches.  This places an image anyplace
	 * on the mosaic where it is a better fit than what is already there.
	 * 
	 * @param srcPixels the image to try and place
	 */
	public void updateMatches (Pixel srcPixels) {
		
		// Check all the segments to see where this might fit
		for (int r=0; r < params.resRows; r++) {
			for(int c =0; c < params.resCols; c++) {

				int rmDiff = Math.abs(srcPixels.avgColor[0] - colorMap[r][c][0]);
				int gmDiff = Math.abs(srcPixels.avgColor[1] - colorMap[r][c][1]);
				int bmDiff = Math.abs(srcPixels.avgColor[2] - colorMap[r][c][2]);
			
				// Keep a score that dictates how good a match is
				// Like in golf, a lower score is better.  This is simply
				// made up of the total difference in each channel, added
				// together.  Other weights can be added in the future.
				int matchScore = rmDiff + gmDiff + bmDiff;
				
				if(wosaic[r][c] != null) {
					// Calculate the score of the Pixel in this spot
					int rsDiff = Math.abs(wosaic[r][c].avgColor[0] - colorMap[r][c][0]);
					int gsDiff = Math.abs(wosaic[r][c].avgColor[1] - colorMap[r][c][1]);
					int bsDiff = Math.abs(wosaic[r][c].avgColor[2] - colorMap[r][c][2]);
					
					int score = rsDiff + gsDiff + bsDiff;
					
					if (matchScore < score) {
						wosaic[r][c] = srcPixels;
					}
				} else {
					// Just assign this Pixel to this spot
					wosaic[r][c] = srcPixels;
				}
			}
		}
	}
	
	/**
	 * Split an image up into segments, and calculate its average color.
	 * 
	 * @param numRows
	 * @param numCols
	 * @param width the width of a segment
	 * @param height the height of a segment
	 * @param mPixel the source image
	 * @return the average colors of each segment
	 */
	public int[][][] analyzeSegments(int numRows, int numCols, int width, int height, Pixel mPixel) {
		int[][][] avgColors = new int[numRows][numCols][3];
		
		for (int r=0; r < numRows; r++){
			for(int c=0; c < numCols; c++) {
				int startY = r * height;
				int startX = c * width;
				
				mPixel.getAvgColor(startX, startY, width, 
									height, avgColors[r][c]);
				System.out.println("Avg color for segment [" + r + "][" + c +"]: " 
									+ avgColors[r][c][0] + ", " + avgColors[r][c][1] + 
									", " + avgColors[r][c][2]);
			}
		}
		
		return avgColors;
	}
	
	/**
	 * Controls the flow of creating a mosaic.  This assumes
	 * Images are already chosen.
	 * 
	 * @param srcImages working set of images
	 * @param mImage the master image
	 * @param param the set of parameters for this mosaic
	 */
	public Pixel[][] createMosaic(Pixel[] srcImages, Pixel mPixel, Parameters param) {
		
		// Split up mImage into segments based on the desired resolution
		int numRows = param.resRows;
		int numCols = param.resCols;
		
		// Set the dimensions of the segments of the 
		// master image.
		Dimension segmentDim = new Dimension(mPixel.width / numCols, mPixel.height / numRows);

		
		// Analyze the segments for average color
		int[][][] avgColors = analyzeSegments(numRows, numCols, segmentDim.width, segmentDim.height, mPixel);
		
		// Iterate through the srcImages and find a good match for each section
		Pixel[][] matches = findMatches(avgColors, srcImages, param);
		
		// Check for errors
		if (matches == null) {
			System.out.println("ERROR: findMatches failed!");
			return null;
		}
		
		// Create and save the image
		BufferedImage mosaic = createImage(matches, param, mPixel.source);
		
		try {
			writeResult(mosaic, "images/output.jpg", "jpeg");
		} catch (IOException e) {
			System.out.println("Saving of mosaic failed!");
			System.out.println(e);
			return null;
		}
		
		return matches;
	}

}
