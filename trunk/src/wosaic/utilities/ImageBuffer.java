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
	public boolean isComplete;
	
	/**
	 * Default constructor.
	 */
	public ImageBuffer() {
		sourcesBuffer = new ArrayList<BufferedImage>();
		isComplete = false;
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

}
