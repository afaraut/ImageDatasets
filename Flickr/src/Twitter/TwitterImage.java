package Twitter;

import java.util.List;

public class TwitterImage {

	private String link;
	private List<String> hashtags;
	private List<String> photos;

	public TwitterImage(String link, List<String> hashtags, List<String> photos) {
		this.link = link;
		this.hashtags = hashtags;
		this.photos = photos;
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
		for (String s : hashtags)
			tmp = tmp.concat(s + ", ");
		tmp = tmp.substring(0, tmp.length()-2);
		tmp = tmp.concat("]\n");
		tmp = tmp.concat("[");
		for (String s : photos)
			tmp = tmp.concat(s + ", ");
		tmp = tmp.substring(0, tmp.length()-2);
		tmp = tmp.concat("]\n");
		
		return tmp;
	}

}