
import utilities.Parameters;
import utilities.Pixel;
import java.awt.image.BufferedImage;

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
		
		/*
		BufferedImage[] srcImages;
		String searchKey = "red";
		flickr.getImage(searchKey, srcImages);
		
		*/
		
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
		
		BufferedImage testImg = new BufferedImage(mPixel.width, mPixel.height, BufferedImage.TYPE_INT_RGB);
		testImg.setData(mPixel.source.getData());
		
		Pixel testPxl = new Pixel(testImg);
		
		Mosaic mosaicProc = new Mosaic();
		Pixel[][] mosaic = mosaicProc.createMosaic(srcImages, testPxl, param);
		
		
		
		
	}

}
