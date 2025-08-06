package codes.rafael.jacksonjaxbextension.xmlelementwrapper.javax;

import codes.rafael.jacksonjaxbextension.xmlelementwrapper.common.XmlElementWrapperModule;
import com.fasterxml.jackson.databind.PropertyName;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchema;

public class JavaxXmlElementWrapperModule extends XmlElementWrapperModule {

    public JavaxXmlElementWrapperModule() {
        this(true, true);
    }

    public JavaxXmlElementWrapperModule(boolean serialization, boolean deserialization) {
        super(XmlElementWrapper.class.getName() + "Module", serialization, deserialization, member -> {
            XmlElementWrapper annotation = member.getAnnotation(XmlElementWrapper.class);
            if (annotation == null) {
                return null;
            }
            String namespace = null;
            if (annotation.namespace().equals(DEFAULT)) {
                Class<?> declaringClass = member.getDeclaringClass();
                if (declaringClass != null) {
                    Package location = declaringClass.getPackage();
                    if (location != null) {
                        XmlSchema schema = location.getAnnotation(XmlSchema.class);
                        if (schema != null && !schema.namespace().isEmpty()) {
                            namespace = schema.namespace();
                        }
                    }
                }
            } else {
                namespace = annotation.namespace();
            }
            return new PropertyName(annotation.name().equals(XmlElementWrapperModule.DEFAULT)
                    ? member.getName()
                    : annotation.name(), namespace);
        });
    }
}
