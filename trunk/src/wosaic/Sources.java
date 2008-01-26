/**
 * 
 */
package wosaic;

import wosaic.exceptions.FlickrServiceException;
import wosaic.utilities.*;
import java.util.ArrayList;

/**
 * @author carl-erik svensson
 *
 */
public class Sources {

	private ArrayList<SourcePlugin> sources;
	private ArrayList<SourcePlugin> enabledSources;
	private Status statusObject;
	
	public static String FACEBOOK = "Facebook";
	public static String FLICKR = "Flickr";
	
	/**
	 * Initializes the sources list.
	 */
	public Sources(Status stat) {
		sources = new ArrayList<SourcePlugin>();
		enabledSources = new ArrayList<SourcePlugin>();
		statusObject = stat;
		
		// Instantiate sources
		sources.add(new Facebook());
		
		FlickrService flickr = null;
		
		try {
			flickr = new FlickrService();
		} catch (FlickrServiceException e) {
			System.out.println("Unable to instantiate flickr in Sources obj!");
			e.printStackTrace();
			return;
		}
		
		sources.add(flickr);
		
		// Automatically enabled sources
		enabledSources.add(flickr);
		
		// Set the status objects of all sources
		for (int i = 0; i < sources.size(); i++) {
			sources.get(i).setStatusObject(statusObject);
		}
	}
	
	/**
	 * Add a source to the pool of enabled sources.
	 * @param src the SourcePlugin to be added.
	 * @return true if successful, false otherwise
	 */
	public boolean addSource(SourcePlugin src) {

		if (src != null) {
			int p = enabledSources.indexOf(src);
			
			if (p < 0) {
				enabledSources.add(src);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add an enabled source, based on that source's 
	 * string type.
	 * @param src the string describing the source
	 * @return true when successful, false otherwise
	 */
	public boolean addSource(String src) {
		SourcePlugin s = findType(src);
		
		if (s != null) {
			boolean val = addSource(s);
			return val;
		}
		
		return false;
	}
	
	/**
	 * Remove a source from the pool of enabled sources.
	 * @param src the SourcePlugin to be removed.
	 * @return if the removal was a success.
	 */
	public boolean removeSource(SourcePlugin src) {
		boolean val = enabledSources.remove(src);
		return val;
	}
	
	/**
	 * Remove an enabled source, based on that source's 
	 * string type.
	 * @param src the string describing the source
	 * @return true when successful, false otherwise
	 */
	public boolean removeSource(String src) {
		SourcePlugin s = findType(src);
		
		if (s != null) {
			boolean val = removeSource(s);
			return val;
		}
		
		return false;
	}
	
	/**
	 * Get a reference to the list of sources.
	 * @return the ArrayList of sources.
	 */
	public ArrayList<SourcePlugin> getSources() {
		return sources;
	}
	
	/**
	 * 
	 * @return a string array containing the names of each source.
	 */
	public String[] getSourcesList() {
		String[] arr = new String[sources.size()];
		
		for (int i = 0; i < sources.size(); i++) {
			arr[i] = sources.get(i).getType();
		}
		
		return arr;
	}
	
	/**
	 * 
	 * @return a string array containing the names of each enabled
	 *  source.
	 */
	public String[] getEnabledSourcesList() {
		String[] arr = new String[enabledSources.size()];
		
		for (int i = 0; i < enabledSources.size(); i++) {
			arr[i] = enabledSources.get(i).getType();
		}
		
		return arr;
	}
	
	/**
	 * Get a reference to all the enabled sources.
	 * @return the ArrayList of enabled sources.
	 */
	public ArrayList<SourcePlugin> getEnabledSources() {
		return enabledSources;
	}
	
	/**
	 * Checks if a source is in the enabled list.
	 * @param src the string representation of the source.
	 * @return true if the source is enabled, false otherwise.
	 */
	public boolean isEnabled(String src) {
		for (int i = 0; i < enabledSources.size(); i++) {
			if (enabledSources.get(i).getType() == src) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Looks for a source of the given type.
	 * @param type a string describing the source.
	 * @return a reference to the source, or null if it
	 * is not found.
	 */
	public SourcePlugin findType(String type) {
		for (int i = 0; i < sources.size(); i++) {
			if (sources.get(i).getType() == type) {
				return sources.get(i);
			}
		}
		
		return null;
	}
}
