package net.aggregat4.javatags;

import static net.aggregat4.javatags.AttributeType.*;

public class Attributes {
    // Helper for non vararg params
    public static Attribute [] attr(Attribute... attributes) { return attributes; }

    public static Attribute id(String value) { return new Attribute(id, value); }
    public static Attribute cssclass(String value) { return new Attribute(classAttr, value); }
    public static Attribute lang(String value) { return new Attribute(lang, value); }

}
