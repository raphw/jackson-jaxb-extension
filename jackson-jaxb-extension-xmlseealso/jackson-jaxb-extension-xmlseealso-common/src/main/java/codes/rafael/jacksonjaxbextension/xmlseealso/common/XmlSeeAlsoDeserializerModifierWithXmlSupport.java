package codes.rafael.jacksonjaxbextension.xmlseealso.common;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoDeserializerModifierWithXmlSupport extends BeanDeserializerModifier {

    private final PropertyName property;

    private final Function<String, PropertyName> resolver;

    private final Function<BeanDescription, Map<PropertyName, Class<?>>> toTypes;

    XmlSeeAlsoDeserializerModifierWithXmlSupport(PropertyName property, Function<String, PropertyName> resolver, Function<BeanDescription, Map<PropertyName, Class<?>>> toTypes) {
        this.property = property;
        this.resolver = resolver;
        this.toTypes = toTypes;
    }

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription description, JsonDeserializer<?> deserializer) {
        if (deserializer instanceof AbstractDeserializer) {
            Map<PropertyName, Class<?>> types = toTypes.apply(description);
            if (types != null) {
                deserializer = new XmlSeeAlsoDeserializerWithXmlSupport(description, property, resolver, types);
            }
        }
        return deserializer;
    }
}
