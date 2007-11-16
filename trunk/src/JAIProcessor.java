/**
 * JAIProcessor.java
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
import com.sun.image.codec.jpeg.*;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class JAIProcessor implements Runnable {

	public static int TOLERANCE = 30;
	public static int INFINITY = 255*3;
	public static int MINUS_INFINITY = -1 - (255*3);
	public static int SLEEP_TIME = 500;

	/**
	 * A set of parameters for this mosaic.  It primarily holds resolution information
	 */
	public Parameters params;
	
	/**
	 * This is the Pixel object for the master image.
	 */
	public Pixel master;
	
	/**
	 * A grid representing the mosaic as pixel objects.
	 */
	public Pixel[][] wosaic;
	
	/**
	 * A reference to the controller object.  This is needed for access
	 * to the shared buffer.
	 */
	public Controller controller;
	
	int[][][] colorMap;
	
	
	/**
	 * This constructor should be used by the threaded application.
	 * 
	 * @param mPixel the master image
	 * @param param mosaic parameters
	 * @param cont the containing controller object
	 */
	public JAIProcessor(Pixel mPixel, Parameters param, Controller cont) {
		params = param;
		master = mPixel;
		controller = cont;
		wosaic = new Pixel[params.resRows][params.resCols];
	}
	
	
	/**
	 * Creates a mosaic by analyzing the master image, and then getting
	 * images from the controller's shared buffer to place in the mosaic.
	 * This thread automatically saves the output (this will change).
	 */
	public void run() {
		
		System.out.println("Running MosaicThrd...");
		
		// Calculate average colors of the segments of the master
		colorMap = analyzeSegments(params.resRows, params.resCols, master.width / params.resCols,
				master.height / params.resRows, master);
		
		while((controller.imagesReceived < controller.targetImages) || 
				controller.sourcesBuffer.size() != 0) 
		{
			// TODO Catch stack overflow exception...
			while (controller.sourcesBuffer.size() != 0) {
				System.out.println("Removing elements from img buf...");
				BufferedImage newImg = controller.removeFromImageBuffer();
				Pixel newPixel = new Pixel(newImg);
				updateMatches(newPixel);
			}
			
			controller.sleepWorker(SLEEP_TIME);
		}
		
		//DBG
		System.out.println("About to put together the mosaic...");
		
		// Paste together the mosaic
		BufferedImage result = createImage(wosaic, params, master.source);
		
		//DBG
		System.out.println("Mosaic created, trying to save...");
		
		// Save it
		try {
			writeResult(result, "threadOutput.jpg", "jpeg");
		} catch (Exception e) {
			System.out.println("Thread could not save image!");
			System.out.println(e);
			return;
		}
		
		System.out.println("Exiting MosaicThrd...");
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
		//JAI.create("encode", img, os, type, null);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        encoder.encode(img);
        os.close();
	}

	/**
	 * Creates a BufferedImage of the final mosaic from the input sources.
	 * 
	 * @param sources segments of the mosaic
	 * @param param the parameters for this mosaic
	 * @param mImage the master image
	 * @return the mosaic
	 */
	public BufferedImage createImage(Pixel[][] sources, Parameters param, RenderedOp mImage) {
		
		// Calculate the target height/width
		int height = (int) param.sHeight * param.resRows;
		int width = (int) param.sWidth * param.resCols;
		
		// Create a writable raster
		Raster raster;
		WritableRaster wr;
		
		//DBG
		System.out.println("Initializing mosaic rasters...");
		
		try {
			raster = mImage.getData();
			wr = raster.createCompatibleWritableRaster(width, height);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("We're running out of memory!");
			return null;
		}
		
		//DBG
		System.out.println("About to iterate through the mosaic pieces...");
		
		// Create the resulting image!
		for (int r=0; r < param.resRows; r++) {
			for (int c=0; c < param.resCols; c++) {
				
				try {
					// Scale the source
					sources[r][c].scaleSource(param.sWidth, param.sHeight);
					
					// Copy the pixels
					wr.setRect(c * sources[r][c].width, r * sources[r][c].height, sources[r][c].getRaster());
				} catch (Exception e) {
					System.out.println(e);
					System.out.println("Running out of memory! ... Continuing");
					System.gc();
				}
			}
		}
		
		//DBG
		System.out.println("Setting the raster data...");
		
		BufferedImage result = null;
		
		try {
			result = new BufferedImage(param.mWidth, param.mHeight, BufferedImage.TYPE_INT_RGB);
			result.setData(wr);
		} catch (Exception e) {
			System.out.println("Writing result failed!");
			System.out.println(e);
		}
		
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
	 * @param srcPixels a set of images to pool from
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
	 * This places an image anyplace
	 * on the mosaic where it is a better fit than what is already there.
	 * This is an incremental version of findMatches.
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
}