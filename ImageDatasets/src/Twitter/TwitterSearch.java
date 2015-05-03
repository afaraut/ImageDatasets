package Twitter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	
	public TwitterSearch (String repertoire, String text, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private ArrayList<TwitterImage> getTwitterRessources() throws JSONException, URISyntaxException {
		
		ArrayList<TwitterImage> list = new ArrayList<TwitterImage>();

		String tmp_requete = null;
		if (text != null) {
			tmp_requete = "&q=" + text;
		}
		if (latitude != null && longitude != null)
			tmp_requete = tmp_requete.concat("&geocode=" + latitude + "," + longitude + "," + "400km");
		
		URI uri = new URI("https", "api.twitter.com", "/1.1/search/tweets.json","filter=images&count=30&include_entities=1&exclude=retweets" + tmp_requete, null);
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
		int nombreDeTweet = tweets.length();

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
				list.add(new TwitterImage(expanded_url, hashtags, jsonPhoto.optString("media_url")));
			}
		}
		return list;
	}

	public List<TwitterImage> getTwitterImages() throws IOException, JSONException, URISyntaxException {
		ArrayList<TwitterImage> list = getTwitterRessources();
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			// Créer le dossier avec tous ses parents
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		/*
		for (TwitterImage tw : list) {
			for (String s : tw.getPhotos()) {
				URL url = new URL(s);
				BufferedImage image = ImageIO.read(url);				
				String tmp[] = s.split("/");
				String nomFichier = tmp[tmp.length-1];
				ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + nomFichier));
			}
		}*/
		
		for (TwitterImage tw : list){
			URL url = new URL(tw.getPhoto());
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + tw.getFileName().concat("jpg")));
		
		}
		
		return list;
	}
	
	public void saveJSON(List<TwitterImage> list) {
		/*for (TwitterImage image : list){
			for (String s : image.getPhotos()) {
				String tmp[] = s.split("/");
				String nomFichier = tmp[tmp.length-1];
				nomFichier = nomFichier.substring(0, nomFichier.length()-3);
				image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + nomFichier.concat("json"));
			}
		}*/
		for (TwitterImage image : list){
			image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
		}
	}
}
