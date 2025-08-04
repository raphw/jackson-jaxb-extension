package codes.rafael.jacksonjaxbextension.xmlelementwrapper.jakarta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JakartaXmlElementWrapperModuleTest {

    @Test
    public void testXmlElementWrapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JakartaXmlElementWrapperModule())
                .registerModule(new JakartaXmlBindAnnotationModule());
        String jsonWith = objectMapper.writeValueAsString(new WithXewPlugin());
        assertEquals(jsonWith, "{\"values\":{\"value\":[\"foo\",\"bar\"]}}");
        assertEquals(
                objectMapper.readValue("{\"value\":[\"foo\",\"bar\"]}", WithXewPlugin.class).getValue(),
                objectMapper.readValue(jsonWith, WithXewPlugin.class).getValue());
    }

    @Test
    public void testXmlElementWrapperSerialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JakartaXmlElementWrapperModule(false))
                .registerModule(new JakartaXmlBindAnnotationModule());
        String jsonWith = objectMapper.writeValueAsString(new WithXewPlugin());
        assertEquals(jsonWith, "{\"value\":[\"foo\",\"bar\"]}");
        assertEquals(
                objectMapper.readValue("{\"value\":[\"foo\",\"bar\"]}", WithXewPlugin.class).getValue(),
                objectMapper.readValue(jsonWith, WithXewPlugin.class).getValue());
    }

    public static class WithXewPlugin {

        @XmlElement(name = "value")
        @XmlElementWrapper(name = "values")
        private final List<String> value = new ArrayList<>();

        public WithXewPlugin() {
            value.add("foo");
            value.add("bar");
        }

        public List<String> getValue() {
            return value;
        }
    }
}