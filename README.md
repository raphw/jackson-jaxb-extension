Extensions for JAXB when using Jackson
===

This library offers two extensions for Jackson's support of the official JAXB annotations. This is to create more consistent XML or JSON, what currently relies to some degree on the configuration of XJB and of custom configurations set in Jackson. This library contains a version for both the *javax* and *jakarta* namespace of JAXB.

`XmlElementWrapper`
====

`XmlElementWrapperModule` offers support for the `XmlElementWrapper` annotation when for example the XEW plugin is used for creating object representations. The annotation allows to avoid intermediate objects for wrapped lists of elements. For example, the following class would create an XML where a list of `value` elements is wrapped by a `values` tag. 

    class WithXewPlugin {
        @XmlElement(name = "value")
        @XmlElementWrapper(name = "values")
        List<String> value = new ArrayList<>();
    }

Without a plugin like XEW, two classes would be created where the outer class defines the wrapper element:

    class WithoutXewPlugin {
        @XmlElement(name = "values")
        Values values;
    }

    class Values {
        @XmlElement(name = "value")
        List<String> value = new ArrayList<>();
    }

Depending on the chosen representation, Jackson would then render two different JSON representations for both object representations:

    {"value": ["first", "second"]}
    {"values": {"value": ["first", "second"]}}

Jackson cannot marshal or unmarshal these representation to each other, despite the JAXB specification defining them to represent an identical structure. With the `XmlElementWrapperModule`, this capability is added to Jackson. If `serialization` is set to `true`, Jackson will always add the wrapper element to the JSON when creating a serialized representation. Without it, only deserializing capabilities are added such that both representations can be read.

`XmlSeeAlso`
====

The XML standard defines a mechanism to represent polymorphic types which is not present in JSON. The `XmlSeeAlso` annotation is however not supported by Jackson where this support can be added by registering the `XmlSeeAlsoModule`. 

Considering the following structure, any object of `base` could be of either type `First` or `Second`.

    class Wrapper {
        @XmlElement(name = "base")
        Base base = new First();
    }

    @XmlSeeAlso({First.class, Second.class})
    abstract class Base { }

    @XmlType(name = "FirstType")
    class First extends Base { }

    @XmlType(name = "SecondType")
    class Second extends Base { }

Jackson's serialization format does however not preserve the type information such that a receiver of the object cannot know that the object would be of type `First`:

    {"base": {}}

By adding a Jackson module `XmlSeeAlsoModule.ofAtType()`, the type information is added to the XML as follows:

    {"base": {"@type": "FirstType"}}

The same module must be registered with the reading mapper. The `@type` element is already a Jackson convention for preserving polymorphism in JSON. The *@* character is however not allowed in XML where the information should be added as an attribute of the XSI namespace by `XmlSeeAlsoModule.ofXsi()` which would result in an XML representation as the following:

    <wrapper>
      <base xsi:type="FirstType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
    </wrapper>

This representation is also understood by any compliant `JAXBContext` implementation.
