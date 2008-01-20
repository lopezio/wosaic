package wosaic.utilities;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
/**
 * Defines an object for reporting status information to the
 * user.
 * 
 * @author carl-eriksvensson
 *
 */
public class Status {

	private JLabel statusLabel;
	private JProgressBar progressBar;
	
	/**
	 * Initializes the status object with a progress
	 * bar.
	 * @param bar a reference to the progress bar
	 */
	public Status(JProgressBar bar) {
		progressBar = bar;
		progressBar.setMinimum(0);
	}
	
	/**
	 * Defines a reference to a label on the UI for 
	 * reporting status messages.
	 * 
	 * @param label label to hold status messages
	 */
	public void setLabel(JLabel label) {
		statusLabel = label;
	}
	
	/**
	 * Sets the status message
	 * 
	 * @param s the status message
	 */
	public void setStatus(String s) {
		statusLabel.setText(s);
	}
	
	/**
	 * Defines the limits for the progress bar
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 */
	public void setProgressLimits(int min, int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}
	
	/**
	 * Sets the mode of the progress bar.
	 * 
	 * @param ind whether or not the progress bar is
	 * in indeterminate mode.
	 */
	public void setIndeterminate(boolean ind) {
		progressBar.setIndeterminate(ind);
	}
	
	/**
	 * Set the amount to display on the progress bar
	 * @param n amount to set progress to
	 */
	public void setProgress(int n) {
		progressBar.setValue(n);
	}
}
