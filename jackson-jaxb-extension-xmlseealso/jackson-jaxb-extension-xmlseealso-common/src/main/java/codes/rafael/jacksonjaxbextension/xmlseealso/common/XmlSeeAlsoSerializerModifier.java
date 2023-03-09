package codes.rafael.jacksonjaxbextension.xmlseealso.common;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoSerializerModifier extends BeanSerializerModifier {

    private final PropertyName property;

    private final Function<PropertyName, String> resolver;

    private final Function<BeanDescription, Map<Class<?>, PropertyName>> toProperties;

    XmlSeeAlsoSerializerModifier(PropertyName property, Function<PropertyName, String> resolver, Function<BeanDescription, Map<Class<?>, PropertyName>> toProperties) {
        this.property = property;
        this.resolver = resolver;
        this.toProperties = toProperties;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription description, JsonSerializer<?> serializer) {
        if (serializer instanceof BeanSerializer) {
            Map<Class<?>, PropertyName> types = toProperties.apply(description);
            if (types != null) {
                serializer = XmlSeeAlsoSerializer.wrap((BeanSerializer) serializer, config, property, resolver, types);
            }
        }
        return serializer;
    }
}
