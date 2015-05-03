import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import Flickr.FlickrSearch;
import Instagram.InstagramSearch;
import Instagram_with_auth.InstagramWASearch;
import Twitter.TwitterSearch;


public class Main {
	public static void main(String args[]) throws Exception {
		FlickrSearch flickr = new FlickrSearch("flickr\\", "mer", 43.7077201, 7.3343701);
		flickr.saveJSON(flickr.getFlickrImages());
		
		TwitterSearch twitter = new TwitterSearch("twitter\\", "mer", 43.50594428, 7.04675669);
		twitter.saveJSON(twitter.getTwitterImages());
		
		InstagramWASearch instagramwa = new InstagramWASearch("instagram_wa\\", 43.50594428, 7.04675669);
		//InstagramSearch instagramwa = new InstagramSearch("instagram\\", "mer");
		instagramwa.saveJSON(instagramwa.getInstagramWAImages());
		
		//InstagramSearch instagram = new InstagramSearch("instagram\\", 43.50594428, 7.04675669);
		InstagramSearch instagram = new InstagramSearch("instagram\\", "mer");
		instagram.saveJSON(instagram.getInstagramImages());
	}
}
