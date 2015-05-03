package Instagram;

import java.util.List;

public class InstagramImage {

	private String link;
	private String photo;
	private List<String> hashtags;

	public InstagramImage(String link, String photo, List<String> hashtags) {
		this.link = link;
		this.photo = photo;
		this.hashtags = hashtags;

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