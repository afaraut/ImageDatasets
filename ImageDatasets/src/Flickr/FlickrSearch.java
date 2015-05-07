package Flickr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Utils.GlobalesConstantes;

public class FlickrSearch {

	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	private Double distance;
	
	public FlickrSearch (String repertoire, String text){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = null;
		this.longitude = null;
		this.distance = null;
	}
	
	public FlickrSearch (String repertoire, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = null;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	public FlickrSearch (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	public Integer getFlickrNumberResult() throws IOException, JSONException {
		String baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
		String query = "&has_geo=true&per_page=500&api_key="+ FlickrConstantes.APIKEY;

		if (text != null) 
			query = query.concat("&text='" + URLEncoder.encode(text + "'", "UTF-8"));
		if (latitude != null && longitude != null && distance != null)
			query = query.concat("&lat=" + latitude + "&lon=" + longitude + "&radius=" + distance);
		
		String fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1";
		URL fullUrl = new URL(fullUrlStr);
		InputStream inputStream = fullUrl.openStream();
		JSONObject result = new JSONObject(new JSONTokener(inputStream));
		return result.getJSONObject("photos").optInt("total");
	}
	
	public void printFlickrNumberResult() {	
		try {
			String tmp = "Results for the following request ...\n";
			if (text != null) 
				tmp = tmp.concat(" text = " + text);
			if (latitude != null && longitude != null && distance != null)
				tmp = tmp.concat(" latitude = " + latitude + " longitude = " + longitude + " distance = " + distance + " on Flickr ... ");
			
			System.out.println(tmp  + getFlickrNumberResult() + " picture(s)");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<FlickrImage> getFlickrRessources()	throws IOException, JSONException {
		ArrayList<FlickrImage> list = new ArrayList<FlickrImage>();
		String baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
		String query = "&has_geo=true&per_page=25&api_key="+ FlickrConstantes.APIKEY;

		if (text != null) 
			query = query.concat("&text='" + URLEncoder.encode(text + "'", "UTF-8"));
		if (latitude != null && longitude != null)
			query = query.concat("&lat=" + latitude + "&lon=" + longitude + "&radius=" + distance);
		
		String fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1";
		URL fullUrl = new URL(fullUrlStr);
		InputStream inputStream = fullUrl.openStream();
		JSONObject result = new JSONObject(new JSONTokener(inputStream));
		int count = result.getJSONObject("photos").optInt("perpage");
		int numberOfPage = result.getJSONObject("photos").optInt("pages");
		for (int k=0; k < numberOfPage; k++) {
			
			if (k != 0) {
				baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
				query = "&has_geo=true&per_page=25&api_key="+ FlickrConstantes.APIKEY;
				fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1&page=" + new Integer(k+1);
				System.out.println(fullUrlStr);
				fullUrl = new URL(fullUrlStr);
				inputStream = fullUrl.openStream();
				result = new JSONObject(new JSONTokener(inputStream));
				count = result.getJSONObject("photos").optInt("perpage");
			}
			
			
			System.out.println("Page [" + new Integer(k+1) + "/" + numberOfPage + "]");
			if (count > 0 ) {
				JSONArray jsonPhotos = result.getJSONObject("photos").getJSONArray("photo");
				for (int i = 0; i < count; i++) {
					ArrayList<String> hashtags = new ArrayList<String>();
					JSONObject jsonPhoto = (JSONObject) jsonPhotos.opt(i);
					String id = jsonPhoto.optString("id");
					// ------------------------------------
					baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.getInfo";
					query = "&photo_id=" + id + "&api_key=" + FlickrConstantes.APIKEY;
	
					fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1";
					fullUrl = new URL(fullUrlStr);
					inputStream = fullUrl.openStream();
		
					result = new JSONObject(new JSONTokener(inputStream));
					JSONObject jsonPInfos = result.getJSONObject("photo");
					if (!jsonPInfos.isNull("tags")) {
						JSONArray tags = jsonPInfos.getJSONObject("tags").getJSONArray("tag");
						for (int j = 0; j < tags.length(); j++) {
							JSONObject tag = tags.getJSONObject(j);
							hashtags.add(tag.optString("raw"));
						}
					}
					
					String link = jsonPInfos.getJSONObject("urls").getJSONArray("url").getJSONObject(0).optString("_content");
					System.out.println("Get the pictures from Flickr... [" + new Integer(i+1) + "/" + count + "]");
					// ------------------------------------
					
					int farm = jsonPInfos.optInt("farm");
					String server = jsonPhoto.optString("server");
					String secret = jsonPInfos.optString("secret");
					String originalsecret = jsonPInfos.optString("originalsecret");
					String originalformat = jsonPInfos.optString("originalformat");
					
					String photo = "";
					if (jsonPInfos.getJSONObject("usage").optString("candownload").equals("1"))
						photo = "https://farm"+farm+".staticflickr.com/"+ server + "/" + id + "_"+ originalsecret + "_o."+ originalformat;
					else 
						photo = "https://farm"+farm+".staticflickr.com/"+ server + "/" + id + "_"+ secret + "_b.jpg";
					
					list.add(new FlickrImage(link, photo, jsonPInfos.optString("description"), hashtags));
				}
				inputStream.close();
			}
		}
		return list;
	}
	
	public void getFlickrRessourcesWithSave()	throws IOException, JSONException {

	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
				new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
		}
		
		String baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
		String query = "&has_geo=true&per_page=25&api_key="+ FlickrConstantes.APIKEY;

		if (text != null) 
			query = query.concat("&text='" + URLEncoder.encode(text + "'", "UTF-8"));
		if (latitude != null && longitude != null)
			query = query.concat("&lat=" + latitude + "&lon=" + longitude + "&radius=" + distance);
		
		String fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1";
		URL fullUrl = new URL(fullUrlStr);
		InputStream inputStream = fullUrl.openStream();
		JSONObject result = new JSONObject(new JSONTokener(inputStream));
		int count = result.getJSONObject("photos").optInt("perpage");
		int numberOfPage = result.getJSONObject("photos").optInt("pages");
		for (int k=0; k < numberOfPage; k++) {
			
			if (k != 0) {
				baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
				query = "&has_geo=true&per_page=25&api_key="+ FlickrConstantes.APIKEY;
				fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1&page=" + new Integer(k+1);
				System.out.println(fullUrlStr);
				fullUrl = new URL(fullUrlStr);
				inputStream = fullUrl.openStream();
				result = new JSONObject(new JSONTokener(inputStream));
				count = result.getJSONObject("photos").optInt("perpage");
			}
			
			
			System.out.println("Page [" + new Integer(k+1) + "/" + numberOfPage + "]");
			if (count > 0 ) {
				JSONArray jsonPhotos = result.getJSONObject("photos").getJSONArray("photo");
				for (int i = 0; i < count; i++) {
					ArrayList<String> hashtags = new ArrayList<String>();
					JSONObject jsonPhoto = (JSONObject) jsonPhotos.opt(i);
					String id = jsonPhoto.optString("id");
					// ------------------------------------
					baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.getInfo";
					query = "&photo_id=" + id + "&api_key=" + FlickrConstantes.APIKEY;
	
					fullUrlStr = baseUrl + query + "&format=json&nojsoncallback=1";
					fullUrl = new URL(fullUrlStr);
					inputStream = fullUrl.openStream();
		
					result = new JSONObject(new JSONTokener(inputStream));
					JSONObject jsonPInfos = result.getJSONObject("photo");
					if (!jsonPInfos.isNull("tags")) {
						JSONArray tags = jsonPInfos.getJSONObject("tags").getJSONArray("tag");
						for (int j = 0; j < tags.length(); j++) {
							JSONObject tag = tags.getJSONObject(j);
							hashtags.add(tag.optString("raw"));
						}
					}
					
					String link = jsonPInfos.getJSONObject("urls").getJSONArray("url").getJSONObject(0).optString("_content");
					System.out.println("Get the pictures from Flickr... [" + new Integer(i+1) + "/" + count + "]");
					// ------------------------------------
					
					int farm = jsonPInfos.optInt("farm");
					String server = jsonPhoto.optString("server");
					String secret = jsonPInfos.optString("secret");
					String originalsecret = jsonPInfos.optString("originalsecret");
					String originalformat = jsonPInfos.optString("originalformat");
					
					String photo = "";
					if (jsonPInfos.getJSONObject("usage").optString("candownload").equals("1"))
						photo = "https://farm"+farm+".staticflickr.com/"+ server + "/" + id + "_"+ originalsecret + "_o."+ originalformat;
					else 
						photo = "https://farm"+farm+".staticflickr.com/"+ server + "/" + id + "_"+ secret + "_b.jpg";
										
					FlickrImage image = new FlickrImage(link, photo, jsonPInfos.optJSONObject("description").optString("_content"), hashtags);
					saveFlickrImage(image); // Download image
					saveJSON(image); // Save json
				}
				inputStream.close();
			}
		}
	}
	
	public void saveFlickrImage(FlickrImage fkImage) throws IOException, JSONException {
	
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }

		URL url = new URL(fkImage.getPhoto());
		BufferedImage image = ImageIO.read(url);
		ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + fkImage.getFileName().concat("jpg")));
	}
	
	public void saveJSON(FlickrImage image) {
		image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
	}

	public List<FlickrImage> saveFlickrImages() throws IOException, JSONException {
		ArrayList<FlickrImage> list = getFlickrRessources();
		
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		
		for (FlickrImage tw : list){
			URL url = new URL(tw.getPhoto());
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + tw.getFileName().concat("jpg")));
		}
	    return list;
	}
		
	public void saveJSONList(List<FlickrImage> list) {
		for (FlickrImage image : list){
			image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
		}
	}
}
