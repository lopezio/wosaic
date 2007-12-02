/**
 * 
 */
package wosaic.utilities;

import java.util.EventObject;

/**
 * @author carl-eriksvensson
 *
 */
public class MosaicEvent extends EventObject {

	public int row;
	public int col;
	
	/**
	 * Creates an event that specifies what part of the mosaic was just updated.
	 * @param source the source object
	 * @param r the row that was updated
	 * @param c the column that was updated
	 */
	public MosaicEvent(Object source, int r, int c) {
		super(source);
		row = r;
		col = c;
	}
	
	public String toString() {
		return "MosaicUpdated at row: " + row + " and col: " + col;
	}

}
