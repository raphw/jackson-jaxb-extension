package codes.rafael.jacksonjaxbextension.xmlelementwrapper.javax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JavaxXmlElementWrapperModuleTest {
    @Test
    public void testXmlElementWrapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaxXmlElementWrapperModule())
                .registerModule(new JaxbAnnotationModule());
        assertEquals(
                "{\"values\":{\"value\":[\"foo\",\"bar\"]}}",
                objectMapper.writeValueAsString(WithXewPlugin.of("foo", "bar")));
        assertEquals(
                Arrays.asList("foo", "bar"),
                objectMapper.readValue("{\"values\":{\"value\":[\"foo\",\"bar\"]}}", WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperEmpty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaxXmlElementWrapperModule())
                .registerModule(new JaxbAnnotationModule());
        assertEquals(
                "{\"values\":{\"value\":[]}}",
                objectMapper.writeValueAsString(WithXewPlugin.of()));
        assertEquals(
                Collections.emptyList(),
                objectMapper.readValue("{\"values\":{\"value\":[]}}", WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaxXmlElementWrapperModule())
                .registerModule(new JaxbAnnotationModule());
        assertNull(objectMapper.readValue("{\"values\":{\"value\":null}}", WithXewPlugin.class).getValue());
        assertNull(objectMapper.readValue("{\"values\":null}", WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperSerialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaxXmlElementWrapperModule(false, false))
                .registerModule(new JaxbAnnotationModule());
        assertEquals(
                "{\"value\":[\"foo\",\"bar\"]}",
                objectMapper.writeValueAsString(WithXewPlugin.of("foo", "bar")));
        assertEquals(
                Arrays.asList("foo", "bar"),
                objectMapper.readValue("{\"value\":[\"foo\",\"bar\"]}", WithXewPlugin.class).getValue());
    }

    public static class WithXewPlugin {

        @XmlElement(name = "value")
        @XmlElementWrapper(name = "values")
        private final List<String> value = new ArrayList<>();

        public static WithXewPlugin of(String... values) {
            WithXewPlugin object = new WithXewPlugin();
            object.value.addAll(Arrays.asList(values));
            return object;
        }

        public List<String> getValue() {
            return value;
        }
    }
}