package wosaic;

import java.util.ArrayList;

import wosaic.exceptions.FlickrServiceException;
import wosaic.utilities.Facebook;
import wosaic.utilities.FilesystemPlugin;
import wosaic.utilities.SourcePlugin;
import wosaic.utilities.Status;

/**
 * @author carl-erik svensson
 * 
 */
public class Sources {

	/**
	 * A list of all the possible source plugins that we can use
	 */
	public static enum Plugin {
		/**
		 * Facebook plugin, takes images from a user's Facebook profile
		 */
		Facebook,
		/**
		 * Filesystem plugin, uses images that are on the user's computer
		 */
		Filesystem,
		/**
		 * Flickr plugin, takes images based on a Flickr search
		 */
		Flickr
	}

	/**
	 * A list of strings representing the plugins that should be used by default
	 */
	protected static final Plugin[] DEFAULT_PLUGINS = { Plugin.Flickr };

	protected SourcePlugin[] enabledSources;

	protected Status status;

	/**
	 * Initializes the sources list.
	 */
	public Sources(final Status statusObject) {

		enabledSources = new SourcePlugin[Plugin.values().length];
		for (int i = 0; i < enabledSources.length; i++)
			enabledSources[i] = null;

		for (final Plugin element : Sources.DEFAULT_PLUGINS)
			addSource(element);

		status = statusObject;
	}

	/**
	 * Add a source to the pool of enabled sources.
	 * 
	 * @param src
	 *            the SourcePlugin to be added.
	 * @return true if successful, false otherwise
	 */
	public boolean addSource(final Plugin src) {

		if (enabledSources[src.ordinal()] != null)
			return false;

		SourcePlugin thePlugin = null;

		switch (src) {
		case Facebook:
			thePlugin = new Facebook();
			break;

		case Flickr:
			try {
				thePlugin = new FlickrService();
			} catch (final FlickrServiceException e) {
				// TODO: Handle this error gracefully, or at least print
				// something!
			}
			break;

		case Filesystem:
			thePlugin = new FilesystemPlugin();
			break;
		}

		if (thePlugin != null)
			thePlugin.setStatusObject(status);
		enabledSources[src.ordinal()] = thePlugin;
		return thePlugin != null;
	}

	/**
	 * Add an enabled source, based on that source's string type.
	 * 
	 * @param src
	 *            the string describing the source
	 * @return true when successful, false otherwise
	 */
	public boolean addSource(final String src) {
		final Plugin thePlugin = Plugin.valueOf(src);
		return addSource(thePlugin);
	}

	/**
	 * Get a reference to all the enabled sources.
	 * 
	 * @return the ArrayList of enabled sources.
	 */
	public ArrayList<SourcePlugin> getEnabledSources() {
		final ArrayList<SourcePlugin> retList = new ArrayList<SourcePlugin>();
		for (final SourcePlugin element : enabledSources)
			if (element != null)
				retList.add(element);

		return retList;
	}

	/**
	 * 
	 * @return a string array containing the names of each enabled source.
	 */
	public String[] getEnabledSourcesList() {
		final ArrayList<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < enabledSources.length; i++)
			if (enabledSources[i] != null)
				tmpList.add(Plugin.values()[i].toString());

		final String[] retArr = new String[tmpList.size()];
		return tmpList.toArray(retArr);
	}

	/**
	 * 
	 * @return a string array containing the names of each source.
	 */
	public String[] getSourcesList() {
		final Plugin[] plugins = Plugin.values();
		final String[] arr = new String[plugins.length];

		for (int i = 0; i < plugins.length; i++)
			arr[i] = plugins[i].toString();

		return arr;
	}

	/**
	 * Checks if a source is in the enabled list.
	 * 
	 * @param src
	 *            the string representation of the source.
	 * @return true if the source is enabled, false otherwise.
	 */
	public boolean isEnabled(final String src) {

		return enabledSources[Plugin.valueOf(src).ordinal()] != null;
	}

	/**
	 * Remove a source from the pool of enabled sources.
	 * 
	 * @param src
	 *            the SourcePlugin to be removed.
	 * @return if the removal was a success.
	 */
	public boolean removeSource(final Plugin src) {
		if (enabledSources[src.ordinal()] == null)
			return false;

		enabledSources[src.ordinal()] = null;
		return true;
	}

	/**
	 * Remove an enabled source, based on that source's string type.
	 * 
	 * @param src
	 *            the string describing the source
	 * @return true when successful, false otherwise
	 */
	public boolean removeSource(final String src) {
		final Plugin thePlugin = Plugin.valueOf(src);
		return removeSource(thePlugin);
	}
}
