/**
 * 
 */
package wosaic.utilities;

/**
 * @author carl-erik svensson
 * 
 * This class is immutable
 */
public final class Parameters {

	/**
	 * Number of rows to divide the master image into
	 */
	public int resRows;
	/**
	 * Number of columns to divide the master image into
	 */
	public int resCols;

	/**
	 * Physical width of each segment
	 */
	public int sWidth;
	/**
	 * Physical height of each segment
	 */
	public int sHeight;
	
	/**
	 * The width of the original source image.  Used for
	 * calculating save dimensions.
	 */
	public int originalWidth;
	
	/**
	 * The height of the original source image.  Used for
	 * calculating save dimensions.
	 */
	public int originalHeight;
	/**
	 * Creates a fully initialized parameter set.
	 * 
	 * @param rows number of rows of segments in mosaic
	 * @param cols number of columns of segments in mosaic
	 * @param mW width of the master image
	 * @param mH height of the master image
	 */
	public Parameters(int rows, int cols, int mW, int mH) {
		resRows = rows;
		resCols = cols;
		
		setSectionSize(mW, mH);
		originalWidth = getMasterWidth();
		originalHeight = getMasterHeight();

	}

	/**
	 * Returns the total scaled width of the master image
	 * 
	 * @return the total scaled width of the master image
	 */
	public int getMasterWidth() {
		return sWidth * resCols;
	}

	/**
	 * Returns the total scaled height of the master image
	 * 
	 * @return the total scaled height of the master image
	 */
	public int getMasterHeight() {
		return sHeight * resRows;
	}
	
	/**
	 * Sets the section width and height based on a target
	 * master width and height.
	 *
	 * @param mW master width
	 * @param mH master height
	 */
	 
	public void setSectionSize(int mW, int mH) {
	
		// Adjust for having a fraction of a pixel in width
		sWidth = mW / resCols;
		int remainder = mW % resCols;
		if (remainder > 0) {
			sWidth++;
		}
		
		// Adjust for having a fraction of a pixel in height
		sHeight = mH / resRows;
		remainder = mH % resRows;
		if (remainder > 0) {
			sHeight++;
		}
		
	}

}
