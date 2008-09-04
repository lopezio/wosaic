package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Calls the save routine for a mosaic. This can take a long time, so it should
 * be in its own thread.
 * 
 * @author carl-eriksvensson
 */
public class SaveThread implements Runnable {

	private final File file;
	private final Mosaic mos;
	private final Status statusObject;

	/**
	 * Minimal constructor for a save thread.
	 * 
	 * @param m the mosaic object to save
	 * @param s the status object for reporting errors
	 * @param f the file to save to
	 */
	public SaveThread(final Mosaic m, final Status s, final File f) {
		file = f;
		mos = m;
		statusObject = s;
	}

	/**
	 * Creates a buffered image from the mosaic and saves it to a file.
	 * 
	 * @see java.lang.Runnable
	 */
	public void run() {
		try {
			final BufferedImage img = mos.createImage();
			String path = file.getAbsolutePath();
			final String lcasePath = path.toLowerCase();

			if (!lcasePath.contains(".jpg") && !lcasePath.contains(".jpeg"))
				path += ".jpg";

			mos.save(img, path, "JPEG");
			statusObject.setStatus("Save Complete!");

		} catch (final Throwable e) {
			System.out.println("Save failed: ");
			System.out.println(e);
			statusObject.setIndeterminate(false);
			statusObject.setProgress(0);
			statusObject.setStatus("Save Failed!");
		}

		statusObject.setIndeterminate(false);

	}

}
