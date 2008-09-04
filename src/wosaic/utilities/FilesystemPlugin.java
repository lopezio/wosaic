package wosaic.utilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import wosaic.Sources;

/**
 * Utility for interfacing with Facebook
 * 
 * @author carl-erik svensson
 */
public class FilesystemPlugin extends SourcePlugin {

	/**
	 * A simple action listener to detect when the user has hit the browse
	 * button. Will spawn the appropriate file dialog.
	 * 
	 * @author scott
	 */
	protected class BrowseButtonAL implements ActionListener {

		/**
		 * The dialog UI for the user to choose a search directory
		 */
		protected JFileChooser DirChooser;

		/**
		 * Default constructor-- simply setup our JFileChooser
		 */
		public BrowseButtonAL() {
			super();
			DirChooser = new JFileChooser();
			DirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}

		/**
		 * Handle event-- spawn our file dialog and set parameters when it
		 * returns
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			final int ret = DirChooser.showOpenDialog(OptionsPane);
			if (ret == JFileChooser.APPROVE_OPTION)
				DirTextBox.setText(DirChooser.getSelectedFile()
						.getAbsolutePath());
		}
	}

	/**
	 * Simple action listener to handle when the user clicks "Cancel" on the
	 * configuration dialog
	 * 
	 * @author scott
	 */
	protected class CancelButtonAL implements ActionListener {

		/**
		 * Handle our action-- simply hide the window
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			OptionsDialog.setVisible(false);
		}
	}

	/**
	 * Simple action listener for when the user clicks on the OK button in the
	 * configuration dialog.
	 * 
	 * @author scott
	 */
	protected class OKButtonAL implements ActionListener {

		/**
		 * Handle the action-- validate data and set the appropriate variables
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			if (!fieldsAreValid())
				JOptionPane.showMessageDialog(OptionsDialog,
						"Please enter a valid directory", "Warning",
						JOptionPane.WARNING_MESSAGE);
			else {
				SearchDirectory = new File(DirTextBox.getText());
				RecurseSubdirs = RecurseCheckBox.isSelected();

				OptionsDialog.setVisible(false);
			}
		}

		/**
		 * Simply check that the user has entered valid data
		 * 
		 * @return true if everything validates, false otherwise
		 */
		protected boolean fieldsAreValid() {
			final File dir = new File(DirTextBox.getText());
			return dir.isDirectory();
		}
	}

	/**
	 * The text box where the user can insert the directory to search
	 */
	protected JTextField DirTextBox = null;

	/**
	 * The frame that we wrap everything in
	 */
	protected JDialog OptionsDialog = null;

	/**
	 * The panel that actually contains all of our configuration UI elements
	 */
	protected JPanel OptionsPane = null;

	/**
	 * The options panel for the user to select a directory.
	 */
	protected JPanel optionsPanel = null;

	/**
	 * A UI element for the user to select whether or not to recurse
	 */
	JCheckBox RecurseCheckBox = null;

	/**
	 * Whether or not we should search inside subdirectories
	 */
	protected boolean RecurseSubdirs = true;

	/**
	 * The directory we will search
	 */
	protected File SearchDirectory;

	/**
	 * Default constructor. After this, setBuffer should be called as soon as
	 * possible to put this object in a usable state.
	 */
	public FilesystemPlugin() {

		initOptionsPane();
	}

	/**
	 * Spawn the individual FileQueries, and then ping them for results to
	 * return
	 * 
	 * @param F The top directory that we should search
	 */
	public void getImages(final File F) {
		// Create our file filter
		final WosaicFilter filter = new WosaicFilter(RecurseSubdirs);
		final ArrayList<Future<BufferedImage>> queryResults = new ArrayList<Future<BufferedImage>>();

		// Create our queries
		spawnQueries(F, filter, queryResults);

		// Iterate over results and return them
		for (int queryNum = 0; queryNum < queryResults.size(); queryNum++) {
			final ArrayList<BufferedImage> images = new ArrayList<BufferedImage>(
					1);
			try {
				images.add(queryResults.get(queryNum).get());
			} catch (final Exception e) {
				System.err
						.println("Exception calling getImages in FilesystemPlugin:");
				System.err.println(e.getMessage());
			}
			sourcesBuffer.addToImageBuffer(images);
		}
	}

	/**
	 * Provide the interface with our configuration JDialog
	 * 
	 * @see wosaic.utilities.SourcePlugin#getOptionsDialog()
	 */
	@Override
	public JDialog getOptionsDialog() {
		return OptionsDialog;
	}

	/**
	 * Provide the interface with our plugin "type" string
	 * 
	 * @see wosaic.utilities.SourcePlugin#getType()
	 */
	@Override
	public Sources.Plugin getType() {
		return Sources.Plugin.Filesystem;
	}

	/**
	 * Initializes the config UI for the Filesystem plugin
	 */
	public void initOptionsPane() {

		// Number of Search Results
		OptionsPane = new JPanel();

		// Create our Components
		final JLabel L1 = new JLabel("Pictures Directory:");
		DirTextBox = new JTextField(25);
		DirTextBox
				.setToolTipText("The folder that will be searched for pictures");
		L1.setLabelFor(DirTextBox);
		final JButton B1 = new JButton("Browse..");
		B1.setToolTipText("Browse for a folder to use for pictures");
		B1.addActionListener(new BrowseButtonAL());
		final JButton B2 = new JButton("Ok");
		B2.addActionListener(new OKButtonAL());
		final JButton B3 = new JButton("Cancel");
		B3.addActionListener(new CancelButtonAL());
		B2.setPreferredSize(B3.getPreferredSize());
		RecurseCheckBox = new JCheckBox("Search Subdirectories", true);
		RecurseCheckBox
				.setToolTipText("Search for pictures in subdirectories as well");
		final JPanel TopPane = new JPanel(new GridBagLayout());
		final JPanel BottomPane = new JPanel(
				new FlowLayout(FlowLayout.TRAILING));

		// Layout our components
		OptionsPane.setLayout(new BorderLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 2, 2);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.NONE;
		TopPane.add(L1, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(3, 5, 2, 2);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		TopPane.add(DirTextBox, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(3, 3, 2, 5);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		TopPane.add(B1, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(3, 5, 0, 5);
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		TopPane.add(RecurseCheckBox, c);

		BottomPane.add(B2);
		BottomPane.add(B3);

		OptionsPane.add(TopPane, BorderLayout.CENTER);
		OptionsPane.add(BottomPane, BorderLayout.PAGE_END);

		OptionsDialog = new JDialog((JFrame) null, "Local Files Options", true);
		OptionsDialog.getContentPane().add(OptionsPane);
		OptionsDialog.pack();
	}

	/**
	 * Asynchronously search our local filesystem for files, and return their
	 * results
	 * 
	 * @see wosaic.utilities.SourcePlugin#run()
	 */
	@Override
	public void run() {
		// FIXME This is a hack to get the progress bar to be alive
		sourcesBuffer.signalProgressCount(10);
		getImages(SearchDirectory);
	}

	/**
	 * Spawn a FileQuery for each image file in the directory. This function
	 * recursively calls itself for each subdirectory it finds
	 * 
	 * @param F The directory to look for pictures in
	 * @param filter The file filter to weed out non-pictures
	 * @param queryResults The ArrayList to add our results to
	 */
	public void spawnQueries(final File F, final FileFilter filter,
			final ArrayList<Future<BufferedImage>> queryResults) {
		final File[] files = F.listFiles(filter);
		for (final File element : files)
			if (element.isDirectory())
				spawnQueries(element, filter, queryResults);
			else
				queryResults.add(ThreadPool.submit(new FileQuery(element)));
	}

	/**
	 * Provide the interface a way to validate our parameters before it actually
	 * calls run
	 * 
	 * @see wosaic.utilities.SourcePlugin#validateParams()
	 */
	@Override
	public String validateParams() {

		if (SearchDirectory == null)
			return "Please enter a search directory for the Local Files plugin";
		return null;
	}
}
