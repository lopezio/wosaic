/**
 * 
 */
package wosaic.utilities;

import java.awt.image.*;
import java.awt.Toolkit;
import java.awt.Image;
import java.util.concurrent.Callable;

import java.io.File;
import javax.imageio.ImageIO;


/**
 * @author scott
 * A light-weight, callable class that  processes and returns
 * thumbnails of local files
 */
public class FileQuery implements Callable<BufferedImage>{

	private File file;
	
	/**
	 * Constructor taking sources buffer and dom node
	 * as parameters.
	 * @param F the image file that should read in
	 */
	public FileQuery(File F) {
		file = F;
	}
	
	/**
	 * Workhorse of the FileQuery class.  This loads the image data
	 * from the file, scales it to thumbnail size, and adds it to the
	 * shared buffer instantiated by the Controller.
	 */
	public BufferedImage call() throws Exception {
		
		System.err.println("Attempting to read in file as image...");
		BufferedImage img = ImageIO.read(file);
			
		System.err.println("QUERY: Orig img=" + img);
		// Crop the image to be square
		int orig_h = img.getHeight();
		int orig_w = img.getWidth();
		int x,y,w,h;
		if (orig_h < orig_w) {
			y = 0;
			x = (orig_w - orig_h)/2;
			w = orig_h;
			h = orig_h;
		} else {
			y = (orig_h - orig_w)/2;
			x = 0;
			w = orig_w;
			h = orig_w;
		}
		CropImageFilter cropFilter = new CropImageFilter(x,y,w,h);
		ImageProducer producer = new FilteredImageSource(img.getSource(), 
									cropFilter);
		//FIXME: Can we assume this will return a bufferedimage?
		img = (BufferedImage) Toolkit.getDefaultToolkit().createImage(producer);
		System.err.println("QUERY: Cropped img=" + img);
		img = (BufferedImage) img.getScaledInstance(75,75,Image.SCALE_FAST);
		System.err.println("QUERY: Scaled img=" + img);
		return img;
	}

}
