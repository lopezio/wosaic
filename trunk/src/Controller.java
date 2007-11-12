
import utilities.Parameters;
import utilities.Pixel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Controller {

	ArrayList<BufferedImage> sourcesBuffer;
	
	synchronized public boolean addToImageBuffer(BufferedImage img) {
		return true;
	}
	
	synchronized public boolean removeFromImageBuffer() {
		return true;
	}
	
	/**
	 * Controls communication between JAI processing and Flickr API.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// FIXME temporary master source
		Parameters param = new Parameters(20, 20);
		String baseURL = "images/";
		String mImage = baseURL + "guitar.jpg";
		
		String searchKey = "guitar";
		FlickrService flickr = null;
		ArrayList<BufferedImage> sources;
		
		try {
			flickr = new FlickrService();
			sources = flickr.GetImagePool(searchKey, 80);
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
			param = new Parameters(param.resRows, param.resCols, mPixel.width*2, mPixel.height*2);
		}
		
		// TODO Iterate through the Buffered Images and create Pixel objects
		Pixel[] sourcePixels = new Pixel[sources.size()];
		for (int i=0; i < sources.size(); i++) {
			sourcePixels[i] = new Pixel(sources.get(i));
		}
		
		//Mosaic mosaicProc = new Mosaic();
		//Pixel[][] mosaic = mosaicProc.createMosaic(sourcePixels, mPixel, param);
		
		// Test the threaded implementation
		Mosaic mProc = new Mosaic(mPixel, param, sourcePixels);
		
		
		Thread workerThread;
		workerThread = new Thread(mProc, "Mosaic Worker Thread");
		workerThread.setPriority(3);
		workerThread.start();
		
		// Show how selections change
		while (workerThread.isAlive()) {
			System.out.println(mProc.wosaic[0][0]);
		}
		
		System.out.println("Controller Exiting!");
	}

}
