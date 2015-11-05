package Twitter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Utils.GlobalesConstantes;

public class TwitterImage {
	
	private String repertoire;
	private String link;
	private String filename;
	private String text;
	private String photo;
	private TwitterUser twitterUser;
	private List<String> hashtags;
	private JSONObject objson;

	public TwitterImage(TwitterUser twitterUser, String repertoire, String link, String text, String id, List<String> hashtags){
		
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
	}
	
	public void setPhoto(String photo){
		this.photo = photo;
		String tmp[] = photo.split("/");
		String tmp_str = tmp[tmp.length-1];
		this.filename = this.filename.concat("_" + tmp_str.substring(0, tmp_str.length()-4));
	}
	
	
	/*public TwitterImage(String link, List<String> hashtags, String photo) {
		
		this.repertoire = repertoire;
	    if(!new File(GlobalesConstantes.REPERTOIRE + repertoire).exists()){
			new File(GlobalesConstantes.REPERTOIRE + repertoire).mkdirs();
	    }
		
		this.link = link;
		this.photo = photo;
		if (photo != null) {
			String tmp[] = photo.split("/");
			String tmp_str = tmp[tmp.length-1];
			this.filename = tmp_str.substring(0, tmp_str.length()-3);
		}
		else {
			System.out.println(link);
			String tmp[] = link.split("/");
			String tmp_str = tmp[tmp.length-1];
			this.filename = tmp_str + ".";
		}
		this.hashtags = hashtags;
		this.objson = null;
	}*/

	public static String toPrettyFormat(String jsonString) 
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }
	
	public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("user", twitterUser.formatToJSON());
		objson.put("link", link);
		objson.put("photo", photo);
		objson.put("text", text);
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
            file.write(toPrettyFormat(objson.toString()));
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
		
		tmp = tmp.concat("[");
		if (hashtags.size() > 0) {
			for (String s : hashtags)
				tmp = tmp.concat(s + ", ");
			tmp = tmp.substring(0, tmp.length()-2);
		}
		tmp = tmp.concat("]\n");
		tmp = tmp.concat("photo : " + photo);
		return tmp;
	}

}