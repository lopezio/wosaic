/**
 * 
 */
package wosaic;

import wosaic.exceptions.FlickrServiceException;
import wosaic.utilities.*;
import java.util.ArrayList;

/**
 * @author carl-eriksvensson
 *
 */
public class Sources {

	private ArrayList<SourcePlugin> sources;
	private ArrayList<SourcePlugin> enabledSources;
	
	/**
	 * Initializes the sources list.
	 */
	public Sources() {
		sources = new ArrayList<SourcePlugin>();
		enabledSources = new ArrayList<SourcePlugin>();
		
		// Instantiate sources
		sources.add(new Facebook());
		
		FlickrService2 flickr = null;
		
		try {
			flickr = new FlickrService2();
		} catch (FlickrServiceException e) {
			System.out.println("Unable to instantiate flickr in Sources obj!");
			e.printStackTrace();
			return;
		}
		
		sources.add(flickr);
		
		// Automatically enabled sources
		enabledSources.add(flickr);
	}
	
	/**
	 * Add a source to the pool of enabled sources.
	 * @param src the SourcePlugin to be added.
	 */
	public void addSource(SourcePlugin src) {
		enabledSources.add(src);
	}
	
	/**
	 * Remove a source from the pool of enabled sources.
	 * @param src the SourcePlugin to be removed.
	 * @return if the removal was a success.
	 */
	public boolean removeSource(SourcePlugin src) {
		return enabledSources.remove(src);
	}
	
	/**
	 * Get a reference to the list of sources.
	 * @return the ArrayList of sources.
	 */
	public ArrayList<SourcePlugin> getSources() {
		return sources;
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
