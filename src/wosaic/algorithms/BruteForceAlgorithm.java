/**
 * 
 */
package wosaic.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import wosaic.utilities.Mosaic;
import wosaic.utilities.Pixel;

/**
 * @author swegner2
 *
 */
public class BruteForceAlgorithm extends AbstractAlgorithm {

	public BruteForceAlgorithm(Mosaic mos, int[][][] colorMap) {
		super(mos, colorMap);
	}

	@Override
	public void AddPixel(Pixel pixel) {
		final int[] avgColors = new int[3];
		for (int r = 0; r < Mos.getParams().resRows; r++)
			for (int c = 0; c < Mos.getParams().resCols; c++) {

				pixel.getAvgImageColor(avgColors);
				final int matchScore = getMatchScore(pixel, r, c);

				if (Mos.getPixelAt(r,c) == null)
					// Just assign this Pixel to this spot
					Mos.UpdatePixel(r, c, pixel, matchScore);

				else if (matchScore < Mos.getScoreAt(r,c))
					Mos.UpdatePixel(r,c, pixel, matchScore);
			}
		
		// FIXME: What does this do?
		notifyAll();
	}

}
