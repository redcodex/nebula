package hello;

import com.mongodb.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dirk on 04.10.14.
 */
@Service
public class MongoDBService {
    private MongoClient mongoClient;
    private DB db;

    @PostConstruct
    public void doInit() {

        try {
            this.mongoClient = new MongoClient("localhost");
            this.db = mongoClient.getDB("mydb");

        } catch ( UnknownHostException e ) {
            throw new RuntimeException(e);
        }
    }

    public DBCollection getCollection(String collection) {
        return this.db.getCollection(collection);
    }

    public List<DBObject> getCollectionDocuments(String collectionName) {
        List<DBObject> documents = new LinkedList<>();
        DBCollection collection = this.getCollection(collectionName);

        DBCursor cursor = collection.find();
        try {
            while(cursor.hasNext()) {
                documents.add((DBObject)cursor.next());
            }
        } finally {
            cursor.close();
        }

        return documents;
    }
}
