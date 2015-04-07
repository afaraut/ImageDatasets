package Flickr;
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

public class FlickrSearch {

	private ArrayList<FlickrImage> getFlickrRessources(String text)	throws IOException, JSONException {
		ArrayList<FlickrImage> list = new ArrayList<FlickrImage>();
		String baseUrl = "https://query.yahooapis.com/v1/public/yql?q="; // and text='" + text + "' 
		String query = "select * from flickr.photos.search where has_geo='true' and lat='43.7077201' and lon='7.3343701' and api_key="+ FlickrConstantes.APIKEY + ";";
		String fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8")	+ "&format=json";

		URL fullUrl = new URL(fullUrlStr);
		InputStream inputStream = fullUrl.openStream();

		JSONObject result = new JSONObject(new JSONTokener(inputStream));
		int count = result.getJSONObject("query").optInt("count");

		JSONArray jsonPhotos = result.getJSONObject("query").getJSONObject("results").getJSONArray("photo");
		for (int i = 0; i < count; i++) {
			JSONObject jsonPhoto = (JSONObject) jsonPhotos.opt(i);
			String id = jsonPhoto.optString("id");
			// ------------------------------------
			query = "select * from flickr.photos.info where photo_id='" + id + "' and api_key=" + FlickrConstantes.APIKEY + ";";
			fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8")+ "&format=json";
			fullUrl = new URL(fullUrlStr);
			inputStream = fullUrl.openStream();

			result = new JSONObject(new JSONTokener(inputStream));
			JSONObject jsonPInfos = result.getJSONObject("query").getJSONObject("results").getJSONObject("photo");
			// ------------------------------------
			FlickrImage flickrImage = new FlickrImage(id,
					jsonPhoto.optString("server"),
					jsonPhoto.optString("secret"),
					jsonPInfos.optString("originalsecret"),
					jsonPInfos.optString("originalformat"), jsonPInfos
							.getJSONObject("usage").optString("candownload"));
			list.add(flickrImage);
		}
		inputStream.close();
		return list;
	}

	public void getFlickrImages() throws IOException, JSONException {
		ArrayList<FlickrImage> list = getFlickrRessources("Beaulieu sur mer");
		for (FlickrImage fi : list) {
			if (fi.getCandownload().equals("1")) {
				URL url = new URL("https://farm8.staticflickr.com/"+ fi.getServer() + "/" + fi.getId() + "_"+ fi.getOriginalsecret() + "_o."+ fi.getOriginalformat());
				BufferedImage image = ImageIO.read(url);
				ImageIO.write(image,"jpg",new File("D:\\flickr_test\\" + fi.getId() + "."+ fi.getOriginalformat()));
			} else {
				URL url = new URL("https://farm8.staticflickr.com/"+ fi.getServer() + "/" + fi.getId() + "_"+ fi.getSecret() + "_b.jpg");
				BufferedImage image = ImageIO.read(url);
				ImageIO.write(image, "jpg",new File("D:\\flickr_test\\" + fi.getId() + ".jpg"));
			}
		}
	}

	public static void main(String args[]) throws Exception {
		FlickrSearch m = new FlickrSearch();
		m.getFlickrImages();
	}
}
