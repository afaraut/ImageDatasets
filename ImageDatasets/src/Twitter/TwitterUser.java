package Twitter;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitterUser {
	private String id;
	private String name;
	private String screen_name;
	private String location;
	private String url;
	private String description;
	private int followers_count;
	private int friends_count;
	private int statuses_count;
	private String created_at;
	private String lang;
	
	public TwitterUser(String id, String name, String screen_name, String location, String url, String description, int followers_count, int friends_count, int statuses_count, String created_at, String lang) {
		this.id = id;
		this.name = name;
		this.screen_name = screen_name;
		this.location = location;
		this.url = url;
		this.description = description;
		this.followers_count = followers_count;
		this.friends_count = friends_count;
		this.statuses_count = statuses_count;
		this.created_at = created_at;
		this.lang = lang;
	}
	
	public JSONObject formatToJSON(){
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("name", name);
			obj.put("screen_name", screen_name);
			obj.put("location", location);
			obj.put("url", url);
			obj.put("description", description);
			obj.put("followers_count", followers_count);
			obj.put("friends_count", friends_count);
			obj.put("statuses_count", statuses_count);
			obj.put("created_at", created_at);
			obj.put("lang", lang);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
