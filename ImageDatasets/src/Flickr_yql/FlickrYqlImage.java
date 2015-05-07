package Flickr_yql;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class FlickrYqlImage {

	private String link;
	private String filename;
	private String description;
	private String photo;
	private List<String> hashtags;
	private JSONObject objson;
	
	public FlickrYqlImage(String link, String photo, String description, List<String> hashtags) {
		this.link = link;
		this.description = description;
		this.photo = photo;
		String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = tmp_str.substring(0, tmp_str.length()-3);
		this.hashtags = hashtags;
		this.objson = null;
	}

	public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("link", link);
		objson.put("description", description);
		objson.put("photo", photo);
		objson.put("hashtags", hashtags);
		return objson;
	}
	
	public void saveJSON(String filename){
		try {
			generateJSON();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		FileWriter file = null;
        try {
        	file = new FileWriter(filename);
            file.write(objson.toString());
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
            try {
				file.flush();
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLink() {
		return link;
	}
	
	public List<String> getHashtags() {
		return hashtags;
	}
	
	public String getPhoto() {
		return photo;
	}

	public String getFileName(){
		return filename;
	}

	public String toString() {
		String tmp = link + "\n";
		tmp = tmp.concat("Description : " +  description + "\n\n");
		tmp = tmp.concat("[");
		if (hashtags.size() > 0) {
			for (String s : hashtags)
				tmp = tmp.concat(s + ", ");
			tmp = tmp.substring(0, tmp.length()-2);
		}
		tmp = tmp.concat("]\n");
		return tmp;
	}

}