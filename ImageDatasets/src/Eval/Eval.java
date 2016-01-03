package Eval;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import Utils.GlobalesConstantes;
import Utils.GlobalesConstantes.SOCIAL_NETWORK;
import Utils.MongoDB;

public class Eval {
	
	public static void makeEvalDB(final GlobalesConstantes.SOCIAL_NETWORK socialNetwork) {
		
		if (socialNetwork == SOCIAL_NETWORK.FLICKR) {
			
		}
		else if (socialNetwork == SOCIAL_NETWORK.INSTAGRAM) {	
			MongoDB.connection(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONINSTAGRAM);
			FindIterable<Document> documents = MongoDB.getCollection().find();
		    documents.forEach(new Block<Document>() {
				@Override
			    public void apply(final Document document) {
					if (document.containsKey("_id") && document.containsKey("images") 
							&& ((Document)document.get("images")).containsKey("standard_resolution") 
							&& ((Document)((Document)document.get("images")).get("standard_resolution")).containsKey("url")) {
						ObjectId id = document.getObjectId("_id");
						//String imagePath = GlobalesConstantes.REPERTOIRE + GlobalesConstantes.DBCOLLECTIONINSTAGRAM;
						//String imageFileName = document.getString("id") + "extension";
						String url = ((Document)((Document)document.get("images")).get("standard_resolution")).getString("url");
						String bool = "";
						JSONObject object = new JSONObject();
						try {
							object.accumulate("id", id);
							object.accumulate("url", url);
							object.accumulate("bool", bool);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						MongoDB.insert(GlobalesConstantes.DBNAME + "Eval", GlobalesConstantes.DBCOLLECTIONINSTAGRAM, object);
					}
			    }
		    });
		}
		else if (socialNetwork == SOCIAL_NETWORK.TWITTER) {
			
		}
		
		MongoDB.close();
	}
}
