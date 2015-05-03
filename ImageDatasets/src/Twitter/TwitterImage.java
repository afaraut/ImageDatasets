package Twitter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitterImage {

	private String link;
	private List<String> hashtags;
	private List<String> photos;
	private JSONObject objson;

	public TwitterImage(String link, List<String> hashtags, List<String> photos) {
		this.link = link;
		this.hashtags = hashtags;
		this.photos = photos;
		this.objson = null;
	}

	public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("link", link);
		objson.put("photos", photos);
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
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + objson);
 
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
	
	public String getLink() {
		return link;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public List<String> getPhotos() {
		return photos;
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
		tmp = tmp.concat("[");
		for (String s : photos)
			tmp = tmp.concat(s + ", ");
		tmp = tmp.substring(0, tmp.length()-2);
		tmp = tmp.concat("]\n");
		
		return tmp;
	}

}