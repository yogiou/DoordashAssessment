package jie.wen.doordash.assessment.springboot.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class CustomModule extends SimpleModule {
    public CustomModule() {
        super("CustomModule");
        addSerializer(ObjectNode.class, CustomSerializer.INSTANCE);
    }

    static class CustomSerializer extends JsonSerializer {
        public static final CustomSerializer INSTANCE = new CustomSerializer();

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value instanceof ObjectNode) {
                gen.writeStartObject(this);
                gen.writeStringField("@class", value.getClass().getName());
                Iterator<Map.Entry<String, JsonNode>> iterator = ((ObjectNode) value).fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> field = iterator.next();
                    JsonNode v = field.getValue();
                    if (v.isArray() && v.isEmpty(serializers)) {
                        continue;
                    }
                    gen.writeFieldName(field.getKey());
                    v.serialize(gen, serializers);
                }
                gen.writeEndObject();
            }
        }
    }
}
