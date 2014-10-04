package hello;

import com.mongodb.ReflectionDBObject;

import java.util.HashMap;

/**
 * Created by dirk on 14.09.14.
 */
public class ObjectSchema extends ReflectionDBObject {
    private String name;
    private HashMap<String,FieldSchema> fields;

    public ObjectSchema() {

    }

    public ObjectSchema(String name, HashMap<String,FieldSchema> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, FieldSchema> getFields() {

        return fields;
    }

    public void setFields(HashMap<String, FieldSchema> fields) {
        this.fields = fields;
    }
}
