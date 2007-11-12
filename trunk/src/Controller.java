
import utilities.Parameters;
import utilities.Pixel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Controller implements Runnable {

	public ArrayList<BufferedImage> sourcesBuffer;
	public int imagesReceived;
	public int targetImages;
	public int numThreads;
	private Thread flickrThread;
	private Thread mosaicThread;
	
	Controller() {
		imagesReceived = 0;
		targetImages = 200;
		numThreads = 10;
		
		sourcesBuffer = new ArrayList<BufferedImage>();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param target total number of flickr images to analyze
	 * @param numThrds the number of images per flickrThread to query
	 */
	Controller(int target, int numThrds) {
		imagesReceived = 0;
		targetImages = target;
		numThreads = numThrds;
		
		sourcesBuffer = new ArrayList<BufferedImage>();
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
		
		// FIXME temporary master source
		Parameters param = new Parameters(50, 50);
		String baseURL = "../images/";
		String mImage = baseURL + "guitar.jpg";
		
		String searchKey = "guitar";
		FlickrService flickr = null;
		ArrayList<BufferedImage> sources;
	
		// Set up a Pixel object for mImage
		Pixel mPixel;
		try {
			mPixel = new Pixel(mImage);
		} catch (Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe just return
			System.out.println(e);
			return;
		}
		
		// Get initial images from flickr
		try {
			flickr = new FlickrService(this);
			//sources = flickr.GetImagePool(searchKey, targetImages);
			sources = flickr.GetImagePool(searchKey, targetImages / numThreads);
		} catch (Exception e) {
			System.out.println("ERROR!  Flickr Failed...");
			System.out.println(e);
			return;
		}
		
		// Start the flickr querying thread
		flickrThread = new Thread(flickr, "Flickr Query Thread");
		flickrThread.setPriority(8);
		flickrThread.start();
		
		// Calculate dimensions of each segment
		if (!param.isInitialized()) {
			param = new Parameters(param.resRows, param.resCols, mPixel.width*2, mPixel.height*2);
		}
		
		// TODO Iterate through the Buffered Images and create Pixel objects
		Pixel[] sourcePixels = new Pixel[sources.size()];
		for (int i=0; i < sources.size(); i++) {
			sourcePixels[i] = new Pixel(sources.get(i));
		}
		
		//Mosaic mosaicProc = new Mosaic();
		//Pixel[][] mosaic = mosaicProc.createMosaic(sourcePixels, mPixel, param);
		
		// Test the threaded implementation
		//Mosaic mProc = new Mosaic(mPixel, param, sourcePixels);
		Mosaic mProc = new Mosaic(mPixel, param, this);
		
		
		mosaicThread = new Thread(mProc, "Mosaic Worker Thread");
		mosaicThread.setPriority(1);
		mosaicThread.start();
		
		// Show how selections change
		/*while (workerThread.isAlive()) {
			System.out.println(mProc.wosaic[0][0]);
		}*/
		
		System.out.println("Controller Exiting!");
	}

}
