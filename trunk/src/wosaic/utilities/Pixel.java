/**
 * Pixel.java
 * 
 * This provides manipulations at the pixel level.
 */
package wosaic.utilities;
import javax.media.jai.*;

import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Toolkit;
import javax.media.jai.operator.AWTImageDescriptor;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Pixel {

	/**
	 * The image associated with this Pixel object.
	 */
	public RenderedOp source = null;
	
	private Raster pixels = null;
	private String file = "";
	
	// Score attributes 
	public int alreadyUsed;
	
	/**
	 * An array containing the averages for each channel (RGB) of this
	 * image.
	 */
	public int[] avgColor = new int[3];
	
	/**
	 * The image's current width.
	 */
	public int width;
	
	/**
	 * The image's current height.
	 */
	public int height;

	
	/**
	 * Constructs a pixel object based on the input image.
	 * 
	 * @param filename the path to an image file
	 * @param master indicates if this file is the master image or not
	 * @throws java.lang.reflect.InvocationTargetException an exception thrown by JAI
	 */
	public Pixel(String filename, boolean master) throws java.lang.reflect.InvocationTargetException {
		file = filename;
		
		// FIXME Get rid of JAI... it has major memory leakage
		// Create a PlanarImage from the given file
		source = JAI.create("fileload", filename);

		// Get pixel-information of the src image
		pixels = source.getData();
		width = source.getWidth();
		height = source.getHeight();
		
		alreadyUsed = 0;
		
		// Get the average color of this image
		System.out.println("Getting Average Color...");
		
		// This step is unnecessary and costly for the master image!
		if (!master) {
			getAvgColor(0, 0, source.getWidth(), source.getHeight(), avgColor);
		}
	}
	
	/**
	 * Creates a Pixel object from a BufferedImage
	 * @param img the source image
	 */
	public Pixel(BufferedImage img) {
		source = AWTImageDescriptor.create(img, null);
		
		// Get pixel-information of the src image
		pixels = source.getData();
		width = source.getWidth();
		height = source.getHeight();
		
		alreadyUsed = 0;
		
		// Get the average color of this image
		getAvgColor(0, 0, source.getWidth(), source.getHeight(), avgColor);
	}
	
	
	public Image getBufferedImage() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img.setData(source.getData());
		
		return img;
	}
	
	/**
	 * Gets the average color of an area of this Pixel's image.
	 * 
	 * @param x starting x position of the area
	 * @param y starting y position of the area
	 * @param w width of area
	 * @param h height of area
	 * @param avgVal holds the red, green, and blue components,
	 * 			respectively
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
	 * Populates the int[0], int[1], int[2] with the
	 * Red, Green, and Blue values for the pixel at
	 * the given x and y location.  
	 * 
	 * @param x the x coordinate of the desired pixel
	 * @param y the y coordinate of the desired pixel
	 * @param array holds return values for pixel

	*/
	public void getPixelAt(int x, int y, int[] array) {
	
		pixels.getPixel(x, y, array);
		return;
	}
	
	/**
	 * Populates the int[] with RGB values for each pixel
	 * in the area specified by width w and height h, with
	 * top-left corner at location (x, y).  Indeces, 0, 1, and 2
	 * of int[] refer to the RGB values for the first pixel.
	 * Likewise indeces i*3, (i+1)*3, and (i+2)*3 refer to the RGB
	 * values of the i-th pixel.
	 * 
	 * @param x starting x position
	 * @param y starting y position
	 * @param w width of the area
	 * @param h height of the area
	 * @param array holds return values
	 * 
	*/
	public void getPixelArea(int x, int y, int w, int h, int[] array) {
	
		pixels.getPixels(x, y, w, h, array);
		return;
	}
	
	/**
	 * An accessor for the raster information of this Pixel.
	 * @return the raster for this Pixel's source image
	 */
	public Raster getRaster() {
		return pixels;
	}
	
	/**
	 * Generates a summary string of this object.
	 * 
	 * @return a string representation of this object in the form
	 * "filename:R:G:B" where R:G:B denote the RGB values representing
	 * the most prominent color.
	*/
	public String toString() {
		String s = file + ": " + avgColor[0] + ", " + avgColor[1] + ", " + avgColor[2];
		
		return s;
	}

	/**
	 * Scale the source image to desired dimensions.
	 * 
	 * @param w desired width
	 * @param h desired height
	 */
	public void scaleSource(int w, int h) {

		/*float modifierX = w / ((float) width);
		float modifierY = h / ((float) height);
		
		ParameterBlock params = new ParameterBlock();
		params.addSource(source);
		params.add(modifierX);//x scale factor
		params.add(modifierY);//y scale factor
		params.add(0.0F);//x translate
		params.add(0.0F);//y translate
		params.add(new InterpolationNearest());//interpolation method

		source = JAI.create("scale", params);*/
		
		Image scalable = source.getAsBufferedImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
		source = AWTImageDescriptor.create(scalable, null);
		
		pixels = source.getData();
		height = source.getHeight();
		width = source.getWidth();
	}


}
