package net.aggregat4.javatags.attributes;

import java.io.IOException;

public interface AttributeType<ValueType> {

    String getName();

    void render(ValueType value, Appendable appendable) throws IOException;

}
