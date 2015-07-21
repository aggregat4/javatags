package net.aggregat4.javatags.attributes;

import java.io.IOException;
import java.util.Collection;

public interface AttributeType<ValueType> {

    String getName();

    boolean isGlobalAttribute();

    Collection<String> getTags();

    void render(ValueType value, Appendable appendable) throws IOException;

}
