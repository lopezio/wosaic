package wosaic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
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

	private final Mosaic mosaic;

	protected Thread mosaicThread;

	private final Pixel mPixel;

	protected JAIProcessor mProc;

	private final int numSources;

	protected int numThreads;

	private final Parameters param;

	private final Vector<SourcePlugin> Plugins;

	/**
	 * Shared buffer of images from Flickr. The JAIProcessor consumes this
	 * buffer, while the FlickrService produces it.
	 */
	protected ImageBuffer sourcesBuffer;

	private final Status statusObject;

	// private Thread flickrThread;
	// private Thread fbThread;
	private final ExecutorService ThreadPool;

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
	 *            should be replaced by a vector indicating which Plugins to
	 *            use, as we incorporate more Plugins.
	 */
	Controller(final Parameters p, final Pixel sourcePixel, final Mosaic mos,
			final Vector<SourcePlugin> sources, final Status stat) {

		mPixel = sourcePixel;
		imagesReceived = 0;
		mosaic = mos;
		Plugins = sources;
		numSources = Plugins.size();
		statusObject = stat;
		_listeners = new ArrayList<ActionListener>();

		// FIXME: We shouldn't have this hardcoded value!!! Get rid of
		// targetImages!
		sourcesBuffer = new ImageBuffer(500, numSources, statusObject);
		param = p;
		ThreadPool = Executors.newFixedThreadPool(Controller.THREAD_POOL);
	}

	private synchronized void _fire() {
		final ActionEvent e = new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, "Mosaic Complete!");
		final Iterator<ActionListener> listeners = _listeners.iterator();

		while (listeners.hasNext())
			listeners.next().actionPerformed(e);
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
		final ExecutorService PluginPool = Executors.newCachedThreadPool();
		for (final SourcePlugin source : Plugins) {
			source.setBuffer(sourcesBuffer);
			source.setPool(ThreadPool);
			PluginPool.submit(source);
		}

		// Start the processing thread
		mProc = new JAIProcessor(mPixel, param, sourcesBuffer, mosaic,
				statusObject);
		mosaicThread = new Thread(mProc, "JAIProcessor Worker Thread");
		mosaicThread.setPriority(1);
		mosaicThread.start();
		try {
			mosaicThread.join();
		} catch (final InterruptedException ex) {
			PluginPool.shutdownNow();
			ThreadPool.shutdownNow();
			mosaicThread.interrupt();
		}

		// Signal that we're done
		_fire();
	}

}
