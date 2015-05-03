package Instagram_with_auth;

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
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import Utils.GlobalesConstantes;
import Utils.InstagramApi;

public class InstagramWASearch {

	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	
	public InstagramWASearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
	}
	
	public InstagramWASearch (String repertoire, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private ArrayList<InstagramWAImage> getInstagramWARessources() throws JSONException, URISyntaxException {
		
		ArrayList<InstagramWAImage> list = new ArrayList<InstagramWAImage>();

		
		String tmp_requete = "";
		URI uri = null;
		if (text != null) {
			tmp_requete = text + "/media/recent";
			uri = new URI("https", "api.instagram.com", "/v1/tags/" + tmp_requete , null);
		}
		if (latitude != null && longitude != null) {
			tmp_requete = tmp_requete.concat("lat=" + latitude + "&lng=" + longitude + "&distance=" + "200");
			uri = new URI("https", "api.instagram.com", "/v1/media/search", tmp_requete, null);
		}

		String requete = uri.toASCIIString();
		System.out.println(requete);
		OAuthService service = new ServiceBuilder().provider(InstagramApi.class).apiKey(InstagramWAConstantes.APIKEY).apiSecret(InstagramWAConstantes.APIKEYSECRET).build();
		Token accessToken = new Token(InstagramWAConstantes.TOKEN, InstagramWAConstantes.TOKENSECRET);

		// https://api.instagram.com/v1/tags/beaulieusurmer/media/recent
				
		System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, requete);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Results...");
		System.out.println(response.getBody());
		JSONObject result = new JSONObject(response.getBody());
		System.out.println(result);
		JSONArray tweets = result.getJSONArray("data");
		int nombreDeTweet = tweets.length();

		for (int i = 0; i < nombreDeTweet; i++) {
			
			ArrayList<String> hashtags = new ArrayList<String>();
			JSONObject tweet = (JSONObject) tweets.opt(i);

			// --- Get all the hashtags
			//
			JSONArray json_hashtags = tweet.getJSONArray("tags");			
			int nombreDeHashTag = json_hashtags.length();
			
			for (int j=0; j<nombreDeHashTag; j++){
				hashtags.add(json_hashtags.optString(j));
			}

			String url_image = tweet.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
			list.add(new InstagramWAImage(tweet.optString("link"), url_image, hashtags));
		}
		return list;
	}

	public void getInstagramWAImages() throws IOException, JSONException, URISyntaxException {
		ArrayList<InstagramWAImage> list = getInstagramWARessources();
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			// Créer le dossier avec tous ses parents
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		for (InstagramWAImage tw : list){
				URL url = new URL(tw.getPhoto());
				BufferedImage image = ImageIO.read(url);
				String nomFichier = tw.getPhoto().split("/")[6];
				ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + nomFichier));
			
		}
	}
	
	
	
}
