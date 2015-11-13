package Instagram;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import Utils.GlobalesConstantes;
import Utils.MongoDB;
import Utils.Toolbox;

public class InstagramImage {
	
	private String repertoire;
	//private String link;
	private String filename;
	private String photo;
	//private List<String> hashtags;
	private JSONObject objson;
	
	/*
	public InstagramImage(String link, String photo, List<String> hashtags){
		this.link = link;
		this.photo = photo;
		String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = tmp_str.substring(0, tmp_str.length()-3);
		this.hashtags = hashtags;
		this.objson = null;
	}*/
	
	public InstagramImage(JSONObject tweet, String repertoire, String photo){
		
		this.repertoire = repertoire;
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
		this.objson = tweet;

		String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = tmp_str.substring(0, tmp_str.length()-3);
		this.photo = photo;
	}
	
	/*public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("link", link);
		objson.put("photo", photo);
		objson.put("hashtags", hashtags);
		return objson;
	}*/
	
	public void saveJSON_DB(){
		MongoDB.insert(GlobalesConstantes.DBCOLLECTIONINSTAGRAM, objson);
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
	/*
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