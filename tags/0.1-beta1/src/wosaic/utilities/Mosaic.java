/**
 * 
 */
package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author carl-erik svensson
 * This class is meant to contain all the pertinent information about a given mosaic.
 */
public class Mosaic {
	
	private Pixel[][] imageGrid;
	private Parameters params;
	private Pixel master;
	private boolean complete;
	private ArrayList<MosaicListener> _listeners;
	
	/**
	 * Default constructor (called by WosaicUI).
	 */
	public Mosaic() {
		_listeners = new ArrayList();
	}
	
	/**
	 * Constructor for a mosaic object called by the Controller.
	 * @param param the set of parameters associated with this mosaic.
	 * @param mPixel the master image in Pixel form
	 */
	
	public Mosaic(Parameters param, Pixel mPixel) {
		init(param, mPixel);
		_listeners = new ArrayList();
	}
	
	/**
	 * Accessor for the 2D Pixel array that locally stores the mosaic.
	 * @return the mosaic as a 2D Pixel array
	 */
	public synchronized Pixel[][] getPixelArr() {
		return imageGrid;
	}
	
	public synchronized Pixel getPixelAt(int x, int y) {
		return imageGrid[x][y];
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
		BufferedImage mImage = master.getBufferedImage();
		
		// Calculate the target height/width
		int height = params.getMasterHeight();
		//int height = params.mHeight;
		int width = params.getMasterWidth();
		//int width = params.mWidth;
		
		// Create a writable raster
		Raster raster;
		WritableRaster wr;
		
		//DBG
		//System.out.println("Initializing mosaic rasters...");
		
		try {
			raster = mImage.getData();
			wr = raster.createCompatibleWritableRaster(width, height);
		} catch (Exception e) {
			System.out.println(e);
			//System.out.println("We're running out of memory!");
			return null;
		}
		
		//DBG
		//System.out.println("About to iterate through the mosaic pieces...");
		
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
					//System.out.println("Running out of memory! ... Continuing");
					System.gc();
				}
			}
		}
		
		//DBG
		//System.out.println("Setting the raster data...");
		
		BufferedImage result = null;
		
		try {
			result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
		ArrayList<Point> updatedCoords = new ArrayList<Point>();
		
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
						//_fire(r, c);
						updatedCoords.add(new Point(r,c));
					}
				} else {
					// Just assign this Pixel to this spot
					imageGrid[r][c] = srcPixels;
					
					// Send an update notification
					updatedCoords.add(new Point(r,c));
				}
			}
		}
		if (updatedCoords.size() != 0)
			_fire(updatedCoords);
		
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
	

	public synchronized void addMosaicEventListener(MosaicListener l) {
		_listeners.add(l);
	}
	
	public synchronized void removeMosaicEventListener(MosaicListener l) {
		_listeners.remove(l);
	}
	
	private synchronized void _fire(ArrayList<Point> coords) {
		MosaicEvent e = new MosaicEvent(this, coords);
		Iterator listeners = _listeners.iterator();
		
		while(listeners.hasNext()) {
			((MosaicListener) listeners.next()).mosaicUpdated(e);
		}
	}
}
