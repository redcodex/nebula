package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.util.DBObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dirk on 14.09.14.
 */
@Service
public class SchemaService {
    public Map<String,ObjectSchema> schemas;
    private ObjectSchema objectSchema;
    private ObjectSchema fieldSchema;

    @Inject
    private MongoDBService mongoDBService;

    private static final String MYSCHEMA = "{ \"name\":\"mySchema\", \"fields\":{ \"name\":{\"type\":\"String\", \"required\":true}, \"test\":{\"type\":\"Object\", \"ref\":\"myObject\"}, \"list\":{\"type\":\"List\", \"ref\":\"myList\"} }}";
    private static final String MYOBJECT = "{ \"name\":\"myObject\", \"fields\":{ \"firstname\":{\"type\":\"String\", \"required\":true} }}";
    private static final String MYLIST   = "{ \"name\":\"myList\", \"fields\":{ \"date\":{\"type\":\"Int\", \"required\":true} }}";

    public SchemaService() {
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
        this.initObjectSchema();
        this.initFieldSchema();
        this.loadSchemas();
    }

    private void initObjectSchema() {
        FieldSchema nameSchema = new FieldSchema();
        nameSchema.setType("String");
        nameSchema.setRequired(true);

        FieldSchema fieldsSchema = new FieldSchema();
        fieldsSchema.setType("Map");
        fieldsSchema.setRef("fieldSchema");

        HashMap<String,FieldSchema> fields = new HashMap<>();
        fields.put("name", nameSchema);
        fields.put("fields", fieldsSchema);

        this.objectSchema = new ObjectSchema("objectSchema", fields);
    }

    private void initFieldSchema() {
        FieldSchema typeSchema = new FieldSchema();
        typeSchema.setType("String");
        typeSchema.setRequired(true);

        FieldSchema refSchema = new FieldSchema();
        refSchema.setType("String");

        FieldSchema requiredSchema = new FieldSchema();
        requiredSchema.setType("Boolean");

        FieldSchema getterSchema = new FieldSchema();
        getterSchema.setType("String");

        FieldSchema setterSchema = new FieldSchema();
        setterSchema.setType("String");

        FieldSchema validatorSchema = new FieldSchema();
        validatorSchema.setType("String");

        FieldSchema postSetValidatorSchema = new FieldSchema();
        postSetValidatorSchema.setType("String");

        FieldSchema defaultValueSchema = new FieldSchema();
        defaultValueSchema.setType("Object");

        HashMap<String,FieldSchema> fields = new HashMap<>();
        fields.put("type", typeSchema);
        fields.put("ref", refSchema);
        fields.put("required", requiredSchema);
        fields.put("getter", getterSchema);
        fields.put("setter", setterSchema);
        fields.put("validator", validatorSchema);
        fields.put("postSetValidator", postSetValidatorSchema);
        fields.put("defaultValue", defaultValueSchema);

        this.fieldSchema = new ObjectSchema("fieldSchema", fields);
    }

    public void transformDocument(DBObject document, ObjectSchema documentSchema) {
        Set<String> documentFields = new HashSet<>(document.keySet());

        System.out.println(document);

        for ( String fieldName : documentSchema.getFields().keySet() ) {
            FieldSchema fieldSchema = documentSchema.getFields().get(fieldName);

            System.out.println(fieldSchema);

            if ( !documentFields.contains(fieldName) ) {
                if ( fieldSchema.isRequired() ) {
                    throw new RuntimeException("required field " + fieldName + " of schema " + documentSchema.getName() + " missing!");
                } else {
                    Object defaultValue = fieldSchema.getDefaultValue();
                    if ( defaultValue != null ) {
                        document.put(fieldName, defaultValue);
                    }
                    continue;
                }
            } else {
                documentFields.remove(fieldName);
            }

            System.out.println(document);

            switch (fieldSchema.getType().toLowerCase() ) {
                case "string":
                    break;
                case "integer":
                    break;
                case "double":
                    break;
                case "long":
                    break;
                case "boolean":
                    break;
                case "object":
                    if ( fieldSchema.getRef() == null ) {
                        throw new RuntimeException("Object type without reference for field "+fieldName);
                    }
                    if ( !this.schemas.containsKey(fieldSchema.getRef()) ) {
                        throw new RuntimeException("Object type references unknown schema for field "+fieldName);
                    }

                    DBObject refObject = (DBObject) document.get(fieldName);
                    this.transformDocument(refObject, this.schemas.get(fieldSchema.getRef()));
                    document.put(fieldName, refObject);

                    break;
                case "list":
                    if ( fieldSchema.getRef() == null ) {
                        throw new RuntimeException("List type without reference for field "+fieldName);
                    }
                    if ( !this.schemas.containsKey(fieldSchema.getRef()) ) {
                        throw new RuntimeException("List type references unknown schema for field "+fieldName);
                    }
                    break;
                case "map":
                    if ( fieldSchema.getRef() == null ) {
                        throw new RuntimeException("List type without reference for field "+fieldName);
                    }
                    if ( !this.schemas.containsKey(fieldSchema.getRef()) ) {
                        throw new RuntimeException("List type references unknown schema for field "+fieldName);
                    }

                    DBObject map = (DBObject) document.get(fieldName);
                    System.out.println(fieldName);
                    System.out.println(document.get("name"));
                    for ( String mapKey : map.keySet() ) {
                        DBObject mapObject = (DBObject) map.get(mapKey);
                        this.transformDocument(mapObject, this.schemas.get(fieldSchema.getRef()));
                        mapObject.put(mapKey, mapObject);
                    }
                    document.put(fieldName, map);

                    break;
                default:
                    throw new RuntimeException("Unknown field type "+fieldSchema.getType()+" for field "+fieldName );
            }
        }

        for ( String fieldName : documentFields ) {
            document.removeField(fieldName);
        }
    }

    private void loadSchemas() {
        this.schemas = new HashMap<>();
        this.schemas.put("objectSchema", this.objectSchema);
        this.schemas.put("fieldSchema", this.fieldSchema);

        for ( DBObject schema : mongoDBService.getCollectionDocuments("schemas") ) {
            System.out.println(schema);
            this.transformDocument(schema, this.objectSchema);
            String name = (String)schema.get("name");
            HashMap<String,FieldSchema> fields = loadFieldSchemas((DBObject) schema.get("fields"));
            ObjectSchema objectSchema = new ObjectSchema(name, fields);

            System.out.println(objectSchema);

            this.schemas.put(name, objectSchema);
        }
    }

    private HashMap<String,FieldSchema> loadFieldSchemas(DBObject fields) {
        HashMap<String,FieldSchema> fieldMap = new HashMap<>();

        for ( String fieldName : fields.keySet() ) {
            DBObject field = (DBObject)fields.get(fieldName);
            FieldSchema fieldSchema = new FieldSchema(field);
            fieldMap.put(fieldName, fieldSchema);
        }

        return fieldMap;
    }
}
