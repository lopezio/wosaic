package wosaic.utilities;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

public class WosaicFilter extends FileFilter {

	/**
	 * Determines if a particular file is accepted by
	 * this filter.
	 * @param file the file to be tested
	 */
	
	ArrayList<String> filters;
	
	public WosaicFilter() {
		filters = new ArrayList<String>();
		
		filters.add(".jpg");
		filters.add(".jpeg");
		filters.add(".bmp");
	}
	
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
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
	
	public void addFilter(String filter) {
		filters.add(filter);
	}
	
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
