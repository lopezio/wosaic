# Wosaic 1.0 beta #
_released 3/6/08_

This release represents a major rewrite in the code for the GUI and general control flow.  While this won't be immediately apparent at first glance, this means major efficiency gains and better stability.

Create multiple mosaics in the same session, and even Cancel processing mid-generation.  Also, the resolution parameters don't take effect until saving, which means you can save a Mosaic at a variety of different resolutions.

To install, simply [download](http://code.google.com/p/wosaic/downloads/list) the package for your platform, and run it.  You should be able to safely install on top of a previous version.

For more details, please see the following wiki pages:
  * [README](README.md)
  * [Install](Install.md)
  * [Usage](Usage.md)
  * [Compile](Compile.md)

## Changelog ##
  * Expand support for all natively-handled image types, including PNG and GIF
  * Complete rewrite of UI back-end code for performance and maintainability
  * Add tooltip hints to many UI components
  * Add Javadoc comments for many more API calls.
  * Change the "All Sources" UI element to "Disabled Sources", and update other related components accordingly
  * Moved dimensions selection to save-time, to be able to save a mosaic in different sizes.
  * Improved threading for picture queries to improve generation times
  * More caching to improve displaying and saving the Mosaic.
  * Fix bug in retrieving pictures from Flickr.
  * Change the progress bar to remove arbitrary progress.