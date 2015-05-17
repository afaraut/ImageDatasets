package Instagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Utils.GlobalesConstantes;

public class InstagramSearch {

	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	private Integer distance;
	
	public InstagramSearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
		this.distance = null;
	}
	
	public InstagramSearch (String repertoire, Double latitude, Double longitude, Integer distance){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance; // La distance est en mètre
	}
	
	
	public void getInstagramRessources() throws JSONException, IOException {
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
	    if (text != null) {
			String query = "https://api.instagram.com/v1/tags/" + text + "/media/recent?client_id=" + InstagramConstantes.CLIENTID;
			URL fullUrl = new URL(query);
			InputStream inputStream = fullUrl.openStream();
			JSONObject result = new JSONObject(new JSONTokener(inputStream));
			JSONObject pagination = result.getJSONObject("pagination");
			
			Boolean bool = true;
			while (bool) {	
				// Generate a new request in order to get all the media from each page
				//
				fullUrl = new URL(query);
				inputStream = fullUrl.openStream();
				result = new JSONObject(new JSONTokener(inputStream));
				pagination = result.getJSONObject("pagination");
				query = pagination.optString("next_url");
				bool = !pagination.isNull("next_url");
				JSONArray tweets = result.getJSONArray("data");
				int nombreDeMessage = tweets.length();
				// Get all the media
				//
				for (int i = 0; i < nombreDeMessage; i++) {
					ArrayList<String> hashtags = new ArrayList<String>();
					JSONObject tweet = (JSONObject) tweets.opt(i);
					JSONArray json_hashtags = tweet.getJSONArray("tags");			
					int nombreDeHashTag = json_hashtags.length();
					// --- Get all the hashtag
					//
					for (int j=0; j<nombreDeHashTag; j++) {
						hashtags.add(json_hashtags.optString(j));
					}
					System.out.println("Get the pictures from Instagram... [" + new Integer(i+1) + "/" + nombreDeMessage + "]");
					String url_image = tweet.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
					InstagramImage image = new InstagramImage(tweet.optString("link"), url_image, hashtags);
					saveInstagramImage(image); // Download image
					saveJSON(image); // Save json
				}
				System.out.println("... Nouvelle page ...");
			}
			inputStream.close();
	    }
	    else if (latitude != null && longitude != null && distance != null) {
			int min_timestamp = 0;
			String query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance;
			while (true) { // A changer pour faire propre
				URL fullUrl = new URL(query);
				InputStream inputStream = fullUrl.openStream();
		
				JSONObject result = new JSONObject(new JSONTokener(inputStream));
				JSONArray tweets = result.getJSONArray("data");
				int nombreDeMessage = tweets.length();

				Integer created_time_tmp = new Integer(((JSONObject) tweets.opt(0)).optString("created_time"));
				min_timestamp = created_time_tmp;

				for (int i = 0; i < nombreDeMessage; i++) {
					ArrayList<String> hashtags = new ArrayList<String>();
					JSONObject tweet = (JSONObject) tweets.opt(i);
					Integer created_time = new Integer(tweet.optString("created_time"));
					if (min_timestamp > created_time)
						min_timestamp = created_time;
					
					JSONArray json_hashtags = tweet.getJSONArray("tags");			
					int nombreDeHashTag = json_hashtags.length();
					// --- Get all the hashtag
					//
					for (int j=0; j<nombreDeHashTag; j++) {
						hashtags.add(json_hashtags.optString(j));
					}
					System.out.println("Get the pictures from Instagram... [" + new Integer(i+1) + "/" + nombreDeMessage + "]");
					String url_image = tweet.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
					InstagramImage image = new InstagramImage(tweet.optString("link"), url_image, hashtags);
					saveInstagramImage(image); // Download image
					saveJSON(image); // Save json
				}			
				query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance + "&max_timestamp=" + min_timestamp;
				inputStream.close();
				System.out.println("... Nouvelle page ...");
			}
	    }
	}
	
	public void saveInstagramImage(InstagramImage igImage) throws IOException, JSONException {
		URL url = new URL(igImage.getPhoto());
		BufferedImage image = ImageIO.read(url);
		ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + igImage.getFileName().concat("jpg")));
	}
	
	public void saveJSON(InstagramImage image) {
		image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
	}
	
	public void printInstagramNumberResult() {
		try {
			String tmp = "Results for the following request ...\n";
			if (text != null) 
				tmp = tmp.concat(" hashtag = " + text + " " );
			if (latitude != null && longitude != null && distance != null)
				tmp = tmp.concat(" latitude = " + latitude + " longitude = " + longitude + " distance = " + distance + " on Instagram ... ");
			
			System.out.println(tmp  + getInstagramNumberResult() + " picture(s)");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Integer getInstagramNumberResult () throws IOException, JSONException {
		Integer total = 0;
		
		if (text != null) {
			String query = "https://api.instagram.com/v1/tags/" + text + "/media/recent?client_id=" + InstagramConstantes.CLIENTID;
			URL fullUrl = new URL(query);
			InputStream inputStream = fullUrl.openStream();
			JSONObject result = new JSONObject(new JSONTokener(inputStream));
			JSONObject pagination = result.getJSONObject("pagination");
			System.out.println("Computing ...");
			Boolean bool = true;
			while (bool) {		
				fullUrl = new URL(query);
				inputStream = fullUrl.openStream();
				result = new JSONObject(new JSONTokener(inputStream));
				pagination = result.getJSONObject("pagination");
				query = pagination.optString("next_url");
				JSONArray tweets = result.getJSONArray("data");
				total+= tweets.length();
				bool = !pagination.isNull("next_url");
			}
			inputStream.close();
		}
		else if (latitude != null && longitude != null && distance != null) {		
			int min_timestamp = 0;
			String query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance;
			System.out.println("Computing ...");
			while (true) { // A changer sinon on n'aura jamais le résultat
				URL fullUrl = new URL(query);
				InputStream inputStream = fullUrl.openStream();
				JSONObject result = new JSONObject(new JSONTokener(inputStream));
				JSONArray tweets = result.getJSONArray("data");
				JSONObject dhfdhdh = result.getJSONObject("meta");
				System.out.println(dhfdhdh.optInt("code"));
				int nombreDeMessage = tweets.length();
				min_timestamp = new Integer(((JSONObject) tweets.opt(0)).optString("created_time"));
				total+= nombreDeMessage;
				for (int i = 0; i < nombreDeMessage; i++) {
					JSONObject tweet = (JSONObject) tweets.opt(i);
					Integer created_time = new Integer(tweet.optString("created_time"));
					if (min_timestamp > created_time)
						min_timestamp = created_time;
				}
				query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance + "&max_timestamp=" + min_timestamp;
				inputStream.close();
			}
		}
		return total;
	}
	
}
