package net.aggregat4.javatags.tags;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.aggregat4.javatags.attributes.Attributes.*;
import static net.aggregat4.javatags.attributes.Attributes.spellcheck;

public class Html5TagSpecTools {

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
        generateTagDeclarations(Html5TagSpecTools.class.getResourceAsStream("/HTML5.html"));
    }

    private static class TagWithAttributes {
        private String tag;
        private List<String> attributes;

        public TagWithAttributes(String tag, List<String> attributes) {
            this.tag = tag;
            this.attributes = attributes;
        }

        private static final List<String> globalAttributes = Arrays.asList(
            "id",
            "classAttr",
            "style",
            "titleAttr",
            "lang",
            "translate",
            "dir",
            "hidden",
            "tabIndex",
            "accessKey",
            "accessKeyLabel",
            "contentEditable",
            "isContentEditable",
            "spellcheck"
        );

        public String toDeclaration() {
            List<String> allAttributes = new ArrayList<>(globalAttributes);
            allAttributes.addAll(attributes);
            allAttributes = allAttributes.stream().map(attr -> "Attributes." + attr).collect(Collectors.toList());
            return String.format("%s(\"%s\", CONTENT_ALLOWED, new AttributeType[] {%s}, CLOSING_TAG),", tag, tag, String.join(", ", allAttributes));
        }

        @Override
        public String toString() {
            return "TagWithAttributes{" +
                "tag='" + tag + '\'' +
                ", attributes=" + attributes +
                '}';
        }
    }

    private static void generateTagDeclarations(InputStream is) throws IOException {
        Document doc = Jsoup.parse(is, StandardCharsets.UTF_8.toString(), "http://");
        Stream<TagWithAttributes> tags = getTags(doc);
        tags.map(TagWithAttributes::toDeclaration)
            .sorted()
            .distinct()
            .forEach(System.out::println);
    }

    private static Stream<TagWithAttributes> getTags(Document doc) {
        return doc.select("h1 + dl.element,h2 + dl.element,h3 + dl.element,h4 + dl.element,h5 + dl.element").stream()
                .map(element -> {
                    String elementName = element.previousElementSibling().select("code").first().text();
                    List<String> attributeNames = element.select("dt:contains(Content attributes)").stream()
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
                    return new TagWithAttributes(elementName, attributeNames);
                });
    }

}
