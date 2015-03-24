## Contents ##
  * Introduction
  * Quickstart
  * Mosaic Options
    * Basic Options
    * Advanced Options
  * Source Plugins
    * Flickr Plugin
    * Facebook Plugin
    * Local Files Plugin
  * Saving
  * More Information

## Introduction ##

Ever wondered what you could do with all the hundreds of digital pictures that you've accumulated? Why not make a mosaic?

Wosaic is an open source project that allows you to recreate an existing image by using many smaller images, as shown above. It was started as a project for CS 242 - Programming Studio at UIUC, and will be continuing on to Engineering Open House in the Spring of 2008.

Wosaic is not the only program of its kind. Several other solutions already exist, however Wosaic aims to be free, easy to use, fast, and accessible. These qualities are yet to be found in any single existing solution. We're also aiming to make use of interesting sources for images. Currently, we support Flickr Facebook, and local sources, but we hope to expand this to possibly include images from Picassa, and hopefully other sources as well.


## Quickstart ##

To get started, open Wosaic from the installed directory on your computer.  Click the "Browse" button to choose the picture that will be the source of your mosaic.  Next, set a search string to get thumbnail images from Flickr.  Then, simply press "Generate Mosaic", and watch the mosaic created in front of your eyes!

## Mosaic Options ##

As seen in the quickstart, there are a number of variables that control the ouput of your mosaic.  Below, we briefly describe the function of each one.

### Basic Options ###

When you startup Wosaic, you see the "Mosaic" tab, which contains the basic options for your mosaic:

  * Source Image: This is the main image that your mosaic will be constructed from.  Smaller images will be pulled from various sources to reproduce the source image.  Press "Browse.." to select an image from your computer.

  * Search String: This is the query that will be used to search the internet for pictures.  Results will be scaled down to small thumbnails and inserted into the mosaic.  Currently the search string is only used for Flickr, but will be used in future plugins as well.

  * Resolution: This dictates how many smaller images will make up a row or column in your mosaic.  Smaller resolutions will produce a more accurate looking mosaic.  However, larger resolutions allow you to more easily view the thumbnail pictures.  The resolution entered in the box will be used for the larger dimension in your source image.

### Advanced Options ###

In the Wosaic interface, you can click the "Advanced Options" tab to select plugins and change advanced options.

  * Mosaic Dimensions: Allows you to configure the width and height of the generated mosaic.  By default, the mosaic will keep the dimensions of the source image.  However, you can also choose a multiple (ie 2.0 would make the output image twice as large), or set the number of pixels in each dimension individually.  This option can be changed even after generating the mosaic, and affects the dimensions for saving.

## Source Plugins ##

By default, Wosaic uses Flickr to aggregate thumbnail images to use within the mosaic.  However, there are a number of other plugins that can be used as well.  You can use many plugins at the same time, and each one is configured independently.

To enable a plugin, simply select from the "Disabled Sources" list, and press the right arrow button.  Similarly, to disable a plugin, select it from the "Enabled Sources", and press left arrow button.  To change configuration options for a plugin, select it from the "Enabled Sources", and click "Config".


### Flickr Plugin ###

This is the default plugin.  Images are returned based on the search string provided in the main options panel.  In the plugin configuration dialog, you can configure how many images are returned.  The default is 500.

### Facebook Plugin ###

The Facebook plugin allows you to create mosaics using every picture you are tagged in on Facebook.  To use this plugin, enable it in the "Advanced Options" panel.  Then, press "Authenticate" in the plugin configuration popup to login to Facebook.

### Local Files Plugin ###

This plugin allows you to use picture albums inside your mosaic that are stored on your computer.  To use it, enable it and open the configuration dialog.  Select a directory where your photos are stored.  If you have images inside of folders inside this directory, select "Search Subdirectories."


## Saving ##

Once you have created a mosaic you are happy with, you can save it on your computer.  Simply press the "Save" button, and choose a name for the image.  This is useful if you'd like to set the mosaic as your desktop background, or view it in another program.  You can also save at different resolutions by changing the settings in the "Advanced Options".

## More Information ##

For the most up-to-date information on the Wosaic project, refer to the project website at:
  * http://code.google.com/p/wosaic/
The webpage contains project news, release binaries, bug reports, and a wiki with other details.

For specific questions or to contact the developers directly, please use the Google Groups mailing-list, which is found at:
  * http://groups.google.com/group/wosaic-forum