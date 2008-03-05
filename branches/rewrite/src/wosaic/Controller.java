package wosaic;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wosaic.utilities.ImageBuffer;
import wosaic.utilities.Mosaic;
import wosaic.utilities.Parameters;
import wosaic.utilities.Pixel;
import wosaic.utilities.SourcePlugin;
import wosaic.utilities.Status;

/**
 * @author carl The layer of the Wosaic that calls other services to do work,
 *         and communicates information between them and the UI. Basically a
 *         level of abstraction.
 */
public class Controller implements Runnable {

	protected static final int THREAD_POOL = 30;

	private final ArrayList<ActionListener> _listeners;

	protected int imagesReceived;

	private Mosaic mosaic;

	protected Thread mosaicThread;

	private Pixel mPixel;

	protected JAIProcessor mProc;

	private int numSources;

	protected int numThreads;

	private Parameters param;

	private Sources sources;

	/**
	 * Shared buffer of images from Flickr. The JAIProcessor consumes this
	 * buffer, while the FlickrService produces it.
	 */
	protected ImageBuffer sourcesBuffer;

	private Status statusObject;

	protected int targetImages;

	private Thread thread;

	// private Thread flickrThread;
	// private Thread fbThread;
	private ExecutorService ThreadPool;

	/**
	 * Constructs a Controller that will handle the mosaic creation process.
	 * Perhaps we should incorporate these parameters into the Parameters
	 * utility object.
	 * 
	 * @param target
	 *            total number of flickr images to analyze
	 * @param numThrds
	 *            the number of images per flickrThread to query
	 * @param numRows
	 *            the desired number of rows in the resulting mosaic
	 * @param numCols
	 *            the desired number of cols in the resulting mosaic
	 * @param xDim
	 *            the width of the final mosaic image
	 * @param yDim
	 *            the height of the final mosaic image
	 * @param search
	 *            the Flickr search string
	 * @param mImage
	 *            the filename of the master image
	 * @param mos
	 *            a reference to the mosaic object which will be operated on
	 * @param fb
	 *            a flag that indicates whether or not to use facebook. This
	 *            should be replaced by a vector indicating which sources to
	 *            use, as we incorporate more sources.
	 */
	Controller(final int target, final int numThrds, final int numRows,
			final int numCols, final int xDim, final int yDim,
			final String search, final String mImage, final Mosaic mos,
			final Sources src, final Status stat) {
		imagesReceived = 0;
		mosaic = mos;
		sources = src;
		numSources = src.getEnabledSources().size();
		statusObject = stat;
		_listeners = new ArrayList<ActionListener>();

		// Set up a Pixel object for mImage
		try {
			mPixel = new Pixel(mImage, true);
		} catch (final Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe
			// just return
			System.out
					.println("Unable to create pixel object from source image");
			System.out.println(e.getCause());
			System.out.println(e);

			return;
		}

		sourcesBuffer = new ImageBuffer(targetImages, numSources, statusObject);
		param = new Parameters(numRows, numCols, xDim, yDim);
		ThreadPool = Executors.newFixedThreadPool(Controller.THREAD_POOL);
	}

	/**
	 * Add a listener that will receive an event when the Mosaic is finished
	 * processing
	 * 
	 * @param l
	 *            the listener
	 */
	public synchronized void addActionListener(final ActionListener l) {
		_listeners.add(l);
	}

	/**
	 * Public getter to retrieve the created mosaic object
	 * 
	 * @return the generated mosaic
	 */
	public Mosaic getMosaic() {
		return mosaic;
	}

	/**
	 * Remove a listener from the action listeners list
	 * 
	 * @param l
	 *            the listener
	 */
	public synchronized void removeActionListener(final ActionListener l) {
		_listeners.remove(l);
	}

	/**
	 * Controls communication between JAI processing and Flickr API.
	 */
	public void run() {

		// System.out.println("Running Controlling Thread!");

		// Setup and run sources
		final ArrayList<SourcePlugin> srcList = sources.getEnabledSources();

		for (int i = 0; i < numSources; i++) {
			final SourcePlugin src = srcList.get(i);
			src.setBuffer(sourcesBuffer);
			src.setPool(ThreadPool);
			thread = new Thread(src, src.getType() + " Thread");
			thread.start();
		}

		// Initialize the mosaic object
		mosaic.init(param, mPixel);

		// Start the processing thread
		mProc = new JAIProcessor(mPixel, param, sourcesBuffer, mosaic,
				statusObject);
		mosaicThread = new Thread(mProc, "JAIProcessor Worker Thread");
		mosaicThread.setPriority(1);
		mosaicThread.start();

		// System.out.println("Controller Exiting!");
	}

}
