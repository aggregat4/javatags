package net.aggregat4.javatags.content;

import java.io.IOException;
import java.util.Objects;

public class Attribute<R extends AttributeRenderer<V>, V> implements Node {
    private final String name;
    private final R renderer;
    private final V value;

    public Attribute(String name, R renderer, V value) {
        this.name = name;
        this.renderer = Objects.requireNonNull(renderer);
        this.value = Objects.requireNonNull(value);
    }

    public String getName() {
        return name;
    }

    public V getValue() {
        return value;
    }

    public void render(Appendable appendable) throws IOException {
        renderer.render(getName(), getValue(), appendable);
    }
}
