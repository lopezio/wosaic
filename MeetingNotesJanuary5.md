## General Updates ##

  * Updated the process of displaying a mosaic
    * We discussed the benefits of displaying the entire mosaic, versus the mosaic at its full output resolution
    * Resizing and repainting still need to be handled
    * Planned to implement a MosaicContainer that handles resizing, scroll bars, and painting corrrectly

  * Redesigned the sources/plugin code
  * There is now one Sources object which instantiates each source
    * This provides wrapper functions for adding/removing/finding sources
    * It keeps two lists, one of all sources, and one of enabled sources
  * Added a default constructor for FlickrService2 and some helper methods to populate target images and sources buffer

  * Asked about the following code in FlickrService2
```
static {
    // Connect to flickr
    try {
    FlickrService2.Connect();
    } catch (final ParserConfigurationException ex) {
    }
}

```

  * This is a static constructor.  It is only called once, regardless of how many times the object is instantiated.
  * This lets us use a single connection to flickr for all queries

## Plugins Interface ##

  * Discussed the plugins interface in the Advanced Options
    * Want to move away from having two lists
    * If there are two lists, only one item should be selectable
    * The two lists should be mutually exclusive
    * Instead, ideally add checkboxes
    * This can be done dynamically by using an ArrayList of checkboxes, visible to all UI components
    * Look into using a row of checkboxes and text as a list element

## Build Process ##

  * Discussed the build process for our application
    * Noted that Eclipse has a feature to export (at least) to a Mac Application
    * Discovered how to auto-generate an Ant build file and added this to the repository
    * Decided to try converting to an Application instead of an Applet, for experimentation purposes