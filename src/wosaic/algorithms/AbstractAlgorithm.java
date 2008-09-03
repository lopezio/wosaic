/**
 * 
 */
package wosaic.algorithms;

import wosaic.utilities.*;

/**
 * Represents the base class for all mosaic matching algorithms.
 * Each algorithm is responsible for analyzing a pool of images from
 * a plugin and fitting them appropriately into the mosaic.
 * @author swegner2
 */
public abstract class AbstractAlgorithm {
	public AbstractAlgorithm(Mosaic mos, int[][][] colorMap) { 
		Mos = mos; 
		ColorMap = colorMap;
	}
	abstract public void AddPixel(Pixel pixel);

	protected Mosaic Mos;
	protected int[][][] ColorMap;
	
	protected int getMatchScore(Pixel pixel, int r, int c) {
		int[] avgColors = new int[3];
		pixel.getAvgImageColor(avgColors);
		final int rmDiff = Math.abs(avgColors[0] - ColorMap[r][c][0]);
		final int gmDiff = Math.abs(avgColors[1] - ColorMap[r][c][1]);
		final int bmDiff = Math.abs(avgColors[2] - ColorMap[r][c][2]);

		// Keep a score that dictates how good a match is
		// Like in golf, a lower score is better. This is simply
		// made up of the total difference in each channel, added
		// together. Other weights can be added in the future.
		return rmDiff + gmDiff + bmDiff;
	}
}
