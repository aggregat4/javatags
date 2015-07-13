package net.aggregat4.javatags.attributes;

import java.io.IOException;
import java.util.Optional;

public interface AttributeType<ValueType> {
    String getName();

    Optional<ValueType[]> getAllowedValues();

    void render(ValueType value, Appendable appendable) throws IOException;
}
