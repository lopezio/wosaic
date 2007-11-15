
import utilities.Parameters;
import utilities.Pixel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Controller implements Runnable {

	/**
	 * Shared buffer of images from Flickr.  The JAIProcessor consumes this
	 * buffer, while the FlickrService produces it.
	 */
	public ArrayList<BufferedImage> sourcesBuffer;
	public int imagesReceived;
	public int targetImages;
	public int numThreads;
	
	private Thread flickrThread;
	private Thread mosaicThread;
	private Parameters param;
	private String searchKey;
	private Pixel mPixel;
	
	Controller() {
		imagesReceived = 0;
		targetImages = 50;
		numThreads = 10;
		
		// FIXME temporary master source
		String baseURL = "../images/";
		String mImage = baseURL + "guitar.jpg";
		searchKey = "guitar";
	
		// Set up a Pixel object for mImage
		try {
			mPixel = new Pixel(mImage);
		} catch (Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe just return
			System.out.println("Unable to create pixel object from source image");
			System.out.println(e);
			return;
		}
		
		sourcesBuffer = new ArrayList<BufferedImage>();
		param = new Parameters(20, 20, mPixel.width, mPixel.height);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param target total number of flickr images to analyze
	 * @param numThrds the number of images per flickrThread to query
	 */
	Controller(int target, int numThrds, int numRows, int numCols, int xDim, int yDim, String search, BufferedImage mImage) {
		imagesReceived = 0;
		targetImages = target;
		numThreads = numThrds;
		searchKey = search;
		
		// Set up a Pixel object for mImage
		try {
			mPixel = new Pixel(mImage);
		} catch (Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe just return
			System.out.println("Unable to create pixel object from source image");
			System.out.println(e);
			return;
		}
		
		sourcesBuffer = new ArrayList<BufferedImage>();
		param = new Parameters(numRows, numCols, xDim, yDim);
	}
	
	/**
	 * Atomically adds an image to the shared image buffer
	 * 
	 * @param img the ArrayList of images to be added
	 * @return returns a status indicator
	 */
	synchronized public boolean addToImageBuffer(ArrayList<BufferedImage> img) {
		if (img != null) {
			sourcesBuffer.addAll(img);
			imagesReceived += img.size();
		}
		
		return true;
	}
	
	/**
	 * Safely remove an element from the shared image buffer.
	 * @return the head element of the buffer
	 */
	synchronized public BufferedImage removeFromImageBuffer() {
		return sourcesBuffer.remove(0);
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
	
	/**
	 * Controls communication between JAI processing and Flickr API.
	 */
	public void run() {

		System.out.println("Starting Controlling Thread!");
		FlickrService flickr = null;
		
		// FIXME make some Flickr initialization calls 
		// separate from getting the first set of images
		try {
			flickr = new FlickrService(this);
			//sources = flickr.GetImagePool(searchKey, targetImages);
			flickr.GetImagePool(searchKey, targetImages / numThreads);
		} catch (Exception e) {
			System.out.println("ERROR!  Flickr Failed...");
			System.out.println(e);
			return;
		}
		
		// Start the flickr querying thread
		flickrThread = new Thread(flickr, "Flickr Query Thread");
		flickrThread.setPriority(8);
		flickrThread.start();

		// Start the processing thread
		JAIProcessor mProc = new JAIProcessor(mPixel, param, this);
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
