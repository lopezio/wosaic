/**
 * 
 */
package wosaic.utilities;

import javax.swing.*;

/**
 * @author carl-eriksvensson
 *
 */
public interface SourcePlugin extends Runnable {
	
	public void run();
	public JPanel getOptionsPane();
	public String getType();
	
	/**
	 * Call this to validate this source's parameters.
	 * @return false if parameters are not valid, true if they are.
	 */
	public boolean validateParams();
	
	/**
	 * This is required for setting the shared buffer for all the sources.
	 * @param buf
	 */
	public void setBuffer(ImageBuffer buf);
}
