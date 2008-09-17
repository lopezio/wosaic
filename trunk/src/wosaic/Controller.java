package wosaic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	// private Thread flickrThread;
	// private Thread fbThread;
	private final ExecutorService ThreadPool;

	private final ArrayList<ActionListener> _listeners;

	private final Mosaic mosaic;

	private static final int THREAD_WAIT_SECS = 3600;

	private Thread mosaicThread;

	private final Pixel mPixel;

	private JAIProcessor mProc;

	private final int numSources;

	private final Parameters param;

	private final Vector<SourcePlugin> Plugins;

	/**
	 * Shared buffer of images from Flickr. The JAIProcessor consumes this
	 * buffer, while the FlickrService produces it.
	 */
	protected ImageBuffer sourcesBuffer;

	private final Status statusObject;

	/**
	 * Constructs a Controller that will handle the mosaic creation process.
	 * Perhaps we should incorporate these parameters into the Parameters
	 * utility object.
	 * 
	 * @param p The parameters associated with the mosaic
	 * @param sourcePixel Source image as a Pixel object
	 * @param mos a reference to the mosaic object which will be operated on
	 * @param sources An array of source plugins to use
	 * @param stat A status object to report progress and events to
	 */
	Controller(final Parameters p, final Pixel sourcePixel, final Mosaic mos,
			final Vector<SourcePlugin> sources, final Status stat) {

		ThreadPool = Executors.newCachedThreadPool();
		mPixel = sourcePixel;
		mosaic = mos;
		Plugins = sources;
		numSources = Plugins.size();
		statusObject = stat;
		_listeners = new ArrayList<ActionListener>();

		// FIXME: We shouldn't have this hardcoded value!!! Get rid of
		// targetImages!
		sourcesBuffer = new ImageBuffer(500, numSources, statusObject);
		param = p;
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
	 * @param l the listener
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
	 * @param l the listener
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

		// Wait for everything to shutdown
		PluginPool.shutdown();
		try {
			PluginPool.awaitTermination(THREAD_WAIT_SECS, TimeUnit.SECONDS);
			ThreadPool.shutdown();
			ThreadPool.awaitTermination(THREAD_WAIT_SECS, TimeUnit.SECONDS);
			// Signal to the JAIProcessor that we've finished
			System.err.println("Interrupting JAIProcessor");
			mosaicThread.interrupt();
			mosaicThread.join();

		} catch (final InterruptedException ex) {
			// This means the user hit cancel-- tell everything to shutdown,
			// and then wait some more.
			ThreadPool.shutdownNow();
			mosaicThread.interrupt();
			try {
				mosaicThread.join();
			} catch (InterruptedException ex2) {
				// We should never get here.
				System.out.println("Controller was interrupted twice!");
			}
		}

		// Signal that we're done
		_fire();
	}

}
