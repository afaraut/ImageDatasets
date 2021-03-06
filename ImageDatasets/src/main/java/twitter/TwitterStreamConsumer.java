package twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.*;

public class TwitterStreamConsumer extends TwitterUtil implements Runnable {
    private static final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json";
    private String repertoire;
	private String text;
	private Double latitude; //bottom_left (south_west)
	private Double longitude; //bottom_left
	
	private Double latitude_tr; //top_right
	private Double longitude_tr; //top_right
	
	
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
		
//	public TwitterStreamConsumer (String repertoire, String text, Double latitude, Double longitude){
//		this.repertoire = repertoire;
//		this.text = text;
//		this.latitude = latitude;
//		this.longitude = longitude;	
//	}

	public TwitterStreamConsumer (String repertoire, String text, Double latitude_bl, Double longitude_bl, Double latitud_tr, Double longitud_tr){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude_bl;
		this.longitude = longitude_bl;	
		latitude_tr = latitud_tr;
		longitude_tr = longitud_tr;
		
	}

	public TwitterStreamConsumer (String repertoire, Double latitude_bl, Double longitude_bl, Double latitud_tr, Double longitud_tr){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude_bl;
		this.longitude = longitude_bl;	
		latitude_tr = latitud_tr;
		longitude_tr = longitud_tr;
		
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
        	String geolocation = new String (latitude + ", " + longitude+ ", " + latitude_tr+ ", " + longitude_tr);
        	//geolocation = geolocation.replace('.', ',');
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
                try {
					JSONObject tweet = new JSONObject(line);
					if (line.length()>3) System.out.println("l:"+line);
					Tweet image = new Tweet(tweet, repertoire, getTweetURL(tweet));
					if (!tweet.getJSONObject("entities").isNull("media")){
						getAllMedia(tweet, image); // Get All Pictures
					}
					else {
						saveJSON(image); // Save json
					}
				} catch (IllegalArgumentException | JSONException e) {
					if (line.length()>3) System.out.println("l:"+line);
				}
            }
        }
        catch (IllegalArgumentException | IOException e){
            e.printStackTrace();
        }
    }
}