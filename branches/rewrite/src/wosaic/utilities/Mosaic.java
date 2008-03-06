/**
 * 
 */
package wosaic.utilities;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author carl-erik svensson This class is meant to contain all the pertinent
 *         information about a given mosaic.
 */
public class Mosaic {

    private ArrayList<MosaicListener> _listeners;

    private Pixel[][] imageGrid;

    private Pixel master;

    private Parameters params;

    /**
         * Constructor for a mosaic object called by the Controller.
         * 
         * @param param
         *                the set of parameters associated with this mosaic.
         * @param mPixel
         *                the master image in Pixel form
         */

    public Mosaic(final Parameters param, final Pixel mPixel) {
	init(param, mPixel);
	_listeners = new ArrayList<MosaicListener>();
    }

    private synchronized void _fire(final ArrayList<Point> coords, Pixel pixel) {
	final MosaicEvent e = new MosaicEvent(this, coords, pixel);
	final Iterator<MosaicListener> listeners = _listeners.iterator();

	while (listeners.hasNext()) {
	    listeners.next().mosaicUpdated(e);
	}
    }

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
	final BufferedImage mImage = master.getBufferedImage();

	// Calculate the target height/width
	final int height = params.getMasterHeight();
	// int height = params.mHeight;
	final int width = params.getMasterWidth();
	// int width = params.mWidth;

	// Create a writable raster
	Raster raster;
	WritableRaster wr;

	// DBG
	// System.out.println("Initializing mosaic rasters...");

	try {
	    raster = mImage.getData();
	    wr = raster.createCompatibleWritableRaster(width, height);
	} catch (final Exception e) {
	    System.out.println(e);
	    // System.out.println("We're running out of memory!");
	    return null;
	}

	// DBG
	// System.out.println("About to iterate through the mosaic pieces...");

	// Create the resulting image!
	for (int r = 0; r < params.resRows; r++) {
	    for (int c = 0; c < params.resCols; c++) {

		try {
		    // Scale the source
		    sources[r][c].scaleSource(params.sWidth, params.sHeight);

		    // Copy the pixels
		    wr.setRect(c * sources[r][c].width, r
			    * sources[r][c].height, sources[r][c].getRaster());
		} catch (final Exception e) {
		    System.out.println(e);
		    // System.out.println("Running out of memory! ...
                        // Continuing");
		    System.gc();
		}
	    }
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
         * Accessor for the 2D Pixel array that locally stores the mosaic.
         * 
         * @return the mosaic as a 2D Pixel array
         */
    public synchronized Pixel[][] getPixelArr() {
	return imageGrid;
    }

    public synchronized Pixel getPixelAt(final int x, final int y) {
	return imageGrid[x][y];
    }

    /**
         * Initializes a mosaic object. A Mosaic object must be initialized
         * before it can be used in computation.
         * 
         * @param param
         *                the set of parameters associated with this mosaic
         * @param mPixel
         *                mPixel the master image in Pixel form
         */
    public void init(final Parameters param, final Pixel mPixel) {
	params = param;
	master = mPixel;
	imageGrid = new Pixel[params.resRows][params.resCols];
    }

    public synchronized void removeMosaicEventListener(final MosaicListener l) {
	_listeners.remove(l);
    }

    /**
         * Writes an image to the specified file.
         * 
         * @param img
         *                the image to be written to disk
         * @param file
         *                the filename for the image
         * @param type
         *                the encoding for the image
         * @throws IOException
         */

    public void save(final BufferedImage img, final String file, final String type)
	    throws IOException {
	final FileOutputStream os = new FileOutputStream(file);
	final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
	encoder.encode(img);
	os.close();
    }

    /**
         * Finds the best spot(s) to put the parameter Pixel object.
         * 
         * @param srcPixel
         *                the pixel to place in the mosaic
         * @param colorMap
         *                the 3D array containing color information about the
         *                master image
         */
    public synchronized void updateMosaic(final Pixel srcPixel, final int[][][] colorMap) {
	// Check all the segments to see where this might fit
	final ArrayList<Point> updatedCoords = new ArrayList<Point>();

	for (int r = 0; r < params.resRows; r++) {
	    for (int c = 0; c < params.resCols; c++) {

		final int rmDiff = Math
			.abs(srcPixel.avgColor[0] - colorMap[r][c][0]);
		final int gmDiff = Math
			.abs(srcPixel.avgColor[1] - colorMap[r][c][1]);
		final int bmDiff = Math
			.abs(srcPixel.avgColor[2] - colorMap[r][c][2]);

		// Keep a score that dictates how good a match is
		// Like in golf, a lower score is better. This is simply
		// made up of the total difference in each channel, added
		// together. Other weights can be added in the future.
		final int matchScore = rmDiff + gmDiff + bmDiff;

		if (imageGrid[r][c] != null) {
		    // Calculate the score of the Pixel in this spot
		    final int rsDiff = Math.abs(imageGrid[r][c].avgColor[0]
			    - colorMap[r][c][0]);
		    final int gsDiff = Math.abs(imageGrid[r][c].avgColor[1]
			    - colorMap[r][c][1]);
		    final int bsDiff = Math.abs(imageGrid[r][c].avgColor[2]
			    - colorMap[r][c][2]);

		    final int score = rsDiff + gsDiff + bsDiff;

		    if (matchScore < score) {
			imageGrid[r][c] = srcPixel;

			// Send an update notification
			// something like notifyUI(r, c, imageGrid)
			// but should that be synchronized? It may slow stuff
                        // down
			// _fire(r, c);
			updatedCoords.add(new Point(r, c));
		    }
		} else {
		    // Just assign this Pixel to this spot
		    imageGrid[r][c] = srcPixel;

		    // Send an update notification
		    updatedCoords.add(new Point(r, c));
		}
	    }
	}
	if (updatedCoords.size() != 0)
	    _fire(updatedCoords, srcPixel);

	notifyAll();
    }
}
