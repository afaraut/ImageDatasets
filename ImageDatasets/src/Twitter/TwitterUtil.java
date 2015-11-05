package Twitter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import Utils.GlobalesConstantes;

public abstract class TwitterUtil {
	
	protected static final OAuthService SERVICE = new ServiceBuilder().provider(TwitterApi.class).apiKey(TwitterConstantes.APIKEY).apiSecret(TwitterConstantes.APIKEYSECRET).build();
	protected static final Token ACCESSTOKEN = new Token(TwitterConstantes.TOKEN, TwitterConstantes.TOKENSECRET);
	
	protected JSONObject makeGetRequestJSONObject(String getRequete){
		//System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, getRequete);
		SERVICE.signRequest(ACCESSTOKEN, request);

		//System.out.println("Results...");
		try {
			Response response = request.send();
			return new JSONObject(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	protected JSONArray makeGetRequestJSONArray(String getRequete){
		//System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, getRequete);
		SERVICE.signRequest(ACCESSTOKEN, request);

		//System.out.println("Results...");
		try {
			Response response = request.send();
			return new JSONArray(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected TwitterUser getUserInformation (JSONObject tweet)throws JSONException{
		String id = new String(), name = new String(), screen_name = new String(), location = new String(), url = new String(), description = new String(), created_at = new String(), lang = new String();
		int followers_count = 0, friends_count = 0, statuses_count = 0;
		
		if (!tweet.isNull("user")){
			if (!tweet.getJSONObject("user").isNull("id_str")){
				id = tweet.getJSONObject("user").optString("id_str");
			}
			if (!tweet.getJSONObject("user").isNull("name")){
				name = tweet.getJSONObject("user").optString("name");
			}
			if (!tweet.getJSONObject("user").isNull("screen_name")){
				screen_name = tweet.getJSONObject("user").optString("screen_name");
			}
			if (!tweet.getJSONObject("user").isNull("location")){
				location = tweet.getJSONObject("user").optString("location");
			}
			if (!tweet.getJSONObject("user").isNull("url")){
				url = tweet.getJSONObject("user").optString("url");
			}
			if (!tweet.getJSONObject("user").isNull("description")){
				description = tweet.getJSONObject("user").optString("description");
			}
			if (!tweet.getJSONObject("user").isNull("created_at")){
				created_at = tweet.getJSONObject("user").optString("created_at");
			}
			if (!tweet.getJSONObject("user").isNull("lang")){
				lang = tweet.getJSONObject("user").optString("lang");
			}
			if (!tweet.getJSONObject("user").isNull("followers_count")){
				followers_count = tweet.getJSONObject("user").optInt("followers_count");
			}
			if (!tweet.getJSONObject("user").isNull("friends_count")){
				friends_count = tweet.getJSONObject("user").optInt("friends_count");
			}
			if (!tweet.getJSONObject("user").isNull("statuses_count")){
				statuses_count = tweet.getJSONObject("user").optInt("statuses_count");
			}
		}
		return new TwitterUser(id, name, screen_name, location, url, description, followers_count, friends_count, statuses_count, created_at, lang);	
	}
	
	protected ArrayList<String> getAllHashTag(JSONObject tweet) throws JSONException{
		ArrayList<String> hashtags = new ArrayList<String>();	
		JSONArray json_hashtags = tweet.getJSONObject("entities").getJSONArray("hashtags");			
		int nombreDeHashTag = json_hashtags.length();
		
		// --- Get all the hashtag
		//
		for (int k=0; k<nombreDeHashTag; k++){
			hashtags.add(((JSONObject) json_hashtags.opt(k)).optString("text"));
		}
		return hashtags;
	}
		
	protected String getScreenName(JSONObject tweet)throws JSONException{
		if (!tweet.getJSONObject("user").isNull("screen_name")){
			return tweet.getJSONObject("user").optString("screen_name");	
		}
		return new String();
	}
	
	protected String getTweetURL(JSONObject tweet)throws JSONException{
		return "https://twitter.com/" + getScreenName(tweet) + "/status/" + getTweetID(tweet);
	}
	
	protected String getTweetID(JSONObject tweet)throws JSONException{
		if (!tweet.isNull("id_str")){
			return tweet.optString("id_str");
		}
		return new String();
	}
	
	protected String getTweetText(JSONObject tweet)throws JSONException{
		if (!tweet.isNull("text")){
			return tweet.optString("text");
		}
		return new String();
	}
	
	protected void getAllMedia(String tweetID, TwitterImage image) throws JSONException, IOException{

		String requeteMedia = "https://api.twitter.com/1.1/statuses/show.json?id=" + tweetID ;
		JSONObject result = makeGetRequestJSONObject(requeteMedia);
		System.out.println("Get the pictures from the tweet...");
		if (!result.isNull("extended_entities")) {
			JSONArray medias = result.getJSONObject("extended_entities").getJSONArray("media");
			int nombreDeMedia = medias.length();
			for (int i=0; i<nombreDeMedia; i++){
				JSONObject jsonPhoto = (JSONObject) medias.opt(i);					
				image.setPhoto(jsonPhoto.optString("media_url"));
				saveTwitterImage(image); // Download image
				saveJSON(image); // Save json
			}
		}
	}
	
	protected void getAllMedia(HashMap<String, TwitterImage> hashMapTweets, String idList) throws JSONException, IOException{	
		
		String requeteMedia = "https://api.twitter.com/1.1/statuses/lookup.json?id=" + idList;	
		JSONArray results = makeGetRequestJSONArray(requeteMedia);
		int numberOfResult = results.length();
		
		for (int i = 0; i < numberOfResult; i++) {
			JSONObject result = (JSONObject) results.opt(i);
			String tweetID = result.optString("id_str");
			TwitterImage image = hashMapTweets.get(tweetID);
			if(image == null) {continue;} // Just to be sure
			System.out.println("Get the pictures from the tweet...");
			if (!result.isNull("extended_entities")) {
				JSONArray medias = result.getJSONObject("extended_entities").getJSONArray("media");
				int nombreDeMedia = medias.length();
				for (int j=0; j<nombreDeMedia; j++){
					JSONObject jsonPhoto = (JSONObject) medias.opt(j);					
					image.setPhoto(jsonPhoto.optString("media_url"));
					saveTwitterImage(image); // Download image
					saveJSON(image); // Save json
				}
			}
		}
	}
	
	protected void saveTwitterImage(TwitterImage twImage) throws IOException, JSONException {
		URL url = new URL(twImage.getPhoto());
		System.out.println(url);
		BufferedImage image = ImageIO.read(url);
		ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + twImage.getDirectory() + twImage.getFileName().concat(".jpg")));
	}
	
	protected void saveJSON(TwitterImage twImage) {	    
		twImage.saveJSON(GlobalesConstantes.REPERTOIRE + twImage.getDirectory() + twImage.getFileName().concat(".json"));
	}
}
