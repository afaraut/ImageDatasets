import Flickr.FlickrSearch;
import Twitter.TwitterSearch;


public class Main {
	public static void main(String args[]) throws Exception {
		/*FlickrSearch flickr = new FlickrSearch("flickr\\", "mer", 43.7077201, 7.3343701);
		flickr.getFlickrImages();*/
		TwitterSearch twitter = new TwitterSearch("twitter\\", "mer", 43.50594428, 7.04675669);
		twitter.getTwitterImages();
	}
}
