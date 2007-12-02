/**
 * 
 */
package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.media.jai.RenderedOp;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import wosaic.WosaicUI;

/**
 * @author carl-erik svensson
 * This class is meant to contain all the pertinent information about a given mosaic.
 */
public class Mosaic {
	
	private Pixel[][] imageGrid;
	private Parameters params;
	private Pixel master;
	private boolean complete;
	
	/**
	 * Default constructor (called by WosaicUI).
	 */
	public Mosaic() {
		
	}
	
	/**
	 * Constructor for a mosaic object called by the Controller.
	 * @param param the set of parameters associated with this mosaic.
	 * @param mPixel the master image in Pixel form
	 */
	
	public Mosaic(Parameters param, Pixel mPixel) {
		init(param, mPixel);
	}
	
	
	/**
	 * Initializes a mosaic object.  A Mosaic object must be initialized before
	 * it can be used in computation.
	 * @param param the set of parameters associated with this mosaic
	 * @param mPixel mPixel the master image in Pixel form
	 */
	public void init(Parameters param, Pixel mPixel) {
		params = param;
		master = mPixel;
		imageGrid = new Pixel[params.resRows][params.resCols];
	}
	
	/**
	 * Writes an image to the specified file.
	 * 
	 * @param img the image to be written to disk
	 * @param file the filename for the image
	 * @param type the encoding for the image
	 * @throws IOException
	 */
	
	public void save(BufferedImage img, String file, String type) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        encoder.encode(img);
        os.close();
	}
	
	
	/**
	 * Creates a BufferedImage of the final mosaic from the input sources.
	 * 
	 * @return the mosaic stitched together in BufferedImage format
	 */
	public BufferedImage createImage() {
		
		Pixel[][] sources = imageGrid;
		RenderedOp mImage = master.source;
		
		// Calculate the target height/width
		int height = (int) params.sHeight * params.resRows;
		int width = (int) params.sWidth * params.resCols;
		
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
		for (int r=0; r < params.resRows; r++) {
			for (int c=0; c < params.resCols; c++) {
				
				try {
					// Scale the source
					sources[r][c].scaleSource(params.sWidth, params.sHeight);
					
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
			result = new BufferedImage(params.mWidth, params.mHeight, BufferedImage.TYPE_INT_RGB);
			result.setData(wr);
		} catch (Exception e) {
			System.out.println("Writing result failed!");
			System.out.println(e);
		}
		
		return result;
	}
	
	/**
	 * Finds the best spot(s) to put the parameter Pixel object.
	 * @param srcPixels the pixel to place in the mosaic
	 * @param colorMap the 3D array containing color information about the master image
	 */
	public synchronized void updateMosaic(Pixel srcPixels, int[][][] colorMap) {
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
				
				if(imageGrid[r][c] != null) {
					// Calculate the score of the Pixel in this spot
					int rsDiff = Math.abs(imageGrid[r][c].avgColor[0] - colorMap[r][c][0]);
					int gsDiff = Math.abs(imageGrid[r][c].avgColor[1] - colorMap[r][c][1]);
					int bsDiff = Math.abs(imageGrid[r][c].avgColor[2] - colorMap[r][c][2]);
					
					int score = rsDiff + gsDiff + bsDiff;
					
					if (matchScore < score) {
						imageGrid[r][c] = srcPixels;
						
						// Send an update notification
						// something like notifyUI(r, c, imageGrid)
						// but should that be synchronized?  It may slow stuff down
					}
				} else {
					// Just assign this Pixel to this spot
					imageGrid[r][c] = srcPixels;
					
					// Send an update notification
				}
			}
		}
		
		notifyAll();
	}
	
	
	/**
	 * Call this function to update the UI with the current mosaic.
	 */
	public synchronized Pixel[][] updateUI() {
		boolean updated = false;
		
		while(!updated && !complete) {
			try {
				wait();
				updated = true;
			} catch (Exception e) {
				System.out.println("updateUI Interrupted!");
				System.out.println(e);
			}
		}
		
		System.out.println("Updating the UIIUIUUII`");
		return imageGrid;
	
	}
	
	
	public synchronized void setDone(boolean d) {
		complete = d;
	}
	
	public synchronized boolean isDone() {
		return complete;
	}
	
	/**
	 * Accessor for the 2D Pixel array that locally stores the mosaic.
	 * @return the mosaic as a 2D Pixel array
	 */
	public Pixel[][] getPixelArr() {
		return imageGrid;
	}
}
