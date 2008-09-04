/**
 * 
 */
package wosaic.utilities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

/**
 * @author scott A light-weight, callable class that processes and returns
 *         thumbnails of local files
 */
public class FileQuery implements Callable<BufferedImage> {

	private final File file;

	/**
	 * Constructor taking sources buffer and dom node as parameters.
	 * 
	 * @param F the image file that should read in
	 */
	public FileQuery(final File F) {
		file = F;
	}

	/**
	 * Workhorse of the FileQuery class. This loads the image data from the
	 * file, scales it to thumbnail size, and adds it to the shared buffer
	 * instantiated by the Controller.
	 * 
	 * @return A scaled representation of the image we were queried on
	 * @throws IOException If the File we are called on isn't a support image
	 *             format
	 */
	public BufferedImage call() throws IOException {

		// System.err.println("Attempting to read in file as image...");
		BufferedImage bufImg = ImageIO.read(file);

		// Crop the image to be square
		final int orig_h = bufImg.getHeight();
		final int orig_w = bufImg.getWidth();
		int x, y, w, h;
		if (orig_h < orig_w) {
			y = 0;
			x = (orig_w - orig_h) / 2;
			w = orig_h;
			h = orig_h;
		} else {
			y = (orig_h - orig_w) / 2;
			x = 0;
			w = orig_w;
			h = orig_w;
		}
		final CropImageFilter cropFilter = new CropImageFilter(x, y, w, h);
		final ImageProducer producer = new FilteredImageSource(bufImg
				.getSource(), cropFilter);

		// We need to work with a regular image, and then convert it back later
		Image img = Toolkit.getDefaultToolkit().createImage(producer);
		img = img.getScaledInstance(30, 30, Image.SCALE_FAST);

		bufImg = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
		final Graphics g = bufImg.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bufImg;
	}

}
