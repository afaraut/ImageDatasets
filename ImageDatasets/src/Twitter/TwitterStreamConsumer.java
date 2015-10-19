package Twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.*;

public class TwitterStreamConsumer extends TwitterUtil implements Runnable {
    private static final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json";
    private String repertoire;
    private String latestTweet;
    private long tweetCount;
	private String text;
	private Double latitude;
	private Double longitude;

	public TwitterStreamConsumer (String repertoire, String text){
		this.repertoire = repertoire;				
		this.text = text;
		this.latitude = null;
		this.longitude = null;
	}
		
	public TwitterStreamConsumer (String repertoire, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;	
	}
		
	public TwitterStreamConsumer (String repertoire, String text, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;	
	}

    public String getLatestTweet(){
        return latestTweet;
    }

    public long getTweetCount(){
        return tweetCount;
    }
    	    
	public BufferedReader makePostRequest(){
				
        // Let's generate the request
        System.out.println("Connecting to Twitter Public Stream");
        OAuthRequest request = new OAuthRequest(Verb.POST, STREAM_URI);
        request.addHeader("version", "HTTP/1.1");
        request.addHeader("host", "stream.twitter.com");
        request.setConnectionKeepAlive(true);
        request.addHeader("user-agent", "Twitter Stream Reader");
        if (text != null) {
            request.addBodyParameter("track", text); // Set keywords you'd like to track here
        }
        if (latitude != null && longitude != null ) {
        	String geolocation = new String (latitude + ", " + longitude);
        	geolocation = geolocation.replace('.', ',');
        	System.out.println(geolocation);
            request.addBodyParameter("locations", geolocation); // Set gelocation you'd like to track here
        }
            
        SERVICE.signRequest(ACCESSTOKEN, request);

		try {
			Response response = request.send();
            // Create a reader to read Twitter's stream
            return new BufferedReader(new InputStreamReader(response.getStream()));
            
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
    public void run(){
        try{
            BufferedReader reader = makePostRequest();
            if (reader == null) {return;}
            
            String line;
            while ((line = reader.readLine()) != null) {
                latestTweet = line;
                try {
					JSONObject tweet = new JSONObject(line);
					String tweetID = tweet.optString("id_str");
					TwitterImage image = new TwitterImage(repertoire, getTweetURL(tweet), tweetID, getAllHashTag(tweet));
					if (!tweet.getJSONObject("entities").isNull("media")){
						getAllMedia(tweet.optString("id_str"), image); // Get All Pictures
					}
					else {
						saveJSON(image); // Save json
					}
	                tweetCount++;
				} catch (JSONException e) {
					System.out.println(line);
				}
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}