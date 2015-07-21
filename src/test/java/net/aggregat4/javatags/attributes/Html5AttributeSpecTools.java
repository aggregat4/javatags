package net.aggregat4.javatags.attributes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Html5AttributeSpecTools {

    /**
     * TODO: redo this whole fucking thing: The IDL is useles, I need to parse the HTML, including all the textual like bits. Jezus.
     * Perhaps I can extract the attributeList at the same time that I do the tags. What I don't know is how I will determine the attribute datatype...
     *
     * Attributes:
     *
     * We need the IDL for getting the datatypes of the rawAttributes.
     *
     * Attributes are not unique, that is to say: rawAttributes with the same name get reused in different contexts
     * with different types. This means that I either need a more complicated model where attributetypes are scoped
     * to certain html elements which would make my current approach for a fluid and easy api to generate HTML very
     * hard to impossible to attain. Therefore I decided to just go for only 2 datatypes:
     * - boolean rawAttributes are modeled as such since they need to be rendered differently, their booleaness is
     *   expressed by them being present or not in an element. From my semi-scientific analysis of the boolean rawAttributes
     *   there is not duplicate usage of elements with the same name but one time with and one time without being a
     *   boolean.
     * - everything else is a string attribute. We lose static type checking at compile time for the values.
     *
     * Elements:
     *
     * Then we need the html text to retrieve a listing of all the actual rawAttributes, the IDL is not complete
     *
     * Following idea:
     * - find all dl with class = element
     * - for each
     * -- find the dt with content "Content rawAttributes"
     * -- for all subsequent dd siblings:
     * --- if it has "Global rawAttributes" in the text, ignore it
     * ---
     */

    public static void main(String [] args) throws Exception {
        InputStream html5SpecInputStream = Html5AttributeSpecTools.class.getResourceAsStream("/HTML5.html");
        Document doc = Jsoup.parse(html5SpecInputStream, StandardCharsets.UTF_8.toString(), "http://");

        Stream<AttributeType> globalAttributes = toAttributeType(getGlobalAttributeDefs(doc));
        Stream<RawTag> tags = getTags(doc);
//        System.out.println("element tags: " + tags.collect(Collectors.toList()));
        Stream<AttributeType> elementAttributes = toAttributeType(getElementAttributeDefs(tags));

//        System.out.println("element attributes: " + elementAttributes.collect(Collectors.toList()));

        List<AttributeType> attributeTypes = Stream.concat(globalAttributes, elementAttributes).collect(Collectors.toList());

        Stream<String> stringAttributeDeclarations = stringDeclarations(attributeTypes.stream());
        String stringAttributeDeclarationBlock = String.join(",\n", stringAttributeDeclarations.sorted().collect(Collectors.toList()));

        Stream<String> booleanAttributeDeclarations = booleanDeclarations(attributeTypes.stream());
        String booleanAttributeDeclarationBlock = String.join(",\n", booleanAttributeDeclarations.sorted().collect(Collectors.toList()));

        System.out.println(stringAttributeDeclarationBlock);
        System.out.println(booleanAttributeDeclarationBlock);

    }

    private static Stream<AttributeDef> getElementAttributeDefs(Stream<RawTag> tags) {
        return tags.flatMap(Html5AttributeSpecTools::getElementAttributeDefs);
    }

    private static Stream<AttributeDef> getElementAttributeDefs(RawTag tag) {
        Map<String, String> attributeTypes = parseAttributeDefs(tag.getIdl().orElseGet(() -> ""), tag.getTag(), false)
                .collect(Collectors.toMap(AttributeDef::getName, AttributeDef::getType));
        return tag.getAttributes().stream()
                .map(attr -> new AttributeDef(attr, attributeTypes.getOrDefault(attr, "DOMString"), tag.getTag(), false));
    }

//    private static void generateAttributeDeclarations(InputStream is) throws IOException {
//        Set<AttributeType> attributeTypeSet = getIdlAttributeTypes(is);
//
//        Stream<String> stringDeclarations = stringDeclarations(attributeTypeSet.stream());
//        String stringAttributeDeclarations = String.join(",\n", stringDeclarations.sorted().collect(Collectors.toList()));
//        System.out.println(stringAttributeDeclarations);
//        System.out.println();
//
//        Stream<String> booleanDeclarations = booleanDeclarations(attributeTypeSet.stream());
//        String booleanAttributeDeclarations = String.join(",\n", booleanDeclarations.sorted().collect(Collectors.toList()));
//        System.out.println(booleanAttributeDeclarations);
////        attributeTypeList.stream().forEach(System.out::println);
//    }

    private static Stream<AttributeDef> getGlobalAttributeDefs(Document doc) throws IOException {
        List<String> idlStrings = getIdlStrings(doc).collect(Collectors.toList());
        String globalHtmlElementIdl = idlStrings.stream().filter(htmlElementIdl()).findFirst().get(); // assuming there is one and only one
        return parseAttributeDefs(globalHtmlElementIdl, null, true);
//        Stream<AttributeType> attributeTypes = convert(attributeDefs);
//        // deduplicate attribute types
//        List<AttributeType> attributeTypeList = attributeTypes.collect(Collectors.toList());
//        return new HashSet<>(attributeTypeList);
    }

    private static Predicate<String> htmlElementIdl() {
        return idl -> idl.startsWith("interface HTMLElement");
    }

//    }

    //    private static Stream<String> toAttributeDeclarations(Stream<AttributeType> attributeTypes) {
//        return attributeTypes
//                .map(at -> String.format("%s = %s(\"%s\")", at.getName(), getAtDelcarationMethod(at), at.getName()));
//    }
//
//    private static String getAtDelcarationMethod(AttributeType at) {
//        if (at instanceof BooleanAttributeType) {
//            return "boolAttr";
//        } else {
//            return "stringAttr";
//        }

    private static String attrParamString(AttributeType at) {
        if (at.isGlobalAttribute()) {
            return String.format("\"%s\"", at.getName());
        } else {
            return String.format("\"%s\", \"%s\"", at.getName(), at.getTags().get());
        }
    }

    private static Stream<String> stringDeclarations(Stream<AttributeType> attributeTypes) {
        return attributeTypes
                .filter(at -> at instanceof StringAttributeType)
            .map(at -> String.format("%s = strAttr(%s)", at.getName(), attrParamString(at)));
    }

    private static Stream<String> booleanDeclarations(Stream<AttributeType> attributeTypes) {
        return attributeTypes
                .filter(at -> at instanceof BooleanAttributeType)
            .map(at -> String.format("%s = boolAttr(%s)", at.getName(), attrParamString(at)));
    }

    private static Stream<AttributeType> toAttributeType(Stream<AttributeDef> attributeDefs) {
        return attributeDefs
            .map(Html5AttributeSpecTools::toAttributeType)
            .filter(Objects::nonNull);
    }

    /**
     * TODO Weird datatypes I need to check out:
     * - DOMStringMap
     * - DOMTokenList
     * - TimeRanges
     * - HTMLCollection
     */
    private static final Set<String> STRING_ATTRIBUTE_TYPES = new HashSet<String>() {{
        add("DOMString");
        add("long");
        add("unsigned long");
        add("unsigned short");
        add("unrestricted double");
        add("double");
    }};

    private static AttributeType toAttributeType(AttributeDef attributeDef) {
        if (attributeDef.getType().equals("boolean")) {
            if (attributeDef.isGlobal()) {
                return new BooleanAttributeType<>(attributeDef.getName());
            } else
            {
                return new BooleanAttributeType<>(attributeDef.getName(), attributeDef.getTag());
            }
        } else if (STRING_ATTRIBUTE_TYPES.contains(attributeDef.getType())){
            if (attributeDef.isGlobal()) {
                return new StringAttributeType<>(attributeDef.getName());
            } else {
                return new StringAttributeType<>(attributeDef.getName(), attributeDef.getTag());
            }
        } else {
            System.out.println("Unhandled IDL datatype: " + attributeDef.getType());
            return null;
        }
    }

//    public static void generateTagDeclarations(InputStream is) throws IOException {
//        Document doc = Jsoup.parse(is, StandardCharsets.UTF_8.toString(), "http://");
//        Stream<RawTag> tags = getTags(doc);
//        tags.map(RawTag::toDeclaration)
//            .sorted()
//            .distinct()
//            .forEach(System.out::println);
//    }

    public static Stream<RawTag> getTags(Document doc) {
        return doc.select("h1 + dl.element,h2 + dl.element,h3 + dl.element,h4 + dl.element,h5 + dl.element").stream()
            .map(element -> {
                String elementName = element.previousElementSibling().select("code").first().text();
                Optional<String> idl = findIdl(element);
                List<String> attributeNames = getAllowedAttributes(element);
                return new RawTag(elementName, attributeNames, idl);
            });
    }

    private static List<String> getAllowedAttributes(Element element) {
        return element.select("dt:contains(Content Attributes)").stream()
            .flatMap(el -> {
                Element sibling = el;
                List<String> attributes = new ArrayList<>();
                while ((sibling = sibling.nextElementSibling()) != null && sibling.tagName().equals("dd")) {
                    Element attributeElement = sibling.select("code a ").first();
                    if (attributeElement != null) {
                        attributes.add(attributeElement.text());
                    }
                }
                return attributes.stream();
            }).collect(Collectors.toList());
    }

    private static class AttributeDef {
        private String name;
        private String type;
        private String tag;
        private boolean global;

        public AttributeDef(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public AttributeDef(String name, String type, String tag, boolean global) {
            this.name = name;
            this.type = type;
            this.tag = tag;
            this.global = global;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getTag() {
            return tag;
        }

        public boolean isGlobal() {
            return global;
        }

        @Override
        public String toString() {
            return "AttributeDef{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", tag='" + tag + '\'' +
                    ", global=" + global +
                    '}';
        }
    }

    private static Stream<AttributeDef> parseAttributeDefs(String idlString, String tagName, boolean globalAttribute) {
        return Stream.of(idlString)
            .flatMap(Html5AttributeSpecTools::parsePartialAttributeDefs)
            .map(ad -> new AttributeDef(ad.getName(), ad.getType(), tagName, globalAttribute));
    }

    private static Stream<AttributeDef> parsePartialAttributeDefs(String idl) {
        return Stream.of(idl.split("\n"))
            .map(String::trim)
            .filter(line -> line.startsWith("attribute"))
            .map(Html5AttributeSpecTools::parseAttribute)
            .filter(Objects::nonNull);
    }

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("attribute\\s+(.*)\\s+([^\\s]+);");

    private static AttributeDef parseAttribute(String line) {
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(line);
        if (matcher.matches()) {
            return new AttributeDef(matcher.group(2), matcher.group(1));
        } else {
            System.out.println(String.format("Supposed attribute line '%s' does not match pattern", line));
            return null;
        }
    }

    private static Stream<String> getIdlStrings(Document doc) {
        return doc.select("pre").stream().filter(el -> el.className().equals("idl")).map(Element::text);
    }

    private static Optional<String> findIdl(Element el) {
        Element idlElement = el.select("pre.idl").first();
        if (idlElement == null) {
            System.out.println("No IDL block found for: " + el.previousElementSibling());
            return Optional.empty();
        } else {
            return Optional.of(idlElement.text());
        }
    }

    public static class RawTag {
        private String tag;

        private List<String> attributes;

        private Optional<String> idl;

        public RawTag(String tag, List<String> attributes, Optional<String> idl) {
            this.tag = tag;
            this.attributes = attributes;
            this.idl = idl;
        }

        public String getTag() {
            return tag;
        }

        public List<String> getAttributes() {
            return attributes;
        }

        public Optional<String> getIdl() {
            return idl;
        }

        @Override
        public String toString() {
            return "RawTag{" +
                    "tag='" + tag + '\'' +
                    ", attributes=" + attributes +
                    ", idl=" + idl +
                    '}';
        }

        //        public String toDeclaration() {
//            List<String> allAttributes = new ArrayList<>(globalAttributes);
//            allAttributes.addAll(rawAttributes);
//            allAttributes = allAttributes.stream().map(attr -> "Attributes." + attr).collect(Collectors.toList());
//            return String.format("%s(\"%s\", CONTENT_ALLOWED, new AttributeType[] {%s}, CLOSING_TAG),", tag, tag, String.join(", ", allAttributes));
//        }
//
//        @Override
//        public String toString() {
//            return "RawTag{" +
//                "tag='" + tag + '\'' +
//                ", rawAttributes=" + rawAttributes +
//                '}';
//        }
    }
}
