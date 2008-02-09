package wosaic.utilities;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import java.io.FileFilter;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import wosaic.Sources;

/**
 * Utility for interfacing with Facebook
 * @author carl-erik svensson
 *
 */
public class FilesystemPlugin extends SourcePlugin {

	/**
	 * The directory we will search
	 */
	protected File SearchDirectory;

	/**
	 * Whether or not we should search inside subdirectories
	 */
	protected boolean RecurseSubdirs = true;

	/**
	 * The text box where the user can insert the directory
	 * to search
	 */
	JTextField DirTextBox = null;

	/**
	 * A UI element for the user to select whether or not to recurse
	 */
	JCheckBox RecurseCheckBox = null;

	/**
	 * The options panel for the user to select a directory.
	 */
	protected JPanel optionsPanel = null;
	
	/**
	 * Number of threads we should spawn to query pictures
	 */
	static protected int NUM_THREADS = 10;

	/**
	 * The thread manager we query for all of our requests
	 */
	protected ExecutorService ThreadPool;

	/**
	 * This constructor should fully initialize the filesystem plugin.
	 * @param buf the shared image buffer initiated by the controller
	 */
	public FilesystemPlugin(ImageBuffer buf) {
		sourcesBuffer = buf;
		ThreadPool = Executors.newFixedThreadPool(NUM_THREADS);
		
		initOptionsPane();
	}
	
	/**
	 * Default constructor.  After this, setBuffer should be called
	 * as soon as possible to put this object in a usable state.
	 */
	public FilesystemPlugin() {
		ThreadPool = Executors.newFixedThreadPool(NUM_THREADS);
		initOptionsPane();
	}
	

	public void run() {
		if (SearchDirectory == null) {
			OptionsFrame.setVisible(true);
			//FIXME: Is there a better way to wait for a dialog?
			while(OptionsFrame.isVisible())
				try {Thread.sleep(300); } catch(Exception e) {}

			if (SearchDirectory == null) {
				// If it's still null, then the user must
				// not actually want to use the plugin
				sourcesBuffer.signalComplete();
				return;
			}
		}
		getImages(SearchDirectory);
		
		// Signal when this is complete
		sourcesBuffer.signalComplete();
		return;
	}

	public void getImages(File F) {
		// Create our file filter
		WosaicFilter filter = new WosaicFilter(RecurseSubdirs);
		ArrayList<Future<BufferedImage>> queryResults = new ArrayList<Future<BufferedImage>>();

		// Create our queries
		spawnQueries(F, filter, queryResults);

		// Iterate over results and return them
		for (int queryNum = 0; queryNum < queryResults.size(); queryNum++) {
			ArrayList<BufferedImage> images = new ArrayList<BufferedImage>(1);
			try {images.add(queryResults.get(queryNum).get());}catch (Exception e) {}
			sourcesBuffer.addToImageBuffer(images);
		}
	}

	public void spawnQueries(File F, FileFilter filter, ArrayList<Future<BufferedImage>> queryResults) {
		// DEBUG
		System.err.println("Processing directory: " + F.getName());
		File[] files = F.listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory())
				spawnQueries(files[i], filter, queryResults);
			else {
				System.err.println("Starting query for file: " + files[i].getName());
				queryResults.add(ThreadPool.submit(new FileQuery(files[i])));
			}
		}
	}


	public String getType() {
		return Sources.LOCAL;
	}

	public String validateParams() {
		
		return null;
	}

	// Config UI Code
	JFrame OptionsFrame = null;
	JPanel OptionsPane = null;
	
	/**
	 * Initializes the config UI for the Filesystem plugin
	 */
	public void initOptionsPane() {
		
		// Number of Search Results
		OptionsPane = new JPanel();
		GroupLayout layout = new GroupLayout(OptionsPane);
		OptionsPane.setLayout(layout);
		
		// Automatically create appropriate gaps
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// Create our Components
		JLabel L1 = new JLabel("Pictures Directory:");
		DirTextBox = new JTextField(25);
		L1.setLabelFor(DirTextBox);
		JButton B1 = new JButton("Browse..");
		B1.addActionListener(new BrowseButtonAL());
		JButton B2 = new JButton("Ok");
		B2.addActionListener(new OKButtonAL());
		JButton B3 = new JButton("Cancel");
		B3.addActionListener(new CancelButtonAL());
		RecurseCheckBox = new JCheckBox("Search Subdirectories", true);

		// Layout our components
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(L1)
					.addGroup(layout.createSequentialGroup()
						.addComponent(DirTextBox)
						.addComponent(B1))
					.addGroup(layout.createSequentialGroup()
						.addComponent(RecurseCheckBox)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                     GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(B2)
						.addComponent(B3))
					);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(L1)
					.addGroup(layout.createParallelGroup()
						.addComponent(DirTextBox)
						.addComponent(B1))
					.addGroup(layout.createParallelGroup()
						.addComponent(RecurseCheckBox)
						.addComponent(B2)
						.addComponent(B3))
					);

		OptionsFrame = new JFrame("Local Files Options");
		OptionsFrame.getContentPane().add(OptionsPane);
		OptionsFrame.pack();
	}
	
	public JFrame getOptionsPane() {
		return OptionsFrame;
	}

	class BrowseButtonAL implements ActionListener {

		protected JFileChooser DirChooser;

		public BrowseButtonAL() {
			super();
			DirChooser = new JFileChooser();
			DirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}

		public void actionPerformed(ActionEvent e) {
			//DirTextBox.setText("this is a test");
			int ret = DirChooser.showOpenDialog(OptionsPane);
			if (ret == JFileChooser.APPROVE_OPTION) {
				DirTextBox.setText(DirChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
	
	class OKButtonAL implements ActionListener {
		
		protected boolean fieldsAreValid() {
			File dir = new File(DirTextBox.getText());
			return dir.isDirectory();
		}

		public void actionPerformed(ActionEvent e) {
			if (!fieldsAreValid())
				JOptionPane.showMessageDialog(OptionsFrame,
							      "Please enter a valid directory",
							      "Warning",
							      JOptionPane.WARNING_MESSAGE);
			else {
				SearchDirectory = new File(DirTextBox.getText());
				RecurseSubdirs = RecurseCheckBox.isSelected();

				OptionsFrame.setVisible(false);
			}
		}
	}

	class CancelButtonAL implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			OptionsFrame.setVisible(false);
		}
	}
}
