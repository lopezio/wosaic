package wosaic;

import javax.swing.JApplet;

/**
 * The Applet version of the Wosaic project
 * 
 * @author scott
 */
public class WosaicApplet extends JApplet {

	/**
	 * Version ID generated by Eclipse
	 */
	private static final long serialVersionUID = 6370224523687153929L;

	/**
	 * Initialize our Applet and display it
	 * 
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init() {
		add(new WosaicUI());
	}
}