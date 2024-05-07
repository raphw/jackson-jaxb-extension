package codes.rafael.jacksonjaxbextension.xmlelementwrapper.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.VirtualAnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;

import java.util.function.Function;

class XmlElementWrapperSerializer extends BeanSerializer {

    boolean alive;

    private final Function<AnnotatedMember, PropertyName> resolver;

    XmlElementWrapperSerializer(BeanSerializer source, MapperConfig<?> config, BeanDescription description, Function<AnnotatedMember, PropertyName> resolver) {
        super(source);
        this.resolver = resolver;
        modify(_props, config, description, false);
        modify(_filteredProps, config, description, true);
    }

    private void modify(
            BeanPropertyWriter[] properties,
            MapperConfig<?> config,
            BeanDescription description,
            boolean filtered
    ) {
        if (properties == null) {
            return;
        }
        for (int index = 0; index < properties.length; index++) {
            if (properties[index] != null) {
                PropertyName name = resolver.apply(properties[index].getMember());
                if (name != null) {
                    alive = true;
                    JavaType wrapper = config.constructType(Object.class);
                    BeanSerializerBuilder builder = new BeanSerializerBuilder(description);
                    builder.setFilterId(_propertyFilterId);
                    properties[index] = new XmlElementWrapperWriter(SimpleBeanPropertyDefinition.construct(
                            config,
                            new VirtualAnnotatedMember(
                                    description.getClassInfo(),
                                    description.getClassInfo().getRawType(),
                                    name.getSimpleName(),
                                    wrapper
                            ),
                            name,
                            PropertyMetadata.STD_OPTIONAL,
                            JsonInclude.Include.NON_EMPTY
                    ), description.getClassInfo().getAnnotations(), wrapper, new BeanSerializer(
                            _beanType,
                            builder,
                            filtered ? null : new BeanPropertyWriter[]{properties[index]},
                            filtered ? new BeanPropertyWriter[]{properties[index]} : null
                    ));
                }
            }
        }
    }
}