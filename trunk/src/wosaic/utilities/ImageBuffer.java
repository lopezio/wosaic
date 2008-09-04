/**
 * 
 */
package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author carl-erik svensson
 */
public class ImageBuffer {

	private int completionState;
	private int progressState;
	private int currentSize;
	/**
	 * A field specifying whether or not all images have been fetched from
	 * sources. This is set dynamically by comparing the number of images
	 * retrieved with the number we eventually expect.
	 */
	public boolean isComplete;
	private int maxSize;
	private final int numSources;
	private final ArrayList<BufferedImage> sourcesBuffer;

	private final Status statusObject;

	/**
	 * Default constructor.
	 * 
	 * @param sz the maximum number of elements we want to allow in the buffer
	 * @param num the number of sources that feed the buffer
	 * @param stat the shared status object used for reporting progress
	 */
	public ImageBuffer(final int sz, final int num, final Status stat) {
		sourcesBuffer = new ArrayList<BufferedImage>();
		isComplete = false;
		maxSize = 0;
		currentSize = 0;
		numSources = num;
		statusObject = stat;
		statusObject.setIndeterminate(true);
		// statusObject.setProgressLimits(0, maxSize);
	}

	/**
	 * Atomically adds an array of image to the shared image buffer
	 * 
	 * @param img the ArrayList of images to be added
	 * @return returns a status indicator
	 */
	synchronized public boolean addToImageBuffer(
			final ArrayList<BufferedImage> img) {
		if (img != null) {
			sourcesBuffer.addAll(img);
			currentSize += img.size();

			/*
			 * if (currentSize >= maxSize) { isComplete = true;
			 * statusObject.setProgress(maxSize); //
			 * System.out.println("DBG: Setting progress to max!"); } else
			 */

			// statusObject.setProgress(currentSize);
			// System.out.println("DBG: Setting progress bar to have " +
			// currentSize + " size");
			notifyAll();
			return true;
		}

		// else
		return false;

	}

	/**
	 * Atomically adds an image to the shared image buffer
	 * 
	 * @param img the BufferedImage to be added
	 * @return returns a status indicator
	 */
	synchronized public boolean addToImageBuffer(final BufferedImage img) {
		if (img != null) {
			sourcesBuffer.add(img);
			currentSize++;

			/*
			 * if (currentSize >= maxSize) { isComplete = true;
			 * statusObject.setProgress(maxSize); //
			 * System.out.println("DBG: Setting progress to max!"); } else
			 */

			// statusObject.setProgress(currentSize);
			// System.out.println("DBG: Setting progress bar to have " +
			// currentSize + " size");
			notifyAll();
			return true;
		}

		// else
		return false;
	}

	/**
	 * Safely remove an element from the shared image buffer.
	 * 
	 * @return the head element of the buffer
	 * @throws InterruptedException if we receive an interrupt while waiting for
	 *             an image to enter the buffer
	 */
	synchronized public BufferedImage removeFromImageBuffer()
			throws InterruptedException {
		while (size() == 0)
			wait();

		return sourcesBuffer.remove(0);
	}

	/**
	 * Implements a simple state machine to keep track of when all the sources
	 * have finished.
	 */
	public synchronized void signalComplete() {
		completionState++;
		if (completionState == numSources) {
			isComplete = true;
			statusObject.setIndeterminate(false);
			statusObject.setProgress(maxSize);
			// System.out.println("DBG: Setting progress to max!");
		}
	}

	/**
	 * Set the number of expected results we will receive. This is for
	 * displaying a relative percentage for progress
	 * 
	 * @param num Number of images we expect to grab from the source plugin
	 */
	public synchronized void signalProgressCount(int num) {
		progressState++;
		maxSize += num;

		if (progressState == numSources) {
			// statusObject.setIndeterminate(false);
			statusObject.setProgressLimits(0, maxSize);
			statusObject.setProgress(maxSize);
			// System.out.println("DBG: Setting progress to max!");
		}
	}

	/**
	 * Atomic access to the size of the buffer.
	 * 
	 * @return the current size of sourcesBuffer
	 */
	synchronized public int size() {
		return sourcesBuffer.size();
	}

}
