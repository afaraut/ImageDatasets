import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import Flickr.FlickrSearch;
import Flickr_yql.FlickrYqlSearch;
import Instagram.InstagramSearch;
import Instagram_with_auth.InstagramWASearch;
import Twitter.TwitterSearch;


public class Main {
	public static void main(String args[]) throws Exception {
		
		// 45.801744, 5.015998 paumé
		// 40.757982, -73.985549 Time square
		
		FlickrSearch flickr = new FlickrSearch("flickr\\", "Time Square",  40.757982, -73.985549, 0.5);
		
		// In order to save all the pictures and json at each loop (more safe)
		//
		flickr.getFlickrRessourcesWithSave();
		
		// In order to know the number of picture(s) for the request
		//
		//flickr.printFlickrNumberResult();
		
		// In order to save all the pictures and json at the end (deprecated)
		//
		//flickr.saveJSONList(flickr.saveFlickrImages());
		
		
		/*FlickrYqlSearch flickryql = new FlickrYqlSearch("flickrYql\\", "light nyc", 40.757982, -73.985549, 0.5);
		flickryql.saveJSON(flickryql.getFlickrImages());*/
		
		/*TwitterSearch twitter = new TwitterSearch("twitter\\", "mer", 45.801744, 5.015998);
		twitter.saveJSON(twitter.getTwitterImages());
		
		InstagramWASearch instagramwa = new InstagramWASearch("instagram_wa\\", 45.801744, 5.015998, 500);
		//InstagramSearch instagramwa = new InstagramSearch("instagram\\", "mer");
		instagramwa.saveJSON(instagramwa.getInstagramWAImages());*/
		
		//InstagramSearch instagram = new InstagramSearch("instagram\\", 43.50594428, 7.04675669, 500);
		//InstagramSearch instagram = new InstagramSearch("instagram\\", "mer");
		//instagram.saveJSON(instagram.getInstagramImages());
	}
}
