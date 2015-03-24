# Introduction #

Here we will post updates on the production status of Wosaic.  This is a work in progress, so the functionalities are liable to change.

# Features #

Currently Wosaic takes as input one source image from the user's hard drive.  In its simplest form, the only other parameters are a search string for creating a Flickr query and a resolution parameter.  The resolution is a single number, n, that describes the number of images that will be used to reconstruct the user's source image.  This image will be split into roughly an nxn grid of images taken from Flickr, which will be arranged in a pattern the mimics the user's source image.

For simplicity's sake, the Wosaic application works with only these three parameters, however more options can be specified.  In addition to using images from a Flickr search, the user can elect to use images from Facebook.  We're currently exploring other sources for images.  The Facebook images come from images that the user is tagged in.  The user has the option of using either Facebook, Flickr or both.  Facebook requires authentication, which is handled via a quick redirect to the facebook login page.  Authentication is only required once.

There are other options related to the dimensions of the mosaic.  The user can choose various ways of specifying the resolution of the resulting mosaic.  The user can either specify a multiple of the original image, the explicit width and height in pixels, or simply use the original source image's size.  By default the original size is used.

The user can also specify how many images to look for when making the mosaic.  Searching for more images will take longer, but may result in finding better fits for the mosaic.

# Demo #

Below is a sample run of our application.  The master image is made up of flickr images that were returned by the search string 'guitar.'  Two different granularities are shown below.

**Master**

![http://wosaic.googlecode.com/svn/images/guitar.jpg](http://wosaic.googlecode.com/svn/images/guitar.jpg)

**Mosaic**

![http://wosaic.googlecode.com/svn/images/mosaic.jpg](http://wosaic.googlecode.com/svn/images/mosaic.jpg)

[Hi-Res Mosaic](http://wosaic.googlecode.com/svn/images/FBFlickrMosaic.jpg)