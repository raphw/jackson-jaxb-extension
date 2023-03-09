package codes.rafael.jacksonjaxbextension.xmlseealso.jakarta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class JakartaXmlSeeAlsoModuleTest {

    @Test
    public void testXmlSeeAlso() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(JakartaXmlSeeAlsoModule.ofAtType())
                .registerModule(new JakartaXmlBindAnnotationModule());
        String json = objectMapper.writeValueAsString(new Wrapper());
        assertEquals(json, "{\"base\":{\"@type\":\"FirstType\"}}");
        assertEquals(First.class, objectMapper.readValue(json, Wrapper.class).getBase().getClass());
        StringWriter xml = new StringWriter();
        JAXBContext.newInstance(Wrapper.class).createMarshaller().marshal(new JAXBElement<>(
                new QName("element"),
                Wrapper.class,
                new Wrapper()
        ), xml);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<element>" +
                        "<base xsi:type=\"FirstType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "</element>",
                xml.toString());
        assertEquals(First.class, JAXBContext.newInstance(Wrapper.class).createUnmarshaller().unmarshal(
                new StreamSource(new StringReader(xml.toString())),
                Wrapper.class
        ).getValue().getBase().getClass());
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Wrapper {

        @XmlElement(name = "base")
        private Base base = new First();

        public Base getBase() {
            return base;
        }

        public void setBase(Base base) {
            this.base = base;
        }
    }

    @XmlSeeAlso({First.class, Second.class})
    public abstract static class Base { }

    @XmlType(name = "FirstType")
    public static class First extends Base { }

    @XmlType(name = "SecondType")
    public static class Second extends Base { }
}