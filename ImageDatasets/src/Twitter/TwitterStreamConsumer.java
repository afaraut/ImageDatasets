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
    private String requete;
    private String latestTweet;
    private int tweetCount;

	public TwitterStreamConsumer (String repertoire, String requete){
		this.repertoire = repertoire;				
		if (requete != null) {
			this.requete = requete;
		}
		System.out.println(requete);
	}
    
    public String getLatestTweet(){
        return latestTweet;
    }

    public int getTweetCount(){
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
        request.addBodyParameter("track", requete); // Set keywords you'd like to track here
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
					TwitterImage image = new TwitterImage(repertoire, getTweetURL(tweet), getAllHashTag(tweet));
					if (!tweet.getJSONObject("entities").isNull("media")){
						getAllMedia(tweet, image); // Get All Pictures
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