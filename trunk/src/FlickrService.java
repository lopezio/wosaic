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

/**
 * 
 */

/**
 * @author scott
 * 
 */
public class FlickrService {

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

	public String getSearchString() {
		return Params.getText();
	}

	public void setSearchString(String searchString) {
		Params.setText(searchString);
		ReturnedPage = 0;
	}
}