package net.aggregat4.javatags.attributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Attribute<AT extends AttributeType<ValueType>, ValueType> {
    private AT type;
    private ValueType value;

    public Attribute(AT type, ValueType value) {
        this.type = Objects.requireNonNull(type);
        this.value = ensureValueIsAllowed(type, Objects.requireNonNull(
            value,
            String.format("Value can not be null since the attribute '%s' is non-nullable",
                type.getName())));
    }

    private ValueType ensureValueIsAllowed(AT type, ValueType value) {
        if (type.getAllowedValues().isPresent()) {
            boolean valueIsAllowed = false;
            for (ValueType allowedValue : type.getAllowedValues().get()) {
                if (allowedValue.equals(value)) {
                    valueIsAllowed = true;
                    break;
                }
            }
            if (!valueIsAllowed) {
                throw new IllegalArgumentException(String.format("The value '%s' is not allowed for " +
                        "attribute '%s', allowed values are: %s", value, type.getName(),
                    Arrays.asList(type.getAllowedValues().get())));
            }
        }
        return value;
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
