package net.aggregat4.javatags.content;

public final class Html {

    // All the content types, even empty tags (eg IMG) are represented because they can have attributes
    public interface HtmlNode extends Node {}
    public interface TitleNode extends Node {}
    public interface ImgNode extends Node {}
    public interface HeadNode extends Node {}
    public interface BodyNode extends Node {}
    public interface H1Node extends Node {}
    public interface PNode extends Node {}

    // A special union type that reflects content that can be used in any tag
    public interface UsableAnywhere extends HtmlNode, TitleNode, ImgNode, HeadNode, BodyNode, H1Node, PNode {}
    // A special union type that reflects all tags that are allowed to have content (e.g. no ImgContent)
    public interface CanBeAChildFor extends HtmlNode, TitleNode, HeadNode, BodyNode, H1Node, PNode {}

    // All the attributes, implementing the content they can be a part of

    // Global attributes can be the content of any tag
    public static class ClassAttr extends Attribute<AttributeRenderer<String>, String> implements UsableAnywhere {
        public ClassAttr(String value) {
            super("class", AttributeRenderer.STRING_ATTR, value);
        }
    }
    // non global attributes are scoped to a specific set of tags
    public static class ManifestAttr extends Attribute<AttributeRenderer<String>, String> implements HtmlNode {
        public ManifestAttr(String value) {
            super("manifest", AttributeRenderer.STRING_ATTR, value);
        }
    }

    // All the tags, typically implementing AllContent since we are not restricting tag nesting, except for the html element of course
    public static class HtmlTag extends Tag<HtmlNode> {
        public HtmlTag(HtmlNode[] contents) {
            super("html", true, contents);
        }
    }
    // Tags either do NOT implement any concrete Content interfaces (e.g. the Html tag) or they implement NonEmptyContent
    public static class TitleTag extends Tag<TitleNode> implements CanBeAChildFor {
        public TitleTag(TitleNode[] contents) {
            super("title", true, contents);
        }
    }
    public static class HeadTag extends Tag<HeadNode> implements CanBeAChildFor {
        public HeadTag(HeadNode[] contents) {
            super("head", true, contents);
        }
    }
    public static class BodyTag extends Tag<BodyNode> implements CanBeAChildFor {
        public BodyTag(BodyNode[] contents) {
            super("body", true, contents);
        }
    }
    public static class H1Tag extends Tag<H1Node> implements CanBeAChildFor {
        public H1Tag(H1Node[] contents) {
            super("h1", true, contents);
        }
    }
    public static class PTag extends Tag<PNode> implements CanBeAChildFor {
        public PTag(PNode[] contents) {
            super("p", true, contents);
        }
    }
    // images can also have contents (attributes)
    public static class ImgTag extends Tag<ImgNode> implements CanBeAChildFor {
        public ImgTag(ImgNode[] contents) { super("img", false, contents); }
    }



    // utility methods
    public static ClassAttr classAttr(String value) {
        return new ClassAttr(value);
    }

    public static ManifestAttr manifest(String value) {
        return new ManifestAttr(value);
    }

    public static HtmlTag html(HtmlNode... contents) {
        return new HtmlTag(contents);
    }

    public static TitleTag title(TitleNode... contents) {
        return new TitleTag(contents);
    }

    public static HeadTag head(HeadNode... contents) { return new HeadTag(contents); }
    public static BodyTag body(BodyNode... contents) { return new BodyTag(contents); }
    public static H1Tag h1(H1Node... contents) { return new H1Tag(contents); }
    public static PTag p(PNode... contents) { return new PTag(contents); }
    public static ImgTag img(ImgNode... contents) { return new ImgTag(contents); }

    public static Text t(String value) {
        return new Text(value);
    }

}
