package wosaic.utilities;


/**
 * Make the actual photo query to the Flickr server and return the results
 * asynchronously
 * 
 * @author scott
 * 
 */
public class FlickrQuery implements Runnable {

	// private final int Page;
	// private final SearchParameters Params;
	// private final int PerPage;
	// private final PhotosInterface PhotosInt;

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
	// public FlickrQuery(final PhotosInterface photosInt,
	// final SearchParameters params, final int perPage, final int page) {
	// PhotosInt = photosInt;
	// Params = params;
	// PerPage = perPage;
	// Page = page;
	// }
	public FlickrQuery(final String url) {
		// TODO: Write constructor
	}

	/**
	 * Asynchronously make the query to Flickr.
	 * 
	 * @return a list of the photos returned from Flickr
	 */
	// public ArrayList<BufferedImage> call() {
	//
	// final ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();
	//
	// PhotoList pl = null;
	// try {
	// pl = PhotosInt.search(Params, PerPage, Page);
	// } catch (final FlickrException ex) {
	// System.out.println("Flickr Exception in " + Page + "! "
	// + ex.getMessage());
	// } catch (final IOException ex) {
	// System.out.println("IOException " + Page + "! " + ex.getMessage());
	// } catch (final SAXException ex) {
	// System.out.println("SAXException " + Page + "! " + ex.getMessage());
	// }
	//
	// if (pl != null)
	// for (int i = 0; i < pl.size(); i++)
	// try {
	// ret.add(ImageIO.read(new URL(((Photo) pl.get(i))
	// .getSmallSquareUrl())));
	// } catch (final Exception ex) {
	// // TODO: Handle exceptions here
	// }
	//
	// return ret;
	// }
	public void run() {
		// TODO Auto-generated method stub

	}
}
