package utils;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



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
	
	public static MongoCollection<Document> getCollection(){
		return collection;
	}
		
	public static void close(){
		mongoClient.close();
	}
}
