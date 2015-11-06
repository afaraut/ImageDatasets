package Twitter;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Query;

public class TwitterSearch4j extends TwitterUtil {

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
	
	public Query generateTheQuery(){
		Query query = null;
        if (text != null && latitude == null && longitude == null && distance == null ){
        	query = new Query(this.text);
        }
        else if (text == null && latitude != null && longitude != null && distance != null ){
        	query = new Query().geoCode(new GeoLocation(latitude,longitude), distance, Query.KILOMETERS.toString());
        }
        else if (text != null && latitude != null && longitude != null && distance != null ){
        	query = new Query(text).geoCode(new GeoLocation(latitude,longitude), distance, Query.KILOMETERS.toString());
        }
        query.count(100);        
        return query;
	}
	
	/*protected JSONObject getUserInformation (Status tweet)throws JSONException{

		JSONObject obj = new JSONObject();
		obj.put("id", "" + tweet.getUser().getId());
		obj.put("name", tweet.getUser().getName());
		obj.put("screen_name", tweet.getUser().getScreenName());
		obj.put("location", tweet.getUser().getLocation());
		obj.put("url", tweet.getUser().getURL());
		obj.put("description", tweet.getUser().getDescription());
		obj.put("created_at", "" + tweet.getUser().getCreatedAt());
		obj.put("lang", tweet.getUser().getLang());
		obj.put("followers_count", tweet.getUser().getFollowersCount());
		obj.put("friends_count", tweet.getUser().getFriendsCount());
		obj.put("statuses_count", tweet.getUser().getStatusesCount());        

		return obj;	
	}*/
	
	public void getTwitterRessources () throws TwitterException, IOException, JSONException {
	    ConfigurationBuilder cb = new ConfigurationBuilder().setDebugEnabled(true)
	            .setOAuthConsumerKey(TwitterConstantes.APIKEY)
	            .setOAuthConsumerSecret(TwitterConstantes.APIKEYSECRET)
	            .setOAuthAccessToken(TwitterConstantes.TOKEN)
	            .setOAuthAccessTokenSecret(TwitterConstantes.TOKENSECRET);
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        Query query = generateTheQuery();
        if (query == null){
        	System.out.println("ERROR in the query");
        	return;
        }
         
        query.setResultType(Query.RECENT); // get the recent tweet
       
        QueryResult result = twitter.search(query);
        long currentTweet = 0;
        do {
        	long numberOfTweet = result.getTweets().size();
	        for (Status tweet : result.getTweets()) {
	        	currentTweet++;
	        	System.out.println("Number of tweet " + currentTweet);
	        	if (tweet.isRetweet()){continue;}        	
	        	try {
	                Status tweetById = twitter.showStatus(tweet.getId());
	                String url= "https://twitter.com/" + tweetById.getUser().getScreenName() 
	                	    + "/status/" + tweetById.getId();
	                /*
	                List<String> hashtags =  new ArrayList<String>();
	                HashtagEntity[] hashtagsEntities = tweetById.getHashtagEntities();
	                for (HashtagEntity hashtag : hashtagsEntities){
	                	hashtags.add(hashtag.getText());
	                }
	                */
	                JSONObject test = new JSONObject(TwitterObjectFactory.getRawJSON(tweet));
	                //String json = TwitterObjectFactory.jsonStoreEnabled(tweet);
	                ExtendedMediaEntity[] medias = tweetById.getExtendedMediaEntities();
	                //Tweet image = new Tweet(getUserInformation(tweetById), repertoire, url, tweetById.getText(), "" + tweetById.getId(), hashtags);
	                Tweet image = new Tweet(test, repertoire, url);
	                if (medias.length == 0){
	                	saveJSON(image); // Save json
	                }
	                else {
		                int nombreImage = 0;
		                for (ExtendedMediaEntity m : medias){
		                	System.out.println("Save image " + nombreImage++ + " du tweet " + currentTweet + "/" + numberOfTweet);
							image.addPhoto(m.getMediaURL());
		                }
						saveTwitterImage(image); // Download images
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
}

