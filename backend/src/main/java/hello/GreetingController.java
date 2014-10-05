package hello;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
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
    public Greeting greeting(String message) throws Exception {
        DBObject document = (DBObject) JSON.parse(message);
        schemaService.transformDocument(document, schemaService.schemas.get("mySchema"));
        return new Greeting(document.toString());
    }
}