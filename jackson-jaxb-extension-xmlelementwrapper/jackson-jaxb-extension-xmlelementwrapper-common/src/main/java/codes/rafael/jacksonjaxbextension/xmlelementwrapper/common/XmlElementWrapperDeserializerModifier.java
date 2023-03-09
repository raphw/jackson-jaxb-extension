package codes.rafael.jacksonjaxbextension.xmlelementwrapper.common;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

import java.util.function.Function;

class XmlElementWrapperDeserializerModifier extends BeanDeserializerModifier {

    private final Function<AnnotatedMember, PropertyName> resolver;

    XmlElementWrapperDeserializerModifier(Function<AnnotatedMember, PropertyName> resolver) {
        this.resolver = resolver;
    }

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription description, JsonDeserializer<?> deserializer) {
        if (deserializer instanceof BeanDeserializer) {
            XmlElementWrapperDeserializer wrapped = new XmlElementWrapperDeserializer((BeanDeserializer) deserializer, resolver);
            if (wrapped.alive) {
                deserializer = wrapped.resolve();
            }
        }
        return deserializer;
    }
}
