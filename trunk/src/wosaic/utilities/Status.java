package wosaic.utilities;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Status {

	private String statusMessage;
	JLabel statusLabel;
	JProgressBar progressBar;
	
	public Status(JProgressBar bar) {
		statusMessage = "";
		progressBar = bar;
		progressBar.setMinimum(0);
	}
	
	public void setLabel(JLabel label) {
		statusLabel = label;
	}
	
	public void setStatus(String s) {
		statusMessage = s;
		statusLabel.setText(s);
	}
	
	public void setProgressLimits(int min, int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}
	
	public void setIndeterminate(boolean ind) {
		progressBar.setIndeterminate(ind);
	}
	
	public void setProgress(int n) {
		progressBar.setValue(n);
	}
}
