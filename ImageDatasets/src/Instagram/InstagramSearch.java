package Instagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	
	public InstagramSearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
	}
	
	public InstagramSearch (String repertoire, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private ArrayList<InstagramImage> getInstagramRessources()	throws IOException, JSONException {
		ArrayList<InstagramImage> list = new ArrayList<InstagramImage>();
		String baseUrl = "https://api.instagram.com/v1/";
		String query = "";
		
		if (text != null) 
			query = query.concat("tags/" + text + "/media/recent?client_id=" + InstagramConstantes.CLIENTID);
		
		if (latitude != null && longitude != null)
			query = query.concat("/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=200");

		URL fullUrl = new URL(baseUrl + query);
		InputStream inputStream = fullUrl.openStream();

		JSONObject result = new JSONObject(new JSONTokener(inputStream));
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
			list.add(new InstagramImage(tweet.optString("link"), url_image, hashtags));
		}
		inputStream.close();
		return list;
	}

	public List<InstagramImage> getInstagramImages() throws IOException, JSONException {
		ArrayList<InstagramImage> list = getInstagramRessources();
		
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			// Créer le dossier avec tous ses parents
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		for (InstagramImage tw : list){
				URL url = new URL(tw.getPhoto());
				BufferedImage image = ImageIO.read(url);
				ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + tw.getFileName().concat("jpg")));
			
		}
		return list;
	}

	public void saveJSON(List<InstagramImage> list) {
		for (InstagramImage image : list){
			image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
		}
	}
}
