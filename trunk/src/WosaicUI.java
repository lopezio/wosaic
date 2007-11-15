import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.GridBagLayout;

import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import java.io.File;

/**
 * 
 */

/**
 * @author scott
 *
 */
public class WosaicUI extends JApplet {

    // This action creates and shows a modal open-file dialog.
    public class OpenFileAction extends AbstractAction {
        /**
		 * 
		 */
		private static final long serialVersionUID = -3576454135128663771L;
		Component parent;
        JFileChooser chooser;
    
        OpenFileAction(Component parent, JFileChooser chooser) {
            super("Open...");
            this.chooser = chooser;
            this.parent = parent;
        }
    
        public void actionPerformed(ActionEvent evt) {
            // Show dialog; this method does not return until dialog is closed
            chooser.showOpenDialog(parent);
            // Get the selected file and put it into our text field.
            ((WosaicUI)parent).FileField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    };
    
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
	
	public Controller controller;
	/**
	 * This is the xxx default constructor
	 */
	public WosaicUI() {
		super();
		FileChooser = new JFileChooser();
		OpenAction = new OpenFileAction(this, FileChooser);
	}

	Action OpenAction = null;
	JFileChooser FileChooser = null;
	/**
	 * This method initializes this
	 * 
	 * 
	 */
	public void init() {
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
		
		controller = new Controller();
		Thread contThread = new Thread(controller, "Controller Thread");
		contThread.setPriority(10);
		//contThread.start();
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
			GenerateButton = new JButton();
			GenerateButton.setText("Generate Mosaic");
			GenerateButton.setMnemonic(KeyEvent.VK_ENTER);
		}
		return GenerateButton;
	}

}
