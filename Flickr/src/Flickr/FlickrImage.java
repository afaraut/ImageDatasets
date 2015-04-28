package Flickr;

public class FlickrImage {

	private String id;
	private String server;
	private String originalsecret;
	private String originalformat;
	private String secret;
	private String candownload;
	private int farm; 

	public FlickrImage(String id, String server, String secret,
			String originalsecret, String originalformat, String candownload, int farm) {
		this.id = id;
		this.server = server;
		this.secret = secret;
		this.originalsecret = originalsecret;
		this.originalformat = originalformat;
		this.candownload = candownload;
		this.farm = farm;
	}

	public String getId() {
		return id;
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

	public String toString() {
		return id + " - " + server + " - " + secret;
	}

}