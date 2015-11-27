# ImageDatasets

This tool allows you to retrieve posts from several social networks as **Twitter**, **Instagram** and **Flickr**.
For each of the following social networks, you need to get at least an api_key to communicate with them.

All the following implementation : 
- They save the picture in a given directory;
- They save the json response for the post in a json file in a given directory;
- They save the json response for the post in a mongoDB collection.

## Twitter ##

You must create a Twitter account. Once you have done this, you have to go on this page: https://apps.twitter.com/ or type “apps twitter” on Google and log in.
Next, you have to click on “Create New App”, because you must create an application in order to get an api_key.
After submitting the form, you will have access to your api key.

I made two different tools with the twitter api.


### 'Past' tool

This tool allows you to retrieve all the past tweets on a given subject and/or a given location with a radius.
More information about the REST api right there https://dev.twitter.com/rest/public

####Examples :

	// Latitude, Longitude, Radius (Here "km" is used)
    // The first parameter is the directory in which you store the data
	TwitterSearch twitter = new TwitterSearch("Twitter\\", "Time Square");
	TwitterSearch twitter = new TwitterSearch("Twitter\\", 40.757982, -73.985549, 1.5);	
	TwitterSearch twitter = new TwitterSearch("Twitter\\",  "TimeSquare", 40.757982, -73.985549, 1.0);
	twitter.getTwitterRessources(); // In order to retrieve them

### Stream tool in real time

The tool allows you to retrieve the tweet in real time, when one tweet is posted with the hashtag you're "following" or when a tweet is posted into a specific region which you're following
More information about the api right there https://dev.twitter.com/streaming/overview

####Examples :

    TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\",  "New york Time Square");
    // ---
    // The first parameter is the directory in which you store the data
    // Here you have to specify a polygon, 4 geo points in order to delimate a zone
    // The first one is south west point and the second one is north est point
	TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\","New york Time Square",  4.839563, 45.753759 , 4.847605,  45.758184);
	new Thread(streamConsumer).start(); // In order to start the thread


You can try to communicate with the online console a this link https://dev.twitter.com/rest/tools/console in order to understand how everything works

## Instagram ##

This is a summary of all the information you can find on the following page: https://instagram.com/developer/authentication/. If you need more information, you can refer to this page.

You must have a smartphone in order to create an Instagram account. Once you have done this, you have to go on this page: https://instagram.com/developer/ and log in.
Next, you have to click on “Register your application”, and fill out the form.
That’s all you have to do if you don’t want to have an access token. An Access token allows you to like, comment, friend, unfriend some accounts or pictures.

So, now the introduction is made, my tool allows you to retrieve posts (pictures) from Instagram thanks to :
 Hashtag
 Localisation (with longitude, latitude and distance)

####Examples :

	// The first parameter is the directory in which you store the data
	InstagramSearch instagram = new InstagramSearch("Instagram\\", "newyorkcity");
	InstagramSearch instagram = new InstagramSearch("Instagram\\", 40.757982, -73.985549, 4); // The last parameter is meter. (a radius)
	instagram.getInstagramRessources(); // In order to retrieve them

I also done a real-time retrieval tool for Instagram posts. It's available on the following link : https://github.com/afaraut/Instagram-Realtime-Stream-ImageDataSet. You cal also find it on my Github home page.

## Flickr ##

You must create a Flickr account. Once you have done this, you have to go on this page: https://www.flickr.com/services/api/misc.api_keys.html or type “flickr api key” on Google and log in.
Next, you have to choose between a commercial key or not then fill out the form.
Once you do this, you will have your api key.

More information about the api right there https://www.flickr.com/services/api/flickr.photos.search.html

####Examples :

	// The first parameter is the directory in which you store the data
	FlickrSearch flickr = new FlickrSearch("Flickr\\",  "newyorkcity");
	FlickrSearch flickr = new FlickrSearch("Flickr\\", 40.757982, -73.985549, 0.5);	

	// ---
	// Here you are looking for data with the #hastag and which is located in the area defined by the location
	// The last parameter is kilometer. (a radius)
	FlickrSearch flickr = new FlickrSearch("Flickr\\",  "newyorkcity", 40.757982, -73.985549, 0.5); 
	flickr.getFlickrRessources(); // In order to retrieve them


###You can find a lot of examples in the Main.java file###

*Hope I was clear in all my explanations.
If you want to have more information about everything concerning the projet, feel free to contact me by mail. The mail is available on my Github home page.*
