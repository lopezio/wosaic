
import utilities.Parameters;
import utilities.Pixel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Controller {

	/**
	 * Controls communication between JAI processing and Flickr API.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// FIXME temporary image source
		Parameters param = new Parameters(20, 20);
		String baseURL = "images/";
		String mImage = baseURL + "guitar.jpg";
		String[] srcImages = new String[11];
		
		for (int i=0; i < srcImages.length; i++) {
			srcImages[i] = baseURL + i + ".jpg";
		}
		
		String searchKey = "red";
		FlickrService flickr = null;
		ArrayList<BufferedImage> sources;
		
		try {
			flickr = new FlickrService();
			sources = flickr.GetImagePool(searchKey, 20);
		} catch (Exception e) {
			System.out.println("ERROR!  Flickr Failed...");
			System.out.println(e);
			return;
		}
		
		// Set up a Pixel object for mImage
		Pixel mPixel;
		try {
			mPixel = new Pixel(mImage);
		} catch (Exception e) {
			// TODO find a way to cleanly kill the app... at this point maybe just return
			System.out.println(e);
			return;
		}
		
		// Calculate dimensions of each segment
		if (!param.isInitialized()) {
			param = new Parameters(param.resRows, param.resCols, mPixel.width, mPixel.height);
		}
		
		// TODO Iterate through the Buffered Images and create Pixel objects
		Pixel[] sourcePixels = new Pixel[sources.size()];
		for (int i=0; i < sources.size(); i++) {
			sourcePixels[i] = new Pixel(sources.get(i));
		}
		
		Mosaic mosaicProc = new Mosaic();
		Pixel[][] mosaic = mosaicProc.createMosaic(sourcePixels, mPixel, param);
		
		
		
		
	}

}
