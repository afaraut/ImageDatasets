package Twitter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TwitterSearch extends TwitterUtil {

	private String repertoire;
	private String requete;
	// filter=images get only tweets having images in OPTIONS
	//
	public static final String OPTIONS =  "count=100&include_entities=1&exclude=retweets";
	
	public TwitterSearch (String repertoire, String text){
		this.repertoire = repertoire;				
		try {
			if (text != null) {
				requete = new URI("https", "api.twitter.com", "/1.1/search/tweets.json", OPTIONS + "&q=" + text, null).toASCIIString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(requete);
	}
	
	public TwitterSearch (String repertoire, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
				
		try {
			if (latitude != null && longitude != null && distance != null) {
				requete = new URI("https", "api.twitter.com", "/1.1/search/tweets.json", OPTIONS + "&geocode=" + latitude + "," + longitude + "," + distance + "km", null).toASCIIString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(requete);
	}
		
	public TwitterSearch (String repertoire, String text, Double latitude, Double longitude, Double distance){
		this.repertoire = repertoire;
		
		try {
			if (latitude != null && longitude != null && distance != null && text != null) {
				requete = new URI("https", "api.twitter.com", "/1.1/search/tweets.json", OPTIONS + "&geocode=" + latitude + "," + longitude + "," + distance + "km"+ "&q=" + text,  null).toASCIIString();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(requete);
	}
			
	/**
	 * Try to get all the media from each page
	 * @throws JSONException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void getTwitterRessources () throws JSONException, URISyntaxException, IOException {
					
		JSONObject result = makeGetRequestJSONObject(requete);
		//System.out.println(result);
		if (!result.isNull("errors")) {
			/*JSONArray errors = result.getJSONArray("errors");
			for (int i=0; i < errors.length(); i++) {
				String code = ((JSONObject) errors.get(i)).optString("code");
				String message = ((JSONObject) errors.get(i)).optString("message");
				System.out.println();
			}*/
			System.out.println("ERROR");
			return;
			//System.out.println("Too many requests ! You have to wait 15 minutes");
		}
		
		JSONArray tweets = result.getJSONArray("statuses");
		String next_results = null;
		if (!result.isNull("search_metadata") && !result.getJSONObject("search_metadata").isNull("next_results")) {
			next_results = result.getJSONObject("search_metadata").optString("next_results");
		}
		int nombreDeTweet = tweets.length();
		System.out.println("nombreDeTweet " + nombreDeTweet);
		HashMap<String, TwitterImage> hashMapTweets = new HashMap<String, TwitterImage>();
	
		do {
			String idList = new String();
			for (int i = 0; i < nombreDeTweet; i++) {
				if (!result.isNull("errors")) { // CHECK THE ERROR CODE, because it can be a new error
					System.out.println("Too many requests ! You have to wait 15 minutes");
					return;
				}
				
				JSONObject tweet = (JSONObject) tweets.opt(i);
				String tweetID = getTweetID(tweet);
				TwitterImage image = new TwitterImage(repertoire, getTweetURL(tweet), getTweetText(tweet), tweetID, getAllHashTag(tweet));
				
				if (!tweet.getJSONObject("entities").isNull("media")) { // If there is media
					idList = idList.concat(tweetID + ",");
					hashMapTweets.put(tweetID, image);
				}
				else {
					saveJSON(image); // Save json
				}
			}
			
			int idListLength = idList.length();
			if (idListLength > 0) { // If there is media
				idList =  idList.substring(0, idListLength-1); // Remove the last ","
				getAllMedia(hashMapTweets, idList);
			}
			
			if (next_results != null) { // There is still tweets
				requete = "https://api.twitter.com/1.1/search/tweets.json" + next_results;
				System.out.println("Request for a new page ... ");
				result = makeGetRequestJSONObject(requete);
				tweets = result.getJSONArray("statuses");					
				nombreDeTweet = tweets.length();
				if (!result.getJSONObject("search_metadata").isNull("next_results")){
					next_results = result.getJSONObject("search_metadata").optString("next_results");
				}
				else {
					next_results = null;
				}
			}
		} while (next_results != null);
		System.out.println("End of the results");

	}
}
