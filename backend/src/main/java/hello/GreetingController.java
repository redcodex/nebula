package hello;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

@Controller
public class GreetingController {

    @Inject
    private SchemaService schemaService;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(JsonNode message) throws Exception {
        parseObject(message, schemaService.schemas.get("mySchema"));
        return new Greeting("Hello, " + message.toString() + "!");
    }

    private void parseObject(JsonNode object, ObjectSchema objectSchema) throws IOException {
        for ( Map.Entry<String, FieldSchema> fieldSchema : objectSchema.getFields().entrySet() ) {
            String field = fieldSchema.getKey();
            FieldSchema schema = fieldSchema.getValue();

            if ( !object.has(field) ) {
                if ( schema.required ) {
                    throw new RuntimeException("required field " + field + " of schema " + objectSchema.getName() + " missing!");
                } else {
                    continue;
                }
            }

            switch ( schema.type.toLowerCase() ) {
                case "string":
                    break;
                case "int":
                    break;
                case "float":
                    break;
                case "object":
                    if ( schema.ref == null ) {
                        throw new RuntimeException("Object type without reference for field "+field);
                    }
                    if ( !schemaService.schemas.containsKey(schema.ref) ) {
                        throw new RuntimeException("Object type references unknown schema for field "+field);
                    }
                    this.parseObject(object.get(field), schemaService.schemas.get(schema.ref));
                    break;
                case "list":
                    break;
                default:
                    throw new RuntimeException("Unknown field type "+schema.type+" for field "+field );
            }
        }
    }

}