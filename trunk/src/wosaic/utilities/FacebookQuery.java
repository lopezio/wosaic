/**
 * 
 */
package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.w3c.dom.Node;

/**
 * @author carl-eriksvensson A light-weight, callable class that downloads a
 *         requested Facebook picture.
 */
public class FacebookQuery implements Runnable {

	private final Node source;

	private final ImageBuffer sourcesBuffer;

	/**
	 * Constructor taking sources buffer and dom node as parameters.
	 * 
	 * @param buf the shared buffer instantiated by the controller
	 * @param n the DOM node whose value is the desired source URL
	 */
	public FacebookQuery(final ImageBuffer buf, final Node n) {
		source = n;
		sourcesBuffer = buf;
	}

	/**
	 * Workhorse of the FacebookQuery class. This downloads the desired Facebook
	 * image from a given URL, and adds it to the shared buffer instantiated by
	 * the Controller.
	 */
	public void run() {

		// Kick off the downloading of images

		try {
			final URL sourceURL = new URL(source.getTextContent().trim());
			final BufferedImage img = ImageIO.read(sourceURL);
			sourcesBuffer.addToImageBuffer(img);
		} catch (final Exception e) {
			System.out.println("Facebook: Failed to read source URL!");
			System.out.println(e);
			return;
		}
	}

}
