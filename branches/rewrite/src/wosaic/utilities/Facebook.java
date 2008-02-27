package wosaic.utilities;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JButton;
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
 * 
 */
public class Facebook extends SourcePlugin {

	public class AuthenticationAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6285228755908052783L;

		public void actionPerformed(final ActionEvent e) {
			// Change this to be... extensible to any service needing
			// authentication...
			// Maybe...
			checkAuthentication();
		}

	}

	public static String API_KEY = "70d85deaa9e38c122cd17bab74ce80a8";

	public static int BIG_SRC = 4;

	public static String LOGIN_URL = "http://www.facebook.com/login.php";

	public static int NUM_QUERIES = 50;

	public static int NUM_THREADS = 10;

	public static String SECRET = "dc48f9f413d3dc738a4536402e2a75b1";

	public static int SMALL_SRC = 5;

	public static int SRC = 3;

	private String auth;

	private FacebookXmlRestClient client;

	private boolean isAuthenticated;

	// Config UI Code
	JFrame OptionsFrame = null;

	JPanel OptionsPane = null;

	private final JPanel optionsPanel = null;

	private ExecutorService ThreadPool;

	private int uid;

	/**
	 * Default constructor. After this, setBuffer should be called as soon as
	 * possible to put this object in a usable state.
	 */
	public Facebook() {
		client = new FacebookXmlRestClient(Facebook.API_KEY, Facebook.SECRET);
		client.setIsDesktop(true);
		ThreadPool = Executors.newFixedThreadPool(Facebook.NUM_THREADS);
		isAuthenticated = false;

		initOptionsPane();
	}

	/**
	 * This constructor should fully initialize the facebook object.
	 * 
	 * @param buf
	 *            the shared image buffer initiated by the controller
	 */
	public Facebook(final ImageBuffer buf) {
		client = new FacebookXmlRestClient(Facebook.API_KEY, Facebook.SECRET);
		client.setIsDesktop(true);
		sourcesBuffer = buf;
		ThreadPool = Executors.newFixedThreadPool(Facebook.NUM_THREADS);
		isAuthenticated = false;

		initOptionsPane();
	}

	/**
	 * Called from either the Advanced Options or when not authenticated and
	 * generating a mosaic.
	 * 
	 * @throws Exception
	 */
	public void authenticate() throws Exception {
		// Create an authentication token
		auth = client.auth_createToken();
		// System.out.println("auth token: " + auth);

		// The following functions can generate exceptions
		final BrowserLauncher browserLauncher = new BrowserLauncher(null);
		browserLauncher.openURLinBrowser(Facebook.LOGIN_URL + "?api_key=" + Facebook.API_KEY
				+ "&auth_token=" + auth);
	}

	/**
	 * Authenticates with facebook for getting facebook pictures
	 * 
	 * @return if the authentication was successful
	 */
	public boolean checkAuthentication() {
		if (!hasAuthenticated()) {
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
		}

		return true;
	}

	/**
	 * Downloads the required images from Facebook.
	 * 
	 * @throws Exception
	 */
	public void getImages() throws Exception {

		final Document d = client.photos_get(uid);
		// FacebookXmlRestClient.printDom(d, " ");

		// Iterate through the images and read the URL
		final NodeList nl = d.getElementsByTagName("photo");
		// System.out.println(nl);
		int i = 0;
		Node photo;
		NodeList kids;
		final ArrayList<Future<BufferedImage>> queryResults = new ArrayList<Future<BufferedImage>>();

		do {
			// Navigate the DOM to the source we want
			photo = nl.item(i);
			kids = photo.getChildNodes();
			final Node source = kids.item(Facebook.SMALL_SRC);

			// Kick off the work
			queryResults.add(ThreadPool.submit(new FacebookQuery(sourcesBuffer,
					source)));

			// Iterate
			i++;
			photo = nl.item(i);

		} while (photo != null);

		// Wait for all threads to finish
		for (int query = 0; query < Facebook.NUM_QUERIES; query++) {
			// Pop off the BufferedImage when this future is ready
			queryResults.get(query).get();
		}

	}

	@Override
	public JFrame getOptionsPane() {
		return OptionsFrame;
	}

	@Override
	public Sources.Plugin getType() {
		return Sources.Plugin.Facebook;
	}

	/**
	 * 
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
		authButton.addActionListener(new AuthenticationAction());
		final GridBagConstraints authConstraints = new GridBagConstraints();
		authConstraints.gridx = 0;
		authConstraints.gridy = 0;
		authConstraints.anchor = GridBagConstraints.WEST;
		OptionsPane.add(authButton, authConstraints);

		OptionsFrame = new JFrame("Facebook Options");
		OptionsFrame.getContentPane().setPreferredSize(new Dimension(400, 200));
		OptionsFrame.getContentPane().add(OptionsPane);
		OptionsFrame.pack();
	}

	@Override
	public void run() {
		try {
			getImages();
		} catch (final Exception e) {
			System.out.println("Facebook: GetImages Failed!");
			System.out.println(e);
		}

		// Signal when this is complete
		sourcesBuffer.signalComplete();
	}

	@Override
	public String validateParams() {
		if (!checkAuthentication())
			return "Facebook was unable to authenticate!";

		return null;
	}

	/**
	 * This should be called after the user has logged in.
	 * 
	 * @throws Exception
	 */
	public void verifyAuthentication() throws Exception {
		client.auth_getSession(auth);
		uid = client.auth_getUserId(auth);
		isAuthenticated = true;
	}
}
