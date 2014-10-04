package hello;

import com.mongodb.ReflectionDBObject;

/**
 * Created by dirk on 14.09.14.
 */
public class FieldSchema extends ReflectionDBObject {
    public String type;
    public String ref = null;
    public boolean required = false;
    public String getter = null;
    public String setter = null;
    public String validator = null;
    public String postSetValidator = null;
    public Object defaultValue = null;

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
