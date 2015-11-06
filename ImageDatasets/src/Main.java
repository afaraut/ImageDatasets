import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import Flickr.FlickrSearch;
import Instagram.InstagramSearch;
import Twitter.TwitterSearch;
import Twitter.TwitterSearch4j;
import Twitter.TwitterStreamConsumer;
import Utils.MongoDB;


public class Main {
	public static void main(String args[]) throws Exception {
		
	//  Latitude	Longitude
	//	Endroit paumé	45.801744	5.015998
	//	Campus de la Doua	45.782510	4.871976
	//  Place Bellecour	45.757753	4.832005
	//	Time Square NY	40.757982	-73.985549		
	//  Guillotiere îlot mazagran 45.7520005, 4.8417091
		
// -------------------------- Flickr		
		
		//FlickrSearch flickr = new FlickrSearch("flickr\\",  "ilôt mazagran");
		//FlickrSearch flickr = new FlickrSearch("flickr\\", 45.7520005, 4.8417091, 0.5);	
		//FlickrSearch flickr = new FlickrSearch("flickr\\",  "Time Square", 40.757982, -73.985549, 0.5);	
		
		//flickr.getFlickrRessources();
		//flickr.printFlickrNumberResult();
		
// -------------------------- Twitter4j	
		
		// Geocode
		// Latitude, Longitude, Radius (Here I use "km")
		
		//TwitterSearch4j twitter = new TwitterSearch4j("Twitter 4j\\",  "Time Square");
		//TwitterSearch4j twitter = new TwitterSearch4j("Twitter 4j\\", 48.8539541, 2.3483307000000195, 0.5);	
		//TwitterSearch4j twitter = new TwitterSearch4j("Twitter 4j\\",  "Time Square", 40.757982, -73.985549, 0.5);	
		
		//twitter.getTwitterRessources();
		
// -------------------------- Twitter Stream	
				
		// Locations
		// A comma-separated list of longitude,latitude pairs specifying a set of bounding boxes to filter Tweets by. Only geolocated Tweets falling within the // requested bounding boxes will be included—unlike the Search API, the user’s location field is not used to filter tweets.

		// Each bounding box should be specified as a pair of longitude and latitude pairs, with the southwest corner of the bounding box coming first. For example:

		// Parameter value							-> Tracks Tweets from…
		// -122.75,36.8,-121.75,37.8				-> San Francisco
		// -74,40,-73,41							-> New York City
		// -122.75,36.8,-121.75,37.8,-74,40,-73,41	-> San Francisco OR New York City
		// https://dev.twitter.com/streaming/overview/request-parameters#locations
		
		//TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\",  "Have a good day");
		//TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\", 48.8539541, 2.3483307000000195);	
		//TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\",  "Time Square", 40.757982, -73.985549);	
		
        //new Thread(streamConsumer).start();
		
// -------------------------- Twitter
		
		// Query operators
		// The query can have operators that modify its behavior, the available operators are:
		
		// Operator						-> Finds tweets …
		// watching now					-> containing both “watching” and “now”. This is the default operator.
		// “happy hour”					-> containing the exact phrase “happy hour”.
		// love OR hate					-> containing either “love” or “hate” (or both).
		// beer -root 					-> containing “beer” but not “root”.
		// #haiku						-> containing the hashtag “haiku”.
		// from:alexiskold				-> sent from person “alexiskold”.
		// to:techcrunch				-> sent to person “techcrunch”.
		// @mashable					-> referencing person “mashable”.
		// superhero since:2015-07-19	-> containing “superhero” and sent since date “2015-07-19” (year-month-day).
		// ftw until:2015-07-19			-> containing “ftw” and sent before the date “2015-07-19”.
		// movie -scary :)				-> containing “movie”, but not “scary”, and with a positive attitude.
		// flight :(					-> containing “flight” and with a negative attitude.
		// traffic ?					-> containing “traffic” and asking a question.
		// hilarious filter:links		-> containing “hilarious” and linking to URL.
		// news source:twitterfeed		-> containing “news” and entered via TwitterFeed
		// https://dev.twitter.com/rest/public/search 
		
		// Geocode
		// Latitude, Longitude, Radius (Here I use "km")
		
		//TwitterSearch twitter = new TwitterSearch("Twitter\\", "Time Square");
		//TwitterSearch twitter = new TwitterSearch("twitter\\", 40.757982, -73.985549, 1.5);	
		//TwitterSearch twitter = new TwitterSearch("twitter\\",  "TimeSquare", 45.7520005, 4.8417091, 1.0);

		//twitter.getTwitterRessources();
		
// -------------------------- Instagram
			
		//InstagramSearch instagram = new InstagramSearch("instagram\\", 40.757982, -73.985549, 500);
		//InstagramSearch instagram = new InstagramSearch("instagram\\", "beaulieusurmer");
				
		//instagram.getInstagramRessources();
		//instagram.printInstagramNumberResult();
	}
}

