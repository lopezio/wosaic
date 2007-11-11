/**
 * Pixel.java
 * 
 * This provides manipulations at the pixel level.
 */
package utilities;
import javax.media.jai.*;

import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Pixel {

	// Image to operate on
	public RenderedOp source = null;
	private Histogram histogram = null;
	private Raster pixels = null;
	private String file = "";
	
	public int[] avgColor = new int[3];
	public int width;
	public int height;

	/**
	 * @param filename the path to an image file
	 * 
	 * Constructs a pixel object based on the input image.
	 */
	public Pixel(String filename) throws java.lang.reflect.InvocationTargetException {
		file = filename;
		
		// TODO Only create source once when computing the averages to save data
		// Create a PlanarImage from the given file
		source = JAI.create("fileload", filename);
		
		// Get pixel-information of the src image
		pixels = source.getData();
		width = source.getWidth();
		height = source.getHeight();
		
		// Get the average color of this image
		getAvgColor(0, 0, source.getWidth(), source.getHeight(), avgColor);
	}
	
	/**
	 * @param x starting x position of the area
	 * @param y starting y position of the area
	 * @param w width of area
	 * @param h height of area
	 * @param avgVal holds the red, green, and blue components,
	 * 			respectively
	 * 
	 * Gets the average color of an area of this Pixel's image.
	 */
	public void getAvgColor(int x, int y, int w, int h, int avgVal[]) {
		
		// Error-check the boundaries of avgVal
		if (avgVal.length < 3) {
			System.out.println("getProminentColor: avgVal not big enough");
			return;
		}
		
		// Get all the pixels in the image
		int numPixels = w * h;
		int[] image = new int[numPixels*3];
		getPixelArea(x,y,w,h, image);
		
		int rSum=0, gSum=0, bSum = 0;
		
		for (int i=0; i<image.length; i += 3) {
			rSum += image[i];
			gSum += image[i+1];
			bSum += image[i+2];
		}
		
		avgVal[0] = rSum / (numPixels);
		avgVal[1] = gSum / (numPixels);
		avgVal[2] = bSum / (numPixels);
		
		return;
	}

	/**
	 * @param x the x coordinate of the desired pixel
	 * @param y the y coordinate of the desired pixel
	 * @param array holds return values for pixel
	 * 
	 * Populates the int[0], int[1], int[2] with the
	 * Red, Green, and Blue values for the pixel at
	 * the given x and y location.   
	*/
	public void getPixelAt(int x, int y, int[] array) {
	
		pixels.getPixel(x, y, array);
		return;
	}
	
	/**
	 * @param x starting x position
	 * @param y starting y position
	 * @param w width of the area
	 * @param h height of the area
	 * @param array holds return values
	 * 
	 * Populates the int[] with RGB values for each pixel
	 * in the area specified by width w and height h, with
	 * top-left corner at location (x, y).  indeces, 0, 1, and 2
	 * of int[] refer to the RGB values for the first pixel.
	 * Likewise indeces i*3, (i+1)*3, and (i+2)*3 refer to the RGB
	 * values of the i-th pixel.
	*/
	public void getPixelArea(int x, int y, int w, int h, int[] array) {
	
		pixels.getPixels(x, y, w, h, array);
		return;
	}
	
	public Raster getRaster() {
		return pixels;
	}
	
	/**
	 * @return a string representation of this object in the form
	 * "filename:R:G:B" where R:G:B denote the RGB values representing
	 * the most prominent color.
	*/
	public String toString() {
		String s = file;
		
		return s;
	}

	/**
	 * Scale the source image to desired dimensions.
	 * @param w desired width
	 * @param h desired height
	 */
	public void scaleSource(float w, float h) {

		float modifierX = w / ((float) width);
		float modifierY = h / ((float) height);
		
		ParameterBlock params = new ParameterBlock();
		params.addSource(source);
		params.add(modifierX);//x scale factor
		params.add(modifierY);//y scale factor
		params.add(0.0F);//x translate
		params.add(0.0F);//y translate
		params.add(new InterpolationNearest());//interpolation method

		source = JAI.create("scale", params);
		pixels = source.getData();
		height = source.getHeight();
		width = source.getWidth();
	}


}
