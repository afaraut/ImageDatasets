package Flickr;

import java.util.List;

public class FlickrImage {

	private String id;
	private String description;
	private String link;
	private String server;
	private String originalsecret;
	private String originalformat;
	private String secret;
	private String candownload;
	private int farm; 
	private List<String> hashtags;

	public FlickrImage(String id, String description, String link, String server, String secret,
			String originalsecret, String originalformat, String candownload, int farm, List<String> hashtags) {
		this.id = id;
		this.description = description;
		this.link = link;
		this.server = server;
		this.secret = secret;
		this.originalsecret = originalsecret;
		this.originalformat = originalformat;
		this.candownload = candownload;
		this.farm = farm;
		this.hashtags = hashtags;
	}

	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLink() {
		return link;
	}

	public String getServer() {
		return server;
	}

	public String getOriginalsecret() {
		return originalsecret;
	}

	public String getOriginalformat() {
		return originalformat;
	}

	public String getSecret() {
		return secret;
	}

	public String getCandownload() {
		return candownload;
	}
	
	public int getFarm() {
		return farm;
	}
	
	public List<String> getHashtags() {
		return hashtags;
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
		return tmp + id + " - " + server + " - " + secret;
	}

}