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
	
	public int originalWidth, originalHeight;
	
	private boolean initialized;
	
	/**
	 * Defaults all values to zero
	 */
	public Parameters() {
		resRows = 0;
		resCols = 0;
		sWidth = 0;
		sHeight = 0;
		initialized = false;
	}
	
	/**
	 * Creates a partial set of parameters.  This is still
	 * considered uninitialized because mWidth and mHeight
	 * are not provided.
	 * 
	 * @param rows number of rows of segments in mosaic
	 * @param cols number of columns of segments in mosaic
	 */
	public Parameters(int rows, int cols) {
		resRows = rows;
		resCols = cols;
		initialized = false;
	}
	
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
		
		initialized = true;
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
	
	/**
	 * Returns whether or not all the parameters have been properly defined.
	 * 
	 * @return a boolean indicating whether or not this is completely initialized
	 */
	public boolean isInitialized() {
		return initialized;
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
}
