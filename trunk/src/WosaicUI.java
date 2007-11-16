import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import java.awt.GridBagConstraints;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import java.io.File;
import javax.swing.SwingConstants;

/**
 * The User interface for Wosaic, and application to create a photo-mosaic
 * using pictures drawn from Flickr.
 */

/**
 * @author scott
 * 
 */
public class WosaicUI extends JApplet {

	static final int THREADS = 10;
	static final int TARGET = 200;
	
	
    // This action creates and shows a modal open-file dialog.
    /**
	 * Creates and shows a modal open-file dialog.
	 * 
	 * @author scott
	 * 
	 */
    public class OpenFileAction extends AbstractAction {
        /**
		 * 
		 */
		private static final long serialVersionUID = -3576454135128663771L;
		Component parent;
        JFileChooser chooser;
    
        public File file = null;
        OpenFileAction(Component parent, JFileChooser chooser) {
            super("Open...");
            this.chooser = chooser;
            this.parent = parent;
        }
    
        public void actionPerformed(ActionEvent evt) {
            // Show dialog; this method does not return until dialog is closed
            chooser.showOpenDialog(parent);
            // Get the selected file and put it into our text field.
            file = chooser.getSelectedFile();
            ((WosaicUI)parent).FileField.setText(file.getAbsolutePath());
        }
    };
    
    public class GenerateMosaicAction extends AbstractAction {
    	
    	Component parent = null;
    	Controller cont = null;
    	
    	GenerateMosaicAction(Component parent) {
    		super();
    		this.parent = parent;
    	}
    	
    	public void actionPerformed(ActionEvent evt) {
    		// Initialize a controller object and run it.
    		WosaicUI wos = (WosaicUI)parent;
    		int target = TARGET;
    		int numThrds = THREADS;
    		
    		
    		try {
    			// FIXME: Infer xDim and yDim from the image size.
    			System.out.println("Opening our source image to grab metadata...");
    			BufferedImage bi = ImageIO.read(OpenAction.file);
    			int xDim = bi.getWidth();
    			int yDim = bi.getHeight();
    			
    			// FIXME: Infer numRows and numCols from resolution and dims
    			int numRows, numCols;
    			if (xDim <= yDim) {
    				numRows = Integer.parseInt(wos.ResolutionField.getText());
    				numCols = (int)(((double)xDim)/yDim * numRows);
    			} else {
    				numCols = Integer.parseInt(wos.ResolutionField.getText());
    				numRows = (int)(((double)yDim)/xDim * numCols);
    			}
    			
    			String search = wos.SearchField.getText();
    			String mImage = wos.FileField.getText();
    			
    			System.out.println("Initialize our controller.");
    			cont = new Controller(target, numThrds, numRows, numCols, xDim, yDim, search, mImage);
    			System.out.println("Call our controller thread");
    			Thread t = new Thread(cont);
    			t.run();
    			System.out.println("Wait for our JAI thread");
    			cont.mosaicThread.join();
    			
    			BufferedImage mos = cont.mProc.createImage(cont.mProc.wosaic, cont.mProc.params, cont.mProc.master.source);
    			wos.ImageBox.setIcon(new ImageIcon(mos));
    		/*
			 * int target, int numThrds, int numRows, int numCols, int xDim, int
			 * yDim, String search, String mImage
			 */
    		} catch (Exception ex) {
    			System.out.println(ex.getMessage());
    		}
    	}
    }
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -7379941758951948236L;
	private JPanel jContentPane = null;
	private JPanel OptionsPanel = null;
	private JLabel FileLabel = null;
	private JTextField FileField = null;
	private JButton BrowseButton = null;
	private JLabel SearchLabel = null;
	private JTextField SearchField = null;
	private JLabel ResolutionLabel = null;
	private JTextField ResolutionField = null;
	private JButton GenerateButton = null;
	
	/**
	 * A reference to a controller-- what actually calls the Flickr service and
	 * JAI processor to do all the work.
	 */
	public Controller controller;
	/**
	 * This is the xxx default constructor
	 */
	public WosaicUI() {
		super();
		FileChooser = new JFileChooser();
		OpenAction = new OpenFileAction(this, FileChooser);
		GenerateAction = new GenerateMosaicAction(this);
	}

	OpenFileAction OpenAction = null;
	GenerateMosaicAction GenerateAction = null;
	
	JFileChooser FileChooser = null;
	private JLabel ImageBox = null;
	/**
	 * This method initializes this
	 * 
	 * 
	 */
	public void init() {
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
		
		/*
		 * controller = new Controller(); Thread contThread = new
		 * Thread(controller, "Controller Thread"); contThread.setPriority(10);
		 */
		// contThread.start();
		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			ImageBox = new JLabel();
			ImageBox.setText("");
			ImageBox.setHorizontalTextPosition(SwingConstants.CENTER);
			ImageBox.setHorizontalAlignment(SwingConstants.CENTER);
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getOptionsPanel(), BorderLayout.NORTH);
			jContentPane.add(ImageBox, BorderLayout.CENTER);
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
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 4;
			gridBagConstraints10.gridheight = 2;
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.NONE;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 3;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 2;
			gridBagConstraints7.gridy = 1;
			ResolutionLabel = new JLabel();
			ResolutionLabel.setText("Resolution:");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.NONE;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 1;
			SearchLabel = new JLabel();
			SearchLabel.setText("Search String:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 3;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			FileLabel = new JLabel();
			FileLabel.setText("Source Image:");
			OptionsPanel = new JPanel();
			OptionsPanel.setLayout(new GridBagLayout());
			OptionsPanel.setPreferredSize(new Dimension(600, 60));
			OptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			OptionsPanel.add(FileLabel, gridBagConstraints);
			OptionsPanel.add(getFileField(), gridBagConstraints2);
			OptionsPanel.add(getBrowseButton(), gridBagConstraints3);
			OptionsPanel.add(SearchLabel, gridBagConstraints4);
			OptionsPanel.add(getSearchField(), gridBagConstraints5);
			OptionsPanel.add(ResolutionLabel, gridBagConstraints7);
			OptionsPanel.add(getResolutionField(), gridBagConstraints8);
			OptionsPanel.add(getGenerateButton(), gridBagConstraints10);
		}
		return OptionsPanel;
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

}
