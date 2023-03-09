package codes.rafael.jacksonjaxbextension.xmlseealso.jakarta;

import codes.rafael.jacksonjaxbextension.xmlseealso.common.PropertyNameParser;
import codes.rafael.jacksonjaxbextension.xmlseealso.common.XmlSeeAlsoModule;
import com.fasterxml.jackson.databind.PropertyName;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import java.util.function.Function;

public class JakartaXmlSeeAlsoModule extends XmlSeeAlsoModule {

    public static XmlSeeAlsoModule ofXsi() {
        return new JakartaXmlSeeAlsoModule("type", "http://www.w3.org/2001/XMLSchema-instance", true);
    }

    public static XmlSeeAlsoModule ofAtType() {
        return new JakartaXmlSeeAlsoModule("@type", null, false);
    }

    public JakartaXmlSeeAlsoModule(String name, String namespace, boolean attribute) {
        this(new PropertyName(name, namespace), attribute, PropertyName::toString, new PropertyNameParser());
    }

    public JakartaXmlSeeAlsoModule(
            PropertyName property,
            boolean attribute,
            Function<PropertyName, String> serializationResolver,
            Function<String, PropertyName> deserializationResolver
    ) {
        super(
                XmlSeeAlso.class.getName() + "Module",
                property,
                attribute,
                serializationResolver,
                deserializationResolver,
                (description, consumer) -> {
                    XmlSeeAlso annotation = description.getClassAnnotations().get(XmlSeeAlso.class);
                    if (annotation == null) {
                        return false;
                    }
                    for (Class<?> value : annotation.value()) {
                        XmlType info = value.getAnnotation(XmlType.class);
                        String namespace = null;
                        if (info == null || info.namespace().equals(DEFAULT)) {
                            Package location = value.getDeclaringClass().getPackage();
                            if (location != null) {
                                XmlSchema schema = location.getAnnotation(XmlSchema.class);
                                if (schema != null && !schema.namespace().isEmpty()) {
                                    namespace = schema.namespace();
                                }
                            }
                        } else {
                            namespace = info.namespace();
                        }
                        consumer.accept(value, new PropertyName(info == null || info.name().equals(DEFAULT)
                                ? value.getName()
                                : info.name(), namespace));
                    }
                    return true;
                }
        );
    }
}
