package Flickr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Utils.GlobalesConstantes;
import Utils.Toolbox;

public class FlickrSearch {
	
	public static final String BASEURLSEARCH = "https://api.flickr.com/services/rest/?method=flickr.photos.search"
			 								 + /*"&has_geo=true" +*/ "&per_page=500&api_key="+ FlickrConstantes.APIKEY;
	public static final String BASEURLGETINFO = "https://api.flickr.com/services/rest/?method=flickr.photos.getInfo";
	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	private Double distance;
	private String query;
	
	public FlickrSearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
		this.distance = null;
		query = BASEURLSEARCH;
		if (text != null) {
			try {
				query += "&text='" + URLEncoder.encode(text + "'", "UTF-8");
			} catch (IllegalArgumentException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		query += "&format=json&nojsoncallback=1";
	}
	
	public FlickrSearch (String repertoire, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
		query = BASEURLSEARCH;
		if (latitude != null && longitude != null && distance != null) {
			query += "&lat=" + latitude + "&lon=" + longitude + "&radius=" + distance;
		}
		query += "&format=json&nojsoncallback=1";
	}
	
	public FlickrSearch (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
		query = BASEURLSEARCH;
		try {
			if (text != null && latitude != null && longitude != null && distance != null) {
				query += "&text='" + URLEncoder.encode(text + "'", "UTF-8") + "&lat=" + latitude + "&lon=" + longitude + "&radius=" + distance;
			}
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		query += "&format=json&nojsoncallback=1";
	}
	
	public Integer getFlickrNumberResult() {
		try {
			URL fullUrl = new URL(query);
			InputStream inputStream = fullUrl.openStream();
			JSONObject result = new JSONObject(new JSONTokener(inputStream));
			return result.getJSONObject("photos").optInt("total");
			
		} catch (IllegalArgumentException | JSONException | IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void printFlickrNumberResult() {	
		String tmp = "Results for the following request ...\n";
		if (text != null) {
			tmp = tmp.concat(" text = " + text + " on Flickr ... ");
		}
		if (latitude != null && longitude != null && distance != null){
			tmp = tmp.concat(" latitude = " + latitude + " longitude = " + longitude + " distance = " + distance + " on Flickr ... ");
		}
		System.out.println(tmp  + getFlickrNumberResult() + " picture(s)");
	}
	
	public String makeURLphoto (JSONObject photo){		
		try {
			JSONObject jsonPInfos = photo.getJSONObject("photo");
			String id = jsonPInfos.optString("id");
			int farm = jsonPInfos.optInt("farm");
			String server = jsonPInfos.optString("server");
			String secret = jsonPInfos.optString("secret");
			String originalsecret = jsonPInfos.optString("originalsecret");
			String originalformat = jsonPInfos.optString("originalformat");
			if (jsonPInfos.getJSONObject("usage").optString("candownload").equals("1")){
				return "https://farm"+farm+".staticflickr.com/"+ server + "/" + id + "_"+ originalsecret + "_o."+ originalformat;
			}
			else {
				return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + "_b.jpg";
			}
		} catch (IllegalArgumentException | JSONException e) {
			e.printStackTrace();
		}
		return new String();
	}
	
	public void getFlickrRessources(){
		try {
			URL fullUrl = new URL(query);
			InputStream inputStream = fullUrl.openStream();
			JSONObject result = new JSONObject(new JSONTokener(inputStream));
			int count = result.getJSONObject("photos").optInt("total") % result.getJSONObject("photos").optInt("perpage");
			int numberOfPage = result.getJSONObject("photos").optInt("pages");
			
			for (int k=0; k < numberOfPage; k++) {
				System.out.println("Page [" + new Integer(k+1) + "/" + numberOfPage + "]");
				//if (count > 0 ) {
					JSONArray jsonPhotos = result.getJSONObject("photos").getJSONArray("photo");
					for (int i = 0; i < count; i++) {
						JSONObject jsonPhoto = (JSONObject) jsonPhotos.opt(i);
						
						// New Request in order to get all the information
						String fullUrlStr = BASEURLGETINFO + "&photo_id=" + jsonPhoto.optString("id") + "&api_key=" + FlickrConstantes.APIKEY + "&format=json&nojsoncallback=1";
						fullUrl = new URL(fullUrlStr);
						inputStream = fullUrl.openStream();
			
						result = new JSONObject(new JSONTokener(inputStream));
						System.out.println("Get the pictures from Flickr... [" + new Integer(i+1) + "/" + count + "]");

						FlickrImage image = new FlickrImage(result, repertoire, makeURLphoto(result));
						saveFlickrImage(image); // Download image
						saveJSON(image); // Save json
					}
					inputStream.close();
				//}
				// Generate a new request in order to get all the media from each page
				fullUrl = new URL(query + "&page=" + new Integer(k+1));
				inputStream = fullUrl.openStream();
				result = new JSONObject(new JSONTokener(inputStream));
				count = result.getJSONObject("photos").optInt("perpage");
			}
			inputStream.close();
		} catch (IllegalArgumentException | JSONException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("End of result ...");
	}
	
	protected void saveFlickrImage(FlickrImage fkImage){
		try {
			URL url = new URL(fkImage.getPhoto());
			String extenstion = Toolbox.getExtensionFromURL(url.toString());
			
			BufferedImage image = ImageIO.read(url);
			String tmp = fkImage.getFileName() + extenstion;
			ImageIO.write(image, extenstion, new File(GlobalesConstantes.REPERTOIRE + fkImage.getDirectory() + tmp));
			
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveJSON(FlickrImage image){	    
		image.saveJSON_FILE(GlobalesConstantes.REPERTOIRE + image.getDirectory() + image.getFileName() + "json");
		image.saveJSON_DB();
	}	
}
