/**
 * 
 */
package wosaic.utilities;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * This class is meant to contain all the pertinent information about a given
 * mosaic.
 * 
 * @author carl-erik svensson
 */
public class Mosaic {

	private final ArrayList<MosaicListener> _listeners;

	private Pixel[][] imageGrid;

	private Pixel master;

	private Parameters params;

	private int[][] scoreGrid;

	/**
	 * Constructor for a mosaic object called by the Controller.
	 * 
	 * @param param the set of parameters associated with this mosaic.
	 * @param mPixel the master image in Pixel form
	 */

	public Mosaic(final Parameters param, final Pixel mPixel) {
		init(param, mPixel);
		_listeners = new ArrayList<MosaicListener>();
	}

	private synchronized void _fire(final ArrayList<Point> coords,
			final Pixel pixel) {
		final MosaicEvent e = new MosaicEvent(this, coords, pixel);
		final Iterator<MosaicListener> listeners = _listeners.iterator();

		while (listeners.hasNext())
			listeners.next().mosaicUpdated(e);
	}

	/**
	 * Add a new listener for Mosaic change events
	 * 
	 * @param l The new MosaicListener
	 */
	public synchronized void addMosaicEventListener(final MosaicListener l) {
		_listeners.add(l);
	}

	/**
	 * Creates a BufferedImage of the final mosaic from the input sources.
	 * 
	 * @return the mosaic stitched together in BufferedImage format
	 */
	public BufferedImage createImage() {

		final Pixel[][] sources = imageGrid;

		// Calculate the target height/width
		final int height = params.getMasterHeight();
		final int width = params.getMasterWidth();

		// Create a writable raster
		WritableRaster wr;

		try {
			wr = master.getImageRaster().createCompatibleWritableRaster(width,
					height);
		} catch (final Exception e) {
			System.out.println(e);
			// System.out.println("We're running out of memory!");
			return null;
		}

		// Dimensions of each segment
		final int sWidth = params.sWidth;
		final int sHeight = params.sHeight;

		// Create the resulting image!
		for (int r = 0; r < params.resRows; r++)
			for (int c = 0; c < params.resCols; c++)
				try {
					// Copy the pixels
					wr.setRect(c * sWidth, r * sHeight, sources[r][c]
							.getScaledImgRaster(sWidth, sHeight));

				} catch (final Exception e) {
					System.out.println(e);
					// System.out.println("Running out of memory! ...
					// Continuing");
					System.gc();
				}

		// DBG
		// System.out.println("Setting the raster data...");

		BufferedImage result = null;

		try {
			result = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			result.setData(wr);
		} catch (final Exception e) {
			System.out.println("Writing result failed!");
			System.out.println(e);
		}

		return result;
	}

	/**
	 * Accessor for this mosaic's parameters.
	 * 
	 * @return Parameters for this mosaic.
	 */
	public Parameters getParams() {
		return params;
	}

	/**
	 * Accessor for the 2D Pixel array that locally stores the mosaic.
	 * 
	 * @return the mosaic as a 2D Pixel array
	 */
	public synchronized Pixel[][] getPixelArr() {
		return imageGrid;
	}

	/**
	 * Retrieve the Pixel object at a particular coordinate of the Mosaic object
	 * 
	 * @param x The x dimension
	 * @param y The y dimension
	 * @return The Pixel at a given coordinate, or null if none exists
	 */
	public synchronized Pixel getPixelAt(final int x, final int y) {
		return imageGrid[x][y];
	}

	/**
	 * Retrieve the current distance score for a particular coordinate
	 * 
	 * @param x The x dimension
	 * @param y The y dimension
	 * @return The current score
	 */
	public synchronized int getScoreAt(final int x, final int y) {
		return scoreGrid[x][y];
	}

	/**
	 * Initializes a mosaic object. A Mosaic object must be initialized before
	 * it can be used in computation.
	 * 
	 * @param param the set of parameters associated with this mosaic
	 * @param mPixel mPixel the master image in Pixel form
	 */
	public void init(final Parameters param, final Pixel mPixel) {
		params = param;
		master = mPixel;
		imageGrid = new Pixel[params.resRows][params.resCols];
		scoreGrid = new int[params.resRows][params.resCols];
	}

	/**
	 * Test whether we have a valid Mosaic or not. This is determined by whether
	 * we have a true Pixel in each grid position
	 * 
	 * @return true if we have a valid Mosaic, or false otherwise
	 */
	public boolean isValid() {
		// We can assume we have a Pixel in each square if we have a Pixel in
		// the last square.
		final int lastx = imageGrid.length - 1;
		final int lasty = imageGrid[lastx].length - 1;
		return imageGrid[lastx][lasty] != null;
	}

	/**
	 * Remove an object from our list of registered listeners. This assumes that
	 * the listener being removed was actually added first.
	 * 
	 * @param l the listener to remove
	 */
	public synchronized void removeMosaicEventListener(final MosaicListener l) {
		_listeners.remove(l);
	}

	/**
	 * Writes an image to the specified file.
	 * 
	 * @param img the image to be written to disk
	 * @param file the filename for the image
	 * @param type the encoding for the image
	 * @throws IOException If the file doesn't exist or is not readable
	 */

	public void save(final BufferedImage img, final String file,
			final String type) throws IOException {
		final FileOutputStream os = new FileOutputStream(file);
		final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(img);
		os.close();
	}

	/**
	 * Take a total width and height for the new output dimension of the mosaic,
	 * and set the parameters object accordingly.
	 * 
	 * @param width The new output width
	 * @param height The new output height
	 */

	public void setOutputSize(final int width, final int height) {
		params.setSectionSize(width, height);
	}

	/**
	 * Finds the best spot(s) to put the parameter Pixel object.
	 * 
	 * @param srcPixel the pixel to place in the mosaic
	 * @param colorMap the 3D array containing color information about the
	 *            master image
	 */
	public synchronized void updateMosaic(final Pixel srcPixel,
			final int[][][] colorMap) {
		// Check all the segments to see where this might fit
		final ArrayList<Point> updatedCoords = new ArrayList<Point>();

		final int[] avgColors = new int[3];
		for (int r = 0; r < params.resRows; r++)
			for (int c = 0; c < params.resCols; c++) {

				srcPixel.getAvgImageColor(avgColors);
				final int rmDiff = Math.abs(avgColors[0] - colorMap[r][c][0]);
				final int gmDiff = Math.abs(avgColors[1] - colorMap[r][c][1]);
				final int bmDiff = Math.abs(avgColors[2] - colorMap[r][c][2]);

				// Keep a score that dictates how good a match is
				// Like in golf, a lower score is better. This is simply
				// made up of the total difference in each channel, added
				// together. Other weights can be added in the future.
				// Try RMS difference
				final int matchScore = (int) Math.sqrt((Math.pow(rmDiff, 2.0) + Math.pow(gmDiff, 2.0) + Math.pow(bmDiff, 2.0)));

				if (imageGrid[r][c] != null) {

					if (matchScore < scoreGrid[r][c]) {
						imageGrid[r][c] = srcPixel;
						scoreGrid[r][c] = matchScore;
						updatedCoords.add(new Point(r, c));
					}

				} else {
					// Just assign this Pixel to this spot
					imageGrid[r][c] = srcPixel;
					scoreGrid[r][c] = matchScore;

					// Send an update notification
					updatedCoords.add(new Point(r, c));
				}
			}
		if (updatedCoords.size() != 0) _fire(updatedCoords, srcPixel);

		notifyAll();
	}

	/**
	 * Update the Pixel in a given coordinate with a new one.
	 * 
	 * @param row The row of the given coordinate
	 * @param col The column of the given coordinate
	 * @param newPixel The new pixel object to use
	 * @param score The score of the new match
	 */
	public synchronized void UpdatePixel(int row, int col,
			final Pixel newPixel, int score) {
		imageGrid[row][col] = newPixel;
		scoreGrid[row][col] = score;
	}
}
