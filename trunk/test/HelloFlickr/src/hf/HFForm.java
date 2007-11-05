/**
 * 
 */
package hf;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.FlowLayout;

/**
 * @author scott
 *
 */
public class HFForm implements ActionListener {

	private JFrame jFrame = null;
	private JSplitPane jSplitPane = null;
	private JTextField jTextField = null;
	private JScrollPane DisplayPane = null;
	private PictureService PicService = null;
	private JPanel MainPanel = null;
	/**
	 * @throws ParserConfigurationException 
	 * 
	 */
	public HFForm(){
		try {
			PicService = new PictureService();
		} catch (ParserConfigurationException ex)	{
			System.out.println("Unable to create PictureService.");
		}
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setBottomComponent(getDisplayPane());
			jSplitPane.setTopComponent(getJTextField());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setHorizontalAlignment(JTextField.CENTER);
			jTextField.setText("hello world");
			jTextField.addActionListener(this);
		}
		return jTextField;
	}

	/**
	 * This method initializes DisplayPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getDisplayPane() {
		if (DisplayPane == null) {
			DisplayPane = new JScrollPane();
			DisplayPane.setViewportView(getMainPanel());
		}
		return DisplayPane;
	}

	/**
	 * This method initializes MainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (MainPanel == null) {
			MainPanel = new JPanel();
			MainPanel.setLayout(new FlowLayout());
		}
		return MainPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				HFForm application = new HFForm();
				application.getJFrame().setVisible(true);
			}
		});
	}

	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(600, 400);
			jFrame.setContentPane(getJSplitPane());
			jFrame.setTitle("HelloFlickr");
		}
		return jFrame;
	}

	public void actionPerformed(ActionEvent evt) {
		DisplayPictures(jTextField.getText());
		
	}

	public void DisplayPictures(String picString) {
		
		// First, remove all old pictures from the display
		MainPanel.removeAll();
		
		// Then, add new pictures, one by one
		for (int i=0; i < picString.length(); i++) {
			addPicture(picString.charAt(i));
		}
	}
		
	public void addPicture(char picChar) {
		
		JLabel label = new JLabel(String.valueOf(picChar),new ImageIcon(PicService.GetLetterImage(picChar)), JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		MainPanel.add(label, null);
		MainPanel.validate();
	}
}
