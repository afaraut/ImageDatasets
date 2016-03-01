package map;

import utils.GlobalesConstantes;
import utils.MongoDB;

public class Map {
	public static void main(String args[]) throws Exception {
		MongoDB.generateMap(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONTWITTERFDL2015, "./web/data_twitter.json");
	}
}
