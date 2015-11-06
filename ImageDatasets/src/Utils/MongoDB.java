package Utils;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class MongoDB {
	private static MongoClient mongoClient;
	private static MongoCollection<Document> collection;
	
	private static void connection(){
		mongoClient = new MongoClient(GlobalesConstantes.SERVER , GlobalesConstantes.PORT );
		MongoDatabase db = mongoClient.getDatabase(GlobalesConstantes.DBNAME);
		collection = db.getCollection(GlobalesConstantes.DBCOLLECTION);
	}
	
	public static void insert(JSONObject object) throws JSONException{
		connection();
		collection.insertOne(Document.parse(object.toString()));
		close();
	}
	
	private static void close(){
		mongoClient.close();
	}
}
