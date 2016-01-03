package Instagram;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import Utils.GlobalesConstantes;
import Utils.MongoDB;
import Utils.Toolbox;

public class InstagramMedia {
	
	private String repertoire;
	//private String link;
	private String filename;
	//private String photo;
	//private String video;
	//private List<String> hashtags;
	private JSONObject objson;
	private ArrayList<String> media;
	
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
	
	public InstagramMedia(JSONObject post, String repertoire/*, String photo, String video*/){
		
		this.repertoire = repertoire;
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
		this.objson = post;

		if (!post.isNull("id")){
			this.filename = post.optString("id");
		}
		else {
			this.filename = "errorfilename";
		}
		media = new ArrayList<String>();
	}
	
	public void addMedium(String medium){
		this.media.add(medium);
	}
	
	/*public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("link", link);
		objson.put("photo", photo);
		objson.put("hashtags", hashtags);
		return objson;
	}*/
	
	public void saveJSON_DB(){
		MongoDB.insert(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONINSTAGRAM, objson);
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
	/*
	public String getLink() {
		return link;
	}

	public List<String> getHashtags() {
		return hashtags;
	}*/

	public ArrayList<String> getMedia() {
		return media;
	}
	
	public String getDirectory() {
		return repertoire;
	}
	
	public String getFileName(){
		return filename;
	}
}