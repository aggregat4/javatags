package net.aggregat4.javatags.content;

import java.io.IOException;

public interface AttributeRenderer<V> {

    void render(String name, V value, Appendable appendable) throws IOException;

    AttributeRenderer<Boolean> BOOL_ATTR = new AttributeRenderer<Boolean>() {
        @Override
        public void render(String name, Boolean value, Appendable appendable) throws IOException {
            if (value) {
                appendable.append(" ").append(name);
            }
        }
    };

    AttributeRenderer<String> STRING_ATTR = new AttributeRenderer<String>() {
        @Override
        public void render(String name, String value, Appendable appendable) throws IOException {
            appendable.append(" ").append(name).append("=").append("\"").append(value).append("\"");
        }
    };

}
