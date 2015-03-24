# Introduction #

Performance analysis for our project is important, since we're dealing with potentially slow operations such as File I/O, Image Manipulation, and Network Traffic.  We used a combination of trial performance tools (http://www.yourkit.com), and open source tools (http://jrat.sourceforge.net) to come up with some statistics for a sample run.


# Details #

The following data pertains to a run of Wosaic with parameters of using a 50x50 grid of images (that's 2500, not necessarily unique, total images), with a set of source images coming from 200 results of a Flickr search for "guitar."

## CPU Telemetry ##

The diagram below indicates the CPU usage during the run.  Note that the total time is about 2 minutes, and that the most CPU-intensive portions are at the beginning and at the end of the program's run.  This corresponds to initially analyzing the master image, and stitching together the final mosaic, respectively.

![http://wosaic.googlecode.com/svn/images/CPUTelemetrySmall.jpg](http://wosaic.googlecode.com/svn/images/CPUTelemetrySmall.jpg)

[(Larger View)](http://wosaic.googlecode.com/svn/images/CPUTelemetry.jpg)

## Call Graph ##

By looking at the call graph, we can see that most of the CPU time is dominated by calls to GetMoreResults.  This is the function that interfaces to Flickr and, in this specific example, gets 10 `BufferedImage`s from Flickr.  For performance reasons, we always get thumbnail-sized images from Flickr.  We do this to limit the network traffic, and note that with a typical mosaic, you will have thousands of images, there is not point in providing large resolutions for these small parts of an image.  We do, however want to make sure images are visible, and this decision comes in choosing the output resolution of the image: a topic for the [Algorithm](Algorithm.md) page.

![http://wosaic.googlecode.com/svn/images/CPUProfiles.jpg](http://wosaic.googlecode.com/svn/images/CPUProfiles.jpg)

## Memory Telemetry ##

The graphs of wosaic's memory usage are pretty dull until it comes time to construct the image.  At this point, the heap usage jumps to near maximal.  Apparently this has something to do with how we construct the resulting image.  This is one of the major performance hurdles we'll have to get over.  Otherwise we get memory corruption and our application will fail.

![http://wosaic.googlecode.com/svn/images/MemProfile.jpg](http://wosaic.googlecode.com/svn/images/MemProfile.jpg)

**Update:**  We've tracked down our memory issues to a memory leak in the JAI library that reared its ugly head when we started making 10,000 calls to `JAI.create("scale", ...)`.  Updated performance statistcs will follow with the fix to this issue.