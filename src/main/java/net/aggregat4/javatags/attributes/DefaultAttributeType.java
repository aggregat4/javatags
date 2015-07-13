package net.aggregat4.javatags.attributes;

import java.util.Objects;
import java.util.Optional;

abstract class DefaultAttributeType<ValueType> implements AttributeType<ValueType> {
    private String name;
    private Optional<ValueType[]> allowedValues;

    public DefaultAttributeType(String name, Optional<ValueType[]> allowedValues) {
        this.name = Objects.requireNonNull(name);
        this.allowedValues = Objects.requireNonNull(allowedValues);
    }

    public String getName() {
        return name;
    }

    public Optional<ValueType[]> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAttributeType<?> that = (DefaultAttributeType<?>) o;
        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DefaultAttributeType{" +
            "name='" + name + '\'' +
            ", allowedValues=" + allowedValues +
            '}';
    }
}
