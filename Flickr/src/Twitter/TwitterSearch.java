package Twitter;

import java.io.IOException;
import java.util.ArrayList;

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
	
	private ArrayList<TwitterImage> getTwitterRessources() throws JSONException {
		
		ArrayList<TwitterImage> list = new ArrayList<TwitterImage>();
		String requete = "https://api.twitter.com/1.1/search/tweets.json?count=30&include_entities=true";
		
		if (text != null) 
			requete = requete.concat("&q=" + text);
		if (latitude != null && longitude != null)
			requete = requete.concat("&geocode=" + latitude + "," + longitude + "," + "200km");
		
		System.out.println(requete);
		
		OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(TwitterConstantes.APIKEY).apiSecret(TwitterConstantes.APIKEYSECRET).build();
		Token accessToken = new Token(TwitterConstantes.TOKEN, TwitterConstantes.TOKENSECRET);
				
		System.out.println("Requete...");
		OAuthRequest request = new OAuthRequest(Verb.GET, requete);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Results...");
		System.out.println();
		//System.out.println(response.getBody());
		JSONObject result = new JSONObject(response.getBody());
		//System.out.println(result);
		JSONArray tweets = result.getJSONArray("statuses");
		
		for (int i = 0; i < 10; i++) {
			JSONObject tweet = (JSONObject) tweets.opt(i);
			System.out.println(tweet);
		}

		return list;
	}

	public void getTwitterImages() throws IOException, JSONException {
		ArrayList<TwitterImage> list = getTwitterRessources();
		/*for (TwitterImage fi : list) {
			if (fi.getCandownload().equals("1")) {
				URL url = new URL("https://farm8.staticflickr.com/"+ fi.getServer() + "/" + fi.getId() + "_"+ fi.getOriginalsecret() + "_o."+ fi.getOriginalformat());
				BufferedImage image = ImageIO.read(url);
				ImageIO.write(image,"jpg",new File("D:\\flickr_test\\" + fi.getId() + "."+ fi.getOriginalformat()));
			} else {
				URL url = new URL("https://farm8.staticflickr.com/"+ fi.getServer() + "/" + fi.getId() + "_"+ fi.getSecret() + "_b.jpg");
				BufferedImage image = ImageIO.read(url);
				ImageIO.write(image, "jpg",new File("D:\\flickr_test\\" + fi.getId() + ".jpg"));
			}
		}*/
	}

}
