/**
 * Pixel.java
 * 
 * This provides manipulations at the pixel level.
 */
package wosaic.utilities;



import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.image.codec.jpeg.*;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Pixel {

	// A cached representation of the scaled pixel
	private BufferedImage cachedImage = null;
	private int cachedWidth = -1;
	private int cachedHeight = -1;

	private Raster pixels = null;
	private String file = "";
	
	private BufferedImage image = null;
	private ImageIcon icon = null;
	
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
	 * @throws IOException thrown if the file is not found
	 * @throws ImageFormatException thrown by JPEG Decoder
	 */
	public Pixel(String filename, boolean master) throws Exception {
		file = filename;
		
		//Load the image with a JPEGDecoder
		FileInputStream is = new FileInputStream(file);
		
		/*JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(is);

		// Get pixel-information of the src image
		pixels = decoder.decodeAsRaster();
		image = decoder.decodeAsBufferedImage();
		*/
		
		// FIXME: Toolkit.getDefaultToolkit() is for an application!
		//			Use getImage for an Applet...
		image = ImageIO.read(is);
		pixels = image.getData();
		width = image.getWidth();
		height = image.getHeight();
		
		alreadyUsed = 0;
		
		// Get the average color of this image
		System.out.println("Getting Average Color...");
		
		// This step is unnecessary and costly for the master image!
		if (!master) {
			getAvgColor(0, 0, width, height, avgColor);
		}
	}
	
	/**
	 * Creates a Pixel object from a BufferedImage
	 * @param img the source image
	 */
	public Pixel(BufferedImage img) {
		//source = AWTImageDescriptor.create(img, null);
		
		// Get pixel-information of the src image
		pixels = img.getData();
		width = img.getWidth();
		height = img.getHeight();
		
		alreadyUsed = 0;
		image = img;
		
		// Get the average color of this image
		getAvgColor(0, 0, width, height, avgColor);
	}
	
	
	public BufferedImage getBufferedImage() {
		// This is set by the constructors... maybe it should be set here
		// and not saved as a reference
		return image;
	}
	
	/**
	 * FIXME: It may be superflous to store a reference to this icon... maybe.
	 * 
	 * @return an ImageIcon representation of this image
	 */
	public ImageIcon getImageIcon() {
		if (icon == null) {
			icon = new ImageIcon(getBufferedImage());
		}
		return icon;
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
		
		// Scale the Image
		Image scalable = image.getScaledInstance(w, h, Image.SCALE_FAST);
		
		// Create a new buffered image
		BufferedImage tmp_image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		// Draw the image into the BufferedImage
		Graphics g = tmp_image.createGraphics();
		g.drawImage(scalable, 0, 0, null);
		g.dispose();
		
		pixels = tmp_image.getData();
		height = tmp_image.getHeight();
		width = tmp_image.getWidth();
		
		image = tmp_image;
	}

	public BufferedImage getScaledImage(int width, int height) {
		if ((cachedWidth == width) && (cachedHeight == height))
			return cachedImage;
		
		// Else, we'll need to scale it
		Image scaled = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage tmp_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Draw the image into the BufferedImage
		Graphics g = tmp_image.createGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();

		cachedImage = tmp_image;
		cachedWidth = width;
		cachedHeight = height;

		return cachedImage;
	}

}
