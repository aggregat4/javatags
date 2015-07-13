package net.aggregat4.javatags.tags;

import net.aggregat4.javatags.attributes.Attribute;

/**
 * Convenience factory methods for builder like fluent api.
 */
public class Tags {

    public static final Attribute[] NO_ATTRIBUTES = new Attribute[0];

    public static Tag text(String content) { return new Text(content); }

    public static NamedTag html(Attribute... attributes) { return new NamedTag(TagType.html, attributes); }
    public static NamedTag html(Tag... children) { return html(NO_ATTRIBUTES).content(children); }

    public static NamedTag head(Tag... children) { return new NamedTag(TagType.head, NO_ATTRIBUTES, children); }

    public static NamedTag title(Tag... children) { return new NamedTag(TagType.title, NO_ATTRIBUTES, children); }
    public static NamedTag title(String content) { return title(text(content)); }

    public static NamedTag body(Attribute... attributes) { return new NamedTag(TagType.body, attributes); }
    public static NamedTag body(Tag... children) { return body(NO_ATTRIBUTES).content(children); }

    public static NamedTag h1(Attribute... attributes) { return new NamedTag(TagType.h1, attributes); }
    public static NamedTag h1(Tag... children) { return h1(NO_ATTRIBUTES).content(children); }
    public static NamedTag h1(String content) { return h1(text(content)); }

    public static NamedTag p(Attribute... attributes) { return new NamedTag(TagType.p, attributes); }
    public static NamedTag p(Tag... children) { return p(NO_ATTRIBUTES).content(children); }
    public static NamedTag p(String content) { return p(text(content)); }
    public static NamedTag p(Attribute[] attributes, String content) { return new NamedTag(TagType.p, attributes).content(text(content)); }

    public static NamedTag br(Attribute... attributes) { return new NamedTag(TagType.br, attributes); }
}