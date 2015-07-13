package net.aggregat4.javatags.attributes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Html5AttributeSpecTools {

    /**
     * Attributes:
     *
     * We need the IDL for getting the datatypes of the attributes.
     *
     * Attributes are not unique, that is to say: attributes with the same name get reused in different contexts
     * with different types. This means that I either need a more complicated model where attributetypes are scoped
     * to certain html elements which would make my current approach for a fluid and easy api to generate HTML very
     * hard to impossible to attain. Therefore I decided to just go for only 2 datatypes:
     * - boolean attributes are modeled as such since they need to be rendered differently, their booleaness is
     *   expressed by them being present or not in an element. From my semi-scientific analysis of the boolean attributes
     *   there is not duplicate usage of elements with the same name but one time with and one time without being a
     *   boolean.
     * - everything else is a string attribute. We lose static type checking at compile time for the values.
     *
     * Elements:
     *
     * Then we need the html text to retrieve a listing of all the actual attributes, the IDL is not complete
     *
     * Following idea:
     * - find all dl with class = element
     * - for each
     * -- find the dt with content "Content attributes"
     * -- for all subsequent dd siblings:
     * --- if it has "Global attributes" in the text, ignore it
     * ---
     */

    public static void main(String [] args) throws Exception {
        generateAttributeDeclarations(Html5AttributeSpecTools.class.getResourceAsStream("/HTML5.html"));
    }

    private static void generateAttributeDeclarations(InputStream is) throws IOException {
        Stream<String> idlStrings = convertHtml5HtmlToIdl(is);
        Stream<AttributeDef> attributeDefs = parseAttributes(idlStrings);
        Stream<AttributeType> attributeTypes = convert(attributeDefs);
        // deduplicate attribute types
        List<AttributeType> attributeTypeList = attributeTypes.collect(Collectors.toList());
        Set<AttributeType> attributeTypeSet = new HashSet<>(attributeTypeList);

        Stream<String> stringDeclarations = stringDeclarations(attributeTypeSet.stream());
        String stringAttributeDeclarations = String.join(",\n", stringDeclarations.sorted().collect(Collectors.toList()));
        System.out.println(stringAttributeDeclarations);
        System.out.println();

        Stream<String> booleanDeclarations = booleanDeclarations(attributeTypeSet.stream());
        String booleanAttributeDeclarations = String.join(",\n", booleanDeclarations.sorted().collect(Collectors.toList()));
        System.out.println(booleanAttributeDeclarations);
//        attributeTypeList.stream().forEach(System.out::println);
    }

    private static Stream<String> stringDeclarations(Stream<AttributeType> attributeTypes) {
        return attributeTypes
            .filter(at -> at.getClass().getSimpleName().equals(StringAttributeType.class.getSimpleName()))
            .map(at -> String.format("%s = strAttr(\"%s\")", at.getName(), at.getName()));
    }

    private static Stream<String> booleanDeclarations(Stream<AttributeType> attributeTypes) {
        return attributeTypes
            .filter(at -> at.getClass().getSimpleName().equals(BooleanAttributeType.class.getSimpleName()))
            .map(at -> String.format("%s = boolAttr(\"%s\")", at.getName(), at.getName()));
    }
    private static Stream<AttributeType> convert(Stream<AttributeDef> attributeDefs) {
        return attributeDefs
            .map(Html5AttributeSpecTools::convert)
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

    private static AttributeType convert(AttributeDef attributeDef) {
        if (attributeDef.getType().equals("boolean")) {
            return new BooleanAttributeType<>(attributeDef.getName());
        } else if (STRING_ATTRIBUTE_TYPES.contains(attributeDef.getType())){
            return new StringAttributeType<>(attributeDef.getName());
        } else {
            System.out.println("Unhandled datatype: " + attributeDef.getType());
            return null;
        }
    }

    private static class AttributeDef {
        private String name;
        private String type;

        public AttributeDef(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "AttributeDef{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
        }
    }

    private static Stream<AttributeDef> parseAttributes(Stream<String> idlStrings) throws IOException {
        return idlStrings
            .filter(idl -> idl.startsWith("interface HTMLElement") || idl.contains(" : HTMLElement"))
            .flatMap(idl -> Stream.of(idl.split("\n")))
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

    private static Stream<String> convertHtml5HtmlToIdl(InputStream is) throws IOException {
        Document doc = Jsoup.parse(is, StandardCharsets.UTF_8.toString(), "http://");
        return doc.select("pre").stream().filter(el -> el.className().equals("idl")).map(Element::text);
    }
}
