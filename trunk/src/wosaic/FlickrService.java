package wosaic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

import wosaic.utilities.ImageBuffer;

/**
 * 
 */

/**
 * An interface to make simple photo queries to Flickr.
 * @author scott
 * 
 */
public class FlickrService implements Runnable{

	private static String API_KEY = "149e20572d673fa56f46a0ed0afe464f";

	private static String HOST = "www.flickr.com";

	private static String SECRET = "35e3f7923939e71a";

	private Flickr F = null;

	private SearchParameters Params = null;

	private PhotosInterface PhotosInt = null;

	private RequestContext ReqCon = null;

	private REST Rest = null;

	private int ReturnedPage = 0;
	private int ResultsPerPage = 0;
	private ImageBuffer sourcesBuffer;
	private int targetImages;
	private int imagesReceived;

	/**
	 * Default constructor.  Makes a connection to Flickr and
	 * initializes all local objects.
	 * @throws Exception
	 * @deprecated
	 */
	public FlickrService() throws Exception {
		// Connect to flickr
		try {
			Connect();
		} catch (Exception ex) {
			throw new Exception("Could not connect to Flickr: "
					+ ex.getMessage(), ex.getCause());
		}

		// Get our picture service
		PhotosInt = F.getPhotosInterface();

		// Set our parameters
		Params = new SearchParameters();
		Params.setSort(SearchParameters.RELEVANCE);
		ReturnedPage = 0;
	}
	
	/**
	 * 
	 * @param cont
	 * @throws Exception
	 */
	public FlickrService(ImageBuffer buf, int target) throws Exception {
		// Connect to flickr
		try {
			Connect();
		} catch (Exception ex) {
			throw new Exception("Could not connect to Flickr: "
					+ ex.getMessage(), ex.getCause());
		}

		// Get our picture service
		PhotosInt = F.getPhotosInterface();

		// Set our parameters
		Params = new SearchParameters();
		Params.setSort(SearchParameters.RELEVANCE);
		ReturnedPage = 0;
		
		sourcesBuffer = buf;
		targetImages = target;
		imagesReceived = 0;
	}

	private void Connect() throws ParserConfigurationException {
		// Initialize
		Rest = new REST();
		Rest.setHost(HOST);
		F = new Flickr(API_KEY);

		// Set the shared secret which is used for any calls which require
		// signing.
		ReqCon = RequestContext.getRequestContext();
		ReqCon.setSharedSecret(SECRET);
	}

	/**
	 * @param searchString The search to query Flickr with
	 * @param n The maximum number of results to return.
	 * @return A (possibly empty) list of Buffered images representing
	 * the return result from a Flickr query.
	 * @throws Exception
	 */
	public ArrayList<BufferedImage> GetImagePool(String searchString, int n)
			throws Exception {
		ResultsPerPage = n;
		setSearchString(searchString);
		ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();

		try {
			ret = GetResultsPage(++ReturnedPage);
		} catch (Exception ex) {
			throw new Exception("Error querying Flickr for images: "
					+ ex.getMessage(), ex.getCause());
		}

		return ret;
	}

	/**
	 * @return A list of Buffered Images representing the next page
	 * of results from a Flickr query.  The parameters to the query
	 * are specified in the previous call to GetImagePool.
	 * @throws Exception
	 */
	public ArrayList<BufferedImage> GetMoreResults() throws Exception {
		if (getSearchString() == null || getSearchString() == "")
			throw new Exception("Flickr search string not set!");

		ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();

		try {
			ret = GetResultsPage(++ReturnedPage);
		} catch (Exception ex) {
			throw new Exception("Error querying Flickr for images: "
					+ ex.getMessage(), ex.getCause());
		}

		return ret;
	}

	private ArrayList<BufferedImage> GetResultsPage(int page)
			throws IOException, SAXException, FlickrException {

		ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();

		PhotoList pl = PhotosInt.search(Params, ResultsPerPage, page);
		for (int i = 0; i < pl.size(); i++)
			ret.add(((Photo) pl.get(i)).getSmallSquareImage());

		return ret;
	}

	/**
	 * @return The string we are querying Flickr with.
	 */
	public String getSearchString() {
		return Params.getText();
	}

	/**
	 * @param searchString
	 */
	public void setSearchString(String searchString) {
		Params.setText(searchString);
		ReturnedPage = 0;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (imagesReceived < targetImages) {
			//System.out.println("Running FlickrThrd...");
			ArrayList<BufferedImage> newList = null;
			try {
				newList = GetMoreResults();
				sourcesBuffer.addToImageBuffer(newList);
				imagesReceived += newList.size();
			} catch (final Exception e) {
				//System.out.println("Get More Results Failed!");
				//System.out.println(e);
				//return;
			}
			
			
		}
		
		sourcesBuffer.isComplete = true;
		
		System.out.println("Exiting FlickrThrd...");
	}
}