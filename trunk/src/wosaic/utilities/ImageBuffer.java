/**
 * 
 */
package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author carl-erik svensson
 *
 */
public class ImageBuffer {

	private ArrayList<BufferedImage> sourcesBuffer;
	private int maxSize;
	private int currentSize;
	private int numSources;
	private int completionState;
	public boolean isComplete;
	
	/**
	 * Default constructor.
	 * @param sz the maximum number of elements we want to allow in the buffer
	 * @param num the number of sources that feed the buffer
	 */
	public ImageBuffer(int sz, int num) {
		sourcesBuffer = new ArrayList<BufferedImage>();
		isComplete = false;
		maxSize = sz;
		currentSize = 0;
		numSources = num;
	}
	
	/**
	 * Atomically adds an array of image to the shared image buffer
	 * 
	 * @param img the ArrayList of images to be added
	 * @return returns a status indicator
	 */
	synchronized public boolean addToImageBuffer(ArrayList<BufferedImage> img) {
		if (img != null) {
			sourcesBuffer.addAll(img);
			currentSize += img.size();
			
			if (currentSize >= maxSize) {
				isComplete = true;
			}
			
			notifyAll();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Atomically adds an image to the shared image buffer
	 * 
	 * @param img the BufferedImage to be added
	 * @return returns a status indicator
	 */
	synchronized public boolean addToImageBuffer(BufferedImage img) {
		if (img != null) {
			sourcesBuffer.add(img);
			currentSize++;
			
			if (currentSize >= maxSize) {
				isComplete = true;
			}
			
			notifyAll();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Safely remove an element from the shared image buffer.
	 * @return the head element of the buffer
	 */
	synchronized public BufferedImage removeFromImageBuffer() {
		while (size() == 0) {
			try {
				wait();
			} catch (Exception e) {
				System.out.println("removeFromImageBuffer was possible interupted!");
				System.out.println(e);
			}
		}
		
		return sourcesBuffer.remove(0);
	}
	
	/**
	 * Atomic access to the size of the buffer.
	 * 
	 * @return the current size of sourcesBuffer
	 */
	synchronized public int size() {
		return sourcesBuffer.size();
	}
	
	/**
	 * Implements a simple state machine to keep track of when
	 * all the sources have finished.
	 */
	public synchronized void signalComplete() {
		completionState++;
		if (completionState == numSources) {
			isComplete = true;
		}
	}

}
