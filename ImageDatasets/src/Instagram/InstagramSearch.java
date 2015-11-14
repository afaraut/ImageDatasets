package Instagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Utils.GlobalesConstantes;
import Utils.Toolbox;

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
	
	public void getInstagramRessources(){

	    if (text != null) {
			String query = "https://api.instagram.com/v1/tags/" + text + "/media/recent?client_id=" + InstagramConstantes.CLIENTID;
			try {
				URL fullUrl = new URL(query);
				InputStream inputStream = fullUrl.openStream();
				JSONObject result = new JSONObject(new JSONTokener(inputStream));
				JSONObject pagination = result.getJSONObject("pagination");
				
				Boolean bool = !pagination.isNull("next_url");
				while (bool) {	
					JSONArray instagramPosts = result.getJSONArray("data");
					int nombreDeMessage = instagramPosts.length();
					// Get all the media
					//
					for (int i = 0; i < nombreDeMessage; i++){
						JSONObject instagramPost = (JSONObject) instagramPosts.opt(i);
						System.out.println("Get the pictures from Instagram... [" + new Integer(i+1) + "/" + nombreDeMessage + "]");
						String url_image = instagramPost.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
						InstagramImage image = new InstagramImage(instagramPost, repertoire, url_image);
						saveInstagramImage(image); // Download image
						saveJSON(image); // Save json
					}
					System.out.println("... Nouvelle page ...");
					
					pagination = result.getJSONObject("pagination");
					query = pagination.optString("next_url");
					bool = !pagination.isNull("next_url");
					fullUrl = new URL(query);
					inputStream.close();
					inputStream = fullUrl.openStream();
					result = new JSONObject(new JSONTokener(inputStream));				
				}
				inputStream.close();
			} catch (IllegalArgumentException | IOException | JSONException e){
				e.printStackTrace();
			}
	    }
	    else if (latitude != null && longitude != null && distance != null){
			int min_timestamp = 0;
			String query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance;
			try {
				URL fullUrl = new URL(query);
				InputStream inputStream = fullUrl.openStream();
				JSONObject result = new JSONObject(new JSONTokener(inputStream));
				JSONArray instagramPosts = result.getJSONArray("data");
				int nombreDeMessage = instagramPosts.length();
				Boolean bool = nombreDeMessage > 0 ? true : false;
				
				while (bool){
					Integer created_time_tmp = new Integer(((JSONObject) instagramPosts.opt(0)).optString("created_time"));
					min_timestamp = created_time_tmp;
					for (int i = 0; i < nombreDeMessage; i++) {
						JSONObject instagramPost = (JSONObject) instagramPosts.opt(i);
						System.out.println(instagramPost);
						Integer created_time = new Integer(instagramPost.optString("created_time"));
						if (min_timestamp > created_time){
							min_timestamp = created_time;
						}
						System.out.println("Get the pictures from Instagram... [" + new Integer(i+1) + "/" + nombreDeMessage + "]");
						String url_image = instagramPost.getJSONObject("images").getJSONObject("standard_resolution").optString("url");
						InstagramImage image = new InstagramImage(instagramPost, repertoire, url_image);
						saveInstagramImage(image); // Download image
						saveJSON(image); // Save json
					}			
					query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance + "&max_timestamp=" + min_timestamp;

					System.out.println("... Nouvelle page ...");
					fullUrl = new URL(query);
					inputStream.close();
					inputStream = fullUrl.openStream();
			
					result = new JSONObject(new JSONTokener(inputStream));
					System.out.println(result);
					instagramPosts = result.getJSONArray("data");
					nombreDeMessage = instagramPosts.length();
					bool = nombreDeMessage > 1 ? true : false;
				}
				inputStream.close();
			} catch (IllegalArgumentException | IOException | JSONException e) {
				e.printStackTrace();
			}
	    }
	    System.out.println("End of result ...");
	}
	
	protected void saveInstagramImage(InstagramImage igImage){
		try {
			URL url = new URL(igImage.getPhoto());
			String extenstion = Toolbox.getExtensionFromURL(url.toString());
			
			BufferedImage image = ImageIO.read(url);
			String tmp = igImage.getFileName() + extenstion;
			ImageIO.write(image, extenstion, new File(GlobalesConstantes.REPERTOIRE + igImage.getDirectory() + tmp));
			
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveJSON(InstagramImage image){	    
		image.saveJSON_FILE(GlobalesConstantes.REPERTOIRE + image.getDirectory() + image.getFileName() + "json");
		image.saveJSON_DB();
	}
	
	public void printInstagramNumberResult(){
		String tmp = "Results for the following request ...\n";
		if (text != null) 
			tmp = tmp.concat(" hashtag = " + text + " " );
		if (latitude != null && longitude != null && distance != null)
			tmp = tmp.concat(" latitude = " + latitude + " longitude = " + longitude + " distance = " + distance + " on Instagram ... ");
		System.out.println(tmp  + getInstagramNumberResult() + " picture(s)");
	}
	
	public Integer getInstagramNumberResult (){
		Integer total = 0;
	    if (text != null) {
			String query = "https://api.instagram.com/v1/tags/" + text + "/media/recent?client_id=" + InstagramConstantes.CLIENTID;
			try {
				URL fullUrl = new URL(query);
				InputStream inputStream = fullUrl.openStream();
				JSONObject result = new JSONObject(new JSONTokener(inputStream));
				JSONObject pagination = result.getJSONObject("pagination");
				
				Boolean bool = !pagination.isNull("next_url");
				while (bool) {	
					JSONArray instagramPosts = result.getJSONArray("data");
					total+= instagramPosts.length();
					System.out.println("... Nouvelle page ...");
					
					pagination = result.getJSONObject("pagination");
					query = pagination.optString("next_url");
					bool = !pagination.isNull("next_url");
					fullUrl = new URL(query);
					inputStream.close();
					inputStream = fullUrl.openStream();
					result = new JSONObject(new JSONTokener(inputStream));				
				}
				inputStream.close();
			} catch (IllegalArgumentException | IOException | JSONException e) {
				e.printStackTrace();
			}
	    }	    
	    else if (latitude != null && longitude != null && distance != null){
			int min_timestamp = 0;
			String query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance;
			try {
				URL fullUrl = new URL(query);
				InputStream inputStream = fullUrl.openStream();
				JSONObject result = new JSONObject(new JSONTokener(inputStream));
				JSONArray instagramPosts = result.getJSONArray("data");
				int nombreDeMessage = instagramPosts.length();
				Boolean bool = nombreDeMessage > 0 ? true : false;
				
				while (bool) {
					Integer created_time_tmp = new Integer(((JSONObject) instagramPosts.opt(0)).optString("created_time"));
					min_timestamp = created_time_tmp;
					for (int i = 0; i < nombreDeMessage; i++) {
						JSONObject instagramPost = (JSONObject) instagramPosts.opt(i);
						System.out.println(instagramPost);
						Integer created_time = new Integer(instagramPost.optString("created_time"));
						if (min_timestamp > created_time){
							min_timestamp = created_time;
						}
					}			
					total+= nombreDeMessage;
					query = "https://api.instagram.com/v1/media/search?client_id=" + InstagramConstantes.CLIENTID + "&lat="+latitude+"&lng="+longitude+"&distance=" + distance + "&max_timestamp=" + min_timestamp;

					System.out.println("... Nouvelle page ...");
					fullUrl = new URL(query);
					inputStream.close();
					inputStream = fullUrl.openStream();
			
					result = new JSONObject(new JSONTokener(inputStream));
					System.out.println(result);
					instagramPosts = result.getJSONArray("data");
					nombreDeMessage = instagramPosts.length();
					bool = nombreDeMessage > 1 ? true : false;
				}
				inputStream.close();
			} catch (IllegalArgumentException | IOException | JSONException e) {
				e.printStackTrace();
			}
	    }
		return total;
	}
}
