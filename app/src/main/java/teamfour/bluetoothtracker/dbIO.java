package teamfour.bluetoothtracker;

import android.os.AsyncTask;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * Created by Surya Raman on 12-03-2017.
 */

public abstract class dbIO
{

        MongoClientURI uri = new MongoClientURI( "" );
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase db = mongoClient.getDatabase(uri.getDatabase());



}
