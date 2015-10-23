package net.aggregat4.javatags.content;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tag<C extends Node> implements Content {
    private final String name;
    private final boolean closingTag;
    private final List<Attribute> attributes;
    private final List<Content> content;

    public Tag(String name, boolean closingTag, C[] nodes) {
        this.name = Objects.requireNonNull(name);
        this.closingTag = closingTag;
        this.attributes = getAttributes(nodes);
        this.content = getChildren(nodes);
    }

    private List<Content> getChildren(C[] contents) {
        // Text nodes are also content
        return Arrays.stream(contents).filter(c -> c instanceof Content).map(Content.class::cast).collect(Collectors.toList());
    }

    private List<Attribute> getAttributes(C[] contents) {
        return Arrays.stream(contents).filter(c -> c instanceof Attribute).map(Attribute.class::cast).collect(Collectors.toList());
    }

    protected void renderOpeningTag(Appendable appendable) throws IOException {
        appendable.append("<");
        appendable.append(name);
        for (Attribute attr : attributes) {
            attr.render(appendable);
        }
        appendable.append(">");
    }

    protected void renderClosingTag(Appendable appendable) throws IOException {
        appendable.append("</");
        appendable.append(name);
        appendable.append(">");
    }

    public void render(Appendable appendable) throws IOException {
        renderOpeningTag(appendable);
        if (closingTag) {
            for (Content c : content) {
                c.render(appendable);
            }
            renderClosingTag(appendable);
        }
    }

}
