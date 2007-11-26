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

import utilities.FlickrQuery;
import utilities.ImageBuffer;

/**
 * @author scott
 *
 */
public class FlickrService2 implements Runnable {

	
	private static String API_KEY = "149e20572d673fa56f46a0ed0afe464f";

	private static String HOST = "www.flickr.com";

	private static String SECRET = "35e3f7923939e71a";

	private Flickr Flickr = null;

	private SearchParameters Params = null;

	private PhotosInterface PhotosInt = null;

	private RequestContext ReqCon = null;

	private REST Rest = null;

	private int ReturnedPage = 0;
	private int ResultsPerPage = 0;
	private ImageBuffer SourcesBuffer;
	private int TargetImages;
	private int ImagesReceived;
	private int NumThreads;
	private int PicsPerQuery;
	
	public FlickrService2(ImageBuffer sourcesBuffer, int targetImages, int numThreads, int picsPerQuery) throws Exception {
		// Connect to flickr
		try {
			Connect();
		} catch (Exception ex) {
			throw new Exception("Could not connect to Flickr: "
					+ ex.getMessage(), ex.getCause());
		}
		
		// Get our picture service
		PhotosInt = Flickr.getPhotosInterface();

		// Set our parameters
		Params = new SearchParameters();
		Params.setSort(SearchParameters.RELEVANCE);
		
		SourcesBuffer = sourcesBuffer;
		TargetImages = targetImages;
		
		PicsPerQuery = picsPerQuery;
		NumThreads = numThreads;
		
		ReturnedPage = 0;
		ImagesReceived = 0;
	}
	
	private void Connect() throws ParserConfigurationException {
		// Initialize
		Rest = new REST();
		Rest.setHost(HOST);
		Flickr = new Flickr(API_KEY);

		// Set the shared secret which is used for any calls which require
		// signing.
		ReqCon = RequestContext.getRequestContext();
		ReqCon.setSharedSecret(SECRET);
	}
	
	public void run() {
		int numQueries = TargetImages / PicsPerQuery;
		for(int i = 0; i < numQueries; i++) {
			FlickrQuery query = new FlickrQuery(PhotosInt, Params, perPage, )
		}
		
		
		while (ImagesReceived < TargetImages) {
			try {
				newList = GetMoreResults();
			} catch (final Exception e) {
				//System.out.println("Get More Results Failed!");
				//System.out.println(e);
				//return;
			}
			
			sourcesBuffer.addToImageBuffer(newList);
			imagesReceived += newList.size();
		}
		
		sourcesBuffer.isComplete = true;
		
		System.out.println("Exiting FlickrThrd...");
	}
}
