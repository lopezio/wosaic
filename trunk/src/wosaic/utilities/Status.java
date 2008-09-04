package wosaic.utilities;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * Defines an object for reporting status information to the user.
 * 
 * @author carl-eriksvensson
 */
public class Status {

	private final JProgressBar progressBar;
	private JLabel statusLabel;

	/**
	 * Initializes the status object with a progress bar and status label.
	 * 
	 * @param label label to hold status messages
	 * @param bar a reference to the progress bar
	 */
	public Status(final JLabel label, final JProgressBar bar) {
		statusLabel = label;
		progressBar = bar;
		progressBar.setMinimum(0);
	}

	/**
	 * Initializes the status object with a progress bar.
	 * 
	 * @param bar a reference to the progress bar
	 */
	public Status(final JProgressBar bar) {
		progressBar = bar;
		progressBar.setMinimum(0);
	}

	/**
	 * Sets the mode of the progress bar.
	 * 
	 * @param ind whether or not the progress bar is in indeterminate mode.
	 */
	public void setIndeterminate(final boolean ind) {
		progressBar.setIndeterminate(ind);
	}

	/**
	 * Defines a reference to a label on the UI for reporting status messages.
	 * 
	 * @param label label to hold status messages
	 */
	public void setLabel(final JLabel label) {
		statusLabel = label;
	}

	/**
	 * Set the amount to display on the progress bar
	 * 
	 * @param n amount to set progress to
	 */
	public void setProgress(final int n) {
		progressBar.setValue(n);
	}

	/**
	 * Defines the limits for the progress bar
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 */
	public void setProgressLimits(final int min, final int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}

	/**
	 * Sets the status message
	 * 
	 * @param s the status message
	 */
	public void setStatus(final String s) {
		statusLabel.setText(s);
	}
}
