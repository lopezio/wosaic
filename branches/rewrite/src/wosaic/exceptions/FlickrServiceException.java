/**
 * 
 */
package wosaic.exceptions;

/**
 * An exception representing an error from Flickr
 * 
 * @author scott
 */
public class FlickrServiceException extends Exception {

	// TODO: Add data members that will allow us to target specific
	// causes of an error.

	/**
	 * Automatically-generated version ID
	 */
	private static final long serialVersionUID = 7605295540343513761L;

	/**
	 * @see Exception#Exception()
	 */
	public FlickrServiceException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public FlickrServiceException(final String arg0) {
		super(arg0);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public FlickrServiceException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public FlickrServiceException(final Throwable arg0) {
		super(arg0);
	}

}
