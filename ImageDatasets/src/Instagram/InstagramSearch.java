package Instagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
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
	
	public InstagramSearch (String repertoire, String text, Double latitude, Double longitude){
		this.repertoire = repertoire;
		this.text = text;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private ArrayList<InstagramImage> getFlickrRessources()	throws IOException, JSONException {
		ArrayList<InstagramImage> list = new ArrayList<InstagramImage>();
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";
		String query = "select * from flickr.photos.search where has_geo='true' and api_key="+ InstagramConstantes.APIKEY;
				
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
			query = "select * from flickr.photos.info where photo_id='" + id + "' and api_key=" + InstagramConstantes.APIKEY + ";";
			
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
			
			// ------------------------------------
			InstagramImage flickrImage = new InstagramImage(id, jsonPInfos.optString("description"), link,
					jsonPhoto.optString("server"),
					jsonPhoto.optString("secret"),
					jsonPInfos.optString("originalsecret"),
					jsonPInfos.optString("originalformat"), jsonPInfos
							.getJSONObject("usage").optString("candownload"), jsonPInfos.optInt("farm"), hashtags);
			list.add(flickrImage);
		}
		inputStream.close();
		return list;
	}

	public void getFlickrImages() throws IOException, JSONException {
		ArrayList<InstagramImage> list = getFlickrRessources();
		
	     if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			// Créer le dossier avec tous ses parents
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	     }
		
		for (InstagramImage fi : list) {
			if (fi.getCandownload().equals("1")) {
				URL url = new URL("https://farm"+fi.getFarm()+".staticflickr.com/"+ fi.getServer() + "/" + fi.getId() + "_"+ fi.getOriginalsecret() + "_o."+ fi.getOriginalformat());
				BufferedImage image = ImageIO.read(url);
				if (image != null)
					ImageIO.write(image,"jpg",new File(GlobalesConstantes.REPERTOIRE + repertoire + fi.getId() + "."+ fi.getOriginalformat()));
			} else {
				URL url = new URL("https://farm"+fi.getFarm()+".staticflickr.com/"+ fi.getServer() + "/" + fi.getId() + "_"+ fi.getSecret() + "_b.jpg");
				//System.out.println(url);
				BufferedImage image = ImageIO.read(url);
				if (image != null)
					ImageIO.write(image, "jpg",new File(GlobalesConstantes.REPERTOIRE + repertoire + fi.getId() + ".jpg"));
			}
		}
	}
}
