package codes.rafael.jacksonjaxbextension.xmlelementwrapper.javax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.junit.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavaxXmlElementWrapperModuleTest {

    @Test
    public void testXmlElementWrapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaxXmlElementWrapperModule())
                .registerModule(new JaxbAnnotationModule());
        String jsonWith = objectMapper.writeValueAsString(WithXewPlugin.of("foo", "bar"));
        assertEquals(jsonWith, "{\"values\":{\"value\":[\"foo\",\"bar\"]}}");
        assertEquals(
                objectMapper.readValue("{\"value\":[\"foo\",\"bar\"]}", WithXewPlugin.class).getValue(),
                objectMapper.readValue(jsonWith, WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperSerialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaxXmlElementWrapperModule(false))
                .registerModule(new JaxbAnnotationModule());
        String jsonWith = objectMapper.writeValueAsString(WithXewPlugin.of("foo", "bar"));
        assertEquals(jsonWith, "{\"value\":[\"foo\",\"bar\"]}");
        assertEquals(
                objectMapper.readValue("{\"values\":{\"value\":[\"foo\",\"bar\"]}}", WithXewPlugin.class).getValue(),
                objectMapper.readValue(jsonWith, WithXewPlugin.class).getValue());
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