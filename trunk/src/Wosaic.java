
import utilities.Parameters;

public class Wosaic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// TODO Grab source images
		Parameters param = new Parameters(2, 2);
		String baseURL = "images/";
		String mImage = baseURL + "guitar.jpg";
		String[] srcImages = new String[11];
		
		for (int i=0; i < srcImages.length; i++) {
			srcImages[i] = baseURL + i + ".jpg";
		}
		
		Mosaic mosaicProc = new Mosaic();
		mosaicProc.createMosaic(srcImages, mImage, param);
	}

}
