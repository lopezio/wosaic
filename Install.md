### Contents ###
  * Platforms Supported
  * Quickstart
  * Windows
  * Mac OS X
    * Installer
    * Disk Image
  * Linux
  * More Information


## Platforms Supported ##

Because Wosaic is built using Java, it should run wherever Java does, including all flavors of Microsoft Windows, Mac OS X, and Linux.  However for completeness, it has been tested on the following platforms:

  * Ubuntu Linux x64 7.10 "Gutsy Gibbon"
  * Mac OS X 10.4 "Tiger"
  * Windows Vista x64 Business Edition

If you've installed Wosaic successfully on another platform, please post to the mailing-list with your results.


## Quickstart ##

Download the appropriate package for your platform.  Currently we have packages for Windows, Mac OS X, and Linux.  If your platform isn't listed, you may simply use the "wosaic.jar" executable tarball.

To install, simply double-click on the installer file to run it.  Fill in the appropriate information at each of the prompts, and Wosaic will be installed accordingly.  If you've specified shortcuts to be created, you can run Wosaic from there after it's installed.

For notes on your particular platform, please see the sections described below.


## Windows ##

On Windows, shortcuts will be created in the start menu, in the Wosaic folder.

If you are installing on Windows Vista, you may need to run the installed as an administrator.  This should be detected automatically, but if it's not, right-click on the installer icon, and choose "Run as Administrator".

Similarly, the uninstaller will need elevated permissions as well.  To uninstall, right click on the shortcut and choose "Run as Adminsitrator".


## Mac OS X ##

### Installer ###

To run the installer, simply double click on the wosaic-install-mac.jar file.

When you run the Mac installer, you will be asked to accept our terms and conditions.  These are simply adhering to GPL v2 open source license terms.  Wosaic will be installed in the directory of your choice.  By default, this is /Applications/Wosaic.

If you choose an existing directory, do not be alarmed if the installer warns you that it may be overwriting existing data.  All that will happen is that Wosaic.app will be copied to this directory.  If you have an existing copy of Wosaic.app in the same directory, it will get overwritten.

To uninstall, simply delete Wosaic.app from the folder you installed into.  Wosaic will now behave like a typical OS X application.

### Disk Image ###

Additionally, Wosaic can be installed from the [Mac OSX disk image](http://wosaic.googlecode.com/files/Wosaic-install-mac.dmg).  When this image finishes downloading, it will be automatically mounted on your system.  You will be prompted about this image potentially containing an application.  You have our word that it is safe to continue from this point on.  When the image has successfully mounted, you should be able to see it in the Finder

![http://wosaic.googlecode.com/svn/images/WosaicDMG.jpg](http://wosaic.googlecode.com/svn/images/WosaicDMG.jpg)

Installation is simply a matter of dragging the Wosaic icon to the Applications icon.  This will copy Wosaic to your applications folder.  Alternatively, you may drag and drop the Wosaic icon to any folder on your system (such as the Desktop).

## Linux ##

Running the installer will install Wosaic in the folder that you choose.  By default, it will install in a subdirectory of your home folder.  If you want to install in another location, such as /usr/local/Wosaic, be sure to run the installer with su or sudo.

If you are using GNOME, shortcuts can be created in the applications menu. By default, a shortcut to Wosaic will be created in the "Graphics" menu.


## More Information ##

For specific questions or to contact the developers directly, please use the Google Groups mailing-list, which is found at:
  * http://groups.google.com/group/wosaic-forum