# Introduction #

Providing basic functionality was our first big milestone, but we have many ideas to improve the functionality of Wosaic.  This includes adding features, as well as optimizing the backend.  Below will represent an exhaustive we are considering-- some of them will be implemented, some won't.


## Parallelization ##

We are considering using threading to parallelize the querying and processing of images.  We have seen that querying Flickr for images is the bottleneck for processing, but there is also a hit for processing the images.  We'd like to use a producer-consumer scheme with one of them following models:

  * Send a batch of n small queries to Flickr all at once asynchronously.  As the results arrive, send them to the JAI processor for analyzing.  Once in analyzes a photo, set it to all regions in the mosaic for which it is the _best_ match, replacing any other current photos.

  * Once again, query Flickr for small sets of images to return results quickly.  As we retrieve results, send them to the JAI processor for analyzing.  After all photos have been analyzed, the JAI processor will have a library of scored Pixel objects.  Iterate over the mosaic, and select the best Pixel for each region.


## Database Use ##

As querying Flickr seems to be the greatest bottleneck, we think it might be beneficial to store results in our own database which we could query.  Basically, we could cache the results of any Flickr query, storing a unique photo ID, the search query it was found by, and the average color.  We could have a separate table that identifies how many "pages" of results we have for each search query, so we know how to query Flickr later.  An implementation like this would have the following pros and cons:

  * Pros
    * We would have the benefit that every photo found from a database query would already be processed.
    * If we host the database on the same subnet as the applet, the queries should be much faster.
    * The more images we process and the more mosaics we create, the better our database will get.
  * Cons
    * Our database will be very small to begin with, so we won't see many hits.  This will just cause more overhead because we will need to then query Flickr, and save all of our results in our database.
    * We are basically mirroring Flickr's database with information that is relevant to us.  It will be a problem if information in Flickr's database changes.  For example, if the URL for a picture changes, or the picture is removed entirely.
    * An image could be returned with many different search strings on Flickr.  It will be hard to represent all of these strings in our own database.

Assuming we use a database, our algorithm could go something like this:
  * Query our own internal database for images matching a search string.
  * If we don't find enough images, query Flickr for the same string.
  * For any images that we get from Flickr that aren't currently in the database, process them and add them to the database.  Repeat until we have enough images for the Mosaic.

## Adding Weights to Source Images ##

We want to take into account other factors when determining where to place a source image.  The list of factors and their weights are:

  * Average color: +x points
  * Photo was already used before: -y points

The idea behind this is that we want to stray away from using the same image many times in a small area.  Having many different images next to each other is part of what makes the mosaic interesting.

## Custom Sources and Cleaning up Existing Sources ##

The following is taken from an e-mail conversation between Scott and I (Carl):  I really like the idea of allowing custom, pluggable services.  I thought briefly about the steps involved (currently) in adding a new source, and it seems like we could abstract that away enough to make it easy for someone to write a class that implements an interface we provide and have it be used as a source.  It seems that in order to run along side the rest of the program, all a new source has to do is have some method of adding to the shared buffer.  The part that isn't as obvious to me is where we can let the user instantiate their new source.

I suppose it wouldn't be a big deal to have a user/developer implement a source via our interface, and then add a couple of lines (to instantiate their class, and maybe add it to a vector of sources) to something that amounts to a sources class, which we can then reference in all the appropriate places.  I'm thinking something along these lines:

```
class Sources {
    public Sources() {
        Facebook fb = new Facebook();
        CustomSource cs = new CustomSource():
        ...
        sources.add(fb);
        sources.add(cs);
        ...
    }
}
```

In fact, I was thinking that even with just more sources being added by us, we should have a cleaner way of adding them into our code.  Now my mind is really racing with ideas.  The other hurdle I saw in adding a new source was that I felt we should add a corresponding option to turn it on or off in the WosaicUI.  If we had this Sources object, we could replace checkboxes in the options panel with a list of all of our sources that you can select to add and remove.  We could use the contents of the sources object to populate this list dynamically, and the action listener could be something as simple as adding or removing from an ArrayList or Vector.

## UpdateUI Thread ##

I was thinking about updating the UI, and how we can make it smoother.  ImageIcons aside for now, I think one of the reasons this degrades with higher resolutions is that there are a large number of events being caught all in one thread (I believe it's the AWT Event Queue).  In the listener that we implemented, we do some heavy work, like creating an ImageIcon (BufferedImage) from a Pixel object.  This work times potentially hundreds or thousands of updates on any given pass of the JAIProcessor (soon to be renamed Processor) is something I think is hurting our performance.  I see three things we can do to help alleviate this.

First one is a simple change, which I think I will implement after writing this e-mail.  I'll keep some extra state in the Pixel object that is the BufferedImage representation of a Pixel.  I'll keep it as a null reference until getImageIcon is called for the first time, at which point I will generate it and return that same reference on all subsequent calls.  This should speed things up significantly, especially if you consider that the first few updates to the mosaic are going to be a copy of the same image in every location.

The second idea I had was to put off the bulk of the work into a new thread... but... the more I think of it the more it just seems to defer the problem and create unnecessary overhead.  However, I do think it would be beneficial to have an additional thread (call it UpdateUI) that runs alongside the WosaicUI, and takes care of updating things like status bars.  It seems that we could defer the work of painting the mosaic into this thread... though the details aren't really clear to me at the moment.

Lastly, we can change the frequency with which we emit the updated events.  It may be more beneficial to simply send a list of updated coordinates at the end of the 'updateMosaic' function call... rather than sending an event for each tile that is updated.  On a default run this means the user would see an update at most 500 times (500 being the number of images we retrieve), and probably at least enough times to show good progress.  This is a relatively easy parameter to change and we can play around with it and see how it performs.