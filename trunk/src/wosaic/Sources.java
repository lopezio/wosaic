package wosaic;

import java.util.ArrayList;

import wosaic.exceptions.FlickrServiceException;
import wosaic.utilities.Facebook;
import wosaic.utilities.FilesystemPlugin;
import wosaic.utilities.SourcePlugin;
import wosaic.utilities.Status;

/**
 * @author carl-erik svensson
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

	/**
	 * List of plugins that actually use the search string
	 */
	static final Sources.Plugin[] SearchablePlugins = { Plugin.Flickr };

	/**
	 * Array of SourcePlugins. Those are aren't enabled will be set to null
	 */
	protected SourcePlugin[] PluginObjects;

	/**
	 * A reference to the status object, which gets passed to each source plugin
	 */
	protected Status status;

	/**
	 * Initializes the sources list.
	 * 
	 * @param statusObject the status object which notifies the user of progress
	 */
	public Sources(final Status statusObject) {

		PluginObjects = new SourcePlugin[Plugin.values().length];
		for (int i = 0; i < PluginObjects.length; i++)
			PluginObjects[i] = null;

		for (final Plugin element : Sources.DEFAULT_PLUGINS)
			addSource(element);

		status = statusObject;
	}

	/**
	 * Add a source to the pool of enabled sources.
	 * 
	 * @param src the SourcePlugin to be added.
	 * @return true if successful, false otherwise
	 */
	public boolean addSource(final Plugin src) {

		if (PluginObjects[src.ordinal()] != null) return false;

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

		if (thePlugin != null) thePlugin.setStatusObject(status);
		PluginObjects[src.ordinal()] = thePlugin;
		return thePlugin != null;
	}

	/**
	 * Add an enabled source, based on that source's string type.
	 * 
	 * @param src the string describing the source
	 * @return true when successful, false otherwise
	 */
	public boolean addSource(final String src) {
		final Plugin thePlugin = Plugin.valueOf(src);
		return addSource(thePlugin);
	}

	/**
	 * Returns the corresponding, enabled SourcePlugin of the given name.
	 * 
	 * @param s - string of the name of the SourcePlugin
	 * @return the SourcePlugin of the desired type
	 */
	public SourcePlugin findType(final String s) {
		final int idx = Sources.Plugin.valueOf(s).ordinal();

		if (idx >= 0 && idx < Plugin.values().length)
			return PluginObjects[idx];

		// else
		return null;
	}

	/**
	 * Return an array of Strings representing disabled source plugins
	 * 
	 * @return an array of Strings for the disabled sources
	 */
	public String[] getDisabledSourcesList() {
		final ArrayList<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < PluginObjects.length; i++)
			if (PluginObjects[i] == null)
				tmpList.add(Plugin.values()[i].toString());

		final String[] retArr = new String[tmpList.size()];
		return tmpList.toArray(retArr);
	}

	/**
	 * Get a reference to all the enabled sources.
	 * 
	 * @return the ArrayList of enabled sources.
	 */
	public ArrayList<SourcePlugin> getEnabledSources() {
		final ArrayList<SourcePlugin> retList = new ArrayList<SourcePlugin>();
		for (final SourcePlugin element : PluginObjects)
			if (element != null) retList.add(element);

		return retList;
	}

	/**
	 * @return a string array containing the names of each enabled source.
	 */
	public String[] getEnabledSourcesList() {
		final ArrayList<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < PluginObjects.length; i++)
			if (PluginObjects[i] != null)
				tmpList.add(Plugin.values()[i].toString());

		final String[] retArr = new String[tmpList.size()];
		return tmpList.toArray(retArr);
	}

	/**
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
	 * @param src the string representation of the source.
	 * @return true if the source is enabled, false otherwise.
	 */
	public boolean isEnabled(final String src) {

		return PluginObjects[Plugin.valueOf(src).ordinal()] != null;
	}

	/**
	 * Remove a source from the pool of enabled sources.
	 * 
	 * @param src the SourcePlugin to be removed.
	 * @return if the removal was a success.
	 */
	public boolean removeSource(final Plugin src) {
		if (PluginObjects[src.ordinal()] == null) return false;

		PluginObjects[src.ordinal()] = null;
		return true;
	}

	/**
	 * Remove an enabled source, based on that source's string type.
	 * 
	 * @param src the string describing the source
	 * @return true when successful, false otherwise
	 */
	public boolean removeSource(final String src) {
		final Plugin thePlugin = Plugin.valueOf(src);
		return removeSource(thePlugin);
	}

	/**
	 * Determines if any enabled plugins are using a search string.
	 * 
	 * @return true if we do need a search string, false if not
	 */
	public boolean usingSearchString() {
		for (final Plugin element : Sources.SearchablePlugins)
			if (PluginObjects[element.ordinal()] != null) return true;

		return false;
	}

	/**
	 * Iterate through each enabled source and validate its parameters
	 * 
	 * @return an error string on failure, null on success
	 */
	public String validateSources() {
		String err = null;

		for (final SourcePlugin element : PluginObjects)
			if (element != null) err = element.validateParams();

		return err;
	}
}
