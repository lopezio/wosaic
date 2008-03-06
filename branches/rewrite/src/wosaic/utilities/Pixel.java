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

/**
 * @author carl-erik svensson
 * @version 0.1
 */
public class Pixel {

	/**
	 * Score attribute, how many times this Pixel has been used in a Mosaic.
	 */
	// Commented out, because our algorithm doesn't use it.
	// public int alreadyUsed;
	private int cachedHeight = -1;

	// A cached representation of the scaled pixel
	private BufferedImage cachedImage = null;

	private int cachedWidth = -1;

	/**
	 * The image's current height.
	 */
	public int height;

	private BufferedImage image = null;

	/**
	 * The image's current width.
	 */
	public int width;

	/**
	 * Creates a Pixel object from a BufferedImage
	 * 
	 * @param img
	 *            the source image
	 * @param isMaster
	 *            Whether or not we're dealing with the master source image
	 */
	public Pixel(final BufferedImage img, final boolean isMaster) {
		// source = AWTImageDescriptor.create(img, null);

		// Get pixel-information of the src image
		width = img.getWidth();
		height = img.getHeight();

		// Commented, because our algorithm doesn't use this currently
		// alreadyUsed = 0;
		image = img;
	}

	/**
	 * Gets the average color of an area of this Pixel's image.
	 * 
	 * @param x
	 *            starting x position of the area
	 * @param y
	 *            starting y position of the area
	 * @param w
	 *            width of area
	 * @param h
	 *            height of area
	 * @param avgVal
	 *            holds the red, green, and blue components, respectively
	 * @param raster
	 *            The raster data for the pixel. This is an optional parameter,
	 *            and can be set to null. You should set the raster parameter if
	 *            you plan on making many successive calls on the same Pixel
	 */
	public void getAvgColor(final int x, final int y, final int w, final int h,
			final int avgVal[], final Raster raster) {

		// Error-check the boundaries of avgVal
		if (avgVal.length < 3) {
			System.out.println("getProminentColor: avgVal not big enough");
			return;
		}

		// Get the Raster for our image
		Raster rast;
		if (raster != null)
			rast = raster;
		else
			rast = image.getData();

		// Get all the pixels in the image
		final int numPixels = w * h;

		final int[] tmpimage = new int[numPixels * 3];
		rast.getPixels(x, y, w, h, tmpimage);

		int rSum = 0, gSum = 0, bSum = 0;

		for (int i = 0; i < tmpimage.length; i += 3) {
			rSum += tmpimage[i];
			gSum += tmpimage[i + 1];
			bSum += tmpimage[i + 2];
		}

		avgVal[0] = rSum / numPixels;
		avgVal[1] = gSum / numPixels;
		avgVal[2] = bSum / numPixels;
	}

	/**
	 * Gets the average color of the entire image. This is an optimized version
	 * of getAvgColor
	 * 
	 * @param avgVal
	 *            holds the red, green, and blue components, respectively
	 */
	public void getAvgImageColor(final int avgVal[]) {

		// Error-check the boundaries of avgVal
		if (avgVal.length < 3) {
			System.out.println("getProminentColor: avgVal not big enough");
			return;
		}

		// Get all the pixels in the image
		final int numPixels = width * height;

		final int[] tmpimage = new int[numPixels * 3];
		image.getData().getPixels(0, 0, width, height, tmpimage);

		int rSum = 0, gSum = 0, bSum = 0;

		for (int i = 0; i < tmpimage.length; i += 3) {
			rSum += tmpimage[i];
			gSum += tmpimage[i + 1];
			bSum += tmpimage[i + 2];
		}

		avgVal[0] = rSum / numPixels;
		avgVal[1] = gSum / numPixels;
		avgVal[2] = bSum / numPixels;
	}

	/**
	 * Get the underlying BufferedImage that represents this pixel
	 * 
	 * @return the BufferedImage
	 */
	public BufferedImage getBufferedImage() {
		return image;
	}

	/**
	 * Retrieve a scaled instance of the Pixel's BufferedImage. Note that this
	 * method uses some caching, so multiple calls for the same dimensions will
	 * be efficient.
	 * 
	 * @param w
	 *            The width of the scaled instance
	 * @param h
	 *            The height of the scaled instance
	 * @return a new BufferedImage of the requested dimensions
	 */
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
	 * Generates a summary string of this object.
	 * 
	 * @return a string representation of this object in the form
	 *         "filename:R:G:B" where R:G:B denote the RGB values representing
	 *         the most prominent color.
	 */
	@Override
	public String toString() {
		final int[] colors = new int[3];
		getAvgImageColor(colors);
		final String s = "Pixel Image: " + colors[0] + ", " + colors[1] + ", "
				+ colors[2];

		return s;
	}
}
