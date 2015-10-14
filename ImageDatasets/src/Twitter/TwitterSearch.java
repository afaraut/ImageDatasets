package Twitter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

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

public class TwitterSearch {

	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	private Double distance;
	private String requete;
	public static final String OPTIONS =  "filter=images&count=100&include_entities=1&exclude=retweets";
	private static final OAuthService SERVICE = new ServiceBuilder().provider(TwitterApi.class).apiKey(TwitterConstantes.APIKEY).apiSecret(TwitterConstantes.APIKEYSECRET).build();
	private static final Token ACCESSTOKEN = new Token(TwitterConstantes.TOKEN, TwitterConstantes.TOKENSECRET);
	
	public TwitterSearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
		this.distance = null;
				
		try {
			if (text != null) {
				requete = new URI("https", "api.twitter.com", "/1.1/search/tweets.json", OPTIONS + "&q=" + text, null).toASCIIString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(requete);
	}
	
	public TwitterSearch (String repertoire, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
				
		try {
			if (latitude != null && longitude != null && distance != null) {
				requete = new URI("https", "api.twitter.com", "/1.1/search/tweets.json", OPTIONS + "&geocode=" + latitude + "," + longitude + "," + distance + "km", null).toASCIIString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(requete);
	}
		
	public TwitterSearch (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
		
		try {
			if (latitude != null && longitude != null && distance != null && text != null) {
				requete = new URI("https", "api.twitter.com", "/1.1/search/tweets.json", OPTIONS + "&geocode=" + latitude + "," + longitude + "," + distance + "km"+ "&q=" + text,  null).toASCIIString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(requete);
	}
	
	
	public JSONObject makeGetRequest(String getRequete){
		//System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, getRequete);
		SERVICE.signRequest(ACCESSTOKEN, request);
		Response response = request.send();
		System.out.println(response.getBody());
		//System.out.println("Results...");
		try {
			return new JSONObject(response.getBody());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getAllHashTag(JSONObject tweet) throws JSONException{
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
	
	public void getAllMedia(JSONObject tweet, ArrayList<String> hashtags, int currentTweet, int nombreDeTweet, int nombreDePage) throws JSONException, IOException{
		if (!tweet.getJSONObject("entities").isNull("media")){
			String expanded_url = ((JSONObject)(tweet.getJSONObject("entities").getJSONArray("media")).opt(0)).optString("expanded_url");				
			
			// --- Second request in order to get all the pictures from the tweet
			//
			String requeteMedia = "https://api.twitter.com/1.1/statuses/show.json?id=" + tweet.optString("id_str") ;
			JSONObject result = makeGetRequest(requeteMedia);
			System.out.println("Get the pictures from the tweet... [" + new Integer(currentTweet+1) + "/" + nombreDeTweet + "] page " + nombreDePage);
			if (!result.isNull("extended_entities")) {
				JSONArray medias = result.getJSONObject("extended_entities").getJSONArray("media");
				int nombreDeMedia = medias.length();
				for (int j=0; j<nombreDeMedia; j++){
					JSONObject jsonPhoto = (JSONObject) medias.opt(j);					
					TwitterImage image = new TwitterImage(expanded_url, hashtags, jsonPhoto.optString("media_url"));
					saveTwitterImage(image); // Download image
					saveJSON(image); // Save json
				}
			}
		}
		else {
			System.out.println("No media");
		}
	}
	
	/**
	 * Try to get all the media from each page
	 * @throws JSONException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void getTwitterRessources () throws JSONException, URISyntaxException, IOException {
				
		JSONObject result = makeGetRequest(requete);
		JSONArray tweets = result.getJSONArray("statuses");		
		String next_results = null;
		if (!result.isNull("search_metadata") && !result.getJSONObject("search_metadata").isNull("next_results")) {
			next_results = result.getJSONObject("search_metadata").optString("next_results");
		}
		int nombreDePage = 0;
		int nombreDeTweet = tweets.length();
				
		//if (result.isNull("errors")) {
			do {
				for (int i = 0; i < nombreDeTweet; i++) {
					if (!result.isNull("errors")) { // CHECK THE ERROR CODE
						System.out.println("Too many requests ! You have to wait 15 minutes");
						return;
					}
					
					JSONObject tweet = (JSONObject) tweets.opt(i);
					ArrayList<String> hashtags = getAllHashTag(tweet); // Get All HashTags
					getAllMedia(tweet, hashtags, i, nombreDeTweet, nombreDePage); // Get All Pictures
					
				}
				nombreDePage++;	
				if (next_results != null) { // There is still tweets
					requete = "https://api.twitter.com/1.1/search/tweets.json" + next_results +"&count=100&include_entities=1";
					result = makeGetRequest(requete);
					tweets = result.getJSONArray("statuses");					
					nombreDeTweet = tweets.length();
					if (!result.getJSONObject("search_metadata").isNull("next_results")){
						next_results = result.getJSONObject("search_metadata").optString("next_results");
					}
					else {
						next_results = null;
					}
				}
			} while (next_results != null);
			System.out.println("End of the results");
		/*}
		else { // Because we cannot do all the request we want
			System.out.println("Too many requests ! You have to wait 15 minutes");
		}*/
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
		image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
	}
	
}
