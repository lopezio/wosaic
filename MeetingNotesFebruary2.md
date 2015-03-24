# General Notes #

We brought up the point of having source images that are square in dimension.  The MosaicPane attempts to partition the screen into squares, in order to fit the dimensions of the master image.  Having the source images be squares minimizes the amount of stretching that has to occur in order to fit the source image into the MosaicPaneTile.  We're not sure what the dimensions are of Facebook images, beyond a "thumbnail" size.  Any distortion from stretching will hopefully be minimal.

This led to an interesting point, which is that when we implement a file system plugin, we will probably have to tackle resizing each image.  We'll aim for square dimensions as much as possible.

We discussed the need for an update to the libraries we are using.  Facebook, Flickr, and JAI all have updates that we need to look into incorporating with our code.

We also talked about the state of the UI and a possible redesign.  It was proposed that we incorporate a step-by-step "wizard," or set of dialogues in order to alleviate any ambiguities about options and inputs.  This has the advantage of being clear for the user, and easy to validate inputs as we go.  The biggest drawback is having to sift through all the dialogues to make a new mosaic.  We noted that having the key properties readily available on the panel is most convenient.  Perhaps a good compromise is a combination of both the existing UI and a wizard.

We also briefly discussed caching a resized image in the Pixel object.  This needs to be developed further, and we should be careful about making too many references to unnecessary images.

## Status ##

We talked a bit about our accomplishments, or lack thereof this week:

  * [Issue14](http://code.google.com/p/wosaic/issues/detail?id=14) remains open.  Suspicion points to a bad JAI library.
  * First iteration of the Wosaic [icon](http://code.google.com/p/wosaic/downloads/detail?name=logo.png&can=2&q=#makechanges) has been designed
  * Cleaned up the ANT build, and removed all external dependencies
  * Implemented an [installer](http://izpack.org/).  This needs to be cleaned up a bit, but mostly works nicely.

## Issues ##

  * ANT Build does not build docs for older versions of ANT
  * Wosaic still crashes (runs out of memory) with certain inputs

## Goals ##

  * Look into getting rid of JAI completely
  * Update all external libraries
  * Add tooltips to UI elements
  * Look into file plugin (for source images)