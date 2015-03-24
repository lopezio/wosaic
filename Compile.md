### Contents ###
  * Supported Platforms
  * Supported Compilers
  * Obtaining Source Code
  * Dependencies
  * Building With Ant
  * Building From Eclipse
  * Building From other IDEs
  * More Information


## Supported Platforms ##

Because Wosaic is built using Java, it should run wherever Java does, including all flavors of Microsoft Windows, Mac OS X, and Linux.  However for completeness, it has been tested on the following platforms:

  * Ubuntu Linux x64 7.10 "Gutsy Gibbon"
  * Mac OS X 10.4 "Tiger"
  * Windows Vista x64 Business Edition

If you've built successfully on another platform, please post to the mailing-list with your results.


## Supported Compilers ##

Wosaic is built and tested using Sun's implementation of Java 5.0 and 6.0, but should be forwards-compatible with future versions of Java as well.  The project has not been tested with non-Sun compilers.  You can find the JDK for your platform at Sun's website here:
  * http://java.sun.com/javase/downloads/index.jsp
To test your java version, issue the following command:
  * `java -version`

If you've built successfully on another compiler, please post to the mailing-list with your results.


## Obtaining Source Code ##

The Wosaic project uses subversion for source control.  To obtain the latest version of the source code, issue the command:
`svn checkout http://wosaic.googlecode.com/svn/trunk/ wosaic`
Note: The trunk version of the source code represents the latest, and possibly unstable, development.  Release versions will be posted at:
  * http://code.google.com/p/wosaic
Or from subversion, in the directory:
`http://wosaic.googlecode.com/svn/tags/current-release`


## Dependencies ##

Because Wosic is written in Java, it depends on a current version of the JRE to build a run.  Wosaic is tested using Sun's implementation of the JRE, which can be obtained at:
  * http://java.sun.com/javase/downloads/index.jsp

Wosaic also depends on the following external libraries, which are distributed with the binary releases, as well as in the ./libs directory of the source code:
  * flickrapi-1.0.jar:		Required by the FlickrService plugin
  * facebook.jar		Required by the Facebook plugin
  * BrowserLauncher2-10rc4.jar	Required by the Facebook plugin for authentication

Make sure you include these libraries in your CLASSPATH when building and running.

The installer that binary distributions are bundled in uses the IzPack libraries.  It is also distributed with the source in the installer/lib directory.


## Section 5: Building with Ant ##

Ant is a tool distributed by Apache for building Java projects.  It's described on the website as:
  * _Apache Ant is a Java-based build tool. In theory, it is kind of like make, without make's wrinkles._

Building Wosaic from Ant is probably the most straightfoward process, and is the most supported.  You can obtain ant from the Apache Ant website here:
  * http://ant.apache.org/bindownload.cgi

Similar to 'Makefile's used with `make`, and uses the 'build.xml' file in the root directory of the source distribute.  To build, simply open a terminal to the source directory, and issue the command:
  * `ant`

By default, Ant will compile, package in an executable jar, and build an installer for the binaries.  For finer-grain control, you can use any of the following Ant targets:

  * `build`:		Only compile source to .class files
  * `package`:		Create an executable JAR file to run
  * `javadoc`:		Create Javadoc documentation of the Wosaic API
  * `installers`:		Create the IzPack installers to distribute Wosaic for each platform
  * `build-installers`:   	Create the IzPack installers to distribute Wosaic for each platform
  * `build-mac-installer`:	Build an installer for Mac
  * `build-win-installer`:	Build an installer for Windows
  * `build-unix-installer`:	Build an installer for Linux
  * `ant-doc`:		Create a html file with details of each ant target

Also note that each of the targets will first build any dependencies that it needs.  To use one of these targets, simply issue the command:
  * `ant {target`}

To clean out your working directory, you can use the command:
  * `ant clean`

And to run the generated Wosaic project, use the command:
  * `ant run`

If you have YourKit profile installed, you can profile with :
  * `ant profile`

## Building from Eclipse ##

Eclipse is a cross-platform IDE for many different programming langues, although it has its roots in Java.  Using Eclipse, you have the benefit of syntax highlighting, a grahical debugger, and even subversion integration.  You can download Eclipse from their website here:
  * http://www.eclipse.org/downloads/

Also, to use subversion from within Eclipse, follow the instructions here:
  * http://subclipse.tigris.org/install.html

Once you have Eclipse and Subclipse setup, use the following instructions to obtain and build Wosaic:

  * Obtain Source code
    * pen Eclipse and go to the "SVN Repository Exploring" perspective. To do so, go to Window, Open Perspective, Other, and select it from the list.
    * In the left pane, click on the icon for "Add SVN Repository."  In the dialog that opens, enter the URL:
      * http://wosaic.googlecode.com/svn/trunk and press Finish.

  * Create Eclipse Project
    * Right click on the new repository and select "Check out".  In the dialog that pops up, simply click "Finish".  This will start the "New Project Wizard".
    * In the dialog, select "Java Project", and click "Next".  Give the project the name "Wosaic", and configure your workspace and JRE.  Under "Project Layout", select "Create separate source and output folders", and then press "Finish".  Press "OK" to the warning dialog that appears.

  * Add Libraries to CLASSPATH
    * In the "Java" perspective, right click on the new "Wosaic" project and select "Properties".
    * In the left pane, select "Java Build Path", and then the "Libraries" tab.
    * Click "Add JARs...", and navigate to Wosaic, libs.  Use shift to select each of the JAR files, and click "OK".  Click "OK" to exit the Properties dialog.

  * (Optional) Enable Java 5 Compatibility
    * If you still get errors, you may be using an incompatible JRE version.  To enable compliance mode, first go back to the "Properties" dialog.
    * From the left pane, select "Java Compiler".  Then, check the box for "Enable proejct specific settings", and change the "Compiler compliance level:" to "5.0".  Select "OK", and allow Eclipse to rebuild the project.

At this point, Eclipse should build the projects automatically without any errors.  If you are having problems, you may need to reconfigure your Eclipse environment.  Double-check the settings inside "Edit, Preferences".


## Building from Other IDEs ##

There are many other useful IDEs for Java development, such as NetBeans.  Although untested, Wosaic should have no trouble building inside them.

However, because other IDEs haven't been tested, we cannot write documentation for them.  If you have build Wosaic using NetBeans or another IDE, please please post to the mailing-list with your results.


## More Information ##

For specific questions or to contact the developers directly, please use the Google Groups mailing-list, which is found at:
  * http://groups.google.com/group/wosaic-forum