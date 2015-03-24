# General Notes #

We discussed the advantages of having Wosaic as an application instead of an applet.  The upside is that it will make for a clearer, simpler build process (probably), and that the number of classes and amount of memory usage is less of a limiting factor in an application setting, versus a remote applet.  There are still advantages to having an applet, and efforts will be made to remedy the issues we've had with launching an applet online.  In order to port our application, we decided to try to find a common base class between JApplet and JFrame, and move as much implementation into an extension of this class, then have two very small classes, one an applet and one a standard application.

We also discussed how to potentially cleanup the WosaicUI class.  We identified that we could move code for all the action listeners, and nested classes outside of WosaicUI for clarity.  One issue with this is the need to maintain references to the WosaicUI elements (such as buttons, textfields and such).

We brainstormed some ideas for a more involved pixel-matching algorithm.  The idea is to break up a slice of the image into regions (top, left, bottom, right, for instance) and then compare each of these regions in every position.  One thing to consider is what would happen on the edges and in corners.  The 'score' for a slice would be determined by the sum of the difference between each region and the corresponding position in the original image.  A lower score is a better match.  Additionally, we can take the square of these differences in order to punish very bad matches and reward better overall matches.

Lastly, we discussed the need for a plugin that gets images from a user's hard disk.  This has been tabled until further notice, citing more pressing issues for the upcoming week.

## Status ##

We listed the accomplishments of the past week.  These include:
  * Adding a progress bar and status messages to the UI
  * Adding file filters to open and save
  * Fixing a bug in FlickrServices ([Issue11](http://code.google.com/p/wosaic/issues/detail?id=11)), and some code cleanup
  * Merging UI changes with the trunk
  * Cleaning up source tree


## Goals ##

  * Debug a potential show-stopper memory leak ([Issue14](http://code.google.com/p/wosaic/issues/detail?id=14))
  * Look into porting the applet to an application
  * Complete the build process with a way to run the application outside of an IDE
  * Start tackling the WosaicUI cleanup
  * Look into implementing new pixel-matching algorithm
  * Possibly look into implementing a local file plugin