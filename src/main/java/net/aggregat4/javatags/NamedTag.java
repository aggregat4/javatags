package net.aggregat4.javatags;

import java.io.IOException;

class NamedTag implements Tag {
    private TagType tagType;
    private TypedAttributes.TypedAttribute[] attributes;
    private Tag[] children = new Tag[]{};

    public NamedTag(TagType tagType, TypedAttributes.TypedAttribute[] attributes) {
        this.tagType = tagType;
        this.attributes = validateAttributes(attributes);
    }

    public NamedTag(TagType tagType, TypedAttributes.TypedAttribute[] attributes, Tag[] children) {
        this(tagType, attributes);
        if (children != null) {
            this.children = validateChildren(children);
        }
    }

    private Tag[] validateChildren(Tag[] children) {
        if (!tagType.isContentAllowed() && children != null && children.length != 0) {
            throw new IllegalArgumentException(String.format("The tag '%s' does not allow content " +
                "according to the html5 specification", tagType.getName()));
        }
        return children;
    }

    private TypedAttributes.TypedAttribute[] validateAttributes(TypedAttributes.TypedAttribute[] attributes) {
        for (TypedAttributes.TypedAttribute attr : attributes) {
            boolean allowed = false;
            for (TypedAttributes.TypedAttributeType allowedAttributeType : tagType.getAllowedAttributeTypes()) {
                if (allowedAttributeType == attr.getType()) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new IllegalArgumentException(String.format("The attribute '%s' is not allowed for the " +
                    "tag '%s'.", attr.getType().getName(), tagType.getName()));
            }
        }
        return attributes;
    }

    public Tag[] getChildren() {
        return children;
    }

    public NamedTag content(Tag... children) {
        this.children = validateChildren(children);
        return this;
    }

    public NamedTag attr(TypedAttributes.TypedAttribute ... attributes) {
        this.attributes = validateAttributes(attributes);
        return this;
    }

    protected void renderOpeningTag(Appendable appendable) throws IOException {
        appendable.append("<");
        appendable.append(tagType.getName());
        for (TypedAttributes.TypedAttribute attr : attributes) {
            attr.render(appendable);
        }
        appendable.append(">");
    }

    protected void renderClosingTag(Appendable appendable) throws IOException {
        appendable.append("</");
        appendable.append(tagType.getName());
        appendable.append(">");
    }

    public void render(Appendable appendable) throws IOException {
        renderOpeningTag(appendable);
        if (tagType.isClosingTag()) {
            for (Tag child : getChildren()) {
                child.render(appendable);
            }
            renderClosingTag(appendable);
        }
    }

}
