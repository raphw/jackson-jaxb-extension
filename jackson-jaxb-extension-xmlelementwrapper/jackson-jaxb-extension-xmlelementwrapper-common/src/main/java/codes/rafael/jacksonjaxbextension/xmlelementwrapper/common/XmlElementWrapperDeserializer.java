package codes.rafael.jacksonjaxbextension.xmlelementwrapper.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

import java.io.IOException;
import java.util.function.Function;

class XmlElementWrapperDeserializer extends BeanDeserializer {

    private final BeanPropertyMap beanProperties;

    boolean alive;

    XmlElementWrapperDeserializer(BeanDeserializer source, Function<AnnotatedMember, PropertyName> resolver) {
        super(source);
        BeanPropertyMap beanProperties = _beanProperties;
        for (SettableBeanProperty property : _beanProperties.getPropertiesInInsertionOrder()) {
            PropertyName name = resolver.apply(property.getMember());
            if (name != null) {
                alive = true;
                beanProperties = beanProperties.withProperty(property.withName(name).withValueDeserializer(new ValueDeserializer(
                        property.getName(),
                        property.getValueDeserializer(),
                        property.getType()
                )));
            }
        }
        this.beanProperties = beanProperties;
    }

    BeanDeserializerBase resolve() {
        return withBeanProperties(beanProperties);
    }

    private static class ValueDeserializer extends JsonDeserializer<Object> {

        private final String name;

        private final JsonDeserializer<Object> delegate;

        private final JavaType type;

        private ValueDeserializer(String name, JsonDeserializer<Object> delegate, JavaType type) {
            this.name = name;
            this.delegate = delegate;
            this.type = type;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.nextFieldName().equals(name) || !parser.nextToken().equals(JsonToken.START_ARRAY)) {
                throw new JsonParseException(parser, "Expected array with name: " + name);
            }
            Object value = delegate == null
                    ? parser.getCodec().readValue(parser, type)
                    : delegate.deserialize(parser, context);
            if (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
                throw new JsonParseException(parser, "Expected end of object for name: " + name);
            }
            return value;
        }
    }
}