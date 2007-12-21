package wosaic;

import wosaic.utilities.Facebook;
import wosaic.utilities.Mosaic;
import wosaic.utilities.Parameters;
import wosaic.utilities.Pixel;
import wosaic.utilities.ImageBuffer;
import wosaic.exceptions.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author carl
 * The layer of the Wosaic that calls other services to do work, and
 * communicates information between them and the UI.  Basically a
 * level of abstraction.
 */
public class Controller implements Runnable {

	/**
	 * Shared buffer of images from Flickr.  The JAIProcessor consumes this
	 * buffer, while the FlickrService produces it.
	 */
	public ImageBuffer sourcesBuffer;
	public int imagesReceived;
	public int targetImages;
	public int numThreads;
	
	private Thread flickrThread;
	private Thread fbThread;
	public Thread mosaicThread;
	private Parameters param;
	private String searchKey;
	private Pixel mPixel;
	private Mosaic mosaic;
	private boolean useFacebook;
	private boolean useFlickr;
	private int numSources;
	private Facebook fb;
	

	/**
	 * Constructs a Controller that will handle the mosaic creation
	 * process.  Perhaps we should incorporate these parameters into
	 * the Parameters utility object.
	 * 
	 * @param target total number of flickr images to analyze
	 * @param numThrds the number of images per flickrThread to query
	 * @param numRows the desired number of rows in the resulting mosaic
	 * @param numCols the desired number of cols in the resulting mosaic
	 * @param xDim the width of the final mosaic image
	 * @param yDim the height of the final mosaic image
	 * @param search the Flickr search string
	 * @param mImage the filename of the master image
	 * @param mos a reference to the mosaic object which will be operated on
	 * @param fb a flag that indicates whether or not to use facebook.  This should be replaced
	 *        by a vector indicating which sources to use, as we incorporate more sources.
	 */
	Controller(int target, int numThrds, int numRows, int numCols, int xDim, int yDim, String search, 
			String mImage, Mosaic mos, boolean fbFlag, Facebook fbObj, boolean flick, int sources) {
		imagesReceived = 0;
		targetImages = target;
		numThreads = numThrds;
		searchKey = search;
		mosaic = mos;
		useFacebook = fbFlag;
		numSources = sources;
		useFlickr = flick;
		
		// Set up a Pixel object for mImage
		try {
			mPixel = new Pixel(mImage, true);
		} catch (Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe just return
			System.out.println("Unable to create pixel object from source image");
			System.out.println(e);
			return;
		}
		
		sourcesBuffer = new ImageBuffer(targetImages, numSources);
		param = new Parameters(numRows, numCols, xDim, yDim);
		fb = fbObj;
	}
	
	/**
	 * Stops any currently running threads.  This is useful if we need to 
	 * terminate because of an error.
	 */
	public void killThreads() {
		if (mosaicThread.isAlive()) {
			mosaicThread.stop();
		}
		
		if (flickrThread.isAlive()) {
			flickrThread.stop();
		}
	}
	
	/**
	 * Allow the mosaic thread to sleep for some time.
	 * 
	 * @param millis time to sleep in milliseconds
	 */
	public void sleepWorker(long millis) {
		try {
			mosaicThread.sleep(millis);
		} catch (Exception e) {
			System.out.println("MosaicThrd woke up prematurely");
		}
	}
	
	public Mosaic getMosaic() {
		return mosaic;
	}
	
	public JAIProcessor mProc;
	
	/**
	 * Controls communication between JAI processing and Flickr API.
	 */
	public void run() {

		System.out.println("Running Controlling Thread!");
		FlickrService2 flickr = null;
		
		// Start the flickr querying thread
		if (useFlickr) {
			try {
				flickr = new FlickrService2(sourcesBuffer, targetImages, searchKey);
			} catch (FlickrServiceException ex) {
				System.out.println("Error starting FlickrService: " + ex.getMessage());
				System.out.println(ex.getCause().getMessage());
			}
			
			System.out.println("Starting Flickr Thread!");
			flickrThread = new Thread(flickr, "Flickr Query Thread");
			flickrThread.setPriority(8);
			flickrThread.start();
		}
		
		// Start the Facebook querying thread
		if (useFacebook) {
			System.out.println("Starting Facebook Thread!");
			fb.setBuffer(sourcesBuffer);
			fbThread = new Thread(fb, "Facebook Query Thread");
			fbThread.start();
		}
		
		// Initialize the mosaic object
		mosaic.init(param, mPixel);
		
		// Start the processing thread
		mProc = new JAIProcessor(mPixel, param, sourcesBuffer, mosaic);
		mosaicThread = new Thread(mProc, "JAIProcessor Worker Thread");
		mosaicThread.setPriority(1);
		mosaicThread.start();
		
		// Update the user display with current mosaic tiles
		/*while (workerThread.isAlive()) {
			System.out.println(mProc.wosaic[0][0]);
		}*/
		
		System.out.println("Controller Exiting!");
	}

}
