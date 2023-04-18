package codes.rafael.jacksonjaxbextension.xmlseealso.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoWrapperWriterWithXmlSupport extends VirtualBeanPropertyWriter {

    private final Function<PropertyName, String> resolver;

    private final Map<Class<?>, PropertyName> types;

    XmlSeeAlsoWrapperWriterWithXmlSupport(
            BeanPropertyDefinition definition,
            Annotations annotations,
            JavaType type,
            Function<PropertyName, String> resolver,
            Map<Class<?>, PropertyName> types,
            Map.Entry<?, ?> attribute
    ) {
        super(definition, annotations, type);
        this.resolver = resolver;
        this.types = types;
        if (attribute != null) {
            setInternalSetting(attribute.getKey(), attribute.getValue());
        }
    }

    @Override
    protected String value(Object bean, JsonGenerator generator, SerializerProvider provided) throws XMLStreamException {
        PropertyName name = types.get(bean.getClass());
        if (name == null) {
            throw new IllegalStateException(bean.getClass() + " is not mapped within " + types.keySet());
        }
        if (generator instanceof ToXmlGenerator) {
            if (name.hasNamespace()) {
                XMLStreamWriter writer = ((ToXmlGenerator) generator).getStaxWriter();
                String prefix = writer.getNamespaceContext().getPrefix(name.getNamespace());
                int index = 0;
                while (prefix == null) {
                    String candidate = "wstxns" + ++index;
                    if (writer.getNamespaceContext().getNamespaceURI(candidate) == null) {
                        writer.writeNamespace(candidate, name.getNamespace());
                        prefix = candidate;
                    }
                }
                return prefix + ":" + name.getSimpleName();
            } else {
                return name.getSimpleName();
            }
        } else {
            return resolver.apply(name);
        }
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass, BeanPropertyDefinition propDef, JavaType type) {
        throw new UnsupportedOperationException();
    }
}
