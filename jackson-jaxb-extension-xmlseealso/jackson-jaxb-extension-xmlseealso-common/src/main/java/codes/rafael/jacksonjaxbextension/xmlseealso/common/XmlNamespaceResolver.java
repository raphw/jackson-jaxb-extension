package codes.rafael.jacksonjaxbextension.xmlseealso.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.util.function.BiFunction;

class XmlNamespaceResolver implements BiFunction<JsonParser, String, String> {

    @Override
    public String apply(JsonParser parser, String value) {
        if (!(parser instanceof FromXmlParser)) {
            return value;
        }
        int namespace = value.indexOf(':');
        String uri = ((FromXmlParser) parser).getStaxReader().getNamespaceURI(value.substring(0, namespace));
        if (uri == null) {
            return value;
        } else {
            return uri + ":" + value.substring(namespace + 1);
        }
    }
}
