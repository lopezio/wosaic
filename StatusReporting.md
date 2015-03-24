# Introduction #

The problem we need to solve is that of reporting progress to the user.  We want to let the user have some indication of  the status of a mosaic, but we find that many different pieces of our code contribute to status changes of the mosaic.  This makes it tricky to find a neatly encapsulated way of reporting status.  To facilitate this, we created a Status object, which contains references to UI components (a JLabel and JProgressBar), which convey the status to the screen in various formats.  The following section provides further details.


# Implementation Details #

The Status object is intended to be something of a globally shared object, which acts as an intermediary between the processing code and the UI.  For lack of a better place, it is instantiated in the WosaicUI class, since it is currently the topmost level of our applet (note that in the future it would be worthwhile to investigate adding a more global layer, separate from the UI to instantiate such objects).  As the UI components get initialized and added to the applet, the Status object stores a reference to these objects.

The Status object is then passed among all the processing players that contribute to status (JAIProcessor, ImageBuffer, and SourcesPlugin).  Upon an initial implementation, it seems that the ImageBuffer contains the most information regarding progress, since it keeps track of how many images have been added and how many are expected.  The interface to the Status object provides simple accessors for setting the text of the JLabel, and changing the state of the progress bar.

```
public class Status {

	private String statusMessage;
	JLabel statusLabel;
	JProgressBar progressBar;
	
        ...

	public void setStatus(String s) {
		statusMessage = s;
		statusLabel.setText(s);
	}
	
        ...

	public void setIndeterminate(boolean ind) {
		progressBar.setIndeterminate(ind);
	}
	
	public void setProgress(int n) {
		progressBar.setValue(n);
	}
```