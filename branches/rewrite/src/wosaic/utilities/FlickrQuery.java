package wosaic.utilities;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * Make the actual photo query to the Flickr server and return the results
 * asynchronously
 * 
 * @author scott
 * 
 */
public class FlickrQuery implements Callable<ArrayList<BufferedImage>> {

	private final int Page;
	private final SearchParameters Params;
	private final int PerPage;
	private final PhotosInterface PhotosInt;

	/**
	 * Create a new immutable FlickrQuery
	 * 
	 * @param photosInt
	 *            The initialized and connected PhotosInterface
	 * @param params
	 *            The search parameters
	 * @param perPage
	 *            Number of results per page to return
	 * @param page
	 *            The page to return
	 */
	public FlickrQuery(final PhotosInterface photosInt,
			final SearchParameters params, final int perPage, final int page) {
		PhotosInt = photosInt;
		Params = params;
		PerPage = perPage;
		Page = page;
	}

	/**
	 * Asynchronously make the query to Flickr.
	 * 
	 * @return a list of the photos returned from Flickr
	 */
	public ArrayList<BufferedImage> call() {

		final ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();

		PhotoList pl = null;
		try {
			pl = PhotosInt.search(Params, PerPage, Page);
		} catch (final Exception ex) {
			// TODO: Handle exceptions here
		}

		if (pl != null)
			for (int i = 0; i < pl.size(); i++)
				try {
					ret.add(ImageIO.read(new URL(((Photo) pl.get(i))
							.getSmallSquareUrl())));
				} catch (final Exception ex) {
					// TODO: Handle exceptions here
				}

		return ret;
	}

}
