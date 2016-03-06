package map;

import java.awt.geom.Point2D;

import utils.GlobalesConstantes;
import utils.MongoDB;

public class Map {
	public static void main(String args[]) throws Exception {
		
		MongoDB.generateMapByTimeWindows(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONTWITTERFDL2015, "./web/data_tw.js", -1, -1);
		// MongoDB.generateMapByTimeWindows(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONTWITTERFDL2015, "./web/data_tw.js", 4, 8);
		
		Point2D.Double pointSW = new Point2D.Double(45.749528, 4.834993);
		Point2D.Double pointNE = new Point2D.Double(45.760457, 4.851172);
		
		MongoDB.generateMapByGeolocalisedZone(GlobalesConstantes.DBNAME, GlobalesConstantes.DBCOLLECTIONTWITTERFDL2015, "./web/data_z.js", pointSW, pointNE);
	}
}
	