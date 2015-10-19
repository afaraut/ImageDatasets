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
	
	protected String getTweetURL(JSONObject tweet)throws JSONException{
		if (!tweet.getJSONObject("entities").isNull("media")){
			return ((JSONObject)(tweet.getJSONObject("entities").getJSONArray("media")).opt(0)).optString("expanded_url");	
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
		BufferedImage image = ImageIO.read(url);
		ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + twImage.getDirectory() + twImage.getFileName().concat(".jpg")));
	}
	
	protected void saveJSON(TwitterImage twImage) {	    
		twImage.saveJSON(GlobalesConstantes.REPERTOIRE + twImage.getDirectory() + twImage.getFileName().concat(".json"));
	}
}
