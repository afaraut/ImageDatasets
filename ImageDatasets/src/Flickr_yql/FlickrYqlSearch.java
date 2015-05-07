package Flickr_yql;

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

public class FlickrYqlSearch {

	private String repertoire;
	private String text;
	private Double latitude;
	private Double longitude;
	private Double distance;
	
	public FlickrYqlSearch (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
	}
	
	private ArrayList<FlickrYqlImage> getFlickrRessources()	throws IOException, JSONException {
		ArrayList<FlickrYqlImage> list = new ArrayList<FlickrYqlImage>();
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
		String query = "select * from flickr.photos.search where has_geo='true' and api_key="+ FlickrYqlConstantes.APIKEY;
		// /!\ get only 10 pictures due to the yql query
		if (text != null) 
			query = query.concat(" and text='" + text + "'");
		if (latitude != null && longitude != null)
			query = query.concat(" and lat='" + latitude + "' and lon='" + longitude + "' and radius=" + distance);
		
		query = query.concat(";");	
		String fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8")	+ "&format=json";
		URL fullUrl = new URL(fullUrlStr);
		InputStream inputStream = fullUrl.openStream();

		JSONObject result = new JSONObject(new JSONTokener(inputStream));
		int count = result.getJSONObject("query").optInt("count");
		System.out.println(result);
		if (count > 0 ) {
			JSONArray jsonPhotos = result.getJSONObject("query").getJSONObject("results").getJSONArray("photo");
			for (int i = 0; i < count; i++) {
				ArrayList<String> hashtags = new ArrayList<String>();
				JSONObject jsonPhoto = (JSONObject) jsonPhotos.opt(i);
				String id = jsonPhoto.optString("id");
				// ------------------------------------
				query = "select * from flickr.photos.info where photo_id='" + id + "' and api_key=" + FlickrYqlConstantes.APIKEY + ";";
				
				fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8")+ "&format=json";
				fullUrl = new URL(fullUrlStr);
				inputStream = fullUrl.openStream();
	
				result = new JSONObject(new JSONTokener(inputStream));
				JSONObject jsonPInfos = result.getJSONObject("query").getJSONObject("results").getJSONObject("photo");
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
				
				list.add(new FlickrYqlImage(link, photo, jsonPInfos.optString("description"), hashtags));
			}
			inputStream.close();
		}
		return list;
	}

	public List<FlickrYqlImage> getFlickrImages() throws IOException, JSONException {
		ArrayList<FlickrYqlImage> list = getFlickrRessources();
		
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		
		for (FlickrYqlImage tw : list){
			URL url = new URL(tw.getPhoto());
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image,"jpg", new File(GlobalesConstantes.REPERTOIRE + repertoire + tw.getFileName().concat("jpg")));
		}
	    return list;
	}
	
	public void saveJSON(List<FlickrYqlImage> list) {
		for (FlickrYqlImage image : list){
			image.saveJSON(GlobalesConstantes.REPERTOIRE + repertoire + image.getFileName().concat("json"));
		}
	}
}
