package net.aggregat4.javatags;

import java.io.IOException;
import java.util.Objects;

public class Attribute {
    private AttributeType attributeType;
    private Object value;

    public Attribute(AttributeType attributeType, Object value) {
        this.attributeType = attributeType;
        if (!attributeType.isNullable()) {
            Objects.requireNonNull(value, "Value can not be null since this attribute is non-nullable");
        }
        if (value != null) {
            if (!attributeType.getDataType().isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException(String.format("The attribute '%s' can only take a " +
                    "value of type '%s'", attributeType.getName(), attributeType.getDataType().getSimpleName()));
            }
        }
        this.value = value;
    }

    public AttributeType getType() {
        return attributeType;
    }

    public void render(Appendable appendable) throws IOException {
        appendable.append(" ");
        appendable.append(attributeType.getName());
        if (value != null) {
            appendable.append("=");
            appendable.append("\"");
            appendable.append(value.toString());
            appendable.append("\"");
        }
    }
}
