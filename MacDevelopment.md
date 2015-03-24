# Introduction #

Documented below are some things to consider when building for a mac platform.


## Look and Feel ##

The look and feel issues are handled by config files in the .app directory for a mac build.  See the Info.plist reference for more details.

## Building An Application ##

The mac .app extension is simply a hierarchy of folders.  Under $(appname).app is a Contents folder.  This contains a MacOS folder with the executable (in this case a stub for executing java binaries).  Jars are stored in $(appname).app/Resources/Java/ and contain all necessary jars for execution (including libraries).

Some configuration files were generated using Apple's Jar Builder.  They exist in the source under trunk/mac/ and define how to launch the application.

## References ##

  * [Apple's Jar Bundler](http://developer.apple.com/documentation/Java/Conceptual/Jar_Bundler/Introduction/chapter_1_section_1.html)
  * [Info.plist Reference](http://developer.apple.com/documentation/Java/Conceptual/JavaPropVMInfoRef/Articles/JavaDictionaryInfo.plistKeys.html#//apple_ref/doc/uid/TP40001969)
  * [Ant App Tutorial](http://today.java.net/pub/a/today/2004/01/05/swing.html)
  * [Look and Feel](http://www.devdaily.com/apple/mac/java-mac-native-look/)