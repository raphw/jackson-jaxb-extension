package codes.rafael.jacksonjaxbextension.xmlseealso.javax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavaxXmlSeeAlsoModuleTest {

    @Test
    public void testXmlSeeAlso() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(JavaxXmlSeeAlsoModule.ofAtType())
                .registerModule(new JaxbAnnotationModule());
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

    @Test
    public void testXmlSeeAlsoList() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(JavaxXmlSeeAlsoModule.ofAtType())
                .registerModule(new JaxbAnnotationModule());
        String json = objectMapper.writeValueAsString(new WrapperList());
        assertEquals(json, "{\"base\":[{\"@type\":\"FirstType\"}]}");
        assertEquals(First.class, objectMapper.readValue(json, WrapperList.class).getBase().get(0).getClass());
        StringWriter xml = new StringWriter();
        JAXBContext.newInstance(WrapperList.class).createMarshaller().marshal(new JAXBElement<>(
                new QName("element"),
                WrapperList.class,
                new WrapperList()
        ), xml);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<element>" +
                        "<base xsi:type=\"FirstType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "</element>",
                xml.toString());
        assertEquals(First.class, JAXBContext.newInstance(WrapperList.class).createUnmarshaller().unmarshal(
                new StreamSource(new StringReader(xml.toString())),
                WrapperList.class
        ).getValue().getBase().get(0).getClass());
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

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WrapperList {

        @XmlElement(name = "base")
        private List<Base> base = new ArrayList<>(Collections.singletonList(new First()));

        public List<Base> getBase() {
            return base;
        }

        public void setBase(List<Base> base) {
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