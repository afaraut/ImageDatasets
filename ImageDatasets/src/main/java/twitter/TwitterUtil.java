package twitter;

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

import utils.GlobalesConstantes;
import utils.Toolbox;

public abstract class TwitterUtil {
	
	protected static final OAuthService SERVICE = new ServiceBuilder().provider(TwitterApi.class).apiKey(TwitterConstantes.APIKEY).apiSecret(TwitterConstantes.APIKEYSECRET).build();
	protected static final Token ACCESSTOKEN = new Token(TwitterConstantes.TOKEN, TwitterConstantes.TOKENSECRET);
	
	protected JSONObject makeGetRequestJSONObject(String getRequete) {
		OAuthRequest request = new OAuthRequest(Verb.GET, getRequete);
		SERVICE.signRequest(ACCESSTOKEN, request);

		try {
			Response response = request.send();
			return new JSONObject(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected JSONArray makeGetRequestJSONArray(String getRequete) {
		OAuthRequest request = new OAuthRequest(Verb.GET, getRequete);
		SERVICE.signRequest(ACCESSTOKEN, request);

		try {
			Response response = request.send();
			return new JSONArray(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*protected JSONObject getTweet (JSONObject tweet)throws JSONException{
		JSONObject obj = new JSONObject();
		if (!tweet.isNull("id_str")){
			obj.put("id", tweet.optString("id_str"));
		}
		if (!tweet.isNull("created_at")){
			obj.put("created_at", tweet.optString("created_at"));
		}
		if (!tweet.isNull("text")){
			obj.put("text", tweet.optString("text"));
		}
		if (!tweet.isNull("geo")){
			obj.put("geo", tweet.optString("geo"));
		}
		if (!tweet.isNull("coordinates")){
			obj.put("coordinates", tweet.optString("coordinates"));
		}
		if (!tweet.isNull("place")){
			obj.put("place", tweet.optString("place"));
		}
		if (!tweet.isNull("retweet_count")){
			obj.put("retweet_count", tweet.optString("retweet_count"));
		}
		if (!tweet.isNull("favorite_count")){
			obj.put("favorite_count", tweet.optString("favorite_count"));
		}
		return obj;
		
	}*/
	
	/*protected JSONObject getUserInformation (JSONObject tweet)throws JSONException{

		JSONObject obj = new JSONObject();
		if (!tweet.isNull("user")){
			if (!tweet.getJSONObject("user").isNull("id_str")){
				obj.put("id", tweet.getJSONObject("user").optString("id_str"));
			}
			if (!tweet.getJSONObject("user").isNull("name")){
				obj.put("name", tweet.getJSONObject("user").optString("name"));
			}
			if (!tweet.getJSONObject("user").isNull("screen_name")){
				obj.put("screen_name", tweet.getJSONObject("user").optString("screen_name"));
			}
			if (!tweet.getJSONObject("user").isNull("location")){
				obj.put("location", tweet.getJSONObject("user").optString("location"));
			}
			if (!tweet.getJSONObject("user").isNull("url")){
				obj.put("url", tweet.getJSONObject("user").optString("url"));
			}
			if (!tweet.getJSONObject("user").isNull("description")){
				obj.put("description", tweet.getJSONObject("user").optString("description"));
			}
			if (!tweet.getJSONObject("user").isNull("created_at")){
				obj.put("created_at", tweet.getJSONObject("user").optString("created_at"));
			}
			if (!tweet.getJSONObject("user").isNull("lang")){
				obj.put("lang", tweet.getJSONObject("user").optString("lang"));
			}
			if (!tweet.getJSONObject("user").isNull("followers_count")){
				obj.put("followers_count", tweet.getJSONObject("user").optInt("followers_count"));
			}
			if (!tweet.getJSONObject("user").isNull("friends_count")){
				obj.put("friends_count", tweet.getJSONObject("user").optInt("friends_count"));
			}
			if (!tweet.getJSONObject("user").isNull("statuses_count")){
				obj.put("statuses_count", tweet.getJSONObject("user").optInt("statuses_count"));
			}
		}
		return obj;	
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
	*/
	
	protected String getScreenName(JSONObject tweet) {
		try {
			if (!tweet.getJSONObject("user").isNull("screen_name")){
				return tweet.getJSONObject("user").optString("screen_name");	
			}
		} catch (IllegalArgumentException | JSONException e) {
			e.printStackTrace();
		}
		return new String();
	}
	
	protected String getTweetURL(JSONObject tweet) {
		return "https://twitter.com/" + getScreenName(tweet) + "/status/" + getTweetID(tweet);
	}
	
	protected String getTweetID(JSONObject tweet) {
		if (!tweet.isNull("id_str")){
			return tweet.optString("id_str");
		}
		return new String();
	}
	
	/*
	protected String getTweetText(JSONObject tweet)throws JSONException{
		if (!tweet.isNull("text")){
			return tweet.optString("text");
		}
		return new String();
	}*/
	
	protected void getAllMedia(JSONObject tweet, Tweet image){
		System.out.println("Get the pictures from the tweet...");
		if (!tweet.isNull("extended_entities")) {
			try {
				JSONArray medias = tweet.getJSONObject("extended_entities").getJSONArray("media");
				int nombreDeMedia = medias.length();
				for (int i=0; i<nombreDeMedia; i++){
					JSONObject jsonPhoto = (JSONObject) medias.opt(i);					
					image.addPhoto(jsonPhoto.optString("media_url"));
				}
				saveTwitterImage(image); // Download images
				saveJSON(image); // Save json
			} catch (IllegalArgumentException | JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void getAllMedia(HashMap<String, Tweet> hashMapTweets, String idList){	
		
		String requeteMedia = "https://api.twitter.com/1.1/statuses/lookup.json?id=" + idList;	
		JSONArray results = makeGetRequestJSONArray(requeteMedia);
		int numberOfResult = results.length();
		
		for (int i = 0; i < numberOfResult; i++) {
			JSONObject result = (JSONObject) results.opt(i);
			String tweetID = result.optString("id_str");
			Tweet image = hashMapTweets.get(tweetID);
			if(image == null) {continue;} // Just to be sure
			image.addIntoJSON("requet_2_extended_entities", result);
			getAllMedia(result, image);
		}
	}
		
	protected void saveTwitterImage(Tweet twImage) {
		ArrayList<String> photos = twImage.getPhotos();
		for (int i=0; i < photos.size(); i++){
			try {
				URL url = new URL(photos.get(i));
				String extenstion = Toolbox.getExtensionFromURL(url.toString());
				
				BufferedImage image = ImageIO.read(url);
				String tmp = twImage.getFileName() + "_" + i + "." + extenstion;
				ImageIO.write(image, extenstion, new File(GlobalesConstantes.REPERTOIRE + twImage.getDirectory() + tmp));
				
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void saveJSON(Tweet twImage) {	    
		twImage.saveJSON_FILE(GlobalesConstantes.REPERTOIRE + twImage.getDirectory() + twImage.getFileName() + ".json");
		twImage.saveJSON_DB();
	}
}
