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
	
	public TwitterSearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
		this.distance = null;
	}
	
	public TwitterSearch (String repertoire, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	public TwitterSearch (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	/**
	 * Try to get all the media from each page
	 * @throws JSONException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void getTwitterRessources () throws JSONException, URISyntaxException, IOException {
		String tmp_requete = "";
		if (text != null) 
			tmp_requete = "&q=" + text;
		if (latitude != null && longitude != null && distance != null)
			tmp_requete = tmp_requete.concat("&geocode=" + latitude + "," + longitude + "," + distance + "km");
		
		URI uri = new URI("https", "api.twitter.com", "/1.1/search/tweets.json","filter=images&count=100&include_entities=1&exclude=retweets" + tmp_requete, null);
		String requete = uri.toASCIIString();
		
		OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(TwitterConstantes.APIKEY).apiSecret(TwitterConstantes.APIKEYSECRET).build();
		Token accessToken = new Token(TwitterConstantes.TOKEN, TwitterConstantes.TOKENSECRET);
				
		System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, requete);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Results...");
		JSONObject result = new JSONObject(response.getBody());
		JSONArray tweets = result.getJSONArray("statuses");
		JSONObject search_metadata = result.getJSONObject("search_metadata");
		int nombreDePage = 0;
		int nombreDeTweet = search_metadata.optInt("count");	
		if (result.isNull("errors")) {
			while (true) { // A modifier
				for (int i = 0; i < nombreDeTweet; i++) {
					if (!result.isNull("errors")) {
						System.out.println("Too many requests ! You have to wait 15 minutes");
						return;
					}
					ArrayList<String> hashtags = new ArrayList<String>();	
					JSONObject tweet = (JSONObject) tweets.opt(i);
					JSONArray json_hashtags = tweet.getJSONObject("entities").getJSONArray("hashtags");			
					int nombreDeHashTag = json_hashtags.length();
					
					// --- Get all the hashtag
					//
					for (int k=0; k<nombreDeHashTag; k++){
						hashtags.add(((JSONObject) json_hashtags.opt(k)).optString("text"));
					}
					
					String expanded_url = ((JSONObject)(tweet.getJSONObject("entities").getJSONArray("media")).opt(0)).optString("expanded_url");
					
					// --- Second request in order to get all the pictures from the tweet
					//
					String requetee = "https://api.twitter.com/1.1/statuses/show.json?id=" + tweet.optString("id_str") ;
					OAuthRequest requesteee = new OAuthRequest(Verb.GET, requetee);
					service.signRequest(accessToken, requesteee);
					response = requesteee.send();
					System.out.println("Get the pictures from the tweet... [" + new Integer(i+1) + "/" + nombreDeTweet + "] page " + nombreDePage);
					result = new JSONObject(response.getBody());
					System.out.println(result);
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
				// Generate a new request in order to get all the media from each page
				//
				nombreDePage++;	
				requete = "https://api.twitter.com/1.1/search/tweets.json" + search_metadata.optString("next_results") +"&count=100&include_entities=1";
				request = new OAuthRequest(Verb.GET, requete);
				service.signRequest(accessToken, request);
				response = request.send();
				result = new JSONObject(response.getBody());
				tweets = result.getJSONArray("statuses");
				search_metadata = result.getJSONObject("search_metadata");
				nombreDeTweet = search_metadata.optInt("count");
			}
		}
		else { // Because we cannot do all the request we want
			System.out.println("Too many requests ! You have to wait 15 minutes");
		}
	}
	
 	/**
	 * Get all the media from the first page (100 pictures)
	 * @throws JSONException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
/*	public void getTwitterRessources() throws JSONException, URISyntaxException, IOException {
		
		String tmp_requete = "";
		if (text != null) 
			tmp_requete = "&q=" + text;
		if (latitude != null && longitude != null && distance != null)
			tmp_requete = tmp_requete.concat("&geocode=" + latitude + "," + longitude + "," + distance + "km");
		
		URI uri = new URI("https", "api.twitter.com", "/1.1/search/tweets.json","filter=images&count=100&include_entities=1&exclude=retweets" + tmp_requete, null);
		String requete = uri.toASCIIString();
		
		OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(TwitterConstantes.APIKEY).apiSecret(TwitterConstantes.APIKEYSECRET).build();
		Token accessToken = new Token(TwitterConstantes.TOKEN, TwitterConstantes.TOKENSECRET);
				
		System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, requete);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Results...");
		JSONObject result = new JSONObject(response.getBody());
		System.out.println(result);
		JSONArray tweets = result.getJSONArray("statuses");
		JSONObject search_metadata = result.getJSONObject("search_metadata");
		int nombreDeTweet = search_metadata.optInt("count");
		
		for (int i = 0; i < nombreDeTweet; i++) {
			
			ArrayList<String> hashtags = new ArrayList<String>();
			ArrayList<String> photos = new ArrayList<String>();	
			
			JSONObject tweet = (JSONObject) tweets.opt(i);
			//System.out.println(tweet);
			// --- Get all the hashtags
			//
			JSONArray json_hashtags = tweet.getJSONObject("entities").getJSONArray("hashtags");			
			int nombreDeHashTag = json_hashtags.length();
			
			for (int k=0; k<nombreDeHashTag; k++){
				JSONObject hashtag = (JSONObject) json_hashtags.opt(k);
				hashtags.add(hashtag.optString("text"));
			}
			
			String expanded_url = ((JSONObject)(tweet.getJSONObject("entities").getJSONArray("media")).opt(0)).optString("expanded_url");
			
			// --- Second request in order to get all the pictures from the tweet
			//
			requete = "https://api.twitter.com/1.1/statuses/show.json?id=" + tweet.optString("id_str") ;
			request = new OAuthRequest(Verb.GET, requete);
			service.signRequest(accessToken, request);
			response = request.send();
			System.out.println("Get the pictures from the tweet... [" + new Integer(i+1) + "/" + nombreDeTweet + "]");
			result = new JSONObject(response.getBody());
			JSONArray medias = result.getJSONObject("extended_entities").getJSONArray("media");
			int nombreDeMedia = medias.length();
			for (int j=0; j<nombreDeMedia; j++){
				JSONObject jsonPhoto = (JSONObject) medias.opt(j);
				photos.add(jsonPhoto.optString("media_url"));
				TwitterImage image = new TwitterImage(expanded_url, hashtags, jsonPhoto.optString("media_url"));
				saveTwitterImage(image); // Download image
				saveJSON(image); // Save json
			}
		}
	}*/

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
