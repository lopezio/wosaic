/**
 * 
 */
package utilities;

/**
 * @author carl-erik svensson
 *
 */
public class Parameters {

	// Rows and columns that specify the number of segments
	// to divide the master into
	public int resRows;
	public int resCols;
	
	// Dimensions of the master image
	public int mWidth;
	public int mHeight;
	
	// Dimensions of each segment
	public int sWidth;
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
	
	public Parameters(int rows, int cols) {
		resRows = rows;
		resCols = cols;
		initialized = false;
	}
	
	public Parameters(int rows, int cols, int mW, int mH) {
		resRows = rows;
		resCols = cols;
		mWidth = mW;
		mHeight = mH;
		sWidth = mWidth / resCols;
		sHeight = mHeight / resRows;
		initialized = true;
	}
	
	/**
	 * @return a boolean indicating whether or not this is completely initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

}
