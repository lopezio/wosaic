/**
 * 
 */
package wosaic.utilities;

import javax.swing.*;

/**
 * @author Carl-Erik Svensson
 *
 */
public abstract class SourcePlugin implements Runnable {
	
	protected ImageBuffer sourcesBuffer = null;
	
	/**
	 * This is the worker thread for the source.  It must
	 * populate the sourcesBuffer with BufferedImages in
	 * order to have its contents processed in the mosaic.
	 */
	abstract public void run();
	
	/**
	 * This is provided to allow a source to specify
	 * its own configurable options.  Each source can
	 * provide its own JPanel and event handlers to 
	 * set its options.
	 * @return a JPanel with configurable options.
	 */
	abstract public JFrame getOptionsPane();
	
	/**
	 * This is used as a method of determining what
	 * kind of source a SourcePlugin is.
	 * @return the string representation of this source.
	 */
	abstract public String getType();
	
	/**
	 * Call this to validate this source's parameters.
	 * @return false if parameters are not valid, true if they are.
	 */
	abstract public String validateParams();
	
	/**
	 * This is required for setting the shared buffer for all the sources.
	 * @param buf
	 */
	public void setBuffer(ImageBuffer buf) {
		sourcesBuffer = buf;
	}

}
