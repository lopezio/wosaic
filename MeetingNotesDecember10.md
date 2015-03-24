## Plugins ##

  * We both like the idea of having some sort of abstract "Sources" class that separate input plugins can inherit from.

# Interface #

  * Making an interface for the plugins and each of their options will be difficult.  We want it to be intuitive, scalable, robust, but easy for the common-case.
  * We've decided to have something similar to Pidgin's plugins box.  However, a default search string will be presented on the main interface tag, and will be used by default for most plugins.
  * Flickr plugin will be the only one enabled by default.
  * When a user selects a new plugin, the configuration options will automatically pop-up, and the plugin can validate that any required options have been filled in.

# Implementation #

  * When a plugin finishes fetching images, it can call a method in the Mosaic like "I'm done", incrementing a variable in the Mosaic.  We know when they are all finished when the variable gets to a certain number.  This will also allow us to update a progress bar.
  * We plan to have a "sources" object, which basically consists of an ArrayList of our plugins.  A reference to this object will be needed at least at the UI and controller level.

  * Our "PluginInterface" abstract class should implement Runnable, so each plugin can run in it's own thread.
  * They will also need to have a function (perhaps implemented in PluginInterface) to add images to the buffer, and then report that it's finished.
  * Also, each plugin should have a JPanel of some sort, to define it's own configuration options that can be set.  We'll need to think of some way to update the general configuration options (default search string, for now)

![http://wosaic.googlecode.com/svn/images/SourcesFlow.jpg](http://wosaic.googlecode.com/svn/images/SourcesFlow.jpg)

**Figure** - The new control flow, with a proposed refactoring of various sources
## Other Issues ##

  * The UI for displaying the Wosaic still needs to be refactored and re-written.  Scott is going to work on that.
  * Carl will start looking into the PluginInterface.
  * We will also address an automated build system, as well as hosting the applet online, when we find time.
  * We've decided to use subversion branch-and-merge style to make these changes.

## Issues Recap ##

  * [Issue 3](https://code.google.com/p/wosaic/issues/detail?id=3), Encapsulation: We've made good progress, but we'll keep it open as a reminder to keep making encapsulaton better.
  * [Issue 4](https://code.google.com/p/wosaic/issues/detail?id=4), Update Display: That'll be handled with the UI re-write.
  * [Issue 5](https://code.google.com/p/wosaic/issues/detail?id=5), Get rid of JAI: We can come back to this later.  We've eliminated most of the memory hogs, but a few occurences are still there.
  * [Issue 6](https://code.google.com/p/wosaic/issues/detail?id=6), Scaled Mosaic display: Once again, will be fixed with the display re-write.
  * [Issue 7](https://code.google.com/p/wosaic/issues/detail?id=7), Save fails: This still needs to be addressed, no ideas yet.
  * [Issue 8](https://code.google.com/p/wosaic/issues/detail?id=8), Slow update UI: We can look into this after the UI re-write, it may fix itself.
  * [Issue 9](https://code.google.com/p/wosaic/issues/detail?id=9), Status bar: This still needs more thought.  As a starting point, we could have a function in the UI UpdateStatus(status, priority), where only high priorites get upgraded to the status bar, and the rest are on stdout.