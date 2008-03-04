package wosaic;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * The Application version of the Wosaic project
 * 
 * @author scott
 */
public class WosaicApplication extends javax.swing.JFrame {

	/**
	 * Version ID generated by Eclipse
	 */
	private static final long serialVersionUID = 5823811339873852749L;

	/**
	 * Main entry point for the Wosaic application. Currently, the args
	 * parameter is ignored.
	 * 
	 * @param args
	 *            Unused.
	 */
	public static void main(final String[] args) {
		new WosaicApplication();
	}

	/**
	 * Constructor for our Wosaic application. Delegates most initialization and
	 * subsequent processing to WosaicUI.
	 */
	public WosaicApplication() {
		super("Wosaic");
		initialize();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			System.err.println("Warning: Unable to set System LookAndFeel");
		}

		add(new WosaicUI2());

		// Allow WosaicUI to determine preferred size
		pack();

		setVisible(true);
	}
}
