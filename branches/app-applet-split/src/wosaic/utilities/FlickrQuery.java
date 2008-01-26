package wosaic.utilities;

import org.xml.sax.SAXException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * Make the actual photo query to the Flickr server
 * and return the results asynchronously
 * @author scott
 *
 */
public class FlickrQuery implements Callable<ArrayList<BufferedImage>> {

	private final PhotosInterface PhotosInt;
	private final SearchParameters Params;
	private final int PerPage;
	private final int Page;
	
	/**
	 * Create a new immutable FlickrQuery
	 * @param photosInt The initialized and connected PhotosInterface
	 * @param params The search parameters
	 * @param perPage Number of results per page to return
	 * @param page The page to return
	 */
	public FlickrQuery(PhotosInterface photosInt, SearchParameters params, int perPage, int page) {
		PhotosInt = photosInt;
		Params = params;
		PerPage = perPage;
		Page = page;
	}
	
	/**
	 * Asynchronously make the query to Flickr.
	 */
	public ArrayList<BufferedImage> call() throws IOException, SAXException, FlickrException {
		
		ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();
		
		PhotoList pl = PhotosInt.search(Params, PerPage, Page);
		for (int i = 0; i < pl.size(); i++)
			ret.add(((Photo) pl.get(i)).getSmallSquareImage());
		
		return ret;
	}


}
