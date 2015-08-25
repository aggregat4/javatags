package net.aggregat4.javatags.attributes;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

class BooleanAttributeType<ValueType extends Boolean> extends DefaultAttributeType<ValueType> {

    public BooleanAttributeType(String name) {
        super(name);
    }

    public BooleanAttributeType(String name, Collection<String> tags) {
        super(name, tags);
    }

    public void render(Boolean value, Appendable appendable) throws IOException {
        if (value) {
            appendable.append(" ").append(getName());
        }
    }

    @Override
    public String toString() {
        return "BooleanAttributeType{} " + super.toString();
    }

}
