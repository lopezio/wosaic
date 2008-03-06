/**
 * Pixel.java
 * 
 * This provides manipulations at the pixel level.
 */
package wosaic.utilities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import javax.swing.ImageIcon;

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Pixel {

    // Score attributes
    public int alreadyUsed;

    /**
         * An array containing the averages for each channel (RGB) of this
         * image.
         */
    public int[] avgColor = new int[3];

    private int cachedHeight = -1;

    // A cached representation of the scaled pixel
    private BufferedImage cachedImage = null;

    private int cachedWidth = -1;

    private final String file = "";

    /**
         * The image's current height.
         */
    public int height;

    private ImageIcon icon = null;

    private BufferedImage image = null;

    private Raster pixels = null;

    /**
         * The image's current width.
         */
    public int width;

    /**
         * Creates a Pixel object from a BufferedImage
         * 
         * @param img
         *                the source image
         * @param isMaster
         *                Whether or not we're dealing with the master source
         *                image
         */
    public Pixel(final BufferedImage img, boolean isMaster) {
	// source = AWTImageDescriptor.create(img, null);

	// Get pixel-information of the src image
	pixels = img.getData();
	width = img.getWidth();
	height = img.getHeight();

	alreadyUsed = 0;
	image = img;

	// This step is unnecessary and costly for the master image!
	if (!isMaster)
	    // Get the average color of this image
	    getAvgColor(0, 0, width, height, avgColor);
    }

    /**
         * Gets the average color of an area of this Pixel's image.
         * 
         * @param x
         *                starting x position of the area
         * @param y
         *                starting y position of the area
         * @param w
         *                width of area
         * @param h
         *                height of area
         * @param avgVal
         *                holds the red, green, and blue components,
         *                respectively
         */
    public void getAvgColor(final int x, final int y, final int w, final int h, final int avgVal[]) {

	// Error-check the boundaries of avgVal
	if (avgVal.length < 3) {
	    System.out.println("getProminentColor: avgVal not big enough");
	    return;
	}

	// Get all the pixels in the image
	final int numPixels = w * h;

	final int[] tmpimage = new int[numPixels * 3];
	getPixelArea(x, y, w, h, tmpimage);

	int rSum = 0, gSum = 0, bSum = 0;

	for (int i = 0; i < tmpimage.length; i += 3) {
	    rSum += tmpimage[i];
	    gSum += tmpimage[i + 1];
	    bSum += tmpimage[i + 2];
	}

	avgVal[0] = rSum / numPixels;
	avgVal[1] = gSum / numPixels;
	avgVal[2] = bSum / numPixels;

	return;
    }

    public BufferedImage getBufferedImage() {
	// This is set by the constructors... maybe it should be set here
	// and not saved as a reference
	return image;
    }

    /**
         * FIXME: It may be superflous to store a reference to this icon...
         * maybe.
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
         * Populates the int[] with RGB values for each pixel in the area
         * specified by width w and height h, with top-left corner at location
         * (x, y). Indeces, 0, 1, and 2 of int[] refer to the RGB values for the
         * first pixel. Likewise indeces i*3, (i+1)*3, and (i+2)*3 refer to the
         * RGB values of the i-th pixel.
         * 
         * @param x
         *                starting x position
         * @param y
         *                starting y position
         * @param w
         *                width of the area
         * @param h
         *                height of the area
         * @param array
         *                holds return values
         * 
         */
    public void getPixelArea(final int x, final int y, final int w, final int h, final int[] array) {

	pixels.getPixels(x, y, w, h, array);
	return;
    }

    /**
         * Populates the int[0], int[1], int[2] with the Red, Green, and Blue
         * values for the pixel at the given x and y location.
         * 
         * @param x
         *                the x coordinate of the desired pixel
         * @param y
         *                the y coordinate of the desired pixel
         * @param array
         *                holds return values for pixel
         * 
         */
    public void getPixelAt(final int x, final int y, final int[] array) {

	pixels.getPixel(x, y, array);
	return;
    }

    /**
         * An accessor for the raster information of this Pixel.
         * 
         * @return the raster for this Pixel's source image
         */
    public Raster getRaster() {
	return pixels;
    }

    public BufferedImage getScaledImage(final int w, final int h) {
	if (cachedWidth == w && cachedHeight == h)
	    return cachedImage;

	// Else, we'll need to scale it
	final Image scaled = image.getScaledInstance(w, h, Image.SCALE_FAST);
	final BufferedImage tmp_image = new BufferedImage(w, h,
		BufferedImage.TYPE_INT_RGB);

	// Draw the image into the BufferedImage
	final Graphics g = tmp_image.createGraphics();
	g.drawImage(scaled, 0, 0, null);
	g.dispose();

	cachedImage = tmp_image;
	cachedWidth = w;
	cachedHeight = h;

	return cachedImage;
    }

    /**
         * Scale the source image to desired dimensions.
         * 
         * @param w
         *                desired width
         * @param h
         *                desired height
         */
    public void scaleSource(final int w, final int h) {

	// Scale the Image
	final Image scalable = image.getScaledInstance(w, h, Image.SCALE_FAST);

	// Create a new buffered image
	final BufferedImage tmp_image = new BufferedImage(w, h,
		BufferedImage.TYPE_INT_RGB);

	// Draw the image into the BufferedImage
	final Graphics g = tmp_image.createGraphics();
	g.drawImage(scalable, 0, 0, null);
	g.dispose();

	pixels = tmp_image.getData();
	height = tmp_image.getHeight();
	width = tmp_image.getWidth();

	image = tmp_image;
    }

    /**
         * Generates a summary string of this object.
         * 
         * @return a string representation of this object in the form
         *         "filename:R:G:B" where R:G:B denote the RGB values
         *         representing the most prominent color.
         */
    @Override
    public String toString() {
	final String s = file + ": " + avgColor[0] + ", " + avgColor[1] + ", "
		+ avgColor[2];

	return s;
    }

}
