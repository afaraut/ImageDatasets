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
	
	public FlickrSearch (String repertoire, String text, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private ArrayList<FlickrImage> getFlickrRessources()	throws IOException, JSONException {
		ArrayList<FlickrImage> list = new ArrayList<FlickrImage>();
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
		String query = "select * from flickr.photos.search where has_geo='true' and api_key="+ FlickrConstantes.APIKEY;
				
		if (text != null) 
			query = query.concat(" and text='" + text + "'");
		if (latitude != null)
			query = query.concat(" and lat='" + latitude + "'");
		if (longitude != null)
			query = query.concat(" and lon='" + longitude + "'");
		
		query = query.concat(";");	
		String fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8")	+ "&format=json";
		URL fullUrl = new URL(fullUrlStr);
		InputStream inputStream = fullUrl.openStream();

		JSONObject result = new JSONObject(new JSONTokener(inputStream));
		int count = result.getJSONObject("query").optInt("count");

		JSONArray jsonPhotos = result.getJSONObject("query").getJSONObject("results").getJSONArray("photo");
		for (int i = 0; i < count; i++) {
			ArrayList<String> hashtags = new ArrayList<String>();
			JSONObject jsonPhoto = (JSONObject) jsonPhotos.opt(i);
			String id = jsonPhoto.optString("id");
			// ------------------------------------
			query = "select * from flickr.photos.info where photo_id='" + id + "' and api_key=" + FlickrConstantes.APIKEY + ";";
			
			fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8")+ "&format=json";
			fullUrl = new URL(fullUrlStr);
			inputStream = fullUrl.openStream();

			result = new JSONObject(new JSONTokener(inputStream));
			JSONObject jsonPInfos = result.getJSONObject("query").getJSONObject("results").getJSONObject("photo");
			System.out.println(result);
			if (!jsonPInfos.isNull("tags")) {
				JSONArray tags = jsonPInfos.getJSONObject("tags").getJSONArray("tag");
				for (int j = 0; j < tags.length(); j++) {
					JSONObject tag = tags.getJSONObject(j);
					hashtags.add(tag.optString("raw"));
				}
			}
			
			String link = jsonPInfos.getJSONObject("urls").getJSONObject("url").optString("content");
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
		return list;
	}

	public List<FlickrImage> getFlickrImages() throws IOException, JSONException {
		ArrayList<FlickrImage> list = getFlickrRessources();
		
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			// Créer le dossier avec tous ses parents
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		
		for (FlickrImage tw : list){
			URL url = new URL(tw.getPhoto());
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + tw.getFileName().concat("jpg")));
		}
	    return list;
	}
	
	public void saveJSON(List<FlickrImage> list) {
		for (FlickrImage image : list){
			image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
		}
	}
}
