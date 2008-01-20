/**
 * 
 */
package wosaic.utilities;

import java.util.EventObject;
import java.awt.Point;
import java.util.ArrayList;

/**
 * @author carl-eriksvensson
 *
 */
public class MosaicEvent extends EventObject {

		public ArrayList<Point> Coords;
	
	/**
	 * Creates an event that specifies what part of the mosaic was just updated.
	 * @param source the source object
	 * @param r the row that was updated
	 * @param c the column that was updated
	 */
	public MosaicEvent(Object source, ArrayList<Point> coords) {
		super(source);
		Coords = coords;
	}
	
	//FIXME: Write a more reasonable "toString"
	public String toString() {
		return "MosaicUpdated";
	}

}
