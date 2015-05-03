package Instagram_with_auth;

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
		URI uri = null;
		if (text != null) 
			uri = new URI("https", "api.instagram.com", "/v1/tags/" + text + "/media/recent" , null);
		
		if (latitude != null && longitude != null) 
			uri = new URI("https", "api.instagram.com", "/v1/media/search", "lat=" + latitude + "&lng=" + longitude + "&distance=" + "200", null);
		
		String requete = uri.toASCIIString();
		System.out.println(requete);
		OAuthService service = new ServiceBuilder().provider(InstagramApi.class).apiKey(InstagramWAConstantes.APIKEY).apiSecret(InstagramWAConstantes.APIKEYSECRET).build();
		Token accessToken = new Token(InstagramWAConstantes.TOKEN, InstagramWAConstantes.TOKENSECRET);		
		System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, requete);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Results...");
		JSONObject result = new JSONObject(response.getBody());
		JSONArray tweets = result.getJSONArray("data");
		int nombreDeMessage = tweets.length();

		for (int i = 0; i < nombreDeMessage; i++) {
			ArrayList<String> hashtags = new ArrayList<String>();
			JSONObject tweet = (JSONObject) tweets.opt(i);

			// --- Get all the hashtags
			//
			JSONArray json_hashtags = tweet.getJSONArray("tags");			
			int nombreDeHashTag = json_hashtags.length();
			
			for (int j=0; j<nombreDeHashTag; j++){
				hashtags.add(json_hashtags.optString(j));
			}
			System.out.println("Get the pictures from Instagram... [" + new Integer(i+1) + "/" + nombreDeMessage + "]");
			String url_image = tweet.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
			list.add(new InstagramWAImage(tweet.optString("link"), url_image, hashtags));
		}
		return list;
	}
	
	public List<InstagramWAImage> getInstagramWAImages() throws IOException, JSONException, URISyntaxException {
		ArrayList<InstagramWAImage> list = getInstagramWARessources();
		
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			// Créer le dossier avec tous ses parents
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		for (InstagramWAImage tw : list){
				URL url = new URL(tw.getPhoto());
				BufferedImage image = ImageIO.read(url);
				ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + tw.getFileName().concat("jpg")));
		}
		return list;
	}
	
	public void saveJSON(List<InstagramWAImage> list) {
		for (InstagramWAImage image : list){
			image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
		}
	}
}
