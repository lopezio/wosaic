import java.util.ArrayList;
import java.awt.image.*;

import javax.xml.parsers.ParserConfigurationException;
import java.lang.Exception;
import org.omg.CORBA.portable.ApplicationException;


import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.groups.*;

/**
 * 
 */

/**
 * @author scott
 *
 */
public class FlickrService {

	private static String HOST = "www.flickr.com";
	private static String API_KEY = "149e20572d673fa56f46a0ed0afe464f";
	private static String SECRET = "35e3f7923939e71a";
	
	private Flickr F = null;
	private REST Rest = null;
	private RequestContext ReqCon = null;
	private PhotosInterface PhotosInt = null;
	private SearchParameters Params = null;

	public FlickrService() throws Exception{
		// Connect to flickr
		try { Connect(); }
		catch (Exception ex) {
			throw new Exception("Could not connect to Flickr", ex.getCause());
		}

		// Get our picture service
		PhotosInt = F.getPhotosInterface();

		// Set our parameters
		Params = new SearchParameters();
		Params.setSort(SearchParameters.RELEVANCE);
	}

	public void Connect() throws ParserConfigurationException{
		// Initialize
		Rest = new REST();
		Rest.setHost(HOST);
		F = new Flickr(API_KEY);
		
		// Set the shared secret which is used for any calls which require
		// signing.
		ReqCon = RequestContext.getRequestContext();
		ReqCon.setSharedSecret(SECRET);
	}

	public BufferedImage GetImagePool(String searchStr, int n) {
		BufferedImage ret = null;
		/*SearchParameters sp;
		

			//Params.setTags(new String[] { String.valueOf(picLetter) });
			sp = Params;


		try {
			PhotoList pl = PhotosInt.search(sp, 20, 1);
			// Get a random integer for the photo to get, between 1 - 50.
			Photo p = (Photo) pl.get(1);
			ret = p.getSmallSquareImage();
		} catch (Exception ex) {
			//System.out.print("Error querying Flickr: '" + picLetter + "'");
		}

*/
		return ret;
	}
	
	public ArrayList <BufferedImage> GetMoreResults()
	{
		return null;
	}
}