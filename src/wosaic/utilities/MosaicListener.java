/**
 * 
 */
package wosaic.utilities;

import java.util.EventListener;

/**
 * @author carl-eriksvensson
 */
public interface MosaicListener extends EventListener {

	/**
	 * Event that triggers when the Mosaic object gets updated with a new Pixel
	 * 
	 * @param e The event parameters
	 */
	public void mosaicUpdated(MosaicEvent e);

}
