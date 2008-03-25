package wosaic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wosaic.ui.MosaicPane;
import wosaic.utilities.ImagePreview;
import wosaic.utilities.Mosaic;
import wosaic.utilities.Parameters;
import wosaic.utilities.Pixel;
import wosaic.utilities.SaveThread;
import wosaic.utilities.SourcePlugin;
import wosaic.utilities.Status;
import wosaic.utilities.WosaicFilter;

/**
 * The new interface for Wosaic. This revision will be an almost complete
 * rewrite to better abstract the creation process and keep track of where are
 * memory is being used.
 */
public class WosaicUI extends Panel implements ActionListener,
		ListSelectionListener {
	/**
	 * The default X-dimension for saving a mosaic.
	 */
	protected static final int DEFAULT_DIM_X = 800;

	/**
	 * The default Y-dimension for saving a mosaic.
	 */
	protected static final int DEFAULT_DIM_Y = 600;

	/**
	 * The default multiplier for specifying the dimensions to save a mosaic as.
	 */
	protected static final double DEFAULT_MULTIPLIER_DIM = 1.0;

	/**
	 * The default mosaic resolution that is set by the applicaiton at runtime.
	 */
	protected static final int DEFAULT_RESOLUTION = 35;

	/**
	 * The default text that is displayed in the status bar when we have nothing
	 * to do.
	 */
	protected static final String DEFAULT_STATUS_TEXT = "Ready";

	/**
	 * Generated by Eclipse
	 */
	private static final long serialVersionUID = -3633641341230537673L;

	/**
	 * The second tab in our UI, responsible to letting the users set extra
	 * options for the Mosaic creation. This is created at runtime and persists
	 * throughout the session.
	 */
	protected JPanel AdvancedOptionsTab = null;

	/**
	 * Button that spawns the File Chooser allowing the user to select a source
	 * picture. This gets created with the MainTab at runtime.
	 */
	protected JButton BrowseButton = null;

	/**
	 * UI element allowing the user to stop generating a mosaic mid-process.
	 */
	protected JButton CancelButton = null;

	/**
	 * UI element allowing a user to configure the selected source plugin.
	 */
	protected JButton ConfigureSourceButton;

	/**
	 * This is the main panel that holds all child UI elements.
	 */
	protected JPanel ContentPane = null;

	/**
	 * The thread that our controller object will run in
	 */
	protected Thread ControllerThread;

	/**
	 * Radio button allowing the user to opt to entering their own custom
	 * dimensions for the saved mosaic output. Although this is created and
	 * adjustable at runtime, it has no effect until saving the mosaic.
	 */
	protected JRadioButton CustomDimsButton = null;

	/**
	 * The text field allowing a user to specify their own custom X dimension
	 * for the saved mosaic output. Although this is created and adjustable at
	 * runtime, it has no effect until saving the mosaic.
	 */
	protected JFormattedTextField CustomDimsTextX = null;

	/**
	 * The text field allowing a user to specify their own custom Y dimension
	 * for the saved mosaic output. Although this is created and adjustable at
	 * runtime, it has no effect until saving the mosaic.
	 */
	protected JFormattedTextField CustomDimsTextY = null;

	/**
	 * A UI Element to enumerate each of the disabled source plugins.
	 */
	protected JList DisabledSourcesList = null;

	/**
	 * UI element allowing a user to disable the selected source plugin.
	 */
	protected JButton DisableSourceButton;

	/**
	 * A UI Element to enumerate all of the source plugins that have been
	 * enabled.
	 */
	protected JList EnabledSourcesList = null;

	/**
	 * UI element allowing a user to enable the selected source plugin.
	 */
	protected JButton EnableSourceButton;

	/**
	 * The underlying Mosaic. The WosaicUI needs this to pass to the Controller
	 * for editing, and to the MosaicPanel to update the grid. The Mosaic object
	 * is initialized when it's time to generate a new Mosaic, rather than at
	 * runtime. It gets replaced everytime we create a new mosaic.
	 */
	protected Mosaic GeneratedMosaic = null;

	/**
	 * The button that starts the actual process of creating the mosaic. Once
	 * the button is pressed, we validate inputs and get the ball rolling.
	 */
	protected JButton GenerateMosaicButton = null;

	/**
	 * The UI element that allows a user to enter an image to use as the mosaic
	 * source. This is generally populated by a file chooser, but the user may
	 * also enter a path manually.
	 */
	protected JTextField InputImageText = null;

	/**
	 * The primary "tab" in our interface. Holds the general configuration
	 * options, and will display the Mosaic when it's created. This is created
	 * at runtime and persists throughout the session.
	 */
	protected JPanel MainTab = null;

	/**
	 * The "Controller" that is responsible for delegating tasks and spawning
	 * threads for the creation of a mosaic. This gets initialized at
	 * "generation time", and is destroyed promptly after the mosaic is
	 * complete. This should help us keep hold of our memory.
	 */
	protected Controller MosaicController = null;

	/**
	 * The lower pane, where we actually display the generated mosaic. We use
	 * the same MosaicPane for the duration of the session, but the tile grid
	 * gets reinitialized each mosaic.
	 */
	protected MosaicPane MosaicDisplay = null;

	/**
	 * A text field where the user can enter an integer to specify the
	 * resolution for the mosaic tiles
	 */
	protected JFormattedTextField MosaicResolutionText = null;

	/**
	 * Radio button allowing the user to opt to entering a multiplier of the
	 * original dimensions to save the mosaic to. Although this is created and
	 * adjustable at runtime, it has no effect until saving the mosaic.
	 */
	protected JRadioButton MultiplierDimsButton = null;

	/**
	 * The text field were a user can specify a multiplier of the original
	 * dimensions to save the mosaic to. Although this is created and adjustable
	 * at runtime, it has no effect until saving the mosaic.
	 */
	protected JFormattedTextField MultiplierDimsText = null;

	/**
	 * Radio button for the user to specify that the image should be saved with
	 * the original dimensions of the source image. Although this is created and
	 * adjustable at runtime, it has no effect until saving the mosaic.
	 */
	protected JRadioButton OriginalDimsButton = null;

	/**
	 * An object to represent the source plugins available, as well as the ones
	 * selected. We create the plugin objects as they are enabled.
	 */
	protected Sources PluginSources = null;

	/**
	 * Button that begins the process of saving the completed mosaic. Only
	 * enabled when there has been a mosaic actually created. This gets created
	 * with the MainTab at runtime.
	 */
	protected JButton SaveButton = null;

	/**
	 * The text field where a user can enter the search string to query for
	 * tiles on.
	 */
	protected JTextField SearchQueryText = null;

	/**
	 * The image that we will use as the source for our mosaic
	 */
	protected BufferedImage SourceImage = null;

	/**
	 * A UI element to show progress and report status messages to the user as
	 * neccessary
	 */
	protected Status StatusUI = null;

	/**
	 * The UI element that holds our different UI "tabs"
	 */
	protected JTabbedPane TabbedPane;

	/**
	 * Default constructor, called at program runtime. Layout the UI, and
	 * initialize the minimum amount of member variables needed.
	 */
	protected WosaicUI() {
		super();
		InitializeUI();

		// Add event listeners
		BrowseButton.addActionListener(this);
		SaveButton.addActionListener(this);
		GenerateMosaicButton.addActionListener(this);
		CancelButton.addActionListener(this);
		OriginalDimsButton.addActionListener(this);
		MultiplierDimsButton.addActionListener(this);
		CustomDimsButton.addActionListener(this);
		EnableSourceButton.addActionListener(this);
		ConfigureSourceButton.addActionListener(this);
		DisableSourceButton.addActionListener(this);
		DisabledSourcesList.addListSelectionListener(this);
		EnabledSourcesList.addListSelectionListener(this);

		// Initialize other member variables
		PluginSources = new Sources(StatusUI);

		final String[] disabledSources = PluginSources.getDisabledSourcesList();
		for (final String element : disabledSources)
			((DefaultListModel) DisabledSourcesList.getModel())
					.addElement(element);
		final String[] enabledSources = PluginSources.getEnabledSourcesList();
		for (final String element : enabledSources)
			((DefaultListModel) EnabledSourcesList.getModel())
					.addElement(element);

		// Set the width of the scrollpanes to be the max preferred width of
		// either
		// This will depend on the longest source string in either of the lists.
		final Dimension disabledSize = DisabledSourcesList
				.getPreferredScrollableViewportSize();
		final Dimension enabledSize = EnabledSourcesList
				.getPreferredScrollableViewportSize();
		final Dimension maxSize = disabledSize.width > enabledSize.width ? disabledSize
				: enabledSize;
		DisabledSourcesList.getParent().setPreferredSize(maxSize);
		EnabledSourcesList.getParent().setPreferredSize(maxSize);

		// Set some defaults
		MosaicResolutionText.setText(Integer
				.toString(WosaicUI.DEFAULT_RESOLUTION));
		MultiplierDimsText.setText(Double
				.toString(WosaicUI.DEFAULT_MULTIPLIER_DIM));
		CustomDimsTextX.setText(Integer.toString(WosaicUI.DEFAULT_DIM_X));
		CustomDimsTextY.setText(Integer.toString(WosaicUI.DEFAULT_DIM_Y));
		StatusUI.setStatus(WosaicUI.DEFAULT_STATUS_TEXT);

		System.gc();
	}

	/**
	 * Handle events from various sources by passing evaluation off to the
	 * appropriate method.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent e) {
		final Object eventSource = e.getSource();

		if (eventSource == BrowseButton)
			LaunchInputBrowseDialog();

		else if (eventSource == GenerateMosaicButton)
			StartMosaicGeneration();

		else if (eventSource == CancelButton)
			CancelGeneration();

		else if (eventSource == SaveButton)
			SaveMosaic();

		else if (eventSource == OriginalDimsButton
				|| eventSource == MultiplierDimsButton
				|| eventSource == CustomDimsButton)
			EnableSelectedDimField((JRadioButton) eventSource);

		else if (eventSource == EnableSourceButton)
			EnableSelectedSource();

		else if (eventSource == DisableSourceButton)
			DisableSelectedSource();

		else if (eventSource == ConfigureSourceButton)
			ConfigureSelectedSource();

		else if (eventSource == MosaicController)
			GenerationCleanup();

	}

	/**
	 * Send the appropriate interrupts to cancel the mosaic currently being
	 * generated, and then update the UI.
	 */
	protected void CancelGeneration() {
		// TODO: Write code for the comments below
		// Prompt the user to make sure the process should be
		// cancelled.
		// Send interrupts to the controller
		if (JOptionPane.showConfirmDialog(this,
				"Are you sure you want to stop mosaic generation?", "Cancel?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
			return;

		ControllerThread.interrupt();
		CancelButton.setEnabled(false);
		StatusUI.setStatus("Mosaic Cancelled!");
		StatusUI.setIndeterminate(false);
		StatusUI.setProgress(0);
	}

	/**
	 * Validates the dimensions. Returns their computed value.
	 * 
	 * @param width
	 *            the ORIGINAL width of the source image
	 * @param height
	 *            the ORIGINAL height of the source image
	 * @return
	 */
	public Dimension checkDimensions(final int width, final int height) {
		int xDim, yDim;
		double multiplier;

		// Check the dimensions of advanced options
		if (MultiplierDimsButton.isSelected()) {
			try {
				multiplier = Double.parseDouble(MultiplierDimsText.getText());
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid number for the multiplier.");
				StatusUI.setStatus("");
				return null;
			}
			xDim = (int) (width * multiplier);
			yDim = (int) (height * multiplier);

		} else if (OriginalDimsButton.isSelected()) {
			xDim = width;
			yDim = height;

		} else { // DimensionsCustom.isSelected()
			int parsedX, parsedY;
			try {
				// First stored parsed values into temp variables,
				// because
				// xDim and yDim are marked final-- they need to be set
				// outside
				// the catch statement to avoid compiler errors.
				parsedX = Integer.parseInt(CustomDimsTextX.getText());
				parsedY = Integer.parseInt(CustomDimsTextY.getText());
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid number for the dimensions.");
				StatusUI.setStatus("");
				StatusUI.setProgress(0);
				return null;
			}
			xDim = parsedX;
			yDim = parsedY;
		}

		return new Dimension(xDim, yDim);

	}

	/**
	 * Get rid of any old references that we have from previous mosaic
	 * generations.
	 */
	protected void CleanSlate() {
		GeneratedMosaic = null;
		SourceImage = null;
		MosaicDisplay.clearGrid();
	}

	/**
	 * Configure the currently selected source plugin by simply calling the
	 * plugin code to launch it's defined configuration panel.
	 */
	private void ConfigureSelectedSource() {

		// Get selected source
		final String selection = (String) EnabledSourcesList.getSelectedValue();
		if (selection == null)
			return;

		final SourcePlugin src = PluginSources.findType(selection);
		if (src == null)
			return;

		final JDialog frame = src.getOptionsDialog();
		if (frame == null)
			return;

		frame.setLocationRelativeTo(this);
		frame.setVisible(true);

	}

	/**
	 * Disable the currently selected source plugin by removing it from our
	 * Sources list, and then updating our UI accordingly
	 */
	protected void DisableSelectedSource() {
		final String src = (String) EnabledSourcesList.getSelectedValue();
		if (src == null)
			return;
		if (PluginSources.removeSource(src)) {
			final DefaultListModel enabledModel = (DefaultListModel) EnabledSourcesList
					.getModel();
			enabledModel.removeElement(src);

			final DefaultListModel disabledModel = (DefaultListModel) DisabledSourcesList
					.getModel();
			disabledModel.addElement(src);
		}
	}

	/**
	 * Update the UI elements for choosing mosaic output dimensions based on the
	 * radio button that the user has selected. Make the fields for the selected
	 * button active, and the others inactive.
	 * 
	 * @param selectedButton
	 *            The user-selected radio button
	 */
	private void EnableSelectedDimField(final JRadioButton selectedButton) {
		OriginalDimsButton.setSelected(selectedButton == OriginalDimsButton);

		MultiplierDimsButton
				.setSelected(selectedButton == MultiplierDimsButton);
		MultiplierDimsText.setEnabled(selectedButton == MultiplierDimsButton);

		CustomDimsButton.setSelected(selectedButton == CustomDimsButton);
		CustomDimsTextX.setEnabled(selectedButton == CustomDimsButton);
		CustomDimsTextY.setEnabled(selectedButton == CustomDimsButton);
	}

	/**
	 * Enable the currently selected source plugin by creating the appropriate
	 * object in our Sources list, as well as updating the UI accordingly
	 */
	protected void EnableSelectedSource() {
		final String src = (String) DisabledSourcesList.getSelectedValue();
		if (src == null)
			return;
		if (PluginSources.addSource(src)) {
			final DefaultListModel enabledModel = (DefaultListModel) EnabledSourcesList
					.getModel();
			enabledModel.addElement(src);

			final DefaultListModel disabledModel = (DefaultListModel) DisabledSourcesList
					.getModel();
			disabledModel.removeElement(src);
		}
	}

	/**
	 * Do any cleanup neccessary after we are finished generating a mosaic. This
	 * includes getting rid of our Controller object, and updating our UI.
	 */
	private void GenerationCleanup() {
		ControllerThread = null;
		MosaicController = null;
		StatusUI.setIndeterminate(false);
		StatusUI.setProgress(0);		
		TabbedPane.setEnabledAt(
				TabbedPane.indexOfComponent(AdvancedOptionsTab), true);
		InputImageText.setEnabled(true);
		BrowseButton.setEnabled(true);
		SearchQueryText.setEnabled(true);
		MosaicResolutionText.setEnabled(true);
		GenerateMosaicButton.setEnabled(true);
		CancelButton.setEnabled(false);
		SaveButton.setEnabled(GeneratedMosaic.isValid());

		System.gc();
	}

	/**
	 * Calculates the parameters (numRows and numCols) for this mosaic. This is
	 * based on the resolution field and the original size of the image.
	 * 
	 * @param bi
	 *            the buffered image of the master image
	 * @return an initialized parameters object
	 */
	protected Parameters GenParams(final BufferedImage bi) {

		final int numRows;
		final int numCols;
		int resolution;
		final int xDim = bi.getWidth();
		final int yDim = bi.getHeight();

		resolution = Integer.parseInt(MosaicResolutionText.getText());

		if (xDim <= yDim) {
			numRows = resolution;
			numCols = (int) ((double) xDim / yDim * numRows);
		} else {
			numCols = resolution;
			numRows = (int) ((double) yDim / xDim * numCols);
		}

		final Parameters p = new Parameters(numRows, numCols, xDim, yDim);
		return p;

	}

	/**
	 * Wrapper method to initialize and layout all UI elements in the Wosaic
	 * interface
	 */
	protected void InitializeUI() {
		TabbedPane = new JTabbedPane();
		// Status Bar
		final JPanel statusBar = new JPanel(new GridLayout(1, 2));
		final JLabel statusLabel = new JLabel();
		final JProgressBar progressBar = new JProgressBar();
		statusBar.add(statusLabel);
		statusBar.add(progressBar);
		StatusUI = new Status(statusLabel, progressBar);

		// Main tab
		MainTab = new JPanel(new BorderLayout());
		final JPanel basicOptionsPane = new JPanel(new GridBagLayout());
		final JLabel searchImageLabel = new JLabel("Source Image:");
		InputImageText = new JTextField(20);
		InputImageText
				.setToolTipText("The image that will be used as the basis of the mosaic");
		BrowseButton = new JButton("Browse..");
		BrowseButton
				.setToolTipText("Browse for an image to use as the basis of the mosaic");
		SaveButton = new JButton("Save");
		SaveButton.setToolTipText("Save the generated mosaic to your computer");
		SaveButton.setEnabled(false);
		final JLabel searchStringLabel = new JLabel("Search String:");
		SearchQueryText = new JTextField(10);
		SearchQueryText
				.setToolTipText("The search query that will be used for finding sub-pictures");
		final JLabel resolutionLabel = new JLabel("Resolution:");
		MosaicResolutionText = new JFormattedTextField(new DecimalFormat("#"));
		MosaicResolutionText.setColumns(5);
		MosaicResolutionText
				.setToolTipText("The number of smaller images that will be used in a row of the mosaic");
		GenerateMosaicButton = new JButton("Generate");
		GenerateMosaicButton.setToolTipText("Create a mosaic");
		CancelButton = new JButton("Cancel");
		CancelButton.setToolTipText("Cancel the mosaic generation");
		CancelButton.setEnabled(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		basicOptionsPane.add(searchImageLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		basicOptionsPane.add(InputImageText, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		basicOptionsPane.add(BrowseButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		basicOptionsPane.add(SaveButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		basicOptionsPane.add(searchStringLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		basicOptionsPane.add(SearchQueryText, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		basicOptionsPane.add(resolutionLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		basicOptionsPane.add(MosaicResolutionText, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		basicOptionsPane.add(GenerateMosaicButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		basicOptionsPane.add(CancelButton, gbc);

		MosaicDisplay = new MosaicPane();

		MainTab.add(basicOptionsPane, BorderLayout.NORTH);
		MainTab.add(MosaicDisplay, BorderLayout.CENTER);

		// Advanced Tab
		AdvancedOptionsTab = new JPanel(new GridBagLayout());
		final JPanel dimensionsPanel = new JPanel(new GridBagLayout());
		final JLabel dimensionsLabel = new JLabel("Mosaic Dimensions");
		OriginalDimsButton = new JRadioButton("Original", true);
		OriginalDimsButton
				.setToolTipText("Use the original image's dimensions for saving");
		MultiplierDimsButton = new JRadioButton("Multiple:", false);
		MultiplierDimsButton
				.setToolTipText("Use a multiple of the original image's dimensions for saving");
		MultiplierDimsText = new JFormattedTextField(new DecimalFormat());
		MultiplierDimsText
				.setToolTipText("The multiplier to use in determining the dimensions for saving");
		MultiplierDimsText.setColumns(5);
		MultiplierDimsText.setEnabled(false);
		CustomDimsButton = new JRadioButton("Custom", false);
		CustomDimsButton
				.setToolTipText("Enter your own custom dimensions for saving");
		final JLabel customXLabel = new JLabel("Width:");
		CustomDimsTextX = new JFormattedTextField(new DecimalFormat("#"));
		CustomDimsTextX.setToolTipText("Saved image width");
		CustomDimsTextX.setColumns(5);
		CustomDimsTextX.setEnabled(false);
		final JLabel customYLabel = new JLabel("Height:");
		CustomDimsTextY = new JFormattedTextField(new DecimalFormat("#"));
		CustomDimsTextY.setToolTipText("Saved image height");
		CustomDimsTextY.setColumns(5);
		CustomDimsTextY.setEnabled(false);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 2;
		dimensionsPanel.add(dimensionsLabel, gbc);
		final Insets leftInset = new Insets(0, 3, 0, 2);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = leftInset;
		gbc.anchor = GridBagConstraints.WEST;
		dimensionsPanel.add(OriginalDimsButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = leftInset;
		gbc.anchor = GridBagConstraints.WEST;
		dimensionsPanel.add(MultiplierDimsButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		dimensionsPanel.add(MultiplierDimsText, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = leftInset;
		gbc.anchor = GridBagConstraints.WEST;
		dimensionsPanel.add(CustomDimsButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = leftInset;
		dimensionsPanel.add(customXLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.WEST;
		dimensionsPanel.add(CustomDimsTextX, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = leftInset;
		dimensionsPanel.add(customYLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.WEST;
		dimensionsPanel.add(CustomDimsTextY, gbc);

		final JPanel sourcesPanel = new JPanel(new GridBagLayout());
		final JLabel disabledSourcesLabel = new JLabel("Disabled Sources");
		final JLabel enabledSourcesLabel = new JLabel("Enabled Sources");
		DisabledSourcesList = new JList(new DefaultListModel());
		DisabledSourcesList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DisabledSourcesList.setLayoutOrientation(JList.VERTICAL);
		DisabledSourcesList.setVisibleRowCount(5);
		final JScrollPane disabledSourcesScrollPane = new JScrollPane(
				DisabledSourcesList);
		EnabledSourcesList = new JList(new DefaultListModel());
		EnabledSourcesList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		EnabledSourcesList.setLayoutOrientation(JList.VERTICAL);
		EnabledSourcesList.setVisibleRowCount(5);
		final JScrollPane enabledSourcesScrollPane = new JScrollPane(
				EnabledSourcesList);
		EnableSourceButton = new JButton("\u21E8"); // Unicode right arrow
		EnableSourceButton.setToolTipText("Enable the selected plugin");
		EnableSourceButton.setEnabled(false);
		ConfigureSourceButton = new JButton("Config");
		ConfigureSourceButton
				.setToolTipText("Set options for the selected plugin");
		ConfigureSourceButton.setEnabled(false);
		DisableSourceButton = new JButton("\u21E6"); // Unicode left arrow
		DisableSourceButton.setToolTipText("Disable the selected plugin");
		DisableSourceButton.setEnabled(false);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		sourcesPanel.add(disabledSourcesLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		sourcesPanel.add(enabledSourcesLabel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		sourcesPanel.add(disabledSourcesScrollPane, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.ipady = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		sourcesPanel.add(EnableSourceButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.ipady = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		sourcesPanel.add(DisableSourceButton, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		sourcesPanel.add(enabledSourcesScrollPane, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		sourcesPanel.add(ConfigureSourceButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		AdvancedOptionsTab.add(dimensionsPanel, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		AdvancedOptionsTab.add(sourcesPanel, gbc);

		TabbedPane.addTab("Mosaic", null, MainTab,
				"Set common options and view the generated mosaic");
		TabbedPane.addTab("Advanced", null, AdvancedOptionsTab,
				"Specify advanced options and choose plugins for mosaics");

		setLayout(new BorderLayout());
		add(TabbedPane, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(600, 400));
		validate();
	}

	/**
	 * Spawn a FileChooser that will allow the user to specify an input file,
	 * and populate the InputImageText field when the user accepts
	 */
	protected void LaunchInputBrowseDialog() {
		final JFileChooser chooser = new JFileChooser(InputImageText.getText());
		chooser.setAccessory(new ImagePreview(chooser));
		chooser.setFileFilter(new WosaicFilter());
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			InputImageText.setText(chooser.getSelectedFile().toString());
	}

	/**
	 * Start the process of saving a mosaic. This should only be called once a
	 * Mosaic has actually be created. Prompt the user to enter a path to save
	 * to, (validate), and stitch the mosaic together for saving.
	 */
	protected void SaveMosaic() {
		final JFileChooser chooser = new JFileChooser(InputImageText.getText());
		chooser.setFileFilter(new WosaicFilter());
		if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		final File theFile = chooser.getSelectedFile();
		if (theFile.exists())
			if (JOptionPane.showConfirmDialog(this, theFile.getName()
					+ " already exists.  Overwrite?", "File Exists",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)
				return;

		// Check the dimensions
		final Dimension d = checkDimensions(
				GeneratedMosaic.getParams().originalWidth, GeneratedMosaic
						.getParams().originalHeight);

		if (d == null)
			return;

		GeneratedMosaic.setOutputSize(d.width, d.height);

		// Kick off the saving
		StatusUI.setIndeterminate(true);
		StatusUI.setStatus("Saving...");

		final SaveThread st = new SaveThread(GeneratedMosaic, StatusUI, theFile);
		final Thread saveThread = new Thread(st);
		saveThread.start();

		System.gc();
	}

	/**
	 * Start the actual process of generating the mosaic. This includes first
	 * verifying the inputs, setting up the UI, and finally creating and running
	 * the controller object.
	 */
	protected void StartMosaicGeneration() {

		// Get rid of any references we have from previous generations
		CleanSlate();
		
		// Set the status object
		StatusUI.setStatus("Validating Inputs...");

		// Set the search string for plugins that need it
		final Vector<SourcePlugin> sources = new Vector<SourcePlugin>(
				PluginSources.getEnabledSources());
		for (final SourcePlugin source : sources)
			source.setSearchString(SearchQueryText.getText());
		
		// Validate parameters
		final String validateResponse = ValidateGenParams();
		if (validateResponse != null) {
			JOptionPane.showMessageDialog(this, validateResponse,
					"Invalid Parameters", JOptionPane.WARNING_MESSAGE);
			StatusUI.setStatus("Please Check Inputs...");
			return;
		}

		// Setup the parameters
		final Parameters params = GenParams(SourceImage);

		// Create the grid elements in our mosaic panel
		MosaicDisplay.setGrid(params.resRows, params.resCols);

		final Pixel sourcePixel = new Pixel(SourceImage);
		GeneratedMosaic = new Mosaic(params, sourcePixel);
		GeneratedMosaic.addMosaicEventListener(MosaicDisplay);

		// Setup the Controller
		MosaicController = new Controller(params, sourcePixel, GeneratedMosaic,
				sources, StatusUI);
		MosaicController.addActionListener(this);

		// Disable some of our UI buttons
		TabbedPane.setEnabledAt(
				TabbedPane.indexOfComponent(AdvancedOptionsTab), false);
		InputImageText.setEnabled(false);
		BrowseButton.setEnabled(false);
		SearchQueryText.setEnabled(false);
		MosaicResolutionText.setEnabled(false);
		GenerateMosaicButton.setEnabled(false);
		CancelButton.setEnabled(true);

		System.gc();

		ControllerThread = new Thread(MosaicController, "Controller Thread");
		ControllerThread.start();
		StatusUI.setStatus("Generating Mosaic...");
	}

	/**
	 * Make sure all of our parameters are setup for creating a mosaic. At the
	 * end of the call, we should have initialized the SourceImage as well.
	 * 
	 * @return An error string to prompt the user to fix the inputs, or null if
	 *         everything is valid.
	 */
	protected String ValidateGenParams() {
		final File sourceFile = new File(InputImageText.getText());
		if (!sourceFile.canRead())
			return "Please enter a valid source file";

		try {
			SourceImage = ImageIO.read(sourceFile);
		} catch (final Exception e) {
			SourceImage = null;
		}
		if (SourceImage == null)
			return "Please enter a valid source image";

		if (PluginSources.usingSearchString())
			if (SearchQueryText.getText().equals(""))
				return "Please enter a valid search string";

		int res;
		try {
			res = Integer.parseInt(MosaicResolutionText.getText());
		} catch (final Exception e) {
			return "Please enter a valid resolution";
		}
		if (res <= 0)
			return "Please enter a positive resolution";
		
		String error;
		
		for(int i = 0; i < EnabledSourcesList.getModel().getSize(); i++) {
			final String selection = (String) EnabledSourcesList.getModel().getElementAt(i);
			
			if (selection == null)
				return "Fatal Error: Enabled Sources List is out of sync!";

			final SourcePlugin src = PluginSources.findType(selection);
			error = src.validateParams();
			
			if (error != null)
				return error;
		}

		return null;
	}

	/**
	 * Handle selection changes in the plugin selection lists. Basically, enable
	 * or disable relevant buttons, and make sure only one has a plugin selected
	 * at a time.
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(final ListSelectionEvent e) {
		final JList src = (JList) e.getSource();
		if (src == EnabledSourcesList && !src.isSelectionEmpty()) {
			DisabledSourcesList.clearSelection();
			DisableSourceButton.setEnabled(true);
			EnableSourceButton.setEnabled(false);
			ConfigureSourceButton.setEnabled(true);
		} else if (src == DisabledSourcesList && !src.isSelectionEmpty()) {
			EnabledSourcesList.clearSelection();
			DisableSourceButton.setEnabled(false);
			EnableSourceButton.setEnabled(true);
			ConfigureSourceButton.setEnabled(false);
		}
	}
}
