package codes.rafael.jacksonjaxbextension.xmlelementwrapper.common;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.function.Function;

class XmlElementWrapperSerializerModifier extends BeanSerializerModifier {

    private final Function<AnnotatedMember, PropertyName> resolver;

    XmlElementWrapperSerializerModifier(Function<AnnotatedMember, PropertyName> resolver) {
        this.resolver = resolver;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription description, JsonSerializer<?> serializer) {
        if (serializer instanceof BeanSerializer) {
            XmlElementWrapperSerializer wrapped = new XmlElementWrapperSerializer((BeanSerializer) serializer, config, description, resolver);
            if (wrapped.alive) {
                serializer = wrapped;
            }
        }
        return serializer;
    }
}
