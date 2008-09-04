package wosaic.utilities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import wosaic.Sources;

import com.facebook.api.FacebookXmlRestClient;

import edu.stanford.ejalbert.BrowserLauncher;

/**
 * Utility for interfacing with Facebook
 * 
 * @author carl-erik svensson
 */
public class Facebook extends SourcePlugin {

	/**
	 * Subclass to handle the event when the user clicks the "Authenticate"
	 * button
	 */
	public class AuthenticationAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6285228755908052783L;

		/**
		 * Handle the actual event. Simply call checkAuthentication()
		 * 
		 * @param e Event parameters
		 */
		public void actionPerformed(final ActionEvent e) {
			// Change this to be... extensible to any service needing
			// authentication...
			// Maybe...
			checkAuthentication();
		}

	}

	private static String API_KEY = "70d85deaa9e38c122cd17bab74ce80a8";

	private static String LOGIN_URL = "http://www.facebook.com/login.php";

	private static int NUM_QUERIES = 50;

	private static String SECRET = "dc48f9f413d3dc738a4536402e2a75b1";

	private static int SMALL_SRC = 5;

	private String auth;

	private final FacebookXmlRestClient client;

	private boolean isAuthenticated;

	// Config UI Code
	/**
	 * Options UI for setting preferences
	 */
	JDialog OptionsDialog = null;

	/**
	 * The actual JPanel that will include UI elements
	 */
	JPanel OptionsPane = null;

	private final JPanel optionsPanel = null;

	private int uid;

	/**
	 * Default constructor. After this, setBuffer should be called as soon as
	 * possible to put this object in a usable state.
	 */
	public Facebook() {
		client = new FacebookXmlRestClient(Facebook.API_KEY, Facebook.SECRET);
		client.setIsDesktop(true);
		isAuthenticated = false;
		numResults = Facebook.NUM_QUERIES;

		initOptionsPane();
	}

	/**
	 * Called from either the Advanced Options or when not authenticated and
	 * generating a mosaic.
	 * 
	 * @throws Exception If the BrowserLauncher encounters an error
	 */
	public void authenticate() throws Exception {
		// Create an authentication token
		auth = client.auth_createToken();
		// System.out.println("auth token: " + auth);

		// The following functions can generate exceptions
		final BrowserLauncher browserLauncher = new BrowserLauncher(null);
		browserLauncher.openURLinBrowser(Facebook.LOGIN_URL + "?api_key="
				+ Facebook.API_KEY + "&auth_token=" + auth);
	}

	/**
	 * Authenticates with facebook for getting facebook pictures
	 * 
	 * @return if the authentication was successful
	 */
	public boolean checkAuthentication() {
		if (!hasAuthenticated())
			try {
				authenticate();
				JOptionPane
						.showMessageDialog(optionsPanel,
								"Please authenticate with Facebook.  Press OK when you have logged in.");
				verifyAuthentication();
			} catch (final Exception e) {
				System.out.println("Unable to authenticate");
				System.out.println(e);
				JOptionPane.showMessageDialog(optionsPanel,
						"Unable to authenticate.  Please try again.");
				return false;
			}

		return true;
	}

	/**
	 * Downloads the required images from Facebook.
	 * 
	 * @throws Exception If the Facebook client encounters an internal error
	 */
	/*
	 * FIXME: We need this because the Facebook client uses an "Integer" type
	 * rather than an int for photos_get. We should check this again if the
	 * Facebook client gets updated.
	 */
	@SuppressWarnings("boxing")
	public void getImages() throws Exception {

		final Document d = client.photos_get(uid);
		// FacebookXmlRestClient.printDom(d, " ");

		// Iterate through the images and read the URL
		final NodeList nl = d.getElementsByTagName("photo");
		numResults = nl.getLength();
		sourcesBuffer.signalProgressCount(numResults);

		// System.out.println(nl);
		int i = 0;
		Node photo;
		NodeList kids;

		// FIXME: Why the use of a do-while loop-- is this the best way?
		do {
			// Navigate the DOM to the source we want
			photo = nl.item(i);
			kids = photo.getChildNodes();
			final Node source = kids.item(Facebook.SMALL_SRC);

			// Kick off the work
			ThreadPool.submit(new FacebookQuery(sourcesBuffer, source));

			// Iterate
			i++;
			photo = nl.item(i);

		} while (photo != null);
	}

	/**
	 * Return a JDialog which displays some parameters that a user can configure
	 * for our plugin
	 */
	@Override
	public JDialog getOptionsDialog() {
		return OptionsDialog;
	}

	/**
	 * Return the type of our plugin.
	 */
	@Override
	public Sources.Plugin getType() {
		return Sources.Plugin.Facebook;
	}

	/**
	 * @return a flag indicating whether or not the user has authenticated with
	 *         facebook
	 */
	public boolean hasAuthenticated() {
		return isAuthenticated;
	}

	/**
	 * Initializes the config UI for Facebook
	 */
	public void initOptionsPane() {

		// Number of Search Results
		OptionsPane = new JPanel();
		OptionsPane.setLayout(new GridBagLayout());

		// Authenticate Button
		final JButton authButton = new JButton("Authenticate");
		authButton.setToolTipText("Login to Facebook to authenticate");
		authButton.addActionListener(new AuthenticationAction());
		final GridBagConstraints authConstraints = new GridBagConstraints();
		authConstraints.gridx = 0;
		authConstraints.gridy = 0;
		authConstraints.anchor = GridBagConstraints.WEST;
		OptionsPane.add(authButton, authConstraints);

		OptionsDialog = new JDialog((JFrame) null, "Facebook Options", true);
		OptionsDialog.getContentPane().add(OptionsPane);
		OptionsDialog.pack();
	}

	/**
	 * Asynchronously start doing work-- query Facebook for images and start
	 * returning them
	 */
	@Override
	public void run() {
		try {
			getImages();
		} catch (final Exception e) {
			System.out.println("Facebook: GetImages Failed!");
			System.out.println(e);
		}
	}

	/**
	 * Validate configuration parameters-- basically, make sure we are
	 * authenticated before trying to query Facebook.
	 */
	@Override
	public String validateParams() {
		if (!checkAuthentication())
			return "Facebook was unable to authenticate!";

		return null;
	}

	/**
	 * This should be called after the user has logged in.
	 * 
	 * @throws Exception If the Facebook client encounters an internal error
	 */
	public void verifyAuthentication() throws Exception {
		client.auth_getSession(auth);
		uid = client.auth_getUserId(auth);
		isAuthenticated = true;
	}
}
