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
     * TODO 20150803
     * - I now know for each tag what attributes with what types are allowed
     * - I need to add the closing tag information and the content allowed information to the tags (jsoup parsing)
     * - based on that info I can then generate the necessary helper methods to construct the tags (allow children or not)
     * - from the attributes info I can validate the attributes at runtime (allowed or not?)
     * - with the attributes info I can generate the necessary attribute definitions and helper methods, here I just need to disambiguate when the type is different but the name the same
     */

    public static void main(String [] args) throws Exception {
        InputStream html5SpecInputStream = Html5AttributeSpecTools.class.getResourceAsStream("/HTML5.html");
        Document doc = Jsoup.parse(html5SpecInputStream, StandardCharsets.UTF_8.toString(), "http://");

        Stream<AttributeDef> globalAttributeDefs = getGlobalAttributeDefs(doc);
        //Stream<AttributeType> globalAttributes = toAttributeType(globalAttributeDefs);

        List<RawTag> tags = getTags(doc).collect(Collectors.toList());
        Stream<AttributeDef> elementAttributeDefs = getElementAttributeDefs(tags.stream());
        //Stream<AttributeType> elementAttributes = toAttributeType(elementAttributeDefs);

        // tags now have all their normal attributes but need to get all the global attributes as well
        List<AttributeDef> globalAttributeDefList = globalAttributeDefs.collect(Collectors.toList());
        List<AttributeDef> elementAttributeDefList = elementAttributeDefs.collect(Collectors.toList());
        Stream<RawTag> tagsWithGlobalAttributes = withGlobalAtributes(tags.stream(), globalAttributeDefList);
//        System.out.println("tags: " + tagsWithGlobalAttributes.collect(Collectors.toList()));
        tagsWithGlobalAttributes.forEach(rt -> System.out.println(rt));

        globalAttributeDefList.stream().forEach(globalAttr -> System.out.println(globalAttr));
        elementAttributeDefList.stream().forEach(elementAttr -> System.out.println(elementAttr));

        // TODO: before I can generate code from the AttributeDefs I need to merge them together based on type and the tags they can be used with

        Stream<String> stringAttributeDeclarations = stringDeclarations(globalAttributeDefList.stream());
        String stringAttributeDeclarationBlock = String.join(",\n", stringAttributeDeclarations.sorted().collect(Collectors.toList()));
        System.out.println(stringAttributeDeclarationBlock);

//        Stream<String> booleanAttributeDeclarations = booleanDeclarations(globalAttributeDefList.stream());
//        String booleanAttributeDeclarationBlock = String.join(",\n", booleanAttributeDeclarations.sorted().collect(Collectors.toList()));

    }

    private static Stream<RawTag> withGlobalAtributes(Stream<RawTag> tags, List<AttributeDef> globalAttributeDefs) {
        return tags.map(tag -> {
            tag.addAllAttributes(globalAttributeDefs);
            return tag;
        });
    }

//    private static List<AttributeType> merge(List<AttributeType> attributeTypes) {
//        // merge by name, but only if all other constraints are satisfied (what are those constraints? at least datatype?)
//
//    }

    private static Stream<AttributeDef> getElementAttributeDefs(Stream<RawTag> tags) {
        return tags.flatMap(Html5AttributeSpecTools::getElementAttributeDefs);
    }

    private static Stream<AttributeDef> getElementAttributeDefs(RawTag tag) {
        Map<String, String> attributeTypes = parseAttributeDefs(tag.getIdl().orElseGet(() -> ""), tag.getTag(), false)
                .collect(Collectors.toMap(AttributeDef::getName, AttributeDef::getType));
        List<AttributeDef> attributeDefs = tag.getAllowedAttributes().stream()
                .map(attr -> new AttributeDef(attr, attributeTypes.getOrDefault(attr, "DOMString"), tag.getTag(), false))
                .collect(Collectors.toList());
        tag.addAllAttributes(attributeDefs);
        return attributeDefs.stream();
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

    private static String attrParamString(AttributeDef at) {
        if (at.isGlobal()) {
            return String.format("\"%s\"", at.getName());
        } else {
            return String.format("\"%s\", \"%s\"", at.getName(), at.getTag());
        }
    }

    private static Stream<String> stringDeclarations(Stream<AttributeDef> attrs) {
        return attrs
            .filter(attr -> ! isBooleanType(attr))
            .map(attr -> String.format("%s = strAttr(%s)", attr.getName(), attrParamString(attr)));
    }

    private static Stream<String> booleanDeclarations(Stream<AttributeType> attributeTypes) {
        return attributeTypes
                .filter(at -> at instanceof BooleanAttributeType)
            .map(at -> String.format("%s = boolAttr(%s)", at.getName(), attrParamString(at)));
    }

//    private static Stream<AttributeType> toAttributeType(Stream<AttributeDef> attributeDefs) {
//        return attributeDefs
//            .map(Html5AttributeSpecTools::toAttributeType)
//            .filter(Objects::nonNull);
//    }

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

    private static boolean isBooleanType(AttributeDef attributeDef) {
        if (attributeDef.getType().equals("boolean")) {
            return true;
        } else if (STRING_ATTRIBUTE_TYPES.contains(attributeDef.getType())){
            return false;
        } else {
            throw new IllegalStateException("Unhandled IDL datatype: " + attributeDef.getType());
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
                boolean closingTagRequired = getClosingTagRequired(element);
                boolean childrenAllowed = getChildrenAllowed(element);
                return new RawTag(elementName, attributeNames, idl, childrenAllowed, closingTagRequired);
            });
    }

    private static boolean getChildrenAllowed(Element element) {
        return element.select("dt:contains(Content model)").stream()
            .map(el -> el.nextElementSibling())
            .filter(el -> el != null && el.tagName().equals("dd"))
            .map(el -> new Boolean(! el.text().contains("Empty.")))
            .findFirst()
            .orElse(true);
    }

    // TODO we should identify the cases where the spec does not conform to our expectation
    // and it doesn't have a dd, or the wrong dd or whatever.
    private static boolean getClosingTagRequired(Element element) {
        return element.select("dt:contains(Tag omission in text/html)").stream()
            .map(el -> el.nextElementSibling())
            .filter(el -> el != null && el.tagName().equals("dd"))
            .map(el -> new Boolean(! el.text().contains("No end tag")))
            .findFirst()
            .orElse(true);
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
        private List<String> allowedAttributes;
        private List<AttributeDef> attributes = new ArrayList<>();
        private Optional<String> idl;
        private boolean childrenAllowed;
        private boolean closingTagRequired;

        public RawTag(String tag, List<String> allowedAttributes, Optional<String> idl, boolean childrenAllowed, boolean closingTagRequired) {
            this.tag = tag;
            this.allowedAttributes = allowedAttributes;
            this.idl = idl;
            this.childrenAllowed = childrenAllowed;
            this.closingTagRequired = closingTagRequired;
        }

        public void addAllAttributes(List<AttributeDef> attributes) {
            this.attributes.addAll(attributes);
        }

        public String getTag() {
            return tag;
        }

        public List<String> getAllowedAttributes() {
            return allowedAttributes;
        }

        public List<AttributeDef> getAttributes() {
            return attributes;
        }

        public Optional<String> getIdl() {
            return idl;
        }

        public boolean isChildrenAllowed() {
            return childrenAllowed;
        }

        public void setChildrenAllowed(boolean childrenAllowed) {
            this.childrenAllowed = childrenAllowed;
        }

        public boolean isClosingTagRequired() {
            return closingTagRequired;
        }

        public void setClosingTagRequired(boolean closingTagRequired) {
            this.closingTagRequired = closingTagRequired;
        }

        @Override
        public String toString() {
            return "RawTag{" +
                "tag='" + tag + '\'' +
                ", allowedAttributes=" + allowedAttributes +
                ", attributes=" + attributes +
                ", childrenAllowed=" + childrenAllowed +
                ", closingTagRequired=" + closingTagRequired +
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
