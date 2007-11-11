import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

/**
 * 
 */

/**
 * @author scott
 *
 */
public class WosaicUI extends JApplet {

	private JPanel jContentPane = null;
	private JPanel jPanel = null;

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
		this.setSize(300, 200);
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
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.setPreferredSize(new Dimension(10, 10));
		}
		return jPanel;
	}

}
