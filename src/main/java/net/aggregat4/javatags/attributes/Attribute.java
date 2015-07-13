package net.aggregat4.javatags.attributes;

import java.io.IOException;
import java.util.Objects;

public class Attribute<AT extends AttributeType<ValueType>, ValueType> {
    private AT type;
    private ValueType value;

    public Attribute(AT type, ValueType value) {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(
            value,
            String.format("Value can not be null since the attribute '%s' is non-nullable",
                type.getName()));
    }

    public AT getType() {
        return type;
    }

    public ValueType getValue() {
        return value;
    }

    public void render(Appendable appendable) throws IOException {
        type.render(value, appendable);
    }
}
