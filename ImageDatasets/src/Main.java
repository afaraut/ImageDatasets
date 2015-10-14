import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import Flickr.FlickrSearch;
import Instagram.InstagramSearch;
import Twitter.TwitterSearch;
import Twitter.TwitterSearch4j;


public class Main {
	public static void main(String args[]) throws Exception {
		
	//  Latitude	Longitude
	//	Endroit paumé	45.801744	5.015998
	//	Campus de la Doua	45.782510	4.871976
	//  Place Bellecour	45.757753	4.832005
	//	Time Square NY	40.757982	-73.985549
		
		
	// guillotiere îlot mazagran 45.7520005, 4.8417091
					
//		FlickrSearch flickr = new FlickrSearch("flickr\\",  "ilôt mazagran");
//		FlickrSearch flickr = new FlickrSearch("flickr\\", 40.757982, -73.985549, 0.5);	
//		FlickrSearch flickr = new FlickrSearch("flickr\\",  "Time Square", 40.757982, -73.985549, 0.5);	
		
		// In order to save all the pictures and json at each loop (more safe)
		//
//		flickr.getFlickrRessources();
		
		// In order to know the number of picture(s) for the request
		//
//		flickr.printFlickrNumberResult();
		
		
		//https://twitter.com/wtfbaile/status/652694439584993280/photo/1
		
		//OldTwitterSearch twitter = new OldTwitterSearch("tttttwitter\\", "ilot lyon");
		TwitterSearch4j twitter = new TwitterSearch4j("tttttwitter\\", "\"Time Square\"");
//		TwitterSearch twitter = new TwitterSearch("twitter\\", 40.757982, -73.985549, 1.5);	
//		TwitterSearch twitter = new TwitterSearch("twitter\\",  "some cool pictures from time square, brooklyn bridge and china town", 40.757982, -73.985549, 0.5);	
//		TwitterSearch twitter = new TwitterSearch("twitter\\",  "TimeSquare");//, 45.7520005, 4.8417091, 1.0);
		// In order to save all the pictures and json at each loop (more safe)
		//
		twitter.getTwitterRessources();

		// In order to know the number of picture(s) for the request
		//
//		twitter.printTwitterNumberResult();
		
			
//		InstagramSearch instagram = new InstagramSearch("instagram\\", 40.757982, -73.985549, 500);
//		InstagramSearch instagram = new InstagramSearch("instagram\\", "beaulieusurmer");
				
		// In order to save all the pictures and json at each loop (more safe)
		//
//		instagram.getInstagramRessources();

		// In order to know the number of picture(s) for the request
		//
//		instagram.printInstagramNumberResult();
		
	}
}

