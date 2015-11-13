package Utils;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class MongoDB {
	private static MongoClient mongoClient;
	private static MongoCollection<Document> collection;
	
	private static void connection(String collectionName){
		mongoClient = new MongoClient(GlobalesConstantes.SERVER , GlobalesConstantes.PORT );
		MongoDatabase db = mongoClient.getDatabase(GlobalesConstantes.DBNAME);
		collection = db.getCollection(collectionName);
	}
	
	public static void insert(String collectionName, JSONObject object) {
		connection(collectionName);
		collection.insertOne(Document.parse(object.toString()));
		close();
	}
	
	private static void close(){
		mongoClient.close();
	}
}
