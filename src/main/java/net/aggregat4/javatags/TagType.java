package net.aggregat4.javatags;

import static net.aggregat4.javatags.Attributes.AttributeType.*;
import static net.aggregat4.javatags.TagType.Constants.*;

public enum TagType {
    html("html",   CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, CLOSING_TAG),
    head("head",   CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, CLOSING_TAG),
    title("title", CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, CLOSING_TAG),
    body("body",   CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, CLOSING_TAG),
    h1("h1",       CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, CLOSING_TAG),
    p("p",         CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, CLOSING_TAG),
    br("br",    NO_CONTENT_ALLOWED, STANDARD_HTML_ATTRIBUTES, NON_CLOSING_TAG),
    ;

    private String name;
    private boolean contentAllowed;
    private Attributes.AttributeType[] allowedAttributeTypes;
    private boolean closingTag;

    TagType(String name, boolean contentAllowed, Attributes.AttributeType[] allowedAttributeTypes, boolean closingTag) {
        this.name = name;
        this.contentAllowed = contentAllowed;
        this.allowedAttributeTypes = allowedAttributeTypes;
        this.closingTag = closingTag;
    }

    public String getName() {
        return name;
    }

    public boolean isContentAllowed() {
        return contentAllowed;
    }

    public Attributes.AttributeType[] getAllowedAttributeTypes() {
        return allowedAttributeTypes;
    }

    public boolean isClosingTag() {
        return closingTag;
    }

    static class Constants {
        public static final Attributes.AttributeType[] NO_ATTRIBUTES_ALLOWED = new Attributes.AttributeType[0];

        // http://www.w3.org/TR/html5/dom.html#global-attributes and http://www.w3.org/TR/html5/dom.html#htmlelement
//        public static final AttributeType[] STANDARD_HTML_ATTRIBUTES = new AttributeType[] {
//            id, classAttr, style, titleAttr, lang, translate, dir,
//            hidden, tabIndex, accessKey, accessKeyLabel, contentEditable,
//            isContentEditable, spellcheck};
        public static final Attributes.AttributeType[] STANDARD_HTML_ATTRIBUTES = new Attributes.AttributeType[]{
            id,
            classAttr,
            style,
            titleAttr,
            lang,
            translate,
            dir,
            hidden,
            tabIndex,
            accessKey,
            accessKeyLabel,
            contentEditable,
            isContentEditable,
            spellcheck };
        // Aliases for readability
        public static final boolean CONTENT_ALLOWED = true;
        public static final boolean NO_CONTENT_ALLOWED = false;
        public static final boolean CLOSING_TAG = true;
        public static final boolean NON_CLOSING_TAG = false;
    }

}
