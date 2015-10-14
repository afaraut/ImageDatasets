package Twitter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitterImage {

	private String link;
	private String filename;
	private String photo;
	private List<String> hashtags;
	private JSONObject objson;

	public TwitterImage(String link, List<String> hashtags, String photo) {
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
	}

	public JSONObject generateJSON() throws JSONException {
		objson = new JSONObject();
		objson.put("link", link);
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