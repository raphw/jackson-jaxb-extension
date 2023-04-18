package codes.rafael.jacksonjaxbextension.xmlseealso.javax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class JavaxXmlSeeAlsoModuleTest {

    @Test
    public void testXmlSeeAlsoAsJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(JavaxXmlSeeAlsoModule.ofAtType())
                .registerModule(new JaxbAnnotationModule());
        String json = objectMapper.writeValueAsString(new Wrapper());
        assertEquals("{\"base\":{\"@type\":\"FirstType\"}}", json);
        assertEquals(First.class, objectMapper.readValue(json, Wrapper.class).getBase().getClass());
    }

    @Test
    public void testXmlSeeAlsoWithNamespaceAsJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(JavaxXmlSeeAlsoModule.ofAtType())
                .registerModule(new JaxbAnnotationModule());
        String json = objectMapper.writeValueAsString(new WrapperWithNamespace());
        assertEquals("{\"base\":{\"@type\":\"{FirstNamespace}FirstType\"}}", json);
        assertEquals(FirstWithNamespace.class, objectMapper.readValue(json, WrapperWithNamespace.class).getBase().getClass());
    }

    @Test
    public void testXmlSeeAlsoAsXml() throws Exception {
        ObjectMapper objectMapper = new XmlMapper()
                .registerModule(JavaxXmlSeeAlsoModule.ofXsi())
                .registerModule(new JaxbAnnotationModule());
        String xml = objectMapper.writeValueAsString(new Wrapper());
        assertEquals("<Wrapper><base xmlns:wstxns1=\"http://www.w3.org/2001/XMLSchema-instance\" wstxns1:type=\"FirstType\"/></Wrapper>", xml);
        assertEquals(First.class, objectMapper.readValue(xml, Wrapper.class).getBase().getClass());
        assertEquals(First.class, JAXBContext.newInstance(Wrapper.class).createUnmarshaller().unmarshal(
                new StreamSource(new StringReader(xml)),
                Wrapper.class
        ).getValue().getBase().getClass());
    }

    @Test
    public void testXmlSeeAlsoWithNamespaceAsXml() throws Exception {
        ObjectMapper objectMapper = new XmlMapper()
                .registerModule(JavaxXmlSeeAlsoModule.ofXsi())
                .registerModule(new JaxbAnnotationModule());
        String xml = objectMapper.writeValueAsString(new WrapperWithNamespace());
        assertEquals("<root><base xmlns:wstxns1=\"FirstNamespace\" xmlns:wstxns2=\"http://www.w3.org/2001/XMLSchema-instance\" wstxns2:type=\"wstxns1:FirstType\"/></root>", xml);
        assertEquals(FirstWithNamespace.class, objectMapper.readValue(xml, WrapperWithNamespace.class).getBase().getClass());
        assertEquals(FirstWithNamespace.class, JAXBContext.newInstance(WrapperWithNamespace.class).createUnmarshaller().unmarshal(
                new StreamSource(new StringReader(xml)),
                WrapperWithNamespace.class
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

    @XmlRootElement(name = "root")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WrapperWithNamespace {

        @XmlElement(name = "base")
        private BaseWithNamespace base = new FirstWithNamespace();

        public BaseWithNamespace getBase() {
            return base;
        }

        public void setBase(BaseWithNamespace base) {
            this.base = base;
        }
    }

    @XmlSeeAlso({FirstWithNamespace.class, SecondWithNamespace.class})
    public abstract static class BaseWithNamespace { }

    @XmlType(name = "FirstType", namespace = "FirstNamespace")
    public static class FirstWithNamespace extends BaseWithNamespace { }

    @XmlType(name = "SecondType", namespace = "SecondNamespace")
    public static class SecondWithNamespace extends BaseWithNamespace { }
}
