package wosaic.utilities;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.facebook.api.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import wosaic.Sources;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

/**
 * Utility for interfacing with Facebook
 * @author carl-erik svensson
 *
 */
public class Facebook extends SourcePlugin {

	public static String API_KEY = "70d85deaa9e38c122cd17bab74ce80a8";
	public static String SECRET = "dc48f9f413d3dc738a4536402e2a75b1";
	public static String LOGIN_URL = "http://www.facebook.com/login.php";
	public static int SMALL_SRC = 5;
	public static int BIG_SRC = 4;
	public static int SRC = 3;
	public static int NUM_THREADS = 10;
	public static int NUM_QUERIES = 50;
	
	private boolean enabled;
	private FacebookXmlRestClient client;
	private String auth;
	private int uid;
	private ExecutorService ThreadPool;
	private boolean isAuthenticated;
	private JPanel optionsPanel = null;
	
	/**
	 * This constructor should fully initialize the facebook object.
	 * @param buf the shared image buffer initiated by the controller
	 */
	public Facebook(ImageBuffer buf) {
		client = new FacebookXmlRestClient(API_KEY, SECRET);
		client.setIsDesktop(true);
		sourcesBuffer = buf;
		ThreadPool = Executors.newFixedThreadPool(NUM_THREADS);
		isAuthenticated = false;
		
		initOptionsPane();
	}
	
	/**
	 * Default constructor.  After this, setBuffer should be called
	 * as soon as possible to put this object in a usable state.
	 */
	public Facebook() {
		client = new FacebookXmlRestClient(API_KEY, SECRET);
		client.setIsDesktop(true);
		ThreadPool = Executors.newFixedThreadPool(NUM_THREADS);
		isAuthenticated = false;
		
		initOptionsPane();
	}

	
	/**
	 * Called from either the Advanced Options or when not authenticated
	 * and generating a mosaic.
	 * @throws Exception
	 */
	public void authenticate() throws Exception {
		// Create an authentication token
		auth = client.auth_createToken();
		System.out.println("auth token: " + auth);
		
		// The following functions can generate exceptions
		BrowserLauncher browserLauncher = new BrowserLauncher(null);
		browserLauncher.openURLinBrowser(LOGIN_URL + "?api_key=" +
				API_KEY + "&auth_token=" + auth);
	}
	
	/**
	 * This should be called after the user has logged in.
	 * @throws Exception
	 */
	public void verifyAuthentication() throws Exception {
		client.auth_getSession(auth);
		uid = client.auth_getUserId(auth);
		isAuthenticated = true;
	}
	
	/**
	 * 
	 * @return a flag indicating whether or not the user has
	 * authenticated with facebook
	 */
	public boolean hasAuthenticated() {
		return isAuthenticated;
	}
	
	/**
	 * Downloads the required images from Facebook.
	 * @throws Exception
	 */
	public void getImages() throws Exception {
		
		Document d = client.photos_get(uid);
		//FacebookXmlRestClient.printDom(d, "  ");
		
		// Iterate through the images and read the URL
		NodeList nl = d.getElementsByTagName("photo");
		System.out.println(nl);
		int i = 0;
		Node photo;
		NodeList kids;
		ArrayList<Future<BufferedImage>> queryResults = new ArrayList<Future<BufferedImage>>();
		
		do {
			// Navigate the DOM to the source we want
			photo = nl.item(i);
			kids = photo.getChildNodes();
			Node source = kids.item(SMALL_SRC);
			
			// Kick off the work
			queryResults.add(ThreadPool.submit(new FacebookQuery(sourcesBuffer, source)));
			
			// Iterate
			i++;
			photo = nl.item(i);
			
		} while(photo != null);
		
		// Wait for all threads to finish
		for (int query = 0; query < NUM_QUERIES; query++) {
			// Pop off the BufferedImage when this future is ready
			queryResults.get(query).get();
		}
		
	}

	public void run() {
		try {
			getImages();
		} catch (Exception e) {
			System.out.println("Facebook: GetImages Failed!");
			System.out.println(e);
		}
		
		// Signal when this is complete
		sourcesBuffer.signalComplete();
	}

	
	/**
	 * Authenticates with facebook for getting facebook pictures
	 * @return if the authentication was successful
	 */
	public boolean checkAuthentication() {
		if(!hasAuthenticated()) {
			try {
				authenticate();
				JOptionPane.showMessageDialog(optionsPanel, 
						"Please authenticate with Facebook.  Press OK when you have logged in.");
				verifyAuthentication();
			} catch (Exception e) {
				System.out.println("Unable to authenticate");
				System.out.println(e);
				JOptionPane.showMessageDialog(optionsPanel, "Unable to authenticate.  Please try again.");
				return false;
			}
		}
		
		return true;
	}

	public String getType() {
		return Sources.FACEBOOK;
	}

	public String validateParams() {
		if (!checkAuthentication()) {
			return "Facebook was unable to authenticate!";
		}
		
		return null;
	}

	// Config UI Code
	JFrame OptionsFrame = null;
	JPanel OptionsPane = null;
	
	/**
	 * Initializes the config UI for Facebook
	 */
	public void initOptionsPane() {
		
		// Number of Search Results
		OptionsPane = new JPanel();
		OptionsPane.setLayout(new GridBagLayout());
		
		// Authenticate Button
		JButton authButton = new JButton("Authenticate");
		authButton.addActionListener(new AuthenticationAction());
		GridBagConstraints authConstraints = new GridBagConstraints();
		authConstraints.gridx = 0;
		authConstraints.gridy = 0;
		authConstraints.anchor = GridBagConstraints.WEST;
		OptionsPane.add(authButton, authConstraints);
		
		OptionsFrame = new JFrame("Facebook Options");
		OptionsFrame.getContentPane().setPreferredSize(new Dimension(400, 200));
		OptionsFrame.getContentPane().add(OptionsPane);
		OptionsFrame.pack();
	}
	
	public JFrame getOptionsPane() {
		return OptionsFrame;
	}
	
	public class AuthenticationAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			// Change this to be... extensible to any service needing authentication...
			// Maybe...
			checkAuthentication();
		}
		
	}
}