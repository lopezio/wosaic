package wosaic;

import wosaic.utilities.Mosaic;
import wosaic.utilities.Parameters;
import wosaic.utilities.Pixel;
import wosaic.utilities.ImageBuffer;
import wosaic.utilities.SourcePlugin;
import wosaic.utilities.Status;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	protected ImageBuffer sourcesBuffer;
	protected int imagesReceived;
	protected int targetImages;
	protected int numThreads;
	
	protected static final int THREAD_POOL = 30;
	
	//private Thread flickrThread;
	//private Thread fbThread;
	private ExecutorService ThreadPool;
	protected Thread mosaicThread;
	private Parameters param;
	private Pixel mPixel;
	private Mosaic mosaic;
	private int numSources;
	private Sources sources;
	private Thread thread;
	private Status statusObject;
	

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
			String mImage, Mosaic mos, Sources src, Status stat) {
		imagesReceived = 0;
		mosaic = mos;
		sources = src;
		numSources = src.getEnabledSources().size();
		statusObject = stat;
		
		// Set up a Pixel object for mImage
		try {
			mPixel = new Pixel(mImage, true);
		} catch (Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe just return
			System.out.println("Unable to create pixel object from source image");
			System.out.println(e.getCause());
			System.out.println(e);
			
			return;
		}
		
		sourcesBuffer = new ImageBuffer(targetImages, numSources, statusObject);
		param = new Parameters(numRows, numCols, xDim, yDim);
		ThreadPool = Executors.newFixedThreadPool(THREAD_POOL);
	}
	
	
	/**
	 * Public getter to retrieve the created mosaic object
	 * @return the generated mosaic
	 */
	public Mosaic getMosaic() {
		return mosaic;
	}
	
	protected JAIProcessor mProc;
	
	/**
	 * Controls communication between JAI processing and Flickr API.
	 */
	public void run() {

		//System.out.println("Running Controlling Thread!");
		
		// Setup and run sources
		ArrayList<SourcePlugin> srcList = sources.getEnabledSources();
		
		for(int i = 0; i < numSources; i++) {
			SourcePlugin src = srcList.get(i);
			src.setBuffer(sourcesBuffer);
			src.setPool(ThreadPool);
			thread = new Thread(src, src.getType() + " Thread");
			thread.start();
		}

		
		// Initialize the mosaic object
		mosaic.init(param, mPixel);
		
		// Start the processing thread
		mProc = new JAIProcessor(mPixel, param, sourcesBuffer, mosaic, statusObject);
		mosaicThread = new Thread(mProc, "JAIProcessor Worker Thread");
		mosaicThread.setPriority(1);
		mosaicThread.start();

		//System.out.println("Controller Exiting!");
	}

}
