package utils;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static java.util.Arrays.asList;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MongoDB {
	private static MongoClient mongoClient;
	private static MongoCollection<Document> collection;
	
	public static void connection(String dataBaseName, String collectionName){
		mongoClient = new MongoClient(GlobalesConstantes.SERVER , GlobalesConstantes.PORT );
		MongoDatabase db = mongoClient.getDatabase(dataBaseName);
		collection = db.getCollection(collectionName);
	}
	
	public static void insert(String dataBaseName, String collectionName, JSONObject object) {
		connection(dataBaseName, collectionName);
		collection.insertOne(Document.parse(object.toString()));
		close();
	}
	
	public static void generateMap (String dataBaseName, String collectionName, String jsonPathName){
		connection(dataBaseName, collectionName);
		
		AggregateIterable<Document> iterable = collection.aggregate(asList(
		new Document("$match", new Document("geo", new Document("$ne", null))),
		new Document("$project", new Document("geo", 1).append("user.id_str",1)),
		new Document("$group", new Document("_id", "$user.id_str").append("count", new Document("$sum", 1))),
		new Document("$sort", new Document("count", -1))));
				
		final JSONObject allPersonPlaces = new JSONObject();
		
		iterable.forEach(new Block<Document>() {
		    public void apply(Document document) {
		    	if (document.getInteger("count") > 3){
			        
					Document whereQuery = new Document("user.id_str", new Document("$eq", document.getString("_id")));
					
					FindIterable<Document> messagesByUser = collection.find(whereQuery);
					final Set<Point2D> setPoint2D = new HashSet<Point2D>();		
					
					final JSONObject person = new JSONObject();
					person.accumulate("user", messagesByUser.first().get("user"));
					
					messagesByUser.forEach(new Block<Document>() {
					    public void apply(Document document) {
					        
					    	if (document.containsKey("geo") && document.containsKey("text")
					    			&& document.containsKey("link") && document.containsKey("created_at")
					    			&& document.containsKey("id_str") && document.containsKey("lang")
					    			&& document.containsKey("timestamp_ms")){
					    		
					    		Document geo = (Document)document.get("geo");
					    		if (geo.containsKey("coordinates")) {
					    			
							    	@SuppressWarnings("unchecked")
									ArrayList<Double> coordinates = (ArrayList<Double>)geo.get("coordinates");
							    	
							        Double latitude = coordinates.get(0);
							        Double longitude = coordinates.get(1);
							        Point2D.Double point = new Point2D.Double(latitude, longitude);
							        
							        if (!setPoint2D.contains(point)){ // In order to remove duplicates 
								        setPoint2D.add(point);
								        
								        JSONObject place = new JSONObject();
								        
								        JSONObject geolocation = new JSONObject();
								        geolocation.accumulate("latitude", latitude);
								        geolocation.accumulate("longitude", longitude);
								        
								        place.accumulate("geo", geolocation);
								        place.accumulate("text", document.get("text"));
								        place.accumulate("link", document.get("link"));
								        place.accumulate("created_at", document.get("created_at"));
								        place.accumulate("id_str", document.get("id_str"));
								        place.accumulate("lang", document.get("lang"));
								        place.accumulate("timestamp_ms", document.get("timestamp_ms"));
								        
								        if (document.containsKey("extended_entities")){
								        	place.accumulate("extended_entities", document.get("extended_entities"));
								        }
								        if (document.containsKey("entities")){
								        	place.accumulate("entities", document.get("entities"));
								        }
								        
								        person.append("places", place);
							        }
					    		}
					    	}			        
					    }
					});
					if (person.getJSONArray("places").length() > 1) { // Keep more than 1 place
						allPersonPlaces.append("points", person);
					}
		    	}
		    }
		});
		
		FileWriter file = null;
        try {
        	file = new FileWriter(jsonPathName);        	 
            file.write(Toolbox.toPrettyFormat(allPersonPlaces.toString()));
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
 
        } finally {
            try {
				file.flush();
				file.close();
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
			}
        }		
		close();
	}
	
	public static MongoCollection<Document> getCollection(){
		return collection;
	}
		
	public static void close(){
		mongoClient.close();
	}
}
