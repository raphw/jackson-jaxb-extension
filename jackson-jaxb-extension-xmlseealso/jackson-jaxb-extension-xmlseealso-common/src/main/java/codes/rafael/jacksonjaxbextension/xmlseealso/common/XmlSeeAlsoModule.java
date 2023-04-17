package codes.rafael.jacksonjaxbextension.xmlseealso.common;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class XmlSeeAlsoModule extends SimpleModule {

    protected static final String DEFAULT = "##default";

    protected XmlSeeAlsoModule(
            String name,
            PropertyName property,
            boolean attribute,
            Function<PropertyName, String> serializationResolver,
            Function<String, PropertyName> deserializationResolver,
            BiPredicate<BeanDescription, BiConsumer<Class<?>, PropertyName>> isHierarchy
    ) {
        super(name);
        boolean supportsXml;
        try {
            Class.forName("com.fasterxml.jackson.dataformat.xml.XmlFactory");
            supportsXml = true;
        } catch (ClassNotFoundException e) {
            supportsXml = false;
        }
        Function<BeanDescription, Map<Class<?>, PropertyName>> toProperties = description -> {
            Map<Class<?>, PropertyName> types = new LinkedHashMap<>();
            if (isHierarchy.test(description, types::put)) {
                return types;
            } else {
                return null;
            }
        };
        Function<BeanDescription, Map<PropertyName, Class<?>>> toTypes = description -> {
            Map<PropertyName, Class<?>> types = new LinkedHashMap<>();
            if (isHierarchy.test(description, (type, candidate) -> types.put(candidate, type))) {
                return types;
            } else {
                return null;
            }
        };
        setSerializerModifier(supportsXml
                ? new XmlSeeAlsoSerializerModifierWithXmlSupport(property, attribute, serializationResolver, toProperties)
                : new XmlSeeAlsoSerializerModifier(property, serializationResolver, toProperties));
        setDeserializerModifier(supportsXml
                ? new XmlSeeAlsoDeserializerModifierWithXmlSupport(property, deserializationResolver, toTypes)
                : new XmlSeeAlsoDeserializerModifier(property, deserializationResolver, toTypes));
    }
}
