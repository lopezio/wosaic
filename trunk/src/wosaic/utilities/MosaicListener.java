/**
 * 
 */
package wosaic.utilities;

import java.util.EventListener;

/**
 * @author carl-eriksvensson
 *
 */
public interface MosaicListener extends EventListener {
	
	public void mosaicUpdated(MosaicEvent e);

}
