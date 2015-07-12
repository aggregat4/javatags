package net.aggregat4.javatags;

import java.io.IOException;
import java.util.*;

public class Attributes {

    public interface AttributeType<ValueType> {
        boolean isNullable();

        String getName();

        Optional<ValueType[]> getAllowedValues();

        void render(ValueType value, Appendable appendable) throws IOException;

        AttributeType<String>
            id = strAttr("id"),
            classAttr = strAttr("class"),
            style = strAttr("style"),
            titleAttr = strAttr("title"),
            lang = strAttr("lang"),
            translate = strAttr("translate", "", "yes", "no"),
            dir = strAttr("dir"),
            hidden = strAttr("hidden"),
            accessKey = strAttr("accessKey"),
            accessKeyLabel = strAttr("accessKeyLabel"),
            contentEditable = strAttr("contentEditable"),
            spellcheck = strAttr("spellcheck");

        AttributeType<Boolean>
            isContentEditable = boolAttr("isContentEditable");

        AttributeType<Long>
            tabIndex = longAttr("tabIndex");
    }

    // Helper methods to easily construct attributes without the generics noise
    private static AttributeType<String> strAttr(String name, String... allowedValues) {
        return new StringAttributeType<>(false, name, Optional.ofNullable(allowedValues.length == 0 ? null : allowedValues));
    }

    private static AttributeType<Boolean> boolAttr(String name, Boolean... allowedValues) {
        return new BooleanAttributeType<>(false, name, Optional.ofNullable(allowedValues.length == 0 ? null : allowedValues));
    }

    private static AttributeType<Long> longAttr(String name, Long... allowedValues) {
        return new LongAttributeType<>(false, name, Optional.ofNullable(allowedValues.length == 0 ? null : allowedValues));
    }

    abstract private static class DefaultAttributeType<ValueType> implements AttributeType<ValueType> {
        private boolean nullable;
        private String name;
        private Optional<ValueType[]> allowedValues;

        public DefaultAttributeType(boolean nullable, String name, Optional<ValueType[]> allowedValues) {
            this.nullable = nullable;
            this.name = Objects.requireNonNull(name);
            this.allowedValues = Objects.requireNonNull(allowedValues);
        }

        public boolean isNullable() {
            return nullable;
        }

        public String getName() {
            return name;
        }

        public Optional<ValueType[]> getAllowedValues() {
            return allowedValues;
        }
    }

    private static class StringAttributeType<ValueType extends String> extends DefaultAttributeType<ValueType> {
        public StringAttributeType(boolean nullable, java.lang.String name, Optional<ValueType[]> allowedValues) {
            super(nullable, name, allowedValues);
        }

        public void render(String value, Appendable appendable) throws IOException {
            appendable.append(" ").append(getName()).append("=").append("\"").append(value).append("\"");
        }
    }

    private static class LongAttributeType<ValueType extends Long> extends DefaultAttributeType<ValueType> {
        public LongAttributeType(boolean nullable, java.lang.String name, Optional<ValueType[]> allowedValues) {
            super(nullable, name, allowedValues);
        }

        public void render(Long value, Appendable appendable) throws IOException {
            appendable.append(" ").append(getName()).append("=").append("\"").append(value.toString()).append("\"");
        }
    }

    private static class BooleanAttributeType<ValueType extends Boolean> extends DefaultAttributeType<ValueType> {
        public BooleanAttributeType(boolean nullable, java.lang.String name, Optional<ValueType[]> allowedValues) {
            super(nullable, name, allowedValues);
        }

        public void render(Boolean value, Appendable appendable) throws IOException {
            if (value) {
                appendable.append(" ").append(getName());
            }
        }
    }

    public static class Attribute<AT extends AttributeType<ValueType>, ValueType> {
        private AT type;
        private ValueType value;

        public Attribute(AT type, ValueType value) {
            this.type = Objects.requireNonNull(type);
            if (!type.isNullable()) {
                Objects.requireNonNull(
                    value,
                    String.format("Value can not be null since the attribute '%s' is non-nullable",
                        type.getName()));
                ensureValueIsAllowed(type, value);
            }
            this.value = value;
        }

        private void ensureValueIsAllowed(AT type, ValueType value) {
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

    // helper for vararg construction
    public static Attribute[] attrs(Attribute... attributes) {
        return attributes;
    }

    public static <T> Attribute<AttributeType<T>, T> attr(AttributeType<T> type, T value) {
        return new Attribute<>(type, value);
    }

    public static Attribute<AttributeType<String>, String> id(final String value) {
        return new Attribute<>(AttributeType.id, value);
    }

}
