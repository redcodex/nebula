package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.util.DBObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dirk on 14.09.14.
 */
@Service
public class SchemaService {
    public Map<String,ObjectSchema> schemas;

    @Inject
    private MongoDBService mongoDBService;

    private static final String MYSCHEMA = "{ \"name\":\"mySchema\", \"fields\":{ \"name\":{\"type\":\"String\", \"required\":true}, \"test\":{\"type\":\"Object\", \"ref\":\"myObject\"}, \"list\":{\"type\":\"List\", \"ref\":\"myList\"} }}";
    private static final String MYOBJECT = "{ \"name\":\"myObject\", \"fields\":{ \"firstname\":{\"type\":\"String\", \"required\":true} }}";
    private static final String MYLIST   = "{ \"name\":\"myList\", \"fields\":{ \"date\":{\"type\":\"Int\", \"required\":true} }}";

    public SchemaService() {
//        schemas = new HashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//
//        System.out.println("mySchema = "+MYSCHEMA);
//        System.out.println("myObject = "+MYOBJECT);
//        System.out.println("myList = "+MYLIST);
//
//        try {
//            schemas.put("mySchema", mapper.readValue(MYSCHEMA, ObjectSchema.class));
//            schemas.put("myObject", mapper.readValue(MYOBJECT, ObjectSchema.class));
//            schemas.put("myList", mapper.readValue(MYLIST, ObjectSchema.class));
//        } catch (IOException e) {
//            System.out.println("Unable to load schemas");
//        }
    }

    @PostConstruct
    private void doInit() {
        this.loadSchemas();
    }

    private void loadSchemas() {
        this.schemas = new HashMap<>();
        for ( ObjectSchema schema : mongoDBService.getCollectionDocuments("schemas", ObjectSchema.class) ) {
            System.out.println(schema);
        }
    }
}
