# General Notes #

## Agenda ##
  1. EOH
    1. What still needs to be done
    1. Forms/Meetings
    1. Judging Categories
    1. Shirts are $4
  1. File Systems Plugin
    1. Missing imports?
    1. Status
  1. [Issue 14](https://code.google.com/p/wosaic/issues/detail?id=14)


We spent some time talking about EOH, and decided to be judged in the EOH Theme: Non-Technical category.

We mentioned changing the sources interface to maintain a JDialogue instead of a JFrame for a source's options.  A JDialogue can be extended and customized.  It inherits from the Container class, which means existing panels of options can be added to the dialogue.  This has the added benefit of stealing focus from the rest of the application, and blocking until the dialogue has returned.

It was also mentioned that certain parameters for a mosaic should be collected during saving, instead of before creating a mosaic.  The output dimensions for a mosaic are a prime example of this.

We set aside a weekly time to code, in order to make sure that the project makes sufficient progress by EOH (March 7th and 8th).  This meets wednesdays at 12:30 in the lower level of SC at the tables by the stairs.

As far as [Issue 14](https://code.google.com/p/wosaic/issues/detail?id=14) is concerned, we decided that it only crops up when images are of a very large size.  For now we will restrict this size, and/or catch the OutOfMemoryError and exit cleanly.

# Goals #

  * Set the output saving dimensions before saving, instead of before creating a mosaic.
  * Cleanly handle OutOfMemoryError

## Carl ##
  * Look into creating a Mac application
  * Find out how to add an accessory to open/save
  * ~~Continue~~ Stop debugging [Issue 14](https://code.google.com/p/wosaic/issues/detail?id=14)

## Scott ##
  * Fix (or remove) the javadoc build in ant
  * Finish up local file plugin
  * Look into using a JDialog instead of JFrame for sources interface
  * See if we can get any help with logo design