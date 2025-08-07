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
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

class XmlElementWrapperDeserializer extends BeanDeserializer {

    private final BeanPropertyMap beanProperties;

    boolean alive;

    XmlElementWrapperDeserializer(BeanDeserializer source, Function<AnnotatedMember, PropertyName> resolver) {
        super(source);
        BeanPropertyMap beanProperties = _beanProperties.withoutProperties(Arrays.stream(_beanProperties.getPropertiesInInsertionOrder())
                .map(SettableBeanProperty::getName)
                .collect(Collectors.toSet()));
        for (SettableBeanProperty property : _beanProperties.getPropertiesInInsertionOrder()) {
            PropertyName name = resolver.apply(property.getMember());
            if (name != null) {
                alive = true;
                beanProperties = beanProperties.withProperty(property.withName(name).withValueDeserializer(new ValueDeserializer(
                        name,
                        property
                )));
            } else {
                beanProperties = beanProperties.withProperty(property);
            }
        }
        this.beanProperties = beanProperties;
    }

    BeanDeserializerBase resolve() {
        return withBeanProperties(beanProperties);
    }

    private static class ValueDeserializer extends JsonDeserializer<Object> {

        private final PropertyName name;
        private final SettableBeanProperty property;

        private ValueDeserializer(PropertyName name, SettableBeanProperty property) {
            this.name = name;
            this.property = property;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!(Objects.equals(parser.currentName(), name.getSimpleName()) && parser.currentToken() == JsonToken.START_OBJECT)) {
                throw new JsonParseException(parser, "Expected array with name: " + name);
            }
            if (parser.nextToken() != JsonToken.FIELD_NAME || !Objects.equals(parser.currentName(), property.getName())) {
                throw new JsonParseException(parser, "Expected start of nested value with name: " + property);
            }
            JsonToken token = parser.nextToken();
            if (token == JsonToken.VALUE_NULL) {
                if (parser.nextToken() != JsonToken.END_OBJECT) {
                    throw new IllegalStateException("Expected end of object for name: " + name);
                }
                return null;
            } else if (token != JsonToken.START_ARRAY) {
                throw new JsonParseException(parser, "Expected start of array for name: " + name);
            }
            Object value = context
                    .findContextualValueDeserializer(property.getType(), property)
                    .deserialize(parser, context);
            if (parser.getLastClearedToken() != JsonToken.END_ARRAY && parser.nextToken() != JsonToken.END_OBJECT) {
                throw new JsonParseException(parser, "Expected end of nested array and object for name: " + name);
            }
            return value;
        }
    }
}
