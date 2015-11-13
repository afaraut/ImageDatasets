package Twitter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import Utils.GlobalesConstantes;
import Utils.MongoDB;
import Utils.Toolbox;

public class Tweet extends TwitterUtil {
	
	private String repertoire;
	//private String link;
	private String filename;
	//private String text;
	//private String photo;
	//private JSONObject twitterUser;
	//private List<String> hashtags;
	private JSONObject objson;
	private ArrayList<String> photos;

	/*public Tweet(JSONObject twitterUser, String repertoire, String link, String text, String id, List<String> hashtags){
		
		this.twitterUser = twitterUser;
		this.repertoire = repertoire;
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
		this.link = link;
		this.hashtags = hashtags;
		this.objson = null;
		this.text = text;
		// Or the id by param
		//String tmp[] = link.split("/");
		//this.filename = tmp[tmp.length-1] + ".";
		this.filename = id;
	}*/
	
	public Tweet(JSONObject tweet, String repertoire, String link){
		
		this.repertoire = repertoire;
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
		this.objson = tweet;
		try {
			this.objson.append("link", link);
			this.filename = getTweetID(tweet);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		photos = new ArrayList<String>();
	}
	
	/*public void setPhoto(String photo){
		this.photo = photo;
		String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = this.filename.concat("_" + tmp_str.substring(0, tmp_str.length()-4));
	}*/
	
	public void addPhoto(String photo){
		this.photos.add(photo);
		
		
		/*String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = this.filename.concat("_" + tmp_str.substring(0, tmp_str.length()-4));*/
	}
	
	public void addIntoJSON(String key, Object json) {
		try {
			this.objson.append(key, json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("user", twitterUser);
		objson.put("link", link);
		objson.put("photo", photo);
		objson.put("text", text);
		objson.put("hashtags", hashtags);
		return objson;
	}*/
	
	public void saveJSON_DB(){
		MongoDB.insert(GlobalesConstantes.DBCOLLECTIONTWITTER, objson);
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
	
	public String getDirectory() {
		return repertoire;
	}
	
	/*public String getLink() {
		return link;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public String getPhoto() {
		return photo;
	}*/
	
	public ArrayList<String> getPhotos() {
		return photos;
	}
	
	public String getFileName(){
		return filename;
	}
}