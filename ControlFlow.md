# Introduction #

One of the inherent problems one encounters in making a mosaic is the sheer number of pictures you need to reconstruct the original, master image.  For Wosaic, this translates into a whole bunch of flickr queries, and a whole lot of image processing.  Initial tests indicate the the flickr queries are the biggest bottleneck, however there may still be something to gain if we can parallelize the process of retrieving photos, and processing them.  Ultimately, we want to allow the user to see his or her mosaic be created as matches are found.  This will help indicate some kind of progress, letting the user know that the application hasn't simply failed.


# Details #

We've broken up our execution into a few key players: WosaicUI, Controller, JAIProcessor, and FlickrService.  The following diagram shows how they are related.

**Control Flow**

![http://wosaic.googlecode.com/svn/images/ControlFlow.jpg](http://wosaic.googlecode.com/svn/images/ControlFlow.jpg)

The WosaicUI is the user interface which is responsible for collecting user input.  This includes the initial master image, some parameters about the resolution of the mosaic, and a flickr search string.  The search string is used to query flickr for a set of photos.

The Controller is the link between the FlickrServices and JAIProcessing.  It ushers parameters back and forth between the two, and gives the final output back to the user interface.  We hope to be able to parallelize some of the image processing and flickr queries.  As of now, the Controller kicks off a couple of threads: one to get a subset of flickr images, and one to incrementally analyze the results as they come in.  The Controller  manages these threads and provides accessors for sleeping and killing errant threads.

JAIProcessor does the grunt work of analyzing source images, segmenting the master image into regions, and finding a match for each region.  When it is running as a thread, it checks the status of a shared buffer that resides in Controller.  If it has images to be analyzed, it will grab them from the buffer and analyze them.  See [Algorithm](Algorithm.md) for further details.

FlickrService provides an interface to Flickr photos.  It is the primary source for images in the mosaic.  It produces images for the shared buffer that resides in controller.  See [Algorithm](Algorithm.md) for more information.