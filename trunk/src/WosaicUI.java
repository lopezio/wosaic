import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.event.KeyEvent;

/**
 * 
 */

/**
 * @author scott
 *
 */
public class WosaicUI extends JApplet {

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
	 * This is the xxx default constructor
	 */
	public WosaicUI() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
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
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;
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
			OptionsPanel.setPreferredSize(new Dimension(0, 60));
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
			FileField = new JTextField();
			FileField.setColumns(30);
			FileField.setText("test");
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
			BrowseButton = new JButton();
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
			SearchField = new JTextField();
			SearchField.setColumns(15);
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
			ResolutionField = new JTextField();
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
