package hello;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.ReflectionDBObject;

/**
 * Created by dirk on 14.09.14.
 */
public class FieldSchema {
    private String type;
    private String ref = null;
    private boolean required = false;
    private String getter = null;
    private String setter = null;
    private String validator = null;
    private String postSetValidator = null;
    private Object defaultValue = null;

    public FieldSchema(){

    }

    public FieldSchema(Object object) {
        if ( object instanceof BasicDBObject ) {
            BasicDBObject schema = (BasicDBObject) object;

            this.setType(schema.getString("type"));

            if ( schema.containsField("ref") ) {
                this.setRef(schema.getString("ref"));
            }

            if ( schema.containsField("required") ) {
                this.setRequired(schema.getBoolean("required"));
            }

            if ( schema.containsField("getter") ) {
                this.setGetter(schema.getString("getter"));
            }

            if ( schema.containsField("setter") ) {
                this.setSetter(schema.getString("setter"));
            }

            if ( schema.containsField("validator") ) {
                this.setValidator(schema.getString("validator"));
            }

            if ( schema.containsField("postSetValidator") ) {
                this.setPostSetValidator(schema.getString("postSetValidator"));
            }

            if ( schema.containsField("defaultValue") ) {
                this.setDefaultValue(schema.getString("defaultValue"));
            }

            return;
        }

        throw new RuntimeException("Unknown object type for new field schema. Was class: "+object.getClass());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getPostSetValidator() {
        return postSetValidator;
    }

    public void setPostSetValidator(String postSetValidator) {
        this.postSetValidator = postSetValidator;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
