# General Outline #

This is an outline of what happens when the user chooses to generate a mosaic.  Note that items one through three can proceed almost entirely in parallel.

  1. Query our sources for a photoset
  1. Split up the master picture into n segments and analyze the colors of these segments
  1. For each result we receive from our query
    1. Analyze its average color
    1. Place it in a regoin of the mosaic where it is a better fit than what already exists there
  1. When a user-defined threshold has been reached, stop processing

# Details #

## 1. Query Sources ##

We want our application to be flexible enough to easily allow for having multiple sources of photos.  Currently we support using pictures from both Flickr and Facebook in the mosaic.  Depending on what the user has chosen, this step involves instantiating a connection with the host, authenticating (if applicable), and querying for a set of photos.  These photos are then downloaded and returned for processing as a Buffered Image.  Each source has access to a shared buffer, which it acts as a producer for, and which the processing thread consumes from.

## 2. Analyze the Master Photo ##

Here 'master photo' refers to the larger picture that is to be split up and made into a mosaic.  Before running our algorithm, the user will have to specify roughly what resolution they want for their mosaic. That is, how many pictures to use in trying to recreate the image.  Once we have this information, we can then split the picture into n different regions (probably rectangles).  We will need to analyze each region to determine its average color.  We then use this value (plus or minus some tolerance) to search for pictures with the same average color to fill this region in the mosaic.

## 3. Processing Results ##

Every time we receive a picture from a source, we must analyze its contents to determine the picture's average color.  When possible, we retrieve thumbnail versions of the pictures in order to reduce download and processing time.  Once the average color is known, this picture is ready for processing.  When it gets processed, the image's average color is compared to the average color of all n segments of the master photo.  If the picture we are comparing currently has a better match than the image that already exists there, then we replace it with the image being processed.  In this manner, we assure that the best fit (closest average color) is chosen for each region.

## 4. User-Defined Threshold ##

Currently we attempt to retrieve a number of images equal to a parameter set by the user.  Once we have retrieved this number of images, we stop fetching images, and simply let the shared buffer get consumed until it is empty.  By default, we attempt to find 500 images from our sources to use in the mosaic.