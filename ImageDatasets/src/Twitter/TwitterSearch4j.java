package Twitter;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONException;

import twitter4j.Query;
import Utils.GlobalesConstantes;

public class TwitterSearch4j {

	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	private Double distance;
	
	public TwitterSearch4j (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
		this.distance = null;
	}
	
	public TwitterSearch4j (String repertoire, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	public TwitterSearch4j (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	public void getTwitterRessources () throws TwitterException, IOException, JSONException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(TwitterConstantes.APIKEY)
                .setOAuthConsumerSecret(TwitterConstantes.APIKEYSECRET)
                .setOAuthAccessToken(TwitterConstantes.TOKEN)
                .setOAuthAccessTokenSecret(TwitterConstantes.TOKENSECRET);
        
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        //twitter.
        double res = 5;
         
        Query query = new Query(this.text);
        //Query query = new Query().geoCode(new GeoLocation(latitude,longitude), res, Query.KILOMETERS.toString()); 
        query.setResultType(Query.RECENT); // get the recent tweet
        query.count(100);
        QueryResult result = twitter.search(query);
        long numberOfTweet = 0;
        do {
        
	        for (Status tweet : result.getTweets()) {
	        	numberOfTweet++;
	        	System.out.println("Number of tweet " + numberOfTweet);
	        	if (tweet.isRetweet()){continue;}        	
	        	try {
	                Status tweetById = twitter.showStatus(tweet.getId());
	                String url= "https://twitter.com/" + tweetById.getUser().getScreenName() 
	                	    + "/status/" + tweetById.getId();
	                
	                List<String> hashtags =  new ArrayList<String>();
	                HashtagEntity[] hashtagsEntities = tweetById.getHashtagEntities();
	                for (HashtagEntity hashtag : hashtagsEntities){
	                	hashtags.add(hashtag.getText());
	                }
	                
	                ExtendedMediaEntity[] medias = tweetById.getExtendedMediaEntities();
	                
	                if (medias.length ==0){
	                	TwitterImage image = new TwitterImage(url, hashtags, null);
	                	saveJSON(image); // Save json
	                }
	                
	                int nombreImage = 0;
	                for (ExtendedMediaEntity m : medias){
	                	System.out.println("Save image " + nombreImage + " du tweet " + numberOfTweet);
						TwitterImage image = new TwitterImage(url, hashtags, m.getMediaURL());
						saveTwitterImage(image); // Download image
						saveJSON(image); // Save json
	                }
	                    
	            } catch (TwitterException e) {
	                System.err.print("Failed to search tweets: " + e.getMessage());
	                return;
	            }
	        }
	    	query = result.nextQuery();
	    	if(query!=null){
	    		result = twitter.search(query);
	    	}
	        
        }while(query!=null);
    }
    
	public void saveTwitterImage(TwitterImage twImage) throws IOException, JSONException {
		
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }

		URL url = new URL(twImage.getPhoto());
		BufferedImage image = ImageIO.read(url);
		ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + twImage.getFileName().concat("jpg")));
	}
	
	public void saveJSON(TwitterImage image) {
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		
		image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
	}
}

