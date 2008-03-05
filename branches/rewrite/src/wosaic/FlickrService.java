package wosaic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import wosaic.exceptions.FlickrServiceException;
import wosaic.utilities.FlickrQuery;
import wosaic.utilities.SourcePlugin;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * Our interface for retrieving images from Flickr. Each FlickrService object is
 * unique to a specific search string. Queries to Flickr are made asynchronously
 * through the flickrj API
 * 
 * @author scott
 * 
 */
public class FlickrService extends SourcePlugin {

	class CancelAction extends AbstractAction {

		/**
		 * Version ID generated by Eclipse
		 */
		private static final long serialVersionUID = 5041800227836704158L;

		/**
		 * If the user cancels, simply hide our configuration screen.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			OptionsDialog.setVisible(false);
		}

	}

	class OkAction extends AbstractAction {

		/**
		 * Version ID generated by Eclipse
		 */
		private static final long serialVersionUID = -5571219156152814455L;

		/**
		 * When the user accepts the options screen, validate our parameters,
		 * and then set them.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent arg0) {
			// Figure out how many results to use
			int target = 0;

			try {
				target = Integer.parseInt(NumSearchField.getText());
				setTargetImages(target);
			} catch (final Exception e) {
				//FIXME: We shouldn't have explicit references to WosaicUI here
				/*
				final int retVal = JOptionPane.showConfirmDialog(OptionsPane,
						"Unable to parse results field, continue using default number of results: "
								+ WosaicUI.TARGET + "?", "Proceed?",
						JOptionPane.YES_NO_OPTION);
				*/
				//FIXME: We're using an arbitrarily-defined constant, ahh!
				final int retVal = JOptionPane.showConfirmDialog(OptionsPane,
						"Unable to parse results field, continue using default number of results: 500?", 
						"Proceed?",
						JOptionPane.YES_NO_OPTION);


				if (retVal != JOptionPane.NO_OPTION) {
					//FIXME: We shouldn't have explicit references to WosaicUI here
					/* setTargetImages(WosaicUI.TARGET); */
					//FIXME: We're using an arbitrarily-defined constant, ahh!
					setTargetImages(500);
				}
			}

			OptionsDialog.setVisible(false);

		}

	}

	/**
	 * API from Flickr. Unique for our registered application
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

	/**
	 * Static constructor-- initialize our Flickr API and connection to Flickr
	 */
	static {
		// Connect to flickr
		try {
			FlickrService.Connect();
		} catch (final ParserConfigurationException ex) {
			//TODO: Handle exceptions here
		}
	}

	private static void Connect() throws ParserConfigurationException {

		// Try to connect at most 'CONNECT_RETRY' times before throwing
		// an exception
		ParserConfigurationException latestEx = null;
		for (int i = 0; !FlickrService.Connected
				&& i < FlickrService.CONNECT_RETRY; i++) {
			try {
				// Initialize
				FlickrService.Rest = new REST();
				FlickrService.Rest.setHost(FlickrService.HOST);
				FlickrService.Flickr = new Flickr(FlickrService.API_KEY);

				// Set the shared secret which is used for any calls which
				// require
				// signing.
				FlickrService.ReqCon = RequestContext.getRequestContext();
				FlickrService.ReqCon.setSharedSecret(FlickrService.SECRET);

				// Get our picture service
				FlickrService.PhotosInt = FlickrService.Flickr
						.getPhotosInterface();
				FlickrService.Connected = true;
			} catch (final ParserConfigurationException ex) {
				latestEx = ex;
			}
		}
		if (!FlickrService.Connected)
			throw latestEx;
	}

	// Configuration UI Code
	JTextField NumSearchField = null;

	JDialog OptionsDialog = null;

	JPanel OptionsPane = null;

	private SearchParameters Params = null;

	private int ReturnedPage = 0;

	private int TargetImages;

	/**
	 * Create a new FlickrService that will make the under-lying connections to
	 * the Flickr API. Note that a new FlickrService should be initialized for
	 * each new search query.
	 * 
	 * This no-argument constructor essentially replaces our previous
	 * constructor, as it's required for the Sources API.
	 * 
	 * @throws FlickrServiceException
	 */
	public FlickrService() throws FlickrServiceException {
		if (!FlickrService.Connected)
			try {
				FlickrService.Connect();
			} catch (final ParserConfigurationException ex) {
				throw new FlickrServiceException("Cannot connect to Flickr", ex);
			}

		// Set our parameters
		Params = new SearchParameters();
		Params.setSort(SearchParameters.RELEVANCE);

		ReturnedPage = 0;

		initOptionsPane();
		//FIXME: We shouldn't have explicit references to WosaicUI here
		/* setTargetImages(WosaicUI.TARGET); */
		//FIXME: Arbitrary constant, oh noes!
		setTargetImages(500);
	}

	/**
	 * Return our configuration panel for the user to set parameters
	 * 
	 * @see wosaic.utilities.SourcePlugin#getOptionsDialog()
	 */
	@Override
	public JDialog getOptionsDialog() {
		return OptionsDialog;
	}

	/**
	 * String needed by the plugins API to distinguish our plugin
	 * 
	 * @see wosaic.utilities.SourcePlugin#getType()
	 */
	@Override
	public Sources.Plugin getType() {
		return Sources.Plugin.Flickr;
	}

	/**
	 * Create our options pane. Right now we only select the number of pictures
	 * to query for.
	 */
	public void initOptionsPane() {

		// Number of Search Results
		OptionsPane = new JPanel();
		OptionsPane.setLayout(new GridBagLayout());

		// Label
		final GridBagConstraints numSearchLabelConstraints = new GridBagConstraints();
		numSearchLabelConstraints.gridx = 0;
		numSearchLabelConstraints.gridy = 0;
		numSearchLabelConstraints.anchor = GridBagConstraints.WEST;
		numSearchLabelConstraints.gridwidth = 2;
		final JLabel numSearchLabel = new JLabel();
		numSearchLabel.setText("Number of Search Results to Use");
		OptionsPane.add(numSearchLabel, numSearchLabelConstraints);

		final GridBagConstraints spacerConstraints2 = new GridBagConstraints();
		spacerConstraints2.gridx = 0;
		spacerConstraints2.gridy = 1;
		spacerConstraints2.anchor = GridBagConstraints.WEST;
		final JLabel spacerLabel2 = new JLabel();
		spacerLabel2.setText("      ");
		OptionsPane.add(spacerLabel2, spacerConstraints2);

		// Search Results Field
		NumSearchField = new JTextField(8);
		//FIXME: We shouldn't have explicit references to WosaicUI here
		/* NumSearchField.setText(((Integer) WosaicUI.TARGET).toString()); */
		//FIXME: We should really just define this better somewhere else...
		NumSearchField.setText("500");
		final GridBagConstraints numSearchFieldConstraints = new GridBagConstraints();
		numSearchFieldConstraints.gridx = 1;
		numSearchFieldConstraints.gridy = 1;
		numSearchFieldConstraints.anchor = GridBagConstraints.WEST;
		numSearchFieldConstraints.ipadx = 7;
		OptionsPane.add(NumSearchField, numSearchFieldConstraints);

		// Ok Button
		final JButton okButton = new JButton("OK");
		okButton.addActionListener(new OkAction());
		final GridBagConstraints okConstraints = new GridBagConstraints();
		okConstraints.gridx = 0;
		okConstraints.gridy = 2;
		okConstraints.anchor = GridBagConstraints.WEST;
		OptionsPane.add(okButton, okConstraints);

		// Cancel Button
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelAction());
		final GridBagConstraints cancelConstraints = new GridBagConstraints();
		cancelConstraints.gridx = 1;
		cancelConstraints.gridy = 2;
		cancelConstraints.anchor = GridBagConstraints.WEST;
		OptionsPane.add(cancelButton, cancelConstraints);

		OptionsDialog = new JDialog((JFrame)null, "Flickr Options", true);
		OptionsDialog.getContentPane().add(OptionsPane);
		OptionsDialog.pack();
	}

	/**
	 * In a new thread, start queuing child threads to query Flickr for results.
	 * The results will be saved in SourcesBuffer, and SourcesBuffer.isComplete
	 * will be set when it is complete
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		final int numQueries = TargetImages / FlickrService.PicsPerQuery;

		// In most cases, our PicsPerQuery won't divide TargetImages, so we'll
		// need to run a partial query. This is especially important for
		// cases where TargetImages < PicsPerQuery.
		final int partialQueryPics = TargetImages - numQueries
				* FlickrService.PicsPerQuery;
		final boolean runPartialQuery = partialQueryPics != 0;

		final ArrayList<Future<ArrayList<BufferedImage>>> queryResults = new ArrayList<Future<ArrayList<BufferedImage>>>(
				numQueries + (runPartialQuery ? 1 : 0));

		for (int queryNum = 0; queryNum < numQueries; queryNum++) {
			final FlickrQuery query = new FlickrQuery(FlickrService.PhotosInt,
					Params, FlickrService.PicsPerQuery, ReturnedPage + queryNum);
			queryResults.add(ThreadPool.submit(query));
		}
		if (runPartialQuery) {
			final FlickrQuery query = new FlickrQuery(FlickrService.PhotosInt,
					Params, partialQueryPics, ReturnedPage + numQueries);
			queryResults.add(ThreadPool.submit(query));
		}

		// Send the results from a separate loop because these calls will block
		for (int queryNum = 0; queryNum < numQueries
				+ (runPartialQuery ? 1 : 0); queryNum++) {
			try {
				sourcesBuffer
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
				// Typically, we'll just want to retry
			}
		}

		sourcesBuffer.signalComplete();
	}

	/**
	 * Publicly-accessible method to set the string to search for
	 * 
	 * @param str
	 *            string that should be searched.
	 */
	public void setSearchString(String str) {
		Params.setText(str);
	}

	/**
	 * Publicly accesible function to set how many images to retrieve.
	 * 
	 * @param target
	 *            The number of images to retrieve.
	 */
	public void setTargetImages(final int target) {
		TargetImages = target;
	}

	/**
	 * Make sure all of our paramaters have valid values before proceeding.
	 * 
	 * @see wosaic.utilities.SourcePlugin#validateParams()
	 */
	@Override
	public String validateParams() {
		if (Params.getText() == null)
			return "Flickr has an invalid search string!";

		if (TargetImages <= 0)
			return "Flickr has an invalid number of target images!";

		if (FlickrService.Connected == false)
			return "Flickr has not connected properly!";

		return null;
	}
}
