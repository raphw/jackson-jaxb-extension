package codes.rafael.jacksonjaxbextension.xmlelementwrapper.common;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.function.Function;

public abstract class XmlElementWrapperModule extends SimpleModule {

    protected static final String DEFAULT = "##default";

    protected XmlElementWrapperModule(String name, boolean serialization, Function<AnnotatedMember, PropertyName> resolver) {
        super(name);
        if (serialization) {
            setSerializerModifier(new XmlElementWrapperSerializerModifier(resolver));
        }
        setDeserializerModifier(new XmlElementWrapperDeserializerModifier(resolver));
    }
}
