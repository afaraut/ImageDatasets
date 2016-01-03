package Flickr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import Utils.GlobalesConstantes;
import Utils.MongoDB;
import Utils.Toolbox;

public class FlickrImage {

	private String repertoire;
	//private String link;
	private String filename;
	private String photo;
	//private List<String> hashtags;
	private JSONObject objson;
	
	/*public FlickrImage(String link, String photo, String description, List<String> hashtags) {
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
	}*/
	
	public FlickrImage(JSONObject tweet, String repertoire, String photo){
		
		this.repertoire = repertoire;
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
		this.objson = tweet;
		addIntoJSON("url_photo", photo);
		String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = tmp_str.substring(0, tmp_str.length()-3);
		this.photo = photo;
	}
	
	public void addIntoJSON(String key, Object json) {
		try {
			this.objson.accumulate(key, json);
		} catch (IllegalArgumentException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void saveJSON_DB(){
		MongoDB.insert(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONFLICKR, objson);
	}
	
	public void saveJSON_FILE(String filename){
		/*try {
			generateJSON();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}*/
		FileWriter file = null;
        try {
        	file = new FileWriter(filename);        	 
            file.write(Toolbox.toPrettyFormat(objson.toString()));
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
 
        } finally {
            try {
				file.flush();
				file.close();
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	/*public String getDescription() {
		return description;
	}
	
	public String getLink() {
		return link;
	}
	
	public List<String> getHashtags() {
		return hashtags;
	}*/
	
	public String getDirectory() {
		return repertoire;
	}
	
	public String getPhoto() {
		return photo;
	}

	public String getFileName(){
		return filename;
	}
}