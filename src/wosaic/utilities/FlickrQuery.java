package wosaic.utilities;

import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Generate a BufferedImage from the Flickr response URL, and send it to the
 * sourcesBuffer
 * 
 * @author scott
 */
public class FlickrQuery implements Runnable {

	/**
	 * The URL to query for an image
	 */
	protected String urlString;

	/**
	 * The Buffer to send our image results to
	 */
	protected ImageBuffer Buffer;

	/**
	 * Default constructor, create our query
	 * 
	 * @param url the URL to look for a picture at
	 * @param buffer the buffer to send our results to
	 */
	public FlickrQuery(final String url, final ImageBuffer buffer) {
		urlString = url;
		Buffer = buffer;
	}

	/**
	 * Start our processing. This is meant to be called in its own thread.
	 * Download the image from the URL, and create a BufferedImage from it.
	 */
	public void run() {
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (Exception ex) {
			System.out.println("Cannot parse URL");
			return;
		}

		try {
			Buffer.addToImageBuffer(ImageIO.read(url));
		} catch (Exception ex) {
			// Do nothing-- if we receive a bad picture from Flickr,
			// there's not much we can do about it.
		}
	}
}
