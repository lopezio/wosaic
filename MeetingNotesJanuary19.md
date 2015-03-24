This was the first of a series of weekly meetings we will be holding on Saturdays during the Spring semester.

# General Notes #
  * We have final solved most of the problems involved with the Mosaic content showing up on the screen.  Once we checkin the code to the main branch, we can close a few of the currently opened issues.
  * Now that this problem is fixed, it would be nice to first display the initial image, and then display our children on top of it.  This should be easy to do.
  * WosaicUI class could use a great deal of cleanup. Most of the actual code is shoved into one method.  We should refactor this method, and pull out some of the sub-classes into their own files, to de-clutter a little bit.
  * When we have time, it would also be a good idea to talk about thread management.  With a brief understanding of what's going on, it seems that there isn't much consistency on how we launch and manage threads right now-- we could potentially maintain one or a few thread pools for each "worker" to have access to.
  * Another good candidate for improvement will be each of the plugins.  There is more functionality built into both Flickr and Facebook, which we could still exploit.
  * Error management:  right now we have very sparse error management as well.  We should make a full interface for reporting errors to the user and otherwise.  Also, we could define a "debug" parameter to dictate how much error info we display.
  * Along the same lines, we should implement a status bar.  Not only can this display helpful information to the user, but we could use a progress bar as well.
  * We could also use some cleanup in our build process.  Particularly, it would be nice to have a README file for users to be able to build and download themselves.  Also, we can cleanup our source directories, and figure out a way to run our applet/application without eclipse.
  * On the topic of presentation, our Wiki is getting slightly out of date.  We could potentially post more details and useful information for our project.  We need to think about going public fairly soon.
  * JAI stuff: Not too important, but it would be nice to eliminate all-together.

## Goals ##

We made a list of goals to try and accomplish before our meeting next week.

### Carl ###
  * Have filters setup for open and save dialogs
  * Create a progress and status bar that different components can hook into.
  * Setup the project as an application (rather than, or in addition to, an applet)

### Scott ###
  * Fix the listed bugs in FlickrService2
  * Checkin code for the UI fix
  * Start cleanup in WosaicUI
  * Create some useful content for the Wiki and README.


## Next Meeting ##

Our next meeting is scheduled for Saturday, January 26, at 11 AM.