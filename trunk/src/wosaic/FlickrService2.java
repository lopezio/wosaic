package wosaic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import wosaic.exceptions.FlickrServiceException;
import wosaic.utilities.FlickrQuery;
import wosaic.utilities.ImageBuffer;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * @author scott
 * 
 */
public class FlickrService2 implements Runnable {

	/**
	 * API from Flickr.  Unique for our registered application
	 */
	private static final String API_KEY = "149e20572d673fa56f46a0ed0afe464f";

	/**
	 * Number of connection tries before giving up
	 */
	private static final int CONNECT_RETRY = 5;

	/**
	 * Flag set to determine if we have a valid connection to Flickr
	 */
	private static boolean Connected = false;

	/**
	 * Our connection to the FlickrAPI
	 */
	private static Flickr Flickr = null;

	/**
	 * Needed by Flickr API, the "host" to connect to
	 */
	private static final String HOST = "www.flickr.com";

	/**
	 * Number of child-threads to spawn to query flickr
	 */
	private static final int NumThreads = 10;

	/**
	 * Needed by Flickr API, access to photo API calls
	 */
	private static PhotosInterface PhotosInt = null;

	/**
	 * Number of images to grab from Flickr in each query
	 */
	private static final int PicsPerQuery = 10;

	/**
	 * Needed by Flickr API, access to search query calls
	 */
	private static RequestContext ReqCon = null;

	/**
	 * Needed by Flickr API, low-level network interface
	 */
	private static REST Rest = null;

	/**
	 * Secret key from Flickr-- unique to our registered application
	 */
	private static final String SECRET = "35e3f7923939e71a";

	static {
		// Connect to flickr
		try {
			FlickrService2.Connect();
		} catch (final Exception ex) {
		}
	}

	private static void Connect() throws ParserConfigurationException {

		// Try to connect at most 'CONNECT_RETRY' times before throwing
		// an exception
		ParserConfigurationException latestEx = null;
		for (int i = 0; !FlickrService2.Connected && i < FlickrService2.CONNECT_RETRY; i++) {
			try {
				// Initialize
				FlickrService2.Rest = new REST();
				FlickrService2.Rest.setHost(FlickrService2.HOST);
				FlickrService2.Flickr = new Flickr(FlickrService2.API_KEY);

				// Set the shared secret which is used for any calls which
				// require
				// signing.
				FlickrService2.ReqCon = RequestContext.getRequestContext();
				FlickrService2.ReqCon.setSharedSecret(FlickrService2.SECRET);

				// Get our picture service
				FlickrService2.PhotosInt = FlickrService2.Flickr.getPhotosInterface();
				FlickrService2.Connected = true;
			} catch (final ParserConfigurationException ex) {
				latestEx = ex;
			}
		}
		if (!FlickrService2.Connected)
			throw latestEx;
	}

	private SearchParameters Params = null;

	private int ReturnedPage = 0;

	private ImageBuffer SourcesBuffer;

	private int TargetImages;

	private ExecutorService ThreadPool;

	/**
	 * Create a new FlickrService that will make the under-lying connections to
	 * the Flickr API. Note that a new FlickrService should be initialized for
	 * each new search query.
	 * 
	 * @author scott
	 * @param sourcesBuffer
	 *            The buffer to send the query results to.
	 * @param targetImages
	 *            The number of images to fetch in each batch
	 * @param searchString
	 *            The query string to search flickr for
	 * @throws FlickrServiceException
	 */
	public FlickrService2(final ImageBuffer sourcesBuffer, final int targetImages,
			final String searchString) throws FlickrServiceException {
		if (!FlickrService2.Connected)
			try {
				FlickrService2.Connect();
			} catch (final Exception ex) {
				throw new FlickrServiceException("Cannot connect to Flickr", ex);
			}

		// Set our parameters
		Params = new SearchParameters();
		Params.setSort(SearchParameters.RELEVANCE);
		Params.setText(searchString);

		SourcesBuffer = sourcesBuffer;
		TargetImages = targetImages;

		ThreadPool = Executors.newFixedThreadPool(FlickrService2.NumThreads);

		ReturnedPage = 0;
	}

	/**
	 * In a new thread, start queuing child threads to query Flickr for results.
	 * The results will be saved in SourcesBuffer, and SourcesBuffer.isComplete
	 * will be set when it is complete
	 * 
	 * @author scott
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		final int numQueries = TargetImages / FlickrService2.PicsPerQuery;
		final ArrayList<Future<ArrayList<BufferedImage>>> queryResults = new ArrayList<Future<ArrayList<BufferedImage>>>(
				numQueries);
		for (int queryNum = 0; queryNum < numQueries; queryNum++) {
			final FlickrQuery query = new FlickrQuery(FlickrService2.PhotosInt, Params,
					FlickrService2.PicsPerQuery, ReturnedPage + queryNum);
			queryResults.add(ThreadPool.submit(query));
		}

		// Send the results from a separate loop because these calls will block
		for (int queryNum = 0; queryNum < numQueries; queryNum++)
			// FIXME: Should we be notifying on each result?
			try {
				SourcesBuffer
						.addToImageBuffer(queryResults.get(queryNum).get());
				ReturnedPage++;

				/*
				 * TODO: Find a way to intuitively handle exceptions within the
				 * "run" function. This is a problem because it appears that
				 * Runnable.run doesn't support throwing exceptions, because we
				 * are in another thread. We will have to find some other way.
				 */
			} catch (final ExecutionException ex) {
				// TODO: Handle ExcecutionException
			} catch (final InterruptedException ex) {
				// TODO: Handle InterruptedException
			}

		SourcesBuffer.isComplete = true;
		// FIXME: Do we need to notify, or will SourcesBuffer to that?
	}
}
