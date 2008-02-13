package wosaic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import wosaic.ui.MosaicPane;
import wosaic.utilities.Mosaic;
import wosaic.utilities.MosaicEvent;
import wosaic.utilities.MosaicListener;
import wosaic.utilities.SourcePlugin;
import wosaic.utilities.Status;
import wosaic.utilities.WosaicFilter;

/**
 * The User interface for Wosaic, and application to create a photo-mosaic using
 * pictures drawn from Flickr.
 * 
 * @author scott
 */
public class WosaicUI extends Panel {

	/**
	 * Action to spawn the the particular Source's configuration screen.
	 * 
	 * @author carl
	 */
	public class ConfigAction extends AbstractAction {

		/**
		 * Version ID generated by Eclipse
		 */
		private static final long serialVersionUID = 6867797165699164244L;

		/**
		 * Respond to user's mouse click-- launch the plugin's configuration frame.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent arg0) {
			final String selection = (String) sourcesList.getSelectedValue();
			final SourcePlugin src = sources.findType(selection);

			if (src != null) {
				// Show confirmation... change text?
				final JFrame frame = src.getOptionsPane();
				if (frame != null) {
					frame.setVisible(true);
					System.out.println(selection + " config up!");
				} else {
					System.out.println("Unable to open options!");
				}

			}
		}

	}

	/**
	 * Disable the selected plugin-- remove it from the list of enabled
	 * plugins.
	 * 
	 * @author carl
	 */
	public class DisableAction extends AbstractAction {

		/**
		 * Version ID generated by Eclipse
		 */
		private static final long serialVersionUID = 4215032342841897617L;

		/**
		 * Respond to mouse click-- disable the plugin.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			final String selection = (String) enabledList.getSelectedValue();

			if (sources.removeSource(selection)) {
				// Show confirmation... change text?
				enabledModel.removeElement(selection);
				System.out.println(selection + " is disabled!");
			}
		}

	}

	/**
	 * Enable the selected plugin-- add it from the list of enabled
	 * plugins.
	 * 
	 * @author carl
	 */
	public class EnableAction extends AbstractAction {

		/**
		 * Version ID generated by Eclipse
		 */
		private static final long serialVersionUID = 7180501212915957688L;

		/**
		 * Respond to mouse click-- enable the plugin.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			final String selection = (String) sourcesList.getSelectedValue();

			if (sources.addSource(selection)) {
				// Show confirmation... change text?
				enabledModel.addElement(selection);
				System.out.println(selection + " is enabled!");
			}
		}

	}

	/**
	 * Action queried to create the Mosaic
	 * 
	 * @author scott
	 */
	public class GenerateMosaicAction extends AbstractAction {

		/**
		 * Generated by Eclipse
		 */
		private static final long serialVersionUID = -4914549621520228000L;

		Controller cont = null;

		Component parent = null;

		GenerateMosaicAction(final Component parent) {
			super();
			this.parent = parent;
		}

		/**
		 * Call the appropriate members to generate a Mosaic.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent evt) {

			// Validate inputs
			BufferedImage bi = null;
			int resolution;
			double multiplier;
			int numSources = 0;

			statusObject.setStatus("Validating Inputs...");
			statusObject.setProgress(0);
			statusObject.setIndeterminate(true);

			// Check the filename
			try {
				System.out
						.println("Opening our source image to grab metadata...");
				final File file = new File(FileField.getText());
				bi = ImageIO.read(file);
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(parent,
						"Please enter a valid source image.");
				statusObject.setStatus("");
				return;
			}

			// Check the search query
			final boolean flickrEnable = sources.isEnabled(Sources.FLICKR);

			if (flickrEnable && SearchField.getText().length() == 0) {
				JOptionPane.showMessageDialog(parent,
						"Please enter a search term.");
				statusObject.setStatus("");
				return;
			} else if (flickrEnable) {
				final FlickrService fl = (FlickrService) sources
						.findType(Sources.FLICKR);
				if (fl != null) {
					fl.setSearchString(SearchField.getText());
				} else {
					System.out
							.println("FlickrService was not found in the sources list!");
					statusObject.setStatus("ERR: Flickr was not enabled...");
					return;
				}
			}

			// Check that the resolution is a number
			try {
				resolution = Integer.parseInt(ResolutionField.getText());
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(parent,
						"Please enter a number for the resolution.");
				statusObject.setStatus("");
				return;
			}

			// Initialize a controller object and run it.
			final WosaicUI wos = (WosaicUI) parent;
			final int numThrds = WosaicUI.THREADS;
			final int target = WosaicUI.TARGET;

			try {
				// FIXME: Infer xDim and yDim from the image size.
				final int xDim;
				final int yDim;

				// Check the dimensions of advanced options
				if (DimensionsMultiple.isSelected()) {
					try {
						multiplier = Double.parseDouble(DimensionsMultipleField
								.getText());
					} catch (final Exception e) {
						JOptionPane
								.showMessageDialog(parent,
										"Please enter a valid number for the multiplier.");
						statusObject.setStatus("");
						return;
					}
					xDim = (int) (bi.getWidth() * multiplier);
					yDim = (int) (bi.getHeight() * multiplier);

				} else if (DimensionsOriginal.isSelected()) {
					xDim = bi.getWidth();
					yDim = bi.getHeight();

				} else { // DimensionsCustom.isSelected()
					int parsedX, parsedY;
					try {
						// First stored parsed values into temp variables,
						// because
						// xDim and yDim are marked final-- they need to be set
						// outside
						// the catch statement to avoid compiler errors.
						parsedX = Integer.parseInt(DimensionsCustomFieldX
								.getText());
						parsedY = Integer.parseInt(DimensionsCustomFieldY
								.getText());
					} catch (final Exception e) {
						JOptionPane
								.showMessageDialog(parent,
										"Please enter a valid number for the dimensions.");
						statusObject.setStatus("");
						return;
					}
					xDim = parsedX;
					yDim = parsedY;
				}

				// Check what sources we use
				final ArrayList<SourcePlugin> enSrcs = sources.getEnabledSources();

				for (int i = 0; i < enSrcs.size(); i++) {
					final String err = enSrcs.get(i).validateParams();
					if (err != null) {
						JOptionPane.showMessageDialog(parent, err);
						statusObject.setStatus("");
						return;
					}
					numSources++;
				}

				if (numSources == 0) {
					JOptionPane
							.showMessageDialog(parent,
									"Please choose at least one source in the Advanced Options!");
					statusObject.setStatus("");
					return;
				}

				// FIXME: Infer numRows and numCols from resolution and dims
				final int numRows;
				final int numCols;
				if (xDim <= yDim) {
					numRows = resolution;
					numCols = (int) ((double) xDim / yDim * numRows);
				} else {
					numCols = resolution;
					numRows = (int) ((double) yDim / xDim * numCols);
				}

				final String search = wos.SearchField.getText();
				final String mImage = wos.FileField.getText();

				// Create a new content pane
				if (ContentPanel != null)
					getJContentPane().remove(ContentPanel);
				ContentPanel = new MosaicPane(numCols, numRows);
				getJContentPane().add(ContentPanel, BorderLayout.CENTER);
				getJContentPane().validate();

				final Mosaic mosaic = new Mosaic();
				SaveAction.addMosaic(mosaic);

				// Create a listener class
				class MosaicListen implements MosaicListener {

					Mosaic mos;

					MosaicListen(final Mosaic m) {
						mos = m;
					}

					/**
					 * Updates the UI when we get word that the mosaic has
					 * changed.
					 */
					public void mosaicUpdated(final MosaicEvent e) {
						final ArrayList<Point> coords = e.Coords;
						for (int i = 0; i < coords.size(); i++) {
							final int row = coords.get(i).x;
							final int col = coords.get(i).y;

							ContentPanel.UpdateTile(row, col, mos.getPixelAt(row, col));
						}
					}

				}

				final MosaicListen listener = new MosaicListen(mosaic);
				mosaic.addMosaicEventListener(listener);

				System.out.println("Initialize our controller.");
				cont = new Controller(target, numThrds, numRows, numCols, xDim,
						yDim, search, mImage, mosaic, sources, statusObject);
				System.out.println("Call our controller thread");
				final Thread t = new Thread(cont);
				t.run();

				SaveButton.setEnabled(true);
				statusObject.setStatus("Generating Mosaic...");

			} catch (final Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	/**
	 * Creates and shows a modal open-file dialog.
	 * 
	 * @author scott
	 * 
	 */
	public class OpenFileAction extends AbstractAction {
		/**
		 * Generated by Eclipse
		 */
		private static final long serialVersionUID = -3576454135128663771L;

		JFileChooser chooser;

		/**
		 * The image file chosen to be the source of the Wosaic
		 */
		public File file = null;

		Component parent;

		OpenFileAction(final Component parent, final JFileChooser chooser) {
			super("Open...");
			this.chooser = chooser;
			this.parent = parent;

            //chooser.setAccessory(new ImagePreview(chooser));
		}

		/**
		 * Retrieve the file to open
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent evt) {
			// Set up a file filter
			chooser.addChoosableFileFilter(new WosaicFilter());

			// Show dialog; this method does not return until dialog is closed
			final int returnVal = chooser.showOpenDialog(parent);

			// Get the selected file and put it into our text field.
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();

				// File could be null if the user clicked cancel or something
				if (file != null) {
					((WosaicUI) parent).FileField.setText(file
							.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Action event listener for the Dimensions radio buttons.
	 * 
	 * @author carl-eriksvensson
	 * 
	 */
	public class RadioButtonPress extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4365602256173754168L;

		/**
		 * Respond to mouse clicks-- select the appropriate Dimensions
		 * option
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			if (DimensionsMultiple.isSelected()) {
				DimensionsCustomFieldX.setEnabled(false);
				DimensionsCustomFieldY.setEnabled(false);
				DimensionsMultipleField.setEnabled(true);
			} else if (DimensionsCustom.isSelected()) {
				DimensionsMultipleField.setEnabled(false);
				DimensionsCustomFieldX.setEnabled(true);
				DimensionsCustomFieldY.setEnabled(true);
			} else {
				DimensionsMultipleField.setEnabled(false);
				DimensionsCustomFieldX.setEnabled(false);
				DimensionsCustomFieldY.setEnabled(false);
			}
		}

	}

	/**
	 * Creates and shows a modal open-file dialog.
	 * 
	 * @author carl
	 * 
	 */
	public class SaveFileAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4126760905669525425L;

		JFileChooser chooser;

		/**
		 * The image file chosen to be the source of the Wosaic
		 */
		public File file = null;

		private Mosaic mos;

		Component parent;

		SaveFileAction(final Component parent, final JFileChooser chooser) {
			super("Save...");
			this.chooser = chooser;
			this.parent = parent;
		}

		/**
		 * Retrieve the file to open
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent evt) {
			// Update Progress Bar
			statusObject.setIndeterminate(true);
			statusObject.setStatus("Saving...");

			// Set up filter
			final WosaicFilter filter = new WosaicFilter();
			filter.removeFilter(".bmp");
			chooser.addChoosableFileFilter(filter);

			// Show dialog; this method does not return until dialog is closed
			final int returnVal = chooser.showSaveDialog(parent);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Get the selected file and save it
				file = chooser.getSelectedFile();

				// FIXME: Do the actual saving in a new thread to
				// keep the UI responsive
				final BufferedImage img = mos.createImage();
				try {
					String path = file.getAbsolutePath();
					final String lcasePath = path.toLowerCase();

					if (!lcasePath.contains(".jpg")
							&& !lcasePath.contains(".jpeg")) {
						path += ".jpg";
					}

					mos.save(img, path, "JPEG");
				} catch (final Exception e) {
					System.out.println("Save failed: ");
					System.out.println(e);
					statusObject.setIndeterminate(false);
					statusObject.setStatus("Save Failed!");
				}

				statusObject.setStatus("Save Complete!");
			} else {
				statusObject.setStatus("");
			}

			statusObject.setIndeterminate(false);
		}

		/**
		 * Associate a mosaic with this save action.
		 * 
		 * @param m
		 */
		public void addMosaic(final Mosaic m) {
			mos = m;
		}
	}

	/**
	 * Generated by Eclipse
	 */
	private static final long serialVersionUID = -7379941758951948236L;

	static final int TARGET = 500;

	static final int THREADS = 10;

	// Advanced Options panel
	private JPanel AdvancedOptions = null;

	private JButton BrowseButton = null;;

	protected MosaicPane ContentPanel = null;

	/**
	 * A reference to a controller-- what actually calls the Flickr service and
	 * JAI processor to do all the work.
	 */
	public Controller controller;

	private JRadioButton DimensionsCustom = null;

	private JTextField DimensionsCustomFieldX = null;

	private JTextField DimensionsCustomFieldY = null;

	private ButtonGroup DimensionsGroup = null;

	private JRadioButton DimensionsMultiple = null;

	private JTextField DimensionsMultipleField = null;

	private JRadioButton DimensionsOriginal = null;

	// Advanced Options
	private JPanel DimensionsPanel = null;

	private JList enabledList = null;

	private DefaultListModel enabledModel = null;

	// File I/O components
	JFileChooser FileChooser = null;

	// UI Components
	private JTextField FileField = null;

	private JLabel FileLabel = null;

	GenerateMosaicAction GenerateAction = null;

	private JButton GenerateButton = null;

	// private JLabel ImageBox = null;
	private JPanel jContentPane = null;

	OpenFileAction OpenAction = null;

	private JPanel OptionsPanel = null;

	private JProgressBar progressBar = null;

	private JTextField ResolutionField = null;

	private JLabel ResolutionLabel = null;

	SaveFileAction SaveAction = null;

	private JButton SaveButton = null;

	JFileChooser SaveChooser = null;

	private JTextField SearchField = null;

	private JLabel SearchLabel = null;

	private Sources sources;

	private JLabel SourcesLabel = null;

	private JList sourcesList = null;

	private JPanel SourcesPanel = null;

	private JLabel StatusLabel = null;

	private Status statusObject;

	private JPanel StatusPanel = null;

	// Tabbed view manager
	JTabbedPane tabbedPane = null;

	/**
	 * This is the default constructor
	 */
	public WosaicUI() {
		super();
		FileChooser = new JFileChooser();
		SaveChooser = new JFileChooser();
		OpenAction = new OpenFileAction(this, FileChooser);
		SaveAction = new SaveFileAction(this, SaveChooser);
		GenerateAction = new GenerateMosaicAction(this);
		tabbedPane = new JTabbedPane();
		progressBar = new JProgressBar();
		statusObject = new Status(progressBar);
		sources = new Sources(statusObject);
		
		tabbedPane.addTab("Mosaic", getJContentPane());
		tabbedPane.addTab("AdvancedOptions", getAdvancedOptionsPanel());
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600,400));
		add(tabbedPane);
		statusObject.setLabel(StatusLabel);
	}

	private JPanel getAdvancedOptionsPanel() {

		if (AdvancedOptions == null) {
			AdvancedOptions = new JPanel();
			AdvancedOptions.setLayout(new GridBagLayout());
			// AdvancedOptions.setLayout(new GridLayout(0,2));
			AdvancedOptions.setPreferredSize(new Dimension(600, 60));

			// Dimensions Panel
			DimensionsPanel = new JPanel();
			DimensionsPanel.setLayout(new GridBagLayout());
			// DimensionsPanel.setPreferredSize(new Dimension(400, 100));
			final GridBagConstraints dimensionsPanelConstraints = new GridBagConstraints();
			dimensionsPanelConstraints.gridx = 0;
			dimensionsPanelConstraints.gridy = 0;
			dimensionsPanelConstraints.anchor = GridBagConstraints.WEST;

			// Mosaic Dimensions Label
			final GridBagConstraints dimensionsLabelConstraints = new GridBagConstraints();
			dimensionsLabelConstraints.gridx = 0;
			dimensionsLabelConstraints.gridy = 0;
			dimensionsLabelConstraints.anchor = GridBagConstraints.WEST;
			dimensionsLabelConstraints.gridwidth = 2;
			dimensionsLabelConstraints.gridheight = 1;
			final JLabel mosaicDimensionsLabel = new JLabel();
			mosaicDimensionsLabel.setText("Mosiac Dimensions");
			DimensionsPanel.add(mosaicDimensionsLabel,
					dimensionsLabelConstraints);

			final GridBagConstraints spacerConstraints = new GridBagConstraints();
			spacerConstraints.gridx = 0;
			spacerConstraints.gridy = 1;
			spacerConstraints.anchor = GridBagConstraints.WEST;
			final JLabel spacerLabel = new JLabel();
			spacerLabel.setText("      ");
			DimensionsPanel.add(spacerLabel, spacerConstraints);

			// Mosaic Dimensions Radio Buttons - Original
			final RadioButtonPress listener = new RadioButtonPress();
			DimensionsOriginal = new JRadioButton("Original");
			DimensionsOriginal.setSelected(true);
			DimensionsOriginal.addActionListener(listener);
			final GridBagConstraints dimensionsOriginalConstraints = new GridBagConstraints();
			dimensionsOriginalConstraints.gridx = 1;
			dimensionsOriginalConstraints.gridy = 1;
			dimensionsOriginalConstraints.anchor = GridBagConstraints.WEST;
			DimensionsPanel.add(DimensionsOriginal,
					dimensionsOriginalConstraints);

			// Mosaic Dimensions Radio Buttons - Multiple
			DimensionsMultiple = new JRadioButton("Multiple");
			DimensionsMultiple.addActionListener(listener);
			final GridBagConstraints dimensionsMultipleConstraints = new GridBagConstraints();
			dimensionsMultipleConstraints.gridx = 1;
			dimensionsMultipleConstraints.gridy = 2;
			dimensionsMultipleConstraints.anchor = GridBagConstraints.WEST;
			DimensionsPanel.add(DimensionsMultiple,
					dimensionsMultipleConstraints);

			DimensionsMultipleField = new JTextField(8);
			DimensionsMultipleField.setColumns(8);
			DimensionsMultipleField.setText("1.0");
			DimensionsMultipleField.setEnabled(false);
			// DimensionsMultipleField.setPreferredSize(new Dimension(5, 30));
			final GridBagConstraints dimensionsMultipleFieldConstraints = new GridBagConstraints();
			dimensionsMultipleFieldConstraints.gridx = 1;
			dimensionsMultipleFieldConstraints.gridy = 3;
			dimensionsMultipleFieldConstraints.anchor = GridBagConstraints.WEST;
			dimensionsMultipleFieldConstraints.ipadx = 7;
			DimensionsPanel.add(DimensionsMultipleField,
					dimensionsMultipleFieldConstraints);

			// Mosaic Dimensions Radio Buttons - Custom
			DimensionsCustom = new JRadioButton("Custom");
			DimensionsCustom.addActionListener(listener);
			final GridBagConstraints dimensionsCustomConstraints = new GridBagConstraints();
			dimensionsCustomConstraints.gridx = 1;
			dimensionsCustomConstraints.gridy = 4;
			dimensionsCustomConstraints.anchor = GridBagConstraints.WEST;
			DimensionsPanel.add(DimensionsCustom, dimensionsCustomConstraints);

			DimensionsCustomFieldX = new JTextField(8);
			DimensionsCustomFieldX.setColumns(8);
			DimensionsCustomFieldX.setText("X-Dimm");
			DimensionsCustomFieldX.setEnabled(false);
			// DimensionsMultipleField.setPreferredSize(new Dimension(5, 30));
			final GridBagConstraints dimensionsMultipleCustomXConstraints = new GridBagConstraints();
			dimensionsMultipleCustomXConstraints.gridx = 1;
			dimensionsMultipleCustomXConstraints.gridy = 5;
			dimensionsMultipleCustomXConstraints.anchor = GridBagConstraints.WEST;
			// dimensionsMultipleCustomXConstraints.ipadx = 0;
			dimensionsMultipleCustomXConstraints.fill = GridBagConstraints.NONE;
			DimensionsPanel.add(DimensionsCustomFieldX,
					dimensionsMultipleCustomXConstraints);

			DimensionsCustomFieldY = new JTextField(8);
			DimensionsCustomFieldY.setColumns(8);
			DimensionsCustomFieldY.setText("Y-Dimm");
			DimensionsCustomFieldY.setEnabled(false);
			// DimensionsMultipleField.setPreferredSize(new Dimension(5, 30));
			final GridBagConstraints dimensionsMultipleCustomYConstraints = new GridBagConstraints();
			dimensionsMultipleCustomYConstraints.gridx = 2;
			dimensionsMultipleCustomYConstraints.gridy = 5;
			dimensionsMultipleCustomYConstraints.anchor = GridBagConstraints.WEST;
			dimensionsMultipleCustomYConstraints.fill = GridBagConstraints.NONE;
			// dimensionsMultipleCustomYConstraints.ipadx = 0;
			DimensionsPanel.add(DimensionsCustomFieldY,
					dimensionsMultipleCustomYConstraints);

			// Mosaic Dimensions Radio Buttons - Group
			DimensionsGroup = new ButtonGroup();
			DimensionsGroup.add(DimensionsOriginal);
			DimensionsGroup.add(DimensionsMultiple);
			DimensionsGroup.add(DimensionsCustom);

			AdvancedOptions.add(DimensionsPanel, dimensionsPanelConstraints);
			// AdvancedOptions.add(DimensionsPanel);

			// Sources Panel
			SourcesPanel = new JPanel();
			SourcesPanel.setLayout(new GridBagLayout());
			// SourcesPanel.setPreferredSize(new Dimension(250, 200));
			// DimensionsPanel.setPreferredSize(new Dimension(400, 100));
			final GridBagConstraints sourcesPanelConstraints = new GridBagConstraints();
			sourcesPanelConstraints.gridx = 1;
			sourcesPanelConstraints.gridy = 0;
			sourcesPanelConstraints.anchor = GridBagConstraints.WEST;

			// Sources Label
			final GridBagConstraints sourcesLabelConstraints = new GridBagConstraints();
			sourcesLabelConstraints.gridx = 0;
			sourcesLabelConstraints.gridy = 0;
			sourcesLabelConstraints.anchor = GridBagConstraints.WEST;
			sourcesLabelConstraints.gridwidth = 2;
			SourcesLabel = new JLabel();
			SourcesLabel.setText("Image Sources");
			SourcesPanel.add(SourcesLabel, sourcesLabelConstraints);

			// Sources list
			sourcesList = new JList(sources.getSourcesList());
			sourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			sourcesList.setLayoutOrientation(JList.VERTICAL);
			sourcesList.setVisibleRowCount(-1);

			final JScrollPane listScroller = new JScrollPane(sourcesList);
			listScroller.setPreferredSize(new Dimension(150, 80));

			final GridBagConstraints sourcesListConstraints = new GridBagConstraints();
			sourcesListConstraints.gridx = 0;
			sourcesListConstraints.gridy = 1;
			sourcesListConstraints.anchor = GridBagConstraints.WEST;
			sourcesListConstraints.gridwidth = 2;

			SourcesPanel.add(listScroller, sourcesListConstraints);

			// Enable Button
			final JButton SourcesEnableButton = new JButton("Enable");
			SourcesEnableButton.addActionListener(new EnableAction());
			final GridBagConstraints sourcesEnableConstraints = new GridBagConstraints();
			sourcesEnableConstraints.gridx = 0;
			sourcesEnableConstraints.gridy = 2;
			sourcesEnableConstraints.anchor = GridBagConstraints.WEST;
			SourcesPanel.add(SourcesEnableButton, sourcesEnableConstraints);

			// Configure Button
			final JButton SourcesConfigButton = new JButton("Config");
			SourcesConfigButton.addActionListener(new ConfigAction());
			final GridBagConstraints sourcesConfigConstraints = new GridBagConstraints();
			sourcesConfigConstraints.gridx = 1;
			sourcesConfigConstraints.gridy = 2;
			sourcesConfigConstraints.anchor = GridBagConstraints.WEST;
			SourcesPanel.add(SourcesConfigButton, sourcesConfigConstraints);

			// Enabled Label
			final GridBagConstraints sourcesEnLabelConstraints = new GridBagConstraints();
			sourcesEnLabelConstraints.gridx = 2;
			sourcesEnLabelConstraints.gridy = 0;
			sourcesEnLabelConstraints.anchor = GridBagConstraints.WEST;
			sourcesEnLabelConstraints.gridwidth = 2;
			final JLabel EnSourcesLabel = new JLabel();
			EnSourcesLabel.setText("Enabled Sources");
			SourcesPanel.add(EnSourcesLabel, sourcesEnLabelConstraints);

			// Enabled list
			enabledModel = new DefaultListModel();
			final String[] enSources = sources.getEnabledSourcesList();

			for (final String element : enSources) {
				enabledModel.addElement(element);
			}

			enabledList = new JList(enabledModel);
			enabledList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			enabledList.setLayoutOrientation(JList.VERTICAL);
			enabledList.setVisibleRowCount(-1);

			final JScrollPane listEnabledScroller = new JScrollPane(enabledList);
			listEnabledScroller.setPreferredSize(new Dimension(150, 80));

			final GridBagConstraints sourcesEnListConstraints = new GridBagConstraints();
			sourcesEnListConstraints.gridx = 2;
			sourcesEnListConstraints.gridy = 1;
			sourcesEnListConstraints.anchor = GridBagConstraints.WEST;
			sourcesEnListConstraints.gridwidth = 2;

			SourcesPanel.add(listEnabledScroller, sourcesEnListConstraints);

			// Disable Button
			final JButton SourcesDisableButton = new JButton("Disable");
			SourcesDisableButton.addActionListener(new DisableAction());
			final GridBagConstraints sourcesDisableonstraints = new GridBagConstraints();
			sourcesDisableonstraints.gridx = 2;
			sourcesDisableonstraints.gridy = 2;
			sourcesDisableonstraints.anchor = GridBagConstraints.WEST;
			SourcesPanel.add(SourcesDisableButton, sourcesDisableonstraints);

			AdvancedOptions.add(SourcesPanel, sourcesPanelConstraints);
		}

		return AdvancedOptions;
	}

	/**
	 * This method initializes BrowseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseButton() {
		if (BrowseButton == null) {
			BrowseButton = new JButton(OpenAction);
			BrowseButton.setText("Browse..");
		}
		return BrowseButton;
	}

	/**
	 * This method initializes FileField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getFileField() {
		if (FileField == null) {
			FileField = new JTextField(20);
			FileField.setColumns(20);
			FileField.setText("");
		}
		return FileField;
	}

	/**
	 * This method initializes GenerateButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getGenerateButton() {
		if (GenerateButton == null) {
			GenerateButton = new JButton(GenerateAction);
			GenerateButton.setText("Generate Mosaic");
			GenerateButton.setMnemonic(KeyEvent.VK_ENTER);
		}
		return GenerateButton;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getOptionsPanel(), BorderLayout.NORTH);

			jContentPane.add(getStatusPane(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes OptionsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOptionsPanel() {
		if (OptionsPanel == null) {

			// Options Panel
			OptionsPanel = new JPanel();
			OptionsPanel.setLayout(new GridBagLayout());
			OptionsPanel.setPreferredSize(new Dimension(600, 60));
			OptionsPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createBevelBorder(BevelBorder.RAISED),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));

			// Save Button
			final GridBagConstraints saveButtonConstraints = new GridBagConstraints();
			saveButtonConstraints.gridx = 5;
			saveButtonConstraints.gridy = 0;
			OptionsPanel.add(getSaveButton(), saveButtonConstraints);

			// Generate Button
			final GridBagConstraints generateButtonConstraints = new GridBagConstraints();
			generateButtonConstraints.gridx = 4;
			generateButtonConstraints.gridheight = 1;
			generateButtonConstraints.gridy = 1;
			OptionsPanel.add(getGenerateButton(), generateButtonConstraints);

			// Resolution Field
			final GridBagConstraints resolutionFieldConstraints = new GridBagConstraints();
			resolutionFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
			resolutionFieldConstraints.gridy = 1;
			resolutionFieldConstraints.weightx = 1.0;
			resolutionFieldConstraints.anchor = GridBagConstraints.WEST;
			resolutionFieldConstraints.gridx = 3;
			OptionsPanel.add(getResolutionField(), resolutionFieldConstraints);

			// Resolution Label
			final GridBagConstraints resolutionLabelConstraints = new GridBagConstraints();
			resolutionLabelConstraints.gridx = 2;
			resolutionLabelConstraints.gridy = 1;
			ResolutionLabel = new JLabel();
			ResolutionLabel.setText("Resolution:");
			OptionsPanel.add(ResolutionLabel, resolutionLabelConstraints);

			// Search Field
			final GridBagConstraints searchFieldConstraints = new GridBagConstraints();
			searchFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
			searchFieldConstraints.gridy = 1;
			searchFieldConstraints.weightx = 1.0;
			searchFieldConstraints.anchor = GridBagConstraints.WEST;
			searchFieldConstraints.gridx = 1;
			OptionsPanel.add(getSearchField(), searchFieldConstraints);

			// Search Label
			final GridBagConstraints searchLabelConstraints = new GridBagConstraints();
			searchLabelConstraints.gridx = 0;
			searchLabelConstraints.anchor = GridBagConstraints.EAST;
			searchLabelConstraints.gridy = 1;
			SearchLabel = new JLabel();
			SearchLabel.setText("Search String:");
			OptionsPanel.add(SearchLabel, searchLabelConstraints);

			// Browse Button
			final GridBagConstraints browseButtonConstraints = new GridBagConstraints();
			browseButtonConstraints.gridx = 4;
			browseButtonConstraints.anchor = GridBagConstraints.WEST;
			browseButtonConstraints.gridy = 0;
			OptionsPanel.add(getBrowseButton(), browseButtonConstraints);

			// File Field
			final GridBagConstraints fileFieldConstraints = new GridBagConstraints();
			fileFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
			fileFieldConstraints.gridy = 0;
			fileFieldConstraints.gridwidth = 3;
			fileFieldConstraints.anchor = GridBagConstraints.WEST;
			fileFieldConstraints.gridx = 1;
			OptionsPanel.add(getFileField(), fileFieldConstraints);

			// File Label
			final GridBagConstraints fileLabelConstraints = new GridBagConstraints();
			fileLabelConstraints.gridx = 0;
			fileLabelConstraints.anchor = GridBagConstraints.EAST;
			fileLabelConstraints.gridy = 0;
			FileLabel = new JLabel();
			FileLabel.setText("Source Image:");
			OptionsPanel.add(FileLabel, fileLabelConstraints);

		}

		return OptionsPanel;
	}

	/**
	 * This method initializes ResolutionField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getResolutionField() {
		if (ResolutionField == null) {
			ResolutionField = new JTextField(5);
			ResolutionField.setColumns(5);
			ResolutionField.setText("25");
		}
		return ResolutionField;
	}

	/**
	 * This method initializes SaveButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSaveButton() {
		if (SaveButton == null) {
			SaveButton = new JButton(SaveAction);
			SaveButton.setText("Save");
			SaveButton.setEnabled(false);
		}
		return SaveButton;
	}

	/**
	 * This method initializes SearchField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSearchField() {
		if (SearchField == null) {
			SearchField = new JTextField(10);
			SearchField.setColumns(10);
		}
		return SearchField;
	}

	private JPanel getStatusPane() {
		if (StatusPanel == null) {
			StatusPanel = new JPanel();
			StatusPanel.setLayout(new GridLayout(1, 2));

			StatusLabel = new JLabel();
			StatusPanel.add(StatusLabel);
			StatusPanel.add(progressBar);
		}

		return StatusPanel;
	}
}
