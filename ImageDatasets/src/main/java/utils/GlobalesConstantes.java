package utils;

public class GlobalesConstantes {
	public static final String REPERTOIRE = "D:\\ImageDatasets\\";
	
	public static enum SOCIAL_NETWORK { 
        INSTAGRAM ,
        TWITTER ,
        FLICKR
    };
	
	/// ----- Database
	public static final String SERVER = "localhost";
	public static final int PORT = 27017;
	public static final String DBNAME = "ImageDataset";
	public static final String DBCOLLECTIONTWITTER = "Twitter";
	public static final String DBCOLLECTIONINSTAGRAM = "Instagram";
	public static final String DBCOLLECTIONFLICKR = "Flickr";
}