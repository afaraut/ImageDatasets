import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import Flickr.FlickrSearch;
import Instagram.InstagramSearch;
import Twitter.TwitterSearch;
import Twitter.TwitterSearch4j;
import Twitter.TwitterStreamConsumer;


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
		
		//TwitterSearch4j twitter = new TwitterSearch4j("Twitter 4j\\",  "Time Square");
		//TwitterSearch4j twitter = new TwitterSearch4j("Twitter 4j\\", 48.8539541, 2.3483307000000195);	
		//TwitterSearch4j twitter = new TwitterSearch4j("Twitter 4j\\",  "Time Square", 40.757982, -73.985549);	
		
		//twitter.getTwitterRessources();
		
// -------------------------- Twitter Stream	
				
		//TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\",  "Time Square, Night");
		//TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\", 48.8539541, 2.3483307000000195);	
		//TwitterStreamConsumer streamConsumer = new TwitterStreamConsumer("Twitter Stream\\",  "Time Square", 40.757982, -73.985549);	
		
        //new Thread(streamConsumer).start();
		
// -------------------------- Twitter
		
		TwitterSearch twitter = new TwitterSearch("Twitter\\", "Guillotiere");
		//TwitterSearch twitter = new TwitterSearch("twitter\\", 40.757982, -73.985549, 1.5);	
		//TwitterSearch twitter = new TwitterSearch("twitter\\",  "TimeSquare", 45.7520005, 4.8417091, 1.0);

		twitter.getTwitterRessources();
		
// -------------------------- Instagram
			
		//InstagramSearch instagram = new InstagramSearch("instagram\\", 40.757982, -73.985549, 500);
		//InstagramSearch instagram = new InstagramSearch("instagram\\", "beaulieusurmer");
				
		//instagram.getInstagramRessources();
		//instagram.printInstagramNumberResult();
	}
}

