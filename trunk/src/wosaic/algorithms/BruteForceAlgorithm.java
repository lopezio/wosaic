/**
 * 
 */
package wosaic.algorithms;

import wosaic.utilities.Mosaic;
import wosaic.utilities.Pixel;

/**
 * Simple "brute force" algorithm. Each time we receive a new pixel, Check each
 * mosaic region and see if the new distance score is better than any existing
 * score, and replace it if it is. This algorithm can be very slow, but is also
 * easy to implement
 * 
 * @author swegner2
 */
public class BruteForceAlgorithm extends AbstractAlgorithm {

	/**
	 * Default constructor-- simple call our superclass constructor
	 * 
	 * @param mos The mosaic to process and fill
	 * @param colorMap Color data for the source pixels
	 */
	public BruteForceAlgorithm(Mosaic mos, int[][][] colorMap) {
		super(mos, colorMap);
	}

	/**
	 * Process a new pixel received from a source plugin. In this algorithm, we
	 * check it against every region in the mosaic and see if it is a better
	 * fit.
	 * 
	 * @param pixel The new source pixel
	 */
	@Override
	public void AddPixel(Pixel pixel) {
		final int[] avgColors = new int[3];
		for (int r = 0; r < Mos.getParams().resRows; r++)
			for (int c = 0; c < Mos.getParams().resCols; c++) {

				pixel.getAvgImageColor(avgColors);
				final int matchScore = getMatchScore(pixel, r, c);

				if (Mos.getPixelAt(r, c) == null)
					// Just assign this Pixel to this spot
					Mos.UpdatePixel(r, c, pixel, matchScore);

				else if (matchScore < Mos.getScoreAt(r, c))
					Mos.UpdatePixel(r, c, pixel, matchScore);
			}

		// FIXME: What does this do?
		notifyAll();
	}

}
