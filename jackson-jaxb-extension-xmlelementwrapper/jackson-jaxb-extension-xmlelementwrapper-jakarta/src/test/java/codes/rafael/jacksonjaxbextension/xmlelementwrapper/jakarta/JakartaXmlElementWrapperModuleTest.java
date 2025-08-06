package codes.rafael.jacksonjaxbextension.xmlelementwrapper.jakarta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JakartaXmlElementWrapperModuleTest {

    @Test
    public void testXmlElementWrapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JakartaXmlElementWrapperModule())
                .registerModule(new JakartaXmlBindAnnotationModule());
        String jsonWith = objectMapper.writeValueAsString(WithXewPlugin.of("foo", "bar"));
        assertEquals(jsonWith, "{\"values\":{\"value\":[\"foo\",\"bar\"]}}");
        assertEquals(
                objectMapper.readValue("{\"value\":[\"foo\",\"bar\"]}", WithXewPlugin.class).getValue(),
                objectMapper.readValue(jsonWith, WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperEmpty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JakartaXmlElementWrapperModule())
                .registerModule(new JakartaXmlBindAnnotationModule());
        String jsonWith = objectMapper.writeValueAsString(WithXewPlugin.of());
        assertEquals(jsonWith, "{\"values\":{\"value\":[]}}");
        assertEquals(
                objectMapper.readValue("{\"value\":[]}", WithXewPlugin.class).getValue(),
                objectMapper.readValue(jsonWith, WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperSerialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JakartaXmlElementWrapperModule(false))
                .registerModule(new JakartaXmlBindAnnotationModule());
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