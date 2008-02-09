/**
 * 
 */
package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;


/**
 * @author scott
 * A light-weight, callable class that  processes and returns
 * thumbnails of local files
 */
public class FileQuery implements Callable<BufferedImage>{

	private ImageBuffer sourcesBuffer;
	private Node source;
	
	/**
	 * Constructor taking sources buffer and dom node
	 * as parameters.
	 * @param buf the shared buffer instantiated by the controller
	 * @param n the DOM node whose value is the desired source URL
	 */
	public FacebookQuery(ImageBuffer buf, Node n) {
		source = n;
		sourcesBuffer = buf;
	}
	
	/**
	 * Workhorse of the FacebookQuery class.  This downloads the
	 * desired Facebook image from a given URL, and adds it to the
	 * shared buffer instantiated by the Controller.
	 */
	public BufferedImage call() throws Exception {
		
		// Kick off the downloading of images
		System.out.println("img: " + source.getTextContent().trim());
		URL sourceURL = new URL(source.getTextContent().trim());
		
		try {
			BufferedImage img = ImageIO.read(sourceURL);
			sourcesBuffer.addToImageBuffer(img);
		} catch (Exception e) {
			System.out.println("Facebook: Failed to read source URL!");
			System.out.println(e);
			return null;
		}
		
		return null;
	}

}
