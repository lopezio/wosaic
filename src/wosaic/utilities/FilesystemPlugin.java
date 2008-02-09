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
import javax.swing.filechooser.FileFilter;
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
	 * The text box where the user can insert the directory
	 * to search
	 */
	JTextField DirTextBox = null;

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
		initOptionsPane();
	}
	

	public void run() {
		/*
		try {
			getImages();
		} catch (Exception e) {
			System.out.println("Facebook: GetImages Failed!");
			System.out.println(e);
		}
		
		// Signal when this is complete
		sourcesBuffer.signalComplete();
		*/
		// TODO: Something here...
		return;
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
		JButton B3 = new JButton("Cancel");
		JCheckBox C1 = new JCheckBox("Search Subdirectories", true);

		// Layout our components
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(L1)
					.addGroup(layout.createSequentialGroup()
						.addComponent(DirTextBox)
						.addComponent(B1))
					.addGroup(layout.createSequentialGroup()
						.addComponent(C1)
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
						.addComponent(C1)
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
}
