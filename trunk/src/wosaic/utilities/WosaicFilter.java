package wosaic.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 * Defines a custom and mutable file filter for use with various open and save
 * dialogues in the project.
 * 
 * @author carl-eriksvensson
 */
public class WosaicFilter extends FileFilter implements java.io.FileFilter {

	/**
	 * Determine whether we should accept directories or not
	 */
	private boolean acceptDirs = true;

	private ArrayList<String> filters;

	/**
	 * Default constructor allows all filters accepted by ImageIO natively. Also
	 * accept directories by default.
	 */
	public WosaicFilter() {
		this(true);
	}

	/**
	 * Default constructor allows all filters accepted by ImageIO natively.
	 * 
	 * @param dirs Whether or not we should accept directories
	 */
	public WosaicFilter(final boolean dirs) {
		filters = new ArrayList<String>();

		filters.addAll(Arrays.asList(ImageIO.getReaderFormatNames()));

		acceptDirs = dirs;
	}

	/**
	 * Defines the criteria for accepting a file.
	 */
	@Override
	public boolean accept(final File file) {
		if (file.isDirectory()) return acceptDirs;

		// Find the extension
		final String lcasePath = file.getAbsolutePath().toLowerCase();

		for (int i = 0; i < filters.size(); i++)
			if (lcasePath.endsWith("." + filters.get(i))) return true;

		return false;
	}

	/**
	 * Add an extension to the list of filters.
	 * 
	 * @param filter name of the extension to be added.
	 */
	public void addFilter(final String filter) {
		filters.add(filter);
	}

	/**
	 * Returns a description of what this filter accepts.
	 */
	@Override
	public String getDescription() {
		return "Images";
	}

	/**
	 * Remove a specified type of extension from the file filter.
	 * 
	 * @param filter string representation of the extension to be removed.
	 */
	public void removeFilter(final String filter) {
		filters.remove(filter);
	}
}
