/**
 * 
 */
package wosaic.utilities;

import java.util.concurrent.ExecutorService;

import javax.swing.JDialog;

import wosaic.Sources;

/**
 * An abstract class to image sources. Plugins can implement this class and
 * easily integrate into Wosaic.
 */
public abstract class SourcePlugin implements Runnable {

	/**
	 * Determines how many images to use from this source.
	 */
	protected int numResults;

	/**
	 * The ImageBuffer where result images should be pushed to
	 */
	protected ImageBuffer sourcesBuffer = null;

	private Status statusObject;

	/**
	 * A shared ThreadPool where child worker threads should be queried
	 */
	protected ExecutorService ThreadPool;

	/**
	 * This is provided to allow a source to specify its own configurable
	 * options. Each source can provide its own JPanel and event handlers to set
	 * its options.
	 * 
	 * @return a JDialog with configurable options.
	 */
	abstract public JDialog getOptionsDialog();

	/**
	 * This is used as a method of determining what kind of source a
	 * SourcePlugin is.
	 * 
	 * @return the string representation of this source.
	 */
	abstract public Sources.Plugin getType();

	/**
	 * Provides an interface for printing status messages.
	 * 
	 * @param stat the message to be printed.
	 */
	public void reportStatus(final String stat) {
		if (statusObject != null) statusObject.setStatus(stat);
	}

	/**
	 * This is the worker thread for the source. It must populate the
	 * sourcesBuffer with BufferedImages in order to have its contents processed
	 * in the mosaic.
	 */
	abstract public void run();

	/**
	 * This is required for setting the shared buffer for all the sources.
	 * 
	 * @param buf The shared image buffer
	 */
	public void setBuffer(final ImageBuffer buf) {
		sourcesBuffer = buf;
	}

	/**
	 * This is required for setting the shared buffer for all the sources.
	 * 
	 * @param tp - a previously instantiated executor service containing threads
	 *            for use in this source.
	 */
	public void setPool(final ExecutorService tp) {
		ThreadPool = tp;
	}

	/**
	 * Set the search string used by the given plugin. Note that not all plugins
	 * will need to use this.
	 * 
	 * @param s The new string to search for.
	 */
	public void setSearchString(final String s) {
	// By default, do nothing
	}

	/**
	 * Defines the how a source can post the status of its running.
	 * 
	 * @param obj the shared reference to a Status object
	 */
	public void setStatusObject(final Status obj) {
		statusObject = obj;
	}

	/**
	 * Call this to validate this source's parameters.
	 * 
	 * @return an error message if the parameters aren't valid, or null
	 */
	abstract public String validateParams();

}
