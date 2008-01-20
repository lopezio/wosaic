/**
 * 
 */
package hf;

import java.util.Random;
import java.awt.image.*;

import javax.xml.parsers.ParserConfigurationException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.photos.*;
import com.aetrion.flickr.groups.*;

/**
 * @author scott
 * 
 */
public class PictureService {

	private static String HOST = "www.flickr.com";
	private static String API_KEY = "149e20572d673fa56f46a0ed0afe464f";
	private static String SECRET = "35e3f7923939e71a";
	private Random RandGen = null;
	private Flickr F = null;
	private REST Rest = null;
	private RequestContext ReqCon = null;
	private PhotosInterface PhotosInt = null;
	private SearchParameters LetterParams = null;
	private SearchParameters FillerParams = null;

	public PictureService() throws ParserConfigurationException {
		// Connect to flickr
		Connect();

		// Get our picture service
		PhotosInt = F.getPhotosInterface();

		// Set our parameters
		LetterParams = new SearchParameters();
		LetterParams.setSort(SearchParameters.RELEVANCE);
		LetterParams.setGroupId(getGroupId("One Letter"));

		FillerParams = new SearchParameters();
		FillerParams.setSort(SearchParameters.RELEVANCE);
		FillerParams.setGroupId(getGroupId("Punctuation"));
		FillerParams.setTags(new String[]{"hyphen"});
		
		// Create random number generator
		RandGen = new Random();
	}

	public void Connect() throws ParserConfigurationException {
		// Initialize
		Rest = new REST();
		Rest.setHost(HOST);
		F = new Flickr(API_KEY);
		Flickr.debugStream = false;
		// Set the shared secret which is used for any calls which require
		// signing.
		ReqCon = RequestContext.getRequestContext();
		ReqCon.setSharedSecret(SECRET);
	}

	public BufferedImage GetLetterImage(char picLetter) {
		BufferedImage ret = null;
		SearchParameters sp;
		
		if (Character.isLetter(picLetter)) {
			LetterParams.setTags(new String[] { String.valueOf(picLetter) });
			sp = LetterParams;
		} else 
			// If we don't have a letter, assume a space, and return a filler
			// character.
			sp = FillerParams;

		try {
			PhotoList pl = PhotosInt.search(sp, 20, 1);
			// Get a random integer for the photo to get, between 1 - 50.
			int x = RandGen.nextInt(pl.size());
			Photo p = (Photo) pl.get(x);
			ret = p.getSmallSquareImage();
		} catch (Exception ex) {
			System.out.print("Error querying Flickr: '" + picLetter + "'");
		}


		return ret;
	}

	private String getGroupId(String grpstr) {
		GroupsInterface gi = F.getGroupsInterface();
		try {
			GroupList gl = (GroupList) gi.search(grpstr, 1, 1);
			Group g = (Group) gl.get(0);
			return g.getId();
		} catch (Exception ex) {
			System.out.println("Error, could not find group!");
			return "";
		}

	}
}
