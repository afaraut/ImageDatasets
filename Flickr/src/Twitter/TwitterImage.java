package Twitter;

public class TwitterImage {

	private String id;
	private String server;
	private String originalsecret;
	private String originalformat;
	private String secret;
	private String candownload;

	public TwitterImage(String id, String server, String secret,
			String originalsecret, String originalformat, String candownload) {
		this.id = id;
		this.server = server;
		this.secret = secret;
		this.originalsecret = originalsecret;
		this.originalformat = originalformat;
		this.candownload = candownload;
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

	public String toString() {
		return id + " - " + server + " - " + secret;
	}

}