package net.aggregat4.javatags.attributes;

import java.io.IOException;
import java.util.Optional;

class StringAttributeType<ValueType extends String> extends DefaultAttributeType<ValueType> {
    public StringAttributeType(String name) {
        super(name);
    }

    public StringAttributeType(String name, String tag) {
        super(name, tag);
    }

    public void render(String value, Appendable appendable) throws IOException {
        appendable.append(" ").append(getName()).append("=").append("\"").append(value).append("\"");
    }

    @Override
    public String toString() {
        return "StringAttributeType{} " + super.toString();
    }

}
