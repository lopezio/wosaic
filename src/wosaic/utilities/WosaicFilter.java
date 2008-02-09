package wosaic.utilities;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

/**
 * Defines a custom and mutable file filter for use with various
 * open and save dialogues in the project.
 * 
 * @author carl-eriksvensson
 *
 */
public class WosaicFilter extends FileFilter {

	/**
	 * Determines if a particular file is accepted by
	 * this filter.
	 * @param file the file to be tested
	 */
	
	ArrayList<String> filters;
	
	/**
	 * Determine wheter we should accept directories
	 * or not
	 */
	boolean acceptDirs = true;

	/**
	 * Default constructor allows .jpg, .jpeg, and
	 * .bmp extensions, since these are the kinds of
	 * images we can safely process in Wosaic.
	 */
	public WosaicFilter() {
		filters = new ArrayList<String>();
		
		filters.add(".jpg");
		filters.add(".jpeg");
		filters.add(".bmp");
	}
	
	/**
	 * Constructor allowing us to specify whether or not
	 * to accept directories.
	 * @param dirs Whether or not we should accept directories
	 */
	public WosaicFilter(boolean dirs) {
		filters = new ArrayList<String>();
		
		filters.add(".jpg");
		filters.add(".jpeg");
		filters.add(".bmp");
		
		acceptDirs = dirs;
	}

	/**
	 * Defines the criteria for accepting a file.
	 */
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return acceptDirs;
		}
		
		// Find the extension
		String lcasePath = file.getAbsolutePath().toLowerCase();
		
		for (int i=0; i < filters.size(); i++) {
			if (lcasePath.endsWith(filters.get(i))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add an extension to the list of filters.
	 * @param filter name of the extension to be added.
	 */
	public void addFilter(String filter) {
		filters.add(filter);
	}
	
	/**
	 * Remove a specified type of extension from the
	 * file filter.
	 * @param filter string representation of the extension
	 * to be removed.
	 */
	public void removeFilter(String filter) {
		filters.remove(filter);
	}

	/**
	 * Returns a description of what this filter
	 * accepts.
	 */
	public String getDescription() {
		return "Images";
	}
}
