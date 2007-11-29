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
	 * Physical width of the master image
	 */
	public int mWidth;
	/**
	 * Physical height of the master image
	 */
	public int mHeight;
	
	/**
	 * Physical width of each segment
	 */
	public int sWidth;
	/**
	 * Physical height of each segment
	 */
	public int sHeight;
	
	private boolean initialized;
	
	/**
	 * Defaults all values to zero
	 */
	public Parameters() {
		resRows = 0;
		resCols = 0;
		mWidth = 0;
		mHeight = 0;
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
		mWidth = mW;
		mHeight = mH;
		
		// Adjust for having a fraction of a pixel in width
		sWidth = mWidth / resCols;
		int remainder = mWidth % resCols;
		if (remainder > 0) {
			sWidth++;
		}
		
		// Adjust for having a fraction of a pixel in height
		sHeight = mHeight / resRows;
		remainder = mHeight % resRows;
		if (remainder > 0) {
			sHeight++;
		}
		
		initialized = true;
	}
	
	/**
	 * Returns whether or not all the parameters have been properly defined.
	 * 
	 * @return a boolean indicating whether or not this is completely initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

}
